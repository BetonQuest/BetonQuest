---
icon: octicons/question-16
---
# Conditions List

## Advancement: `advancement`

This condition checks if the player has specified advancement. The only argument is the full name of the advancement.
This includes the namespace, the tab and the name of the advancement as configured on your server. 
[List of all vanilla advancements](https://minecraft.wiki/w/Advancement#List_of_advancements).

!!! example
    ```YAML
    advancement minecraft:adventure/kill_a_mob
    ```

## Conjunction: `and`

**static**

Conjunction of specified conditions. This means that every condition has to be met in order for conjunction to be true. Used only in complex alternatives, because conditions generally work as conjunction. Instruction string is exactly the same as in `alternative`.

!!! example
    ```YAML
    and has_helmet,has_chestplate,has_leggings,has_boots
    ```

## Armor: `armor`

The armor condition requires the player to wear an armor that has been specified in the _items_ section.

!!! example
    ```YAML
    armor helmet_of_concrete
    ```

## Biome: `biome`

This condition will check if the player is in specified biome. The only argument is the [biome type](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/Biome.html).

!!! example
    ```YAML
    biome savanna_rock
    ```
    
## Burning: `burning`

This condition will check if the player is on fire.

```YAML title="Example"
conditions:
  isOnFire: "burning"
```

## Check conditions: `check`

**persistent**, **static**

This condition allow for specifying multiple instruction strings in one, longer string. Each instruction must be started with `^` character and no other dividers should be used. The condition will be met if all inner conditions are met. It's not the same as `and` condition, because you can specify an instruction string, not a condition name.

!!! example
    ```YAML
    check ^tag beton ^item emerald:5 ^location 100;200;300;survival_nether;5 ^experience 20
    ```

## Chest Item: `chestitem` 

**persistent**, **static**

This condition works in the same way as `item` condition, but it checks the specified chest instead of a player. The first argument is a location of the chest and the second one is the list of items defined in the same way as in `item` condition. If there is no chest at specified location the condition won't be met.

!!! example
    ```YAML
    chestitem 100;200;300;world emerald:5,sword
    ```

## Conversation: `conversation`

This condition will check if a conversation has an available starting option. If no starting option has a condition that returns true then this will return false.

!!! example
    ```YAML
    conversation innkeeper
    ```

## Day of week: `dayofweek`

It must be a specific day of the week that this condition returns true. You can specify either the english name of the day or the number of the day (1 being monday, 7 sunday,..).

!!! example
    ```YAML
    dayofweek sunday
    ```
 
## Potion Effect: `effect`

To meet this condition the player must have an active potion effect. There is only one argument and it takes values from this page: [potion types](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/potion/PotionEffectType.html).

!!! example
    ```YAML
    effect SPEED
    ```

## Empty inventory slots: `empty`

To meet this condition the players inventory must have the specified amount of empty slots or more.
If you want to check for a specific amount (for example for a full inventory with 0 empty slots) you can append the `equal` argument.

!!! example
    ```YAML
    empty 5
    ```

## Entities in area: `entities` 

**persistent**, **static**

This condition will return true only if there is a specified amount (or more) of specified entities in the specified area. 
There are three required arguments - entity type, location and range. Entities are defined as a list separated by commas.
Each entity type (taken from [here](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html))
can have an additional amount suffix, for example `ZOMBIE:5,SKELETON:2` means 5 or more zombies and 2 or more skeletons.
The location is defined as usual. The number after the location is the range around the location in which will be checked for these entities. You can also specify additional `name:` argument,
with the name of the required entity. Replace all spaces with `_` here. You can use `marked:` argument to check only for entities marked in `spawn` event.

!!! example
    ```YAML
    entities ZOMBIE:2 100;200;300;world 10 name:Deamon
    ```

## Experience: `experience`

This condition is met when the player has the specified amount of experience levels.
You can also define decimal numbers, for example `experience 1.5` will be met when the player has 1.5 or more experience levels.
If you want to check for an absolute amount of experience points you can convert it to decimal levels.

!!! example
    ```YAML
    experience 30
    experience 5.5
    ```

## Facing direction: `facing`

Checks if the player is looking in the given direction. Valid directions are `UP`, `DOWN`, `NORTH`, `EAST`, `WEST` and `SOUTH`. Up and down start at a pitch of 60°.

!!! example
    ```YAML
    facing EAST
    ```

## Fly: `fly`

This will check if the player is currently flying (Elytra type of flight).

!!! example
    ```YAML
    fly
    ```

## Game mode: `gamemode`

This condition checks if the player is in a specified game mode. The first argument is the game mode, i.e. survival, creative, adventure.

!!! example
    ```YAML
    gamemode survival
    ```

## Global point: `globalpoint` 

**persistent**, **static**

The same as point condition but it checks the amount for a global point category which has the same value for all players.

!!! example
    ```YAML
    globalpoint global_knownusers 100
    ```

## Global tag: `globaltag`

**persistent**, **static**

This requires a specific global tag to be set and works the same as normal tag condition.

!!! example
    ```YAML
    globaltag global_areNPCsAgressive
    ```

## Item in Hand: `hand`

This condition is met only when the player holds the specified quest item in their hand.
The offhand will be checked instead of the main hand if the `offhand` keyword is added.
Amount cannot be set here, though it may be checked with the `item` condition.

!!! example
    ```YAML
    hand SpecialSword
    hand QuestShield offhand
    ```

## Health: `health`

Requires the player to have equal or more health than specified amount. The only argument is a number (double). Players can have 0 to 20 health by default (there are some plugins and commands which change the maximum) (0 means dead, don't use that since it will only be met when the player sees the red respawn screen).

!!! example
    ```YAML
    health 5.6
    ```

## Height: `height`

This condition requires the player to be _below_ specific Y height. The required argument is a number or a location (for example 100;200;300;world). In case of location it will take the height from it and use it as regular height.

!!! example
    ```YAML
    height 16
    ```

## Hunger: `hunger`

Requires the player to have equal or more hunger points, the condition is the same as `health` just for hunger. If the hunger level is below 7, the player cannot sprint.

!!! example
    ```YAML
    hunger 15
    ```
    
## In Conversation: `inconversation`

This condition checks, if the player is in a conversation.

| Parameter      | Syntax              | Default Value          | Explanation                                                                                              |
|----------------|---------------------|------------------------|----------------------------------------------------------------------------------------------------------|
| _conversation_ | `conversation:name` | :octicons-x-circle-16: | Optional name of the conversation. If specified, it will only check for the conversation with this name. |

```YAML title="Example"
conditions:
  isInConversation: "inconversation"
  talksToInnkeeper: "inconversation conversation:innkeeper"
```

## Item in Inventory: `item`

This condition requires the player to have all specified items in his inventory or backpack. You specify items in a list separated by commas (without spaces between!) Each item consists of its name and amount, separated by a colon. Amount is optional, so if you specify just item's name the plugin will assume there should be only one item.

!!! example
    ```YAML
    item emerald:5,gold:10
    ```

## Durability of item: `itemdurability`

This condition requires the player to have a certain amount of durability on an item.
The first argument is the slot, the second the amount.
Optional `relative` argument sets 0 to broken and 1 to the maximum durability the item can have.
This condition returns false when no item is in the given slot or does not have durability, like stone or sticks.
Available slot types: `HAND`, `OFF_HAND`, `HEAD`, `CHEST`, `LEGS`, `FEET`.

!!! example
    ```YAML
    itemdurability HAND 50
    itemdurability CHEST 0.5 relative
    ```

## Journal entry: `journal`

This condition will return true if the player has specified entry in his journal (internal name of the entry, like in _journal_ section). The only argument is name of the entry.

!!! example
    ```YAML
    journal wood_started
    ```

## Language: `language`

**persistent**

This condition is fulfilled as long as the player has one of the specified languages selected as their quest language.

!!! example
    ```YAML
    language en,de,fr
    ```

## Location: `location`

It returns true only when the player is closer to specified location than the specified distance. Just two mandatory attributes - location and radius around it (can be a variable).

!!! example
    ```YAML
    location 100;200;300;survival_nether 5
    ```

##  Looking at a block: `looking`

Checks if the player is looking at a block with the given location or material. You must specify either `loc:` optional (the location of the block) or `type:` optional as a `block selector`. You can also specify both.

!!! example
    ```YAML
    looking loc:12.0;14.0;-15.0;world type:STONE
    ```

## Moon Cycle: `mooncycle`

**static**

This condition checks the moon cycle (1 is full moon, 8 is Waxing Gibbous) in the given world or the players world. A list of phases can be 
found 
[here](https://minecraft.wiki/w/Moon).
    
| Parameter   | Syntax     | Default Value          | Explanation                                               |
|-------------|------------|------------------------|-----------------------------------------------------------|
| _MoonPhase_ | Number     | :octicons-x-circle-16: | The MoonPhase to check for. Can be a variable.            |
| _world_     | world:name | player location        | The world to check for the moon phase. Can be a variable. |


```YAML title="Example"
fullMoon: "mooncycle 1"
newMoonHub: "mooncycle 5 world:hub"
```

## Number compare: `numbercompare`

This condition compares two numbers.
The valid operations are: `<`, `<=`, `=`, `!=`, `>=`, `>`.

!!! example
    ```YAML
    numbercompare %ph.other_plugin:points% >= 100
    ```

## Objective: `objective`

This condition is very simple: it's true only when the player has an active objective. The only argument is the name of the objective, as defined in the _objectives_ section.

!!! example
    ```YAML
    objective wood
    ```

## Alternative: `or`

**persistent**, **static**

Alternative of specified conditions. This means that only one of conditions has to be met in order for alternative to be true. You just define one mandatory argument, condition names separated by commas. `!` prefix works as always.

!!! example
    ```YAML
    or night,rain,!has_armor
    ```

## Partial date: `partialdate`

The current date must match the given pattern. You can specify the day of the month, the month or the year it must be that this condition returns true or combine them. You can also specify multiple days/months/years by just separating them by `,` or a interval by using `-`. If you have trouble understanding how this works have a look at the example.

The example is true between the 1st and the 5th or on the 20th of each month, but only in the year 2017.

!!! example
    ```YAML
    partialdate day:1-5,20 year:2017
    ```

## Party: `party`

**static**

This is part of the [party system](../Parties.md).
This condition takes three optional arguments: `every:`, `any:`, `count:` and `location:`.  
"Every" is a list of conditions that must be met by every player in the party.  
Any is a list of conditions that must be met by at least one player in a party (it doesn't have to be the same player,
one can meet first condition, another one can meet the rest, and it will work).  
Count is just a number, minimal amount of players in the party.
Location can be used to create a party without the need of a player that is the center of the party.
You don't have to specify all those arguments, you can use only one if you want.

!!! example
    ```YAML
    party 10 has_tag1,!has_tag2 every:some_item any:some_location,some_other_item count:5
    ```

## Permission: `permission`

The player must have a specified permission for this condition to be met. The instruction string must contain permission node as the required argument.

!!! example
    ```YAML
    permission essentials.tpa
    ```

## Point: `point`

Requires the player to have amount of points equal to the specified category or more. There are two required arguments, first is the category (string), second is the amount (integer). You can also add optional argument `equal` to accept only players with exactly equal amount of points.

!!! example
    ```YAML
    point beton 20
    ```

## Ride an entity: `ride`

This condition checks if the player rides the specified
[entity](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html).
`any` is also a valid input and matches any entity.

!!! example
    ```YAML
    ride horse
    ride any
    ```

## Random: `random`

**persistent**, **static**

This condition is met randomly. There is one argument: two positive numbers like `5-12`. They mean something like that: "It will be true 5 times out of 12".

!!! example
    ```YAML
    random 12-100
    ```

## Armor Rating: `rating`

This one requires the player to wear armor which gives him specified amount of protection (armor icons). The first and only argument should be an integer. One armor point is equal to half armor icon in-game (10 means half of the bar filled).

!!! example
    ```YAML
    rating 10
    ```
    
## Real time: `realtime`

**static****persistent**

There must a specific (real) time for this condition to return true.

| Parameter  | Syntax            | Default Value          | Explanation                                                                                                  |
|------------|-------------------|------------------------|--------------------------------------------------------------------------------------------------------------|
| _Timespan_ | startTime-endTime | :octicons-x-circle-16: | Two points of time seperated by dash in the 24-hour format (0 - 24). The minutes are optional (hh or hh:mm). |


```YAML title="Example"
allDayReal: "realtime 6-19"
midnightReal: "realtime 23:30-0:30"
knoppersTimeReal: "realtime 9:30-10"
```

## Scoreboard: `score`

**persistent**

With this condition you can check if the score in a specified objective on a scoreboard is greater or equal to specified amount.

| Parameter              | Syntax         | Default Value          | Explanation                               |
|------------------------|----------------|------------------------|-------------------------------------------|
| _scoreboard objective_ | Objective name | :octicons-x-circle-16: | The name of the scoreboard objective      |
| _count_                | Number         | :octicons-x-circle-16: | The minimum whole number of the objective |

```YAML title="Example"
hasAtLeastTenKills: "score kills 10"
```

## Sneaking: `sneak`

Sneak condition is only true when the player is sneaking. This would probably be useful for creating traps, I'm not sure. There are no arguments for this one.

!!! example
    ```YAML
    sneak
    ```
    
## Check Stage: `stage`
This condition compares the players current stage with the given stage by its index numbers.
For more take a look at the [stage objective](./Objectives-List.md#stages-stage).  
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

## Tag: `tag`

This one requires the player to have a specified tag. Together with `!` negation it is one of the most powerful tools when creating conversations. The instruction string must contain tag name.

!!! example
    ```YAML
    tag quest_completed
    ```

## Test for block: `testforblock`

**persistent**, **static**

This condition is met if the block at specified location matches the given material. First argument is a location, and the second one is a `block selector`.

!!! example
    ```YAML
    testforblock 100;200;300;world STONE
    ```

## Time: `time`

**static**

There must be specific (Minecraft) time on the world for this condition to return true.

| Parameter  | Syntax            | Default Value          | Explanation                                                                                                  |
|------------|-------------------|------------------------|--------------------------------------------------------------------------------------------------------------|
| _Timespan_ | startTime-endTime | :octicons-x-circle-16: | Two points of time seperated by dash in the 24-hour format (0 - 24). The minutes are optional (hh or hh:mm). |
| _world_    | world:name        | player location        | The world to check for the time. Can be a variable.                                                          |


```YAML title="Example"
allDay: "time 6-19"
midnightInOverworld: "time 23:30-0:30 world:overworld"
knoppersTime: "time 9:30-10"
exactAtTwelveAtPlayersHome: "time 12-12 world:%ph.player_home_world%"
```

## Variable: `variable`

**static**

This condition checks if a variable value matches given [regular expression](../Data-Formats.md#regex-regular-expressions)

| Parameter   | Syntax          | Default Value          | Explanation                                                                                                                                |
|-------------|-----------------|------------------------|--------------------------------------------------------------------------------------------------------------------------------------------|
| _Variable_  | Any variable    | :octicons-x-circle-16: | The variable (surrounded by `%` characters).                                                                                               |
| _Regex_     | A regex pattern | :octicons-x-circle-16: | The [regex](../Data-Formats.md#regex-regular-expressions) that the variables value must match. The regex can also be stored in a variable. |
| _forceSync_ | Keyword         | False                  | Forces the variables to be resolved on the main thread. This may be required by some third party variables.                                |


```YAML title="Example"
anyNumber: "variable %objective.var.price% -?\\d+" #(1)!
isPlayer: "variable %ph.parties_members_1% %player%" #(2)!
denizenVariable: "variable %ph.denizen_<server.match_player[SomeName].has_flag[flag_name]>% true forceSync" #(3)!
denizenVariableThis: "variable %ph.denizen_<player.has_flag[flag_name]>% true forceSync" #(4)!
```

1. Returns true if the variable `%objective.var.price%` contains any number.
2. Returns true if the `parties_members_1` variable contains the player's name.
3. Returns true if the `denizen_<server.match_player[SomeName].has_flag[flag_name]>` Denizen variable contains `true`.
   This variable is resolved on the main thread. <p>The `someName` part can't be a variable!
4. Works the same as the `denizenVariable` with the only difference it checks for the player the condition is executed with.

## Weather: `weather`

**static**

There must be a specific weather for this condition to return true. There are three possible options: sun, rain and storm. Note that `/toggledownfall` does not change the weather, it just does what the name suggests: toggles downfall. The rain toggled off will still be considered as rain! Use `/weather clear` instead.

| Parameter | Syntax     | Default Value          | Explanation                         |
|-----------|------------|------------------------|-------------------------------------|
| _weather_ | Keyword    | :octicons-x-circle-16: | The weather to check for.           |
| _world_   | world:name | player location        | The world to check for the weather. |


```YAML title="Example"
isSunny: "weather sun"
weatherInPlayerWorld: "weather rain world:%ph.player_home_world%"
overworldIsRainy: "weather rain world:overworld"
```

## World: `world`

This conditions checks if the player is in a specified world. The first argument is the name of a world.

!!! example
    ```YAML
    world world
    ```
