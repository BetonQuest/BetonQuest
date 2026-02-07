---
icon: material/variable-box
tags:
  - Placeholder
---
# Placeholders List

This page lists all the placeholders that are available in BetonQuest.
Some of them are only useful when exported for use in other plugins through the [support for PlaceHolderAPI](Integration-List.md#placeholderapi).

## Quest types

### <span hidden>`objective` -</span> Objective

__Context__: @snippet:placeholder-meta:online-offline@  
__Syntax__: `objective.<id>.<property>`  
__Description__: Represents the specified property of the specified objective.

The first argument is an ID of the objective as defined in the _objectives_ section (not the type).
Make sure that the player has this objective active or it will be replaced with nothing ("").
Second argument is the name of a property you want to display.
All properties are described in "Objectives List" chapter.

```scss title="Example"
%objective.kill_zombies.left%
```

### <span hidden>`condition` -</span> Condition

__Context__: @snippet:placeholder-meta:online-offline@  
__Syntax__: `condition.<id>.[papiMode]`  
__Description__: Represents the specified condition as a boolean value.

You can expose BetonQuest's conditions to 3rd party plugins by using the `condition` placeholder together with the 
[PAPI support](Integration-List.md#placeholderapi).
The placeholder will return `true` or `false` by default. If you add `papiMode` to the instruction it will return `yes` or `no`.    
You can translate the papiMode's result by changing the values of `condition_placeholder_met` `condition_placeholder_not_met` in 
the *messages.yml* config.

```scss title="Example"
%condition.myCondition%
%condition.myCondition.papiMode%
``` 

### <span hidden>`constant` -</span> Constant

__Context__: @snippet:placeholder-meta:independent@  
__Syntax__: `constant.<name>`  
__Description__: Represents the value of the specified constant.

Constants are a bit different from other placeholders, as you can freely define the values of them.
They are defined in the *constants* section like this:

```YAML
constants:
  village_location: 100;200;300;world
  village_name: Concrete
```

To use a `constant` placeholder, you must use `%constant.constantName%`:

```scss title="Example"
%constant.village_location%
%constant.village_name%
```

If you want to parse a placeholder from a different package,
follow the same syntax as you would [working across packages](https://betonquest.org/3.0-DEV/Documentation/Scripting/Packages-%26-Templates/#defining-features).
The proper syntax is `%questPackage>constant.constantName%`.

## Data types

### <span hidden>`point` -</span> Point

__Context__: @snippet:placeholder-meta:online-offline@  
__Syntax__: `point.<category>.<amount|left>`  
__Description__: Represents the number of points in the specified category.
 
It is also possible to display the remaining number of points to reach a certain number of points.
The first argument is the name of a category and the second argument is either `amount` or `left:x`, where `x` is a number.

```scss title="Example"
%point.reputation.amount%
%point.reputation.left:15%
```

### <span hidden>`globalpoint` -</span> Global point

__Context__: @snippet:placeholder-meta:independent@  
__Syntax__: `globalpoint.<category>.<amount|left>`  
__Description__: Represents the number of global points in the specified category.
 
It is also possible to display the remaining number of points to reach a certain number of points.
The first argument is the name of a category and the second argument is either `amount` or `left:x`, where `x` is a number.

```scss title="Example"
%globalpoint.global_knownusers.amount%
%globalpoint.global_knownusers.left:100%
```

### <span hidden>`tag` -</span> Tag

__Context__: @snippet:placeholder-meta:online-offline@  
__Syntax__: `tag.<name>.[papiMode]`  
__Description__: Represents whether a tag is set or not as a boolean value.

The placeholder will return true or false by default. If you add papiMode to the instruction it will return yes or no.
You can translate the papiMode's result by changing the values of `condition_placeholder_met` and `condition_placeholder_not_met`
in the messages.yml config.

```scss title="Example"
%tag.test%
%tag.test.papiMode%
```

### <span hidden>`globaltag` -</span> Global tag

__Context__: @snippet:placeholder-meta:independent@  
__Syntax__: `globaltag.<name>.[papiMode]`  
__Description__: Represents whether a global tag is set or not as a boolean value.

The placeholder will return true or false by default. If you add papiMode to the instruction it will return yes or no.
You can translate the papiMode's result by changing the values of `condition_placeholder_met` and `condition_placeholder_not_met`
in the messages.yml config.

```scss title="Example"
%globaltag.test%
%globaltag.test.papiMode%
```

## Others

### <span hidden>`eval` -</span> Evaluate

__Context__: @snippet:placeholder-meta:independent@  
__Syntax__: `eval.<expression>`  
__Description__: Represents the value of the resolved expression.

You need to escape the `%` inside eval with a backslash `\` to prevent it from being interpreted as a delimiter.
You can nest multiple evals, but this leads you to an escape hell.
If you do so, you need to add one escape level with each nesting level,
this means normally you write `\%` and in the next level you need to write `\\\%`.

```scss title="Example"
%eval.player.\%objective.placeholderStore.displayType\%%
%eval.player.\%eval.objective.\\\%objective.otherStore.targetStore\\\%.displayType\%%
```

### <span hidden>`item` -</span> Item property

__Context__: @snippet:placeholder-meta:independent@  
__Syntax__: `item.<id>.<property>`  
__Description__: Represents the specified property of the specified item.

The first argument is the name of the item (as defined in the _items_ section).
The `amount` argument displays the number of items in the players inventory and backpack,
the `left:x` gives the difference to the `x` value (when the amount is higher than the value it will be negative).
The `name` argument simply gives the defined name or an empty String, when not set
and `lore:x` displays the lore row with index `x` (starting with 0).
Both `name` and `lore` supports the `raw` subargument to get the text without formatting.

```scss title="Example"
%item.stick.amount%
%item.stick.left:32%
%item.epic_sword.name%
%item.epic_sword.lore:0.raw%
```

### <span hidden>`itemdurability` -</span> Item durability

__Context__: @snippet:placeholder-meta:online@  
__Syntax__: `itemdurability.<slot>.[relative].[digits|percent]`  
__Description__: Represents the durability of the item in the specified slot.

The first argument is the slot.
An optional argument is `relative` which will display the durability of the item relative to the maximum
from 0 to 1, where 1 is the maximum. You can specify the amount of digits with the argument `digits:x`,
where `x` is a whole number. This default is 2 digits.
Additionally, you get the output in `percent` (inclusive the '%' symbol).

```scss title="Example"
%itemdurability.HAND%
%itemdurability.CHEST.relative%
%itemdurability.CHEST.relative.percent%
%itemdurability.HEAD.relative.digits:5%
```

### <span hidden>`location` -</span> Location

__Context__: @snippet:placeholder-meta:online@  
__Syntax__: `location.<format>.[precision]`  
__Description__: Represents the location of the player in the specified format.

The x, y and z coordinates, the world name, the yaw and pitch (head rotation).
There are also modes for the [Unified Location Formatting](../Data-Formats.md#unified-location-formating) (ULF from now on)
which means that this placeholder can also be used in actions, conditions etc.
If you just specify `%location%` the placeholders will resolve to a ULF with yaw and pitch.
You can add two options to that base, one will give back parts of the ULF and the other will set to how many decimal places 
the placeholder will resolve. 

```scss title="Example"
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

### <span hidden>`math` -</span> Calculate

__Context__: @snippet:placeholder-meta:independent@  
__Syntax__: `math.calc:<calculation>`  
__Description__: Represents the result of the specified mathematical operation.

Performs a calculation based on other placeholders (for example point or objective placeholders)
and resolves to the result of the specified calculation. The placeholder always starts with `math.calc:`, followed by the
calculation which should be calculated. Supported operations are `+`, `-`, `*`, `/`, `^` and `%`. You can use `( )` and
`[ ]` braces and also calculate absolute values with `| |`. But be careful, don't use absolute values in the command
action as it splits the commands at every `|` and don't nest them without parenthesis (`|4*|3-5||` won't work, but
`|4*(|3-5|)|` does). Additionally, you can use the round operator `~` to round everything left of it to the number of
decimal digits given on the right. So `4+0.35~1` will produce `4.4` and `4.2~0` will produce `4`.

To use placeholders in the calculation you have two options: First just write the placeholder, but  without `%` around them;
In cases where this doesn't work, e.g. if the placeholder contains mathematical operators, you can surround it with curly
braces `{ }`. Inside the curly braces you have to escape with `\`, so to have a `\` in your placeholder you need to write
`\\`, to have a `}` inside your placeholder you need to write `\}`.

When the calculation fails `0` will be returned and the reason logged.

!!! Warning
    The modulo operator needs to be escaped with a backslash `\` to prevent it from being interpreted as a placeholder delimiter.
    If you don't want to escape the percentage and actually want to write a backslash you can use `\\%`.
    Don't forget to escape the backslash itself with another backslash if you are inside a double-quoted string `"`.

```scss title="Example"
%math.calc:100*(15-point.reputation.amount)%
%math.calc:objective.kill_zombies.left/objective.kill_zombies.total*100~2%
%math.calc:-{ph.myplugin_stragee+placeholder}%
%math.calc:64\%32%
```

### <span hidden>`npc` -</span> Npc

__Context__: @snippet:placeholder-meta:independent@  
__Syntax__: `npc.<id>.<property>`  
__Description__: Represents the specified property of the specified npc.
 
Specifying an argument determines the return: the Npc name, or full name (with formatting).

Arguments:
  
- `name` - Return Npc name  
- `full_name` - Return Npc name with formatting  
- `location` - Return Npc location in the specified format, for details see the [location placeholder](#location-location).
  The general syntax is `%npc.<id>.location.<mode>.<precision>%`.

```scss title="Example"
%npc.bob.name%
%npc.bob.full_name%

%npc.mayor.location%           # -> 325;121;814;npcWorldName;12;6
%npc.mayor.location.xyz%       # -> 325 121 814 
%npc.mayor.location.ulfLong.5% # -> 325.54268;121.32186;814.45824;npcWorldName;12.0;6.0
```

### <span hidden>`player` -</span> Player

__Context__: @snippet:placeholder-meta:online-offline@  
__Syntax__: `player.<format>`  
__Description__: Represents the player's name in the specified format.

The placeholder `%player%` is the same as `%player.name%` and will display the name of the player.
`%player.display%` will use the display name used in chat and `%player.uuid%` will display the UUID of the player.

```scss title="Example"
%player%
%player.name%
%player.display%
%player.uuid%
```

### <span hidden>`quester` -</span> Quester

__Context__: @snippet:placeholder-meta:online-offline@  
__Syntax__: `quester`  
__Description__: Represents the name of the quester in the current conversation.

If the player is not in a conversation, the placeholder is empty.

```scss title="Example"
%quester%
```

### <span hidden>`randomnumber` -</span> Random

__Context__: @snippet:placeholder-meta:independent@  
__Syntax__: `randomnumber.<whole|decimal>.<min>~<max>`  
__Description__: Represents a random number in the specified range.

The first argument is `whole` or `decimal`, the second and third arguments are numbers or placeholders,
separated by a `~`.
Like the `math` placeholder you can round the decimal value by using
instead of `decimal` the argument `decimal~x` where `x` is the maximal amount of decimal places. 
Placeholders can be used with `{}` instead of `%%`.
Note that the first value is returned when it is higher than the second.

```scss title="Example"
%randomnumber.whole.0~10%
%randomnumber.whole.-70~70%
%randomnumber.decimal~3.3.112~100%
%randomnumber.decimal~1.0~{location.y}%
```

### <span hidden>`sync` -</span> Synchronization

__Context__: @snippet:placeholder-meta:online-offline-independent@  
__Syntax__: `sync.<expression>`  
__Description__: Represents the value of the specified placeholder but resolved on the server's main thread.

Its syntax is identical to the [eval placeholder](#eval-evaluate), but you should only use it if syncing is required.
If you encapsule multiple evaluations with `sync`, all sub-evaluations will be executed on the server's main thread 
and should be done with `eval` instead.

```scss title="Example"
%sync.player.\%objective.placeholderStore.displayType\%%
%sync.player.\%eval.objective.\\\%objective.otherStore.targetStore\\\%.displayType\%%
```

### <span hidden>`version` -</span> Plugin version

__Context__: @snippet:placeholder-meta:independent@  
__Syntax__: `version.[plugin]`  
__Description__: Represents the version of the specified plugin.

You can optionally add the name of the plugin as an argument to display version of another plugin.

```scss title="Example"
%version.Citizens%
```
