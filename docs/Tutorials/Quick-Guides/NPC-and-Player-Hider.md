---
icon: material/eye-off
---

# How to hide players and NPCs

BetonQuest allows you to hide players and NPCs dynamically based on conditions.
This is useful for creating private quest instances, story areas, or secret locations without affecting other players.
Choose the implementation that best fits your use case.

The basic idea is: 

- Player visibility is controlled with the `player_hider` section.
- NPC visibility is controlled with the `hide_npcs` section.
- Visibility changes automatically whenever the configured conditions change.
- The `updatevisibility` action can be used to force an immediate refresh instead of waiting for the next update interval.


=== "Hide players"

    The following example hides all players inside a secret room from each other.
    
    ```yaml
    player_hider:
      secretRoom:
        source_player: inSecretRoom #(1)!
        target_player: inSecretRoom #(2)!
    
    conditions:
      inSecretRoom: "location 100;100;100;world 10" #(3)!
    ```
    
    1. Players meeting this condition will no longer see the target players.
    2. Players matching this condition become invisible to all matching source players.
    3. Defines the area in which players become invisible to each other.

=== "Hide NPCs"

    The following example hides an NPC while a specific condition is met.
    
    ``` yaml
    npcs:
      dummy: "citizens 20"
    
    npc_conversations:
      dummy: "dummyTheYummy"
    
    hide_npcs:
      dummy: "alreadyTalkedToDummy" #(1)!
    
    actions:
      hideNPC: "folder addTagTalked,hideInstantly" #(2)!
      addTagTalked: "tag add talkedToDummy"
      hideInstantly: "updatevisibility" #(3)!
    
    conditions:
      alreadyTalkedToDummy: "tag talkedToDummy" #(4)!
    
    conversations:
      dummyTheYummy:
        quester: "Dumbum"
        first: "dialog_1_1"
    
        NPC_options:
          dialog_1_1:
            text: "Hey! You look new around here. What's your name?"
            pointers: "dialog_1_1_1,dialog_1_1_2"
    
          dialog_1_2:
            text: "Nice to meet you, %player%! I have to leave now, but come back tomorrow and I'll send you on an adventure."
            pointers: "dialog_1_1_3"
    
          dialog_1_3:
            text: "Oh, you don't want to tell me? That's okay. I have to leave now anyway. Maybe you'll tell me tomorrow when I'm back."
            pointers: "dialog_1_1_3"
    
        player_options:
          dialog_1_1_1:
            text: "My name is %player%."
            pointers: "dialog_1_2"
    
          dialog_1_1_2:
            text: "That's a secret."
            pointers: "dialog_1_3"
    
          dialog_1_1_3:
            text: "See you tomorrow!"
            actions: "hideNPC" #(5)!
    ```
    
    1. The NPC is hidden from players that meet the `alreadyTalkedToDummy` condition.
    2. After the conversation ends, the player receives the tag and the NPC visibility is updated.
    3. Forces an immediate visibility update instead of waiting for the configured `npc_update_interval`.
    4. Once the player has the `talkedToDummy` tag, the NPC is hidden from them.
    5. The NPC disappears immediately after the player finishes the conversation.
    
