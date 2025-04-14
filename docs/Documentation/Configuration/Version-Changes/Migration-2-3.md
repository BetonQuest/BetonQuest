---
icon: material/upload
---
This guide explains how to migrate from the latest BetonQuest 2.X version to BetonQuest 3.X.

**The majority of changes will be migrated automatically. However, some things must be migrated manually.**

!!! warning 
    Before you start migrating, you should **back up your server**!

!!! info
    You need to set `legacy` as version in your `package.yml` (for both QuestPackages and -Templates)
    to automatically migrate from "not versioned".
    ```yaml title="Required version"
    package:
      version: legacy
    ```

## Changes

Steps marked with :gear: are migrated automatically. Steps marked with :exclamation: must be done manually.

- [3.0.0-DEV-58 - Delete messages.yml](#300-dev-58-delete-messagesyml) :exclamation:
- [3.0.0-DEV-65 - Delete menuConfig.yml](#300-dev-65-delete-menuconfigyml) :exclamation:
- [3.0.0-DEV-71 - Renamed Translation Keys](#300-dev-71-renamed-translation-keys) :gear:
- [3.0.0-DEV-114 - Npc Rework](#300-dev-114-npc-rework) :exclamation:
- [3.0.0-DEV-135 - Citizens Adaption to NpcID](#300-dev-135-citizens-adaption-to-npcid) :exclamation:
- [3.0.0-DEV-142 - Conversation Sounds](#300-dev-142-conversation-sounds) :exclamation:

### 3.0.0-DEV-58 - Delete messages.yml :exclamation:

The `messages.yml` file has been removed.
All messages are now stored in the `lang` folder, you can also customize them there and add new languages.
If you still have the old messages.yml file, you get a warning in the console.
If you don't have any custom messages,
you can delete the file safely otherwise you should move the messages to the new location.

### 3.0.0-DEV-65 - Delete menuConfig.yml :exclamation:

The `menuConfig.yml` file has been removed.
If you had the option `default_close` configured, you can now find this option in the `config.yml` file.
All translations in the config where also moved to the `lang` folder,
so also here you need to move your custom translations.

### 3.0.0-DEV-71 - Renamed Translation Keys :gear:

??? info "Automated Migration"
    *The migration is automated. You shouldn't have to do anything.*
    
    -------------

    All translations where moved from the messages.yml file to individual files in the `lang` folder.
    Also all keys for the langusges have been renamed to match the new lang-region format, where lang and region are always two letters.  
    So the mapping of the old to the new keys is as follows:
    
    | Old Key | New Key | Old Key | New Key | Old Key | New Key | Old Key | New Key |
    |---------|---------|---------|---------|---------|---------|---------|---------|
    | en      | en-US   | de      | de-DE   | es      | es-ES   | fr      | fr-FR   |
    | hu      | hu-HU   | it      | it-IT   | nl      | nl-NL   | pl      | pl-PL   |
    | pt-br   | pt-BR   | pt-pt   | pt-PT   | ru      | ru-RU   | vi      | vi-VN   |
    | cn      | zh-CN   |         |         |         |         |         |         |
    
    If you have custom translations, you need to move them to the new `lang` folder.
    Because the keys have been renamed, you need to rename the keys in some parts of config files and scripts.
    
    ```YAML title="new config.yml"
        language: en-US
    ```
    
    <div class="grid" markdown>
    
    ```YAML title="Old Syntax Events"
    events:
      notify: notify {en} English text {de} Deutscher text
    ```
    
    ```YAML title="New Syntax Events"
    events:
      notify: notify {en-US} English text {de-DE} Deutscher text
    ```
    
    </div>
    
    And the most complicated change is everywhere you used multiple languages in sections, here is only one example:
    
    <div class="grid" markdown>
    
    ```YAML title="Old Conversation"
    conversations:
      unkown:
        quester:
          en: Jane Doe
          de: Erika Mustermann
    ```
    
    ```YAML title="New Conversation"
    conversations:
      unkown:
        quester:
          en-US: Jane Doe
          de-DE: Erika Mustermann
    ```
    
    </div>
    
    The same applies to all other sections where you used multiple languages, this should be a complete list:
    
    - `conversations.*.quester`
    - `conversations.*.NPC_options.*.text`
    - `conversations.*.player_options.*.text`
    - `compass.*.name`
    - `cancel.*.name`
    - `journal.*`
    - `journal_main_page.*.text`
    - `menus.*.items.*.text`

### 3.0.0-DEV-114 - Npc Rework :exclamation:

To support more Npc plugins than just Citizens the system got a rework.

Npcs are now addressed with IDs and defined in the `npcs` section.
Starting conversations with Npc interaction is moved inside the `npc_conversations` section.

Also, the `teleportnpc` event got renamed to `npcteleport`.

In addition, the `npc` variable to get the quester name of the current conversation got changed to `quester`.
That change is automated.

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

### 3.0.0-DEV-135 - Citizens Adaption to NpcID :exclamation:

To streamline usage of Npcs the Citizens specific events and objective now also use the NpcID introduced in 3.0.0-DEV-114.
Also, the `movenpc` and `stopnpc` events are renamed into `npcmove` and `npcstop`.

As in the migration above stated you can either use a descriptive name as the id or use the numeric Citizens id of the Npc.

<div class="grid" markdown>

```YAML title="Old Syntax"
events:
  move: movenpc 0 100;200;300;world #(1)!
  stop: stopnpc 0
objectives:
  kill: npckill 1 amount:5 events:reward
```

1. The `0` is here the citizens npc id.

```YAML title="New Syntax"
npcs:
  0: "citizens 0" #(1)!
  thief: "citizens thief byName"

events:
  move: npcmove 0 100;200;300;world
  stop: npcstop 0 #(2)!
objectives:
  kill: npckill thief amount:5 events:reward
```

1. The `0` before the ':' is now the BetonQuest ID, where the `citizens 0` defines the Npc with id "0" from the 
Citizens integration. 
2. The `0` is here the BetonQuest NpcID but stays the same.
</div>

### 3.0.0-DEV-142 - Conversation Sounds :exclamation:

The start and stop sound in the configuration for conversations were removed in favor of the notification system,
that now also has the two new build in categories `conversation_start` and `conversation_end`.

To get the previous sounds back, you need to configure it now like the following in any quest package:

<div class="grid" markdown>

```YAML title="Old Syntax (config.yml)"
sounds:
  start: ENTITY_VILLAGER_AMBIENT
  end: ENTITY_VILLAGER_YES
```

```YAML title="New Syntax"
notifications:
  conversation_start:
    sound: ENTITY_VILLAGER_AMBIENT
  conversation_end:
    sound: ENTITY_VILLAGER_YES
```

</div>
