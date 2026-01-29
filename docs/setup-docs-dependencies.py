import os
import subprocess

common_dependencies = [
    'mkdocs-snippets',
    'mike==2.1.3',
    'mkdocs-exclude==1.0.2',
    'mkdocs-material==9.7.1',
]

subprocess.run(['pip', 'install'] + common_dependencies)
