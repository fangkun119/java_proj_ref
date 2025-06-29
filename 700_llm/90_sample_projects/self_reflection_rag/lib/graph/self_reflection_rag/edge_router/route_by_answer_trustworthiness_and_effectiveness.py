from langchain_core.runnables.base import RunnableLike, RunnableSerializable
from lib.graph.self_reflection_rag.state.graph_state import RagGraphState
from lib.chains.check_answer import create_trustworthiness_check_chain
from lib.chains.check_answer import create_effectiveness_check_chain


def create_answer_check_router() -> RunnableLike:
    def check_answer(state: RagGraphState, trustworthiness_check_chain: RunnableSerializable, effectiveness_check_chain: RunnableSerializable):
        """
        确定生成是否基于文档并回答问题。

        参数： state (dict)：当前图形状态
        返回： str：路由结论，useful (答案能够解答问题）, not useful (答案不能解答问题），not support（大模型没有足够的信息，无法回答该问题，产生了幻觉）
        """
        question = state["question"]
        documents = state["documents"] if "documents" in state else None
        generation = state["generation"]
        print(f"question: {question}")
        print(f"generation: {generation}"[:200])

        # 检查幻觉
        if documents is not None and len(documents) > 0:
            print("---检查幻觉---")
            score = trustworthiness_check_chain.invoke(
                {"documents": documents, "generation": generation}
            )
            print(f"documents: {documents}")
            print(f"generation: {generation}")
            grade = score.binary_score
            if grade == "yes":
                print("* 答案被文件内容支持")
            else:
                print("* 答案不能被文件支持")
                return "not supported"

        # 回答有效性
        print("---生成评价&问题---")
        score = effectiveness_check_chain.invoke({"question": question, "generation": generation})
        grade = score.binary_score
        print(f"question: {question}")
        print(f"generation: {generation}")
        if grade == "yes":
            print("* 答案解决了问题")
            return "useful"
        else:
            print("* 答案不能解决问题")
            return "not useful"

    trustworthiness_chain = create_trustworthiness_check_chain()
    effectiveness_chain = create_effectiveness_check_chain()
    return lambda state: check_answer(state, trustworthiness_chain, effectiveness_chain)