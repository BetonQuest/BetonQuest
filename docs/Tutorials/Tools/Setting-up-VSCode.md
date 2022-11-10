---
icon: octicons/paste-16
tags:
  - VSCode
  - Editor
---

What is the most important thing, before we can start writing quests?
Correct! We need an editor to edit our files and make life easier with some shortcuts
and a good syntax highlighting. For this tutorial we will introduce you to
Visual Studio Code (VSCode). It is one of the most popular code editors.
You will learn how to install it, how to use it and some tips and tricks!

But very first: What is YAML?
YAML is a data-serialization language that is readable by humans. It is frequently used for configuration files and in programs that store or send data.

<div class="grid" markdown>
!!! danger "Requirements"
    * No further requirements.

!!! example "Related Docs"
    * No references.
</div>


## 1. Download and installing VSCode

In this topic we go through the download and installing steps that need to be done.

_Step 1:_ [Download the right version from the VSCode website](https://code.visualstudio.com/download) 
for your operating system.

??? example "VSCode Download Screenshot"
    ![VSCode Website](../../_media/content/Tutorials/VSCode-Setup/download_VSCode.png)

After you successfully downloaded the right version you can move on with the next step.

---
_Step 2:_ Click on the VSCodeUserSetup-xXX-X.XX.X.exe in your download folder. (You'll
find your _downloads_ folder in your explorer.)

---
_Step 3:_ Go through the installation steps you can leave everything default. No need to change ticks of any checkbox!
If you completed the installation and have the setup is closed. Move on with _Step 4_.

---
_Step 4:_ Double click Visual Studio Code on your desktop or search it in your searchbar.

---
_Step 5:_ In this last step we will activate the `Auto-Save` function from VSCode.
Go to "File" and then click on "Auto-Save" to activate it.

This is a very useful feature because it automatically saves your progress and no data will
get lost on unexpected shutdowns or whatever can happen.
This is also good because there is no need for extra save your files. You can just
go inGame and reload the plugin.

??? example "VSCode Auto-Save Screenshot"
    ![auto_save](../../_media/content/Tutorials/VSCode-Setup/auto_save.png)
    
In this topic you learned how to download and install Visual Studio Code and even
how to activate the _Auto-Save_ function that's built-in. Now you are good to go
to the next topic and learn how to create a workspace!
    
## 2. Creating a workspace in VSCode

If you want to work fast and effectively, it is necessary to create a workspace!
The advantage of a workspace is that you can edit your files directly from the folder, so
there is no need to extra save them or open them again every time you closed them.

??? tip ""
    If you want to have more than one workspace, simply right-click in the folder box as showed in the screenshot.
    This is useful when having a production server and a test server for example.
    ![more workspace](../../_media/content/Tutorials/VSCode-Setup/auto_save.png)
    

And this is how to do it:

_Step 1: Left-click on the folder button._
![folder button](../../_media/content/Tutorials/VSCode-Setup/creating_workspace_1.png)

_Step 2: Left-click on the "Open Folder"._
![Open Folder](../../_media/content/Tutorials/VSCode-Setup/creating_workspace_2.png)


_Step 3: Define a path and click on "Add"._

_Navigate to your server files -> got to plugins folder -> click on "BetonQuest" folder -> click on "Add"_
(DO NOT DOUBLE CLICK BETONQQUEST FOLDER!)
![Define a path](../../_media/content/Tutorials/VSCode-Setup/creating_workspace_3.png)

If you have done everything correctly it should look like this (If not, start this topic
again):

![Workspace result](../../_media/content/Tutorials/VSCode-Setup/creating_workspace_result.png)

Now that our workspace is ready to work aty, you can edit the configs, create new quest files/folders and
even create new templates. You can do everything you want from just this one place!

In the next topic you will learn more about how to find YAML syntax errors and the meaning of it.

## 3. Installing YAML Syntax extension

You'll soon find out that something is not working because you miss typed something or
have a space too much... VSCode showing these errors and highlighting them.
Because this is NOT a built-in function we need to install the _YAML Plugin_ from the
Extensions store!

To do so, click on the "_Extensions Button_" as it is shown in the picture below:

![install plugin](../../_media/content/Tutorials/VSCode-Setup/plugin_installation.png)

A new tab will open. Now search for `YAML` and install the plugin from the author "Red Hat"

![yaml plugin](../../_media/content/Tutorials/VSCode-Setup/yaml.png)

If the installation was successful you can click on the folders button on the left
side again.

!!! tip ""
    No worries, installing this plugin is not dangerous. It is verified!

## 4. Checking YAML Syntax

You wonder how to use the new _YAML_ plugin and how to read these errors?
In this topic we will show you how to interpret some kinds of errors and how
to fix them.

Let me show you a not working example of a small quest:
```YAML
conversations:
  Jack:
    quester: "Jack"
    first: "completeQuest"
    NPC_options:
      completeQuest:
       text: "Hello, how are you?"
       conditions: !hasEnoughFish

events:
  giveFishObj "objective add fishObj"
  notifyPlayer: 'notify You've completed the quest!'
  addTag: "tag add enoughFish"

conditions:
  hasEnoughFish: "tag enoughFish"

objectives:
  fishObj: "fish COD 5 events:addTag"
```

On the first look, you can see that in the events section is one event written in green instead of blue.
That's because of a YAML Syntax error.
Do you already see the mistake here? It is simple: There is a colon missing after
the key `giveFishObj`. Because of the missing colon, now the YAML don't know that this should be a key.

But now we will have a look at VSCode:

![yaml errors 1](../../_media/content/Tutorials/VSCode-Setup/yaml_errors_1.png)

The YAML Syntax extension already shows you the mistakes and try to give you a solution for
it. To look at the proposed solution you have to hover with your mouse over the word/sentences
underlined with a red line and click `ALT` + `F8`.

It looks like this:
![yaml errors 2](../../_media/content/Tutorials/VSCode-Setup/yaml_errors_2.png)

This is how you could solve the problems on your own if anything is not working as you expect it.

!!! warning ""
    Not everything could be displayed here. There are several more errors, but we
    tried to cover the most common ones

Let's have a look at the condition `!hasEnoughFish` in the conversations part:

![yaml errors 3](../../_media/content/Tutorials/VSCode-Setup/yaml_errors_3.png)

This will give us a _unresolved tag: ..._ error because we need to recognize, that
we can't negate a condition with a _( ! ) exclamation mark_ without surrounding it with _( " " ) double quotes_.

Another common mistake is to use single quotes to surround a value and then use it in a conversation
like this:

![yaml errors 4](../../_media/content/Tutorials/VSCode-Setup/yaml_errors_4.png)

You'll notice that there are three single quotes in one line. To prevent those errors
we highly recommend to use double quotes to surround a value and single quotes in a simple
sentence.
 
## Summary

In this Tutorial you've learned how to download and install VSCode and how to create
a workspace for your project. Further you've learned how to install the YAML Syntax plugin
and how to interpret common errors.
---
