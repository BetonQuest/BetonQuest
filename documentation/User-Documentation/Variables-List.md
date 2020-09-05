# Variables List

## Custom Strings

It is possible to save multiple custom strings per player. This works by using the [`variable`](Objectives-List.md#variable-variable) objective and the [`variable`](Events-List.md#variable-variable) event. 

## Global point: `globalpoint`

Works the same as normal point variable but instead of displaying points from a players category it displays points in a global, player independent category.

!!! example
    `%globalpoint.global_knownusers.left:100%`

## Item: `item`

With this variable you can display amount of specific items in player's inventory or a number needed to reach specific amount. The first argument is the name of an item (as defined in _items.yml_) and the second one is either `amount` or `left:x`, where `x` is a number.

!!! example
    `%item.stick.amount%`

## Location: `location`

This variable resolves to all aspects of the player's location. The x, y and z coordinates, the world name, the yaw and pitch (head rotation).
There are also modes for Betons [Unified Location Formatting](Reference.md#unified-location-formating) (ULF from now on)
which means that this variable can also be used in events, conditions etc.
If you just specify `%location%` the variables will resolve to a ULF with yaw and pitch.
You can add two options to that base, one will give back parts of the ULF and the other will set to how many decimal places 
the variable will resolve. 

!!! example
    ```YAML
    %location%           # -> 325;121;814;myWorldName;12;6
    %location.xyz%       # -> 325 121 814 
    %location.x%         # -> 325
    %location.y%         # -> 121
    %location.z%         # -> 814
    %location.yaw%       # -> 12
    %location.pitch%     # -> 6
    %location.world%     # -> myWorldName
    %location.ulfShort%  # -> 325;121;814;myWorldName
    %location.ulfLong%   # -> 325;121;814;myWorldName;12;6
    
    %location.x.2%       # -> 325.16
    %location.ulfLong.5% # -> 325.54268;121.32186;814.45824;myWorldName;12.0;6.0
    ```
    
    
## Calculate mathematical expression: `math.calc`

This variable allows you to perform a calculation based on other variables (for example point or objective variables) and resolves to the result of the specified calculation. The variable always starts with `math.calc:`, followed by the calculation which should be calculated. Supported operations are `+`, `-`, `*`, `/` and  `^`. You can use `( )` and `[ ]` braces and also calculate absolute values with `| |` (but don't use this in the command event as it splits the commands at every `|`). If you want to use variables in the calculation, don't put `%` around them.

!!! example
    `%math.calc:100*(15-point.reputation.amount)%`

## NPC: `npc`

It's a very simple variable. It's replaced by the name of the NPC in player's language.

!!! example
    `%npc%`

## Objective: `objective`

Using this variable you can display a property of an objective. The first argument is an ID of the objective as defined in _objectives.yml_ (not the type). Make sure that the player has this objective active or it will be replaced with nothing (""). Second argument is the name of a property you want to display. All properties are described in "Objectives List" chapter.

!!! example
    `%objective.kill_zombies.left%`

## Player: `player`

This variable will be replaced with the name of the player. If you add `display` argument, it will use display name instead of real name.

!!! example
    `%player.display%`

## Point: `point`

This variable displays the amount of points you have in some category or amount of points you need to have to reach a number. The first argument is the name of a category and the second argument is either `amount` or `left:x`, where `x` is a number.

!!! example
    `%point.reputation.left:15%`

## Version: `version`

This variable displays the version of the plugin. You can optionally add the name of the plugin as an argument to display version of another plugin.

!!! example
    `%version.Citizens%`



