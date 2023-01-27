---
icon: material/upload
---
This guide explains how to migrate from BetonQuest 1.12.X or any BetonQuest 2.0.0 dev build to the latest BetonQuest
2.0.0 dev build.
The migration must be done manually. This will not change while BQ 2.0 is in development!

!!! warning 
    Before you start migrating, you should **backup your server**!

## Changes

Skip to the first version that is newer than the version that you're migrating from:

- [2.0.0-DEV-98 - RPGMenu Merge](#200-dev-98-rpgmenu-merge)
- [2.0.0-DEV-238 - Package Structure Rework](#200-dev-238-package-structure-rework)
- [2.0.0-DEV-337 - Event Scheduling Rework](#200-dev-337-event-scheduling-rework)
- [2.0.0-DEV-450 - Package section](#200-dev-450-package-section)
- [2.0.0-DEV-485 - Experience changes](#200-dev-485-experience-changes)
- [2.0.0-DEV-538 - Smelt Objective](#200-dev-538-smelt-objective)
- [2.0.0-DEV-539 - NPC Holograms](#200-dev-539-npc-holograms)

### 2.0.0-DEV-98 - RPGMenu Merge

All existing RPGMenu users must update their RPGMenu config file. Simply rename it from `rpgmenu.config.yml` to
`menuConfig.yml`.

### 2.0.0-DEV-238 - Package Structure Rework

- Ensure your server is running on **Java 17**
- Move your current Quests to the folder "_BetonQuest/QuestPackages_"`, as quests are now loaded from there
- Rename all "_main.yml_" files to "_package.yml_"
- Quest packages can now contain nested quest packages in sub folders. You can also have any file and folder structure
  with any file and folder names you want. Only the "_package.yml_" is reserved as indicator for a [quest
  package](../../Scripting/Packages-&-Templates.md).
  * Therefore, the "_events.yml_`, "_objectives.yml_", "_conditions.yml_", "_journal.yml_" and "_items.yml_" files must
    be updated to the following format:
    Every type that was previously a separate file with a special name is now identified by a "parent-section". It's
    the names of the type / the name the file previously had. Let's take a look at an example for events and conditions:
  
    !!! info "Example"
        === "Old Way"
            ``` YAML title="events.yml"
            myEvent: "teleport 1;2;3;world"
            myOtherEvent: "point level 1"
            ```
            ``` YAML title="conditions.yml"
            myCondition: "location 300;200;300;world"
            ```
        === "New Way"
            ``` YAML title="events.yml"
            events:
              myEvent: "teleport 1;2;3;world"
              myOtherEvent: "point level 1"
            ```
            ``` YAML title="conditions.yml"
            conditions:
              myCondition: "location 300;200;300;world"
            ```
            This allows you to freely name the files. Also, it is no longer necessary that events, conditions etc. are in separate files.
            You could also put everything in a single file or use any other file structure:
            ``` YAML title="anyFileName.yml"
            events:
              myEvent: "teleport 1;2;3;world"
              myOtherEvent: "point level 1"
            conditions:
              myCondition: "location 300;200;300;world"
            ```
    !!! warning 
        You must do this change for all types, not just events and conditions! 

- Alongside the previous change, **conversations** and **menus** must also be updated to the following format:
  Add an extra prefix matching their type and the file name:

    !!! info "Example"
        === "Old Syntax" 
            ``` YAML title="lisa.yml" 
            quester: Lisa
            first: option1, option2
            NPC_options:
              option1:
              # ...
            ```
        === "New Syntax"
            ``` YAML title="anyFileName.yml"
            conversations:
              lisa: #(1)!
                quester: Lisa
                first: option1, option2
                NPC_options:
                  option1:
                  # ...
            ```

            1. This key is now the conversation name that you must refer to when linking NPCs to conversations. 

            Or alternatively:
            ``` YAML
            conversations.lisa:
              quester: Lisa
              first: option1, option2
              NPC_options:
                option1:
                # ...
            ```

### 2.0.0-DEV-337 - Event Scheduling Rework

- All your static events need to be converted to the new scheduling system.
  The [`realtime-daily`](../../Scripting/Schedules.md#daily-realtime-schedule-realtime-daily) schedule makes this easy:
  
    !!! info "Example"
            ```YAML title="Old Syntax"
            static:
              '09:00': beton
              '11:23': some_command,command_announcement
            ```
            ```YAML title="New Syntax"
            schedules:
              betonAt09: #(1)!
                type: realtime-daily #(2)!
                time: '09:00' #(3)!
                events: beton #(4)!
              cmdAt1123:
                type: realtime-daily
                time: '11:23'
                events: some_command,command_announcement
            ```
  
            1. A name for the new schedule.  
              Can be anything you want for organizing your schedules.
        
            2. The type schedule `realtime-daily` was created for easy updating.   
              It behaves just like the old static events.
        
            3. The former key is now the time value.  
              You still have to put it in 'quotes'.
        
            4. The former value is now the events value.

### 2.0.0-DEV-450 - Package Section

- There is now a new section `package` for organizing package related settings.
  As a result of this the `enabled` boolean was moved to this section.
  If you use the `enabled` boolean you need to move it to the `package` section.
  
    !!! info "Example"
            ```YAML title="Old Syntax"
            enabled: false
            ```
            
            ```YAML title="New Syntax"
            package:
              enabled: false
            ```

### 2.0.0-DEV-485 - Experience changes

Due to a misuse, all code regarding player experience (`experience` event, condition and objective) has been changed.
It is not possible to obtain the amount of experience points a player has, only their level can be obtained.  
If you used these you might have to adjust the configured values because the behaviour changed as follows:

- The `experience` objective and condition do not allow raw experience anymore. Only levels are supported from now on.
- The `experience` objective, condition and event now supports decimal numbers.  
  For example, you can use `experience 1.5` to check for one and a half level.  
  You can convert raw experience points to levels, using such decimal numbers.

### 2.0.0-DEV-538 - Smelt Objective

The `smelt` objective now requires a [quest item](./Items.md) instead of a BlockSelector.
Therefore, you now need to define the item you want to smelt in the items section.
It is recommended to use the `/q item packageName.ItemName` command to save the target item from in-game. This will save the
item you currently hold in your hand to the given package with the given name.
After you did this, you need to replace the BlockSelector in the `smelt` objective with the item's name. 

### 2.0.0-DEV-539 - NPC Holograms

Holograms were reworked. Mainly three things have to be changed:

- The `vector` is now above the head of the NPC by default. This was previously achieved with `0;3;0`. Therefore, every hologram that has defined a vector is now three blocks higher than you want. If you have `0;3;0` you can just delete the vector argument. If you have another value you need to subtract `3` from the y-axis.
- The `follow` boolean can now be set for each NPC Hologram, so you have to add it to each NPC Hologram. It's off by default. Don't add it to still-standing NPCs. This will save you a lot of performance.
- The `check_interval` can now be set for each NPC Hologram as well. This allows for finer control over how much server resources are used.


<div class="grid" markdown>

```YAML title="Old Syntax"
npc_holograms:
  check_interval: 100
  follow: true
  default:
    lines:
      - "Some text!"
    conditions: "has_some_quest"
    vector: 0;3;0
    npcs:
      - 0
      - 22
```

```YAML title="New Syntax"
npc_holograms:
  default:
    lines:
      - "Some text!"
    conditions: "has_some_quest"
    vector: 0;0;0 #(1)!
    check_interval: 100 #(2)!
    follow: true #(3)!
    npcs:
      - 0
      - 22
```

1. You can delete this if you had `0;3;0` previously as the origin was changed. Subtract 3 from the y-axis for any other value.
2. You can delete this if you had the default value of `100` (or whatever you set in "_config.yml_").
3. You can delete this if you had the default value of `false`.
</div>
