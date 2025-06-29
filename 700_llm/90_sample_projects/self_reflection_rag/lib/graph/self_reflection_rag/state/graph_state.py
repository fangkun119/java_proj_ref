from typing_extensions import TypedDict
from typing import List


class RagGraphState(TypedDict):
    """
    表示图表的状态。在Graph运行过程中，存储各个Node产生的数据，
    - 用作Node的输入和输出。
    - 用作Edge的输入，帮助Edge决定路由到哪个Node上

    属性：
    - question：用户提问
    - generation：LLM生成的答案
    - documents：从向量数据库中检索到的文档列表
    """

    question: str
    generation: str
    documents: List[str]
