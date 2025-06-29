[TOC]

## 1. setup environment

check python version

~~~bash
# need python 3.12
python3 --version
~~~ 

create venv

~~~bash
rm -rf ./venv
python3 -m venv --system-site-packages venv
~~~

activate venv

~~~bash
source venv/bin/activate
~~~

install dependencies

~~~bash
python3 -m pip install --upgrade pip --trusted-host pypi.org --trusted-host pypi.python.org --trusted-host files.pythonhosted.org
python3 -m pip install -r requirements.txt --trusted-host pypi.org --trusted-host pypi.python.org --trusted-host files.pythonhosted.org
# 使用国内源加速
# python3 -m pip install -r requirements.txt -i https://mirrors.aliyun.com/pypi/simple
~~~

### 2. setup jupyter notebook

install jupyter

~~~bash
python3 -m pip install jupyter
python3 -m pip install ipykernel
~~~

install Kernel and setup up a Kernel that use venv

~~~bash
python3 -m ipykernel install --user --name=myenv --display-name="venv"
~~~

### 3. setup API keys

setup API keys in ~/.bash_profile

~~~bash
export OPENAI_API_KEY="sk-..." 
export ZHIPU_API_KEY="210...w5y" 
export TAVILY_API_KEY="tvl...E1R"
export LANGCHAIN_API_KEY="lsv...599"
export KIMI_API_KEY="sk-...h59"
~~~

### 4. start jupyter notebook

load API keys into environment variables and activate venv

~~~bash
source ~/.bash_profile
source venv/bin/activate
# check
echo $OPENAI_API_KEY
~~~

start jupyter notebook

~~~bash
# please select kernel 'venv' in webpage when jupyter notebook is started\npress any key to continue: 
python3 -m jupyter notebook
~~~

### 3. start local rest server

load API keys into environment variables and activate venv

~~~bash
source ~/.bash_profile
source venv/bin/activate
# check
echo $OPENAI_API_KEY
~~~

start rest server

~~~
python3 api/knowledge_helper.py
~~~

### 4. start web server (streamlit)

load API keys into environment variables and activate venv

~~~bash
source ~/.bash_profile
source venv/bin/activate
# check
echo $OPENAI_API_KEY
~~~

```shell
streamlit run knowledge_chatbot.py
```

<!-- not implemented yet -->
<!-- 02 实现基于 gradio + OpenAI 的QA问答（文件格式：PDF）-->
<!-- gradio chatbot_gradio.py -->

