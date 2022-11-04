---
icon: octicons/file-directory-16
tags:
  - package structure
  - QuestPackages
---

This part of the tutorial will teach you about the new designed package managing system, which was implemented
to easily handle a bunch of files or just one big file.
The advantage is that you can have your objectives, events, conditions and conversations where ever you want.
You can write a small quest in just one file all together or split a big quest into lots of small files. It's completely
up to you! This split is called _sections_.

!!! warning "Attention"
    If you are using an older version of BetonQuest and just want to convert your 1.12.9 packages to the new format(2.0.0), 
    use the [Migration Guide](/Documentation/Migration.md).

<div class="grid" markdown>
!!! danger "Requirements"
    No requirements.

!!! example "Related Docs"
    * [Structure Reference](../../../Documentation/Reference.md#structure)
    * [Defining Features Reference](../../../Documentation/Reference.md#defining-features)
</div>

## 1.General explanation of QuestPackages

This part of the tutorial will teach you how the new QuestPackages work and what the difference are compared to
the 1.12.9 version.
With this QuestPackages system it's now possible to have as many files as you want. You can even name them
what ever you want, so there are no more limitations to naming and amount of files. What a great thing!

Still not know what I mean? Let me show you the difference:

<div class="grid" markdown>
!!! success "BetonQuest 2.0.0 conzept"
    * :material-folder-open: QuestPackages
        - :material-folder-open: myExampleQuest
          - :material-file: package.yml
          - :material-file: myEventsList1.yml
          - :material-file: myEventsList2.yml
          - :material-file: importantConditions.yml
          - :material-file: dungeonObjectives.yml
          - :material-file: normalObjectives.yml
          - :material-file: myVariablesFile.yml
            - :material-folder-open: conversations
                - :material-file: indiana.yml
                - :material-file: jones.yml

!!! warning "BetonQuest 1.12.9 concept"
    * :material-folder-open: tutorialQuest
        - :material-file: main.yml
        - :material-file: events.yml
        - :material-file: objectives.yml
        - :material-file: conditions.yml
        - :material-file: custom.yml
        - :material-folder-open: conversations
            - :material-file: indiana.yml
            - :material-file: jones.yml
</div>

Did you see the difference? In the 1.12.9 one, there was no space for unique naming of the files not even having more than one file
for each feature and in the 2.0.0 version nearly everything is possible.

One more thing to know when using the new system: there is no _main.yml_ needed anymore. Instead of main
we will use the _package.yml_. The _package.yml_ is necessary otherwise it won't get detected as a quest and will not
be affected in a BetonQuest reload.

## 2. Creating a QuestPackage with multiple files

We will now explain you, how to actually get used to this system. After this you will know how to create
your own _QuestPackages_ and how to let them grow!

We will begin as usual with the file structure and some stuff to it afterwards.
Create your own structure in the _QuestPackages_ folder. I am using the example from above:

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
The example will be a small and simple woodcutting quest with a reward on completion: 

!!! Example

    === "package.yml"

        ``` markdown
        {==npcs:==}
          '0': "Jones"
          
        {==items:==}
          oakLog: "minecraft:oak_log"
          jewelry: "minecraft:diamond"
        ```
    === "jones.yml"

        ``` markdown
        {==conversations:==}
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

        ``` markdown
        {==events:==}
          questStarted: "folder startedTagAdd,addWoodcuttingObj"
          startedTagAdd: "tag add startedTag"
          addWoodcuttingObj: "objective add woodCuttingObj"
        ```
        
    === "myEventsList2.yml"

        ``` markdown
        {==events:==}
          questDone: "folder takeWoodFromPlayer,rewardPlayer,addQuestDoneTag"
          takeWoodFromPlayer: "take oakLog:10"
          rewardPlayer: "give jewelry:2"
          addQuestDoneTag: "tag add questDoneTag"
          
          addWoodcuttingDoneTag: "tag add woodcuttingDoneTag"
        ```
        
    === "myAwesomeObjectives.yml"
    
        ``` markdown
        {==objectives:==}
          woodCuttingObj: "block OAK_LOG -10 notify events:addWoodcuttingDoneTag"
        ```
        
    === "importantConditions.yml"

        ``` markdown
        {==conditions:==}
          woodcuttingDoneTag: "tag woodcuttingDoneTag"
          logsInInventory: "item oakLog:10"
          questDoneTag: "tag questDoneTag"
          startedTag: "tag startedTag"
        ```
        
As you can see: Every feature goes into a section like `events:`, `objectives:`, `conversations:` marked in {==blue==}
in the example quest.
You can write these sections in any file you want, and it will still work! That's the way you can organize 
yourself and your quests.

It's not necessary to download this quest files, but it is quite useful because it's way easier to understand
if you are doing things on your own and looking it up in the editor.

--8<-- "Tutorials/download-complete-files.md"
    ```
    /bq download BetonQuest/Quest-Tutorials main QuestPackages /Basics/packageStructure/1-MultiFileStructure /packageStructure/MultiFile
    ```
    You can now find all files needed for this tutorial in this location:
    "_YOUR-SERVER-LOCATION/plugins/BetonQuest/QuestPackages/packageStructure_"

## 3. Creating a QuestPackage with a single file

Now that you understand how the new _multifile_ system works we will give you another example with the exact same quest
but just one file:

!!! Example

    === "package.yml"

        ``` markdown
        {==npcs:==}
          '0': "Jones"
          
        {==items:==}
          oakLog: "minecraft:oak_log"
          jewelry: "minecraft:diamond"

        {==conversations:==}
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

        {==events:==}
          questStarted: "folder startedTagAdd,addWoodcuttingObj"
          startedTagAdd: "tag add startedTag"
          addWoodcuttingObj: "objective add woodCuttingObj"

          questDone: "folder takeWoodFromPlayer,rewardPlayer,addQuestDoneTag"
          takeWoodFromPlayer: "take oakLog:10"
          rewardPlayer: "give jewelry:2"
          addQuestDoneTag: "tag add questDoneTag"
          
          addWoodcuttingDoneTag: "tag add woodcuttingDoneTag"

        {==objectives:==}
          woodCuttingObj: "block OAK_LOG -10 notify events:addWoodcuttingDoneTag"

        {==conditions:==}
          woodcuttingDoneTag: "tag woodcuttingDoneTag"
          logsInInventory: "item oakLog:10"
          questDoneTag: "tag questDoneTag"
          startedTag: "tag startedTag"
        ```

You can download the example below as well, so you can see the difference in your editor. Now worries, the files
not get overwritten.

--8<-- "Tutorials/download-complete-files.md"
    ```
    /bq download BetonQuest/Quest-Tutorials main QuestPackages /Basics/packageStructure/2-SingleFileStructure /packageStructure/SingleFile
    ```
    You can now find all files needed for this tutorial in this location:
    "_YOUR-SERVER-LOCATION/plugins/BetonQuest/QuestPackages/packageStructure_"

## 4. Creating packages in packages

As the title already explains: Yes it is possible to have a package in a package.
And it's really that easy like it sounds. Just look at the following structure:

<div class="grid" markdown>
!!! success "Example"
    * :material-folder-open: QuestPackages
        - :material-folder-open: woodCuttingQuest
            - :material-file: package.yml
            - {==:material-folder-open: miningQuest==}
                - {==:material-file: package.yml==}

!!! success "Example"
    * :material-folder-open: QuestPackages
        - :material-folder-open: woodCuttingQuest
            - :material-file: package.yml
              - {==:material-folder-open: miningQuest==}
                - {==:material-file: package.yml==}
                  - {==:material-folder-open: fishingQuest==}
                    - {==:material-file: package.yml==}
</div>

Not hard to understand but to make things clearer:
Every folder that has a _package.yml_ inside, is a completely standalone quest.
If you create another folder in an existing quest folder with a new created _package.yml_
it will run as a new quest and is no longer part of the other package.

--8<-- "Tutorials/download-complete-files.md"
    ```
    /bq download BetonQuest/Quest-Tutorials main QuestPackages /Basics/packageStructure/3-PackageInPackage /packageStructure/PackageInPackage
    ```
    You can now find all files needed for this tutorial in this location:
    "_YOUR-SERVER-LOCATION/plugins/BetonQuest/QuestPackages/packageStructure_"

## 5. Creating subdirectories in a package

Now you know, that it is possible to do nearly everything to structure your quests,
I'll show you one more possibility: _subdirectories_.
Let me first show you the example for it:

!!! success "Example"
    * :material-folder-open: QuestPackages
        - :material-folder-open: woodCuttingQuest
            - :material-file: package.yml
            - {==:material-folder-open: questPart1==}
                - :material-file: myEventsPart1.yml
                    - {==:material-folder-open: questPart2==}
                        - :material-file: myEventsPart2.yml

If you ever wanted to split quests into parts with folders, it's possible.
You only need to create the folder with the name of your choice and create a new file
inside. If you use the specific _section keywords_ inside the specific files, there
are no more rules.

--8<-- "Tutorials/download-complete-files.md"
    ```
    /bq download BetonQuest/Quest-Tutorials main QuestPackages /Basics/packageStructure/4-SubDirectory /packageStructure/SubDirectory
    ```
    You can now find all files needed for this tutorial in this location:
    "_YOUR-SERVER-LOCATION/plugins/BetonQuest/QuestPackages/packageStructure_"


## Summary

In this QuestPackages tutorial you've learned how you can better organise your quests
and QuestPackages. Further you've learned the different possibilities on how to create
the perfect structure for your next quest!
---
