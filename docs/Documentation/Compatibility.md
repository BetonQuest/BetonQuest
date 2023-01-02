---
icon: material/handshake
---
# Compatibility
**In total 33 plugins have dedicated support for BetonQuest.**

BetonQuest hooks into other plugins by itself to provide more events, conditions and objectives or other features. 
26 plugins are supported right now:    
_AureliumSkills, Brewery, Citizens, DecentHolograms, Denizen, EffectLib, Heroes, HolographicDisplays, JobsReborn, LuckPerms, Magic,
mcMMO, MythicLib, MMOCore, MMOItems, MythicMobs, PlaceholderAPI, ProtocolLib, Quests, Shopkeepers, ProSkillAPI,
Skript, Vault, WorldEdit, FastAsyncWorldEdit and WorldGuard._

Some plugins also hook into BetonQuest and provide support by themselves:  
[nuNPCDestinations](https://www.spigotmc.org/resources/13863/),
[CalebCompass](https://www.spigotmc.org/resources/82674/),
[NotQuests](https://www.spigotmc.org/resources/95872/),
[HonnyCompass](https://github.com/honnisha/HonnyCompass)
[MythicDungeons](https://www.spigotmc.org/resources/102699/)

There are also plugins that hook into BetonQuest that require a clientside mod:  
[BetonQuestGUI](https://github.com/giovanni-bozzano/betonquest-gui-plugin),
[NGVexJournal](https://www.spigotmc.org/resources/76938/)


## [AureliumSkills](https://www.spigotmc.org/resources/81069/)

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


## [Brewery](https://www.spigotmc.org/resources/3082/)

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

## NPC's using [Citizens](https://www.spigotmc.org/resources/13811/)

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

EffectLib is not a normal plugin, it's a developer tool - there are no official docs. However, the Magic plugin has a
[wiki](https://reference.elmakers.com/#effectlib) for EffectLib.
It does contain a few magic specific settings though so please don't be confused if some stuff does not work.
There is also a [magic editor](https://sandbox.elmakers.com/#betonquestEffectLibTemplate) with autocompletion for EffectLib.

You can control the behaviour of particles around the NPCs in the `npc_effects` section.
Each effect is defined as a separate subsection and consists of EffectLib options (described on the EffectLib page) and several BetonQuest settings.
`npcs` is a list of all NPCs on which this effect can be displayed. If no `npcs` are specified it will use the package NPCs from _package.yml_.
`conditions` is a list of conditions the player has to meet in order to see the effect.
BetonQuest will find the first effect which can be displayed and show it to the player.
`interval` controls how often the effect is displayed (in ticks). The effect will be fired from the exact location of the NPC, upwards.

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

## [Heroes](https://www.spigotmc.org/resources/24734/)

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

## Holograms

!!! info "Required Dependencies"
    The following feature can be activated by using any of the following plugins:
    
    | Plugin               | Required Version | Additional Dependencies                                                            |
    |----------------------|------------------|------------------------------------------------------------------------------------|
    | DecentHolograms      | 2.7.5 or above   | [PlaceholderAPI](https://www.spigotmc.org/resources/6245/) for in-line variables.  |
    | Holographic Displays | 3.0.0 or above   | [ProtocolLib](https://www.spigotmc.org/resources/1997/) for conditioned holograms. | 
    
    If you have both plugins installed, you can use the [`default_hologram` option in "_config.yml_"](Configuration.md#default-hologram-plugin) to set which plugin should be used.
    
    !!! bug ""
        **When used by external plugins like BetonQuest, DecentHolograms does not support custom model data in items lines!**

### Hidden Holograms
Installing either of these plugins will enable you to create hidden holograms, which will be shown to players only if they meet specified conditions.

In order to create a hologram, you have to add a `holograms` section. Add a node named as your hologram to this section
and define `lines`, `conditions` and `location` subnodes. The first one should be a list of texts - these will be the lines
of a hologram. Color codes are supported. Second is a list of conditions separated by commas. Third is a location in a standard
format, like in `teleport` event. An example of such hologram definition:

```YAML
holograms:
  beton:
    lines:
    - 'item:custom_item'
    - '&2Top questers this month'
    - 'top:completed_quests;desc;10;&a;§6;2;&6'
    - '&2Your amount: &6%point.completed_quests.amount%'
    - '&Total amount: &6%azerothquests.globalpoint.total_completed_quests.amount%'
    conditions: has_some_quest, !finished_some_quest    
    location: 100;200;300;world
    # How often to check conditions (optional)
    check_interval: 20
```

#### Item Lines
A line can also represent a floating item. To do so enter the line as 'item:`custom_item`'. It will be replaced with the
`custom_item` defined in the `items` section. If the Item is defined for example as map, a floating map will be seen between two lines of text.
!!! bug ""
    **When used by external plugins like BetonQuest, DecentHolograms does not support custom model data in items lines!**

#### Ranking Holograms
Holograms created by BetonQuest can rank users by the score of a point. Such scoreboards (not to be confused with the
Minecraft vanilla scoreboard) are configured as one line and replaced by multiple lines according to the limit definition.
Each scoreboard line comes in the format `#. name - score` The short syntax is 'top:`point`;`order`;`limit`'. The specified
`point` must be located inside the package the hologram is declared in. To use a point from another package, put `package.point`
instead. The `order` is either 'desc' for descending or 'asc' for ascending. If something other is specified, descending will
be used by default. The limit should be a positive number. In the short declaration, the whole line will be white. To color
each of the four elements of a line (place, name, dash and score), the definition syntax can be extended to
'top:`point`;`order`;`limit`;`c1`;`c2`;`c3`;`c4`'. The color codes can be prefixed with either `§` or `&`, but do not have
to be. If for example `c2` is left blank (two following semicolons), it is treated as an 'f' (color code for white).

Each BetonQuest variable can be displayed on a hologram in a text line. These variables use the same definition syntax as
in conversations such that; '`%package.variable%`'. Where the `package` part is optional if the hologram is defined in the
same package as the variable. If you wish to refer to a variable that is *not* in the same package as the hologram, then you
must specify a [package](Packages-&-Templates.md) before the `variable`.

!!! warning "Potential lags"
    The HolographicDisplays documentations warns against using too many individual hologram variables since they are rendered
    for each player individually. If you are using HolographicDisplays, to save resources, it is recommended to minimise the use of non-static variables.

The hologram's conditions are checked every 10 seconds, meaning a hologram will respond to a condition being met or un-met
every 10 seconds. If you want to make it faster, add `hologram_update_interval` option in _config.yml_ file and set it to a
number of ticks you want to pass between updates (one second is 20 ticks). Don't set it to 0 or negative numbers, it will result in an error.

Keep in mind that each hologram plugin also updates it's holograms on a timer individually, meaning that hologram variables will refresh at a much quicker rate than the above.

### NPC Holograms

If Citizens is also installed then you can have holograms configured relative to an npc. Add the following:

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
!!! bug ""
    **When used by external plugins like BetonQuest, DecentHolograms does not support custom model data in items lines!**

## [JobsReborn](https://www.spigotmc.org/resources/4216/)

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


## [LuckPerms](https://luckperms.net/)

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

## [Magic](http://dev.bukkit.org/bukkit-plugins/magic/)

### Conditions

#### Wand: `wand`

This condition can check wands. The first argument is either `hand`, `inventory` or `lost`. If you choose `lost`, the condition will check if the player has lost a wand. If you choose `hand`, the condition will check if you're holding a wand in your hand. `inventory` will check your whole inventory instead of just the hand. In case of `hand` and `inventory` arguments you can also add optional `name:` argument followed by the name of the wand (as defined in _wands.yml_ in Magic plugin) to check if it's the specific type of the wand. In the case of `inventory` you can specify an amount with `amount` and this will only return true if a player has that amount. You can also use optional `spells:` argument, followed by a list of spells separated with a comma. Each spell in this list must have a minimal level defined after a colon.

!!! example
    ```YAML
    wand hand name:master spells:flare:3,missile:2
    ```

## [McMMO](https://www.spigotmc.org/resources/64348/)

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

## TeamRequiem ([MMOCore](https://www.spigotmc.org/resources/70575/), [MMOItem](https://www.spigotmc.org/resources/39267/), [MythicLib](https://www.spigotmc.org/resources/73855/))


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

#### Cast item ability: `mmoitemcastability`
This objective requires the player to cast an ability using an item. The only argument is the abilityID.
```YAML linenums="1"
mmoitemcastability LIFE_ENDER
```

#### Cast class skill: `mmocorecastskill`
This objective requires the player to cast a class skill. The only argument is the abilityID.
```YAML linenums="1"
mmocorecastskill BACKSTAB
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


## [MythicMobs](http://dev.bukkit.org/bukkit-plugins/mythicmobs/)
!!! info ""
    **Required MythicMobs version: _5.0.0_ or above** 


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
| _location_ | [ULF](./Data-Formats.md#unified-location-formating) | :octicons-x-circle-16: | The location to spawn the mob at.                                                                                                       |
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

## [PlaceholderAPI](https://www.spigotmc.org/resources/6245/)

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

## [ProtocolLib](https://www.spigotmc.org/resources/1997/)

### Hiding NPC's
Having ProtocolLib installed will let you hide Citizens NPCs if specified conditions are met.
You can do that by adding a `hide_npcs` section in your package. 
It allows you to assign conditions to specific NPC IDs like so:

```YAML
hide_npcs:
  41: killedAlready,questStarted
  127: '!questStarted'
```

The interval the conditions are checked in can be configured in the [config.yml](./Configuration.md#npc-hider-interval).

### Force Visibility update
You can run the `updatevisibility` event to manually update the visibility. This is useful for performance optimizations
on large servers if used together with the [npc hider interval](./Configuration.md#npc-hider-interval) set to high values.

### Conversation IO: `menu`

ProtocolLib also enables a conversation IO that makes use of a chat menu system.

<video controls loop src="../../_media/content/Documentation/Conversations/MenuConvIO.mp4" width="100%">
  Sorry, your browser doesn't support embedded videos.
</video>

Customize how it looks by adding the following lines to your quest package:

```YAML
menu_conv_io:
  line_length: 50 # (1)!
  refresh_delay: 180 # (2)!
  selectionCooldown: 10 # (3)!

  npc_wrap: '&l &r' # (4)!
  npc_text: '&l &r&f{npc_text}' # (5)!
  npc_text_reset: '&f' # (6)!
  option_wrap: '&r&l &l &l &l &r' # (7)!
  option_text: '&l &l &l &l &r&8[ &b{option_text}&8 ]' # (8)!
  option_text_reset: '&b' # (9)! 
  option_selected: '&l &r &r&7»&r &8[ &f&n{option_text}&8 ]' # (10)!
  option_selected_reset: '&f' # (11)!
  option_selected_wrap: '&r&l &l &l &l &r&f&n' # (12)!

  control_select: jump,left_click # (13)!
  control_cancel: sneak # (14)! 
  control_move: scroll,move # (15)! 

  npc_name_type: chat # (16)!
  npc_name_align: center # (17)!
  npc_name_format: '&e{npc_name}&r' # (18)!
  npc_name_newline_separator: true # (19)!
```

1. Maximum size of a line till its wrapped.
2. Specify how many ticks to auto update display. Default 180.
3. The cooldown for selecting another option after selecting an option. Measured in ticks. 20 ticks = 1 second.
4. What text to prefix each new line in the NPC text that wraps.
5. How to write the NPC text. Replaces {1} with the npcs text.
6. When a color reset is found, what to replace it with.
7. What text to prefix each new line in an option that wraps.
8. How to write an option. Replaces {1} with the option text.
9. When a color reset is found, what to replace it with.
10. How to write a selected option. Replaces {1} with the option text.
11. When a color reset is found, what to replace it with.
12. What text to prefix each new line in a selected option that wraps.
13. Comma separated actions to select options. Can be any of `jump`, `left_click`, `sneak`.
14. Comma separated actions to cancel the conversation. Can be any of `jump`, `left_click`, `sneak`.
15. Comma separated actions to move the selection. Can be any of `move`, `scroll`.
16. Type of NPC name display. Can be one of: `none`, `chat`.
17. For npc name type of `chat`, how to align name. One of: `left`, `right`, `center`.
18. How to format the npc name.
19. Whether an empty line is inserted after the NPC's name if there is space leftover.

Variables:

  * `{npc_text}` - The text the NPC says
  * `{option_text}` - The option text
  * `{npc_name}` - The name of the NPC

### Chat Interceptor: `packet`

Intercepts pretty much anything sent to the player by intercepting packets sent to them. This can be enabled by default by setting the `default_interceptor` to `packet` in config.yml or per conversation by setting `interceptor` to `packet` in the top level of the conversation.

### Freeze players: 'freeze'
This event allows you to freeze player for the given amount of ticks:
```YAML
freezeMe: "freeze 100" #Freezes the player for 5 seconds
```


## [Quests](https://www.spigotmc.org/resources/3711/)

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

## :material-sword-cross: [ProSkillAPI](https://www.spigotmc.org/resources/91913/)

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

## [Skript](http://dev.bukkit.org/bukkit-plugins/skript/)

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

## :material-treasure-chest: [Vault](http://dev.bukkit.org/bukkit-plugins/vault/)

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

## [WorldEdit](http://dev.bukkit.org/bukkit-plugins/worldedit/) or [FastAsyncWorldEdit](https://www.spigotmc.org/resources/13932/)

### Events

#### Paste schematic: `paste`

**persistent**, **static**

This event will paste a schematic at the given location. The first argument is a location and the second one is the name of schematic file. The file must be located in `WorldEdit/schematics` or `FastAsyncWorldEdit/schematics` and have a name like `some_building.schematic`. An optional `noair` can be added to paste ignoring air blocks.
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
