from typing import Optional, Callable, Any
from langchain_core.output_parsers import StrOutputParser
from langchain_core.messages import SystemMessage, HumanMessage
from langchain_core.prompts import ChatPromptTemplate, MessagesPlaceholder
from langchain_core.runnables import Runnable
from langchain_core.runnables.history import RunnableWithMessageHistory
from langchain_core.runnables.history import GetSessionHistoryCallable
from lib.util.llm_utils import ChatModelUtil


def create_answer_with_doc_chain(get_by_session_id: Optional[GetSessionHistoryCallable] = None) -> Runnable:
    # llm and background message
    llm = ChatModelUtil.getDefaultChatModel()
    background_msg = """You are an assistant for question-answering tasks. Use the following pieces of 
            retrieved context to answer the question. \n Answer 'I don't know' if you do not have information to 
            answer this question. \n Use three sentences maximum and keep the answer concise if you have information 
            to answer this question."""
    if get_by_session_id is None:
        # rag without chat history, require upstream input has below fields
        # * question  : user question
        # * documents : documents retrieved for answer this question
        non_history_template: Callable[[Any], ChatPromptTemplate] \
                = lambda upstream_input: ChatPromptTemplate.from_messages([
                    SystemMessage(content=background_msg),
                    HumanMessage(
                        f"Question: {upstream_input['question']} \nAnswer: ",
                        additional_kwargs={"documents": upstream_input["documents"]},
                    )
                ])
        return non_history_template | llm | StrOutputParser()
    else:
        # rag with chat history, require upstream message provides below fields
        # * question  : user question
        # * documents : documents retrieved for answer this question
        # https://api.python.langchain.com/en/latest/runnables/langchain_core.runnables.history.RunnableWithMessageHistory.html
        history_template: Callable[[Any], ChatPromptTemplate] \
            = lambda upstream_input: ChatPromptTemplate.from_messages([
                SystemMessage(content=background_msg),
                MessagesPlaceholder(variable_name="history"),
                HumanMessage(
                    f"Question: {upstream_input['question']} \nAnswer: ",
                    additional_kwargs={"documents": upstream_input["documents"]},
                )
            ]
        )
        return RunnableWithMessageHistory(
            history_template | llm | StrOutputParser(),
            get_by_session_id,
            input_messages_key="question",
            history_messages_key="history",
        )


def create_answer_with_llm_chain(get_by_session_id: Optional[GetSessionHistoryCallable] = None) -> Runnable:
    # llm and background message
    llm = ChatModelUtil.getDefaultChatModel()
    background_msg = """You are an assistant for question-answering tasks. Answer the question based upon your 
    knowledge. Use three sentences maximum and keep the answer concise."""
    if get_by_session_id is None:
        # answer question without chat history, require upstream message provides below fields
        # * question  : user question
        non_history_template: Callable[[Any], ChatPromptTemplate] \
            = lambda x: ChatPromptTemplate.from_messages([
                SystemMessage(content=background_msg),
                HumanMessage(f"Question: {x['question']} \nAnswer: ")
            ]
        )
        return non_history_template | llm | StrOutputParser()
    else:
        # https://api.python.langchain.com/en/latest/runnables/langchain_core.runnables.history.RunnableWithMessageHistory.html
        # answer question with chat history, require upstream message provides below fields
        # * question : user question
        # * history  : chat history
        history_template: Callable[[Any], ChatPromptTemplate] \
            = lambda x: ChatPromptTemplate.from_messages([
                SystemMessage(content=background_msg),
                HumanMessage(f"Question: {x['question']} \nAnswer: ")
            ]
        )
        return RunnableWithMessageHistory(
            history_template | llm | StrOutputParser(),
            get_by_session_id,
            input_messages_key="question",
            history_messages_key="history",
        )
