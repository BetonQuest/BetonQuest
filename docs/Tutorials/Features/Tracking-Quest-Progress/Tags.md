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
    It is absolutely necessary to be familiar with the basics of conditions and understand the underlying principles
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
    /bq download BetonQuest/Quest-Tutorials ${ref} QuestPackages 
    /Advanced/Tracking-Quest-Progress/1-ExampleQuest 
    /questTracking overwrite
    ```
    You can now find all files needed for this tutorial in this location:
    "_YOUR-SERVER-LOCATION/plugins/BetonQuest/QuestPackages/questTracking_"

After you have created or downloaded all the necessary files, we will begin to fill our conversation files with some
small talk. In this tutorial, we will create three NPCs that you have to talk to, and with tags, we will prevent you 
from talking to the same NPC repeatedly. We will also configure the second and third NPC so that you can only talk 
to them after you have introduced yourself to the first NPC.

=== "Bonny.yml"

    ``` yaml
    conversations:
      Bonny:
        quester: "Bonny"
        first: "firstGreeting"
        NPC_options:
          firstGreeting:
            text: "Hey Stranger! You look new to me. Can you introduce yourself?"
            pointer: "introduce"
          niceToMeetYou:
            text: "Nice to meet you %player%. I hope we see us again very soon!"
        player_options:
          introduce:
            text: "I am %player%"
            pointer: "niceToMeetYou"
    ```

=== "Joe.yml"

    ``` yaml
    conversations:
      Joe:
        quester: "Joe"
        first: "firstGreeting"
        NPC_options:
          firstGreeting:
            text: "Hey %player% Bonny already told me about you! Nice to have you here in our town."
    ```
    
=== "Fren.yml"

    ``` yaml
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
    
Now that we have our basic conversations we need to add tags to it otherwise you could talk to any NPC at any time
and we want to prevent that to get a nice feeling conversation with these NPC's.


