---
icon: octicons/tag-16
tags:
  - Quest-Progress
  - Tracking
  - Tags
---

Tags can be a great solution to add various "checkpoints" if you have small quests or mechanics. With 
tags and their conditions, you can give your quests the necessary polish so that players do not have to start over 
or repeat tasks. Tags can do much more, but in this tutorial, we will focus only on how to track quest progress 
using them.

<div class="grid" markdown>
!!! danger "Requirements"
    It is helpful to be familiar with the basics of conditions and understand the underlying principles
    in order to comprehend and apply this tutorial.
    
    * [Conditions Tutorial](../../../Tutorials/Getting-Started/Basics/Conditions.md)

!!! example "Related Docs"
    * [Conditions Reference](../../../Documentation/Scripting/About-Scripting.md#conditions)
    * [Condition Tag](../../../Documentation/Scripting/Building-Blocks/Conditions-List.md#tag-tag)
</div>

## 1. Creating the folder structure for the example quest

Add a new structure for the example quest in the `QuestPackage` folder. The name could be "_questTracking_" for example.

The file structure should look like this:

* :material-folder-open: questTracking
    - :material-file: package.yml
    - :material-file: events.yml
    - :material-file: conditions.yml
    - :material-folder-open: conversations
        - :material-file: joe.yml
        - :material-file: bonny.yml
        - :material-file: fren.yml

@snippet:tutorials:download-complete-files@
    ```
    /bq download BetonQuest/Quest-Tutorials ${ref} QuestPackages /Advanced/Tracking-Quest-Progress/1-ExampleQuest /trackingTutorial overwrite
    ```
    You can now find all files needed for this tutorial in this location:
    "_YOUR-SERVER-LOCATION/plugins/BetonQuest/QuestPackages/questTracking_"

After you have created all the necessary files, we will begin to fill our conversation files with some
small talk. If you already downloaded it with the download command above you can skip this part.

In this tutorial, we will create three NPCs that you have to talk to, and with tags, we will prevent you 
from talking to the same NPC repeatedly. We will also configure the second and third NPC so that you can only talk 
to them after you have introduced yourself to the first NPC.

=== "bonny.yml"

    ``` yaml linenums="1"
    conversations:
      Bonny:
        quester: "Bonny"
        first: "firstGreeting"
        NPC_options:
          firstGreeting:
            text: "Hey Stranger! You look new to me. Can you introduce yourself?"
            pointer: "introduce"
          niceToMeetYou:
            text: "Nice to meet you %player%. Please also introduce yourself to Joe and Fren and come back when 
            you've done it!"
        player_options:
          introduce:
            text: "I am %player%"
            pointer: "niceToMeetYou"
    ```

=== "joe.yml"

    ``` yaml linenums="1"
    conversations:
      Joe:
        quester: "Joe"
        first: "firstGreeting"
        NPC_options:
          firstGreeting:
            text: "Hey %player% Bonny already told me about you! Nice to have you here in our town."
    ```
    
=== "fren.yml"

    ``` yaml linenums="1"
    conversations:
      Fren:
        quester: "Fren"
        first: "firstGreeting"
        NPC_options:
          firstGreeting:
            text: "Hey %player%. Already know you because bonny told me about you and that you're new here!"
            pointer: "introduce"
          niceToMeetYou:
            text: "Nice to meet you tho! I really like new people in our town!"
        player_options:
          introduce:
            text: "That's correct!"
            pointer: "niceToMeetYou"
    ```

=== "package.yml"

    ``` yaml linenums="1"
    npcs:
      '1': "Joe"
      '2': "Bonny"
      '3': "Fren"
    ```
    
Now that we have our basic conversations we need to add tags to it otherwise you could talk to any NPC at any time
and we want to prevent that to get a nice feeling conversation with these NPCs.

## 2. Adding conditions to the conversations

We are now adding conditions to the conversations to prevent the player having the same conversation over and over 
again and to make sure that you can only talk to the Fren and Joe *after* you get the task to meet those.

First we add events to the correct part of the conversation where the tag should be added and in order to that we 
will add this events with the corresponding conditions to our events/conditions sections.

@snippet:tutorials:new-line-highlighting@

=== "bonny.yml"

    ``` yaml hl_lines="16" linenums="1"
    conversations:
      Bonny:
        quester: "Bonny"
        first: "firstGreeting"
        NPC_options:
          firstGreeting:
            text: "Hey Stranger! You look new to me. Can you introduce yourself?"
            pointer: "introduce"
          niceToMeetYou:
            text: "Nice to meet you %player%. Please also introduce yourself to Joe and Fren and come back when 
            you've done it!"
        player_options:
          introduce:
            text: "I am %player%"
            pointer: "niceToMeetYou"
            events: "addTagIntroducedToBonny"
    ```

=== "joe.yml"

    ``` yaml hl_lines="8" linenums="1"
    conversations:
      Joe:
        quester: "Joe"
        first: "firstGreeting"
        NPC_options:
          firstGreeting:
            text: "Hey %player% Bonny already told me about you! Nice to have you here in our town."
            events: "addTagMetJoe"
    ```
    
=== "fren.yml"

    ``` yaml hl_lines="15" linenums="1"
    conversations:
      Fren:
        quester: "Fren"
        first: "firstGreeting"
        NPC_options:
          firstGreeting:
            text: "Hey %player%. Already know you because Bonny told me about you and that you're new here!"
            pointer: "introduce"
          niceToMeetYou:
            text: "Nice to meet you tho! I really like new people in our town!"
        player_options:
          introduce:
            text: "That's correct!"
            pointer: "niceToMeetYou"
            events: "addTagMetFren"
    ```

=== "events.yml"

    ``` yaml hl_lines="1-4" linenums="1"
    events:
      addTagIntroducedToBonny: "tag add introducedToBonny"
      addTagMetJoe: "tag add metJoe"
      addTagMetFren: "tag add metFren"
    ```

=== "conditions.yml"

    ``` yaml hl_lines="1-4" linenums="1"
    conditions:
      introducedToBonny: "tag introducedToBonny"
      metJoe: "tag metJoe"
      metFren: "tag metFren"
    ```

Now that we have written the events and conditions it's important to actually add the condition tags to the 
conversations and also add some more conversation so that it makes more sense. This will make the magic work!

!!! tip

    I always start by writing the events into the conversation options. Once the event is written, I proceed to write it in 
    the **events section** to ensure that the event actually exists. After that, I ask myself: What do I need for the
    event? Do I still need to write a **condition** or an **objective** for it? If you proceed systematically like this,
    you will make significantly fewer mistakes.


=== "bonny.yml"

    ``` yaml hl_lines="12-17" linenums="1"
    conversations:
      Bonny:
        quester: "Bonny"
        first: "{==finishedTask==},{==askingForProgress==},firstGreeting"
        NPC_options:
          firstGreeting:
            text: "Hey Stranger! You look new to me. Can you introduce yourself?"
            pointer: "introduce"
          niceToMeetYou:
            text: "Nice to meet you %player%. Please also introduce yourself to Joe and Fren and come back when 
            you've done it!"
          askingForProgress:
            text: "Hey %player% I think you don't have met them all yet.. Come back when you are ready!"
            conditions: "!metJoe,!metFren,introducedToBonny" #(1)!
          finishedTask:
            text: "You have met Joe and Fren! We are all there for you if you need something!"
            conditions: "metJoe,metFren" #(2)!
        player_options:
          introduce:
            text: "I am %player%"
            pointer: "niceToMeetYou"
            events: "addTagIntroducedToBonny"
    ```
    
    1. **metJoe** and **metFren** are the conditions that you need to negotiate because we want them to be met. We 
    also need **introducedToBonny** otherwise the conversation starts before the `firstGreeting` option. Thats 
    because the **metXXX** tags are true when negotiated.
   
    2. Both conditions must be true **metJoe** and **metFren** in order to activate this conversation. We also add 
    this conversation to `first`.

=== "joe.yml"

    ``` yaml hl_lines="9" linenums="1"
    conversations:
      Joe:
        quester: "Joe"
        first: "firstGreeting"
        NPC_options:
          firstGreeting:
            text: "Hey %player% Bonny already told me about you! Nice to have you here in our town."
            events: "addTagMetJoe"
            conditions: "introducedToBonny" #(1)!
    ```
    
    1. This conditions will be added to prevent the player from talking with the NPC before he not introduced 
    himself to Bonny first.
    
=== "fren.yml"

    ``` yaml hl_lines="9" linenums="1"
    conversations:
      Fren:
        quester: "Fren"
        first: "firstGreeting"
        NPC_options:
          firstGreeting:
            text: "Hey %player%. Already know you because Bonny told me about you and that you're new here!"
            pointer: "introduce"
            conditions: "introducedToBonny" #(1)!
          niceToMeetYou:
            text: "Nice to meet you tho! I really like new people in our town!"
        player_options:
          introduce:
            text: "That's correct!"
            pointer: "niceToMeetYou"
            events: "addTagMetFren"
    ```

    1. This conditions will be added to prevent the player from talking with the NPC before he not introduced 
    himself to Bonny first.
    
After we have added the conditions and events to the conversations and files we can now test it in-game!
You can now only talk to **Fren and Joe** after you introduced yourself to **Bonny**.

@snippet:tutorials:download-solution@
       ```
       /bq download BetonQuest/Quest-Tutorials ${ref} QuestPackages /Features/Tracking-Quest-Progress/2-HalfExample /trackingTutorial overwrite
       ```

## 3. Complete the introduction quest (optional)

We will now add some **events** and **condition tags** to round up the quest feeling. After we talked to the Fren 
and Joe, Bonny always would say the same. We can prevent that also adding a condition tag here.
Let us have a look:


=== "bonny.yml"

    ``` yaml hl_lines="18-21" linenums="1"
    conversations:
      Bonny:
        quester: "Bonny"
        first: "{==startMainQuest==},finishedTask,askingForProgress,firstGreeting"
        NPC_options:
          firstGreeting:
            text: "Hey Stranger! You look new to me. Can you introduce yourself?"
            pointer: "introduce"
          niceToMeetYou:
            text: "Nice to meet you %player%. Please also introduce yourself to Joe and Fren and come back when 
            you've done it!"
          askingForProgress:
            text: "Hey %player% I think you don't have met them all yet.. Come back when you are ready!"
            conditions: "!metJoe,!metFren,introducedToBonny"
          finishedTask:
            text: "You have met Joe and Fren! We are all there for you if you need something!"
            conditions: "metJoe,metFren"
            events: "addTagIntroducedToEveryone"
          startMainQuest:
            text: "Now I could need your help! Would you mind bringing me XXX?"
            conditions: "introducedToEveryone"
            events: #(1)!
            pointer: #(2)!
        player_options:
          introduce:
            text: "I am %player%"
            pointer: "niceToMeetYou"
            events: "addTagIntroducedToBonny"
    ```

    1. You can now continue your main quest here with whatever you want. Maybe with a **pointer** to another 
    conversation or a **event** to start something.
    
    2. You can now continue your main quest here with whatever you want. Maybe with a **pointer** to another 
    conversation or a **event** to start something.

=== "joe.yml"

    ``` yaml hl_lines="10-12" linenums="1"
    conversations:
      Joe:
        quester: "Joe"
        first: "{==mainConversation==},firstGreeting"
        NPC_options:
          firstGreeting:
            text: "Hey %player% Bonny already told me about you! Nice to have you here in our town."
            events: "addTagMetJoe"
            conditions: "introducedToBonny"
          mainConversation:
            text: "Very nice to see you again! I dont have any tasks for you at the moment"
            conditions: "metJoe"
    ```
    
=== "fren.yml"

    ``` yaml hl_lines="10-12" linenums="1"
    conversations:
      Fren:
        quester: "Fren"
        first: "{==mainConversation==},firstGreeting"
        NPC_options:
          firstGreeting:
            text: "Hey %player%. Already know you because Bonny told me about you and that you're new here!"
            pointer: "introduce"
            conditions: "introducedToBonny"
          niceToMeetYou:
            text: "Nice to meet you tho! I really like new people in our town!"
          mainConversation:
            text: "You again! I dont have anything to do for you! Come back later."
            conditions: "metFren"
        player_options:
          introduce:
            text: "That's correct!"
            pointer: "niceToMeetYou"
            events: "addTagMetFren"
    ```

=== "events.yml"

    ``` yaml hl_lines="5" linenums="1"
    events:
      addTagIntroducedToBonny: "tag add introducedToBonny"
      addTagMetJoe: "tag add metJoe"
      addTagMetFren: "tag add metFren"
      addTagIntroducedToEveryone: "run ^tag add introducedToEveryone ^tag delete metFren,metJoe" #(1)!
    ```

    1. We suggest to remove unnessecary tags and only have the needed ones active.

=== "conditions.yml"

    ``` yaml hl_lines="5" linenums="1"
    conditions:
      introducedToBonny: "tag introducedToBonny"
      metJoe: "tag metJoe"
      metFren: "tag metFren"
      introducedToEveryone: "tag introducedToEveryone"
    ```
    
@snippet:tutorials:download-this-part@
       ```
       /bq download BetonQuest/Quest-Tutorials ${ref} QuestPackages /Features/Tracking-Quest-Progress/3-FullExample /trackingTutorial overwrite
       ```
