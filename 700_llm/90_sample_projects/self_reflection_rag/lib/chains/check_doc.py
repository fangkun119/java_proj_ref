from langchain_core.runnables import RunnableSerializable
from langchain_core.messages import SystemMessage, BaseMessage
from langchain_core.prompts import ChatPromptTemplate, HumanMessagePromptTemplate
from lib.util.llm_utils import ChatModelUtil
from lib.util.structure_output_utils import YesOrNoUtil


def create_doc_relevance_check_chain() -> RunnableSerializable:
    # llm
    llm = ChatModelUtil.getDefaultChatModel()

    # structure output
    structured_llm_grader = llm.with_structured_output(
        schema=YesOrNoUtil.YesOrNo, method="json_mode", include_raw=False)

    # ensemble chain
    grade_prompt = ChatPromptTemplate(
        messages=[
            SystemMessage(content=f"""You are a grader assessing relevance of a retrieved-document to a user-question. 
            If the document contains keyword(s) or semantic meaning related to the user question, 
            grade it as relevant. \n 
            {YesOrNoUtil.json_mode_prompt(yes_means="the document is relevant to the question")}."""),
            HumanMessagePromptTemplate.from_template(
                "retrieved-document: \n\n {document} \n\n User question: {question}")
        ]
    )
    retrieval_grader = grade_prompt | structured_llm_grader
    return retrieval_grader
