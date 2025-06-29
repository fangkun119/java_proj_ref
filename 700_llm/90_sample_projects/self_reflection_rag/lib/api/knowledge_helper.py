from PyPDF2 import PdfReader
from fastapi import FastAPI
from langchain.chains import ConversationalRetrievalChain, RetrievalQA
from langchain.memory import ConversationBufferMemory
from langchain.schema.runnable import RunnableLambda
from langserve import add_routes
from dotenv import load_dotenv
from lib.util.llm_utils import EmbeddingUtil
from lib.util.llm_utils import ChatModelUtil
from lib.util.vector_store_utils import VectorStoreUtil
from lib.config.app_config import Config

load_dotenv()

app = FastAPI(
    title="LangChain Server",
    version="1.0",
    description="A simple api server using Langchain's Runnable interfaces",
)


def getContext(file_path):
    text = ""
    print("file path：" + file_path)
    pdf_reader = PdfReader(file_path)
    for page in pdf_reader.pages:
        text += page.extract_text()
    return text


print("init knowledge base")
vectorstore_wrapper = VectorStoreUtil.create_default_vectorstore_wrapper()
vectorstore_wrapper.init_from_dump(base_dir=Config.vectorstore_dump_dir())

# texts = getContext("data/file/本地知识库.pdf")
# content_chunks = split_content_into_chunks(texts)
# embedding_model = EmbeddingUtil.getDefaultEmbeddingModel()
# vector_store = save_chunks_into_vectorstore(content_chunks, embedding_model)

llm = ChatModelUtil.getDefaultChatModel()
memory = ConversationBufferMemory(memory_key='chat_history', return_messages=True)
conversation_chain = ConversationalRetrievalChain.from_llm(
    llm=llm,
    retriever=vectorstore_wrapper.get_vector_store().as_retriever(),
    memory=memory
)


def get_knowledge_by_input(input_str: str):
    print("user question：" + input_str)
    result = conversation_chain({'question': input_str})
    print(result)
    return result


"""
REST API exposed
"""
add_routes(
    app, RunnableLambda(get_knowledge_by_input), path="/get_knowledge")

"""
start server: run this file a the project root dir with environments variable set before hand

api doc: 
http://localhost:9999/docs

chat: 
curl -X 'POST' http://localhost:9999/get_knowledge/invoke -H 'accept: application/json' -H 'Content-Type: application/json' -d '{"input":"你是谁","config":{},"kwargs":{}}' 
"""

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="localhost", port=9999)
