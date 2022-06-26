---
icon: material/message-processing
---

## About
:octicons-clock-24:  30 minutes · :octicons-tag-16: Basics

In this tutorial you will learn the basics of the conversations. These allow you to create a dialog between the player
and a NPC. Therefore, these are the basic tool for story telling.


!!! danger "Requirements"
    Setup of:
    
    * [Setup Guide](../Setup-Guide/Setting-up-a-test-server.md)

    You don't need any experience in creating quests. This is the very beginning.

## 1. Package Setup

--8<-- "Tutorials/download-setup.md"
    ```
    /bq download BetonQuest/Quest-Tutorials main QuestPackages /Basics/Conversations/1-DirectoryStructure /tutorialQuest overwrite
    ```

You can now find all files needed for this tutorial in this location:
"_YOUR-SERVER-LOCATION/plugins/BetonQuest/QuestPackages/tutorialQuest_"    

## 2. Linking a conversation to a NPC

Usually, conversations happen between a NPC and the player. 
Therefore, we need to create the `npcs` section in the _package.yml_ so that the plugin knows which Citizens NPC
uses which conversation. This is how it works:

``` YAML title="package.yml" linenums="1"
npcs:
  '1': Jack
```
This links the NPC with the given ID (`1`) to the conversation with the given identifier (`Jack`).
Save the file after editing.

??? info "How to find the Citizens NPC ID"
    Execute this command if you haven't created an NPC yet: `/npc create Jack`  
    
    1. Stay close to the NPC you want to select
    2. Type the command `/npc select` to select the nearest NPC.
    3. Type the command `/npc id` to get the ID from your NPC.


## 3. Creating your first conversation

It's time to create the first conversation with Jack! This chapter will teach you the basic structure of a conversation.

Let's take a look at how a conversation is defined in the plugin's files:

!!! question ""
    **Tip:** Click the plus buttons next to the text for explanations! 

``` YAML title="jack.yml" linenums="1"
conversations:
  Jack: # (1)!
    quester: Jack # (2)!
    first: firstGreeting # (3)!
    NPC_options: # (4)!
      firstGreeting:
        text: Hello and welcome to my town traveler! Nice to see you. Where are you from?
        pointer: whereYouFrom # (5)!
   
    player_options: # (6)!
      whereYouFrom:
        text: First I want to know who you are!
```

1. This is the identifier of the conversation. Make sure this equals the conversation identifier in "_package.yml_". 
2. Defines the name of the NPC that displayed during the conversation.
3. Defines which `NPC_option` should be used as the start of the conversation.
4. This section contains everything the NPC says.
5. Defines which `player_option` is shown next. 
6. This section contains everything the player says.


A BetonQuest conversation is a cycle of responses between the NPC and the player.
Anything the NPC says is called `NPC_options`, all player text is called `player_options`.

A conversation always starts with an `NPC_option`. 
Now the player must answer the NPC using a `player_option`.

Options point to each other using the `pointer` argument. In the case of an NPC_option, the pointer argument would contain
the name of a `player_option`. 
Usually, a player has more than one answer to choose from. This is done by adding multiple `player_option` names to a
`NPC_option`.

After the player responded, they are shown another `NPC_option` that the previously chosen `player_option` points to.
  
Whenever either a `player_option` or a `NPC_option` point to no other option the conversation ends as there are no more
responses or answers.

!!! info "The Conversation Cycle"
    ``` mermaid
    graph LR
    X{Conversation Starts} --> C
    C[First NPC_option] --> A
    A[player_option] --> |Pointer|B[NPC_option];
    B --> |Pointer|A;
    A -.No pointer present .-> D
    B -.No pointer present .-> D
    D{Conversation Ends}
    ```
    
You can easily check if your quest is working on the server.
Open the file "_jack.yml_" in the "_conversations_" folder.
Copy the above conversation into it and save the file.
Now type `/bq reload` in the chat and right-click the NPC.

You can select the answer by pressing ++space++.

## 4. Conversations with multiple choices

Let's see how to create multiple responses for the player to choose from using the `pointer` argument.

A NPC_option can point to multiple player options at the same time.
As soon as a pointer argument contains more than one player option, the player can choose.

!!! question ""
    **Tip:** Highlighted lines are new compared with the previous example. 

``` YAMl title="jack.yml" hl_lines="9-15 19-25" linenums="1"
conversations:
  Jack:
    quester: Jack
    first: firstGreeting
    NPC_options:
      firstGreeting:
        text: Hello and welcome to my town traveler! Nice to see you. Where are you from?
        pointer: whereYouFrom
      whoAmI:
        text: I am &6Jack&r. The mayor of this beautiful town. We have some big farms and good old taverns well worth checking out! So now where are you from?
        pointer: smallIsland,bigCity # (1)!
      islandAnswer: 
        text: That sounds familiar! I grew up in a small town with few people. So we already have something in common! Do you want something to eat?
      cityAnswer: 
        text: Oh I know! I think you're from Kayra, right? Nice city but to be honest I prefer country life... You look a bit hungry. Do you want something to eat?
    
    player_options:
      whereYouFrom: 
        text: First I want to know who you are!
        pointer: whoAmI 
      smallIsland: # (2)!
        text: From a small island located east.
        pointer: islandAnswer # (4)!
      bigCity:  # (3)!
        text: From a big city located west.
        pointer: cityAnswer # (5)!
```

1. This npc_option points to multiple player_options. This allows the player to choose. The names of the player_options must be comma seperated.
2. The `whoAmI` npc_option points to me.
3. Gets pointed on by the `whoAmI` npc_option.
4. Points to `islandAnswer` `NPC_option`.
5. Points to the `cityAnswer` `NPC_option`.


With these changes, the mayor asks the player where he is from.
The player can either say that they are from a `smallIsland` or from a
`bigCity`. This creates two different paths through the conversation. 

Let's join these paths again to show the same ending:<br>
Add the same pointer argument to both paths' NPC_options. They point to the new `yesPlease` player_option.
``` YAML title="jack.yml" hl_lines="14 17-19 30-32" linenums="1" 
conversations:
  Jack:
    quester: Jack
    first: firstGreeting
    NPC_options:
      firstGreeting:
        text: Hello and welcome to my town traveler! Nice to see you. Where are you from?
        pointer: whereYouFrom
      whoAmI:
        text: I am &6Jack&r. The mayor of this beautiful town. We have some big farms and good old taverns well worth checking out! So now where are you from?
        pointer: smallIsland,bigCity
      islandAnswer:
        text: That sounds familiar! I grew up in a small town with few people. So we already have something in common! Do you want something to eat?
        pointer: yesPlease # (1)!
      cityAnswer:
        text: Oh I know! I think you're from Kayra, right? Nice city but to be honest I prefer country life... You look a bit hungry. Do you want something to eat?
        pointer: yesPlease # (2)!
      foodAnswer:
        text: You're welcome! Take it... &7*gives food*
    
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
        text: Oh yes I'm starving! Thank you.
        pointer: foodAnswer
```

1. I point to `yesPlease` in the `player_options` section.
2. I also point to `yesPlease` in the `player_options` section.
3. Two `NPC_options` point to me.

The following graph shows the paths through the conversation. Since there are two pointers assigned to the `whoAmI` option,
the player can choose between one of the paths.

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

Try the conversation ingame by saving the file and executing the `/bq reload` command!
Then right-click Jack.
Select different options using ++w++ and ++s++. Confirm them using ++space++.


--8<-- "Tutorials/download-broken.md"
    ```
    /q download BetonQuest/Quest-Tutorials main QuestPackages /Basics/Conversations/2-FullExample /tutorialQuest overwrite
    ```

## Further Information
!!! info ""
    More information about conversations can be found in the [Conversation Reference](../../../Documentation/Conversations.md)

## Summary

!!! abstract ""
    You've learned how to create simple conversations in which the player can choose different paths.
    In the next part of the basics tutorial you will learn how Jack the mayor can give food to the player using **events**!

## What`s next?
[:octicons-arrow-right-16: Events Tutorial ](Events.md){ .md-button .md-button--primary}
