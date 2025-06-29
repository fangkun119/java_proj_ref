import time
from typing import List, Optional

from langchain_community.vectorstores import Chroma, FAISS
from langchain_core.documents import Document
from langchain_core.embeddings import Embeddings
from itertools import islice
from abc import ABC, abstractmethod

from langchain_core.vectorstores import VST

from lib.util.llm_utils import EmbeddingUtil
from lib.config.vector_store_vendor import VectorStoreProp, VectorStoreVendor, get_vectorstore_prop
from lib.config.app_config import Config


# todo: 改成单例 + 多线程保护
#
# def singleton(cls):
#    instances = {}
#
#    def wrapper(*args, **kwargs):
#        if cls not in instances:
#            instances[cls] = cls(*args, **kwargs)
#        return instances[cls]
#
#    return wrapper

# @singleton
class VectorStoreWrapper(ABC):
    def __init__(self, prop: VectorStoreProp, embeddings: Embeddings):
        self._prop = prop
        self._vector_store = None
        self._embeddings = embeddings

    def get_embeddings(self):
        return self._embeddings

    @abstractmethod
    def init_from_docs(self, docs: List[Document]):
        pass

    @abstractmethod
    def init_from_dump(self, base_dir: str):
        pass

    @abstractmethod
    def add_docs(self, docs: List[Document]):
        pass

    @abstractmethod
    def get_vector_store(self) -> VST:
        pass

    @abstractmethod
    def trigger_dump(self, base_dir: str):
        pass


class FAISSWrapper(VectorStoreWrapper, ABC):
    def init_from_docs(self, docs: List[Document]):
        # make sure not empty
        if docs is None or len(docs) == 0:
            docs = [Document("")]
        # split into chunks
        chunk_size = 10
        doc_list_arr = [list(islice(docs, index, index + chunk_size)) for index in
                        range(0, len(docs), chunk_size)]
        # init vector store
        for idx, doc_list in enumerate(doc_list_arr):
            print(f"load_chunk: {idx}")
            if self._vector_store is None:
                print("init vector store and load document")
                self._vector_store = FAISS.from_documents(documents=doc_list, embedding=self._embeddings)
            else:
                time.sleep(1)  # prevent triggering the rate limiter
                print("add doc into vector store")
                self._vector_store.add_documents(documents=doc_list)
        print("vector store load complete")

    def add_docs(self, docs: List[Document]):
        chunk_size = 10
        doc_list_arr = [list(islice(docs, index, index + chunk_size)) for index in
                        range(0, len(docs), chunk_size)]
        for idx, doc_list in enumerate(doc_list_arr):
            time.sleep(1)  # prevent triggering the rate limiter
            print("add doc into vector store")
            self._vector_store.add_documents(documents=doc_list)
        print("document added into vector store")

    def trigger_dump(self, base_dir: str):
        print(f"vector store dump triggered, dir: {base_dir}/{self._prop.dump_sub_dir}")
        self._vector_store.save_local(
            folder_path=f"{base_dir}/{self._prop.dump_sub_dir}",
            index_name=self._prop.dump_file)

    def init_from_dump(self, base_dir: str):
        print(f"load from {base_dir}/{self._prop.dump_sub_dir}")
        self._vector_store = FAISS.load_local(
            folder_path=f"{base_dir}/{self._prop.dump_sub_dir}",
            embeddings=self._embeddings,
            index_name=self._prop.dump_file,
            allow_dangerous_deserialization=True)
        print(f"load compete")

    def get_vector_store(self) -> VST:
        return self._vector_store


class VectorStoreUtil:
    @staticmethod
    def create_wrapper(vendor: VectorStoreVendor, embedding: Embeddings) -> Optional[VectorStoreWrapper]:
        if vendor == VectorStoreVendor.FAISS:
            print("init FAISS")
            wrapper = FAISSWrapper(prop=get_vectorstore_prop(vendor), embeddings=embedding)
            wrapper.init_from_docs(docs=[])
            return wrapper
        else:
            print(f"vector store vendor not supported: {vendor}")
            return None

    @staticmethod
    def create_default_vectorstore_wrapper() -> Optional[VectorStoreWrapper]:
        embd = EmbeddingUtil.getDefaultEmbeddingModel()
        return VectorStoreUtil.create_wrapper(Config.default_vectorstore_vendor(), embd)

# ① FAISS
# pip install faiss-gpu (如果没有GPU，那么 pip install faiss-cpu)
#    vectorstore = FAISS.from_texts(texts=content_chunks,
#                                   embedding=embedding_model)

# ② Pinecone
# pip install pinecone-client==2.2.2
# 初始化
# pinecone.init(api_key=Keys.PINECONE_KEY, environment="asia-southeast1-gcp")
# # 创建索引
# index_name = "pinecone-chatbot-demo"
# # 检查索引是否存在，如果不存在，则创建
# if index_name not in pinecone.list_indexes():
#     pinecone.create_index(name=index_name,
#                           metric="cosine",
#                           dimension=1536)
# vectorstore = Pinecone.from_texts(texts=content_chunks,
#                                       embedding=embedding_model,
#                                       index_name=index_name)

# ③ Milvus, pip install pymilvus
# 要么安装到云上
# 要么安装到虚拟机上
# vectorstore = Milvus.from_texts(texts=content_chunks,
#                                     embedding=embedding_model,
#                                     connection_args={"host": "localhost", "port": "19530"},
# )
