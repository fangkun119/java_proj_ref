from langchain_core.messages import SystemMessage
from langchain_core.runnables import RunnableSerializable
from langchain_core.prompts import ChatPromptTemplate, HumanMessagePromptTemplate
from langchain_core.tools import tool
from lib.util.llm_utils import ChatModelUtil


@tool
def vectorstore(query: str):
    """The vectorstore contains documents related to agents, prompt engineering, and adversarial attacks. Use the vectorstore for questions on these topics. Use the vectorstore for questions on these topics.

    Args:
        query: The query to use when searching the vectorstore.
    """
    return


@tool
def web_search(query: str):
    """The web_search contains knowledge in other area. Use web_search for answering question outside the topics of agents, prompt engineering, and adversarial attacks.

    Args:
        query: The query to use when searching the internet.
    """
    return


@tool
def llm_fallback(query: str):
    """The llm_fallback is The llm model used for casual conversation.

    Args:
        query: The sentence send to LLM model.
    """
    return


def create_question_route_chain() -> RunnableSerializable:
    # llm with tools
    llm = ChatModelUtil.getDefaultChatModel()
    structured_llm_router = llm.bind_tools(tools=[web_search, vectorstore])
    # prompt template
    route_prompt = ChatPromptTemplate(
        messages=[
            SystemMessage(
                content=f"""You are an expert at routing a user question to one of below target: {vectorstore} or {web_search} or {llm_fallback}.\n 
                        The vectorstore contains documents related to agents, prompt engineering, and adversarial attacks. Use the vectorstore for questions on these topics. Use the vectorstore for questions on these topics.\n
                        The llm_fallback is The llm model used for casual conversation.\n 
                        \n"""),
            HumanMessagePromptTemplate.from_template("{question}")
        ]
    )
    # router chain
    question_router = route_prompt | structured_llm_router
    return question_router
