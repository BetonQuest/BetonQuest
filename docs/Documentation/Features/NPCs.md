---
icon: material/message-text
tags:
  - Npcs
---

NPCs are an essential part of every RPG for player ingame interaction.
In BetonQuest NPCs can be used to start conversations or interact with them otherwise,
as shown in the `Scripting` and `Visual Effects` section of the documentation.

!!! info
    This NPC is not related to the NPC/Quester in [Conversations](Conversations.md)

## Provided Integrations

BetonQuest provides Integrations for the following Npc plugins:

- [Citizens](../Scripting/Building-Blocks/Integration-List.md#citizens)
- [MythicMobs](../Scripting/Building-Blocks/Integration-List.md#mythicmobs)
- [FancyNpcs](../Scripting/Building-Blocks/Integration-List.md#fancynpcs)
- [ZNPCsPlus](../Scripting/Building-Blocks/Integration-List.md#znpcsplus)

## Referring an NPC

Npcs are defined in the `npcs` section.
!!! note ""
    === "Citizens"
        ```YAML title="Example"
        npcs:
          innkeeper: citizens 0
          mayorHans: citizens 4
          guard: citizens Guard byName
        ```
       
        You simply use the Citizens NPC id as argument.
        To acquire the NPCs ID select the NPC using `/npc select`, then run `/npc id`.
     
        You can also get a NPC by its name with the `byName` argument.
        That is useful when you have many NPCs with the same name which should all start the same conversation
        or count together in the `npcinteract` and `npckill` objectives.
            
    === "MythicMobs"
        ```YAML title="Example"
        npcs:
          innkeeper: mythicmobs MYTHIC_MOB inkeeper
          mayorHans: mythicmobs UUID b18af0c3-5db7-4878-9693-05fe1b2c5a2f
          guard: mythicmobs FACTION guards
        ```
       
        You simply use the Entity UUID as argument.
        To acquire the NPCs UUID use the `/mm info` command and copy the `UUID` from the NPC info.
     
        You can also get a NPC by its `mythic mob` type or `faction`.
        That is useful when you have many NPCs which should all start the same conversation
        or count together in the `npcinteract` and `npckill` objectives.
            
    === "FancyNpcs"
        ```YAML title="Example"
        npcs:
          innkeeper: FancyNpcs dc8f2889-ed79-455e-944b-115dae978737
          mayorHans: FancyNpcs 72910823-c0c3-499d-adcc-d31cb75963c0
          guard: FancyNpcs Guard byName
        ```
        
        You simply use the FancyNpcs NPC id as argument.
        To acquire the NPCs ID use the `/npc nearby` command and copy the `UUID` from the NPC info.
        
        You can also get a NPC by its name with the `byName` argument.
        That is useful when you have many NPCs with the same name which should all start the same conversation
        or count together in the `npcinteract` and `npckill` objectives.
            
    === "ZNPCsPlus"
        ```YAML title="Example"
        npcs:
          bernhard: ZNPCsPlus bernhard
          guard: ZNPCsPlus Guard10
        ```
        
        You simply use the ZNPCsPlus NPC ID as argument.
        To acquire the NPCs ID use the `/npc near 5` command and copy the `ID` from the NPC info.

!!! warning
    If there are more NPCs than one NPC with the same name, and you select multiple NPCs by name (like by using 
    Citizens `byName` option) certain events like `npcteleport` or objectives like `npcrange` might throw an exception.
## Conversations

You can start [Conversations](Conversations.md) with NPC interaction by assigning them in the
[`npc_conversations` section](Conversations.md#binding-conversations-to-npcs) of a quest package.

## NPC Hiding: `hide_npcs`
You can hide NPCs for certain players using conditions.
[You can find information about it here.](../Visual-Effects/NPC-Effects/NPC-Hiding.md)
