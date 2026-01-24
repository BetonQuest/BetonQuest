---
icon: octicons/question-16
tags:
  - Condition
---
# Conditions List

## Has advancement

__Context__: @snippet:condition-meta:online@  
__Syntax__: `advancement <name>`  
__Description__: Whether the player has the specified advancement.

The only argument is the name of the advancement.
The namespace can be omitted for the `minecraft` default namespace.
The name must be in the format as configured on your server.  
[List of all vanilla advancements](https://minecraft.wiki/w/Advancement#List_of_advancements).

```YAML title="Example"
conditions:
  killedAllMobs: "advancement adventure/kill_all_mobs"
  killedAMob: "advancement minecraft:adventure/kill_a_mob"
```

## Conjunction

__Context__: @snippet:condition-meta:independent@  
__Syntax__: `and <conditions>`  
__Description__: The conjunction of the specified conditions.

This means that every condition has to be met in order for conjunction to be true. Used only in complex alternatives, because conditions generally work as conjunction. Instruction string is exactly the same as in `alternative`.

```YAML title="Example"
conditions:
  hasArmor: "and has_helmet,has_chestplate,has_leggings,has_boots"
```

## Is wearing armor

__Context__: @snippet:condition-meta:online@  
__Syntax__: `armor <item>`  
__Description__: Whether the player is wearing the specified armor specified in the _items_ section.

```YAML title="Example"
conditions:
  armorHelmet: "armor helmet_of_concrete"
```

## Is in biome

__Context__: @snippet:condition-meta:online@  
__Syntax__: `biome <biome>`  
__Description__: Whether the player is in the specified biome.

The only argument is the [biome type](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/Biome.html).

```YAML title="Example"
conditions:
  inSavannaRock: "biome savanna_rock"
```

## Is burning

__Context__: @snippet:condition-meta:online@  
__Syntax__: `burning`  
__Description__: Whether the player is on fire.

```YAML title="Example"
conditions:
  isOnFire: "burning"
```

## Check conditions inline

__Context__: @snippet:condition-meta:independent@  
__Syntax__: `check <conditions>`  
__Description__: Allows you to specify multiple instructions in one long conjunctive instruction.

Each instruction must be started with `^` character and no other dividers should be used. The condition will be met if all inner conditions are met. It's not the same as `and` condition, because you can specify an instruction string, not a condition name.

```YAML title="Example"
conditions:
  fulfillRequirements: "check ^tag beton ^item emerald:5 ^location 100;200;300;survival_nether;5 ^experience 20"
```

## Are items in a chest

__Context__: @snippet:condition-meta:independent@  
__Syntax__: `chestitem <location> <items>`  
__Description__: Whether the specified items are in the specified chest.

This condition works in the same way as `item` condition, but it checks the specified chest instead of a player. The first argument is a location of the chest and the second one is the list of items defined in the same way as in `item` condition. If there is no chest at specified location the condition won't be met.

```YAML title="Example"
conditions:
  emeraldsInChest: "chestitem 100;200;300;world emerald:5,sword"
```

## Is conversation startable

__Context__: @snippet:condition-meta:online-offline@  
__Syntax__: `conversation <conversation>`  
__Description__: Whether the conversation has an available starting option.

If no starting option has a condition that returns true then this will return false.

```YAML title="Example"
conditions:
  isInInnkeeperConv: "conversation innkeeper"
```

## Is day of the week

__Context__: @snippet:condition-meta:independent@  
__Syntax__: `dayofweek <day>`  
__Description__: Whether the current day of the week is the specified one.

You can specify either the english name of the day or the number of the day (1 being monday, 7 sunday,...).

```YAML title="Example"
conditions:
  isSunday: "dayofweek sunday"
```

## Has potion effect

__Context__: @snippet:condition-meta:online@  
__Syntax__: `effect <effect>`  
__Description__: Whether the player has the specified potion effect.

There is only one argument and it takes values from this page: [potion types](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/potion/PotionEffectType.html).

```YAML title="Example"
conditions:
  hasSpeed: "effect SPEED"
```

## Has empty inventory slots

__Context__: @snippet:condition-meta:online@  
__Syntax__: `empty <amount>`  
__Description__: Whether the player has at least the specified amount of empty inventory slots.

If you want to check for a specific amount (for example for a full inventory with 0 empty slots) you can append the `equal` argument.

```YAML title="Example"
conditions:
  hasFiveEmptySlots: "empty 5"
```

## Are entities in area

__Context__: @snippet:condition-meta:independent@  
__Syntax__: `entities <entities> <location> <range> [name] [marked]`  
__Description__: Whether there are at least the specified amount of specified entities in the specified area.

There are three required arguments - entity type, location and range. Entities are defined as a list separated by commas.
Each entity type (taken from [here](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html))
can have an additional amount suffix, for example `ZOMBIE:5,SKELETON:2` means 5 or more zombies and 2 or more skeletons.
The location is defined as usual. The number after the location is the range around the location in which will be checked for these entities. You can also specify additional `name:` argument,
with the name of the required entity. You can use `marked:` argument to check only for entities marked in `spawn` action.

```YAML title="Example"
conditions:
  daemonsSpawned: "entities ZOMBIE:2 100;200;300;world 10 name:Daemon"
```

## Evaluate a condition

__Context__: @snippet:condition-meta:independent@  
__Syntax__: `eval <expression>`  
__Description__: Evaluate the expression and checks the resulting condition.

This condition allows you to resolve an expression containing placeholders, and the result will then be interpreted
again as a condition.

```YAML title="Example"
conditions:
  simpleEval: "eval chestitem -288;64;357;World emerald:5"
  complexEval: "eval point ranking 5 %objective.settings.equal%" #(1)!
```

1. This could evaluate to `point ranking 5 equal` and will be true if the player has 5 points in the ranking category.
   But the placeholder could also be empty, and it could be higher or equal to 5. This is not possible in a normal
   condition.

## Has experience

__Context__: @snippet:condition-meta:online@  
__Syntax__: `experience <amount>`  
__Description__: Whether the player has the specified amount of experience levels.

You can also define decimal numbers, for example `experience 1.5` will be met when the player has 1.5 or more experience levels.
If you want to check for an absolute amount of experience points you can convert it to decimal levels.

```YAML title="Example"
conditions:
  hasLevel30: "experience 30"
  hasLevel5dot5: "experience 5.5"
```

## Is facing in a direction

__Context__: @snippet:condition-meta:online@  
__Syntax__: `facing <direction>`  
__Description__: Whether the player is facing the specified direction.

Valid directions are `UP`, `DOWN`, `NORTH`, `EAST`, `WEST` and `SOUTH`. Up and down start at a pitch of 60°.

```YAML title="Example"
conditions:
  lookingEast: "facing EAST"
```

## Is flying

__Context__: @snippet:condition-meta:online@  
__Syntax__: `fly`  
__Description__: Whether the player is currently flying (Elytra type of flight).

```YAML title="Example"
conditions:
  isFlying: "fly"
```

## Is in gamemode

__Context__: @snippet:condition-meta:online@  
__Syntax__: `gamemode <gamemode>`  
__Description__: Whether the player is in the specified gamemode.

The first argument is the game mode, i.e. survival, creative, adventure.

```YAML title="Example"
conditions:
  isInSurvival: "gamemode survival"
```

## Has global point

__Context__: @snippet:condition-meta:independent@  
__Syntax__: `globalpoint <category> <point>`  
__Description__: Whether the specified global point category has the specified amount of points.

```YAML title="Example"
conditions:
  has100Users: "globalpoint global_knownusers 100"
```

## Has global tag

__Context__: @snippet:condition-meta:independent@  
__Syntax__: `globaltag <tag>`  
__Description__: Whether the specified global tag is set.

```YAML title="Example"
conditions:
  areNpcsAggressive: "globaltag global_areNPCsAggressive"
```

## Has item in a hand

__Context__: @snippet:condition-meta:online@  
__Syntax__: `hand <item> [offhand]`  
__Description__: Whether the player holds the specified quest item in the hand.

The offhand will be checked instead of the main hand if the `offhand` keyword is added.
Amount cannot be set here, though it may be checked with the `item` condition.

```YAML title="Example"
conditions:
  holdSword: "hand SpecialSword"
  holdShieldOffhand: "hand QuestShield offhand"
```

## Has health

__Context__: @snippet:condition-meta:online@  
__Syntax__: `health <amount>`  
__Description__: Whether the player has at least the specified amount health.

The only argument is a number (double).
Players can have 0 to 20 health by default (there are some plugins and commands which change the maximum)
(0 means dead, don't use that since it will only be met when the player sees the red respawn screen).

```YAML title="Example"
conditions:
  has5dot6Health: "health 5.6"
```

## Is below height

__Context__: @snippet:condition-meta:online@  
__Syntax__: `height <amount>`  
__Description__: Whether the player is _below_ the specified height.

The required argument is a number or a location (for example 100;200;300;world).
In case of location it will take the height from it and use it as regular height.

```YAML title="Example"
conditions:
  isBelow16: "height 16"
```

## Has hunger

__Context__: @snippet:condition-meta:online@  
__Syntax__: `hunger <amount>`  
__Description__: Whether the player has at least the specified amount of hunger.

If the hunger level is below 7, the player cannot sprint.

```YAML title="Example"
conditions:
  has15orMoreHunger: "hunger 15"
```

## Is in conversation

__Context__: @snippet:condition-meta:online@  
__Syntax__: `inconversation [conversation]`  
__Description__: Whether the player is currently in the specified conversation.

| Parameter      | Syntax              | Default Value          | Explanation                                                                                              |
|----------------|---------------------|------------------------|----------------------------------------------------------------------------------------------------------|
| _conversation_ | `conversation:name` | :octicons-x-circle-16: | Optional name of the conversation. If specified, it will only check for the conversation with this name. |

```YAML title="Example"
conditions:
  isInConversation: "inconversation"
  talksToInnkeeper: "inconversation conversation:innkeeper"
```

## Has items

__Context__: @snippet:condition-meta:online@  
__Syntax__: `item <items>`  
__Description__: Whether the player has the specified items in his inventory or backpack.

You specify items in a list separated by commas (without spaces between!).
Each item consists of its name and amount, separated by a colon.
Amount is optional, so if you specify just item's name the plugin will assume there should be only one item.

```YAML title="Example"
conditions:
  emeraldsAndGold: "item emerald:5,gold:10"
```

## Has durability

__Context__: @snippet:condition-meta:online@  
__Syntax__: `itemdurability <slot> <amount> [relative]`  
__Description__: Whether the player has a certain amount of durability on an item.

The first argument is the slot, the second the amount.
Optional `relative` argument sets 0 to broken and 1 to the maximum durability the item can have.
This condition returns false when no item is in the given slot or does not have durability, like stone or sticks.
Available slot types: `HAND`, `OFF_HAND`, `HEAD`, `CHEST`, `LEGS`, `FEET`.

```YAML title="Example"
conditions:
  handOver50: "itemdurability HAND 50"
  chestMoreThan50Percent: "itemdurability CHEST 0.5 relative"
```

## Has journal entry

__Context__: @snippet:condition-meta:online-offline@  
__Syntax__: `journal <entry>`  
__Description__: Whether the player has the specified journal entry.

The only argument is name of the entry.

```YAML title="Example"
conditions:
  journalHasWoodStarted: "journal wood_started"
```

## Has language selected

__Context__: @snippet:condition-meta:online-offline@  
__Syntax__: `language <languages>`  
__Description__: Whether the player has one of the specified languages selected.

```YAML title="Example"
conditions:
  hasValidLanguage: "language en-US,de-DE,fr-FR"
```

## Is at location

__Context__: @snippet:condition-meta:online@  
__Syntax__: `location <location> <radius>`  
__Description__: Whether the player is at the specified location within the specified radius.

Just two mandatory attributes - location and radius around it.

```YAML title="Example"
conditions:
  netherEntrance: "location 100;200;300;survival_nether 5"
```

## Is looking at a block

__Context__: @snippet:condition-meta:online@  
__Syntax__: `looking [location | type]`  
__Description__: Whether the player is looking towards the specified block at the specified location or with the specified material.

You must specify either `loc:` optional (the location of the block) or `type:` optional as a `block selector`.
You can also specify both.

```YAML title="Example"
conditions:
  targetingStone: "looking loc:12.0;14.0;-15.0;world type:STONE"
```

## Is in moonphase

__Context__: @snippet:condition-meta:independent@  
__Syntax__: `moonphase <moonphase> [world]`  
__Description__: Whether the specified moonphase matches the one in the specified world. 

| Parameter   | Syntax                                                                                                    | Default Value          | Explanation                            |
|-------------|-----------------------------------------------------------------------------------------------------------|------------------------|----------------------------------------|
| _MoonPhase_ | [Keyword](https://jd.papermc.io/paper/1.21.5/io/papermc/paper/world/MoonPhase.html#enum-constant-summary) | :octicons-x-circle-16: | A list of moon phases to check for.    |
| _world_     | world:name                                                                                                | player location        | The world to check for the moon phase. |


```YAML title="Example"
conditions:
  fullMoon: "moonphase FULL_MOON"
  darkInHub: "moonphase WANING_CRESCENT,NEW_MOON,WAXING_CRESCENT world:hub"
  playersFirstJoinMoon: "moonphase %ph.player_first_join_moon%"
```

## Has distance to npc

__Context__: @snippet:condition-meta:online@  
__Syntax__: `npcdistance <npc> <distance>`  
__Description__: Whether the player is inside the specified radius of the specified npc.

| Parameter  | Syntax   | Default Value          | Explanation          |
|------------|----------|------------------------|----------------------|
| _Npc_      | Npc      | :octicons-x-circle-16: | The ID of the Npc    |
| _Distance_ | Number   | :octicons-x-circle-16: | The maximum distance |

```YAML title="Example"
conditions:
  canHearBandit: "npcdistance bandit 22"
```

## Is npc at location

__Context__: @snippet:condition-meta:independent@  
__Syntax__: `npclocation <npc> <location> <distance>`  
__Description__: Whether the specified npc is at the specified location within the specified radius.

| Parameter  | Syntax   | Default Value          | Explanation          |
|------------|----------|------------------------|----------------------|
| _Npc_      | Npc      | :octicons-x-circle-16: | The ID of the Npc    |
| _Location_ | Location | :octicons-x-circle-16: | The location         |
| _Distance_ | Number   | :octicons-x-circle-16: | The maximum distance |

```YAML title="Example"
conditions:
  nearTarget: "npclocation merchant 4.0;14.0;-20.0;world 22"
```

## Compare numbers

__Context__: @snippet:condition-meta:independent@  
__Syntax__: `numbercompare <number1> <operation> <number2>`  
__Description__: This condition compares two numbers.

The valid operations are: `<`, `<=`, `=`, `!=`, `>=`, `>`.

```YAML title="Example"
conditions:
  hasMoreThan100Points: "numbercompare %ph.other_plugin:points% >= 100"
```

## Has objective

__Context__: @snippet:condition-meta:online-offline@  
__Syntax__: `objective <objective>`  
__Description__: Whether the player has the specified objective.

The only argument is the name of the objective, as defined in the _objectives_ section.

```YAML title="Example"
conditions:
  hasWoodObjective: "objective wood"
```

## Alternative

__Context__: @snippet:condition-meta:independent@  
__Syntax__: `or <conditions>`  
__Description__: The disjunction of the specified conditions.

This means that only one of the conditions has to be met in order for alternative
to be true. You just define one mandatory argument, condition names separated by commas. `!` prefix works as always.

```YAML title="Example"
conditions:
  hasOneRequirement: "or night,rain,!has_armor"
```

## Match date

__Context__: @snippet:condition-meta:independent@  
__Syntax__: `partialdate <pattern>`  
__Description__: Whether the current date matches the specified pattern.

You can specify the day of the month, the month or the year it must be that this condition returns true or combine them.
You can also specify multiple days/months/years by just separating them by `,` or an interval by using `-`.
If you have trouble understanding how this works have a look at the example.

The example is true between the 1st and the 5th or on the 20th of each month, but only in the year 2017.

```YAML title="Example"
conditions:
  isEventDate: "partialdate day:1-5,20 year:2017"
```

## Check conditions for party 

__Context__: @snippet:condition-meta:independent@  
__Syntax__: `party <radius> <conditions> [location] [every] [any] [count]`  
__Description__: Whether the party members fulfill the specified conditions.

This is part of the [party system](../Parties.md).
This condition takes three optional arguments: `every:`, `any:`, `count:` and `location:`.  
"Every" is a list of conditions that must be met by every player in the party.  
Any is a list of conditions that must be met by at least one player in a party (it doesn't have to be the same player,
one can meet first condition, another one can meet the rest, and it will work).  
Count is just a number, minimal amount of players in the party.
Location can be used to create a party without the need of a player that is the center of the party.
You don't have to specify all those arguments, you can use only one if you want.

```YAML title="Example"
conditions:
  partyRequirements: "party 10 has_tag1,!has_tag2 every:some_item any:some_location,some_other_item count:5"
```

## Has permission

__Context__: @snippet:condition-meta:online@  
__Syntax__: `permission <permission>`  
__Description__: Whether the player has the specified permission.

The instruction string must contain permission node as the required argument.

```YAML title="Example"
conditions:
  essentialsTpaPermissions: "permission essentials.tpa"
```

## Has point

__Context__: @snippet:condition-meta:online-offline@  
__Syntax__: `point <category> <amount> [equal]`  
__Description__: Whether the player has at least the specified amount of points in the specified category.

There are two required arguments, first is the category (string), second is the amount (integer).
You can also add optional argument `equal` to accept only players with exactly equal amount of points.

```YAML title="Example"
conditions:
  has20BetonPoints: "point beton 20"
```

## Is mounted

__Context__: @snippet:condition-meta:online@  
__Syntax__: `ride <entity>`  
__Description__: Whether the player is mounted on the specified [entity](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html).

`any` is also a valid input and matches any entity.

```YAML title="Example"
conditions:
  horse: "ride horse"
  anything: "ride any"
```

## Random

__Context__: @snippet:condition-meta:independent@  
__Syntax__: `random <number>`  
__Description__: This condition is met randomly using the specified chance.

There is one argument: two positive numbers like `5-12`. They mean something like that: "It will be true 5 times out of 12".

```YAML title="Example"
conditions:
  twelfthPercent: "random 12-100"
```

## Has armor rating

__Context__: @snippet:condition-meta:online@  
__Syntax__: `rating <amount>`  
__Description__: Whether the player wears armor which gives at least the specified amount of protection (armor icons).

The first and only argument should be an integer. One armor point is equal to half armor icon in-game (10 means half of the bar filled).

```YAML title="Example"
conditions:
  armorRating10: "rating 10"
```

## Is real time

__Context__: @snippet:condition-meta:independent@  
__Syntax__: `realtime <timespan>`  
__Description__: Whether the specified time matches the current real time.

| Parameter  | Syntax            | Default Value          | Explanation                                                                                                  |
|------------|-------------------|------------------------|--------------------------------------------------------------------------------------------------------------|
| _Timespan_ | startTime-endTime | :octicons-x-circle-16: | Two points of time separated by dash in the 24-hour format (0 - 24). The minutes are optional (hh or hh:mm). |


```YAML title="Example"
conditions:
  allDayReal: "realtime 6-19"
  midnightReal: "realtime 23:30-0:30"
  knoppersTimeReal: "realtime 9:30-10"
```

## Has scoreboard score

__Context__: @snippet:condition-meta:online-offline@  
__Syntax__: `score <score> <amount>`  
__Description__: Whether the score in the specified scoreboard objective has at least the specified amount.

| Parameter              | Syntax         | Default Value          | Explanation                               |
|------------------------|----------------|------------------------|-------------------------------------------|
| _scoreboard objective_ | Objective name | :octicons-x-circle-16: | The name of the scoreboard objective      |
| _count_                | Number         | :octicons-x-circle-16: | The minimum whole number of the objective |

```YAML title="Example"
conditions:
  hasAtLeastTenKills: "score kills 10"
```

## Has scoreboard tag

__Context__: @snippet:condition-meta:online@  
__Syntax__: `scoretag <tag>`  
__Description__: Whether the player has the specified scoreboard tag.

The kind of tags that are used by vanilla Minecraft and not the [betonquest tags](#has-tag).

| Parameter        | Syntax   | Default Value          | Explanation                     |
|------------------|----------|------------------------|---------------------------------|
| _scoreboard tag_ | Tag name | :octicons-x-circle-16: | The name of the scoreboard tag. |

```YAML title="Example"
conditions:
  hasVanillaTag: "scoretag vanilla_tag"
```

## Is sneaking

__Context__: @snippet:condition-meta:online@  
__Syntax__: `sneak`  
__Description__: Whether the player is sneaking.

This would probably be useful for creating traps, I'm not sure. There are no arguments for this one.

```YAML title="Example"
conditions:
  isSneaking: "sneak"
```
    
## Compare stage

__Context__: @snippet:condition-meta:online-offline@  
__Syntax__: `stage <objective> <comparator> <stage>`  
__Description__: Compare the player's current stage with the specified stage using its index numbers.

For more take a look at the [stage objective](./Objectives-List.md#complete-stages).  
The valid operations are: `<`, `<=`, `=`, `!=`, `>=`, `>`.

| Parameter         | Syntax     | Default Value          | Explanation                              |
|-------------------|------------|------------------------|------------------------------------------|
| _stage objective_ | Objective  | :octicons-x-circle-16: | The name of the stage objective          |
| _comparator_      | Comparator | :octicons-x-circle-16: | The comparator to use for the comparison |
| _stage_           | Stage      | :octicons-x-circle-16: | The name of the stage to compare         |

```YAML title="Example"
conditions:
  isDeliverCookies: "stage bakeCookies = deliverCookies"
  isDeliverCookiesOrAbove: "stage bakeCookies > cookCookies"
```

## Has tag

__Context__: @snippet:condition-meta:online-offline@  
__Syntax__: `tag <tag>`  
__Description__: Whether the player has the specified tag.

Together with `!` negation it is one of the most powerful tools when creating conversations. The instruction string must contain tag name.

```YAML title="Example"
conditions:
  questCompleted: "tag quest_completed"
```

## Matches block

__Context__: @snippet:condition-meta:independent@  
__Syntax__: `testforblock <location> <selector>`  
__Description__: Whether the block at specified location matches the specified material.

First argument is a location, and the second one is a `block selector`.

```YAML title="Example"
conditions:
  stoneSet: "testforblock 100;200;300;world STONE"
```

## Is time

__Context__: @snippet:condition-meta:independent@  
__Syntax__: `time <timespan> [world]`  
__Description__: Whether the specific time matches the current time in the specified world.

| Parameter  | Syntax            | Default Value          | Explanation                                                                                                  |
|------------|-------------------|------------------------|--------------------------------------------------------------------------------------------------------------|
| _Timespan_ | startTime-endTime | :octicons-x-circle-16: | Two points of time separated by dash in the 24-hour format (0 - 24). The minutes are optional (hh or hh:mm). |
| _world_    | world:name        | player location        | The world to check for the time.                                                                             |


```YAML title="Example"
conditions:
  allDay: "time 6-19"
  midnightInOverworld: "time 23:30-0:30 world:overworld"
  knoppersTime: "time 9:30-10"
  exactAtTwelveAtPlayersHome: "time 12-12 world:%ph.player_home_world%"
```

## Placeholder matches expression

__Context__: @snippet:condition-meta:independent@  
__Syntax__: `variable <placeholder> <regex> [forceSync]`  
__Description__: Whether the placeholder value matches the specified [regular expression](../Data-Formats.md#regex-regular-expressions)

| Parameter     | Syntax          | Default Value          | Explanation                                                                                                                                     |
|---------------|-----------------|------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------|
| _Placeholder_ | Any placeholder | :octicons-x-circle-16: | The placeholder (surrounded by `%` characters).                                                                                                 |
| _Regex_       | A regex pattern | :octicons-x-circle-16: | The [regex](../Data-Formats.md#regex-regular-expressions) that the placeholder value must match. The regex can also be stored in a placeholder. |
| _forceSync_   | Keyword         | False                  | Forces the placeholder to be resolved on the main thread. This may be required by some third party placeholder.                                 |


```YAML title="Example"
conditions:
  anyNumber: "variable %objective.var.price% -?\\d+" #(1)!
  isPlayer: "variable %ph.parties_members_1% %player%" #(2)!
  denizenPlaceholder: "variable %ph.denizen_<server.match_player[SomeName].has_flag[flag_name]>% true forceSync" #(3)!
  denizenPlaceholderThis: "variable %ph.denizen_<player.has_flag[flag_name]>% true forceSync" #(4)!
```

1. Returns true if the placeholder `%objective.var.price%` contains any number.
2. Returns true if the `parties_members_1` placeholder contains the player's name.
3. Returns true if the `denizen_<server.match_player[SomeName].has_flag[flag_name]>` Denizen placeholder contains `true`.
   This placeholder is resolved on the main thread. <p>The `someName` part can't be a placeholder!
4. Works the same as the `denizenPlaceholder` with the only difference it checks for the player the condition is executed with.

## Is weather

__Context__: @snippet:condition-meta:independent@  
__Syntax__: `weather <weather> [world]`  
__Description__: Whether the specified weather matches the current weather in the specified world.

There are three possible options: sun, rain and storm. Note that `/toggledownfall` does not change the weather, it just does what the name suggests: toggles downfall. The rain toggled off will still be considered as rain! Use `/weather clear` instead.

| Parameter | Syntax     | Default Value          | Explanation                         |
|-----------|------------|------------------------|-------------------------------------|
| _weather_ | Keyword    | :octicons-x-circle-16: | The weather to check for.           |
| _world_   | world:name | player location        | The world to check for the weather. |


```YAML title="Example"
conditions:
  isSunny: "weather sun"
  weatherInPlayerWorld: "weather rain world:%ph.player_home_world%"
  overworldIsRainy: "weather rain world:overworld"
```

## Is in world

__Context__: @snippet:condition-meta:online@  
__Syntax__: `world <world>`  
__Description__: Whether the player is in the specified world.

The first argument is the name of a world.

```YAML title="Example"
conditions:
  isInWorld: "world world"
```
