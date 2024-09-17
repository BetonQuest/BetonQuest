---
icon: material/variable-box
---
# Variables List

This page lists all the variables that are available in BetonQuest.
Some of them are only useful when exported for use in other plugins through the [support for PlaceHolderAPI](Integration-List.md#placeholderapi).

Variables marked as **static** can be resolved without a player specified.

## BetonQuest Elements

### Objective Property Variable

Using this variable you can display a property of an objective. The first argument is an ID of the objective as
defined in the _objectives_ section (not the type). Make sure that the player has this objective active or it
will be replaced with nothing (""). Second argument is the name of a property you want to display.
All properties are described in "Objectives List" chapter.

```
%objective.kill_zombies.left%
```

### Condition Variable

You can expose BetonQuest's conditions to 3rd party plugins by using the `condition` variable together with the 
[PAPI support](Integration-List.md#placeholderapi).
The variable will return `true` or `false` by default. If you add `papiMode` to the instruction it will return `yes` or `no`.    
You can translate the papiMode's result by changing the values of `condition_variable_met` `condition_variable_not_met` in 
the *messages.yml* config.

```
%condition.myCondition%
%condition.myCondition.papiMode%
``` 

## BetonQuest Data Types

### Point Variable

This variable displays the amount of points you have in some category or amount of points you need to have to reach a
number. The first argument is the name of a category and the second argument is either `amount` or `left:x`, where `x` is a number.

```
%point.reputation.amount%
%point.reputation.left:15%
```

### Global Point Variable

**static**

This variable displays the amount of global points in some category or the amount of points needed to reach a number.
The first argument is the name of a category and the second argument is either `amount` or `left:x`, where `x` is a number.

```
%globalpoint.global_knownusers.amount%
%globalpoint.global_knownusers.left:100%
```

### Tag Variable

This variable displays whether the player has a tag or not.
The variable will return true or false by default. If you add papiMode to the instruction it will return yes or no.
You can translate the papiMode's result by changing the values of `condition_variable_met` and `condition_variable_not_met`
in the messages.yml config.

```
%tag.test%
%tag.test.papiMode%
```

### Global Tag Variable

**static**

This variable displays whether a global tag is set or not.
The variable will return true or false by default. If you add papiMode to the instruction it will return yes or no.
You can translate the papiMode's result by changing the values of `condition_variable_met` and `condition_variable_not_met`
in the messages.yml config.

```
%globaltag.test%
%globaltag.test.papiMode%
```

### Custom Text Variable

It is possible to save text per player. This works by using the [`variable`](Objectives-List.md#variable-variable)
 objective and the [`variable`](Events-List.md#variable-variable) event. 

## Global variables

You can insert a global variable in any instruction text. It looks like this: `$beton$` (and this one would be
called "beton"). When the plugin loads that instruction string it will replace those variables with values assigned to
them in the `variables:` section **before** all instructions are parsed. This is useful for example when installing a
package containing a WorldEdit schematic of the quest building. Instead of going through the whole code to set those
locations, names or texts you will only have to specify a few variables (that is, of course, if the author of the
package used those variables properly in his code).

Note that these variables are something entirely different from other variables. Global ones use `$` characters
and conversation ones use `%` characters. The former is resolved before the instruction text is parsed while the
latter is resolved when the quests are running, usually on a per-player basis.

```YAML
variables:
  village_location: 100;200;300;world
  village_name: Concrete
```

## Other Variables

### Conversation Variable

When the player is in a conversation, this variable will contain the quester's name in the player's quest language.
If the player is not in a conversation, the variable is empty.

```
%conversation%
```

### Eval Variable

**static**

This variable allows you to resolve an expression containing variables,
and the result will then be interpreted again as a variable.
You need to escape the `%` inside eval with a backslash `\` to prevent it from being interpreted as a delimiter.
You can nest multiple evals, but this leads you to an escape hell.
If you do so, you need to add one escape level with each nesting level,
this means normally you write `\%` and in the next level you need to write `\\\%`.

````
%eval.player.\%objective.variableStore.displayType\%%
%eval.player.\%eval.objective.\\\%objective.otherStore.targetStore\\\%.displayType\%%
````

### Item Variable

With this variable you can display different properties of a specific QuestItem.
The first argument is the name of the item (as defined in the _items_ section).
The `amount` argument displays the number of items in the players inventory and backpack,
the `left:x` gives the difference to the `x` value (when the amount is higher than the value it will be negative).
The `name` argument simply gives the defined name or an empty String, when not set
and `lore:x` displays the lore row with index `x` (starting with 0).
Both `name` and `lore` supports the `raw` subargument to get the text without formatting.

```
%item.stick.amount%
%item.stick.left:32%
%item.epic_sword.name%
%item.epic_sword.lore:0.raw%
```

### Item durability variable

With this variable you can display the durability of an item.
The first argument is the slot.
An optional argument is `relative` which will display the durability of the item relative to the maximum
from 0 to 1, where 1 is the maximum. You can specify the amount of digits with the argument `digits:x`,
where `x` is a whole number. This default is 2 digits.
Additionally, you get the output in percent (inclusive the '%' symbol).

```
%itemdurability.HAND%
%itemdurability.CHEST.relative%
%itemdurability.CHEST.relative.percent%
%itemdurability.HEAD.relative.digits:5%
```

### Location Variable

This variable resolves to all aspects of the player's location. The x, y and z coordinates, the world name, the yaw and pitch (head rotation).
There are also modes for the [Unified Location Formatting](../Data-Formats.md#unified-location-formating) (ULF from now on)
which means that this variable can also be used in events, conditions etc.
If you just specify `%location%` the variables will resolve to a ULF with yaw and pitch.
You can add two options to that base, one will give back parts of the ULF and the other will set to how many decimal places 
the variable will resolve. 

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
    
    
### Math Variable

**static**

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

!!! Warning
    The modulo operator needs to be escaped with a backslash `\` to prevent it from being interpreted as a placeholder delimiter.
    If you don't want to escape the percentage and actually want to write a backslash you can use `\\%`.
    Don't forget to escape the backslash itself with another backslash if you are inside a double-quoted string `"`.

```
%math.calc:100*(15-point.reputation.amount)%
%math.calc:objective.kill_zombies.left/objective.kill_zombies.total*100~2%
%math.calc:-{ph.myplugin_stragee+placeholder}%
%math.calc:64\%32%
```

### Npc Variable: `%npc.<id>.<argument>%`

**static**

This variable resolves information about a Npc. 
Specifying an argument determines the return: the Npc name, or full name (with formatting).

Arguments:
* name - Return citizen name
* full_name - Full Citizen name

```YAML title="Example"
%npc.bob.name%        # Bob
%npc.bob.full_name%   # &eBob
```

#### Npc Location Variable: `%npc.<id>.location.<mode>.<precision>%`

This variable resolves to all Npc location. For details see the [location variable](#location-variable).

```YAML title="Example"
%npc.mayor.location%           # -> 325;121;814;npcWorldName;12;6
%npc.mayor.location.xyz%       # -> 325 121 814 
%npc.mayor.location.ulfLong.5% # -> 325.54268;121.32186;814.45824;npcWorldName;12.0;6.0
```

### Player Name Variable

The variable `%player%` is the same as `%player.name%` and will display the name of the player.
`%player.display%` will use the display name used in chat and `%player.uuid%` will display the UUID of the player.

```
%player%
%player.name%
%player.display%
%player.uuid%
```

### Random Number Variable

**static**

This variable gives a random number from the first value to the second.
The first argument is `whole` or `decimal`, the second and third arguments are numbers or variables,
seperated by a `~`.
Like the `math` variable you can round the decimal value by using
instead of `decimal` the argument `decimal~x` where `x` is the maximal amount of decimal places. 
Variables can be used with `{}` instead of `%%`.
Note that the first value is returned when it is higher than the second.

```
%randomnumber.whole.0~10%
%randomnumber.whole.-70~70%
%randomnumber.decimal~3.3.112~100%
%randomnumber.decimal~1.0~{location.y}%
```

### Version Variable

**static**

This variable displays the version of the plugin. You can optionally add the name of the plugin as an argument to display version of another plugin.

```
%version.Citizens%
```
