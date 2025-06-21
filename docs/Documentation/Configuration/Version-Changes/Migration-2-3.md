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

!!! Note
    - :sun: **Fully automated migration** – These steps are reliably migrated without issues in most cases.
      You usually don’t need to take any action. However, certain rarely used or non-standard formats (e.g., run 
      events or math variables) may not migrate correctly if they deviate from the common structure.
    - :white_sun_cloud: **Automated migration with known limitations** – These steps are generally migrated automatically,
      especially for straightforward cases. However, there are known edge cases that cannot be detected or handled 
      automatically. You should review these steps and be prepared to make manual adjustments where needed.
    - :thunder_cloud_rain: **Manual migration required** – These steps are not migrated at all.
      Either the structure is too complex to detect automatically, or the new format requires additional information.
      You will need to fully rewrite or convert these steps yourself.

- [3.0.0-DEV-58 - Delete messages.yml](#300-dev-58-delete-messagesyml) :thunder_cloud_rain:
- [3.0.0-DEV-65 - Delete menuConfig.yml](#300-dev-65-delete-menuconfigyml) :thunder_cloud_rain:
- [3.0.0-DEV-71 - Renamed Translation Keys](#300-dev-71-renamed-translation-keys) :sun:
- [3.0.0-DEV-114 - Npc Rework](#300-dev-114-npc-rework) :thunder_cloud_rain:
- [3.0.0-DEV-135 - Citizens Adaption to NpcID](#300-dev-135-citizens-adaption-to-npcid) :thunder_cloud_rain:
- [3.0.0-DEV-142 - Conversation Sounds](#300-dev-142-conversation-sounds) :thunder_cloud_rain:
- [3.0.0-DEV-217 - Item Type](#300-dev-217-item-type) :sun:
- [3.0.0-DEV-232 - Singular to Plural](#300-dev-232-singular-to-plural) :sun:
- [3.0.0-DEV-233 - `pickrandom` event](#300-dev-233-pickrandom-event) :sun:
- [3.0.0-DEV-244 - Menu Item move](#300-dev-244-menu-item-move) :sun:
- [3.0.0-DEV-267 - MoonPhases rename](#300-dev-267-moonphase-rename) :sun:
- [3.0.0-DEV-274 - String List remove](#300-dev-274-string-list-remove) :sun:
- [3.0.0-DEV-277 - Rename Constants](#300-dev-277-rename-constants) :white_sun_cloud:
- [3.0.0-DEV-284 - Change Head Owner](#300-dev-284-change-head-owner) :sun:
- [3.0.0-DEV-299 - NPC events rename](#300-dev-299-npc-events-rename) :sun:
- [3.0.0-DEV-306 - MMOItems Item Type](#300-dev-306-mmoitems-item-type) :thunder_cloud_rain:
- [3.0.0-DEV-313 - Folder Time Unit](#300-dev-313-folder-time-unit) :white_sun_cloud:
- [3.0.0-DEV-316 - Chest Conversation IO](#300-dev-316-chest-conversation-io) :thunder_cloud_rain:
- [3.0.0-DEV-329 - Delete `menu_conv_io` settings](#300-dev-329-delete-menu_conv_io-settings) :thunder_cloud_rain:
- [3.0.0-DEV-337 - Menu Conversation IO line wrapping](#300-dev-337-menu-conversation-io-components) :white_sun_cloud:

### 3.0.0-DEV-58 - Delete messages.yml :thunder_cloud_rain:

The `messages.yml` file has been removed.
All messages are now stored in the `lang` folder, you can also customize them there and add new languages.
If you still have the old messages.yml file, you get a warning in the console.
If you don't have any custom messages,
you can delete the file safely otherwise you should move the messages to the new location.

### 3.0.0-DEV-65 - Delete menuConfig.yml :thunder_cloud_rain:

The "_menuConfig.yml_" file has been removed.
If you had the option `default_close` configured, you can now find this option in the "_config.yml_" file.
All translations in the config where also moved to the `lang` folder,
so also here you need to move your custom translations.

### 3.0.0-DEV-71 - Renamed Translation Keys :sun:

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

### 3.0.0-DEV-114 - Npc Rework :thunder_cloud_rain:

To support more Npc plugins than just Citizens the system got a rework.

Npcs are now addressed with IDs and defined in the `npcs` section.
Starting conversations with Npc interaction is moved inside the `npc_conversations` section.

Also, the `teleportnpc` event got renamed to `npcteleport`. That change is automated, when updating to version
3.0.0-DEV-299 or newer.

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

### 3.0.0-DEV-135 - Citizens Adaption to NpcID :thunder_cloud_rain:

To streamline usage of Npcs the Citizens specific events and objective now also use the NpcID introduced in 3.0.0-DEV-114.

Also, the `movenpc` and `stopnpc` events are renamed into `npcmove` and `npcstop`. These renames are automated, when 
updating to version 3.0.0-DEV-299 or newer.

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

### 3.0.0-DEV-142 - Conversation Sounds :thunder_cloud_rain:

The start and stop sound in the configuration for conversations were removed in favor of the notification system,
that now also has the two new build in categories `conversation_start` and `conversation_end`.
Also the messages it self are now printed in every conversation and can now be configured with the notification system.

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

If you want to disable the the start and end message, now you can configure the following:

````YAML title="Disable Messages"
notifications:
  conversation_start:
    io: suppress
  conversation_end:
    io: suppress
````

and if you only want sounds and no message, you use `sound` instead of `suppress` as io.

### 3.0.0-DEV-217 - Item Type :sun:

??? info "Automated Migration"
    *The migration is automated. You shouldn't have to do anything.*
    
    -------------

    To allow usage of 3rd-party items in QuestItems the standard definition is now prefixed with `simple`.
    
    <div class="grid" markdown>
    
    ```YAML title="Old Syntax"
    items:
      blizz: "DIAMOND_SWORD name:The_Blizz lore:Made_of_Ice"
    ```
    
    ```YAML title="New Syntax"
    items:
      blizz: "simple DIAMOND_SWORD name:The_Blizz lore:Made_of_Ice"
    ```

    </div>

### 3.0.0-DEV-232 - Singular to Plural :sun:

??? info "Automated Migration"
    *The migration is automated. You shouldn't have to do anything.*
    
    -------------

    BetonQuest allows a lot of lists (comma separated values) to reference for example events and conditions.
    In the past the key of those lists was always `event` or `condition`.
    Then someone introduced the first plural `events` and `conditions`, and so the project supported both.
    As this leads into a lot of bad code and confusion, we decided to remove the singular version.
    Therefore the follorwing changes were made:
    
    - objectives - `event` and `condition` are now `events` and `conditions`
    - events - `condition` is now `conditions`
    - menus - `condition` is now `conditions`
    - conversations - options `condition`, `event`, `pointer` and `extend` are now `conditions`, `events`, `pointers` and `extends`
    
    <div class="grid" markdown>
    
    ```YAML title="Old Syntax"
    objectives:
      action: action LEFT ANY global persistent event:notify condition:sneak
    ```
    
    ```YAML title="New Syntax"
    objectives:
      action: action LEFT ANY global persistent events:notify conditions:sneak
    ```

    </div>

### 3.0.0-DEV-233 - `pickrandom` event :sun:

??? info "Automated Migration"
    *The migration is automated. You shouldn't have to do anything.*
    
    -------------

    To allow a list of events also containing variables in any form, it is not possible anymore to use the percentage,
    instead the tilde `~` is used to separate the chance from the event.
    
    <div class="grid" markdown>
    
    ```YAML title="Old Syntax"
    events:
      pickRandom: pickrandom 1%pickRandom1,2%pickRandom2,3%pickRandom3
    ```
    
    ```YAML title="New Syntax"
    events:
      pickRandom: pickrandom 1~pickRandom1,2~pickRandom2,3~pickRandom3
    ```

    </div>

### 3.0.0-DEV-244 - Menu Item move :sun:

??? info "Automated Migration"
    *The migration is automated. You shouldn't have to do anything.*
    
    -------------

    To use a Menu Item in multiple Menus they are now defined in their own `menu_items` section.
    
    <div class="grid" markdown>
    
    ```YAML title="Old Syntax"
    menus:
      questMenu:
        height: 4
        title: "&6&lQuests"
        bind: "openMenuItem"
        command: "/quests"
        slots:
          4: "reputation"
          5-8: "filler,filler,filler,filler"
    
        items:
          reputation:
            item: "xpBottle"
            amount: 1
            text:
                - "&2Quest Level: &6&l%point.quest_reputation.amount%"
            close: true
          filler:
            text: "&a "
            item: "filler"
    ```
    
    ```YAML title="New Syntax"
    menus:
      questMenu:
        height: 4
        title: "&6&lQuests"
        bind: "openMenuItem"
        command: "/quests"
        slots:
          4: "reputation"
          5-8: "filler,filler,filler,filler"
    
    menu_items:
      reputation:
        item: "xpBottle"
        amount: 1
        text:
            - "&2Quest Level: &6&l%point.quest_reputation.amount%"
        close: true
      filler:
        text: "&a "
        item: "filler"
    ```
    
    </div>

### 3.0.0-DEV-267 - MoonPhase rename :sun:

??? info "Automated Migration"
    *The migration is automated. You shouldn't have to do anything.*
    
    -------------
    
    The `mooncycle` condition was renamed to `moonphase` and instead of numbers, which stood for the mood phases, 
    the names of the moon phases are now used directly.
    
    <div class="grid" markdown>
    
    ```YAML title="Old Syntax"
    conditions:
      mooncycle: mooncycle 1
      otherMooncycle: mooncycle 2 world:world
    ```
    
    ```YAML title="New Syntax"
    conditions:
      moonphase: moonphase NEW_MOON
      otherMoonphase: moonphase FULL_MOON world:world
    ```
    
    </div>

### 3.0.0-DEV-274 - String List remove :sun:

??? info "Automated Migration"
    *The migration is automated. You shouldn't have to do anything.*
    
    -------------
    
    All string lists (lists that are new lines with dashes) at unusual places are now replaced with a comma separated list.
    This includes:
    
    - `npcs` in the `npc_holograms` section
    - `npcs`, `conditions` and `locations` in the `effectlib` section

    <div class="grid" markdown>
    
    ```YAML title="Old Syntax"
    effectlib:
      effect1:
        class: VortexEffect
        npcs:
          - NPC1
          - NPC2
          - NPC3
    ```
    
    ```YAML title="New Syntax"
    effectlib:
      effect1:
        class: VortexEffect
        npcs: NPC1,NPC2,NPC3
    ```
    
    </div>

### 3.0.0-DEV-277 - Rename Constants :white_sun_cloud:
??? info "Automated Migration"
    *The migration is automated. You shouldn't have to do anything.*
    
    -------------
    
    "Global Variables" have been replaced by "Constants" to better reflect their purpose
    and also to integrate them into the existing variable system.
    
    <div class="grid" markdown>
    
    ```YAML title="Old Syntax"
    variables:
      MyVariable: Hello
      MyCustomVariable: $MyVariable$ World
    events:
      sendNotify: notify $MyCustomVariable$
    ```
    
    ```YAML title="New Syntax"
    constants:
      MyVariable: Hello
      MyCustomVariable: %constant.MyVariable% World
    events:
      sendNotify: notify %constant.MyCustomVariable%
    ```
    
    </div>

### 3.0.0-DEV-284 - Change Head Owner :sun:
??? info "Automated Migration"
    *The migration is automated. You shouldn't have to do anything.*
    
    -------------
    
    To allow pre-parsing of `constant` variables in `simple` Quest Items the `owner:%player%` has been replaced with `owner:`.
    
    <div class="grid" markdown>
    
    ```YAML title="Old Syntax"
    items:
      head: simple PLAYER_HEAD owner:%player%
    ```
    
    ```YAML title="New Syntax"
    items:
      head: 'simple PLAYER_HEAD owner:'
    ```
    
    </div>

### 3.0.0-DEV-299 - NPC events rename  :sun:
??? info "Automated Migration"
    *The migration is automated. You shouldn't have to do anything.*
    
    -------------
    
    Some NPC events were renamed in the versions [3.0.0-DEV-114 - Npc Rework](#300-dev-114-npc-rework) and [3.0.
    0-DEV-135 - Citizens Adaption to NpcID](#300-dev-135-citizens-adaption-to-npcid). These renames are automated now.

### 3.0.0-DEV-306 - MMOItems Item Type :thunder_cloud_rain:

MMOItems is now integrated into the item system.

Instead of using mmo specific pickup objectives or item conditions and events it now uses the standard implementations.
The following obsolete implementations were removed:

- `mmoitem` condition
- `mmohand` condition
- `mmoitemgive` event
- `mmoitemtake` event
- `mmoitemcraft` objective

<div class="grid" markdown>

```YAML title="Old Syntax"
conditions:
  hand: mmohand ARMOR SKELETON_CROWN
  inventory: mmoitem ARMOR SKELETON_CROWN
events:
  give: mmoitemgive ARMOR SKELETON_CROWN
  take: mmoitemtake ARMOR SKELETON_CROWN
objectives:
  craft: mmoitemcraft ARMOR SKELETON_CROWN
```

```YAML title="New Syntax"
items:
  crown: mmoitem ARMOR SKELETON_CROWN
conditions:
  hand: hand crown
  inventory: item crown
events:
  give: give crown
  take: take crown
objectives:
  craft: craft crown
```

</div>

The `mmoitemupgrade` and `mmoitemapplygem` objectives exist unchanged.

### 3.0.0-DEV-313 - Folder Time Unit :white_sun_cloud:
??? info "Automated Migration"
    *The migration is automated. You shouldn't have to do anything.*
    
    -------------
    
    To allow variables for the time unit in the `folder` event, the time unit now needs a key `unit`.
    
    <div class="grid" markdown>
    
    ```YAML title="Old Syntax"
    events:
      setBlocks: folder block1,block2,block3 period:10 ticks
    ```
    
    ```YAML title="New Syntax"
    events:
      setBlocks: folder block1,block2,block3 period:10 unit:ticks
    ```
    
    </div>

### 3.0.0-DEV-316 - Chest Conversation IO :thunder_cloud_rain:

To make future changes possible and to improve the possibility to add new features,
the display `item` is now defined in a new section called `properties`.

<div class="grid" markdown>

```YAML title="Old Syntax"
conversations:
  #...
    player_options:
      exampleOption:
        text: "{diamond}This is an example option"
```

```YAML title="New Syntax"
conversations:
  #...
    player_options:
      exampleOption:
        text: "This is an example option"
        properties:
          item: "diamond"
items:
  diamond: "DIAMOND"
```

</div>

### 3.0.0-DEV-329 - Delete `menu_conv_io` settings :thunder_cloud_rain:

All options configured in the `menu_conv_io` section in quest packages,
are now defined in the '_config.yml_' under the path `conversations.io.menu`.

### 3.0.0-DEV-337 - Menu Conversation IO components :white_sun_cloud:

The new used components don't need the settings `conversation.io.menu.option_selected_reset`,
`conversation.io.menu.option_text_reset` and `conversation.io.menu.npc_text_reset` anymore and also the menu
conversation IO text wrapping was reworked.
As a result the `line_length` unit is now in pixels instead of characters.
This means that the old value is multiplied by 6.
As a result, the default value is now 320 pixels instead of 50 characters what is more precise and allows more text.
The default value is migrated automatically, but if you have a custom value, you need to change it manually.
The same applies to all the actual printed texts, all default values are automatically migrated,
but if you have custom configurations, you need to change them manually to the new message parser format.

<div class="grid" markdown>

```YAML title="Old config.yml"
conversation:
  io:
    menu:
      line_length: 50
      npc_text_reset: '&f'
      option_text_reset: '&b'
      option_selected_reset: '&f'
      # More settings...
      npc_text: '&l &r&f{npc_text}'
      # More settings...
```

```YAML title="New config.yml"
conversation:
  io:
    menu:
      line_length: 320
      # More settings...
      npc_text: '@[minimessage] <white>{npc_text}'
      # More settings...
```

</div>
