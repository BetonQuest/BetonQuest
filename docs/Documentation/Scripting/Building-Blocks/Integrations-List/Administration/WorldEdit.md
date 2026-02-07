# WorldEdit[](http://dev.bukkit.org/bukkit-plugins/worldedit/) or FastAsyncWorldEdit[](https://www.spigotmc.org/resources/13932/)

### Actions

#### Paste schematic: `paste`

**persistent**, **static**

This action will paste a schematic at the given location.
The first argument is a location and the second one is the name of a schematic file.
The file must be located in `WorldEdit/schematics` or `FastAsyncWorldEdit/schematics` and must have a name like
`some_building.{++schematic++}`. If WorldEdit saves `.schem` schematic files, simply append `.schem` to the
schematic name in the action's instruction.

The optional `noair` keyword can be added to ignore air blocks while pasting.
You can also rotate the schematic by adding `rotation:90` where `90` is the angle in degrees.

```YAML title="Example"
actions:
  pasteCastle: "paste 100;200;300;world castle noair" #(1)!
  pasteTree: "paste 100;200;300;world tree.schem noair" #(2)!
```

1. Pastes the schematic file `castle.{++schematic++}` at the location `100;200;300;world`.
2. Pastes the schematic file `tree.{++schem++}` at the location `100;200;300;world`.
