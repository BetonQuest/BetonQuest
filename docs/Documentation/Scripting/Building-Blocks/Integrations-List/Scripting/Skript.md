# [Skript](http://dev.bukkit.org/bukkit-plugins/skript/)

BetonQuest can also hook into Skript. Firstly, to avoid any confusion, I will reference to everything here by
name of the plugin (Skript action is something else than BetonQuest action).
Having Skript on your server will enable using BetonQuest actions and conditions in scripts, and also trigger them by BetonQuest action.

You can use cross-package paths using `-` between the packages. Example:
`player meets condition "default-Forest-Jack>Completed"`

### Skript event triggered by BetonQuest `skript` action

This entry will describe two things: Skript event and BetonQuest action.

1. **Skript event** - `on [betonquest] action "id"` - this is the line you use in your scripts to trigger the code.
  `betonquest` part is optional, and `id` is just some string, which must be equal to the one you specified in
  BetonQuest action.
2. **BetonQuest action** - `skript` - this action will trigger the above Skript event in your scripts.
  The instruction string accepts only one argument, id of the event. It have to be the same as the one defined in
  Skript event for it to be triggered.

**In your script**

```text title="Example"
on betonquest action "concrete":
```

**In BetonQuest**

```YAML title="Example"
actions:
  fire_concrete_script: "skript concrete"
```

### Skript condition

You can check BetonQuest conditions in your scripts by using the syntax `player meets [betonquest] condition "id"`. `betonquest` is optional, and `id` is the name of the condition, as defined in the _conditions_ section.

**In your script**

```text title="Example"
player meets condition "has_ore"
```

**In BetonQuest**

```YAML title="Example"
conditions:
  has_ore: "item iron_ore:5"
```

### Skript action

You can also fire BetonQuest actions with scripts. The syntax for Skript effect is `fire [betonquest] action "id" for player`. Everything else works just like in condition above.

**In your script**

```text title="Example"
fire action "give_emeralds" for player
```

**In BetonQuest**

```YAML title="Example"
actions:
  give_emeralds: "give emerald:5"
```
