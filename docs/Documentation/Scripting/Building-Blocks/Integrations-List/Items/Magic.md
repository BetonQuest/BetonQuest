# [Magic](http://dev.bukkit.org/bukkit-plugins/magic/)

@snippet:versions:minimum@ _10.2_

!!! info
    Magic integration supports other features as well.
    For those, please visit the [Magic](../MMO/Magic.md) MMO page.

## Items

### `Magic`

Magic items are integrated to the [BetonQuest Items](../Items/index.md) system.

In addition, you can also add `quest-item` argument to tag them as "QuestItem".

```YAML title="Example"
items:
  crown: "magic KingsCrown"
  sword: "magic SkeletonKingSword quest-item"
conditions:
  hasCrown: "armor crown"
actions:
  giveSword: "give sword"
```
