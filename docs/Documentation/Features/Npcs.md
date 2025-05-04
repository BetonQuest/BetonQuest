---
icon: material/message-text
tags:
  - Npcs
---

Npcs are an essential part of every RPG for player ingame interaction.
In BetonQuest Npcs can be used to start conversations or interact with them otherwise,
as shown in the `Scripting` and `Visual Effects` section of the documentation.

!!! info
    This Npc is not related to the NPC/Quester in [Conversations](Conversations.md)

## Provided Integrations

BetonQuest provides Integrations for the following Npc plugins:

- [Citizens](../Scripting/Building-Blocks/Integration-List.md#citizens)
- [FancyNpcs](../Scripting/Building-Blocks/Integration-List.md#fancynpcs)
- [ZNPCsPlus](../Scripting/Building-Blocks/Integration-List.md#znpcsplus)

!!! warning
    Citizens needs [ProtocolLib plugin](https://ci.dmulloy2.net/job/ProtocolLib/lastSuccessfulBuild/artifact/build/libs/ProtocolLib.jar) for all functions to work correctly!!!

## Referring an Npc

Npcs are defined in the `npcs` section.
### Example
=== "Citizens"
    !!! example
        ```YAML
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
        
=== "FancyNpcs"
    !!! example
        ```YAML
        npcs:
          innkeeper: FancyNpcs dc8f2889-ed79-455e-944b-115dae978737
          mayorHans: FancyNpcs 72910823-c0c3-499d-adcc-d31cb75963c0
          guard: FancyNpcs Guard byName
        ```
        
        You simply use the FancyNpcs Npc id as argument.
        To acquire the Npcs ID use the `/npc nearby` command and copy the `UUID` from the Npc info.
        
        You can also get a Npc by its name with the `byName` argument.
        That is useful when you have many Npcs with the same name which should all start the same conversation
        or count together in the `npcinteract` and `npckill` objectives.
        
=== "ZNPCsPlus"
    !!! example
        ```YAML
        npcs:
          bernhard: ZNPCsPlus bernhard
          guard: ZNPCsPlus Guard10
        ```
        You simply use the ZNPCsPlus Npc id as argument.
        To acquire the Npcs ID use the `/npc near 5` command and copy the `ID` from the Npc info.

!!! warning
    If there are more npcs than one npc with the same name, and you select multiple npcs by name (like by using 
    Citizens `byName` option) certain events like `npcteleport` or objectives like `npcrange` might throw an exception.
## Conversations

You can start [Conversations](Conversations.md) with Npc interaction by assigning them in the
[`npc_conversations` section](Conversations.md#binding-conversations-to-npcs) of a quest package.

## Npc Hiding: `hide_npcs`
You can hide NPCs for certain players using conditions.
[You can find information about it here](../../Visual-Effects/NPC-Effects/NPC-Hiding.md)
@snippet:integrations:protocollib@
