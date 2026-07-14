# [TheBrewingProject](https://modrinth.com/plugin/thebrewingproject)

@snippet:versions:minimum@ _3.3.0_

## Items

### `Brew`


__Syntax__: `tbp_brew <brew-id> [quality] [quest-item]`  
__Description__: A brew item definition

TheBrewingProject brew item usage is integrated to the [Items](../../../Advanced/Items.md) system and can therefore be used for 
actions and conditions.

| Parameter                | Type                | Explanation                                                                  |
|--------------------------|---------------------|------------------------------------------------------------------------------|
| brew-id <br>[String]     | Required            | Specifies the type of brew                                                   |
| quality <br>[Quality]    | Optional <br>[NONE] | The quality of the brew, if not specified this will not be validated against |
| quest-item <br>[Boolean] | Flag <br>[false]    | Mark this item as a quest item                                               |

```YAML title="Example"
items:
  beer: 'tbp_brew beer quality:excellent'
  hotChocolate: "tbp_brew hot_choc quest-item"
conditions:
  hasBeer: "item beer"
  hasHotChocolateInHand: "hand hotChocolate"
actions:
  giveHotChocolate: "give hotChocolate"
  takeBeer: "take beer"
```

## Conditions

### `Modifier`

__Context__: @snippet:condition-meta:online-offline@
__Syntax__: `tbp_modifier <modifier-id> <operation> <value>`  
__Description__: Compare players modifier with specified value

| Parameter                 | Type     | Explanation                  |
|---------------------------|----------|------------------------------|
| modifier-id <br>[String]  | Required | The id of the modifier       |
| operation <br>[Operation] | Required | The comparison oparator      |
| value <br>[Number]        | Required | The value to compare against |


```YAML title="Example"
conditions:
  drunk50: "tbp_modifier alcohol > 50"
```

## Actions

### `Event`

__Context__: @snippet:action-meta:online@  
__Syntax__: `tbp_event <event-id>`  
__Description__: Trigger a drunken event with the specified id

| Parameter             | Type     | Explanation                |
|-----------------------|----------|----------------------------|
| event-id <br>[String] | Required | The id of the event to run |


```YAML title="Example"
actions:
  puke: "tbp_event <event-key>"
```

## Objectives

### `Cook`

Heated cauldron brew creation.

__Context__: @snippet:objective-meta:online@  
__Syntax__: `tbp_cook <cooking-time> <ingredients> [cauldron-type]`  
__Description__: The player needs to have extracted a brew from a cauldron with specified properties

| Parameter                      | Type                | Explanation                                       |
|--------------------------------|---------------------|---------------------------------------------------|
| cooking-time <br>[Number]      | Requried            | The time in minutes the brew has to be cooked for |
| ingredients  <br>[Ingredients] | Required            | The ingredients added into the cauldorn           |
| cauldron <br>[CauldronType]    | Optional <br>[NONE] | The type of the cauldron block                    |

```YAML title="Example"
objectives:
  beerBase: "tbp_cook 8 wheat/6 cauldron:water"
  failedBase: "tbp_cook 2 wheat/6,apple/3 cauldron:lava"
```

### `Mix`

Cold cauldron brew creation.

__Context__: @snippet:objective-meta:online@  
__Syntax__: `tbp_mix <mixing-time> <ingredients> [cauldron-type]`  
__Description__: The player needs to have extracted a brew from a cauldron with specified properties

| Parameter                     | Type           | Explanation                                      |
|-------------------------------|----------------|--------------------------------------------------|
| mixing-time <br>[Number]      | Required       | The time in minutes the brew has to be mixed for |
| ingredients <br>[Ingredients] | Required       | The ingredients added into the cauldron          |
| cauldron  <br>[CauldronType]  | Optional[NONE] | The type of the cauldron contents                |


```YAML title="Example"
objectives:
  cook: "tbp_mix 2 brewery:gin/3,chorus_fruit/6 cauldron:water"
```

### `Distill`

__Context__: @snippet:objective-meta:online@  
__Syntax__: `tbp_distill <distill-runs>`  
__Description__: The player needs to have extracted a brew from a distillery with specified runs

| Parameter                 | Type     | Explanation                              |
|---------------------------|----------|------------------------------------------|
| distill-runs <br>[Number] | Required | Tha amount of runs of the extracted brew |


```YAML title="Example"
objectives:
  cook: "tbp_distill 6"
```

### `Age`

__Context__: @snippet:objective-meta:online@  
__Syntax__: `tbp_age <barrel-type> <aging-years>`  
__Description__: The player needs to have extracted a brew from a barrel with specified properties

Note that an aging year might change if TheBrewingProject reloads.

| Parameter                    | Type     | Explanation                                                        |
|------------------------------|----------|--------------------------------------------------------------------|
| barrel-type <br>[BarrelType] | Required | The type of the barrel                                             |
| aging-years <br>[Number]     | Required | The time the brew needs to have aged (needs to be larger than 0.5) |


```YAML title="Example"
objectives:
  cook: "tbp_age any 1"
```

### `Transfer`

__Context__: @snippet:objective-meta:online@  
__Syntax__: `tbp_transfer <transfer-type> <structure-type> <brew-id> <operator> <brew-quality>`  
__Description__: The player needs to have transferred a brew with given conditions

| Parameter                          | Type     | Description                                      |
|------------------------------------|----------|--------------------------------------------------|
| transfer-type <br>[TransferType]   | Required | If the player extracted or inserted the brew     |
| structure-type <br>[StructureType] | Required | The type of structure the transfer is from or to |
| brew-id <br>[String]               | Required | The recipe key the brew matches                  |
| operator <br>[Operation]           | Required | A comparison between brew quality                |
| brew-quality <br>[Quality]         | Required | The brew quality to compare against              |

```YAML title="Example"
objectives:
  extract: "tbp_transfer extract barrel beer > good" # Only excellent brews will match
  insert: "tbp_transfer insert barrel whiskey > bad" # Good and excellent brews will match
  extract2: "tbp_transfer extract distillery whiskey != good" # Poor and excellent brews will match
```

### `Consume`

__Context__: @snippet:objective-meta:online@  
__Syntax__: `tbp_consume <brew-id> <operator> <brew-quality>`  
__Description__: The player needs to have consumed a brew with given properties

| Parameter                  | Type     | Description                       |
|----------------------------|----------|-----------------------------------|
| brew-id <br>[String]       | Required | The recipe key the brew matches   |
| operator <br>[Operation]   | Required | A comparison between brew quality |
| brew-quality <br>[Quality] | Required | The brew quality to compare to    |

```YAML title="Example"
objectives:
  consume: "tbp_consume beer > good" # Only matches against excellent brews
```

### `Event`

__Context__: @snippet:objective-meta:online@  
__Syntax__: `tbp_event <event-id>`  
__Description__: The player needs to experience a drunken event with specified key

| Parameter             | Type     | Description                              |
|-----------------------|----------|------------------------------------------|
| event-id <br>[String] | Required | The id of the event a player experienced |


```YAML title="Example"
objectives:
  consume: "tbp_event puke"
  gsitSit: "tbp_event gsit:sit" # If there's integrations available, you can specify those
```

### `StructureDestroy`

__Context__: @snippet:objective-meta:online@  
__Syntax__: `tbp_structure_destroy <structure-type>`  
__Description__: The player needs to destroy a brewing structure with specified type

| Parameter                          | Type     | Explanation                                    |
|------------------------------------|----------|------------------------------------------------|
| structure-type <br>[StructureType] | Required | The structure type the player needs to destroy |

```YAML title="Example"
objectives:
  structureDestroyed: tbp_structure_destroy barrel
```

### `StructureCreate`

__Context__: @snippet:objective-meta:online@  
__Syntax__: `tbp_structure_create <structure-type>`  
__Description__: The player needs to create a brewing structure with specified type

| Parameter                          | Type     | Explanation                                    |
|------------------------------------|----------|------------------------------------------------|
| structure-type <br>[StructureType] | Required | The structure type the player needs to destroy |

```YAML title="Example"
objectives:
  structureCreated: tbp_structure_create distillery
```

*[Quality]: bad, good, excellent
*[CauldronType]: water, lava, snow, brew
*[Operation]: Any of <, <=, =, !=, >=, >
*[Ingredients]: A list with ingredient/amount as entries
*[BarrelType]: any, oak, spruce, birch, jungle, acacia, dark_oak, crimson, warped, cherry, pale_oak, copper
*[TransferType]: extract, insert
*[StructureType]: barrel, distillery, cauldron

--8<-- "instruction-datatypes.md"
