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
    If you are using an old version of BetonQuest and just want to convert your old packages to the new format, use
    the [Migration Guide](/Documentation/Migration.md).

<div class="grid" markdown>
!!! danger "Requirements"
    No requirements.

!!! example "Related Docs"
    * [Structure Reference](../../../Documentation/Reference.md#structure)
    * [Defining Features Reference](../../../Documentation/Reference.md#defining-features)
</div>

## 1.General explanation of QuestPackages

This part of the tutorial will teach you how the new QuestPackages work and what the difference are compared to
the old system.
With this QuestPackages system it's now possible to have as many files as you want. You can even name them
what ever you want, so there are no more limitations to naming and amount of files. What a great thing!

Still not know what I mean? Let me show you the difference:

<div class="grid" markdown>
!!! success "new multifile structure"
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

!!! warning "old concept"
    * :material-folder-open: tutorialQuest
        - :material-file: main.yml
        - :material-file: events.yml
        - :material-file: objectives.yml
        - :material-file: conditions.yml
        - :material-folder-open: conversations
            - :material-file: indiana.yml
            - :material-file: jones.yml
</div>

Did you see the difference? In the old one, there was no space for unique naming of the files not even have more than one file
for each feature.

One more thing to know when using the new system: there is no _main.yml_ needed anymore. Instead of main
we will use the _package.yml_. The _package.yml_ is necessary otherwise it won't get detected as a quest and will not
be affected in a BetonQuest reload.

## 2. Creating your first QuestPackage with multiple files

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
          '1': "Jones"
          
        {==items:==}
          oakLogs: "minecraft:oak_log"
          jewelry: "minecraft:diamonds"
        ```
    === "jones.yml"

        ``` markdown
        {==conversations:==}
          Jones:
            quester: "Jones"
            first: "questAlreadyDone,questDone,questNotDone,firstGreeting"
            NPC_options:
              firstGreeting: 
                text: "Yoo! You look like you can handle those heavy axes to cut down some trees..?"
                pointer: "probably"
              woodAmountAnswer:
                text: "Bring me 20 logs of oak and you will get my special axe for woodcutting!"
                pointer: "letsDoIt"
              seeYou:
                text: "See you soon!"
              questNotDone:
                text: "Oh you still need some time for the mission?.."
                conditions: "!woodcuttingDoneTag,!logsInInventory"
              questDone:
                text: "That's the wood I was looking for! Thank you so much! Here is my special axe for my special friend."
                events: "questDone"
                conditions: "woodcuttingDoneTag,logsInInventory"
              questAlreadyDone:
                text: "Hey! I dont need you anymore. Thanks again for the help."
                conditions: "questDoneTag"
            player_options:
              probably: 
                text: "Yes I can do that for you! How much wood do you need?"
                pointer: "woodAmountAnswer"
              letsDoIt:
                text: "Alright let's get the job done!"
                pointer: "seeYou"
                events: "questAccepted"
        ```

    === "myEventsList1.yml"

        ``` markdown
        {==events:==}
          questStarted: "folder startedTagAdd,addWoodcuttingObj"
          startedTag: "tag add startedTag"
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
          woodCuttingObj: "block oakLog -10 notify events:addWoodcuttingDoneTag"
        ```
        
    === "importantConditions.yml"

        ``` markdown
        {==conditions:==}
          woodcuttingDoneTag: "tag woodcuttingDoneTag"
          logsInInventory: "item oakLogs:10"
          questDoneTag: "tag questDoneTag"
        ```
        
As you can see: Every feature goes into a section like `events:`, `objectives:`, `conversations:` marked in {==blue==}
int the example quest.
You can write these sections in any file you want, and it will still work! That's the way you can organize 
yourself and your quests.

It's not necessary to download this quest files, but it is quite useful because it's way easier to understand
if you are doing things on your own and looking it up in the editor.

Now that you understand how the new _multifile_ system works we will give you another example with the exact same quest:

!!! Example

    === "package.yml"

        ``` markdown
        {==npcs:==}
          '1': "Jones"
          
        {==items:==}
          oakLogs: "minecraft:oak_log"
          jewelry: "minecraft:diamonds"

        {==conversations:==}
          Jones:
            quester: "Jones"
            first: "questAlreadyDone,questDone,questNotDone,firstGreeting"
            NPC_options:
              firstGreeting: 
                text: "Yoo! You look like you can handle those heavy axes to cut down some trees..?"
                pointer: "probably"
              woodAmountAnswer:
                text: "Bring me 20 logs of oak and you will get my special axe for woodcutting!"
                pointer: "letsDoIt"
              seeYou:
                text: "See you soon!"
              questNotDone:
                text: "Oh you still need some time for the mission?.."
                conditions: "!woodcuttingDoneTag,!logsInInventory"
              questDone:
                text: "That's the wood I was looking for! Thank you so much! Here is my special axe for my special friend."
                events: "questDone"
                conditions: "woodcuttingDoneTag,logsInInventory"
              questAlreadyDone:
                text: "Hey! I dont need you anymore. Thanks again for the help."
                conditions: "questDoneTag"
            player_options:
              probably: 
                text: "Yes I can do that for you! How much wood do you need?"
                pointer: "woodAmountAnswer"
              letsDoIt:
                text: "Alright let's get the job done!"
                pointer: "seeYou"
                events: "questAccepted"

        {==events:==}
          questStarted: "folder startedTagAdd,addWoodcuttingObj"
          startedTag: "tag add startedTag"
          addWoodcuttingObj: "objective add woodCuttingObj"

          questDone: "folder takeWoodFromPlayer,rewardPlayer,addQuestDoneTag"
          takeWoodFromPlayer: "take oakLog:10"
          rewardPlayer: "give jewelry:2"
          addQuestDoneTag: "tag add questDoneTag"
          
          addWoodcuttingDoneTag: "tag add woodcuttingDoneTag"

        {==objectives:==}
          woodCuttingObj: "block oakLog -10 notify events:addWoodcuttingDoneTag"

        {==conditions:==}
          woodcuttingDoneTag: "tag woodcuttingDoneTag"
          logsInInventory: "item oakLogs:10"
          questDoneTag: "tag questDoneTag"
        ```

--8<-- "Tutorials/download-setup-warning.md"
    ```
    !!!!!!!!!!!!!!!!!/bq download BetonQuest/Quest-Tutorials main QuestPackages /Basics/Conditions/1-DirectoryStructure /tutorialQuest
    ```
    You can now find all files needed for this tutorial in this location:
    "_YOUR-SERVER-LOCATION/plugins/BetonQuest/QuestPackages/tutorialQuest_"
    
small example quest inside. maybe a material grid here?

But why is it working? Keywords Objectives: etc

Its also possible to write it in one file:

--8<-- "Tutorials/download-setup-warning.md"
    ```
    /bq download BetonQuest/Quest-Tutorials main QuestPackages /Basics/Conditions/1-DirectoryStructure /tutorialQuest
    ```
    You can now find all files needed for this tutorial in this location:
    "_YOUR-SERVER-LOCATION/plugins/BetonQuest/QuestPackages/tutorialQuest_"
