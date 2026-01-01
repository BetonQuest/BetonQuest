---
icon: material/folder-open
tags:
  - QuestPackages
---

# :material-folder-open: Quest Packages

This tutorial will teach you about the package managing system, which helps you keep your quests organized.
You can write a small quest in just one file or split a big quest into lots of small files. It's completely
up to you! 

<div class="grid" markdown>
!!! danger "Requirements"
    Doing this tutorial helps but is not strictly required:
    
    * [Basic Tutorial](../Getting-Started/About.md)

!!! example "Related Docs"
    * [Package Structure Reference](../../Documentation/Scripting/Packages-&-Templates.md#structure)
    * [Defining Features Reference](../../Documentation/Scripting/Packages-&-Templates.md#defining-features)
</div>

## 1. What is a Quest Package?

A quest package is a folder that contains all the files that belong to a "quest". Since BetonQuest doesn't have its own 
definition of a quest, a quest package could technically also contain multiple quests. That is up to you.

It is created by placing a _package.yml_ file inside a folder.
If a folder lacks the _package.yml_, it will be considered as a part of another package that is located in a folder
above it.

The files inside a quest package can be organized in any way you like. There are no limitations on file names or file count.
The only thing that matters is that the _package.yml_ file is present. 
 
Let's compare the structure of the basic tutorial to one of a realistic quest package: 

<div class="grid" markdown>
!!! example annotate "Basics Tutorial Structure"
    * :material-folder-open: tutorialQuest
        - :material-file-star: package.yml (1)
        - :material-file: events.yml  (2)
        - :material-file: conditions.yml
        - :material-file: objectives.yml
        - :material-folder-open: conversations
            - :material-file: blacksmith.yml
            - :material-file: jack.yml

1. The package.yml file is required to make the folder a quest package.
2. We created only one file per content type to make the basics tutorial easier. In a real quest package you would
   probably have multiple files per content type.

!!! example annotate "Typical Quest Structure"
    - :material-folder-open: myExampleQuest
        - :material-file-star: package.yml (1)
        - :material-file: myEventsList1.yml (2)
        - :material-file: myEventsList2.yml (3)
        - :material-file: importantConditions.yml
        - :material-file: normalObjectives.yml (4)
        - :material-file: dungeonObjectives.yml
        - :material-file: myConstantsFile.yml
        - :material-folder-open: conversations (5)
             - :material-file: indiana.yml
             - :material-file: jones.yml
             
1. The package.yml file is required to make a folder a quest package. In this case the package is called `myExampleQuest`
   because the folder it is located in is called `myExampleQuest`.
2. Multiple files with the same content type are possible and typically for bigger quests.
3. Multiple files with the same content type are possible and typically for bigger quests.
4. The files can have ANY name you want as the content type is defined in the file itself. How that works is explained
   later in this tutorial.
5. Since this folder does not contain a _package.yml_, it will be considered as a part of the quest package.
 
</div>

## 2. Creating a Quest Package with multiple files

Download this example quest and take a look at the following structure overview and explanation.
It's a simple woodcutting quest with a reward upon completion.
Then play around with this system to get a feel for it.

@snippet:tutorials:download-complete-files@
    ```
    /bq download BetonQuest/Quest-Tutorials ${ref} QuestPackages /Syntax/packageStructure/1-MultiFileStructure /packageStructure/MultiFile
    ```
    You can now find all files needed for this tutorial in this location:
    "_YOUR-SERVER-LOCATION/plugins/BetonQuest/QuestPackages/packageStructure/MultiFile_"

!!! example "File Structure"
    * :material-folder-open: QuestPackages
        - :material-folder-open: myExampleQuest
            - :material-file: package.yml
            - :material-file: myEventsList1.yml
            - :material-file: myEventsList2.yml
            - :material-file: importantConditions.yml
            - :material-file: myAwesomeObjectives.yml
            - :material-file: myConstantsFile.yml
            - :material-folder-open: conversations
                - :material-file: jones.yml
 
!!! example "File Contents"

    === "package.yml"
        ```YAML
        {==npcs==}:
          JonesNpc: "citizens 0"
          
        {==npc_conversations==}:
          JonesNpc: "Jones"
          
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
                pointers: "probably"
              woodAmountAnswer:
                text: "Bring me 20 logs of oak and you will get my special axe for woodcutting!"
                pointers: "letsDoIt"
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
                pointers: "woodAmountAnswer"
              letsDoIt:
                text: "Alright let's get the job done!"
                pointers: "seeYou"
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
BetonQuest uses these section names to understand the contents of a file.
You can write these sections in any file you want, and it will still work! That's the way you can organize your quests.

!!! note "A note about Sections"
    While you can have multiple sections in one file, you can't have multiple sections with the same name.
    For example, you can't have two `events:` sections in one file. If you do, the second one will overwrite the first one.
    You can however have `events:` sections in two different files. In this case, the events from both files will be loaded.
    
    You also cannot have two features (e.g. events) with the same name in one package, even if those are in different files.

## 3. Creating a Quest Package with a single file

Now that you understood how the _multifile_ system works we will try another example.
It's the exact same quest but in just one file:

!!! Example
     ```YAML title="package.yml"
     {==npcs==}:
       JonesNpc: "citizens 0"
       
     {==npc_conversations==}:
       JonesNpc: "Jones"
       
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
             pointers: "probably"
           woodAmountAnswer:
             text: "Bring me 20 logs of oak and you will get my special axe for woodcutting!"
             pointers: "letsDoIt"
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
             pointers: "woodAmountAnswer"
           letsDoIt:
             text: "Alright let's get the job done!"
             pointers: "seeYou"
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

@snippet:tutorials:download-complete-files@
    ```
    /bq download BetonQuest/Quest-Tutorials ${ref} QuestPackages /Syntax/packageStructure/2-SingleFileStructure /packageStructure/SingleFile
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

@snippet:tutorials:download-complete-files@
    ```
    /bq download BetonQuest/Quest-Tutorials ${ref} QuestPackages /Syntax/packageStructure/3-PackageInPackage /packageStructure/PackageInPackage
    ```
    You can now find all files needed for this tutorial in this location:
    "_YOUR-SERVER-LOCATION/plugins/BetonQuest/QuestPackages/packageStructure/PackageInPackage_"

## 5. Creating subdirectories in a package

Now you know, that it is possible to do nearly everything to structure your quests.
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
As long as you don't create a _package.yml_ file in a subdirectory, it will belong to the package defined further up 
in the directory tree.

!!! warning "Directory names with spaces"
    The only limitation for directory names are spaces. These cannot be used. 

@snippet:tutorials:download-complete-files@
    ```
    /bq download BetonQuest/Quest-Tutorials ${ref} QuestPackages /Syntax/packageStructure/4-SubDirectory /packageStructure/SubDirectory
    ```
    You can now find all files needed for this tutorial in this location:
    "_YOUR-SERVER-LOCATION/plugins/BetonQuest/QuestPackages/packageStructure/SubDirectory_"


## Summary

You have learned how to structure your quest packages. Now you are able to pick the best structure for your quests. 
