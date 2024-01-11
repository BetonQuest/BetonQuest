import os
import platform
import subprocess


common_dependencies = [
    'mkdocs-snippets',
    'mike==2.0.0',
    'mkdocs-exclude==1.0.2',
]


def get_install_requires():
    mkdocs_token = os.environ.get('MKDOCS_MATERIAL_INSIDERS')

    if mkdocs_token:
        mkdocs_enabled = os.environ.get('MKDOCS_MATERIAL_INSIDERS_ENABLED')
        if not mkdocs_enabled:
            print("\033[93m"
                + "Warning: 'MKDOCS_MATERIAL_INSIDERS' is set "
                + "but 'MKDOCS_MATERIAL_INSIDERS_ENABLED' is not set to 'true', "
                + "so if you serve mkdocs you will serve the normal version!"
                + "\033[0m")
        return [
            f'git+https://{mkdocs_token}@github.com/squidfunk/mkdocs-material-insiders.git@9.5.2-insiders-4.47.1',
            'pillow',
            'cairosvg',
        ] + common_dependencies
    else:
        return ['mkdocs-material==9.5.2'] + common_dependencies


subprocess.run(['pip', 'install'] + get_install_requires())
