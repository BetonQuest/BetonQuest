# [TheBrewingProject](https://modrinth.com/plugin/thebrewingproject)

@snippet:versions:minimum@ _3.2.0_

## Items

### `Brew`

TheBrewingProject brew item usage is integrated to the [Items](../../../Advanced/Items.md) system and thus used for 
actions and  conditions.

The first argument is the name and the second the quality.
You can specify the mode to select the brew by its `id`.  
The `quality` is used for determining equality, and can be `bad`,`good`,`excellent`.  
In addition, you can also add `quest-item` argument to tag them as "QuestItem".

```YAML title="Example"
items:
  beer: 'tbp_brew beer excellent'
  hotChocolate: "tbp_brew hot_choc bad quest-item"
conditions:
  hasBeer: "item beer"
  hasHotChocolateInHand: "hand hotChocolate"
actions:
  giveHotChocolate: "give hotChocolate"
  takeBeer: "take beer"
```

## Conditions

### `Modifier`

__Context__: @snippet:condition-meta:independent@  
__Syntax__: `tbp_modifier <modifier-key> <operation> <value>`  
__Description__: Compare players modifier with specified value

The valid operations are: `<`, `<=`, `=`, `!=`, `>=`, `>`.

```YAML title="Example"
conditions:
  drunk50: "tbp_modifier alcohol > 50"
```

## Actions

### `Event`

__Context__: @snippet:condition-meta:online@  
__Syntax__: `tbp_event <event-key>`  
__Description__: Trigger a drunken event with the specified key

```YAML title="Example"
actions:
  puke: "tbp_event <event-key>"
```
## Objectives

### `Cook`

Heated cauldron brew creation.

__Context__: @snippet:condition-meta:online@  
__Syntax__: `tbp_cook <cauldron-type> <cooking-time> <ingredients>`  
__Description__: The player needs to have extracted a brew from a cauldron with specified properties

| Parameter       | Syntax                                                         | Explanation                                       |
|-----------------|----------------------------------------------------------------|---------------------------------------------------|
| _cauldron-type_ | Any of `water`, `lava`, `snow`, `brew`                         | The type of the cauldron contents                 |
| _cooking-time_  | Any number above 0                                             | The time in minutes the brew has to be cooked for |
| _ingredients_   | A list with `<ingredient>/<amount>` example: `wheat/6,apple/7` | The ingredients added into the cauldorn           |

```YAML title="Example"
objectives:
  beerBase: "tbp_cook water 8 wheat/6"
  failedBased: "tbp_cook lava 2 wheat/6,apple/3"
```

### `Mix`

Cold cauldron brew creation.

__Context__: @snippet:condition-meta:online@  
__Syntax__: `tbp_mix <cauldron-type> <mixing-time> <ingredients>`  
__Description__: The player needs to have extracted a brew from a cauldron with specified properties

| Parameter       | Syntax                                                         | Explanation                                      |
|-----------------|----------------------------------------------------------------|--------------------------------------------------|
| _cauldron-type_ | Any of `water`, `lava`, `snow`, `brew`                         | The type of the cauldron contents                |
| _mixing-time_   | Any number above 0                                             | The time in minutes the brew has to be mixed for |
| _ingredients_   | A list with `<ingredient>/<amount>` example: `wheat/6,apple/7` | The ingredients added into the cauldorn          |



```YAML title="Example"
objectives:
  cook: "tbp_mix water 2 brewery:gin/3,chorus_fruit/6"
```
### `Distill`

__Context__: @snippet:condition-meta:online@  
__Syntax__: `tbp_distill <distill-runs>`  
__Description__: The player needs to have extracted a brew from a distillery with specified runs

Distill runs needs to be an integer above 0.

```YAML title="Example"
objectives:
  cook: "tbp_distill 6"
```
### `Age`

__Context__: @snippet:condition-meta:online@  
__Syntax__: `tbp_age <barrel-type> <aging-years>`  
__Description__: The player needs to have extracted a brew from a barrel with specified properties

| Parameter     | Syntax                                                                                                                           | Explanation                          |
|---------------|----------------------------------------------------------------------------------------------------------------------------------|--------------------------------------|
| _barrel-type_ | A barrel type, for default values check [here](https://docs.breweryteam.dev/docs/tbp/gameplay/creating-brews/aging#barrel-types) | The type of the barrel               |
| _aging-years_ | A number above (not equal to) 0.5                                                                                                | The time the brew needs to have aged |

```YAML title="Example"
objectives:
  cook: "tbp_age any 1"
```

### `Transfer`

__Context__: @snippet:condition-meta:online@  
__Syntax__: `tbp_transfer <transfer-type> <structure-type> <brew-key> <operator> <brew-quality>`  
__Description__: The player needs to have transferred a brew with given conditions

| Parameter        | Syntax                                                         | Description                                      |
|------------------|----------------------------------------------------------------|--------------------------------------------------|
| _transfer-type_  | Either `extract` or `insert`                                   | If the player extracted or inserted the brew     |
| _structure-type_ | Any of `barrel`, `distillery`, `cauldron`                      | The type of structure the transfer is from or to |
| _brew-key_       | A recipe key                                                   | The recipe key the brew matches                  |
| _operator_       | Any of the following operators `<`, `<=`, `=`, `!=`, `>=`, `>` | A comparison between brew quality                |
| _brew-quality_   | Any of `bad`, `good`, `excellent`                              | The brew quality to compare to                   |

```YAML title="Example"
objectives:
  extract: "tbp_transfer extract barrel beer > good" # Only excellent brews will match
  insert: "tbp_transfer insert barrel whiskey > bad" # Good and excellent brews will match
  extract2: "tbp_transfer extract distillery whiskey != good" # Poor and excellent brews will match
```

### `Consume`

__Context__: @snippet:condition-meta:online@  
__Syntax__: `tbp_consume <brew-key> <operator> <brew-quality>`  
__Description__: The player needs to have consumed a brew with given properties

| Parameter        | Syntax                                                         | Description                                      |
|------------------|----------------------------------------------------------------|--------------------------------------------------|
| _brew-key_       | A recipe key                                                   | The recipe key the brew matches                  |
| _operator_       | Any of the following operators `<`, `<=`, `=`, `!=`, `>=`, `>` | A comparison between brew quality                |
| _brew-quality_   | Any of `bad`, `good`, `excellent`                              | The brew quality to compare to                   |

```YAML title="Example"
objectives:
  consume: "tbp_consume beer > good" # Only matches against excellent brews
```

### `Event`

__Context__: @snippet:condition-meta:online@  
__Syntax__: `tbp_event <event-key>`  
__Description__: The player needs to experience a drunken event with specified key

```YAML title="Example"
objectives:
  consume: "tbp_event puke"
  gsitSit: "tbp_event gsit:sit" # If there's integrations available, you can specify those
```

### `StructureDestroy`

__Context__: @snippet:condition-meta:online@  
__Syntax__: `tbp_structure_destroy <structure-type>`  
__Description__: The player needs to destroy a brewing structure with specified type

_structure-type_ can have the values `barrel` or `distillery`.
