---
icon: material/sword
---
## Item Basics  
Items in BetonQuest are defined in the _items_ section. Each item has an instruction string, similar to events, conditions etc.

BetonQuest provides the `simple` item type, to which this page will outline the creation process. _Item type_ here 
refers to the "source" of the item (A bit like when you specify the [namespace](https://minecraft.fandom.com/wiki/Resource_location) in `/give` commands). 

Here is the basic syntax:
```YAML
items:
  itemExample: simple BLOCK_SELECTOR other arguments... #(1)!
```

1. Consider [YAML multiline syntax](../Scripting/Quoting-&-YAML.md) for 
long item 
strings.

[BLOCK_SELECTOR](../Scripting/Data-Formats.md#block-selectors) specifies the type of item you are creating. At its 
most basic, it is the item material. It doesn't have to be all in uppercase.


`Other arguments` is for additional data like the name of the item, lore, enchantments or potion effects.
There are two categories for these arguments: The ones you can apply to every item and type-specific arguments.
Examples would be `name` (for every item type) and `text` (only for books).


Every argument is used in two ways: When creating an item and when checking if some existing item matches the 
instruction.

The first case is pretty straightforward - BetonQuest takes all the data you specified and creates an item. You would 
give it to your players using events. 
The second case is more complicated. You can require certain properties of the item to exist, others to not exist, or 
skip some property checks altogether. You would use these items in conditions.


These are arguments that can be applied to every item:
!!! info inline end
    As of version [3.0.0-DEV-416](/Documentation/Configuration/Version-Changes/Migration-2-3/#300-dev-416-simple-item-textparser)
    , you no 
    longer require underscores
    and need to quote the `name` and `lore` arguments.

<div class="annotate" markdown>
- `name`
The display name of the item. If you want to specifically say that the item must not have any name, use `none` 
keyword. Default styling is italic.(1)

</div>

1. If you do not want italics, you must append `<!i>` before the name text. See [Minimessage Formatting](https://docs.advntr.dev/minimessage/format.html#decoration) for more information. 

<div class="annotate" markdown>
* `lore` - Text under the item's name. Default styling of lore is purple and italic. 
To make a new line, use `;` character. If you require the item to not have lore at all, use `none` keyword.
By default, lore will match only if all lines are exactly the same.
If you want to accept all items that contain specified lines (and/or more lines),
add `lore-containing` argument to the instruction string. (1)

</div>

1. If you do not want the default styling, you must append `<!i><colorValue>` before the text in your `lore` argument.

<div class="annotate" markdown>
* `enchants` - a list of enchantments and their levels. You can require the item to not have any enchantments by 
using `none` keyword.
 Each enchantment consists of these things, separated by 
colons(1):
    - [name](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/enchantments/Enchantment.html)
    - level (only positive numbers, including zero) (2)

      * By default, all specified enchantments are required. If you want to check if the item contains a matching 
      enchantment (and/or more enchants), add `enchants-containing` argument to the instruction string. Each 
      specified enchantment will be required on the item by default unless you prefix its name with `none-`(3). 
!!! warning
    **Do not use `none-` prefix unless you're using `enchants-containing` argument**, or else it doesn't make any sense 
    and will break the check!

* `unbreakable` - This makes the item unbreakable.
   You can specify it as either `unbreakable` or `unbreakable:true` to require an item to be unbreakable.
   If you want to check if the item is breakable, use `unbreakable:false`.


* `custom-model-data` - Set the custom model data of the item. You have to specify the data value: 
`custom-model-data:3`.
   To check that an item does not have custom model data set `no-custom-model-data`.


* `flags` - Item flags that govern the visibility of some item info (comma-delimited) including:
    - HIDE_ENCHANTS: Hides the item's enchants
    - HIDE_ATTRIBUTES: Hides attributes like damage
    - HIDE_UNBREAKABLE: Hides the unbreakable tag
    - HIDE_DESTROYS: Hides what the item can break or destroy
    - HIDE_PLACED_ON: Hides where the item can be placed
    - HIDE_POTION_EFFECTS: Hides potion effects, book and firework info, map tool tips, banner patters, and enchantments
    - HIDE_DYE: Hides the dye labels on colored leather armor

</div>

1. You can specify additional enchantments by separating them with commas. 
2. You can also add `+`/`-` characters to the 
enchantment level to make the check require levels greater/less (and equal) than specified. If you don't care about 
the level, replace the number with `?`.
3. For example `none-knockback` means that the item must not have any knockback enchantment.



??? example "Item Creation Examples" 
    === "Basic Item Example"
        ```YAML 
        holySword: simple stone_sword "name:&4Sword made of Holy Concrete" "lore:Blessed by the Emperor!" #(1)!
        ```
        
        1. ![folder button](../../../_media/content/Documentation/Features/BasicItemExampleSimple.PNG)
  
    === "Complex Item Example With Multiline Syntax"
        ```YAML
        holierSword: >-
        simple stone_sword 
        "name:<!i><red>An Even Better Sword" 
        "lore:<#FF1CA4>Sharp as can be!"
        enchants:damage_all:3
        unbreakable
        flags:HIDE_ENCHANTS,HIDE_ATTRIBUTES,HIDE_UNBREAKABLE #(1)!
        ```
        
        1. ![folder button](../../../_media/content/Documentation/Features/BasicItemExampleComplexMltLine.png)
??? example "Item Matching Examples:material-information-outline:{ title="For when you want to parse specific parts of your items." }"
    === "Lore Matching Example"
        ```YAML
        newSword: simple stone_sword "name:Excellent Sword" "lore:Sharper than most!" lore-containing #(1)!"
        ```
        
        1.  Will match all stone swords containing a lore-line `Sharper than most!`. Can still be used as a regular 
        item but mainly used for `item` condition checks.
    === "Enchantment Matching Examples"
        ```YAML
        sharpSword: simple stone_sword "name:Sharp Sword" enchants:damage_all:? enchants-containing #(1)!
        bluntSword: simple stone_sword name:none enchants:none-damage_all enchants-containing #(2)!
        ```
        
        1. Will match all stone swords with any `damage_all` enchantment.
        2. Will only match with stone swords that do *not* have a `damage_all` enchantment.


## Special Item Types

### Books

_This applies to a written book and a book and quill._

- `title` - the title of a book.  
    If you want to specifically say that the book must not have any title, use `none` keyword.

- `author` - the author of a book.  
    If you want to specifically say that the book must not have any author, use `none` keyword.

- `text` - the text of the book.  
    The text will wrap if amount of characters exceeds `journal.line_length` and `journal.line_count` setting in "_config.yml_".
    If you want to manually wrap the page, use `|` character.
    If you don't want the book to have any text, use `none` keyword instead.

```YAML title="Examples"
"title:Malleus Maleficarum"
"author:&eGallus Anonymus"
"text:@[minimessage]Lorem ipsum dolor sit amet, <newline>consectetur adipiscing elit. |Pellentesque ligula urna(...)"
```

### Potions

_This applies to potions, splash potions and lingering potions._

- `type` - type of a potion. Here's [the list](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/potion/PotionType.html) of possible types.
    Do not mistake this for a custom effect, this argument corresponds to the default vanilla potion types.

- `extended` - extended property of the potion (you can achieve it in-game by adding redstone).
    It can be specified as `extended` or `extended:true`. If you want to check the potion that is NOT extended, use `extended:false`.

- `upgraded` - upgraded property of the potion (you can achieve it in-game by adding glowstone).
    It can be specified as `upgraded` or `upgraded:true`. If you want to check the potion that is NOT upgraded, use `upgraded:false`.

- `effects` - a list of custom effects. These are independent of the potion type. The effects must be separated by commas.
    Each effect consists of these things, separated by colons:

    - [type](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html) (this is different stuff that the link above!)
    - power
    - duration (in seconds)
    
    An example would be `WITHER:2:30`, which is a wither effect of level 2 for 30 seconds.

    If you want to target only potions without custom effects, use `none` keyword. You can target potions with level
    and time greater/less (and equal) than specified with `+`/`-` character after the number.
    If you don't care about the level/time, you can replace them with question mark.
    
    By default, all specified effects are required.
    If you want to check if the potion contains these effects among others, add `effects-containing` argument to the instruction string.
    Now if you want to make sure the potion doesn't contain a specific effect, prefix the effect name with `none-`.
    **Don't use that prefix unless you're also using `effects-containing` argument**, it doesn't make any sense and it will break the check.

**Examples**:

```YAML
type:instant_heal
extended
upgraded:false
effects:poison:1+:?,slow:?:45-
effects:none-weakness,invisibility:?:? effects-containing
```

### Heads

#### Player Heads

- `owner` - this is the name of the head owner. It will **not** use formatting.
    If you want to check for heads without any owner, use `none` keyword.
  - Use `owner:` to get the current players head. You need to quote the whole instruction when using that.

```YAML title="Examples"
owner:Co0sh
owner:none
```

#### Custom Heads 

This applies to heads with custom texture (Base64 encoded).

- `player-id` - this is the UUID of the head owner.
- `texture` - this is the Base64 encoded JSON for the texture metadata.

**Examples**:

The metadata will be automatically extracted from an item in your hand when using the item command and produce something like the following item data:

```YAML
player-id:66ab473e-d118-4e55-9717-431dfe7a69bc
texture:eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmIwNmIxOGQzZGZlZGFiNDQ0NjZlMGE3NGUxNTVhOGYyMTc4NzIwNDBhMDg1NTIwYTVhMGYzMGU4Y2QxODg1YyJ9fX0=
```

### Leather armor

_This applies to all parts of leather armor._

- `color` - this is the color of the armor piece. It can be either one of [these values](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/DyeColor.html),
    a hexadecimal RGB value prefixed with `#` character or its decimal representation without the prefix.
    You can also check if the armor piece doesn't have any color with `none` keyword.

**Examples**:

```YAML
color:light_blue
color:#ff00ff
color:none
```

### Fireworks

_This applies to fireworks._

- `firework` - this is a list of effects of the firework rocket. They are separated by commas.
    Each effect consists of these things separated by colons:

    - [effect type](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/FireworkEffect.Type.html)
    - a list of main colors (refer to leather armor colors above for syntax) separated by semicolons
    - a list of fade colors
    - `true`/`false` keyword for trail effect
    - `true`/`false` keyword for flicker.

    Note the separation characters, this is important: commas separate effects, colons separate effect properties, semicolons separate colors.
    
    If you want to target fireworks without any effects, use `none` keyword. If you want to target any effect type, use question mark instead of the effect name.
    If you don't want the effect to have any main/fade colors, use `none` keyword in the place of colors.
    If you don't care about main/fade colors, use question marks in that place.
    If you don't care about trail/flicker effect, use question marks instead of `true`/`false` keyword.
    
    By default, the check will require all specified effects to be present on the firework.
    You can check if the firework contains specified effects among others by adding `firework-containing` argument to the instruction string.
    To match the item which must not have an effect, prefix the effect name with `none-` keyword.
    **Don't use that prefix unless you're also using `firework-containing` argument**, it doesn't make any sense and will break the check.

- `power` - flight duration of the firework, in levels. You can use `+`/`-` character to target greater/less (and equal) levels.


```YAML title="Examples"
firework:ball:red;white:green;blue:true:true,ball_large:green;yellow:pink;black:false:false
firework:burst:?:none:?:? firework-containing
firework:none-creeper firework-containing
firework:none
power:3
power:2+
```

### Firework charges

_This applies to firework charges._

- `firework` - this is almost the same as fireworks. You can only specify a single effect and the `power` argument has no effects.

## Backpack

Sometimes you'll want some items to be persistent over death. The quest could be broken if the player loses them.
Such an item wouldn't be dropped (on death), instead it would be placed in the player's backpack.

You can add a specific line to an item's lore to make it persistent. It's `&2Quest Item` (you need to use quotes) if your default language is english.
The translation of the line can be found in the lang files if a different default language is configured. It's also possible to change the translation. 

Note that this must be an entirely new line in the lore!    

```YAML title="Example" 
important_sword: "simple DIAMOND_SWORD \"name:Sword for destroying  The Concrete\"
  \"lore:Made of pure Mithril;&2Quest Item\""
```

The backpack can be opened with the **/backpack** command. The inventory window will open, displaying your stored items.
The first slot is always the journal, and if you get it, the slot will stay empty. You can transfer quest items back and forth between inventories by clicking on them.
Left click will transfer just one item, right click will try to transfer all items. Normal items cannot be stored into the backpack, so it's not an infinite inventory.

If you will ever have more than one page of quest items, the buttons will appear.
You can customize those buttons by creating `previous_button` and `next_button` items in the _items_ section.
Their name will be overwritten with the one defined in _messages.yml_.

Quest items cannot be dropped and most world interaction is blocked.

!!! note "Creative Mode"
    Don't worry if the item-dropping filter isn't working for your items when you're in creative mode - it's not a bug.
    It's a feature. Creative-mode players should be able to easily put quest items in containers like TreasureChests.
