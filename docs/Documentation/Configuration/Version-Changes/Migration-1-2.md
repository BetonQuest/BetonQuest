---
icon: material/upload
---
This guide explains how to migrate from BetonQuest 1.12.X or any BetonQuest 2.0.0 dev build to BetonQuest
2.0.0.

**The majority of changes will be migrated automatically. However, some things must be migrated manually.**

The migration will first find any 1.12 packages in the BetonQuest plugin folder and migrate them to the 2.0 package
format. It will then place them inside the newly introduced QuestPackages folder. Then it updates everything inside the
QuestPackages folder to the new 2.0 syntax. This way the migration works for both 1.12 and 2.0.0-DEV packages.

!!! warning 
    Before you start migrating, you should **backup your server**!

## Changes

Steps marked with :gear: are migrated automatically. Steps marked with :exclamation: must be done manually.

- [2.0.0-DEV-87 - Rename to `ride`](#200-dev-87-rename-to-ride) :gear:
- [2.0.0-DEV-98 - RPGMenu Merge](#200-dev-98-rpgmenu-merge) :gear:
- [2.0.0-DEV-238 - Package Structure Rework](#200-dev-238-package-structure-rework) :gear:
- [2.0.0-DEV-337 - Event Scheduling Rework](#200-dev-337-event-scheduling-rework) :gear:
- [2.0.0-DEV-450 - Package section](#200-dev-450-package-section) :gear:
- [2.0.0-DEV-485 - Experience changes](#200-dev-485-experience-changes) :exclamation:
- [2.0.0-DEV-538 - Smelt Objective](#200-dev-538-smelt-objective) :exclamation:
- [2.0.0-DEV-539 - NPC Holograms](#200-dev-539-npc-and-non-npc-holograms) :exclamation:
- [2.0.0-DEV-644 - Database migration for profiles](#200-dev-644-database-migration-for-profiles) :gear:
- [2.0.0-DEV-647 - EffectLib](#200-dev-647-effectlib) :gear:
- [2.0.0-DEV-674 - MMO Updates](#200-dev-674-mmo-updates) :gear:
- [2.0.0-DEV-749 - Static Event Rework](#200-dev-749-static-event-rework) :gear:
- [2.0.0-DEV-769 - RemoveEntity-Event](#200-dev-769-removeentity-event) :gear:
- [2.1.0-DEV-1 - Instruction Quoting](#210-dev-1-instruction-quoting) :exclamation:
- [2.1.1-DEV-2 - Rename AuraSkills](#211-dev-2-rename-auraskills) :gear:
- [2.2.0-DEV-89 - Rename Fabled](#220-dev-90-rename-fabled) :gear:

### 2.0.0-DEV-87 - Rename to `ride` :gear:

??? info "Automated Migration"
    *The migration is automated. You shouldn't have to do anything.*
    
    -------------

    To unify the naming for riding a vehicle, we renamed the condition (`riding`) and the objective (`vehicle`)
    to `ride`.
    
    <div class="grid" markdown>
    
    ```YAML title="Old Syntax (conditions.yml)"
    rideHorse: riding Horse
    ```
    
    ```YAML title="New Syntax (conditions.yml)"
    rideHorse: ride Horse
    ```
    
    </div>
    
    <div class="grid" markdown>
    
    ```YAML title="Old Syntax (objectives.yml)"
    rideHorse: vehicle Horse events:teleport
    ```
    
    ```YAML title="New Syntax (objectives.yml)"
    rideHorse: ride Horse events:teleport
    ```
    
    </div>

### 2.0.0-DEV-98 - RPGMenu Merge :gear:

??? info "Automated Migration"
    *The migration is automated. You shouldn't have to do anything.*
    
    -------------

    All existing RPGMenu users must update their RPGMenu config file. Simply rename it from `rpgmenu.config.yml` to
    `menuConfig.yml`.

### 2.0.0-DEV-238 - Package Structure Rework :gear:

??? info "Automated Migration"
    *The migration is automated. You shouldn't have to do anything.*
    
    -------------
    
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
      
        <div class="grid" markdown>
        
        ``` YAML title="Old events.yml"
        myEvent: "teleport 1;2;3;world"
        myOtherEvent: "point level 1"
        ```
        
        ``` YAML title="New events.yml"
        events:
          myEvent: "teleport 1;2;3;world"
          myOtherEvent: "point level 1"
        ```
        
        ``` YAML title="old conditions.yml"
        myCondition: "location 300;200;300;world"
        ```
        
        ``` YAML title="New conditions.yml"
        conditions:
          myCondition: "location 300;200;300;world"
        ```
        
        </div>
        
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
      
        <div class="grid" markdown>
        
        <div>
      
        ``` YAML title="Old lisa.yml" 
        quester: Lisa
        first: option1, option2
        NPC_options:
          option1:
          # ...
        ```
        
        </div>
        <div>
        
        ``` YAML title="New anyFileName.yml"
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
        ``` YAML title="New anyFileName.yml"
        conversations.lisa:
          quester: Lisa
          first: option1, option2
          NPC_options:
            option1:
            # ...
        ```
        
        </div>
        
        </div>

### 2.0.0-DEV-337 - Event Scheduling Rework :gear:

??? info "Automated Migration"
    *The migration is automated. You shouldn't have to do anything.*
    
    -------------
    
    - All your static events need to be converted to the new scheduling system.
      The [`realtime-daily`](../../Scripting/Schedules.md#daily-realtime-schedule-realtime-daily) schedule makes this easy:
      
      <div class="grid" markdown>
      
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
      
      </div>

### 2.0.0-DEV-450 - Package Section :gear:

??? info "Automated Migration"
    *The migration is automated. You shouldn't have to do anything.*
    
    -------------
    
    - There is now a new section `package` for organizing package related settings.
      As a result of this the `enabled` boolean was moved to this section.
      If you use the `enabled` boolean you need to move it to the `package` section.
      
      <div class="grid" markdown>
      
      ```YAML title="Old Syntax"
      enabled: false
      ```
      
      ```YAML title="New Syntax"
      package:
        enabled: false
      ```
      
      </div>
    
### 2.0.0-DEV-485 - Experience changes :exclamation:

Due to a misuse of the Spigot API, all code regarding player experience (`experience` event, condition and objective) had to be changed.
It is not possible to obtain the amount of experience points a player has, only their level can be obtained.
  
If you used any of these elements you might have to adjust the configured values because the behaviour changed as follows:

- The `experience` objective and condition do not allow raw experience anymore. Only levels are supported from now on.
- The `experience` objective, condition and event now supports decimal numbers.  
  For example, you can use `experience 1.5` to check for one and a half level.  
  You can convert raw experience points to levels, using such decimal numbers.

### 2.0.0-DEV-538 - Smelt Objective :exclamation:

The `smelt` objective now requires a [quest item](../../Features/Items.md) instead of a BlockSelector.
Therefore, you now need to define the item you want to smelt in the items section.
It is recommended to use the `/q item packageName.ItemName` command to save the target item from in-game. This will save the
item you currently hold in your hand to the given package with the given name.
After you did this, you need to replace the BlockSelector in the `smelt` objective with the item's name. 

### 2.0.0-DEV-539 - NPC and Non-NPC Holograms :exclamation:

!!! warning "Potentially Faulty Automatic Migration"
    In some cases this migration will not be run although the configuration is in the 1.12 format. 
    This is the case because it is not possible to reliably detect if the configuration must be migrated - it simply has not changed enough.  
    If the (NPC-)hologram section does NOT contain one of the following keys the migration will NOT be run: 
      
      * `follow`
      * `check_interval`  

    If this is the case you need to update the configration manually. See the steps below.

Both NPC and Non-NPC Holograms were reworked. Mainly three things have to be changed:

- The `vector` is now above the head of the NPC by default. This was previously achieved with `0;3;0`. Therefore, every hologram that has defined a vector is now three blocks higher than before. If it is set to `0;3;0` delete the vector argument. Otherwise, subtract `3` from the y-axis.
- The `follow` boolean can now be set for each NPC Hologram, so you have to add it to each NPC Hologram. It's off by default. Don't add it to still-standing NPCs. This will save you a lot of performance.
- The `check_interval` can now be set for each NPC Hologram as well. This allows for finer control over how much server resources are used.


<div class="grid" markdown>

```YAML title="Old Syntax"
npc_holograms:
  check_interval: 200
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
    check_interval: 200 #(2)!
    follow: true #(3)!
    npcs:
      - 0
      - 22
```

1. You can delete this if you had `0;3;0` previously as the origin was changed. Subtract 3 from the y-axis for any other value.
2. You can delete this if you had the default value of `200` (or whatever you set in "_config.yml_").
3. You can delete this if you had the default value of `false`.
</div>

### 2.0.0-DEV-644 - Database migration for profiles :gear:

??? info "Automated Migration"
    *The migration is automated. You shouldn't have to do anything.*
    
    -------------

    The database migrated to a new format for profiles and every profile will have a name. You can set a initial creation
    name in your config.yml, so every new generated profile (through migration or joining of a new player) will get this name. 
    If you don't set a initial name, the initial name will be "default".
    
    ```YAML title="config.yml"
    profiles:
      initial_name: player # (1)!
    ```
    
    1. Only set this if you want to change the initial name. If you don't set this, the initial name will be "default".

### 2.0.0-DEV-647 - EffectLib :gear:

??? info "Automated Migration"
    *The migration is automated. You shouldn't have to do anything.*
    
    -------------

    The EffectLib integration was rewritten. With this rewrite, the following things are now possible:
    
    - NPC effects will now move with the NPC
    - Effects can be assigned to locations
    
    The following changes need to be done:
    
    - Rename `npc_effects` to `effectlib`.
    - Add `pitch: -90` to preserve the old rotation to NPC effects.
    - Remove `check_interval` from the npc_effects section
    - Remove `disabled` from the npc_effects section
    
    <div class="grid" markdown>
    
    ```YAML title="Old Syntax"
    npc_effects:
       check_interval: 50
       disabled: false
       farmer:
          class: VortexEffect
          iterations: 20
          particle: crit_magic
          helixes: 3
          circles: 1
          grow: 0.1
          radius: 0.5
          interval: 30
          npcs:
             - 1
          conditions:
             - '!con_tag_started'
             - '!con_tag_finished'
    ```
    
    ```YAML title="New Syntax"
    effectlib: 
       farmer: 
          class: VortexEffect 
          iterations: 20 
          particle: crit_magic 
          helixes: 3
          circles: 1
          grow: 0.1
          radius: 0.5
          pitch: -60 #(4)!
          yaw: 90
          interval: 30 #(1)!
          checkinterval: 80 #(2)!
          npcs: #(3)!
             - 1 
          locations:
             - 171;72;-127;world
          conditions: 
             - '!con_tag_started'
             - '!con_tag_finished'
    
    ```
    
    1. This field is optional. You can delete this if you had the default value of `100`.
    2. This field is new and optional. It replaces the old `check_interval` field. You can delete this if you had the default value of `100`.
    3. In case you never had the `npcs` field and the effect were played on every npc in the package,
    you now need to add the `npcs` section with every npc the effect should be played at.
    4. In case you never had the `pitch` field, you need to use the default value of `-90`.
    </div>

### 2.0.0-DEV-674 - MMO Updates :gear:

??? info "Automated Migration"
    *The migration is automated. You shouldn't have to do anything.*
    
    -------------

    A change related to the integration of the MMO suite by Phoenix Development was made. 
    The objectives `mmocorecastskill` and `mmoitemcastability` were merged into the `mmoskill` objective.
    The `mmoskill` objective works exactly like its predecessors, but also supports defining one or more trigger types.
    
    See the [objective's documentation](../../Scripting/Building-Blocks/Integration-List.md#activate-mythiclib-skill-mmoskill) for more information.
    
    <div class="grid" markdown>
    
    ```YAML title="Old Syntax"
    castMMOCoreSkill: "mmocorecastskill DEEP_WOUND"
    castMMOItemSkill: "mmoitemcastability DEEP_WOUND"
    ```
    
    ```YAML title="New Syntax"
    castMMOCoreSkill: "mmoskill DEEP_WOUND trigger:CAST"
    castMMOItemSkill: "mmoskill DEEP_WOUND trigger:RIGHT_CLICK"
    ```
    
    </div>

### 2.0.0-DEV-749 - Static Event Rework :gear:

??? info "Automated Migration"
    *The migration is automated. You shouldn't have to do anything.*
    
    -------------
    
    Until now if you used non-static events in a [schedule](../../Scripting/Schedules.md), they were executed for every player that was online at the time.  
    If you run such a schedule now you will get a warning message in the console similar to this:
    
    !!! example
        ```
        [15:27:10 WARN]: [BetonQuest] Cannot fire non-static event 'announcements.ringBell' without a player!
        ```
    
    To fix this wrap the event in a [`runForAll`](../../Scripting/Building-Blocks/Events-List.md#run-events-for-all-online-players-runforall) event:
    
    <div class="grid" markdown>
    
    ```YAML title="Old Syntax"
    events:
      # Events that require a player (non-static events).
      bell_sound: 'notify io:sound sound:block.bell.use'
      bell_ring: 'folder bell_sound,bell_sound,bell_sound,bell_sound period:0.5'
      # Player independent events (static events).
      notify_goodNight: 'notifyall &6Good night, sleep well!'
    
    
    schedules:
      sayGoodNight:
        type: realtime-daily
        time: '22:00'
        events: bell_ring,notify_goodNight
    ```
    
    ```YAML title="New Syntax"
    events:
      # Events that require a player (non-static events).
      bell_sound: 'notify io:sound sound:block.bell.use'
      bell_ring: 'folder bell_sound,bell_sound,bell_sound,bell_sound period:0.5'
      # Player independent events (static events).
      notify_goodNight: 'notifyall &6Good night, sleep well!'
      bell_ring_all: 'runForAll events:bell_ring' #(1)!
    
    schedules:
      sayGoodNight:
        type: realtime-daily
        time: '22:00'
        events: bell_ring_all,notify_goodNight #(2)!
    ```
    
    1. Runs `bell_ring` for all online players.
    2. `notify_goodNight` is a [static event](../../Scripting/Schedules.md#player-independent-events), so no need to wrap it in `runForAll`.
    
    </div>
    
    While this seems like more work for the same functionality it gives you more control over how events are run.  
    **With this change we finally allow using conditions in schedules!**  
    Just keep in mind you can only add player dependent conditions if the event is run player dependent (wrapped inside `runForAll`).
    
    !!! tip
        To check if your event still works inside a schedule or if it must be wrapped,
        use the following command to run the event without a player:
         
        ```
        /q event - <package>.<event>
        ```
        The `-` is important, it means run independent :wink:.

### 2.0.0-DEV-769 - RemoveEntity-Event :gear:

??? info "Automated Migration"
    *The migration is automated. You shouldn't have to do anything.*
    
    -------------
    
    As you probably noticed, the `ClearEntity` event and the `KillMob` event did almost the same thing.
    Both got merged into the [RemoveEntity event](../../Scripting/Building-Blocks/Events-List.md#remove-entity-removeentity),
    while keeping the syntax more or less the same.
    
    <div class="grid" markdown>
    
    ```YAML title="Old Syntax"
    events:
      clearArea: 'clear ZOMBIE,CREEPER 100;200;300;world 10 name:Monster'
      killBolec: 'killmob ZOMBIE 100;200;300;world 40 name:Bolec'
    ```
    
    ```YAML title="New Syntax"
    events:
      clearArea: 'removeentity ZOMBIE,CREEPER 100;200;300;world 10 name:Monster'
      killBolec: 'removeentity ZOMBIE 100;200;300;world 40 name:Bolec kill'
    ```
    
    </div>

### 2.1.0-DEV-1 - Instruction Quoting :exclamation:

BetonQuest had quoting support since November 2018, but unfortunately it was broken from the very start and also never properly documented.

You probably don't need to change anything, but it is recommended to read how
[quoting](../../Scripting/Quoting-&-YAML.md#quoting) works.

If you are facing errors for instructions containing the double quote character `"` then you might need to escape them:

<div class="grid" markdown>

```YAML title="Old Syntax"
events:
  literal: notify "special\secret"message #(1)!
  quoted: notify this" was quoted" previously #(2)!
  parameter: tag add x condition:"with space"
conditions:
  "with space": tag "other tag"
```

1. Output: "special\secret"message
2. Output: this was quoted previously

```YAML title="New Syntax"
events:
  literal: notify "\"special\\secret\"message" #(1)!
  quoted: notify "this was quoted previously" #(2)!
  parameter: tag add x "condition:with space" #(3)!
conditions:
  "with space": tag "other tag"
```

1. When quoting add `\` before every `"` and already existing `\`.
2. Do not quote only a part of a message, but instead the full message.
3. Move the quote before the parameter name.

</div>

### 2.1.1-DEV-2 - Rename AuraSkills :gear:

??? info "Automated Migration"
    *The migration is automated. You shouldn't have to do anything.*
    
    -------------
    
    AureliumSkills was renamed to AuraSkills, so all conditions and events where renamed as well.
    
    <div class="grid" markdown>
    
    ```YAML title="Old Syntax"
    conditions:
      skillLevel: aureliumskillslevel fighting 5
      statLevel: aureliumstatslevel luck 5
    events:
      giveSkillXP: aureliumskillsxp farming 5
    ```
    
    ```YAML title="New Syntax"
    conditions:
      skillLevel: auraskillslevel fighting 5
      statLevel: auraskillsstatslevel luck 5
    events:
      giveSkillXP: auraskillsxp farming 5
    ```
    
    </div>

### 2.2.0-DEV-90 - Rename Fabled :gear:

??? info "Automated Migration"
    *The migration is automated. You shouldn't have to do anything.*
    
    -------------
    
    ProSkillAPI was renamed to Fabled, so all conditions were renamed as well.
    
    <div class="grid" markdown>
    
    ```YAML title="Old Syntax"
    conditions:
      class: skillapiclass warrior
      level: skillapilevel warrior 3
    ```
    
    ```YAML title="New Syntax"
    conditions:
      class: fabledclass warrior
      level: fabledlevel warrior 3
    ```
    
    </div>
