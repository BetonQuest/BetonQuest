from betonquest.doc_instruction import DocInstruction


class BqCmd:
    def __init__(self, id: str, log):
        self.id = id
        self.log = log

    def match(self, id: str) -> bool:
        return self.id == id

    def run(self, instruction: DocInstruction):
        self.log.error("Not implemented")
