---
icon: material/book-open-variant-outline
---
# Definition Encyclopedia

This is the definition encyclopedia. All important and regularly used keywords are defined here.

## Character Semantics

A list of all characters that are used in the BetonQuest language.
Each character's semantics are highly dependent on the context.
    
| Symbol              | Example                      | Context                     | Reference                                         |
|---------------------|------------------------------|-----------------------------|---------------------------------------------------|
| `@`                 | `@[legacy]`                  | text messages               | [Text Formatting](../Advanced/Text-Formatting.md) |
| `^`                 | `run ^burn ^kill`            | special action syntax       | [Run](Actions-List.md#run)                        |
| `:`                 | `param:value`                | parameters                  | [Parameter Types](#parameter-type)                |
| `,`                 | `3,6,9,14`                   | lists of values             |                                                   |
| `!`                 | `!hasTag`                    | condition negation          |                                                   |
| `>`                 | `package>identifier`         | package-identifer separator |                                                   |
| `-`                 | `package-sub>identifier`     | package path separator      |                                                   |
| `%`                 | `%location%`                 | placeholder brackets        |                                                   |
| `.`                 | `%foo.bar%`                  | sub addressing placeholder  |                                                   |
| `.`                 | `convo.sub`                  | sub addressing conversation |                                                   |
| <code>&#124;</code> | <code>sudo x &#124; y</code> | special action syntax       | [Sudo](Actions-List.md#sudo)                      |
| `+`                 | `+param:value`               | additional parameters       | [Parameter Types](#syntax)                        |
| `"`                 | `"Some text"`                | quoting syntax              |                                                   |
| `~`                 | `0.5~action2`                | special action syntax       | [PickRandom](Actions-List.md#pickrandom)          |
| `_`                 | `_-parent>identifier`        | package parent accessor     |                                                   |

## Data Types

A list of all data types that require a special and more elaborate explanation.

### Basic Types

!!! info "Basic Types"
    
    There are a few basic data types that are commonly used in elements:
    
    - `String`: A string of characters that may contain any character including spaces if quoted.
    - `Number`: Any number, either integer or floating point. Might be negative as well.
    Limited only to java's `integer`, `long`, `float` and `double` depending on the element. 
    See [here](https://www.w3schools.com/java/java_data_types.asp) for more information.
    - `Boolean`: Binary value. Either `true` or `false`.
    
    Other mostly element-specific data types are defined in the other sections or in the element's documentation itself.

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

### Time Unit

!!! info "Time Unit"

    Time units are used to define durations from raw numbers.
    In [Elements](#elements) they usually get their own optional argument.
    BetonQuest supports the following time units _case insensitive_ and with a fixed rate:
    
    - `ticks`: Smallest common time unit for minecraft.
    - `seconds`: 20 ticks = 1 second.
    - `minutes`: 60 seconds = 1 minute.
    - `hours`: 60 minutes = 1 hour.
    - `days`: 24 hours = 1 day.
    - `weeks`: 7 days = 1 week.
    - `months`: 30 days = 1 month.
    - `years`: 365 days = 1 year.

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

## Elements

All actions, conditions, etc. - essentially anything definable under a specific section in the script - are considered elements.

### Element Types

_tbd_

### Context

!!! info "Context"

    The context defines the scope of the element. The context is defined for every element and determines 
    the requirement of a profile for a valid execution of the element.
    Commonly used contexts are:
    
    - `online`: Used for elements that require a player _to be online_ to be properly executed.
    - `offline`: Used for elements that require a player _that may be offline_ to be properly executed.
    - `independent`: Used for elements that _don't require a player_ at all.
    
    Since those requirements are not exclusive to one another, they often appear in combinations.
    The following icons are used to represent the context combination for each element.
    For example, for actions: @snippet:action-meta:online-offline-independent@  
    Be aware that the precise definition of the context variations might differ, so read their hover text for more information.

### Parameter Type

!!! info "Parameter Type"

    There are currently four types of parameters used in elements:
    
    - `Required`: The parameter is required to be **present** in the element's instruction at the exact position and 
    without its name.
    - `Optional`: The parameter is optional and may be **defined** or **absent**. The order is ignored.
    - `Flag`: The parameter is a flag and may be **defined**, **undefined** or **absent**. The order is ignored.
    - `Additionals`: The parameter allows any number of additional **defined** parameters. Their individual names are free 
    to choose and usually depend on the context of the element. All additional parameter names are prefixed with `+` and the 
    order is ignored.
    
    The naming of the parameters is defined in the element's [syntax](#syntax). Except required parameters, all other 
    parameters are named and allow different specifications.
    
    - `present`: A present parameter is simply `value` where `value` is just the value of the parameter, without mentioning the name at all.
    - `defined`: A defined parameter is like `param:value` where `param` is the name of the parameter and `value` is 
    the value of the parameter.
    - `undefined`: An undefined parameter is simply `param` where `param` is the name of the parameter falling back to the 
    `undefined` default value of the parameter.
    - `absent`: An absent parameter is simply not specifying the parameter at all falling back to the `absent` default value 
    of the parameter.

### Syntax

!!! info "Syntax"

    The syntax of an element defines the format of that element's instruction. 
    It usually consists of a single line starting with the element's name and is followed by parameters separated by an element-specific separator.
    
    The common types of parameters are defined like:
    
    - required parameters: `<parameter>`
    - optional parameters: `[parameter]`
    - flag parameters: `{parameter}`
    - additional parameters: `+[...]`
    
    For more information on different types of parameters, see the [Parameter Type](#parameter-type) section.
