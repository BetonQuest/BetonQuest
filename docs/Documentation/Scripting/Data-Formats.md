---
icon: material/cube
---

## :octicons-location-16: Unified location formating

Whenever you want to define locations in your actions, conditions, objectives or anywhere else, you will define it
with this specific format. The location consists of 2 things: base and vector. Only the base is always required.

### Base Location
Locations are defined in the format `x;y;z;world;yaw;pitch` for example `100;200;300;world`, where `100` is the x
coordinate, `200` for y, `300` for z and `world` is the name of the world. All numbers may have decimal places.
You can also omit the `yaw` and `pitch` values as they are optional.
They define the rotation and if you want to set them you must provide them both.
Yaw is the left-right rotation, pitch is the up-down rotation.
A fully defined location may look like this: `100;200;300;world;90;-45`.

Every single element may be a placeholder as well as the entire location itself.
As an example you can use the [`%location%`](Building-Blocks/Placeholders-List.md#location-placeholder) placeholder to get a player's current location.

### Vectors
The vector is a modification of the location. Vectors look like `->(10;2.5;-13)` and are added
to the end of the base. This will modify the location, X by 10, Y by 2.5 and Z by -13. For example, location written as
`100;200;300;world_nether->(10;2.5;-13)` will generate a location with X=110, Y=202.5 and Z=287 in the world `world_nether`.

## Block Selectors

When specifying a way of matching a block, a `block selector` is used.

### Format

The format of a block selector is: `namespace:material[state=value,...]`

Where:

  - `namespace` - (optional) The material namespace. If left out then it will be assumed to be 'minecraft'.
   Can be a [regex](#regex-regular-expressions).
  
  - `material` - The material the block is made of. All materials can be found in
  [Spigots Javadocs](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html). 
  It can be a [regex](#regex-regular-expressions).
  If the regex ends with square brackets you have to add another pair of empty square brackets even if you don't want to 
  use the state argument (`[regex][]`).  
  Instead of using a regex to match multiple materials you can also define a [tag](https://minecraft.wiki/w/Tag).
  Every tag matches a special group of blocks or items that can be grouped together logically. They can be using this format `:blocks:flowers` or `minecraft:blocks:flowers`.
  Be aware that a tag always starts with either `:` or a namespace. 
  
  - `state` - (optional) The block states can be provided in a comma separated `key=value` list surrounded by square brackets.
   You can look up states in the Minecraft [wiki](https://minecraft.wiki/w/Block_states).
   Any states left out will be ignored when matching.
   *Values* can be a [regex](#regex-regular-expressions).

Examples:

  - `minecraft:stone` - Matches all blocks of type STONE
  
  - `redstone_wire` - Matches all blocks of type REDSTONE_WIRE
  
  - `redstone_wire[power=5]` - Matches all blocks of type REDSTONE_WIRE and which have a power of 5
  
  - `redstone_wire[power=5,facing=1]` - Matches all blocks of type REDSTONE_WIRE and which have both a power of 5 and are facing 1
  
  - `.*_LOG` - Matches all LOGS
  
  - `.*` - Matches everything
  
  - `.*[waterlogged=true]` - Matches all waterlogged blocks
  
  - `minecraft:blocks:flowers` - Matches all flowers
  
  - `:blocks:crops[age=0]` - Matches all crops with an age of 0 meaning, not grown / just planted

### Setting behaviour

A block selector with a regex or tag as it's material name results in a random block out of all blocks that match that regex or tag.
You cannot use a regex in block states when the block selector is used for placing blocks.

### Matching behaviour

The block state will ignore all additional block states on the block it's compared with by default.
Example: `fence[facing=north] matches fence[facing=north] and fence[facing=north,waterlogged=true]`
You can add an `exactMatch` argument if you only want to match blocks that exactly match the block state. 
A regex is allowed in any block state value when the block selector is used to match blocks.

## Regex (Regular Expressions)
A regular expression is a sequence of characters that specifies a search pattern for text. It's used in BetonQuest to
check if game objects match a user-defined input. For example, [Block Selectors](#block-selectors) use a regex to match
multiple materials or block states. You can also use regular expressions in the 
[variable condition](Building-Blocks/Conditions-List.md#placeholder-matches-expression) or the 
[password objective](Building-Blocks/Objectives-List.md#enter-a-password) to match player names, item names, etc. These expressions are
a very powerful tool, but can be confusing at first.

### Common Use Cases

| Use Case                                                      | Regex                  |
|---------------------------------------------------------------|------------------------|
| A specific text e.g. `STONE`                                  | `STONE`                |
| A text starting with `STONE`                                  | `STONE.*`              |
| A text ending with `_LOG`                                     | `.*_LOG`               |
| A specific number e.g. `42`                                   | `^42$`                 |
| A specific range of numbers, e.g. any number between 0 and 99 | `[0-9]{1,2}`           |
| Positive numbers only                                         | `^\d+$`                |
| Negative numbers only                                         | `^-\d+$`               |
| Any number                                                    | `[-+]?[0-9]+\.?[0-9]+` |

### More complex use cases

If you want to use complex patterns you must learn more about regular expressions. There are countless resources online,
for example you could read this 
[cheatsheet](https://medium.com/factory-mind/regex-tutorial-a-simple-cheatsheet-by-examples-649dc1c3f285).
