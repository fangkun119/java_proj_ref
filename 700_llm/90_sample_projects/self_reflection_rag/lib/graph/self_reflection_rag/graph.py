from typing import Optional

from langchain_core.runnables.history import GetSessionHistoryCallable
from langgraph.graph import END, StateGraph
from langgraph.graph.graph import CompiledGraph

from lib.util.vector_store_utils import VectorStoreWrapper
from lib.graph.self_reflection_rag.state.graph_state import RagGraphState
from lib.graph.self_reflection_rag.node.web_search import create_web_search_node
from lib.graph.self_reflection_rag.node.retrieve_doc import create_retrieve_node
from lib.graph.self_reflection_rag.node.check_doc import create_doc_relevance_check_node
from lib.graph.self_reflection_rag.node.check_doc import create_web_relevance_check_node
from lib.graph.self_reflection_rag.node.answer_question import create_answer_with_doc_node
from lib.graph.self_reflection_rag.node.answer_question import create_answer_with_llm_node
from lib.graph.self_reflection_rag.edge_router.route_by_question_type import create_question_router
from lib.graph.self_reflection_rag.edge_router.route_by_doc_relevance import create_doc_relevance_check_router
from lib.graph.self_reflection_rag.edge_router.route_by_answer_trustworthiness_and_effectiveness import \
    create_answer_check_router


class RAGGraphWrapper:
    def __init__(self,
                 vector_store_wrapper: VectorStoreWrapper,
                 get_by_session_id: Optional[GetSessionHistoryCallable] = None):
        # tool
        self.vector_store_wrapper = vector_store_wrapper
        self.get_session_by_id = get_by_session_id
        print(self.vector_store_wrapper.get_vector_store())
        # node
        self.web_search_node = create_web_search_node()
        self.retrieve_node = create_retrieve_node(self.vector_store_wrapper)
        self.doc_relevance_check_node = create_doc_relevance_check_node()
        self.web_relevance_check_node = create_web_relevance_check_node()
        self.answer_with_doc_node = create_answer_with_doc_node(get_by_session_id=self.get_session_by_id)
        self.answer_with_llm_node = create_answer_with_llm_node(get_by_session_id=self.get_session_by_id)
        # edge router
        self.question_router = create_question_router()
        self.relevance_check_router = create_doc_relevance_check_router()
        self.answer_check_router = create_answer_check_router()
        # graph
        self.graph = self.create_graph()

    def get_graph(self):
        return self.graph

    def get_vector_store_wrapper(self):
        return self.vector_store_wrapper

    def create_graph(self) -> CompiledGraph:
        workflow = StateGraph(RagGraphState)

        # 定义节点并给他们命名（node key）
        workflow.add_node("web_search_node", self.web_search_node)  # 网络搜索
        workflow.add_node("retrieve_node", self.retrieve_node)  # 检索
        workflow.add_node("doc_relevance_check_node", self.doc_relevance_check_node)  # 给文件打分
        workflow.add_node("web_relevance_check_node", self.web_relevance_check_node)  # 更搜索结果打分
        workflow.add_node("answer_with_doc_node", self.answer_with_doc_node)  # RAG
        workflow.add_node("answer_with_llm_node", self.answer_with_llm_node)  # llm

        # 构建图表
        workflow.set_conditional_entry_point(
            self.question_router,
            {
                "vectorstore": "retrieve_node",
                "web_search": "web_search_node",
                "llm_fallback": "answer_with_llm_node",
            },
        )
        workflow.add_edge("retrieve_node", "doc_relevance_check_node")
        workflow.add_conditional_edges(
            source="doc_relevance_check_node",
            path=self.relevance_check_router,
            path_map={
                "has_relevance": "answer_with_doc_node",
                "no_relevance": "web_search_node"
            },
        )
        workflow.add_edge("web_search_node", "web_relevance_check_node")
        workflow.add_conditional_edges(
            source="web_relevance_check_node",
            path=self.relevance_check_router,
            path_map={
                "has_relevance": "answer_with_doc_node",
                "no_relevance": "answer_with_llm_node"
            },
        )
        workflow.add_conditional_edges(
            "answer_with_doc_node",
            self.answer_check_router,
            {
                "not supported": "answer_with_doc_node",  # 幻觉，文档和grand truth不支持
                "not useful": "answer_with_doc_node",     # 生成的答案并不能回答问题
                "useful": END,                            # 生成答案不错
            },
        ),
        workflow.add_edge("answer_with_llm_node", END)

        # 编译
        app = workflow.compile()
        return app

#if __name__ == "__main__":
#    graph = RAGGraphWrapper()
