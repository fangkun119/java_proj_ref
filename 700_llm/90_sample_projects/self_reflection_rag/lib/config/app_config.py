from lib.config.llm_vendor import LLMVendor
from lib.config.vector_store_vendor import VectorStoreVendor

default_chat_model_vendor = LLMVendor.ZHIPU
default_embedding_vendor = LLMVendor.ZHIPU
default_vectorstore_vendor = VectorStoreVendor.FAISS


class Config:
    @staticmethod
    def default_chat_model_vendor():
        return default_chat_model_vendor

    @staticmethod
    def default_embedding_vendor():
        return default_embedding_vendor

    @staticmethod
    def default_vectorstore_vendor():
        return default_vectorstore_vendor

    @staticmethod
    def vectorstore_dump_dir():
        return "data/vector_store"
