import os
import platform
import subprocess


def update_or_set_environment_variable(profile_file, variable_name, variable_value):
    with open(profile_file, 'r') as f:
        lines = f.readlines()

    for i, line in enumerate(lines):
        if line.startswith(f'export {variable_name}'):
            lines[i] = f'export {variable_name}={variable_value}\n'
            with open(profile_file, 'w') as f:
                f.writelines(lines)
            print(f'Updated in {profile_file}')
            print(f"")
            return

    # If variable not found, append to the end of the file
    with open(profile_file, 'a') as f:
        f.write(f'export {variable_name}={variable_value}\n')
    print(f'Added to {profile_file}')
    print(f"")


def set_persistent_environment_variable(variable_name, variable_value):
    print(f"Setting environment variable '" + variable_name + "' to '" + variable_value + "'")
    if platform.system() == "Windows":
        subprocess.run(['setx', variable_name, variable_value], shell=True)
        print(f"")
    else:
        home_directory = os.path.expanduser("~")
        possible_profile_files = [
            os.path.join(home_directory, ".bashrc"),
            os.path.join(home_directory, ".bash_profile"),
            os.path.join(home_directory, ".zshrc"),
            os.path.join(home_directory, ".profile"),
        ]

        for profile_file in possible_profile_files:
            if os.path.isfile(profile_file):
                with open(profile_file, "a") as f:
                    update_or_set_environment_variable(profile_file, variable_name, variable_value)
                return
        if variable_value:
            print(
                f"Could not find a profile file to add the environment variable to. "
                f"Please add the environment variable '{variable_name}' with the value '{variable_value}' manually,"
                f"if you want to use the insiders version of mkdocs."
            )


common_dependencies = [
    'mkdocs-snippets',
    'mike==2.0.0',
    'mkdocs-exclude==1.0.2',
]


def get_install_requires():
    mkdocs_token = os.environ.get('MKDOCS_MATERIAL_INSIDERS')

    if mkdocs_token:
        set_persistent_environment_variable('MKDOCS_MATERIAL_INSIDERS_ENABLED', 'true')
        return [
            'git+https://' + mkdocs_token + '@github.com/squidfunk/mkdocs-material-insiders.git@9.5.2-insiders-4.47.1',
            'pillow',
            'cairosvg',
        ] + common_dependencies
    else:
        set_persistent_environment_variable('MKDOCS_MATERIAL_INSIDERS_ENABLED', 'false')
        return ['mkdocs-material==9.5.2'] + common_dependencies


subprocess.run(['pip', 'install'] + get_install_requires())
