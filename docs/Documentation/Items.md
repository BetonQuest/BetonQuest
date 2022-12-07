---
icon: material/sword
---
## Item Basics  
Items in BetonQuest are defined in the _items_ section. Each item has an instruction string, similarly to events, conditions etc. Basic syntax is very simple:

```YAML
item: BLOCK_SELECTOR other arguments...
```

[BLOCK_SELECTOR](#block-selectors) is a type of the item. It doesn't have to be all in uppercase. Other arguments specify data like name of the item, lore, enchantments or potion effects. There are two categories of these arguments: the ones you can apply to every item and type specific arguments. Examples would be name (for every item type) and text (only in books).

Every argument is used in two ways: when creating an item and when checking if some existing item matches the instruction. The first case is pretty straightforward - BetonQuest takes all data you specified and creates an item, simple as that. Second case is more complicated. You can require some property of the item to exist, other not to exist, or skip this property check altogether. You can also accept an item only if some value (like enchantment level) is greater/less than _x_. You can use wildcards in the BLOCK_SELECTOR to match multiple types of items.

These are arguments that can be applied to every item:

- `name` - the display name of the item. All underscores will be replaced with spaces and you can use `&` color codes. If you want to specifically say that the item must not have any name, use `none` keyword.

- `lore` - text under the item's name. Default styling of lore is purple and italic. All underscores will be replaced with spaces and you can use `&` color codes. To make a new line use `;` character. If you require the item not to have lore at all, use `none` keyword. By default lore will match only if all lines are exactly the same. If you want to accept all items which contain specified lines (and/or more lines), add `lore-containing` argument to the instruction string.

- `enchants` - a list of enchantments and their levels. Each enchantment consists of these things, separated by colons:
    - [name](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/enchantments/Enchantment.html)
    - level (a positive number)
    
    For example `damage_all:3` is _Sharpness III_. You can specify additional enchantments by separating them with commas.
    
    You can require the item not to have any enchantments by using `none` keyword. You can also add `+`/`-` character to the enchantment level to make the check require levels greater/less (and equal) than specified. If you don't care about the level, replace the number with a question mark.
    
    By default, all specified enchantments are required. If you want to check if the item contains a matching enchantment (and/or more enchants), add `enchants-containing` argument to the instruction string. Each specified enchantment will be required on the item by default unless you prefix its name with `none-`, for example `none-knockback` means that the item must not have any knockback enchantment. **Do not use `none-` prefix unless you're using `enchants-containing` argument**, it doesn't make any sense and will break the check!

- `unbreakable` - this makes the item unbreakable. You can specify it either as `unbreakable` or `unbreakable:true` to require an item to be unbreakable. If you want to check if the item is breakable, use `unbreakable:false`.

- `custom-model-data` - set the custom model data of the item. You have to specify the data value: `custom-model-data:3`. To check that an item does not have custom model data set `no-custom-model-data`.

```YAML title="Examples"
name:&4Sword_made_of_Holy_Concrete
name:none
lore:&cOnly_this_sword_can_kill_the_Lord_Ruler
lore:&2Quest_Item lore-containing
lore:none
enchants:damage_all:3+,none-knockback
enchants:power:? enchants-containing
enchants:none
unbreakable
unbreakable:false
```

## Special Item Types

### Books

_This applies to a written book and a book and quill._

- `title` - the title of a book. All underscores will be replaced with spaces and you can use `&` color codes. If you want to specifically say that the book must not have any title, use `none` keyword.

- `author` - the author of a book. All underscores will be replaced with spaces, you cannot use color codes here. If you want to specifically say that the book must not have any author, use `none` keyword.

- `text` - the text of the book. All underscores will be replaced with spaces and you can use `&` color codes. The text will wrap to the next page if amount of characters exceeds `journal.chars_per_page` setting in _config.yml_. If you want to manually wrap the page, use `|` character. To go to new line use `\n`. Keep in mind that you can't use any spaces here, you must only use underscores (`_`). This needs to be a single argument, even if it's really long. If you don't want the book to have any text, use `none` keyword instead.

```YAML title="Examples"
title:Malleus_Maleficarum
author:&eGallus_Anonymus
text:Lorem_ipsum_dolor_sit_amet,\nconsectetur_adipiscing_elit.|Pellentesque_ligula_urna(...)
```

### Potions

_This applies to potions, splash potions and lingering potions._

- `type` - type of a potion. Here's [the list](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/potion/PotionType.html) of possible types. Do not mistake this for a custom effect, this argument corresponds to the default vanilla potion types.

- `extended` - extended property of the potion (you can achieve it in-game by adding redstone). It can be specified as `extended` or `extended:true`. If you want to check the potion that is NOT extended, use `extended:false`.

- `upgraded` - upgraded property of the potion (you can achieve it in-game by adding glowstone). It can be specified as `upgraded` or `upgraded:true`. If you want to check the potion that is NOT upgraded, use `upgraded:false`.

- `effects` - a list of custom effects. These are independent of the potion type. The effects must be separated by commas. Each effect consists of these things, separated by colons:

    - [type](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html) (this is different stuff that the link above!)
    - power
    - duration (in seconds)
    
    An example would be `WITHER:2:30`, which is a wither effect of level 2 for 30 seconds.

    If you want to target only potions without custom effects, use `none` keyword. You can target potions with level and time greater/less (and equal) than specified with `+`/`-` character after the number. If you don't care about the level/time, you can replace them with question mark.
    
    By default, all specified effects are required. If you want to check if the potion contains these effects among others, add `effects-containing` argument to the instruction string. Now if you want to make sure the potion doesn't contain a specific effect, prefix the effect name with `none-`. **Don't use that prefix unless you're also using `effects-containing` argument**, it doesn't make any sense and it will break the check.

**Examples**:

```YAML
type:instant_heal
extended
upgraded:false
effects:poison:1+:?,slow:?:45-
effects:none-weakness,invisibility:?:? effects-containing
```

### Heads

_This applies to human heads._

- `owner` - this is the name of the head owner. It will **not** use color codes nor replace underscores with spaces. If you want to check for heads without any owner, use `none` keyword.
  - Use `%player%` to get the current players head.
  - Custom Heads are not supported yet.

**Examples**:

```YAML
owner:Co0sh
owner:none
```

### Leather armor

_This applies to all parts of leather armor._

- `color` - this is the color of the armor piece. It can be either one of [these values](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/DyeColor.html), a hexadecimal RGB value prefixed with `#` character or its decimal representation without the prefix. You can also check if the armor piece doesn't have any color with `none` keyword.

**Examples**:

```YAML
color:light_blue
color:#ff00ff
color:none
```

### Fireworks

_This applies to fireworks._

- `firework` - this is a list of effects of the firework rocket. They are separated by commas. Each effect consists of these things separated by colons:

    - [effect type](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/FireworkEffect.Type.html)
    - a list of main colors (refer to leather armor colors above for syntax) separated by semicolons
    - a list of fade colors
    - `true`/`false` keyword for trail effect
    - `true`/`false` keyword for flicker.

    Note the separation characters, this is important: commas separate effects, colons separate effect properties, semicolons separate colors.
    
    If you want to target fireworks without any effects, use `none` keyword. If you want to target any effect type, use question mark instead of the effect name. If you don't want the effect to have any main/fade colors, use `none` keyword in the place of colors. If you don't care about main/fade colors, use question marks in that place. If you don't care about trail/flicker effect, use question marks instead of `true`/`false` keyword.
    
    By default, the check will require all specified effects to be present on the firework. You can check if the firework contains specified effects among others by adding `firework-containing` argument to the instruction string. To match the item which must not have an effect, prefix the effect name with `none-` keyword. **Don't use that prefix unless you're also using `firework-containing` argument**, it doesn't make any sense and will break the check.

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

You can add a specific line to an item's lore to make it persistent. It's `&2Quest_Item` (`_` is a space in an item's definition) if your default language is english.
The translation of the line can be found in *messages.yml* if a different default language is configured. It's also possible to change the translation. 

Note that this must be an entirely new line in the lore!    

```YAML title="Example" 
important_sword: "DIAMOND_SWORD name:Sword_for_destroying__The_Concrete lore:Made_of_pure_Mithril;&2Quest_Item"
```

The backpack can be opened with the **/backpack** command. The inventory window will open, displaying your stored items. The first slot is always the journal, and if you get it, the slot will stay empty. You can transfer quest items back and forth between inventories by clicking on them. Left click will transfer just one item, right click will try to transfer all items. Normal items cannot be stored into the backpack, so it's not an infinite inventory.

If you will ever have more than one page of quest items, the buttons will appear. You can customize those buttons by creating `previous_button` and `next_button` items in the _items_ section. Their name will be overwritten with the one defined in _messages.yml_.

Quest items cannot be dropped in any way other than using them. This way you can create a quest for eating cookies by giving the player a stack of cookies flagged as quest items and not continuing until there are no more cookies in his inventory/backpack. The player cannot drop the cookies, so he must eat every one of them to complete the quest.

!!! note "Creative Mode"
    Don't worry if the item-dropping filter isn't working for your items when you're in creative mode - it's not a bug. It's a feature. Creative-mode players should be able to easily put quest items in containers like TreasureChests.
