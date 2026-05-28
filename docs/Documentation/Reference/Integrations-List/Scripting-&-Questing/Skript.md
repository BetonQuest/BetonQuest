# [Skript](http://dev.bukkit.org/bukkit-plugins/skript/)

@snippet:versions:minimum@ _2.9.1_

To avoid any confusion, everything is referenced with the plugin name.
Having Skript on your server will enable using BetonQuest actions and conditions in scripts and also trigger them by BetonQuest action.

## Skript condition

You can check BetonQuest conditions in your scripts by using the syntax
`player meets [betonquest] condition "id"`. It is optional to write `betonquest` and `id` is the package and name 
of the condition, as defined in the _conditions_ section.

```text title="Example in Script"
player meets condition "MyPackage>has_ore"
```

```YAML title="Example in BetonQuest"
conditions:
  has_ore: "item iron_ore:5"
```

## Skript event

You can fire BetonQuest actions in your scripts by using the syntax
`fire [betonquest] action "id" for player`. Everything else works just like in the condition above.

```text title="Example in Script"
fire action "MyPackage>give_emeralds" for player
```

```YAML title="Example in BetonQuest"
actions:
  give_emeralds: "give emerald:5"
```

## Actions

### `Skript`

__Context__: @snippet:action-meta:online@  
__Syntax__: `skript <id>`  
__Description__: Runs Skript event with given id.

This entry will describe two things:

1. **Skript event** - `on [betonquest] action "id"` - this is the line you use in your scripts to trigger the code.
  `betonquest` part is optional, and `id` is just some string, which must be equal to the one you specified in
  BetonQuest action.
2. **BetonQuest action** - `skript` - this action will trigger the above Skript event in your scripts.
  The instruction string accepts only one argument, id of the event. It have to be the same as the one defined in
  Skript event for it to be triggered.

```text title="Example in Script"
on betonquest action "concrete":
```

```YAML title="Example in BetonQuest"
actions:
  fire_concrete_script: "skript concrete"
```
