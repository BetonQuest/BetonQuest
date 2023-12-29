---
icon: simple/visualstudiocode
tags:
  - VSCode
  - Editor
---

What is the most important thing before writing quests?
Correct! We need a good text editor to make our lives easier with shortcuts and syntax highlighting.
In this tutorial, you will be introduced to Visual Studio Code (VSCode), the most popular text editor.

In this tutorial, you will learn how to install and use it to your advantage!


<div class="grid" markdown>
!!! danger "Requirements"
    * No further requirements.

!!! example "Related Docs"
    * [YAML](../Getting-Started/Basics/YAML.md)
</div>


## 1. Download and installing VSCode

In this step we will install VSCode on your computer.

[:material-download: Install Visual Studio Code](https://code.visualstudio.com){ .md-button .md-button--primary .noExternalLinkIcon}

Install it like any other software. The default settings are sufficient for BetonQuest usage.

Now start Visual Studio Code. We will activate the `Auto-Save` feature.
Click "File" in the top left corner and then click on "Auto-Save".

This is a very useful feature that automatically saves your files. No data will ever be lost again!
It also saves you from one of the most common errors when writing quests: Forgetting to save!
From now on, you can just execute `/bq reload` as soon as your mouse leaves the editor.

??? example "VSCode Auto-Save Setup"
    ![auto_save](../../../_media/content/Tutorials/VSCode-Setup/auto_save.png)
    
Now that you have a working VS Code installation, you can learn how to use it.
    
## 2. Creating a workspace in VSCode

If you want to work fast and efficient, it is necessary to create a workspace!
A workspace is a folder that contains all your project's files. In this case, it will be the BetonQuest folder with all
quests and settings.

??? tip "More than one workspace"
    If you want to have more than one plugin in your workspace, you can click on "File" and then "Add Folder to Workspace"
    to add any other folder.
    You can also close and reopen the workspace at any time using the steps below.
    

And this is how to do it:

_Step 1:_ Left-click on the folder button.

![folder button](../../../_media/content/Tutorials/VSCode-Setup/creating_workspace_1.png)

_Step 2:_ Left-click on "Open Folder".

![Open Folder](../../../_media/content/Tutorials/VSCode-Setup/creating_workspace_2.png)


_Step 3:_ Define a path and click on "Add".

Navigate to your server files :arrow_right: Go to the "_plugins_" folder :arrow_right: Select the "_BetonQuest_" folder

![Define a path](../../../_media/content/Tutorials/VSCode-Setup/creating_workspace_3.png)

If you have done everything correctly, it will look like this:

![Workspace result](../../../_media/content/Tutorials/VSCode-Setup/creating_workspace_result.png)

In the next topic, you will learn more about how to deal with YAML syntax errors.

## 3. Installing YAML Syntax extension

You'll soon experience that your quest is not working because you made a YAML syntax error.
But what is YAML?

YAML is a data-serialization language that is readable by humans. It is frequently used for Minecraft related configuration files.
Unfortunately, it is quite easy to make a mistake in YAML syntax. 

VSCode can highlight these mistakes and suggest how to fix them. 
Because this is NOT a built-in function, we need to install the _YAML Plugin_ from the
Extensions store!

To do so, click on the "_Extension Button_" as it is shown in the picture below:

![install plugin](../../../_media/content/Tutorials/VSCode-Setup/plugin_installation.png)

A new tab will open. Now search for `YAML` and install the plugin from the author "Red Hat"

![yaml plugin](../../../_media/content/Tutorials/VSCode-Setup/yaml.png)

If the installation was successful, you can click on the folder button on the left
side again.

!!! tip ""
    No worries, installing this plugin is not dangerous. It is verified!

## 4. YAML Basics

YAML is `key: "value"` based. This means you use a :octicons-key-16: to get a certain value. Values are typically surrounded by double quotes (`"..."`).
Let me show you an example:

```YAML title="YAML Data Format"
key: "value"
Jack: "Some data about Jack"
```
Now you can use the :octicons-key-16: `Jack` to obtain `Some data about Jack`.

Keys and values can also be nested into each other. Then they **must** be indented with two **spaces**.

```YAML title="Nested YAML"
outerName:
  innerName: "innerValue"
  anotherInnerName: "BetonQuest is great!"
```

Tabs are not supported. Use spaces instead.

You shouldn't name anything `yes`, `no`, `on`, `off`, `null`, `true` or `false` as those names are reserved keywords in YAML.  
 
---

# 5. Spotting YAML Syntax Errors

Now you know the basics of YAML. But what if you make a mistake? How can you spot it?

Let me show you an example of a small quest with a few typical YAML errors. You do not have to understand any of the config
yet though. Based on the information from chapter four, you might be able to see that something is off.
```YAML title="Example Quest with YAML Errors" linenums="1"
conversations:
  Jack:
    NPC_options:
      completeQuest:
       text: "Hello, how are you?"
       conditions: !hasEnoughFish

events:
  giveFishObj "objective add fishObj"
  notifyPlayer: 'notify You've completed the quest!'
  addTag: "tag add enoughFish"

```

You will notice that two events in the events section are written in green instead of blue.
That's because of a YAML Syntax error.
Do you already see the mistake here? It is simple: There is a colon (`:`) missing after
the key `giveFishObj` in line 9. Because of the missing colon YAML will fail to parse this file.

But now let's have a look at the same file in VSCode. The YAML Syntax extension will clearly highlight the error:

![yaml errors 1](../../../_media/content/Tutorials/VSCode-Setup/yaml_errors_1.png)

If you hover over the error you will see more information:

![yaml errors 2](../../../_media/content/Tutorials/VSCode-Setup/yaml_errors_2.png)

Whilst these are quite technical and hard to understand, the highlighting will clearly show you where errors need to be fixed.

Let's have a look at the condition `!hasEnoughFish` in the conversations part:

![yaml errors 3](../../../_media/content/Tutorials/VSCode-Setup/yaml_errors_3.png)

This will give us an _unresolved tag: ..._ error because special characters like the exclamation mark (`!`) 
cannot be written without surrounding (`" "`) double quotes.

Another common mistake is to use single quotes to surround a value and then also use it inside the value itself
like this:

![yaml errors 4](../../../_media/content/Tutorials/VSCode-Setup/yaml_errors_4.png)

Instead, the line should be written like this: `notifyPlayer: {++"++}notify You've completed the quest!{++"++}`
**To prevent those errors we highly recommend to always use double quotes.**
 
## Summary

In this Tutorial, you've learned how to download and install VSCode and how to create
a workspace for your project. Further, you've learned how to install the YAML Syntax plugin
and how to interpret common errors.

Now that we have created a proper setup for writing quests, we will start writing your first quest!

---
[:material-arrow-right: Next Step: Conversations](../Basics/Conversations.md){ .md-button .md-button--primary }
