---
icon: material/book-open-variant-outline
---
# Definition Encyclopedia

This is the definition encyclopedia. All important and regularly used keywords are defined here.

## Data Types

A list of all data types that require a special and more elaborate explanation.

### Block Selectors

!!! info "Block Selector"

    A `block selector` is used whenever targeting a block or material (or multiple blocks).
    The format of a block selector is: `namespace:material[state=value,...]`:
    
      - `namespace` - (optional) The material namespace. If left out then it will be assumed to be 'minecraft'.  
      May be a [regex](#regular-expressions).
      - `material` - The material the block is made of. All materials can be found in
      [Spigots Javadocs](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html).   
      It may be a [regex](#regular-expressions).
      If the regex ends with square brackets you have to add another pair of empty square brackets even if you don't want to 
      use the state argument (`[regex][]`).  
      Instead of using a regex to match multiple materials you can also define a [tag](https://minecraft.wiki/w/Tag).
      Every tag matches a special group of blocks or items that can be grouped together logically. They can be using this format `:blocks:flowers` or `minecraft:blocks:flowers`.
      Be aware that a tag always starts with either `:` or a namespace.
      - `state` - (optional) The block states can be provided in a comma separated `key=value` list surrounded by square brackets.
       You can look up states in the Minecraft [wiki](https://minecraft.wiki/w/Block_states).
       Any state left out will be excluded from the matching process.  
       *Values* may be a [regex](#regular-expressions).
    
    **Examples**: 
    
      - `minecraft:stone` - Matches all blocks of type STONE
      - `redstone_wire` - Matches all blocks of type REDSTONE_WIRE
      - `redstone_wire[power=5]` - Matches all blocks of type REDSTONE_WIRE and which have a power of 5
      - `redstone_wire[power=5,facing=1]` - Matches all blocks of type REDSTONE_WIRE and which have both a power of 5 and are facing 1
      - `.*_LOG` - Matches all LOGS
      - `.*` - Matches everything
      - `.*[waterlogged=true]` - Matches all waterlogged blocks
      - `minecraft:blocks:flowers` - Matches all flowers
      - `:blocks:crops[age=0]` - Matches all crops with an age of 0 meaning, not grown / just planted
    
    The block state will ignore all additional block states on the block it's compared with by default.
    Example: `fence[facing=north]` matches `fence[facing=north]` and `fence[facing=north,waterlogged=true]`.
    You can add an `exactMatch` argument if you only want to match blocks that exactly match the block state. 
    
    A regex is allowed in any block state value when the block selector is used to **match** blocks.
    You cannot use a regex in block states when the block selector is used for **placing** blocks.

### Regular Expressions

!!! info "Regular Expression"

    A regular expression is a sequence of characters that is used for pattern matching on strings. 
    It's used in BetonQuest to check if game objects match a user-defined input.
    Regular expressions are a powerful and versatile tool, but they can be quite confusing to non-programmers at first.
    
    If you want to use more complex regular expressions, have a look at this [cheatsheet](https://medium.com/factory-mind/regex-tutorial-a-simple-cheatsheet-by-examples-649dc1c3f285).
    You might also want to use a tool like [Regexr](https://regexr.com/) to create and test your regular expressions at the same time.
    
    **Examples**:
    
    | Use Case                                                      | Regex                    |
    |---------------------------------------------------------------|--------------------------|
    | A specific text e.g. `STONE`                                  | `STONE`                  |
    | A text starting with `STONE`                                  | `STONE.*`                |
    | A text ending with `_LOG`                                     | `.*_LOG`                 |
    | A specific number e.g. `42`                                   | `^42$`                   |
    | A specific range of numbers, e.g. any number between 0 and 99 | `[0-9]{1,2}`             |
    | Positive numbers only                                         | `^\d+$`                  |
    | Negative numbers only                                         | `^-\d+$`                 |
    | Any number                                                    | `[-+]?[0-9]+(\.[0-9]+)?` |

### Unified Location Format

!!! info "Location"

    A location consists of two elements: 
    
    - A base location/position
    - An optional vector offset
    
    The base location is always required and is defined by the format `x;y;z;world;yaw;pitch`.  
    The vector offset is optional and is defined by adding `->(a;b;c)` after the base location resulting in `x;y;z;world;yaw;pitch->(a;b;c)`.  
    `x`, `y`, `z` are the coordinates of the base location and `yaw` and `pitch` are the optional rotation at that 
    location.  
    `a`, `b` `c` are the values of the vector offset and are _added_ to the base location.  
    `world` is the name of the world the location is set in.
    All values besides `world` are numbers that allow decimal places.
    
    Every single element may be a placeholder as well as the entire location itself.
    As an example you can use the [`%location%`](Placeholders-List.md#location) placeholder to get a player's current location.
    
    **Examples**:
    
    - `100;200;300;world;90;-45`
    - `100;200;300;world_nether->(10;2.5;-13)`
    - `%location%`
    - `%location%->(10;2.5;-13)`

## Element Meta

### Context
