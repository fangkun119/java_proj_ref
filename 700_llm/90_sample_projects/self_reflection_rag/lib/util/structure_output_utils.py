from langchain_core.pydantic_v1 import BaseModel, Field


class YesOrNoUtil:
    class YesOrNo(BaseModel):
        """an answer to a yes or no question"""
        binary_score: str = Field(
            description="the answer, 'yes' or 'no'"
        )

    @staticmethod
    def json_mode_prompt(yes_means: str):
        return ("Respond using a JSON that contains the key 'binary_score' with the value being 'yes' or 'no', "
                f"Yes means that {yes_means}")
