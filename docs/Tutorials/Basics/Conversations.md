---
icon: material/message-processing
---

## About
:octicons-clock-24:  30 minutes · :octicons-tag-16: Basics

In this tutorial you will learn the basics of the conversations. These allow you to create a dialog between the player
and a NPC. Therefore, these are the basic tool for story telling.


!!! danger "Requirements"
    For this part of the tutorial you have to go through the [Setup Guide](../Getting-Started/Setting-up-a-local-test-server.md).
    After you finished setting up your local server, editor and installed the plugin you can start with this tutorial.

    You don't need any experience in creating quest. This is the very beginning.

## 1. Creating the folder structure for the first quest

Let's start by creating a folder structure for this tutorial. 
All files related to quests must be placed inside the :material-folder-home: *QuestPackages* directory, which is
automatically created by the plugin.

Please create folders and files so your file structure looks like this:

* :material-folder-home: QuestPackages *(already there)*
    - :material-folder-open: tutorialQuest
        - :material-file: package.yml
          - :material-folder-open: conversations
              - :material-file: jack.yml  
    

This means that the _QuestPackages_ folder must contain a folder called _tutorialQuest_. This folder contains a file named _package.yml_
and another directory called _conversations_. The _conversations_ directory must contain another file named _jack.yml_.

Now that we have the folder and file structure we can create our first conversation.
First we need to create the `npcs` section in the _package.yml_ so that the plugin knows which Citizens NPC
we want to talk with. This is how it works:

``` YAML title="package.yml" linenums="1"
npcs:
  '1': Jack
```
This links the NPC with the given ID to the conversation with the given ID.
Save the file after editing.

??? info "How to find the Citizens NPC ID"
    
    1. Stay close to the NPC you want to select
    2. Type the command `/npc select` to select the nearby NPC
    3. Type the command `/npc id` to get the ID from your NPC


## 2. Creating your first conversation

It's time to create the first conversation with the NPC! The goal is to learn how the basics works and to
understand how pointers are working.

Open the file `jack.yml` in the `conversations` folder. Now we will start creating the first conversation.
The goal is that we can talk to the created NPC and have the first small conversation with him.
We start off with that:

``` YAML title="jack.yml" linenums="1"
conversations:
  Jack: # (1)!
    quester: Jack # (2)!
    first: firstGreeting # (3)!
    NPC_options: # (4)!
      firstGreeting:
        text: Hello and welcome to my town traveler. Nice to see you. But first where are you from?
        pointer: whereYouFrom # (5)!
    player_options: # (6)!
      whereYouFrom:
        text: First I want to know who you are!
```

1. `Jack:` defines the ID of the conversation. Make sure this equals the conversation name in `package.yml` 
2. `quester:` defines the name of the NPC in the conversation.
3. `first:` defines what conversation should be played first. Also defines the order when you have more than one.
4. `NPC_options:` this is the section for what the NPC says.
5. `pointer:` defines which conversation option plays next. If multiple are defined than the player can choose which one plays next.
6. `player_options:` this is the section for what the player can say.

We've created a `conversations` section above and started with creating the `Jack` conversation.
When you create a pointer like `whereYouFrom` then you have to create it in `player_options` as well as the example shows.
If no first argument is defined nothing would happen if you try to talk to the NPC. Dont forget to save and test your
work! You can easily check if your quest is working on the server. Just type `/q reload` in chat and right click
the NPC.

!!! info "The Conversation Cycle"
    ``` mermaid
    graph LR
    C[Initial NPC_option] --> A
    A[player_options] --> |Pointer must point to|B[NPC_options];
    B --> |Pointer must point to|A;
    A -.Missing pointer .-> D
    B -.Missing pointer .-> D
    D{Conversation Ends}
    ```

## 3. Conversations with multiple choice

It is also possible to have multiple answers for a player when a NPC asks something. This is very helpful for
creating more advanced quests with different endings. In this step we will have a closer look at the `pointer`
argument to see what it could look like.

``` YAMl title="jack.yml" hl_lines="9-15 19-25" linenums="1"
conversations:
  Jack:
    quester: Jack
    first: firstGreeting
    NPC_options:
      firstGreeting:
        text: Hello and welcome to my town traveler. Nice to see you. But first where are you from?
        pointer: whereYouFrom # (1)!
      whoAmI:
        text: I am &6Jack&r. The mayor of this beautiful town here. We have some big farms and good old taverns and these are well worth checking out! So now where are you from?
        pointer: smallIsland,bigCity  # (3)!
      islandAnswer: # (8)!
        text: Thats sounds familiar to me because I also grow up in a small town with few people. So we already have a good connection! And because of that I want to give you some food!
      cityAnswer: # (9)!
        text: Oh I know! I think you're from Kayra, right? Nice city to be honest but I prefer country life. However... You look a bit hungry do you want some food from the best chef out here?
    player_options:
      whereYouFrom:  # (2)!
        text: First I want to know who you are!
        pointer: whoAmI # (6)!
      smallIsland:  # (4)!
        text: From a small island located east.
        pointer: islandAnswer # (7)!
      bigCity:  # (5)!
        text: From a big city located west.
        pointer: cityAnswer
```

1. This is a pointer called `whereYouFrom` that points to the `whereYouFrom` text bracket in the `player_options` section.
2. A player option that can be said when the NPC asks `whereYouFrom`.
3. Two possibilities to answer the question. Multiple answers can be separated by a comma. Both pointers point to the different answers.
4. I get pointed from the `whoAmI` text from the `NPC_options`.
5. I get pointed too from the `whoAmI` text from the `NPC_options`.
6. This is an option that a player can say to ask where the NPC is from. This points to `whoAmI` in the `NPC_options` section.
7. `pointer: islandAnswer` points to the `islandAnswer` in the `NPC_options` section.
8. I get pointed from the `smallIsland` answer in the `player_options` section.
9. I get pointed from the `bigCity` answer in the `player_options` section.

Now we have two choices to answer the question. The player can either say that he is from a `smallIsland` or from a
`bigCity`. If no `pointer` is defined for the current option then the conversation will end.

Now that the player has two choices to answer we want to go from the split conversation to just one again.
This is very simple! Just define another pointer for that but instead of two different pointers you have to
create just ONE and use it in both parts like this:
``` YAML title="jack.yml" hl_lines="14 17-19 30-32" linenums="1" 
conversations:
  Jack:
    quester: Jack
    first: firstGreeting
    NPC_options:
      firstGreeting:
        text: Hello and welcome to my town traveler. Nice to see you. But first where are you from?
        pointer: whereYouFrom
      whoAmI:
        text: I am &6Jack&r. The mayor of this beautiful town here. We have some big farms and good old taverns and these are well worth checking out! So now where are you from?
        pointer: smallIsland,bigCity
      islandAnswer:
        text: Thats sounds familiar to me because I also grow up in a small town with few people. So we already have a good connection! And because of that I want to give you some food!
        pointer: yesPlease # (1)!
      cityAnswer:
        text: Oh I know! I think you're from Kayra, right? Nice city to be honest but I prefer country life. However... You look a bit hungry do you want some food from the best chef out here?
        pointer: yesPlease # (2)!
      foodAnswer:
        text: Your welcome! Take it... &7*gives food*
    player_options:
      whereYouFrom:
        text: First I want to know who you are!
        pointer: whoAmI
      smallIsland:
        text: From a small island located east.
        pointer: islandAnswer
      bigCity:
        text: From a big city located west.
        pointer: cityAnswer
      yesPlease: # (3)!
        text: Oh yes I am starving! Thank you.
        pointer: foodAnswer
```

1. I am pointing to `yesPlease` in the `player_options` section.
2. I am pointing as well to `yesPlease` in the `player_options` section.
3. I get pointed from two texts in the `NPC_options` section.

!!! info "Conversation Flow Graph"
    ``` mermaid
    stateDiagram-v2
        [*] --> firstGreeting: Interaction with NPC
        firstGreeting --> whereYouFrom: points to
        whereYouFrom --> whoAmI: points to
        whoAmI --> smallIsland: points to
        whoAmI --> bigCity: points to
        smallIsland --> islandAnswer: points to
        bigCity --> cityAnswer: points to
        islandAnswer --> yesPlease: points to
        cityAnswer --> yesPlease: points to
    ```

--8<-- "Tutorials/download.md"
    `Download Link`

Now that the conversations `smallIsland` and `bigCity` pointing to the same text `yesPlease` in the `NPC_options` section we are good to go
and finished your first conversation with multiple answers.

## Further Information
!!! info ""
    You want some more information about conversations? [Conversation Reference](/Documentation/Conversations/)

## Summary

!!! abstract ""
    You've learned the basics to create simple conversations and split them into different answers. You can even let 
    conversations join together again and understanding how the system works.  
    In the next part of the basics tutorial you will learn how to give the food to the player with **events**!



## What`s next?
[Events Tutorial :octicons-arrow-right-16:](#){ .md-button }
