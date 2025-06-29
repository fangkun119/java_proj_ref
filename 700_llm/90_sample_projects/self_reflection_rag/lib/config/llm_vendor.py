from enum import Enum

class LLMVendorProp:
    def __init__(self, vendor_name, api_key_name,
                 default_model, default_temperature, default_base_url) -> None:
        self._vendor_name: str = vendor_name
        self._api_key_name: str = api_key_name
        self._default_model: str = default_model
        self._default_temperature: str = default_temperature
        self._default_base_url: str = default_base_url

    @property
    def vendor_name(self):
        return self._vendor_name

    @property
    def api_key_name(self):
        return self._api_key_name

    @property
    def default_model(self):
        return self._default_model

    @property
    def default_temperature(self):
        return self._default_temperature

    @property
    def default_base_url(self):
        return self._default_base_url


class LLMVendor(Enum):
    ZHIPU = 1,
    KIMI = 2,
    OPENAI = 3,
    OPENAI_PROXY = 4,


vendor_map = {
    # https://open.bigmodel.cn/pricing
    # GLM-4-0520：128K, 旗舰，0.1 元 / 千tokens
    # GLM-4-AirX: 8K fast, 极速推理，0.01 元 / 千tokens
    # GLM-4-Air: 128k，高性价比，0.001 元 / 千tokens
    # GLM-4-Long: 1M，超长输入，0.001 元 / 千tokens
    # GLM-4-Flash: 128K，低价极速，0.0001 元 / 千tokens
    LLMVendor.ZHIPU: LLMVendorProp('ZHIPU', 'ZHIPU_API_KEY', 'glm-4-air', '0', 'https://open.bigmodel.cn/api/paas/v4'),

    # moonshot-v1-8k: 它是一个长度为 8k 的模型，适用于生成短文本。
    # moonshot-v1-32k: 它是一个长度为 32k 的模型，适用于生成长文本。
    # moonshot-v1-128k: 它是一个长度为 128k 的模型，适用于生成超长文本。
    LLMVendor.KIMI: LLMVendorProp('KIMI', 'KIMI_API_KEY', 'moonshot-v1-32k', '0', 'https://api.moonshot.cn/v1'),

    # gpt-3.5-turbo
    # gpt-4
    LLMVendor.OPENAI: LLMVendorProp('OPENAI', 'OPENAI_API_KEY', 'gpt-3.5-turbo', '0', 'https://api.openai.com/v1'),

    # gpt-3.5-turbo
    # gpt-4
    LLMVendor.OPENAI_PROXY: LLMVendorProp('OPENAI', 'OPENAI_API_KEY', 'gpt-3.5-turbo', '0', 'https://api.openai.com/v1')
}


def get_llm_vendor_prop(vendor: LLMVendor) -> LLMVendorProp:
    return vendor_map[vendor]
