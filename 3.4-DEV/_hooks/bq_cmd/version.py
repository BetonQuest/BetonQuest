import re
from betonquest.badges import Badge
from betonquest.doc_instruction import DocInstruction
from betonquest.path_utils import relative_link
from bq_cmd.cmd_template import BqCmd
from pathlib import Path

ORIGIN_CHANGELOG = "CHANGELOG.md"
ORIGIN_API_CHANGELOG = "API-CHANGELOG.md"

CHANGELOG_BASE_LINK = "Documentation/CHANGELOG.md"
API_CHANGELOG_BASE_LINK = "API/CHANGELOG.md"

ICON = "material-tag-outline"


class VersionCmd(BqCmd):
    def __init__(self, log):
        super().__init__("version", log)
        self.file_changelog = None
        self.file_api_changelog = None
        self.src_file = None

    def populate_files(self, files):
        for file in files:
            if file.src_uri.endswith(CHANGELOG_BASE_LINK):
                self.file_changelog = file
            if file.src_uri.endswith(API_CHANGELOG_BASE_LINK):
                self.file_api_changelog = file

    def get_changelog_file(self, api: bool):
        return self.file_api_changelog if api else self.file_changelog

    def get_log(self):
        return self.log

    def get_src_file(self):
        return self.src_file

    def run(self, instruction: DocInstruction):
        version = instruction.get_argument(0)
        is_api = instruction.argument_count() == 2 and instruction.get_argument(1).value == "api"
        self.src_file = instruction.get_page().file
        self.populate_files(instruction.get_files())
        self.log.debug(f"Resolved version command: {version.value} / api: {is_api}")
        return _version_command(version, is_api, self)


def _version_command(version: str, is_api: bool, src: VersionCmd):
    tag_text = "Minimum supported version" if not is_api else "Minimum supported API version"
    ref = _read_changelog_link(version.value, is_api, src)
    tag = f"[:{ICON}:]('{tag_text}')"
    text = f"[{version.value}]({ref} '{version.value}')"
    return Badge(tag, text).get_badge()


def _read_changelog_link(version: str, api: bool, src: VersionCmd):
    changelog = Path(ORIGIN_CHANGELOG if not api else ORIGIN_API_CHANGELOG).read_text(encoding="utf-8")
    raw_pattern = r"## \[%\] - (.*)\s*".replace("%", version)
    pattern = re.compile(raw_pattern)
    match = re.search(pattern, changelog)
    relative_changelog_link = relative_link(src.get_src_file(), src.get_changelog_file(api))
    if not match:
        log.error(f"Could not find changelog entry for version {version} / api: {api}")
        return relative_changelog_link
    raw_link = version.replace(".", "") + "-" + match.group(1)
    return relative_changelog_link + "#" + raw_link
