import base64
from PyPDF2 import PdfReader


def extract_text_from_pdf(files) -> str:
    # todo: use an agent to extract pdf
    text = ""
    for pdf in files:
        pdf_reader = PdfReader(pdf)
        for page in pdf_reader.pages:
            text += page.extract_text()
    return text


def load_avatar(file_name):
    with open(file_name, 'rb') as image_file:
        encoded_image = base64.b64encode(image_file.read()).decode()
        return f"data:image/png;base64,{encoded_image}"

# def get_chat_chain(vector_store):
#    # 1. get chat model
#    llm = ChatModelUtil.getDefaultChatModel()
#    # 2. create conversation buffer memory
#    memory = ConversationBufferMemory(memory_key='chat_history', return_messages=True)
#    # 3. create chat chain
#    conversation_chain = ConversationalRetrievalChain.from_llm(
#        llm=llm,
#        retriever=vector_store.as_retriever(),
#        memory=memory
#    )
#    return conversation_chain
