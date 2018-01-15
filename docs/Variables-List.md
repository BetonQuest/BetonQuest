# Variables List

## Player: `player`

This variable will be replaced with the name of the player. If you add `display` argument, it will use display name instead of real name.

**Example**: `%player.display%`

## NPC: `npc`

It's a very simple variable. It's replaced by the name of the NPC in player's language.

**Example**: `%npc%`

## Objective: `objective`

Using this variable you can display a property of an objective. The first argument is an ID of the objective as defined in _objectives.yml_ (not the type). Make sure that the player has this objective active or it will be replaced with nothing (""). Second argument is the name of a property you want to display. All properties are described in "Objectives List" chapter.

**Example**: `%objective.kill_zombies.left%`

## Point: `point`

This variable displays the amount of points you have in some category or amount of points you need to have to reach a number. The first argument is the name of a category and the second argument is either `amount` or `left:x`, where `x` is a number.

**Example**: `%point.reputation.left:15%`

## Item: `item`

With this variable you can display amount of specific items in player's inventory or a number needed to reach specific amount. The first argument is the name of an item (as defined in _items.yml_) and the second one is either `amount` or `left:x`, where `x` is a number.

**Example**: `%item.stick.amount%`

## Version: `version`

This variable displays the version of the plugin. You can optionally add the name of the plugin as an argument to display version of another plugin.

**Example**: `%version.Citizens%`

## Location: `location`

This variable resolves to player's current location, formatted as an absolute location format (more about it in the _Reference_ chapter). The location will contain yaw and pitch. You can use it instead of coordinates as location arguments in events, conditions and objectives.

**Example**: `%location%`
