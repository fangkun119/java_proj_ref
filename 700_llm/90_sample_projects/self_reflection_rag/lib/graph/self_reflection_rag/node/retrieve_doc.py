from lib.graph.self_reflection_rag.state.graph_state import RagGraphState
from lib.util.vector_store_utils import VectorStoreWrapper
from langchain_core.runnables.base import RunnableLike
from langchain_core.vectorstores import VectorStoreRetriever


def create_retrieve_node(vector_store_wrapper: VectorStoreWrapper) -> RunnableLike:
    def retrieve(state: RagGraphState, retriever: VectorStoreRetriever):
        """
        检索文档，从向量数据库查询与用户提问有关的内容

        参数： state (dict)：当前图形状态
        返回： state (dict)：添加到包含已检索文档的状态文档的新键
        """
        print("---检索---")
        question = state["question"]

        # 检索
        documents = retriever.invoke(question)

        # 返回更新后的graph state
        return {"documents": documents, "question": question}\

    print(vector_store_wrapper)
    print(vector_store_wrapper.get_vector_store())
    vector_store = vector_store_wrapper.get_vector_store()
    return lambda state: retrieve(state, vector_store.as_retriever())
