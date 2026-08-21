import re
from enum import Enum


class ArgumentType(Enum):
    RAW = 1
    NAMED = 2


class InstructionArgument:
    def __init__(self, type: ArgumentType = ArgumentType.RAW, value: str = "", name: str = ""):
        self.type = type
        self.name = name
        self.value = value

    def get_type(self) -> ArgumentType:
        return self.type

    def get_name(self) -> str:
        return self.name

    def get_value(self) -> str:
        return self.value


class DocInstruction:
    def __init__(self, page, id: str, arguments: list[InstructionArgument] = []):
        self.id = id
        self.page = page
        self.arguments = arguments

    def get_id(self) -> str:
        return self.id

    def get_page(self):
        return self.page

    def get_arguments(self) -> list[InstructionArgument]:
        return self.arguments

    def argument_count(self) -> int:
        return len(self.arguments)

    def get_argument(self, index: int = 0) -> InstructionArgument:
        return self.arguments[index]

    def has_argument(self, name: str) -> bool:
        return any(arg.name == name for arg in self.arguments)


ARGUMENT_PATTERN = re.compile(r"(([^:])+:)?(.*)")


def parse(page, instruction: str) -> DocInstruction | None:
    parts = instruction.strip().split(" ")
    cmd = parts[0]
    if not cmd.startswith("bq:"):
        return None
    id = cmd[3:]
    str_args = parts[1:]
    arguments = _parse_arguments(str_args)
    return DocInstruction(page, id, arguments)


def _parse_arguments(str_args: list[str]) -> list[InstructionArgument]:
    return [_parse_argument(arg) for arg in str_args]


def _parse_argument(str_arg: str) -> InstructionArgument:
    match = ARGUMENT_PATTERN.match(str_arg)
    if match:
        name = match.group(2)
        value = match.group(3)
        return InstructionArgument(name=name, value=value)
    return InstructionArgument(value=str_arg)
