from typing import Optional
from langchain_core.runnables.base import RunnableLike, Runnable
from langchain_core.runnables.history import GetSessionHistoryCallable
from lib.graph.self_reflection_rag.state.graph_state import RagGraphState
from lib.chains.answer_question import create_answer_with_doc_chain
from lib.chains.answer_question import create_answer_with_llm_chain


def create_answer_with_doc_node(get_by_session_id: Optional[GetSessionHistoryCallable] = None) -> RunnableLike:
    def answer_with_doc(state: RagGraphState, chain: Runnable):
        """
        使用 vectorstore 生成答案

        参数： state (dict)：当前图形状态
        返回： state (dict)：添加到状态、generation 的新键，包含 LLM Generation
        """
        print("---GENERATE---")
        question = state["question"]
        documents = state["documents"]
        if not isinstance(documents, list):
            documents = [documents]

        # RAG 生成
        generation = chain.invoke({"documents": documents, "question": question})
        return {"documents": documents, "question": question, "generation": generation}

    answer_with_doc_chain = create_answer_with_doc_chain(get_by_session_id)
    return lambda state: answer_with_doc(state, answer_with_doc_chain)


def create_answer_with_llm_node(get_by_session_id: Optional[GetSessionHistoryCallable] = None) -> RunnableLike:
    def answer_with_llm(state : RagGraphState, chain: Runnable):
        """
        只使用LLM生成答案

        参数：state (dict)：当前图形状态
        返回：state (dict)：添加到状态、generation 的新键，其中包含 LLM Generation
        """
        print("---LLM Fallback---")
        question = state["question"]
        generation = f"{chain.invoke({"question": question})} (no support document)"
        print(f"question: {question}")
        print(f"generation: {generation}")
        return {"question": question, "generation": generation}

    answer_with_llm_chain = create_answer_with_llm_chain(get_by_session_id)
    return lambda state: answer_with_llm(state, answer_with_llm_chain)
