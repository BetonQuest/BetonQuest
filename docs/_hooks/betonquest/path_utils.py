import posixpath
from pathlib import Path
from pathlib import PurePosixPath


def relative_link(source_file, target_file):
    source = PurePosixPath(source_file.src_uri)
    target = PurePosixPath(target_file.src_uri)
    source_dir = source.parent
    return posixpath.relpath(target, start=source_dir)
