from typing import Optional
import traceback

import streamlit as st
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain_core.chat_history import BaseChatMessageHistory, InMemoryChatMessageHistory
from langchain_community.chat_message_histories import StreamlitChatMessageHistory
from lib.util.streamlit_web_utils import extract_text_from_pdf, load_avatar
from lib.config.environment import Environment
from lib.config.app_config import Config
from lib.util.llm_utils import EmbeddingUtil
from lib.util.vector_store_utils import VectorStoreUtil
from lib.graph.self_reflection_rag.graph import RAGGraphWrapper
from lib.web.msg_templates import user_template, bot_template

Environment.setup_up_env_vars(enable_langsmith=False, langsmith_proj="rag_bot")


def get_chat_history(session_id: Optional[str] = None) -> BaseChatMessageHistory:
    return StreamlitChatMessageHistory()


def load_or_init_session():
    # init session variables
    print("load or init chat session")
    # if "session_history" not in st.session_state:
    #    print("init session history")
    #    st.session_state.session_history = StreamlitChatMessageHistory()
    if "vectorstore_wrapper" not in st.session_state:
        print("init vector store")
        vectorstore_wrapper = VectorStoreUtil.create_default_vectorstore_wrapper()  # todo: init singleton when start
        vectorstore_wrapper.init_from_dump(base_dir=Config.vectorstore_dump_dir())
        st.session_state.vectorstore_wrapper = vectorstore_wrapper
    if "rag_graph_wrapper" not in st.session_state:
        print("init rag graph")
        st.session_state.rag_graph_wrapper = RAGGraphWrapper(
            vector_store_wrapper=st.session_state.vectorstore_wrapper,
            get_by_session_id=get_chat_history)
    print(st.session_state)


def add_text_into_vectorstore(texts: str):
    splitter = RecursiveCharacterTextSplitter.from_tiktoken_encoder(chunk_size=512, chunk_overlap=0)
    doc_splits = splitter.create_documents(splitter.split_text(texts))
    st.session_state.vectorstore_wrapper.add_docs(doc_splits, EmbeddingUtil.getDefaultEmbeddingModel())


def get_ai_response(user_input: str):
    rag_graph_wrapper: RAGGraphWrapper = st.session_state.rag_graph_wrapper
    try:
        response = rag_graph_wrapper.get_graph().invoke(
            {'question': user_input}, config={'configurable': {'session_id': '123'}})
        return response['generation']
    except Exception as e:
        print(f"exception:{e}")
        traceback.print_exc()
        return "I can't answer this question based on the documents collected"


def process_user_input(user_input):
    user_avatar = load_avatar("static/user.png")
    ai_avatar = load_avatar("static/ai.png")

    if st.session_state.rag_graph_wrapper is not None:
        # ask rag graph
        ai_response = get_ai_response(user_input)

        # add to history
        chat_history = get_chat_history()
        chat_history.add_user_message(user_input)
        chat_history.add_ai_message(ai_response)

        # display chat_history
        for i, message in enumerate(chat_history.messages):
            message_type = message.type
            if message_type == 'human':
                # user question
                st.write(
                    user_template
                    .replace("{{MSG}}", message.content)
                    .replace("{{USER_AVATAR}}", user_avatar),
                    unsafe_allow_html=True)  # unsafe_allow_html=True表示允许HTML内容被渲染
            else:
                st.write(
                    bot_template
                    .replace("{{MSG}}", message.content)
                    .replace("{{AI_AVATAR}}", ai_avatar),
                    unsafe_allow_html=True)


def main():
    # web page config
    st.set_page_config(page_title="ReadMind", page_icon=":robot:")
    st.header("ReadMind：Your AI DOC Assistant")

    # load or init session
    load_or_init_session()

    # web page component
    # 1. text box that process user input
    user_input = st.text_input("Input your question: ")
    if user_input:
        process_user_input(user_input)

    # 2. sidebar
    with st.sidebar:
        # sidebar header
        st.subheader("Add DOC to knowledge base")
        # button that upload file into knowledge base
        files = st.file_uploader("upload your document，then click 'Submit'", accept_multiple_files=True)
        if st.button("Submit"):
            with st.spinner("processing ..."):
                texts = extract_text_from_pdf(files)
                add_text_into_vectorstore(texts)


if __name__ == "__main__":
    main()
