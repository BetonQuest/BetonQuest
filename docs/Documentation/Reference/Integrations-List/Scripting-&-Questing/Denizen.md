# [Denizen](http://dev.bukkit.org/bukkit-plugins/denizen/)

@snippet:versions:minimum@ _1.2.5-SNAPSHOT_

Depenizen is also integrated with BetonQuest! Discover available features on the [meta documentation](https://meta.denizenscript.com/Docs/Search/BetonQuest).

## Actions

### `Script`

__Context__: @snippet:action-meta:online-offline@  
__Syntax__: `script <name>`  
__Description__: With this action you can fire Denizen task scripts.

Don't confuse it with `skript` action, these are different. The first and only argument is the name of the script.

```YAML title="Example"
actions:
  runDenizenScript: "script beton"
```
