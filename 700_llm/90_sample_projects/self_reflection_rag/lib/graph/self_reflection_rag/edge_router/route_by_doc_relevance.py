from langchain_core.runnables.base import RunnableLike
from lib.graph.self_reflection_rag.state.graph_state import RagGraphState


def create_doc_relevance_check_router() -> RunnableLike:
    def decide_to_generate(state: RagGraphState):
        """
        确定是否生成答案或重新生成问题。

        参数：state (dict)：当前图形状态
        返回：str：路由结论， web_search (所有文档都和问题无关，尝试网络搜索), generate (生成答案)
        """

        print("---评估已评分文件---")
        filtered_documents = state["documents"]

        if not filtered_documents or len(filtered_documents) == 0:
            # 所有文档都已过滤 check_relevance
            # 我们将重新生成一个新查询
            print("---所有文件与问题无关 ---")
            return "no_relevance"
        else:
            # 我们有相关文件，因此生成答案
            print(f"---{len(filtered_documents)}个文件与问题相关---")
            return "has_relevance"

    return lambda state: decide_to_generate(state)
