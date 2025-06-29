from langchain_core.runnables import RunnableSerializable
from langchain_core.prompts import ChatPromptTemplate, HumanMessagePromptTemplate
from langchain_core.messages import SystemMessage
from lib.util.structure_output_utils import YesOrNoUtil
from lib.util.llm_utils import ChatModelUtil


def create_trustworthiness_check_chain() -> RunnableSerializable:
    # llm
    llm = ChatModelUtil.getDefaultChatModel()
    structured_llm_grader = llm.with_structured_output(YesOrNoUtil.YesOrNo, method='json_mode', include_raw=False)
    # prompt
    hallucination_prompt = ChatPromptTemplate.from_messages(
        [
            SystemMessage(content=f"""You are a grader assessing whether all content in LLM-generation is grounded in the set-of-facts and the information within this set of facts is sufficiently robust to substantiate. \n 
            {YesOrNoUtil.json_mode_prompt(
                yes_means="all content in LLM-generation is grounded in the set-of-facts and the information within this set-of-facts is sufficiently robust to substantiate.")}"""),
            HumanMessagePromptTemplate.from_template(
                "LLM-generation: \n\n {generation} \n\n"
                "set-of-facts: \n\n {documents}"),
        ]
    )
    chain = hallucination_prompt | structured_llm_grader
    return chain


def create_effectiveness_check_chain() -> RunnableSerializable:
    # llm with structure output
    llm = ChatModelUtil.getDefaultChatModel()
    structured_llm_grader = llm.with_structured_output(YesOrNoUtil.YesOrNo, method='json_mode', include_raw=False)
    # prompt
    answer_prompt = ChatPromptTemplate.from_messages(
        [
            SystemMessage(
                content=f"""
                You are a grader assessing whether an LLM-generation addresses / resolves the user-question. 
                {YesOrNoUtil.json_mode_prompt(yes_means="the LLM-generation resolves the user-question")}
                """),
            HumanMessagePromptTemplate.from_template(
                "user-question: \n\n {question} \n\n LLM-generation: {generation}")
        ]
    )
    # chain
    effectiveness_grader = answer_prompt | structured_llm_grader
    return effectiveness_grader
