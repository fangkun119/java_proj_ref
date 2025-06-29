import inspect
import pydoc


class CodeUtils:
    @staticmethod
    def print_code(method_or_class):
        source_code = inspect.getsource(method_or_class)
        formatted_class_definition = pydoc.plain(source_code)
        print(formatted_class_definition)
