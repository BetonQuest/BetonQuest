---
icon: material/microsoft-visual-studio-code
tags:
  - VSCode
  - Editor
---

# VSCode Basics

Now that Visual Studio Code is installed and your BetonQuest folder is opened as a workspace, we will look at the
basic controls you need for writing quests.

You do not need to know every VSCode feature. For BetonQuest, it is enough to understand how to find files, edit them,
spot syntax errors, and reload your changes on the server.

<div class="grid" markdown>
!!! danger "Requirements"
    * [Setting up VSCode](./Setting-up-VSCode.md)

!!! example "Related Docs"
    * [YAML Basics](../Basics/YAML-Basics.md)
    * [Handling YAML Errors](../../Tools/YAML-Errors.md)
</div>

## 1. Understanding the Layout

VSCode is split into a few important areas:

* **Activity Bar**: The icons on the far left. You can switch between files, search, extensions, and other tools here.
* **Explorer**: The file tree next to the Activity Bar. This is where you open your BetonQuest files.
* **Editor**: The large area in the middle. This is where you change quest files.
* **Tabs**: Open files are shown at the top of the editor. Click a tab to switch between files.
* **Status Bar**: The bar at the bottom. It shows information about the current file, including the file type and errors.

Most of the time, you will work in the Explorer and the Editor.

!!! tip "Keep the BetonQuest folder open"
    If the Explorer does not show files like `package.yml`, `actions.yml`, `conditions.yml`, or `conversations`,
    you probably opened the wrong folder.

## 2. Opening and Editing Files

Quest packages are made of `.yml` files. To edit one, open it from the Explorer with a left-click.

Common files are:

* `package.yml` for the package structure
* `actions.yml` for actions
* `conditions.yml` for conditions
* `objectives.yml` for objectives
* files inside `conversations` for NPC conversations

After opening a file, click into the editor and change the text.

If you enabled `Auto-Save` in the setup tutorial, VSCode saves your changes automatically.
If you did not enable it, save with ++ctrl+s++ before testing your quest.

!!! warning "Unsaved files"
    A white dot next to a file name means the file has unsaved changes. BetonQuest can only load the saved version.

## 3. Creating Files and Folders

You can create new files and folders directly in the Explorer.

Right-click the folder where the new file should be created and choose **New File** or **New Folder**.
For example, you can create a new conversation file like this:

```text
conversations/blacksmith.yml
```

Use clear names without spaces. This makes files easier to reference later.

!!! tip "Use the correct file ending"
    BetonQuest configuration files should end with `.yml`. A file named `actions.txt` or `actions.yaml` is not the same.

## 4. Searching Through Your Quest Files

Once your quest grows, scrolling through every file becomes slow.
Use the search view instead.

Click the search icon in the Activity Bar or press ++ctrl+shift+f++.
Then search for names like an action ID, a condition ID, an NPC name, or a tag.

This is useful when you want to find where something is used before renaming or deleting it.

!!! example "Example"
    If an action is called `start_tour`, search for `start_tour` before changing its name.
    You may find that it is used in a conversation option or inside another action.

## 5. Reading YAML Errors

With the Red Hat YAML extension installed, VSCode marks many YAML syntax mistakes before you reload BetonQuest.

Errors are usually shown in three places:

* a red underline in the editor
* a red file name in the Explorer
* the **Problems** panel at the bottom

Click an error in the Problems panel to jump directly to the affected line.

Common mistakes are:

* using tabs instead of spaces
* forgetting a colon after a key
* using different indentation levels in the same section
* forgetting quotes around text with special characters

!!! note
    VSCode can find YAML syntax errors, but it cannot understand every BetonQuest-specific mistake.
    If VSCode shows no YAML error but the quest still does not work, read the server console after `/bq reload`.

## 6. Testing Changes Ingame

The normal workflow for writing quests is:

1. Change a file in VSCode.
2. Save the file or wait for Auto-Save.
3. Run `/bq reload` on your server.
4. Read the console and ingame messages.
5. Fix errors and test again.

Small changes are easier to debug than large changes.
Reload often, especially after editing indentation or adding new quest elements.

!!! warning "Reload before testing"
    If you change a file and immediately test ingame without reloading, the server may still use the old version.

## Summary

You now know the VSCode basics needed for BetonQuest:

* opening the correct workspace
* editing and saving `.yml` files
* creating files and folders
* searching through quest files
* reading YAML errors
* testing changes with `/bq reload`

Next, we will look at the YAML format itself. YAML is the structure used in almost every BetonQuest file.

---
<div class="grid" markdown style="text-align: center;">
<div markdown style="text-align: left;">
[:octicons-arrow-left-16: Setting up Visual Studio Code](./Setting-up-VSCode.md){ .md-button .md-button--primary }
</div>
<div markdown style="text-align: right;">
[YAML Basics :octicons-arrow-right-16:](../Basics/YAML-Basics.md){ .md-button .md-button--primary }
</div>
</div>
