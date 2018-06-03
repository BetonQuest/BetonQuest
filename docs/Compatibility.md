# Compatibility

BetonQuest can hook into other plugins to extend its functionality. Currently there are 19 plugins: Citizens, Vault, EffectLib, MythicMobs, Magic, Skript, Denizen, WorldEdit, WorldGuard, mcMMO, Heroes, SkillAPI, RacesAndClasses, LegendQuest, Shopkeepers, Quests, PlaceholderAPI, HolographicDisplays and PlayerPoints.

## [Citizens](http://dev.bukkit.org/bukkit-plugins/citizens/)

If you have this plugin you can use it's NPCs for conversations. I highly recommend you installing it, these NPCs are way more immersive. Having Citizens also allows you to use NPCKill objective.

_Notice: When you use Citizens, in main.yml you need to specify the ID of the NPC instead of the name!_

### NPC kill objective: `npckill`

NPC Kill objective requires the player to kill an NPC with the given ID. You can also define how many times an NPC has to be killed. Right after objective's name there must be na ID of the NPC. You can also add an amount by `amount:`.

**Example**: `npckill 16 amount:3 events:reward`

### NPC interact objective: `npcinteract`

The player has to right-click on the NPC with specified ID. It can also optionally cancel the action, so the conversation won't start. The first argument is number (ID of the NPC), and the second is optional `cancel`.

**Example**: `npcinteract 3 cancel conditions:sneak events:steal`

### Move NPC event: `movenpc`

This event will make the NPC move to a specified location. It will not return on its own, so you have to set a single path point with _/npc path_ command - it will then return to that point every time. If you make it move too far away, it will teleport or break, so beware. You can change maximum pathfinding range in Citizens configuration files. The first argument in this event is ID of the NPC to move. Second one is a location in a standard format (like in `teleport` event). You can also specify additional arguments: `wait:` is a number of tick the NPC will wait at its destination before firing events, `done:` is a list of events fired after reaching the destination, `fail:` is a list of events fired if this event fails. Move event can fail if the NPC is already moving for another player.

**Example**: `movenpc 121 100;200;300;world wait:20 done:msg_were_here,give_reward fail:msg_cant_go,give_reward`

## [Vault](http://dev.bukkit.org/bukkit-plugins/vault/)

By installing Vault you enable Permission event and Money condition/event.

### Permission event: `permission`

Adds or removes a permission or a group. First argument is `add` or `remove`. It's self-explanatory. Second is `perm` or `group`. It also shouldn't be hard to figure out. Next thing is actual string you want to add/remove. At the end you can also specify world in which you want these permissions. If the world name is ommited then permission/group will be global.

**Example**: `permission remove group bandit world_nether`

### Money event: `money`

Deposits, withdraws or multiplies money on player's account. There is only one argument, amount of money to modify. It can be positive, negative or start with an asterisk for multiplication.

**Example**: `money -100`

### Money condition: `money`

Checks if the player has specified amount of money. You can specify only one argument, amount integer. It cannot be negative!

**Example**: `money 500`

### Money variable: `money`

There is only one argument in this variable, `amount` for showing money amount or `left:` followed by a number for showing the difference between it and amount of money.

**Example**: `%money.left:500%`

## [MythicMobs](http://dev.bukkit.org/bukkit-plugins/mythicmobs/)

Having MythicMobs allows you to use MythicMobs MobKill objective and MythicMobs SpawnMob event.

### MobKill objective: `mmobkill`

To complete this objective you need to kill specified amount of MythicMobs. The first argument must be the mob's internal name (the one defined in MythicMobs' configuration). You can optionally add `amount:` argument to specify how many of these mobs the player needs to kill. You can also add "notify" keyword if you want to display to players the amount of mobs left to kill.

**Example**: `mmobkill SkeletalKnight amount:2 events:reward`

### SpawnMob event: `mspawnmob`

Spawn specified amount of MythicMobs at given location. The first argument is a location defined like `100;200;300;world`. Second is MythicMobs internal name (the one defined in MythicMobs' configuration) followed by a colon and a level. Third one is amount and it's required!

**Example**: `mspawnmob 100;200;300;world SkeletalKnight:1 5`

## [McMMO](http://dev.bukkit.org/bukkit-plugins/mcmmo/)

### Level condition: `mcmmolevel`

This conditions checks if the player has high enough level in the specified skill. The first argument is the name of the skill, second one is the minimum level the player needs to have to pass this condition.

**Example**: `mcmmolevel woodcutting 50`

### Experience event: `mcmmoexp`

This event adds experience points in a specified skill. The first argument is the name of the skill, second one is the amount of experience to add.

**Example**: `mcmmoexp swords 1500`

## [Skript](http://dev.bukkit.org/bukkit-plugins/skript/)

BetonQuest can also hook into Skript. Firstly, to avoid any confusion, I will refere to everything here by name of the plugin (Skript event is something else than BetonQuest event). Having Skript on your server will enable using BetonQuest events and conditions in scripts, and also trigger them by BetonQuest event.

### Skript event triggered by BetonQuest `skript` event

This entry will describe two things: Skript event and BetonQuest event.

1. **Skript event** - `on [betonquest] event "id"` - this is the line you use in your scripts to trigger the code. `betonquest` part is optional, and `id` is just some string, which must be equal to the one you specified in BetonQuest event.
2. **BetonQuest event** - `skript` - this event will trigger the above Skript event in your scripts. The instruction string accepts only one argument, id of the event. It have to be the same as the one defined in Skript event for it to be triggered.

**Example**: _in your script:_ `on betonquest event "concrete":` _in events.yml:_ `fire_concrete_script: skript concrete`

### Skript condition

You can check BetonQuest conditions in your scripts by using the syntax `player meets [betonquest] condition "id"`. `betonquest` is optional, and `id` is the name of the condition, as defined in _conditions.yml_.

**Example**: _in your script:_ `player meets condition "has_ore"` _in conditions.yml:_ `has_ore: item iron_ore:5`

### Skript event

You can also fire BetonQuest events with scripts. The syntax for Skript effect is `fire [betonquest] event "id" for player`. Everything else works just like in condition above.

**Example**: _in your script:_ `fire event "give_emeralds" for player` _in events.yml:_ `give_emeralds: give emerald:5`

## [WorldGuard](http://dev.bukkit.org/bukkit-plugins/worldguard/)

### Region objective: `region`

To complete this objective you need to enter WorldGuard region with specified name. The only argument in instruction string is name of the region.

**Example**: `region beton events:kill`

### Region condition: `region`

This condition is met when the player is inside the specified region. The only argument is the name of the region.

**Example**: `region beton`

## [WorldEdit](http://dev.bukkit.org/bukkit-plugins/worldedit/)

### Paste schematic event: `paste`

This event will paste a schematic at the given location. The first argument is a location and the second one is the name of schematic file. The file must be located in `WorldEdit/schematics` and have a name like `some_building.schematic`.

**Example**: `paste 100;200;300;world some_building`

## [EffectLib](http://dev.bukkit.org/bukkit-plugins/effectlib/)

If you install this plugin on your server you will be able to set a particle effect on NPCs with conversations and use `particle` event.

You can control the behaviour of particles around the NPCs in _custom.yml_ file, in `npc_effects` section. Each effect is defined as a separate subsection and consists of EffectLib options (described on the EffectLib page) and several BetonQuest settings. `npcs` is a list of all NPCs on which this effect can be displayed. `conditions` is a list of conditions the player has to meet in order to see the effect. BetonQuest will find the first effect which can be displayed and show it to the player. `interval` controls how often the effect is displayed (in ticks). The effect will be fired from the exact location of the NPC, upwards.

### Particle event: `particle`

This event will load an effect defined in `effects` section in _custom.yml_ file and display it on player's location. The only argument is the name of the effect. You can optionally add `loc:` argument followed by a location written like `100;200;300;world;180;-90` to put it on that location.

**Example in _custom.yml_**:

    effects:
      beton:
        class: HelixEffect
        iterations: 100
        particle: smoke
        helixes: 5
        circles: 20
        grow: 3
        radius: 30

**Example**: `particle beton loc:100;200;300;world;180;-90`

## [PlayerPoints](http://dev.bukkit.org/bukkit-plugins/playerpoints/)

### PlayerPoints event: `playerpoints`

This event simply adds, removes or multiplies points in the PlayerPoints plugin. The only argument is a number, it can be positive, negative or prefixed with an asterisk for multiplication.

**Example**: `playerpoints *2`

### PlayerPoints condition: `playerpoints`

This condition simply checks if the player has specified amount of points in the PlayerPoints plugin. The only argument is a number.

**Example**: `playerpoints 100`

## [Heroes](http://dev.bukkit.org/bukkit-plugins/heroes/)

When you install Heroes, all kills done via this plugin's skills will be counted in MobKill objectives.

### Experience event: `heroesexp`

This event simply gives the player specified amount of Heroes experience. The first argument is either `primary` or `secondary` and it means player's class. Second one is the amount of experience to add.

**Example**: `heroesexp primary 1000`

### Class condition: `heroesclass`

This condition checks the classes of the player. The first argument must be `primary`, `secondary` or `mastered`. Second is the name of a class or `any`. You can optionally specify `level:` argument followed by the required level of the player.

**Example**: `heroesclass mastered warrior`

### Skill condition: `heroesskill`

This condition checks if the player can use specified skill. The first argument is the name of the skill.

**Example**: `heroesskill charge`

## [Magic](http://dev.bukkit.org/bukkit-plugins/magic/)

### Wand condition: `wand`

This condition can check wands. The first argument is either `hand`, `inventory` or `lost`. If you choose `lost`, the condition will check if the player has lost a wand. If you choose `hand`, the condition will check if you're holding a wand in your hand. `inventory` will check your whole inventory instead of just the hand. In case of `hand` and `inventory` arguments you can also add optional `name:` argument followed by the name of the wand (as defined in _wands.yml_ in Magic plugin) to check if it's the specific type of the wand. You can also use optional `spells:` argument, followed by a list of spells separated with a comma. Each spell in this list can have defined minimal level required, after a colon.

**Example**: `wand hand name:master spells:flare,missile:2`

## [Denizen](http://dev.bukkit.org/bukkit-plugins/denizen/)

### Script event: `script`

With this event you can fire Denizen task scripts. Don't confuse it with `skript` event, these are different. The first and only argument is the name of the script.

**Example**: `script beton`

## [SkillAPI](http://dev.bukkit.org/bukkit-plugins/skillapi/)

### Class condition: `skillapiclass`

This condition checks if the player has specified class or a child class of the specified one. The first argument is simply the name of a class. You can add `exact` argument if you want to check for that exact class, without checking child classes.

**Example**: `skillapiclass warrior`

### Level condition: `skillapilevel`

This condition checks if the player has specified or greater level is the specified class. The first argument is class name, the second one is the required level.

**Example**: `skillapilevel warrior 3`

## [Quests](http://dev.bukkit.org/bukkit-plugins/quests/)

Quests is another questing plugin, which offers very simple creation of quests. If you don't want to spend a lot of time to write advanced quests in BetonQuest but you need a specific thing from this plugin you can use Custom Event Reward or Custom Condition Requirement. Alternatively, if you have a lot of quests written in Quests, but want to integrate them with the conversation system, you can use `quest` event and `quest` condition.

### Event Reward (Quests)

When adding rewards to a quest or a stage, choose "Custom reward" and then select "BetonQuest event". Now specify event's name and it's package (like `package.eventName`). Quests will fire BetonQuest event when this reward will run.

### Condition Requirement (Quests)

When adding requirements to a quest, choose "Custom requirement" and then select "BetonQuest condition". Now specify condition's name and it's package (like `package.conditionName`). Quests will check BetonQuest condition when starting the quest.

### Quest event: `quest` (BetonQuest)

This event will start the quest for the player. The first argument must be the name of the quest, as defined in `name` option in the quest. If the name contains any spaces replace them with `_`. You can optionally add `check-requirements` argument if you want the event to respect this quest's requirements (otherwise the quest will be forced to be started).

**Example**: `quest stone_miner check-requirements`

### Quest condition: `quest` (BetonQuest)

This condition is met when the player has completed the specified quest. The first and only argument is the name of the quest. It it contains any spaces replace them with `_`.

**Example**: `quest stone_miner`

## [Shopkeepers](http://dev.bukkit.org/bukkit-plugins/shopkeepers/)

### Open shop window event: `shopkeeper`

This event opens a trading window of a Villager. The only argument is the uniqueID of the shop. You can find it in _Shopkeepers/saves.yml_ file, under `uniqueID` option.

**Example**: `shopkeeper b687538e-14ce-4b77-ae9f-e83b12f0b929`

### Shop amount condition: `shopamount`

This condition checks if the player owns specified (or greater) amount of shops. It doesn't matter what type these shops are. The only argument is a number - minimum amount of shops.

**Example**: `shopamount 2`

## [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)

If you have this plugin, BetonQuest will add a `betonquest` placeholder to it and you will be able to use `ph` variable in your conversations.

### Placeholder: `betonquest`

In any other plugin which uses PlaceholderAPI you can use BetonQuest variables with `%betonquest_package:variable%` placeholder. The `package:` part is the name of a package. If you skip this, the plugin will assume you're using that variable in `default` package. The `variable` part is just a BetonQuest variable without percentage characters, like `point.beton.amount`.

**Example**: `%betonquest_someGreatQuest:objective.killZombies.left%`

### Variable: `ph`

You can also use placeholders from other plugins in BetonQuest. Simply insert a variable starting with `ph`, the second argument should be the placeholder without percentage characters.

**Example**: `%ph.player_item_in_hand%`

## [HolographicDisplays](http://dev.bukkit.org/bukkit-plugins/holographic-displays/)

Installing this plugin will enable you to create hidden holograms, which will be shown to players only if they meet specified conditions. Note that you need to have [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/) installed in order to hide holograms from certain players.

In order to create a hologram, you have to add `holograms` section in your _custom.yml_ file. Add a node named as your hologram to this section and define `lines`, `conditions` and `location` subnodes. The fist one should be a list of texts - these will be the lines of a hologram. Color codes are supported. Second is a list of conditions separated by commas. Third is a location in a standard format, like in `teleport` event. An example of such hologram definition:

    holograms:
      beton:
        lines:
        - '&bThis is Beton.'
        - '&eBeton is strong.'
        location: 100;200;300;world
        conditions: has_some_quest, !finished_some_quest

The holograms are updated every 10 seconds. If you want to make it faster, add `hologram_update_interval` option in _config.yml_ file and set it to a number of ticks you want to pass between updates (one second is 20 ticks). Don't set it to 0 or negative numbers, it will result in an error.

## [RacesAndClasses](http://dev.bukkit.org/bukkit-plugins/racesandclasses/)

Another race/class/skill plugin. By installing RacesAndClasses you gain access to these events, conditions and variables:

### Class condition: `racclass`

This conditions checks if the player has specified class. You can use `none` to check if he does not have any class.

**Example**: `racclass warrior`

### Class event: `racclass`

This event sets player's class to the specified one.

**Example**: `racclass magician`

### Class variable: `racclass`

This variable resolves to class name.

**Example**: `%racclass%`

### Race condition: `racrace`

This condition checks if the player has specified race. You can use `none` to check if he does not have any race.

**Example**: `racrace Elv`

### Race event: `racrace`

This event sets player's race to the specified one.

**Example**: `racrace Orc`

### Race variable: `racrace`

This variable resolves to race name.

**Example**: `%racrace%`

### Level condition: `raclevel`

This condition is met if the player has a level equal or greater than specified.

**Example**: `raclevel 5`

### Level event: `raclevel`

This event adds (or removes if negative) levels.

**Example**: `raclevel -2`

### Level variable: `raclevel`

This variable has 2 possible arguments: `amount` will resolve to player's level and `left:` will display how many levels the player lacks to the specified number.

**Example**: `%raclevel.left:5%`

### Experience condition: `racexo`

This condition is met if the player has experience equal or greater than specified.

**Example**: `racexo 600`

### Experience event: `racexo`

This event adds (or removes if negative) experience.

**Example**: `racexo 100`

### Experience variable: `racexo`

This variable has 2 possible arguments: `amount` will resolve to player's experience and `left:` will display how much experience the player lacks to the specified number.

**Example**: `%racexo:amount%`

### Mana condition: `racmana`

This condition is met if the player has mana equal or greater than specified.

**Example**: `racmana 1`

### Mana event: `racmana`

This event adds (or removes if negative) mana. You can use `refill` instead of a number to simply set mana to player's maximum.

**Example**: `racmana refill`

### Trait condition: `ractrait`

This condition checks if the player has the specified trait.

**Example**: `ractrait SwordDamageIncreaseTrait`

## [LegendQuest](http://dev.bukkit.org/bukkit-plugins/legendquest/)

### Class condition: `lqclass`

Checks if the player has specified class. It can also check subclass if you add `--subclass` argument.

**Example**: `lqclass Cleric`

### Race condition: `lqrace`

Checks if the player has specified race.

**Example**: `lqrace Dwarf`

### Attribute condition: `lqattribute`

Checks player's attributes. The first argument is attribute (STR, CON, DEX, INT, WIS, CHR) and the second argument is a number - minimal required level of the attribute.

**Example**: `lqattribute INT 10`

### Karma condition: `lqkarma`

Checks if the player has specified amount of karma. The only argument is a number - minimal amount of karma required.

**Example**: `lqkarma 20`

### Class variable: `lqclass`

Resolves to player's class.

**Example**: `%lqclass%`

### Race variable: `lqrace`

Resolves to player's race.

**Example**: `%lqrace%`

### Attribute variable: `lqattribute`

Resolves to player's attribute. The first argument is name of the attribute (like in `lqattribute` condition), second one is either `amount` or `left:` followed by a number. First of these will simply display attribute level and second will display the difference between attribute level and the number.

**Example**: `%lqattribute.str.left:13`

### Karma variable: `lqkarma`

Resolves to player's karma. The only argument here is either `amount` or `left:` followed by a number. First of these will simply display karma amount and second will display the difference between karma amount and the number.

**Example**: `%lqkarma.amount%`

## [BetonLangAPI](https://github.com/Co0sh/BetonLangAPI)

When BetonLangAPI is installed on the server BetonQuest will integrate its translation system with that plugin. Changing the language with _/lang_ command or `language` event will change the language globally, not only in one of those plugins. The _/questlang_ command will not modify BetonLangAPI language though!

## [BountifulAPI](https://www.spigotmc.org/resources/bountifulapi-1-8-1-12-1.1394/)

### Title event: `title`

BountifulAPI enables you to use `title` event without spamming the console with `/title` command output. The syntax is exactly the same as in regular `title` event described in _Events List_.

**Example**: `title subtitle 0;0;0 {en} Lobby joined! {pl} Dołączono do lobby!`

## [ProtocolLib](https://www.spigotmc.org/resources/protocollib.1997/)

Having ProtocolLib installed will let you hide Citizens NPCs if specified conditions are met. You can do that by adding `hide_npcs` section to _custom.yml_ file in your package. There you can assign conditions to specific NPC IDs:

```
hide_npcs:
  41: killedAlready,questStarted
  127: '!questStarted'
```
