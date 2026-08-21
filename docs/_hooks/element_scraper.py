import re
from mkdocs.config import config_options
from mkdocs.plugins import BasePlugin
from mkdocs.plugins import get_plugin_logger
from pathlib import Path

from betonquest.path_utils import relative_link

log = get_plugin_logger("element-scraper")
SCRAPER_PATTERN = re.compile(r"(\%\%scraper:([^%]+)\%\%)")
CONTENT_PATTERN = re.compile(r"__Context__: @snippet:([a-z]+)-meta:[a-z\-]+@\s+__Syntax__: `([^`]+)`")


def on_page_markdown(markdown, **kwargs):
    if not "%%scraper:" in markdown:
        return markdown
    result = contains_scraper(markdown)
    if result != "":
        elements = scrape_all(result[1].split(","), kwargs["files"])
        reference_dict = generate_reference_list(elements, kwargs["page"])
        markdown = markdown.replace(result[0], create_markdown(reference_dict))
    return markdown


def contains_scraper(markdown):
    match = SCRAPER_PATTERN.search(markdown)
    if match:
        return [match.group(1), match.group(2).strip()]
    return ""


def scrape_all(configured, files):
    result = []
    for file in files:
        path = Path(file.src_uri)
        if path.suffix.lower() != ".md":
            continue
        if path.name in configured:
            result.append(file)
            continue
        if any(parent in configured or parent.name in configured
               for parent in path.parents):
            result.append(file)
    return [scrape_page(file) for file in result]


def scrape_page(file):
    content = Path(file.abs_src_path).read_text(encoding="utf-8")
    matches = re.finditer(CONTENT_PATTERN, content)
    return [[match.group(2), match.group(1), file] for match in matches]


def generate_reference_list(elements, page):
    unpacked = [item for sublist in elements for item in sublist]
    unpacked.sort(key=lambda x: x[1] + x[0])
    result = [(element[1], generate_reference(element, page.file)) for element in unpacked]
    ref_dict = {}
    for ref in result:
        type = ref[0]
        if type in ref_dict:
            ref_dict[type].append(ref[1])
        else:
            ref_dict[type] = [ref[1]]
    return ref_dict


def generate_reference(element, relative_to):
    name = element[0]
    path = relative_link(relative_to, element[2]) + "#" + re.split(r"[\s\.]", name, maxsplit=1)[0]
    return f"[`{name}`]({path})"


def create_markdown(reference_dict):
    result = ""
    for type in reference_dict:
        result += f"## {type.title()}\n"
        for ref in reference_dict[type]:
            result += f"- {ref}\n"
    return result
