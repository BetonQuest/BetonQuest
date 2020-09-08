# Compatibility
In total 28 plugins have dedicated support for BetonQuest.

BetonQuest can hook into other plugins to extend its functionality. Currently, 23 plugins are supported:  
Brewery, Citizens, Denizen, EffectLib, Heroes, HolographicDisplays, JobsReborn, Magic,
mcMMO, MMOLib, MMOCore, MMOItems, MythicMobs, PlaceholderAPI, PlayerPoints, ProtocolLib, Quests,
Shopkeepers, SkillAPI, Skript, Vault, WorldEdit and WorldGuard.

Some plugins also hook into BetonQuest and provide support by themselves:  
[nuNPCDestinations](https://www.spigotmc.org/resources/npcdestinations-create-living-npcs.13863/),
[RPGMenu](https://www.spigotmc.org/resources/rpgmenu.55170/),
[CalebCompass](https://www.spigotmc.org/resources/caleb-compass.82674/)

There are also plugins that hook into BetonQuest that require a clientside mod:
[BetonQuestGUI](https://github.com/giovanni-bozzano/betonquest-gui-plugin),
[NGVexJournal](https://www.spigotmc.org/resources/ngvexjournal-multi-function-betonquest-journal-gui-1-7-10-1-13.76938/)


## [Brewery](https://www.spigotmc.org/resources/brewery.3082/)

### Conditions

#### Drunk: `drunk`
This condition is true if the player is drunken. Only argument is the minimal drunkness (0-100).

``` YAML linenums="1"
drunk 50
```

#### Drunk Quality: `drunkquality`
This condition is true if the player has the given drunk quality. Only argument is the minimal drunk quality (1-10).

``` YAML linenums="1"
drunkquality 3
```

#### Has Brew: `hasbrew`
This condition is true if the player has the given grew with the specified amount in his inventory.

``` YAML linenums="1"
hasbrew MY_BREW 2
```

### Events

#### Give Brew: `givebrew`
Gives the player the specified drink. An amount needs to be specified.

``` YAML linenums="1"
givebrew MY_BREW 2
```

#### Take Brew: `takebrew`
Removes the specified drink from the players inventory. An amount needs to be specified.

``` YAML linenums="1"
takebrew MY_OTHER_BREW 2
```

## NPC's with [Citizens](http://dev.bukkit.org/bukkit-plugins/citizens/)

If you have this plugin you can use it's NPCs for conversations. I highly recommend you installing it,
it's NPCs are way more immersive. Having Citizens also allows you to use NPCKill objective and to have moving NPC's.

!!! notice
    When you use Citizens, in main.yml you need to specify the ID of the NPC instead of the name!

### Conditions

#### NPC distance: `npcdistance`

This condition will return true if the player is closer to the NPC with the given ID than the given distance.
 The NPCs ID is the first argument, the distance is the second. If the npc is despawned the condition will return false.

!!! example
    ```YAML
    npcdistance 16 22
    ```

#### NPC location: `npclocation`

**persistent**, **static**

This condition will return true if a npc is close to a location. First argument is the id of the NPC, second the location and third the maximum distance to the location that the npc is allowed to have.

!!! example
    ```YAML
    npclocation 16 4.0;14.0;-20.0;world 22
    ```

#### NPC region: `npcregion`

**persistent**, **static**

!!! notice
    This condition also requires WorldGuard to work.

This condition will return true if a npc is inside a region. First argument is the id of the npc second is the name of the region.

!!! example
    ```YAML
    npcregion 16 spawn`
    ```

### Events

#### Move NPC: `movenpc`

This event will make the NPC move to a specified location. It will not return on its own,
so you have to set a single path point with _/npc path_ command - it will then return to that point every time.
If you make it move too far away, it will teleport or break, so beware. You can change maximum pathfinding range in Citizens
configuration files. The first argument in this event is ID of the NPC to move. Second one is a location in a standard format (like in `teleport` event).
You can also specify multiple locations separated by colons to let the npc follow a path of locations.
You can also specify additional arguments: `block` will block the NPC so you won't be able to start a conversation with him while he is moving,
`wait:` is a number of tick the NPC will wait at its destination before firing events,
`done:` is a list of events fired after reaching the destination, `fail:` is a list of events fired if this event fails.
Move event can fail if the NPC is already moving for another player.

!!! example
    ```YAML
    movenpc 121 100;200;300;world,105;200;280;world block wait:20 done:msg_were_here,give_reward fail:msg_cant_go,give_reward
    ```

#### Stop moving NPC: `stopnpc`

This will stop all current move tasks for the npc with the given ID.

!!! example
    ```YAML
    stopnpc 16
    ```
    
#### Teleport NPC: `teleportnpc`

This event will teleport the NPC with the given ID to the given location.

!!! example
    ```YAML
    teleportnpc 53 100;200;300;world
    ```

### Objectives

#### NPC Interact: `npcinteract`

The player has to right-click on the NPC with specified ID. It can also optionally cancel the action, so the conversation won't start.
The first argument is number (ID of the NPC), and the second is optional `cancel`.

!!! example
    ```YAML
    npcinteract 3 cancel conditions:sneak events:steal`
    ```

#### NPC Kill: `npckill`

NPC Kill objective requires the player to kill an NPC with the given ID. You can also define how many times an NPC has to be killed. Right after objective's name there must be na ID of the NPC. You can also add an amount by `amount:`.

!!! example
    ```YAML
    npckill 16 amount:3 events:reward`
    ```

#### NPC Range: `npcrange`

The player has to enter/leave a circle with the given radius around the NPC to complete this objective. First argument is the ID of the NPC, second one is either `enter` or `leave` and the third one is the range.

!!! example
    ```YAML
    npcrange 3 enter 20 events:master_inRange
    ```

## [Denizen](http://dev.bukkit.org/bukkit-plugins/denizen/)

### Events

#### Script: `script`

With this event you can fire Denizen task scripts. Don't confuse it with `skript` event, these are different. The first and only argument is the name of the script.

!!! example
    ```YAML
    script beton
    ```

## [EffectLib](http://dev.bukkit.org/bukkit-plugins/effectlib/)

If you install this plugin on your server you will be able to set a particle effect on NPCs with conversations and use `particle` event.

You can control the behaviour of particles around the NPCs in _custom.yml_ file, in `npc_effects` section. Each effect is defined as a separate subsection and consists of EffectLib options (described on the EffectLib page) and several BetonQuest settings. `npcs` is a list of all NPCs on which this effect can be displayed. `conditions` is a list of conditions the player has to meet in order to see the effect. BetonQuest will find the first effect which can be displayed and show it to the player. `interval` controls how often the effect is displayed (in ticks). The effect will be fired from the exact location of the NPC, upwards.

```YAML
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

### Events

#### Particle: `particle`

This event will load an effect defined in `effects` section in
_custom.yml_ file and display it on player's location. The only argument
is the name of the effect. You can optionally add `loc:` argument
followed by a location written like `100;200;300;world;180;-90` to put
it on that location. If you add `private` argument the effect will only
be displayed to the player for which you ran the event.

!!! example
    **In custom.yml**
    ```YAML
    effects:
      beton:
        class: HelixEffect
        iterations: 100
        particle: smoke
        helixes: 5
        circles: 20
        grow: 3
        radius: 30
    ```
    
    **In events.yml**
    ```YAML
    particle beton loc:100;200;300;world;180;-90 private`
    ```

## [Heroes](http://dev.bukkit.org/bukkit-plugins/heroes/)

When you install Heroes, all kills done via this plugin's skills will be counted in MobKill objectives.

### Conditions

#### Class: `heroesclass`

This condition checks the classes of the player. The first argument must be `primary`, `secondary` or `mastered`. Second is the name of a class or `any`. You can optionally specify `level:` argument followed by the required level of the player.

!!! example
    ```YAML
    heroesclass mastered warrior
    ```

#### Skill: `heroesskill`

This condition checks if the player can use specified skill. The first argument is the name of the skill.

!!! example
    ```YAML
    heroesskill charge`
    ```

### Events

#### Experience: `heroesexp`

This event simply gives the player specified amount of Heroes experience. The first argument is either `primary` or `secondary` and it means player's class. Second one is the amount of experience to add.

!!! example
    ```YAML
    heroesexp primary 1000
    ```

## [HolographicDisplays](http://dev.bukkit.org/bukkit-plugins/holographic-displays/)

Installing this plugin will enable you to create hidden holograms, which will be shown to players only if they meet specified conditions. Note that you need to have [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/) installed in order to hide holograms from certain players.

In order to create a hologram, you have to add `holograms` section in your _custom.yml_ file. Add a node named as your hologram to this section and define `lines`, `conditions` and `location` subnodes. The fist one should be a list of texts - these will be the lines of a hologram. Color codes are supported. Second is a list of conditions separated by commas. Third is a location in a standard format, like in `teleport` event. An example of such hologram definition:

```YAML
holograms:
  beton:
    lines:
    - '&bThis is Beton.'
    - 'item:custom_item'
    - '&eBeton is strong.'
    location: 100;200;300;world
    conditions: has_some_quest, !finished_some_quest
    # How often to check conditions (optional)
    check_interval: 20
```

A line can also represent a floating item. To do so enter the line as 'item:`custom_item`'. It will be replaced with the `custom_item` defined defined in items.yml. If the Item is defined for example as map, a floating map will be seen between two lines of text.

The holograms are updated every 10 seconds. If you want to make it faster, add `hologram_update_interval` option in _config.yml_ file and set it to a number of ticks you want to pass between updates (one second is 20 ticks). Don't set it to 0 or negative numbers, it will result in an error.

If Citizens is also installed then you can have holograms configured relative to an npc. Add the following to _custom.yml_.

```YAML
npc_holograms:
  # How often to check conditions
  check_interval: 100
  
  # Holograms follow npcs when they move (higher cpu usage when true)
  follow: false

  # Disable npc_holograms
  disabled: false

  # Hologram Settings
  default:
    # Lines in hologram
    lines:
      - "Some text!"
    # Vector offset to NPC position to place hologram
    vector: 0;3;0

    # Conditions to display hologram
    conditions: has_some_quest, !finished_some_quest

    # NPC's to apply these settings to. If blank, applies by default
    npcs:
      - 0
      - 22
```

Item lines are also supported here.

## [JobsReborn](https://www.spigotmc.org/resources/jobs-reborn.4216/)

Requires adding the following to _config.yml_:
```YAML
hook:
  jobs: 'true'
```

### Conditions

#### Can Level up: `nujobs_canlevel {jobname}`

Returns true if the player can level up

#### Has Job: `nujobs_hasjob {jobname}`

Returns true if the player has this job

!!! example
    ```YAML
    nujobs_hasjob Woodcutter
    ```

#### Job Full: `nujobs_jobfull {jobname}`

Returns true if the job is at the maximum slots

#### Job Level: `nujobs_joblevel {jobname} {min} {max}`

Returns true if the player has this job, and at a level equal to or between the min/max

!!! example
    ```YAML
    nujobs_joblevel Woodcutter 5 10
    ```

### Events

#### Add Experience: `nujobs_addexp {jobname} {exp}`

Gives the player experience

#### Increase Level: `nujobs_addlevel {jobname} {amount}`

Increases the player level by amount.

#### Decrease Level: `nujobs_dellevel {jobname} {amount}`

Decreases the players level by amount.

#### Join Job: `nujobs_joinjob {jobname}`

Joins the player to job.

#### Leave Job: `nujobs_leavejob {jobname}`

Removes the player from job.

#### Set Level: `nujobs_setlevel {jobname} {level}`

Set the player to level.

### Objectives

#### Join Job: `nujobs_joinjob {jobname}`

Triggers when player joins job.

#### Leave Job: `nujobs_leavejob {jobname}`

Triggers when player leaves job.

!!! notice
    This is not triggered by '/jobs leaveall'

#### Job Levelup: `nujobs_levelup {jobname}`

Triggers when player levels up.

#### Job Payment: `nujobs_payment {amount}`

Triggers when player makes {amount} of money from jobs.


## [Magic](http://dev.bukkit.org/bukkit-plugins/magic/)

### Conditions

#### Wand: `wand`

This condition can check wands. The first argument is either `hand`, `inventory` or `lost`. If you choose `lost`, the condition will check if the player has lost a wand. If you choose `hand`, the condition will check if you're holding a wand in your hand. `inventory` will check your whole inventory instead of just the hand. In case of `hand` and `inventory` arguments you can also add optional `name:` argument followed by the name of the wand (as defined in _wands.yml_ in Magic plugin) to check if it's the specific type of the wand. In the case of `inventory` you can specify an amount with `amount` and this will only return true if a player has that amount. You can also use optional `spells:` argument, followed by a list of spells separated with a comma. Each spell in this list can have defined minimal level required, after a colon.

!!! example
    ```YAML
    wand hand name:master spells:flare,missile:2
    ```

## [McMMO](http://dev.bukkit.org/bukkit-plugins/mcmmo/)

### Conditions

#### Level: `mcmmolevel`

This conditions checks if the player has high enough level in the specified skill. The first argument is the name of the skill, second one is the minimum level the player needs to have to pass this condition.

!!! example
    ```YAML
    mcmmolevel woodcutting 50
    ```

### Events

#### Experience: `mcmmoexp`

This event adds experience points in a specified skill. The first argument is the name of the skill, second one is the amount of experience to add.

!!! example
    ```YAML
    mcmmoexp swords 1500
    ```

## MMOGroup ([MMOCore](https://www.spigotmc.org/resources/mmocore.70575/), [MMOItem](https://www.spigotmc.org/resources/mmoitems-premium.39267/), [MMOLib](https://www.spigotmc.org/resources/mmolib.73855/))

### Conditions

#### MMOCore class: `mmoclass` 
Checks if a player has the given MMOCore class. If a level has been specified the player needs to be on that level or higher to meet the condition.
You can disable this behaviour by adding the `equal` argument. 
```YAML linenums="1"
mmoclass WARRIOR
mmoclass MAGE 5
mmoclass MAGE 5 equal
```

#### MMOCore attribute: `mmoattribute`
Checks if a player has the specified attribute on the given level or higher.
You can disable this behaviour by adding the `equal` argument. 
```YAML linenums="1"
mmoclass mmoattribute strength 2 
mmoclass mmoattribute strength 2 equal
```

#### MMOCore profession: `mmoprofession`
Checks if a player has the specified profession on the given level or higher.
You can disable this behaviour by adding the `equal` argument. 
```YAML linenums="1"
mmoprofession mining 2 
mmoprofession mining 2 equal
```

#### MMOItems item: `mmoitem`
Checks if a player has the specified MMOItem in his inventory. If no amount has been defined the default amount is one.
```YAML linenums="1"
mmoitem ARMOR SKELETON_CROWN
mmoitem GEMS SPEED_GEM 3
```

#### MMOItems hand: `mmohand`
Checks if a player holds the specified MMOItem in his hand. Checks the main hand if not specified otherwise using the `offhand` argument.
If no amount has been defined the default amount is one.
```YAML linenums="1"
mmohand ARMOR SKELETON_CROWN
mmohand GEMS SPEED_GEM 3 offhand
```

#### MMOLib stat: `mmostat`
Checks <a href="https://github.com/mmopluginteam/mmolib/blob/master/src/main/java/net/mmogroup/mmolib/api/stat/SharedStat.java" target="_blank">these</a>
stats that combine all sorts of stats from MMOCore and MMOItems.
The player needs to be on the specified level or higher in order to meet this condition.
You can disable this behaviour by adding the `equal` argument. 
```YAML linenums="1"
mmostat DAMAGE_REDUCTION 3
```

## [MythicMobs](http://dev.bukkit.org/bukkit-plugins/mythicmobs/)

Having MythicMobs allows you to use MythicMobs MobKill objective and MythicMobs SpawnMob event.

### Objectives

#### MobKill: `mmobkill`

To complete this objective you need to kill specified amount of MythicMobs. The first argument must be the mob's internal name (the one defined in MythicMobs' configuration). You can optionally add `amount:` argument to specify how many of these mobs the player needs to kill. You can also add "notify" keyword if you want to display to players the amount of mobs left to kill.

!!! example
    ```YAML
    mmobkill SkeletalKnight amount:2 events:reward
    ```

### Conditions

#### MythicMob distance: `mythicmobdistance`

### Events

Check whether the player is near a specific MythicMobs entity. The first argument is the internal name of the mob (the one defined in MythicMobs' configuration). The second argument is the distance to check, measured in block lengths in a circular radius.

!!! example
    ```YAML
    mythicmobdistance SkeletalKnight 7
    ```

#### SpawnMob: `mspawnmob`

Spawn specified amount of MythicMobs at given location. The first argument is a location defined like `100;200;300;world`. Second is MythicMobs internal name (the one defined in MythicMobs' configuration) followed by a colon and a level. Third one is amount and it's required!

!!! example
    ```YAML
    mspawnmob 100;200;300;world SkeletalKnight:1 5
    ```

## [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)

If you have this plugin, BetonQuest will add a `betonquest` placeholder to it and you will be able to use `ph` variable in your conversations.

### Placeholder: `betonquest`

In any other plugin which uses PlaceholderAPI you can use BetonQuest variables with `%betonquest_package:variable%` placeholder. The `package:` part is the name of a package. If you skip this, the plugin will assume you're using that variable in `default` package. The `variable` part is just a BetonQuest variable without percentage characters, like `point.beton.amount`.

!!! example
    ```YAML
    %betonquest_someGreatQuest:objective.killZombies.left%
    ```

### Variable: `ph`

You can also use placeholders from other plugins in BetonQuest. Simply insert a variable starting with `ph`, the second argument should be the placeholder without percentage characters.

!!! example
    ```YAML
    %ph.player_item_in_hand%
    ```

## [PlayerPoints](http://dev.bukkit.org/bukkit-plugins/playerpoints/)

### Conditions

#### PlayerPoints: `playerpoints`

This condition simply checks if the player has specified amount of points in the PlayerPoints plugin. The only argument is a number.

!!! example
    ```YAML
    playerpoints 100
    ```
    
### Events

#### PlayerPoints: `playerpoints`

This event simply adds, removes or multiplies points in the PlayerPoints plugin. The only argument is a number, it can be positive, negative or prefixed with an asterisk for multiplication.

!!! example
    ```YAML
    playerpoints *2
    ```

## [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/)

### Hiding NPC's
Having ProtocolLib installed will let you hide Citizens NPCs if specified conditions are met. You can do that by adding `hide_npcs` section to _custom.yml_ file in your package. There you can assign conditions to specific NPC IDs:

```YAML
hide_npcs:
  41: killedAlready,questStarted
  127: '!questStarted'
```

### Conversation IO: `menu`

ProtocolLib also enables a conversation IO that makes use of a chat menu system. A video of it in action can be seen [here](../media/content/Home/MenuConvIO.mp4).

Customize how it looks by adding the following lines to custom.yml:

```YAML
menu_conv_io:
  line_length: 50
  refresh_delay: 180

  npc_wrap: '&l &r'
  npc_text: '&l &r&f{npc_text}'
  npc_text_reset: '&f'
  option_wrap: '&r&l &l &l &l &r'
  option_text: '&l &l &l &l &r&8[ &b{option_text}&8 ]'
  option_text_reset: '&b'
  option_selected: '&l &r &r&7»&r &8[ &f&n{option_text}&8 ]'
  option_selected_reset: '&f'
  option_selected_wrap: '&r&l &l &l &l &r&f&n'

  control_select: jump,left_click
  control_cancel: sneak
  control_move: scroll,move

  npc_name_type: chat
  npc_name_align: center
  npc_name_format: '&e{npc_name}&r'
```

Where:

  * `line_length` - Maximum size of a line till its wrapped
  * `refresh_delay` - Specify how many ticks to auto update display. Default 180
  * `npc_wrap` - What text to prefix each new line in the NPC text that wraps
  * `npc_text` - How to write the NPC text. Replaces {1} with the npcs text
  * `npc_text_reset` - When a color reset is found, what to replace it with
  * `option_wrap` - What text to prefix each new line in an option that wraps
  * `option_text` - How to write an option. Replaces {1} with the option text
  * `option_text_reset` - When a color reset is found, what to replace it with
  * `option_selected` - How to write a selected option. Replaces {1} with the option text
  * `option_selected_reset` - When a color reset is found, what to replace it with
  * `option_selected_wrap` - What text to prefix each new line in a selected option that wraps
  * `control_select` - Space separated actions to select. Can be any of 'jump', 'left_click', 'sneak'
  * `control_cancel` - Space separated actions to select. Can be any of 'jump', 'left_click', 'sneak'
  * `control_move` - Space separated actions to move selection. Can be any of 'move', 'scroll'
  * `npc_name_type` - Type of NPC name display. Can be one of: 'none', 'chat'
  * `npc_name_align` - For npc name type of 'chat', how to align name. One of: 'left', 'right', 'center'
  * `npc_name_format` - How to format the npc name

Variables:

  * `{npc_text}` - The text the NPC says
  * `{option_text}` - The option text
  * `{npc_name}` - The name of the NPC

### Chat Interceptor: `packet`

Intercept pretty much anything sent to the player by intercepting packets sent to them. This can be enabled by default by setting the `default_interceptor` to `packet` in config.yml or per conversation by setting `interceptor` to `packet` in the top level of the conversation.

## [Quests](http://dev.bukkit.org/bukkit-plugins/quests/)

Quests is another questing plugin, which offers very simple creation of quests. If you don't want to spend a lot of time to write advanced quests in BetonQuest but you need a specific thing from this plugin you can use Custom Event Reward or Custom Condition Requirement. Alternatively, if you have a lot of quests written in Quests, but want to integrate them with the conversation system, you can use `quest` event and `quest` condition.

### Condition Requirement (Quests)

When adding requirements to a quest, choose "Custom requirement" and then select "BetonQuest condition". Now specify condition's name and it's package (like `package.conditionName`). Quests will check BetonQuest condition when starting the quest.

### Event Reward (Quests)

When adding rewards to a quest or a stage, choose "Custom reward" and then select "BetonQuest event". Now specify event's name and it's package (like `package.eventName`). Quests will fire BetonQuest event when this reward will run.

### Conditions

#### Quest condition: `quest`

This condition is met when the player has completed the specified quest. The first and only argument is the name of the quest. It it contains any spaces replace them with `_`.

!!! example
    ```YAML
    quest stone_miner
    ```

### Events

#### Quest: `quest`

This event will start the quest for the player. The first argument must be the name of the quest, as defined in `name` option in the quest. If the name contains any spaces replace them with `_`. You can optionally add `check-requirements` argument if you want the event to respect this quest's requirements (otherwise the quest will be forced to be started).

!!! example
    ```YAML
    quest stone_miner check-requirements
    ```

## [Shopkeepers](http://dev.bukkit.org/bukkit-plugins/shopkeepers/)

### Conditions

#### Shop amount: `shopamount`

This condition checks if the player owns specified (or greater) amount of shops. It doesn't matter what type these shops are. The only argument is a number - minimum amount of shops.

!!! example
    ```YAML
    shopamount 2
    ```

### Events

#### Open shop window: `shopkeeper`

This event opens a trading window of a Villager. The only argument is the uniqueID of the shop. You can find it in _Shopkeepers/saves.yml_ file, under `uniqueID` option.

!!! example
    ```YAML
    shopkeeper b687538e-14ce-4b77-ae9f-e83b12f0b929
    ```

## [SkillAPI](http://dev.bukkit.org/bukkit-plugins/skillapi/)

### Conditions

#### Class: `skillapiclass`

This condition checks if the player has specified class or a child class of the specified one. The first argument is simply the name of a class. You can add `exact` argument if you want to check for that exact class, without checking child classes.

!!! example
    ```YAML
    skillapiclass warrior
    ```

#### Level: `skillapilevel`

This condition checks if the player has specified or greater level is the specified class. The first argument is class name, the second one is the required level.

!!! example
    ```YAML
    skillapilevel warrior 3
    ```

## [Skript](http://dev.bukkit.org/bukkit-plugins/skript/)

BetonQuest can also hook into Skript. Firstly, to avoid any confusion, I will refere to everything here by name of the plugin (Skript event is something else than BetonQuest event). Having Skript on your server will enable using BetonQuest events and conditions in scripts, and also trigger them by BetonQuest event.

### Skript event triggered by BetonQuest `skript` event

This entry will describe two things: Skript event and BetonQuest event.

1. **Skript event** - `on [betonquest] event "id"` - this is the line you use in your scripts to trigger the code. `betonquest` part is optional, and `id` is just some string, which must be equal to the one you specified in BetonQuest event.
2. **BetonQuest event** - `skript` - this event will trigger the above Skript event in your scripts. The instruction string accepts only one argument, id of the event. It have to be the same as the one defined in Skript event for it to be triggered.

!!! example
    **In your script**
    ```YAML
    on betonquest event "concrete":
    ```
    
    **In events.yml**
    ```YAML
    fire_concrete_script: skript concrete
    ```

### Skript condition

You can check BetonQuest conditions in your scripts by using the syntax `player meets [betonquest] condition "id"`. `betonquest` is optional, and `id` is the name of the condition, as defined in _conditions.yml_.

!!! example
    **In your script**
    ```YAML
    player meets condition "has_ore"
    ```
     
     **In conditions.yml**
     ```YAML
     has_ore: item iron_ore:5
     ```

### Skript event

You can also fire BetonQuest events with scripts. The syntax for Skript effect is `fire [betonquest] event "id" for player`. Everything else works just like in condition above.

!!! example
    **In your script**
    ```YAML
    fire event "give_emeralds" for player
    ```
     
    **In events.yml**
    ```YAML
    give_emeralds: give emerald:5
    ```

## [Vault](http://dev.bukkit.org/bukkit-plugins/vault/)

By installing Vault you enable Permission event and Money condition/event.

### Conditions

#### Money: `money`

Checks if the player has specified amount of money. You can specify only one argument, amount integer. It cannot be negative!

!!! example
    ```YAML
    money 500
    ```

### Events

#### Money: `money`

Deposits, withdraws or multiplies money on player's account. There is only one argument, amount of money to modify. It can be positive, negative or start with an asterisk for multiplication.

!!! example
    ```YAML
    money -100
    ```

#### Permission: `permission`

Adds or removes a permission or a group. First argument is `add` or `remove`. It's self-explanatory. Second is `perm` or `group`. It also shouldn't be hard to figure out. Next thing is actual string you want to add/remove. At the end you can also specify world in which you want these permissions. If the world name is ommited then permission/group will be global.

!!! example
    ```YAML
    permission remove group bandit world_nether
    ```

### Variables

#### Money: `money`

There is only one argument in this variable, `amount` for showing money amount or `left:` followed by a number for showing the difference between it and amount of money.

!!! example
    ```YAML
    %money.left:500%
    ```

## [WorldEdit](http://dev.bukkit.org/bukkit-plugins/worldedit/)

### Events

#### Paste schematic: `paste`

This event will paste a schematic at the given location. The first argument is a location and the second one is the name of schematic file. The file must be located in `WorldEdit/schematics` and have a name like `some_building.schematic`. An optional `noair` can be added to paste ignoring air blocks.
If you have only a `.schem` schematic, simply append `.schem` to the schematic name.

!!! example
    ```YAML
    paste 100;200;300;world some_building noair
    ```

## [WorldGuard](http://dev.bukkit.org/bukkit-plugins/worldguard/)

### Conditions

#### Inside Region: `region`

This condition is met when the player is inside the specified region. The only argument is the name of the region.

!!! example
    ```YAML
    region beton
    ```

### Objectives

#### Enter Region: `region`

To complete this objective you need to enter WorldGuard region with specified name. A required argument is the name of the region and you may also pass an optional `entry` and/or `exit` to only trigger when entering or exiting a region instead of anytime inside a region.

!!! example
    ```YAML
    region beton events:kill
    ```
