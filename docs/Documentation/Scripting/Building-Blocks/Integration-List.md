---
icon: material/handshake
toc_depth: 2
---
# Integration List
This page contains documentation for known integrations that exist for third party plugins.
Some integrations also have dedicated pages in the documentation.
In total @snippet:constants:totalIntegratedPluginsNumber@ plugins have dedicated support for BetonQuest.
 
## Provided by BetonQuest

BetonQuest hooks into other plugins by itself to provide more events, conditions and objectives or other features.  
_AureliumSkills, Brewery, Citizens, DecentHolograms, Denizen, EffectLib, FakeBlock, Heroes, HolographicDisplays, JobsReborn, LuckPerms, Magic,
mcMMO, MythicLib, MMOCore, MMOItems, MythicMobs, PlaceholderAPI, ProtocolLib, Quests, Shopkeepers, ProSkillAPI,
Skript, RedisChat, Vault, WorldEdit, FastAsyncWorldEdit and WorldGuard._

## Provided by other plugins
Some plugins also hook into BetonQuest and provide support by themselves:  
[nuNPCDestinations](https://www.spigotmc.org/resources/13863/),
[CalebCompass](https://www.spigotmc.org/resources/82674/),
[Depenizen](https://github.com/DenizenScript/Depenizen),
[NotQuests](https://www.spigotmc.org/resources/95872/),
[HonnyCompass](https://github.com/honnisha/HonnyCompass)
[MythicDungeons](https://www.spigotmc.org/resources/102699/)

There are also plugins that hook into BetonQuest that require a clientside mod:  
[BetonQuestGUI](https://github.com/giovanni-bozzano/betonquest-gui-plugin),
[NGVexJournal](https://www.spigotmc.org/resources/76938/)


## AureliumSkills[](https://www.spigotmc.org/resources/81069/)

### Conditions

#### Skill level: `aureliumskillslevel`
Checks if the player has the specified skill level. The amount can be a variable or a number.
The player needs to be on that level or higher to meet the condition.
You can disable this behaviour by adding the `equal` argument, then the player must match the specified level exactly.
```YAML linenums="1"
aureliumskillslevel fighting 5
aureliumskillslevel farming 10 equal
```

#### Stat level: `aureliumstatslevel`
Checks if the player has the specified stat level. The amount can be a variable or a number.
The player needs to be on that level or higher to meet the condition.
You can disable this behaviour by adding the `equal` argument, then the player must match the specified level exactly.
```YAML linenums="1"
aureliumstatslevel luck 5
aureliumstatslevel luck 10 equal
```

### Events

### Give Skill Xp : `aureliumskillsxp`
Adds experience to the players skill. The amount can be a variable or a number.
The `level` argument is optional and would convert the amount to levels instead of XP points.
```YAML linenums="1"
aureliumskillsxp farming 5
aureliumskillsxp farming 10 level
```


## Brewery[](https://www.spigotmc.org/resources/3082/)

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
This condition is true if the player has the given brew with the specified amount in his inventory.

``` YAML linenums="1"
hasbrew 2 MY_BREW
```

### Events

#### Give Brew: `givebrew`
Gives the player the specified drink. The first number is the amount, and the second number is the quality of the drink.

``` YAML linenums="1"
givebrew 1 10 MY_BREW
```

#### Take Brew: `takebrew`
Removes the specified drink from the players inventory. An amount needs to be specified.

``` YAML linenums="1"
takebrew 2 MY_OTHER_BREW 
```

## Citizens[](https://www.spigotmc.org/resources/13811/)

If you have this plugin you can use it's NPCs for conversations. I highly recommend you installing it,
it's NPCs are way more immersive. Having Citizens also allows you to use NPCKill objective and to have moving NPC's.

A Citizen NPC will only react to right clicks by default. This can be changed by 
setting `acceptNPCLeftClick` in the config.yml to `true`.

!!! notice
      You need to specify the ID of the NPC instead of it's name in the package.yml when using Citizens!

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
    npcregion 16 spawn
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
    npcinteract 3 cancel conditions:sneak events:steal
    ```

#### NPC Kill: `npckill`

The NPC kill objective requires the player to kill a NPC with the given ID. You can also define how many times the NPC
has to be killed. Right after the objective's name there must be the ID of the NPC. You can also add an amount with the
`amount` keyword. You can use the `notify` keyword to display a message each time the player advances the objective,
optionally with the notification interval after a colon.

This objective has three properties: `amount`, `left` and `total`. `amount` is the amount of NPCs already killed,
`left` is the amount of NPCs still needed to kill and `total` is the amount of NPCs initially required.

!!! example
    ```YAML
    npckill 16 amount:3 events:reward notify
    ```

#### NPC Range: `npcrange`

The player has to enter/leave a circle with the given radius around the NPC to complete this objective.
It is also possible to define multiple NPCs separated with `,`. The objective will be completed as soon as you meet the requirement of just one npc.
First argument is the ID of the NPC, second one is the type: Either `enter`, `leave`, `inside` or `outside` and the third one is the range.
The types `enter`, `leave` force the player to actually enter the radius after you were outside of it and vice versa.
This means that `enter` is not completed when the player gets the objective and is already in the range, while `inside` is instantly completed.

!!! example
    ```YAML
    npcrange 3,5 enter 20 events:master_inRange
    ```

### Variables

#### Citizen Variable: `%citizen.<id>.<argument>%`

This variable resolves information about a Citizen NPC by id. Specifying an argument determines the return: the NPC name, or full name.

Arguments:
* name - Return citizen name
* full_name - Full Citizen name

!!! example
  ```YAML
  %citizen.15.name%        # Bob
  %citizen.15.full_name%   # &eBob
  ```

#### Citizen Location Variable: `%citizen.<id>.location.<mode>.<precision>%`

This variable resolves to all Citizen location arguments: the x, y and z coordinates, the world name, the yaw and pitch
(head rotation). The first argument is citizen, followed by the citizen ID, then location. It also supports the 
BetonQuest [Unified Location Formatting](../Data-Formats.md#unified-location-formating) modes which can optionally be added to the end.

!!! example
  ```YAML
  %citizen.15.location%           # -> 325;121;814;npcWorldName;12;6
  %citizen.15.location.xyz%       # -> 325 121 814 
  %citizen.15.location.x%         # -> 325
  %citizen.15.location.y%         # -> 121
  %citizen.15.location.z%         # -> 814
  %citizen.15.location.yaw%       # -> 12
  %citizen.15.location.pitch%     # -> 6
  %citizen.15.location.world%     # -> npcWorldName
  %citizen.15.location.ulfShort%  # -> 325;121;814;npcWorldName
  %citizen.15.location.ulfLong%   # -> 325;121;814;npcWorldName;12;6
  
  %citizen.15.location.x.2%       # -> 325.16
  %citizen.15.location.ulfLong.5% # -> 325.54268;121.32186;814.45824;npcWorldName;12.0;6.0
  ```

## Denizen[](http://dev.bukkit.org/bukkit-plugins/denizen/)


Depenizen is also integrated with BetonQuest! Discover available features on the [meta documentation](https://meta.denizenscript.com/Docs/Search/BetonQuest).

### Events

#### Script: `script`

With this event you can fire Denizen task scripts. Don't confuse it with `skript` event, these are different. The first and only argument is the name of the script.

```YAML title="Example"
runDenizenScript: "script beton"
```

## EffectLib[](http://dev.bukkit.org/bukkit-plugins/effectlib/)

If you install this plugin on your server you will be able to play particle effects on NPCs and locations. 
You can also use the `particle` event to trigger particle.

!!! info EffectLib Documentation
    EffectLib is not a normal plugin, it's a powerful developer tool - there are no official docs. However, the Magic plugin has a
    [wiki](https://reference.elmakers.com/#effectlib) for EffectLib.
    It does contain a few magic specific settings though so please don't be confused if some stuff does not work.
    There is also a [magic editor](https://sandbox.elmakers.com/#betonquestEffectLibTemplate) with autocompletion for EffectLib.

```YAML title="Example"
effectlib: #(1)!
   farmer: #(2)!
      class: VortexEffect #(3)!
      iterations: 20 #(4)!
      particle: crit_magic 
      helixes: 3
      circles: 1
      grow: 0.1
      radius: 0.5
      pitch: -60 #(9)!
      yaw: 90 #(10)!
      interval: 30 #(8)!
      checkinterval: 80 #(11)!
      npcs: #(5)!
         - 1 
      locations: #(6)!
         - 171;72;-127;world
      conditions: #(7)!
         - '!con_tag_started'
         - '!con_tag_finished'
```

1. All effects need to be defined in this section.
2. Each effect is defined as a separate subsection. You can choose any name for it.
3. Any EffectLib effect class.
4. This and all following options until `interval` are EffectLib parameters. You can find them in the 3rd party documentation linked above.
5. A list of all NPCs on which this effect is displayed. This section is optional.
6. A list of all locations on wich the effect is displayed. Optional.
7. The conditions that must be true so that the player can see this effect.
8. Controls after how many ticks the effect is restarted. Optional, default: 100 ticks
9. Controls the vertical direction of the effect.
10. Controls the horizontal direction of the effect.
11. Controls how often the conditions should be checked (in ticks). Optional, default: 100 ticks

### Events

#### Particle: `particle`

This event will load an effect defined in `effects` section
and display it on player's location. The only argument
is the name of the effect. You can optionally add `loc:` argument
followed by a location written like `100;200;300;world;180;-90` to put
it on that location. If you add `private` argument the effect will only
be displayed to the player for which you ran the event.

!!! example
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
    ```YAML
    events:
      playEffect: particle beton loc:100;200;300;world;180;-90 private
    ```

## FakeBlock[](https://github.com/toddharrison/BriarCode/tree/main/fake-block)

If you have the FakeBlock integration installed, you will be able to view and hide the block groups 
created in FakeBlock on a player-specific basis.

### Events

#### Show and hide block groups: `fakeblock` 

Shows or hides the block group for the player. The block group can be specified as a comma-separated list. 
The groups are case-sensitive. To show a group the `showgroup` argument is required. To hide a group the `hidegroup` argument is required.


```YAML
events:
  showBridge: "fakeblock showgroup bridge"
  hideCityBorder: "fakeblock hidegroup gate,wall,door"
```

## Heroes[](https://www.spigotmc.org/resources/24734/)

When you install Heroes, all kills done via this plugin's skills will be counted in MobKill objectives.

### Conditions

#### Heroes Class: `heroesclass`

This condition checks the classes of the player. The first argument must be `primary`, `secondary` or `mastered`. Second is the name of a class or `any`. You can optionally specify `level:` argument followed by the required level of the player.

!!! example
    ```YAML
    heroesclass mastered warrior
    ```

#### Heroes Attribute: `heroesattribute`

This condition check's the level of a player's attribute. The first argument must be `strength`, `constitution`, `endurance`, `dexterity`, `intellect`, `wisdom`, or `charisma`. Second argument is the required level of the attribute. Must be greater than or equal the specified number.

!!! example
    ```YAML
    heroesattribute strength 5
    ```

#### Skill: `heroesskill`

This condition checks if the player can use specified skill. The first argument is the name of the skill.

!!! example
    ```YAML
    heroesskill charge
    ```

### Events

#### Heroes experience: `heroesexp`

This event simply gives the player specified amount of Heroes experience. The first argument is either `primary` or `secondary` and it means player's class. Second one is the amount of experience to add.

!!! example
    ```YAML
    heroesexp primary 1000
    ```

## JobsReborn[](https://www.spigotmc.org/resources/4216/)

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

#### Add Jobs Experience: `nujobs_addexp {jobname} {exp}`

Gives the player experience

#### Increase Jobs Level: `nujobs_addlevel {jobname} {amount}`

Increases the player level by amount.

#### Decrease Jobs Level: `nujobs_dellevel {jobname} {amount}`

Decreases the players level by amount.

#### Join Jobs Job Event: `nujobs_joinjob {jobname}`

Joins the player to job.

#### Leave Jobs Job Event: `nujobs_leavejob {jobname}`

Removes the player from job.

#### Set Jobs Level: `nujobs_setlevel {jobname} {level}`

Set the player to level.

### Objectives

#### Join Jobs Job Objective: `nujobs_joinjob {jobname}`

Triggers when player joins job.

#### Leave Jobs Job Objective: `nujobs_leavejob {jobname}`

Triggers when player leaves job.

!!! notice
    This is not triggered by '/jobs leaveall'

#### Jobs Job Levelup: `nujobs_levelup {jobname}`

Triggers when player levels up.

#### Jobs Job Payment: `nujobs_payment {amount}`

Triggers when player makes {amount} of money from jobs. You can use the `notify` keyword to display a message each time
the player advances the objective, optionally with the notification interval after a colon.

This objective has three properties: `amount`, `left` and `total`. `amount` is the amount of money already received,
`left` is the amount of money still needed to receive and `total` is the amount of money initially required.


## LuckPerms[](https://luckperms.net/)

### Context Integration

Any BetonQuest tag (global and per-player) can be used as a LuckPerms context. This means that a player needs the specified tag for a permission
to be true - this removes the need for tons of `permission add ...` events as you can hook your existing
quest progress tags right into LuckPerms permission
[contexts](https://luckperms.net/wiki/Context).
The syntax is as follows:

| key                                        | value |
|--------------------------------------------|-------|
| betonquest:tag:PACKAGE_NAME.TAG_NAME       | true  |
| betonquest:globaltag:PACKAGE_NAME.TAG_NAME | true  |
| betonquest:tag:myPackage.tagName           | true  |
| betonquest:globaltag:myQuest.someTag       | true  |

Check the [Luck Perms documentation](https://luckperms.net/wiki/Context)
for an in-depth explanation on what contexts are and how to add them to permissions.

## Magic[](http://dev.bukkit.org/bukkit-plugins/magic/)

### Conditions

#### Wand: `wand`

This condition can check wands. The first argument is either `hand`, `inventory` or `lost`. If you choose `lost`, the condition will check if the player has lost a wand. If you choose `hand`, the condition will check if you're holding a wand in your hand. `inventory` will check your whole inventory instead of just the hand. In case of `hand` and `inventory` arguments you can also add optional `name:` argument followed by the name of the wand (as defined in _wands.yml_ in Magic plugin) to check if it's the specific type of the wand. In the case of `inventory` you can specify an amount with `amount` and this will only return true if a player has that amount. You can also use optional `spells:` argument, followed by a list of spells separated with a comma. Each spell in this list must have a minimal level defined after a colon.

!!! example
    ```YAML
    wand hand name:master spells:flare:3,missile:2
    ```

## McMMO[](https://www.spigotmc.org/resources/64348/)

### Conditions

#### McMMO Level: `mcmmolevel`

This conditions checks if the player has high enough level in the specified skill. The first argument is the name of the skill, second one is the minimum level the player needs to have to pass this condition.

!!! example
    ```YAML
    mcmmolevel woodcutting 50
    ```

### Events

#### Add MCMMO Experience: `mcmmoexp`

This event adds experience points in a specified skill. The first argument is the name of the skill, second one is the amount of experience to add.

!!! example
    ```YAML
    mcmmoexp swords 1500
    ```

## MMOCore[](https://www.spigotmc.org/resources/70575/) & MMOItems[](https://www.spigotmc.org/resources/39267/) & MythicLib[](https://www.spigotmc.org/resources/90306/)


### Conditions

#### MMOCore class: `mmoclass` 
Checks if a player has the given MMOCore class. You can check for any class that is not the default class by writing `*`
instead of a class name.
If a level has been specified the player needs to be on that level
or higher to meet the condition.
You can disable this behaviour by adding the `equal` argument. 
```YAML linenums="1"
mmoclass * 5
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
Checks if a player has the specified amount of MMOItems or more in his inventory. If no amount has been defined the default amount is one.
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

#### MythicLib stat: `mmostat`
Checks [these](https://gitlab.com/phoenix-dvpmt/mythiclib/-/blob/master/plugin/src/main/java/io/lumine/mythic/lib/api/stat/SharedStat.java)
stats that combine all sorts of stats from MMOCore and MMOItems.
The player needs to be on the specified level or higher in order to meet this condition.
You can disable this behaviour by adding the `equal` argument. 
```YAML linenums="1"
mmostat DAMAGE_REDUCTION 3
```

### Objectives

#### Break Special Blocks: `mmocorebreakblock`
This objective requires the player to break 
[special blocks from MMOCore](https://gitlab.com/phoenix-dvpmt/mmocore/-/wikis/Mining%20and%20Block%20Regen).
All three different block types and an amount can be defined. You can also send notifications to the player by appending
the `notify` keyword optionally with the notification interval after a colon.

This objective has three properties: `amount`, `left` and `total`. `amount` is the amount of blocks already broken,
`left` is the amount of blocks still left to break and `total` is the amount of blocks initially required.

```YAML linenums="1"
mmocorebreakblock 5 block:1      #A custom block's block ID
mmocorebreakblock 64 block:STONE  #vanilla material
mmocorebreakblock 1 block:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVy #... this is a heads texture data
```

#### MMOCore Profession levelup: `mmoprofessionlevelup`
This objective requires the player to level the given profession to the specified level.
Use `main` to check for class level ups.

```YAML linenums="1"
mmoprofessionlevelup MINING 10
```

#### Craft item: `mmoitemcraft`
This objective requires the player to craft the item with the given type and id.
It supports any MMOItem that was crafted using vanilla crafting methods, MMOItems "recipe-amounts" crafting and MMOItems station crafting.
An amount can also be set if it shall differ from the default (which is one) by adding the `amount:` argument.
The amount is based on how many items have actually been crafted, not how often a specific recipe has been used! Therefore,
a recipe that makes four items at once will let the objective progress by four steps. You can use the `notify` keyword
to display a message each time the player advances the objective, optionally with the notification interval after a
colon.

This objective has three properties: `amount`, `left` and `total`. `amount` is the amount of items already crafted,
`left` is the amount of items still needed to craft and `total` is the amount of items initially required.

```YAML linenums="1"
mmoitemcraft SWORD STEEL_SWORD
mmoitemcraft HEALTH_POTION_RECIPE amount:5
```
#### Upgrade Item: `mmoitemupgrade`
This objective tracks if a player upgrades the given item with an upgrade consumable.  
```YAML linenums="1"
mmoitemupgrade SWORD FALCON_BLADE
```

#### Apply gemstone: `mmoitemapplygem`
This objective is completed when the player applies the gemstone with the given gemstoneID to an item with the given
itemType and itemID.
```YAML linenums="1"
mmoitemapplygem SWORD CUTLASS GEM_OF_ACCURACY
```

#### Activate MythicLib skill: `mmoskill`
This objective requires the player to activate a MythicLib skill (e.g. with MMOItems or MMOCore). 

| Parameter | Syntax     | Default Value          | Explanation                                                                                                                                                                          |
|-----------|------------|------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| _skill_   | SKILL_ID   | :octicons-x-circle-16: | The ID of the skill.                                                                                                                                                                 |
| _trigger_ | name:level | All trigger types.     | The [types of triggers](https://gitlab.com/phoenix-dvpmt/mythiclib/-/wikis/Skills#trigger-types) that can be used to activate the skill. If not specified, all triggers are allowed. |


```YAML title="Example" linenums="1"
triggerSkill: "mmoskill LIFE_ENDER event:updateStatistics"
castSkillWithMMOCore: "mmoskill DEEP_WOUND trigger:CAST event:completeTutorial"
itemSkill: "mmoskill DEEP_WOUND trigger:RIGHT_CLICK,LEFT_CLICK event:giveReward"
```

### Events

#### Give MMOCore class experience: `mmoclassexperience`
Adds experience to the players class. The amount can be a variable or a number. The `level` argument
is optional and would convert the amount to levels instead of XP points.
```YAML linenums="1"
mmoclassexperience 150
mmoclassexperience 1 level
```

#### Give MMOCore profession experience: `mmoprofessionexperience`
Adds experience in the specified player profession. The amount can be a variable or a number. The `level` argument
is optional and would convert the amount to levels instead of XP points.
```YAML linenums="1"
mmoprofessionexperience MINING 100
mmoprofessionexperience CUSTOM_PROFESSION_NAME 1 level
```

#### Give class points: `mmocoreclasspoints`
Gives the player class points. The amount can be a variable or a number.
```YAML linenums="1"
mmocoreclasspoints 1
```

#### Give skill points: `mmocoreskillpoints`
Gives the player skill points. The amount can be a variable or a number.
```YAML linenums="1"
mmocoreskillpoints 10
```

#### Give attribute points: `mmocoreattributepoints`
Gives the player attribute points. The amount can be a variable or a number.
```YAML linenums="1"
mmocoreattributepoints 2
```

#### Give attribute reallocation points: `mmocoreattributereallocationpoints`
Gives the player attribute reallocation points. The amount can be a variable or a number.
```YAML linenums="1"
mmocoreattributereallocationpoints 1
```

#### Give MMOItem: `mmoitemgive`
Gives the player predefined item. Default amount is one and can be set manually to a higher amount or a variable.
The item can be adjusted to the players level by adding the `scale` option. If you want all items to be stacked together 
the `singleStack` option can be set. If the player doesn't have required space in the inventory, the items will be dropped on the ground.
You can also specify the `notify` keyword to display a message to the player about what items have been received.
```YAML linenums="1"
mmoitemgive CONSUMABLE MANA_POTION
```

#### Take MMOItem: `mmoitemtake`

Removes the specified item from the players inventory. Optional arguments are an amount and `notify` to send a notification
to the player.

Which inventory types are checked is defined by the `invOrder:`
option. You can use `Backpack`, `Inventory` and `Armor` there. One after another will be checked if multiple types are defined.
The backpack will not work before 2.0's item rework since the current item system does not safe custom NBT data.

You can also specify `notify` keyword to display a simple message to the player about loosing items.

Amount can be a variable.
```YAML linenums="1"
mmoitemtake SWORD STEEL_SWORD
mmoitemtake SWORD STEEL_SWORD notify
mmoitemtake CONSUMABLE HEALTH_POTION amount:5
mmoitemtake CONSUMABLE BAKED_APPLES amount:2 invOrder:Backpack,Inventory
mmoitemtake ARMOR KINGS_CHESTPLATE invOrder:Armor,Backpack
```


## MythicMobs[](http://dev.bukkit.org/bukkit-plugins/mythicmobs/)
!!! info ""
    **Required MythicMobs version: _5.3.5_ or above** 


### Objectives

#### MobKill: `mmobkill`

You need to kill the specified amount of MythicMobs to complete this objective. The first argument must be
the mob's internal name (the one defined in your MythicMobs configuration). Multiple mob names must be comma seperated.
You can optionally add the `amount:` argument to specify how many of these mobs need to be killed. It's also possible
to add the optional arguments `minLevel` and `maxLevel` to further customize what mobs need to be killed.
You can also add an optional `neutralDeathRadiusAllPlayers` argument to complete the objective for each nearby player
within the defined radius when the mob is killed by any non-player source.
Alternatively, you could use the `deathRadiusAllPlayers` argument to count all deaths of the specified mythic mob(s),
no matter if it was killed by a non-player source or not.
You can add a `notify` keyword if you want to send a notification to players whenever the objective progresses.
You can also add an optional `marked` argument to only count kills marked with the `mspawn` event.
The only supported variable for the marked argument is `%player%`.

This objective has three properties: `amount`, `left` and `total`. `amount` is the amount of mythic mobs already killed,
`left` is the amount of mythic mobs still needed to kill and `total` is the amount of mythic mobs initially required.

!!! example
    ```YAML
    mmobkill SkeletalKnight amount:2 events:reward
    mmobkill SnekBoss,SnailBoss,SunBoss amount:10 events:reward
    mmobkill SnekBoss amount:2 minlevel:4 maxlevel:6 events:reward marked:DungeonBoss3
    mmobkill dungeonDevil deathRadiusAllPlayers:30 events:reward
    ```

### Conditions

#### MythicMob distance: `mythicmobdistance`

Check whether the player is near a specific MythicMobs entity. The first argument is the internal name of the mob (the one defined in MythicMobs' configuration). The second argument is the distance to check, measured in block lengths in a circular radius.

!!! example
    ```YAML
    mythicmobdistance SkeletalKnight 7
    ```
    
### Events

#### :material-skull: Spawn MythicMob: `mspawnmob`

| Parameter  | Syntax                                              | Default Value          | Explanation                                                                                                                             |
|------------|-----------------------------------------------------|------------------------|-----------------------------------------------------------------------------------------------------------------------------------------|
| _location_ | [ULF](../Data-Formats.md#unified-location-formating) | :octicons-x-circle-16: | The location to spawn the mob at.                                                                                                       |
| _name_     | name:level                                          | :octicons-x-circle-16: | MythicMobs mob name. A level must be specifed after a colon.                                                                            |
| _amount_   | Positive Number                                     | :octicons-x-circle-16: | Amount of mobs to spawn.                                                                                                                |
| _target_   | Keyword                                             | False                  | Will make the mob target the player.                                                                                                    |
| _private_  | Keyword                                             | Disabled               | Will hide the mob from all other players until restart. This does not hide particles or block sound from the mob. Also see notes below. |
| _marked_   | marked:text                                         | None                   | Marks the mob. You can check for marked mobs in mmobkill objective.                                                                     |


```YAML title="Example"
events:
  spawnBoss: mspawnmob 100;200;300;world MegaBoss:1 1 target
  spawnKnights: mspawnmob 100;200;300;world SkeletalKnight:3 5
  spawnPrivateDevil: mspawnmob 100;200;300;world Mephisto:1 5 target private marked:DungeonBoss3
```

!!! warning "Private Argument"
    The `private` argument requires some MythicMob setup for optimal use.
    It's best to use the `private` argument in combination with the `target` argument so the mob does not attack 
    players that cannot see it.
    Additionally, the mob should be configured to never change its AI target using MythicMobs.

## PlaceholderAPI[](https://www.spigotmc.org/resources/6245/)

If you have this plugin, BetonQuest will add a `betonquest` placeholder to it and you will be able to use `ph` variable in your conversations.

### Placeholder: `betonquest`

You can use all BetonQuest variables in any other plugin that supports PlaceholderAPI.
You can even use BetonQuests conditions using the [condition variable](Variables-List.md#condition-variable)!    
This works using the `%betonquest_package:variable%` placeholder. The `package:` part is the name of a package.
The `variable` part is just a [BetonQuest variable](Variables-List.md) without percentage characters, like `point.beton.amount`.

Testing your placeholder is easy using this command:    
`/papi parse <PlayerName> %betonquest_<PackageName>:<VariableType>.<Property>%`
```YAML linenums="1"
%betonquest_someGreatQuest:objective.killZombies.left%
```

### Variable: `ph`

You can also use placeholders from other plugins in BetonQuest. Simply insert a variable starting with `ph`, the second argument should be the placeholder without percentage characters.

!!! example
    ```YAML
    %ph.player_item_in_hand%
    ```

## ProtocolLib[](https://www.spigotmc.org/resources/1997/)

### Freeze players: 'freeze'
This event allows you to freeze player for the given amount of ticks:
```YAML
freezeMe: "freeze 100" #Freezes the player for 5 seconds
```

## Quests[](https://www.spigotmc.org/resources/3711/)

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

## Shopkeepers[](http://dev.bukkit.org/bukkit-plugins/shopkeepers/)

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

## ProSkillAPI[](https://www.spigotmc.org/resources/91913/)

This adds support for [ProMCTeam's SkillAPI fork](https://www.spigotmc.org/resources/91913/). They still update SkillAPI.  

### Conditions

#### SkillAPI Class: `skillapiclass`

This condition checks if the player has specified class or a child class of the specified one. The first argument is simply the name of a class. You can add `exact` argument if you want to check for that exact class, without checking child classes.

!!! example
    ```YAML
    skillapiclass warrior
    ```

#### SkillAPI Level: `skillapilevel`

This condition checks if the player has specified or greater level is the specified class. The first argument is class name, the second one is the required level.

!!! example
    ```YAML
    skillapilevel warrior 3
    ```

## Skript[](http://dev.bukkit.org/bukkit-plugins/skript/)

BetonQuest can also hook into Skript. Firstly, to avoid any confusion, I will refere to everything here by name of the plugin (Skript event is something else than BetonQuest event). Having Skript on your server will enable using BetonQuest events and conditions in scripts, and also trigger them by BetonQuest event.

You can use cross-package paths using `-` between the packages. Example:
`player meets condition "default-Forest-Jack.Completed"`

### Skript event triggered by BetonQuest `skript` event

This entry will describe two things: Skript event and BetonQuest event.

1. **Skript event** - `on [betonquest] event "id"` - this is the line you use in your scripts to trigger the code. `betonquest` part is optional, and `id` is just some string, which must be equal to the one you specified in BetonQuest event.
2. **BetonQuest event** - `skript` - this event will trigger the above Skript event in your scripts. The instruction string accepts only one argument, id of the event. It have to be the same as the one defined in Skript event for it to be triggered.

!!! example
    **In your script**
    ```YAML
    on betonquest event "concrete":
    ```
    **In BetonQuest**
    ```YAML
    events:
      fire_concrete_script: skript concrete
    ```

### Skript condition

You can check BetonQuest conditions in your scripts by using the syntax `player meets [betonquest] condition "id"`. `betonquest` is optional, and `id` is the name of the condition, as defined in the _conditions_ section.

!!! example
    **In your script**
    ```YAML
    player meets condition "has_ore"
    ```
    **In BetonQuest**
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
    **In BetonQuest**
    ```YAML
    events:
      give_emeralds: give emerald:5
    ```

## Vault[](http://dev.bukkit.org/bukkit-plugins/vault/)

### Conditions

#### Vault Money Condition: `money`

Checks if the player has the specified amount of money.

```YAML
conditions:
  hasMoney: "money 1"
  canAffordPlot: "money 10000"
  isRich: "money 1000000"
```

!!! tip
    Invert this condition if you want to check if the player has less money than specified. Example:
    ```YAML
    conditions:
      isRich: "money 100000"
    events:
      giveSubsidy: "money +500 conditions:!isRich" #(1)!
    ```
    
    1. If the player has less than 100000 money, the `giveSubsidy` event will be fired.    

### Events

#### Vault Money Event: `money`

Deposits, withdraws or multiplies money in the player's account.

| Parameter | Syntax            | Default Value          | Explanation                                                    |
|-----------|-------------------|------------------------|----------------------------------------------------------------|
| _amount_  | Number            | :octicons-x-circle-16: | The amount of money to add or remove. Use `*` to multiply.     |
| _notify_  | Keyword: `notify` | Disabled               | Display a message to the player when their balance is changed. |

```YAML
events:
  sellItem: "money +100"
  buyPlot: "money -10000"
  winLottery: "money *7 notify"
```

#### Change Permission (Groups): `permission`

Adds or removes a permission or a group.

| Parameter | Syntax                      | Default Value          | Explanation                                                                                                          |
|-----------|-----------------------------|------------------------|----------------------------------------------------------------------------------------------------------------------|
| _action_  | `add` or `remove`           | :octicons-x-circle-16: | Whether to add or remove the thing specified using the following arguments.                                          |
| _type_    | `perm` or `group`           | :octicons-x-circle-16: | Whether to use a permission or permission group.                                                                     |                                   | Disabled               | Will hide the mob from all other players until restart. This does not hide particles or block sound from the mob. Also see notes below. |
| _name_    | The name of the permission. | :octicons-x-circle-16: | The name of the permission or group to add.                                                                          |
| _world_   | The name of the world.      | Global                 | You can limit permissions to certain worlds only. If no world is set the permission will be set everywhere (global). |

```YAML
events:
  allowFly: "permission add perm essentials.fly"
  joinBandit: "permission add group bandit"
  leaveBandit: "permission remove group bandit"
```

### Variables

#### Vault Money Variable: `money`

Use `%money.amount%` for showing the player's balance.
Use `%money.left:500%` for showing the difference between the player's balance and the specified amount of money.

```YAML
events:
  notifyBalance: "notify You have %money.amount%$!"
  notifyNotEnough: "notify You still need %money.left:10000%$ to buy this plot."
```

## WorldEdit[](http://dev.bukkit.org/bukkit-plugins/worldedit/) or FastAsyncWorldEdit[](https://www.spigotmc.org/resources/13932/)

### Events

#### Paste schematic: `paste`

**persistent**, **static**

This event will paste a schematic at the given location.
The first argument is a location and the second one is the name of a schematic file.
The file must be located in `WorldEdit/schematics` or `FastAsyncWorldEdit/schematics` and must have a name like
`some_building.{++schematic++}`. If WorldEdit saves `.schem` schematic files, simply append `.schem` to the
schematic name in the event's instruction.

The optional `noair` keyword can be added to ignore air blocks while pasting.


```YAML title="Example"
events:
  pasteCastle: "paste 100;200;300;world castle noair" #(1)!
  pasteTree: "paste 100;200;300;world tree.schem noair" #(2)!
```

1. Pastes the schematic file `castle.{++schematic++}` at the location `100;200;300;world`.
2. Pastes the schematic file `tree.{++schem++}` at the location `100;200;300;world`.

## WorldGuard[](http://dev.bukkit.org/bukkit-plugins/worldguard/)

### Conditions

#### Inside Region: `region`

This condition is met when the player is inside the specified region. The only argument is the name of the region.


```YAML title="Example"
conditions:
  inCastle: "region castle"
```

### Objectives

#### Enter Region: `region`

To complete this objective you need to enter WorldGuard region with specified name. A required argument is the name of the region and you may also pass an optional `entry` and/or `exit` to only trigger when entering or exiting a region instead of anytime inside a region.


```YAML title="Example"
objectives:
  deathZone: "region deathZone entry events:kill"
```
## [RedisChat](https://emibergo.gitbook.io/redischat/)
This integration stops the chat when a conversation starts and then resumes it when the conversation ends.
