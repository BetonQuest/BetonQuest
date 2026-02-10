# [ItemsAdder](https://www.spigotmc.org/resources/73355/)

!!! info ""
    **Required ItemsAdder version: _4.0.10_ or above**

### Items

ItemsAdder usage is integrated to the [Items](../../../../Features/Items.md) system and thus used for actions and conditions.

In addition, you can also add `quest-item` argument to tag them as "QuestItem".

```YAML title="Example"
items:
  ruby: "itemsAdder iasurvival:ruby"
  sword: "itemsAdder iaalchemy:mysterious_sword quest-item"
conditions:
  hasSword: "item sword"
actions:
  giveRuby: "give ruby:3"
```

### Conditions

#### Check for block `itemsAdderBlock`

Check if the ItemsAdder block is at a location.

| Parameter  | Syntax                                                     | Default Value          | Explanation                         |
|------------|------------------------------------------------------------|------------------------|-------------------------------------|
| _itemId_   | string                                                     | :octicons-x-circle-16: | Identifier of the block to check.   |
| _location_ | [ULF](../../../Data-Formats.md#unified-location-formating) | :octicons-x-circle-16: | The location to check the block at. |

```YAML title="Example"
conditions:
  iaBlock: "itemsAdderBlock itemsadder:ruby_ore 40;72;3;world"
```

### Actions

#### Place block `itemsAdderBlock`

Changes the block at the given position.

| Parameter  | Syntax                                                     | Default Value          | Explanation                         |
|------------|------------------------------------------------------------|------------------------|-------------------------------------|
| _itemId_   | string                                                     | :octicons-x-circle-16: | Identifier of the block to place.   |
| _location_ | [ULF](../../../Data-Formats.md#unified-location-formating) | :octicons-x-circle-16: | The location to place the block at. |

```YAML title="Example"
actions:
  iaSetBlock: "itemsAdderBlock itemsadder:ruby_ore 100;200;300;world"
```

#### Play totem animation `itemsAdderAnimation`

Plays an ItemsAdder totem animation.

| Parameter     | Syntax | Default Value          | Explanation             |
|---------------|--------|------------------------|-------------------------|
| _animationId_ | string | :octicons-x-circle-16: | Animation name to play. |

```YAML title="Example"
actions:
  iaPlayAnimation: "itemsAdderAnimation totem1"
```

### Objectives

#### Break Block `itemsAdderBlockBreak`

To complete this objective player must break specified amount of ItemsAdder blocks.
You can add a `notify` keyword if you want to send a notification to players whenever the objective progresses.

| Parameter | Syntax        | Default Value          | Explanation                       |
|-----------|---------------|------------------------|-----------------------------------|
| _itemId_  | string        | :octicons-x-circle-16: | Identifier of the block to break. |
| _amount_  | amount:number | 1                      | Amount of blocks to break.        |

```YAML title="Example"
objectives:
  iaBreak: "itemsAdderBlockBreak itemsadder:ruby_ore amount:20 notify:5"
```

#### Place Block `itemsAdderBlockPlace`

To complete this objective player must place specified amount of ItemsAdder blocks.
You can add a `notify` keyword if you want to send a notification to players whenever the objective progresses.

| Parameter | Syntax        | Default Value          | Explanation                       |
|-----------|---------------|------------------------|-----------------------------------|
| _itemId_  | string        | :octicons-x-circle-16: | Identifier of the block to place. |
| _amount_  | amount:number | 1                      | Amount of blocks to place.        |

```YAML title="Example"
objectives:
  iaPlace: "itemsAdderBlockPlace iasurvival:restoration_table"
```
