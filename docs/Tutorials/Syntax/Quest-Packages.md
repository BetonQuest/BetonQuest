---
icon: material/folder-open
tags:
  - QuestPackages
---

# :material-folder-open: Quest Packages

This tutorial will teach you about the package managing system, which helps you keep your quests organized.
You can write a small quest in just one file or split a big quest into lots of small files. It's completely
up to you! 

!!! warning "Attention"
    If you are using an older version of BetonQuest and just want to convert your 1.12 packages to the new 2.0 format,
    use the [Migration Guide](../../Documentation/Migration.md).

<div class="grid" markdown>
!!! danger "Requirements"
    Doing this tutorial helps but is not strictly required:
    
    * [Basic Tutorial](../Getting-Started/About.md)

!!! example "Related Docs"
    * [Package Structure Reference](../../Documentation/Reference.md#structure)
    * [Defining Features Reference](../../Documentation/Reference.md#defining-features)
</div>

## 1. General explanation of Quest Packages

This part of the tutorial will teach you how the new quest packages work and how it's different from the 1.12 version. 

A quest package is a folder that contains all the files that belong to a quest. Since BetonQuest doesn't have its own 
definition of a quest, a quest package could technically contain multiple quests as well. That is up to you.
 
Let's take a look at the following example of a typical quest in 2.0 and 1.12:

<div class="grid" markdown>
!!! success "BetonQuest 2.0.0 Concept"
    * :material-folder-open: BetonQuest (Plugin Folder)
        * :material-folder-open: QuestPackages
             - :material-folder-open: myExampleQuest
                 - :material-file-star: package.yml
                 - :material-file: myEventsList1.yml
                 - :material-file: myEventsList2.yml
                 - :material-file: importantConditions.yml
                 - :material-file: normalObjectives.yml
                 - :material-file: dungeonObjectives.yml
                 - :material-file: myVariablesFile.yml
                 - :material-folder-open: conversations
                      - :material-file: indiana.yml
                      - :material-file: jones.yml
             - :material-folder-file: anotherQuest
             - :material-folder-file: spookyQuest

!!! warning "BetonQuest 1.12 Concept"
    * :material-folder-open: BetonQuest (Plugin Folder)
        * :material-folder-open: tutorialQuest
            - :material-file-star: main.yml
            - :material-file: events.yml
            - :material-file: conditions.yml
            - :material-file: objectives.yml
            - :material-file: custom.yml
            - :material-folder-open: conversations
                - :material-file: indiana.yml
                - :material-file: jones.yml
        * :material-folder-file: anotherQuest
        * :material-folder-file: spookyQuest
</div>

In the 1.12, the quests were created right in the BetonQuest plugin folder,
which made other files and folders (_config.yml, messages.yml, logs_ etc.) hard to find.
Additionally, each quest had to follow a strict layout - one file for one feature type.
It was not possible to apply your own naming conventions.

This is the opposite in 2.0: You can freely name your files and folders, and you can have as many files as you want.

Additionally, a package is no longer defined by a _main.yml_ file inside a folder.
Instead, we use a _package.yml_ file inside a folder.
If a folder lacks the _package.yml_, it will be considered as a part of another package that is located in a parent
folder. 

## 2. Creating a Quest Package with multiple files

We will now explore, how to work with this system. After this you will know how to create
your own quest packages!

We will begin as usual by creating the file structure. Later on we will add content to it.
Create your own structure in the _QuestPackages_ folder. I'm using something similar to the example from above:

* :material-folder-open: QuestPackages
    - :material-folder-open: myExampleQuest
      - :material-file: package.yml
      - :material-file: myEventsList1.yml
      - :material-file: myEventsList2.yml
      - :material-file: importantConditions.yml
      - :material-file: myAwesomeObjectives.yml
      - :material-file: myVariablesFile.yml
      - :material-folder-open: conversations
          - :material-file: jones.yml

Let's fill those files with a little quest to make it a bit clearer for you!
The example will be a small and simple woodcutting quest with a reward upon completion: 

!!! Example

    === "package.yml"
        ```YAML
        {==npcs==}:
          '0': "Jones"
          
        {==items==}:
          oakLog: "minecraft:oak_log"
          jonesAxe: "IRON_AXE name:§7Jones_Hardened_Axe enchants:DIG_SPEED:2,DURABILITY:4"
        ```
    === "jones.yml"
        ```YAML
        {==conversations==}:
          Jones:
            quester: "Jones"
            first: "questAlreadyDone,noWoodInInv,wrongWood,questNotDone,questDone,firstGreeting"
            NPC_options:
              firstGreeting:
                text: "Yoo! You look like you can handle those heavy axes to cut down some trees..?"
                pointer: "probably"
              woodAmountAnswer:
                text: "Bring me 20 logs of oak and you will get my special axe for woodcutting!"
                pointer: "letsDoIt"
              seeYou:
                text: "See you soon!"
              noWoodInInv:
                text: "Looks like you don't have the required wood with you. Bring me 20 oak logs!"
                conditions: "startedTag,woodcuttingDoneTag,!logsInInventory"
              wrongWood:
                text: "Oh you still need some time for the mission?.. You have to actually chop them down and not take it from your chest!"
                conditions: "startedTag,!woodcuttingDoneTag,logsInInventory"
              questNotDone:
                text: "Oh you still need some time for the mission?.."
                conditions: "startedTag,!woodcuttingDoneTag,!logsInInventory"
              questDone:
                text: "That's the wood I was looking for! Thank you so much! Here is my special axe for my special friend."
                events: "questDone"
                conditions: "woodcuttingDoneTag,logsInInventory"
              questAlreadyDone:
                text: "Hey! I don't need you anymore. Thanks again for the help."
                conditions: "questDoneTag"
            player_options:
              probably:
                text: "Yes I can do that for you! How much wood do you need?"
                pointer: "woodAmountAnswer"
              letsDoIt:
                text: "Alright let's get the job done!"
                pointer: "seeYou"
                events: "questStarted"
        ```
    === "myEventsList1.yml"
        ```YAML
        {==events==}:
          questStarted: "folder startedTagAdd,addWoodcuttingObj"
          startedTagAdd: "tag add startedTag"
          addWoodcuttingObj: "objective add woodCuttingObj"
        ```
    === "myEventsList2.yml"
        ```YAML
        {==events==}:
          questDone: "folder takeWoodFromPlayer,rewardPlayer,addQuestDoneTag"
          takeWoodFromPlayer: "take oakLog:10"
          rewardPlayer: "give jonesAxe"
          addQuestDoneTag: "tag add questDoneTag"
          
          addWoodcuttingDoneTag: "tag add woodcuttingDoneTag"
        ```
    === "myAwesomeObjectives.yml"
        ```YAML
        {==objectives==}:
          woodCuttingObj: "block OAK_LOG -10 notify events:addWoodcuttingDoneTag"
        ```
    === "importantConditions.yml"
        ```YAML
        {==conditions==}:
          woodcuttingDoneTag: "tag woodcuttingDoneTag"
          logsInInventory: "item oakLog:10"
          questDoneTag: "tag questDoneTag"
          startedTag: "tag startedTag"
        ```
        
As you can see: Every feature goes into a section like `events:`, `objectives:`, `conversations:` which are marked in
{==blue==} in this example quest.
Instead of file names like in 1.12, BetonQuest now uses these section names to understand the contents of a file.
You can write these sections in any file you want, and it will still work! That's the way you can organize your quests.

!!! note "A note about Sections"
    While you can have multiple sections in one file, you can't have multiple sections with the same name.
    For example, you can't have two `events:` sections in one file. If you do, the second one will overwrite the first one.
    You can however have `events:` sections in two different files. In this case, the events from both files will be loaded.

Download this example quest and play around with it. This will help you understand it.

--8<-- "Tutorials/download-complete-files.md"
    ```
    /bq download BetonQuest/Quest-Tutorials main QuestPackages /Syntax/packageStructure/1-MultiFileStructure /packageStructure/MultiFile
    ```
    You can now find all files needed for this tutorial in this location:
    "_YOUR-SERVER-LOCATION/plugins/BetonQuest/QuestPackages/packageStructure/MultiFile_"

## 3. Creating a Quest Package with a single file

Now that you understood how the new _multifile_ system works we will try another example.
It's the exact same quest but in just one file:

!!! Example
     ```YAML title="package.yml"
     {==npcs==}:
       '0': "Jones"
       
     {==items==}:
       oakLog: "minecraft:oak_log"
       jewelry: "minecraft:diamond"

     {==conversations==}:
       Jones:
         quester: "Jones"
         first: "questAlreadyDone,noWoodInInv,wrongWood,questNotDone,questDone,firstGreeting"
         NPC_options:
           firstGreeting:
             text: "Yoo! You look like you can handle those heavy axes to cut down some trees..?"
             pointer: "probably"
           woodAmountAnswer:
             text: "Bring me 20 logs of oak and you will get my special axe for woodcutting!"
             pointer: "letsDoIt"
           seeYou:
             text: "See you soon!"
           noWoodInInv:
             text: "Looks like you don't have the required wood with you. Bring me 20 oak logs!"
             conditions: "startedTag,woodcuttingDoneTag,!logsInInventory"
           wrongWood:
             text: "Oh you still need some time for the mission?.. You have to actually chop them down and not take it from your chest!"
             conditions: "startedTag,!woodcuttingDoneTag,logsInInventory"
           questNotDone:
             text: "Oh you still need some time for the mission?.."
             conditions: "startedTag,!woodcuttingDoneTag,!logsInInventory"
           questDone:
             text: "That's the wood I was looking for! Thank you so much! Here is my special axe for my special friend."
             events: "questDone"
             conditions: "woodcuttingDoneTag,logsInInventory"
           questAlreadyDone:
             text: "Hey! I don't need you anymore. Thanks again for the help."
             conditions: "questDoneTag"
         player_options:
           probably:
             text: "Yes I can do that for you! How much wood do you need?"
             pointer: "woodAmountAnswer"
           letsDoIt:
             text: "Alright let's get the job done!"
             pointer: "seeYou"
             events: "questStarted"

     {==events==}:
       questStarted: "folder startedTagAdd,addWoodcuttingObj"
       startedTagAdd: "tag add startedTag"
       addWoodcuttingObj: "objective add woodCuttingObj"

       questDone: "folder takeWoodFromPlayer,rewardPlayer,addQuestDoneTag"
       takeWoodFromPlayer: "take oakLog:10"
       rewardPlayer: "give jewelry:2"
       addQuestDoneTag: "tag add questDoneTag"
       
       addWoodcuttingDoneTag: "tag add woodcuttingDoneTag"

     {==objectives==}:
       woodCuttingObj: "block OAK_LOG -10 notify events:addWoodcuttingDoneTag"

     {==conditions==}:
       woodcuttingDoneTag: "tag woodcuttingDoneTag"
       logsInInventory: "item oakLog:10"
       questDoneTag: "tag questDoneTag"
       startedTag: "tag startedTag"
     ```

You can download this example as well. No worries, the files of the previous example will not get overwritten.
Instead, a new folder will be created in the previous package.

--8<-- "Tutorials/download-complete-files.md"
    ```
    /bq download BetonQuest/Quest-Tutorials main QuestPackages /Syntax/packageStructure/2-SingleFileStructure /packageStructure/SingleFile
    ```
    You can now find all files needed for this tutorial in this location:
    "_YOUR-SERVER-LOCATION/plugins/BetonQuest/QuestPackages/packageStructure/SingleFile_"

## 4. Creating packages in packages

It is also possible to have a package inside a package.
And it's pretty easy to do! Just look at the following structure:

<div class="grid" markdown>
!!! example "Example 1"
    * :material-folder-open: QuestPackages
        - :material-folder-open: woodCuttingQuest
            - :material-file-star: package.yml
            - {==:material-folder-open: miningQuest==}
                - {==:material-file-star: package.yml==}

!!! example "Example 2"
    * :material-folder-open: QuestPackages
        - :material-folder-open: woodCuttingQuest
            - :material-file-star: package.yml
              - {==:material-folder-open: miningQuest==}
                - {==:material-file-star: package.yml==}
                  - {==:material-folder-open: fishingQuest==}
                    - {==:material-file-star: package.yml==}
</div>

Not hard to understand but to make things clearer:
Every folder that contains a _package.yml_ file is a standalone quest package.
By creating a folder with a _package.yml_ file inside another package, you create a standalone "subpackage". 

--8<-- "Tutorials/download-complete-files.md"
    ```
    /bq download BetonQuest/Quest-Tutorials main QuestPackages /Syntax/packageStructure/3-PackageInPackage /packageStructure/PackageInPackage
    ```
    You can now find all files needed for this tutorial in this location:
    "_YOUR-SERVER-LOCATION/plugins/BetonQuest/QuestPackages/packageStructure/PackageInPackage_"

## 5. Creating subdirectories in a package

Now you know, that it is possible to do nearly everything to structure your quests,
But there is one more possibility: _subdirectories_.
Let me first show you the example for it:

!!! success "Example"
    * :material-folder-open: QuestPackages
        - :material-folder-open: woodCuttingQuest
            - :material-file: package.yml
            - {==:material-folder-open: questPart1==}
                - :material-file: myEventsPart1.yml
                - :material-file: MageConversation.yml
            - {==:material-folder-open: questPart2==}
                - :material-file: myEventsPart2.yml
                - :material-file: KingConversation.yml

You can create as many subdirectories in your quest packages as you like.
Just create a folder with a name of your choice and start adding files to it!

--8<-- "Tutorials/download-complete-files.md"
    ```
    /bq download BetonQuest/Quest-Tutorials main QuestPackages /Syntax/packageStructure/4-SubDirectory /packageStructure/SubDirectory
    ```
    You can now find all files needed for this tutorial in this location:
    "_YOUR-SERVER-LOCATION/plugins/BetonQuest/QuestPackages/packageStructure/SubDirectory_"


## Summary

In this tutorial about quest packages tutorial you've learned how to efficiently organise your quests.
 Let's sum it up: 

* Quest packages are defined by a _package.yml_ file.
* You can create a quest package by creating a folder and adding a _package.yml_ file to it.
* Files inside quest packages (apart from the _package.yml_) do not follow any naming conventions.
* You can create a subpackage by creating a folder inside a quest package and adding a _package.yml_ file to it.
---
