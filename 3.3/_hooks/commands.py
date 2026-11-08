import re
from mkdocs.plugins import get_plugin_logger

from betonquest.doc_instruction import DocInstruction
from betonquest.doc_instruction import parse as parse_doc_instruction
from bq_cmd.element_scraper import ElementScraperCmd
from bq_cmd.version import VersionCmd

COMMAND_PATTERN = re.compile(r"<!--\s*(bq:.*)\s*-->")

log = get_plugin_logger("betonQuest-cmd")

COMMANDS = [VersionCmd(log), ElementScraperCmd(log)]


def on_page_markdown(markdown, **kwargs):
    return re.sub(COMMAND_PATTERN, lambda match: _replace(kwargs["files"], kwargs["page"], match), markdown)


def _replace(files, page, match):
    instruction = parse_doc_instruction(files, page, match.group(1).strip())
    if instruction is None:
        return match.group(0)
    return _replace_command(instruction)


def _replace_command(instruction: DocInstruction):
    id = instruction.get_id()
    for cmd in COMMANDS:
        if cmd.match(id):
            return cmd.run(instruction)
    log.error(f"Unknown command: {id}")
    return None
