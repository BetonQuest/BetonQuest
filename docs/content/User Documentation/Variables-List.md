# Variables List

## Global point: `globalpoint`

Works the same as normal point variable but instead of displaying points from a players category it displays points in a global, player independent category.

!!! example
    `%globalpoint.global_knownusers.left:100%`

## Item: `item`

With this variable you can display amount of specific items in player's inventory or a number needed to reach specific amount. The first argument is the name of an item (as defined in _items.yml_) and the second one is either `amount` or `left:x`, where `x` is a number.

!!! example
    `%item.stick.amount%`

## Location: `location`

This variable resolves to player's current location, formatted as an absolute location format (more about it in the _Reference_ chapter). The location will contain yaw and pitch. You can use it instead of coordinates as location arguments in events, conditions and objectives.

!!! example
    `%location%`
    
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



