from langchain_core.runnables.base import RunnableLike, RunnableSerializable

from lib.chains.route_question import create_question_route_chain
from lib.graph.self_reflection_rag.state.graph_state import RagGraphState


def create_question_router() -> RunnableLike:
    def route_question(state: RagGraphState, chain: RunnableSerializable):
        """
        将问题路由到网络搜索或 RAG。

        参数： state (dict)：当前图形状态
        返回： str：路由结论，包括web_search, vectorstore, llm_fallback
        """

        print("---路由用户问题---")
        question = state["question"]
        source = chain.invoke({"question": question})

        # 如果没有决定则返回 LLM 或引发错误
        if "tool_calls" not in source.additional_kwargs:
            print("---把问题路由到LLM---")
            return "llm_fallback"
        if len(source.additional_kwargs["tool_calls"]) == 0:
            raise "路由无法确定来源"

        # 选择数据源
        datasource = source.additional_kwargs["tool_calls"][0]["function"]["name"]
        if datasource == "web_search":
            print("---把问题路由到网络搜索---")
            return "web_search"
        elif datasource == "vectorstore":
            print("---把问题路由到数据库---")
            return "vectorstore"
        else:
            print("---把问题路由到LLM---")
            return "llm_fallback"

    question_route_chain = create_question_route_chain()
    return lambda state: route_question(state, question_route_chain)
