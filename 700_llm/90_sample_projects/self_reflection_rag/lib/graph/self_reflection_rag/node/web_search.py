from langchain_core.documents import Document
from langchain_core.runnables.base import RunnableLike
from lib.util.web_search_utils import create_web_search_tool


def create_web_search_node() -> RunnableLike:
    def web_search(state, tool):
        """
        根据重新表述的问题进行网络搜索。

        参数：state (dict)：当前图形状态
        返回：state (dict)：使用附加的网络结果更新文档键
        """

        print("---网络搜索---")
        question = state["question"]

        # 网络搜索
        docs = tool.invoke({"query": question})
        print(f"web_results: {docs}"[:300])

        web_results = "\n".join([d["content"] for d in docs])
        web_results = Document(page_content=web_results)
        return {"documents": web_results, "question": question}

    web_search_tool = create_web_search_tool()
    return lambda state: web_search(state, web_search_tool)
