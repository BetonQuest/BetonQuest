# Conditions List

## Achievement: `achievement`

This condition checks if the player has specified achievement (default Minecraft achievements). The first argument is name of the [achievement](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Achievement.html).

!!! example
    ```YAML
    achievement BUILD_FURNACE
    ```

## Conjunction: `and`

Conjunction of specified conditions. This means that every condition has to be met in order for conjunction to be true. Used only in complex alternatives, because conditions generally work as conjunction. Instruction string is exactly the same as in `alternative`.

!!! example
    ```YAML
    and has_helmet,has_chestplate,has_leggings,has_boots
    ```

## Armor: `armor`

The armor condition requires the player to wear specified armor, as an item defined in _items.yml_ file.

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

## Check conditions: `check`

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

To meet this condition the player has to have specified amount of empty slots in his inventory.

!!! example
    ```YAML
    empty 5
    ```

## Entities in area: `entities` 

**persistent**, **static**

This condition will return true only if there is a specified amount (or more) of specified entities in the specified area. There are three required arguments - entity type, location and range. Entities are defined as a list separated by commas. Each entity type (taken from [here](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html)) can have additional `:amount` suffix, for example `ZOMBIE:5,SKELETON:2` means 5 or more zombies and 2 or more skeletons. Location is standard. Range is a number representing a radius in which the entities will be looked for. You can also specify additional `name:` argument, with the name of the required entity. Replace all spaces with `_` here. You can use `marked:` argument to check only for monsters marked in `spawn` event.

!!! example
    ```YAML
    entities ZOMBIE:2 100;200;300;world 10 name:Deamon
    ```

## Experience: `experience`

This condition is met when the player has a specified level (default minecraft experience). It is measured by full levels, not experience points. The instruction string must contain an integer argument.

!!! example
    ```YAML
    experience 30
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

This event is met only when player is holding a specified item in his hand. Amount cannot be set here, though it may be checked with `item` condition.

!!! example
    ```YAML
    hand sword
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

## Item in Inventory: `item`

This event is met only when player has specified item in his inventory. You specify items in a list separated by commas (without spaces between!) Each item consists of its name and amount, separated by a colon. Amount is optional, so if you specify just item's name the plugin will assume there should be only one item.

!!! example
    ```YAML
    item emerald:5,gold:10`
    ```

## Journal entry: `journal`

This condition will return true if the player has specified entry in his journal (internal name of the entry, like in _journal.yml_). The only argument is name of the entry.

!!! example
    ```YAML
    journal wood_started
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

This condition checks the players moon cycle (1 is full moon, 8 is Waxing Gibbous) and returns if the player is under that moon. A list of phases can be found [here](https://minecraft.gamepedia.com/Moon).

!!! example
    ```YAML
    mooncycle 1
    ```

## Objective: `objective`

This conditions is very simple: it's true only when the player has an active objective. The only argument is the name of the objective, as defined in _objectives.yml_.

!!! example
    ```YAML
    objective wood
    ```

## Alternative: `or`

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

To see details about parties read "Party" chapter in **Reference** section. This condition takes three optional arguments: `every:`, `any:` and `count:`. "Every" is a list of conditions that must be met by every player in the party. Any is a list of conditions that must be met by at least one player in a party (it doesn't have to be the same player, one can meet first condition, another one can meet the rest and it will work). Count is just a number, minimal amount of players in the party. You don't have to specify all those arguments, you can use only one if you want.

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

There must a specific (real) time for this condition to return true. You need to specify two times (formatted like `hh:mm`) separated by dash. If the first is before the second the time must be between these two, if its after the second the time must be later than the first and earlier than the second to return true.

!!! example
    ```YAML
    realtime 8:00-12:30
    ```

## Scoreboard: `score`

With this condition you can check if the score in a specified objective on a scoreboard is greater or equal to specified amount. The first argument is the name of the objective, second one is amount (an integer).

!!! example
    ```YAML
    score kills 20
    ```

## Sneaking: `sneak`

Sneak condition is only true when the player is sneaking. This would probably be useful for creating traps, I'm not sure. There are no arguments for this one.

!!! example
    ```YAML
    sneak
    ```

## Tag: `tag`

This one requires the player to have a specified tag. Together with `!` negation it is one of the most powerful tools when creating conversations. The instruction string must contain tag name.

!!! example
    ```YAML
    tag quest_completed
    ```

## Test for block: `testforblock`

**persistent**, **static**

This condition is met if the block at specified location matches the given material. First argument is a location, and the second one is a `block selector`. The older `data:value` parameter is still supported but now deprecated.

!!! example
    ```YAML
    testforblock 100;200;300;world STONE:1
    ```

## Time: `time`

There must be specific (Minecraft) time on the player's world for this condition to return true. You need to specify two hour numbers separated by dash. These number are normal 24-hour format hours. The first must be smaller than the second. If you want to achieve time period between 23 and 2 you need to negate the condition.

!!! example
    ```YAML
    time 2-23
    ```

## Variable: `variable`

This condition checks if a variable value matches given [pattern](https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html). The first argument is a variable (with `%` characters). Second one is the pattern (for example, if you want to check if it's "word", the patter would simply be `word`, but if you want to check if it's a number (positive or negative) you would use `-?\d+` pattern - `-?` means a dash or no dash, `\d` means any digit and `+` allows that digit to be repeated one or more times).

!!! example
    ```YAML
    variable %objective.var.price% -?\d+
    ```

## Weather: `weather`

There must be a specific weather for this condition to return true. There are three possible options: sun, rain and storm. Note that `/toggledownfall` does not change the weather, it just does what the name suggests: toggles downfall. The rain toggled off will still be considered as rain! Use `/weather clear` instead.

!!! example
    ```YAML
    weather sun
    ```

## World: `world`

This conditions checks if the player is in a specified world. The first argument is the name of a world.

!!! example
    ```YAML
    world world
    ```
