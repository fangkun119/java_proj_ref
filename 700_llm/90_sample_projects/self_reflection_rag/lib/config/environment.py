import os


class Environment:
    # DATA_PATH = "data/file/本地知识库.pdf"
    # PINECONE_KEY = "xxxx"
    # HUGGINGFACEHUB_API_TOKEN = "hf_xxxx"

    os.environ['LANGCHAIN_TRACING_V2'] = 'false'
    os.environ['LANGCHAIN_PROJECT'] = ''
    os.environ['LANGCHAIN_ENDPOINT'] = ''

    @staticmethod
    def setup_up_env_vars(enable_langsmith: bool, langsmith_proj: str):
        os.environ['LANGCHAIN_TRACING_V2'] = 'true' if enable_langsmith else 'false'
        os.environ['LANGCHAIN_PROJECT'] = langsmith_proj
        os.environ['LANGCHAIN_ENDPOINT'] = "https://api.smith.langchain.com"

    @staticmethod
    def print_env_vars():
        print(f"ZHIPU_API_KEY\t: {os.environ['ZHIPU_API_KEY'][:5]}... ")
        print(f"OPENAI_API_KEY\t: {os.environ['OPENAI_API_KEY'][:5]}...")
        print(f"TAVILY_API_KEY\t: {os.environ['TAVILY_API_KEY'][:5]}...")
        print(f"LANGCHAIN_API_KEY\t: {os.environ['LANGCHAIN_API_KEY'][:5]}...")
        print(f"KIMI_API_KEY\t: {os.environ['KIMI_API_KEY'][:5]}...")
        print(f"LANGCHAIN_TRACING_V2\t: {os.environ['LANGCHAIN_TRACING_V2']}")
        print(f"LANGCHAIN_PROJECT\t: {os.environ['LANGCHAIN_PROJECT']}")
        print(f"LANGCHAIN_ENDPOINT\t: {os.environ['LANGCHAIN_ENDPOINT']}")
