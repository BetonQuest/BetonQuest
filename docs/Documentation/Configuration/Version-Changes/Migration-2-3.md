---
icon: material/upload
---
This guide explains how to migrate from the latest BetonQuest 2.X version to BetonQuest 3.X.

**The majority of changes will be migrated automatically. However, some things must be migrated manually.**

!!! warning 
    Before you start migrating, you should **backup your server**!

## Changes

Steps marked with :gear: are migrated automatically. Steps marked with :exclamation: must be done manually.

- [3.0.0-DEV-58 - Delete messages.yml](#300-dev-58-delete-messagesyml) :exclamation:
- [3.0.0-DEV-X - Npc Rework](#300-dev-x-npc-rework) :exclamation:

### 3.0.0-DEV-58 - Delete messages.yml :exclamation:

The `messages.yml` file has been removed.
All messages are now stored in the `lang` folder, you can also customize them there and add new languages.
If you still have the old messages.yml file, you get a warning in the console.
If you dont have any custom messages,
you can delete the file safely otherwise you should move the messages to the new location.

### 3.0.0-DEV-X - Npc Rework :exclamation:

To support more Npc plugins than just Citizens the system got a rework.

Npcs are now addressed with IDs and defined in the `npcs` section.
Starting conversations with Npc interaction is moved inside the `npc_conversations` section.

Also, the `teleportnpc` event got renamed to `npcteleport`.

You can keep most of the syntax when you use the Citizens Npc id as their BetonQuest identifier,
but changing the "name" makes the difference more clear.

Also, the `citizens_npcs_by_name` configuration option was removed in favor of the
[`byName`](../../Scripting/Building-Blocks/Integration-List.md#citizens) argument.

<div class="grid" markdown>

```YAML title="Old Syntax"
npcs:
  '0': "HansConv" #(1)!
  '1': "HansConv"
events:
  teleportNpc: teleportnpc 0 100;200;300;world #(2)!
conditions:
  nearNpc: npcdistance 0 10 #(2)!
  nearNpcTwo: npcdistance 1 10
conversations:
  HansConv: #(3)!
    quester: Hans
    first: Hello
    NPC_options:
      Hello:
        text: Hello Adventurer!
```

1. The conversation the interaction with the Npc will start.
2. The `0` is here the citizens npc id.
3. The conversation name as used in the `npcs` section.

```YAML title="New Syntax"
npcs:
  0: "citizens 0" #(1)!
  HansTwo: "citizens 1" #(2)!
  
npc_conversations:
  0: HansConv #(3)!
  HansTwo: HansConv #(4)!
events:
  teleportNpc: npcteleport 0 100;200;300;world #(3)!
conditions:
  nearNpc: npcdistance 0 10 #(3)!
  nearNpcTwo: npcdistance HansTwo 15 #(4)!
conversations:
  HansConv:
    quester: Hans
    first: Hello
    NPC_options:
      Hello:
        text: Hello Adventurer!
```

1. The `0` before the ':' is now the BetonQuest ID, where the `citizens 0` defines the Npc with id "0" from the 
Citizens integration. 
2. Here we use a name for the id to make the difference more clear.
3. The `0` is here the BetonQuest NpcID but stays the same.
4. An example of a "renamed" reference.
5. The Npcs that start this conversation.
</div>
