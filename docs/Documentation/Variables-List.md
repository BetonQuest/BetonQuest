---
icon: material/variable-box
---
# Variables List

## Custom Strings

It is possible to save multiple custom strings per player. This works by using the [`variable`](Objectives-List.md#variable-variable) objective and the [`variable`](Events-List.md#variable-variable) event. 

## Expose conditions to 3rd party plugins: `condition`

You can expose BetonQuest's conditions to 3rd party plugins by using the `condition` variable together with the 
[PAPI support](Compatibility.md#placeholderapi).
The variable will return `true` or `false` by default. If you add `papiMode` to the instruction it will return `yes` or `no`.    
You can translate the papiMode's result by changing the values of `condition_variable_met` `condition_variable_not_met`in 
the *messages.yml* config.
```
%condition.myCondition%
%condition.myCondition.papiMode%
``` 

## Global point: `globalpoint`

Works the same as normal point variable but instead of displaying points from a players category it displays points in a global, player independent category.

!!! example
    `%globalpoint.global_knownusers.left:100%`

## Item: `item`

With this variable you can display amount of specific items in player's inventory or a number needed to reach specific amount. The first argument is the name of an item (as defined in the _items_ section) and the second one is either `amount` or `left:x`, where `x` is a number.

!!! example
    `%item.stick.amount%`

## Location: `location`

This variable resolves to all aspects of the player's location. The x, y and z coordinates, the world name, the yaw and pitch (head rotation).
There are also modes for the [Unified Location Formatting](Reference.md#unified-location-formating) (ULF from now on)
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

This variable allows you to perform a calculation based on other variables (for example point or objective variables)
and resolves to the result of the specified calculation. The variable always starts with `math.calc:`, followed by the
calculation which should be calculated. Supported operations are `+`, `-`, `*`, `/`, `^` and `%`. You can use `( )` and
`[ ]` braces and also calculate absolute values with `| |`. But be careful, don't use absolute values in the command
event as it splits the commands at every `|` and don't nest them without parenthesis (`|4*|3-5||` wont work, but
`|4*(|3-5|)|` does). Additionally, you can use the round operator `~` to round everything left of it to the number of
decimal digits given on the right. So `4+0.35~1` will produce `4.4` and `4.2~0` will produce `4`.

To use variables in the calculation you have two options: First just write the variable, but  without `%` around them;
In cases where this doesn't work, e.g. if the variable contains mathematical operators, you can surround it with curly
braces `{ }`. Inside the curly braces you have to escape with `\`, so to have a `\` in your variable you need to write
`\\`, to have a `}` inside your variable you need to write `\}`.

!!! example
    ```
    %math.calc:100*(15-point.reputation.amount)%
    %math.calc:objective.kill_zombies.left/objective.kill_zombies.total*100~2%
    %math.calc:-{ph.myplugin_stragee+placeholder}%
    %math.calc:64%32%
    ```

## NPC: `npc`

It's a very simple variable. It's replaced by the name of the NPC in player's language.

!!! example
    `%npc%`

## Objective: `objective`

Using this variable you can display a property of an objective. The first argument is an ID of the objective as defined in the _objectives_ section (not the type). Make sure that the player has this objective active or it will be replaced with nothing (""). Second argument is the name of a property you want to display. All properties are described in "Objectives List" chapter.

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



