#Setting up the editor

In theory, you can edit quests with any editor. However, using the feature-packed but lightweight 
Visual Studio Code is highly recommended!
We even made our own **^^Visual Studio Code BetonQuest addon^^** 
that enables some neat productivity advantages over any other editor.

## Installation

* Head over to <a href="https://code.visualstudio.com" target="_blank">Visual Studio Code's site</a>
  to install the latest version.

* Then install the BetonQuest extension from the
  <a href="https://marketplace.visualstudio.com/items?itemName=BetonQuest.betonquest-code-snippets" target="_blank">Visual Studio Marketplace</a>.
  Just click the big green "Install" button and allow the site to open Visual Studio Code.

![Marketplace Screenshot](../../_media/content/Tutorials/addon-marketplace.png){ width=60% }

* Just click "Install" again after Visual Studio Code has opened. 

![VSCode Install Screenshot](../../_media/content/Tutorials/addon-in-editor.png){ width=60% }
 
If this does not work for some reason go to the extensions view in VSCode (++ctrl+shift+x++) and search for `BetonQuest`.
A full guide on extensions can be found <a href="https://code.visualstudio.com/docs/editor/extension-gallery" target="_blank">here</a>.


## Usage

### Setup

Open up any of your quests using *File > Open Folder...* (if you haven't made any yet, select the default quest in *plugin/BetonQuest/default*).
Then click on any file to open it.
There are a few general things to keep in mind when editing files.
VSCode does a really nice job at exposing these directly to the user. They are all listed in the bottom right corner 
of the editor window:

!!! caution
    **Make sure to set these settings in the bottom right corner of VSCode**:

    `Spaces:2` Indent using Spaces. 
    
    `UTF-8` file encoding. 
    
    `LF` end of line characters. 
    
    `YAML` language module.

![Shows the location of the settings](../../_media/content/Tutorials/vscode.png){width=60%}

These ensure that the extension is activated.
Additionally, your configuration works with all unique language characters (a german example: ä, ü, ö)
and the file can be read on any server running on Linux.

### Snippets 
The main feature of our extension are so called "code snippets". These are basically pre-defined events, conditions etc.
that can be filled with any of your custom options.

Start typing the name of an event/condition/objective and press ++ctrl++ + ++space++. VSCode will list possible completions.
These include both snippets and yaml entries that are already defined in your file. Snippets will have the official 
documentation as description. 
VSCode will enter snippet mode once you press ++enter++ while having a snippet selected.
Navigating between options works by pressing ++tab++.
If you want to go backwards you can invert the movement of the cursor by pressing ++shift++ and ++tab++.

An option will be fully highlighted if it needs to be replaced with material names, messages... They will not show a drop-down menu.
If an option does, you need to choose between the offered options. Optional options are displayed as an empty field in the drop down.
These optional options can also contain a colon (`:`). You have to specify something behind them depending on the context 
if they do. An example might be provided after the colon (`:`). Just replace it with the value you want.

**Neat features:** 

* You will automatically jump to the next line if you press tab one last time after any option.
* The snippet automatically surrounds the event/condition... with `""`.
* There are not only snippets for `events`, `objectives` and `conditions` but also for conversations!
* Pressing ++alt++ + ++b++ creates a new conversationOption.


**Take a look at this short video or play around with it yourself until you understand how it works:**

*Note: This video is slightly outdated and does not show some newer features!*

<div style="text-align: center">
 <video controls loop
     src="../../../_media/content/LearnBeton/VSCodeExtension.mp4"
     width="780" height="500">
 Sorry, your browser doesn't support embedded videos.
 </video>
</div>

**Next step: [YAML-for-questers](./YAML-for-questers.md)**
