from langchain_core.runnables.base import RunnableLike
from lib.chains.check_doc import create_doc_relevance_check_chain


def check_relevance(state, chain):
    """
    确定检索到的文档是否与问题相关。

    参数：state (dict)：当前图形状态
    返回：state (dict)：仅使用经过筛选的相关文档更新文档键
    """

    print("---检查文件与问题的相关性---")
    question = state["question"]
    documents = state["documents"]
    print(f"Question: {question}")
    print(f"Documents: {str(documents)[:50]}...")

    # 为每个文档评分
    filtered_docs = []
    for doc in documents:
        score = chain.invoke({"question": question, "document": doc})
        grade = score.binary_score
        if grade == "yes":
            print("---打分：文档相关---")
            filtered_docs.append(doc)
        else:
            print("---打分：文档不相关---")
            print(doc)
            continue
    return {"documents": filtered_docs, "question": question}


def create_doc_relevance_check_node() -> RunnableLike:
    doc_relevance_check_chain = create_doc_relevance_check_chain()
    return lambda state: check_relevance(state, doc_relevance_check_chain)


def create_web_relevance_check_node() -> RunnableLike:
    doc_relevance_check_chain = create_doc_relevance_check_chain()
    return lambda state: check_relevance(state, doc_relevance_check_chain)
