import os
import subprocess

common_dependencies = [
    'mkdocs-snippets',
    'mike==2.0.0',
    'mkdocs-exclude==1.0.2',
]


def get_install_requires():
    mkdocs_token = os.environ.get('MKDOCS_MATERIAL_INSIDERS')

    if mkdocs_token:
        return [
            'git+https://' + mkdocs_token + '@github.com/squidfunk/mkdocs-material-insiders.git@9.5.2-insiders-4.47.1',
            'pillow',
            'cairosvg',
        ] + common_dependencies
    else:
        return ['mkdocs-material==9.5.2'] + common_dependencies


subprocess.run(['pip', 'install'] + get_install_requires())
