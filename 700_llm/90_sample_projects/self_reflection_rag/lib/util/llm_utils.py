import os
from typing import Optional

from langchain_core.embeddings import Embeddings
from langchain_core.language_models import BaseChatModel
from langchain_community.embeddings.zhipuai import ZhipuAIEmbeddings
from langchain_community.chat_models.moonshot import MoonshotChat
from langchain_community.embeddings import OpenAIEmbeddings
from langchain_openai import ChatOpenAI

from lib.config.llm_vendor import LLMVendor, get_llm_vendor_prop
from lib.config.app_config import Config


class EmbeddingUtil:
    @staticmethod
    def getEmbeddingModel(vendor: LLMVendor) -> Optional[Embeddings]:
        vendor_prop = get_llm_vendor_prop(vendor)
        api_key = os.environ[vendor_prop.api_key_name]
        print(f"{vendor_prop.api_key_name}\t: {api_key[:5]}... ")
        if vendor == LLMVendor.ZHIPU:
            return ZhipuAIEmbeddings(
                api_key=api_key)
        elif vendor in {LLMVendor.OPENAI, LLMVendor.OPENAI_PROXY}:
            return OpenAIEmbeddings(
                openai_api_key=api_key,
                openai_api_base=vendor_prop.default_base_url)
        else:
            return None

    @staticmethod
    def getDefaultEmbeddingModel() -> Optional[Embeddings]:
        return EmbeddingUtil.getEmbeddingModel(Config.default_embedding_vendor())


class ChatModelUtil:
    @staticmethod
    def getChatModel(
            vendor: LLMVendor,
            temperature: Optional[int] = None,
            model: Optional[str] = None,
            base_url: Optional[str] = None) -> Optional[BaseChatModel]:
        # get vendor prop
        vendor_prop = get_llm_vendor_prop(vendor)
        # parse vendor prop
        api_key = os.environ[vendor_prop.api_key_name]
        default_temperature = vendor_prop.default_temperature
        default_model = vendor_prop.default_model
        default_base_url = vendor_prop.default_base_url
        # generate model
        if vendor in {LLMVendor.ZHIPU, LLMVendor.OPENAI, LLMVendor.OPENAI_PROXY}:
            # https://open.bigmodel.cn/dev/api#langchain_sdk
            return ChatOpenAI(
                temperature=default_temperature if (temperature is None) else temperature,
                model=default_model if (model is None) else model,
                openai_api_key=api_key,
                openai_api_base=default_base_url if (base_url is None) else default_base_url
            )
        if vendor == LLMVendor.KIMI:
            # https://python.langchain.com/v0.1/docs/integrations/chat/moonshot/
            # https://api.python.langchain.com/en/latest/chat_models/langchain_community.chat_models.moonshot.MoonshotChat.html
            return MoonshotChat(
                model=default_model if (model is None) else model,
                base_url=default_base_url if (base_url is None) else base_url,
                moonshot_api_key=api_key,
                temperature=default_temperature if (temperature is None) else temperature
            )
        else:
            return None

    @staticmethod
    def getDefaultChatModel() -> Optional[BaseChatModel]:
        return ChatModelUtil.getChatModel(Config.default_chat_model_vendor())

# from langchain.llms import HuggingFaceHub
# from langchain.embeddings import OpenAIEmbeddings, HuggingFaceInstructEmbeddings
# from lib.config.environment import Environment

# def get_huggingfacehub(model_name=None):
#    llm_model = HuggingFaceHub(repo_id=model_name,
#                               huggingfacehub_api_token=Environment.HUGGINGFACEHUB_API_TOKEN)
#    return llm_model
#
# def get_huggingfaceEmbedding_model(model_name):
#    return HuggingFaceInstructEmbeddings(model_name=model_name)

# llm = get_huggingfacehub(model_name="google/flan-t5-xxl")