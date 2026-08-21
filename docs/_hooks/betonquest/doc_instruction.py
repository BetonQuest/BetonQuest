class InstructionArgument:
    def __init__(self, value: str):
        self.value = value

    def get_value(self) -> str:
        return self.value


class DocInstruction:
    def __init__(self, files, page, id: str, arguments: list[InstructionArgument] = []):
        self.id = id
        self.page = page
        self.arguments = arguments
        self.files = files

    def get_id(self) -> str:
        return self.id

    def get_page(self):
        return self.page

    def get_files(self):
        return self.files

    def get_arguments(self) -> list[InstructionArgument]:
        return self.arguments

    def argument_count(self) -> int:
        return len(self.arguments)

    def get_argument(self, index: int = 0) -> InstructionArgument:
        return self.arguments[index]


def parse(files, page, instruction: str) -> DocInstruction | None:
    parts = instruction.strip().split(" ")
    cmd = parts[0]
    if not cmd.startswith("bq:"):
        return None
    id = cmd[3:]
    str_args = parts[1:]
    arguments = _parse_arguments(str_args)
    return DocInstruction(files, page, id, arguments)


def _parse_arguments(str_args: list[str]) -> list[InstructionArgument]:
    return [_parse_argument(arg) for arg in str_args]


def _parse_argument(str_arg: str) -> InstructionArgument:
    return InstructionArgument(str_arg)
