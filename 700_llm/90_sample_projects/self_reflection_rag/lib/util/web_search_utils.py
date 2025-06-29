import os
from langchain_community.tools.tavily_search import TavilySearchResults
from langchain_core.tools import BaseTool


def create_web_search_tool() -> BaseTool:
    # check API key
    print(os.environ['TAVILY_API_KEY'][:5])
    # sample search
    web_search_tool = TavilySearchResults()
    return web_search_tool
