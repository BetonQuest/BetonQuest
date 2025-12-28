---
icon: material/television-guide
---

# Menus
BetonQuest allows the creation of fully custom GUIs using the events and items system.  
Nearly everything can be done with these, from GUIs listing open quests over simple warp systems to
information GUIs that display player stats.

<span class="centered">![Menu example](../../../_media/content/Documentation/Menu/RPGMenuExample.png)</span>

[:material-play-outline: Try the working example for a quick overview.](./Example.md){ .md-button }

## Creating a menu
To create a new menu just create a `menus` section in any file inside a [quest package](../../Scripting/Packages-&-Templates.md).
The name which can be used to identify each menu will be the name of another section as shown below.

``` YAML title="Menu Definition Example"
menus:
  myMenuName:
    title: "My Menu Title"
    slots: #...
menu_items: #...
items: #...
```

## General Menu Settings
These are general settings for customizing a menu.

#### Required Settings

| Setting Name | <div style="width:160px">Example</div> | Description                                                                                                                                                                                        |
|:------------:|:---------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|   `title`    | `title: "&6&lQuests"`                  | Will be displayed in the top left corner of your menu. You can use [color codes](https://minecraft.wiki/w/Formatting_codes) to color the title. Placeholders and defining languages are supported. |
|   `height`   | `height: 3`                            | How many lines of slots your menu will have. Minimum 1, Maximum 6.                                                                                                                                 |

#### Optional Settings

| <div style="width:130px">Setting Name</div> | Example                                     | Description                                                                                                                                           |
|:-------------------------------------------:|---------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------|
|              `open_conditions`              | `open_conditions: "unlockedMenu,!sneaking"` | One or multiple conditions (separated by a `,`) which all have to be true to open the menu with a bound item or a bound command.                      |
|                `open_events`                | `open_events: "menuOpenSound"`              | One or multiple events (separated by a `,`) which will be fired when the menu is opened.                                                              |
|               `close_events`                | `close_events: "menuCloseSound"`            | One or multiple events (separated by a `,`) which will be fired when the menu is closed.                                                              |  
|                   `bind`                    | `bind: "openMenuItem"`                      | Clicking with this [quest item](../../Features/Items.md) in hand will open the menu. You can create this item in the `items` section of your package. |
|                  `command`                  | `command: "/quests"`                        | This command can be executed to open the menu.                                                                                                        |

## The `menu_items` section
The items section contains all items which should be displayed in the menu, defined as individual sections of the config.

A basic item section looks like this:
``` YAML title="Item Section Example"
menus:
  myMenuName:
    title: "My Menu Title"
    slots: #...
menu_items: #(1)!
  skeletonQuestDone: #(2)!
    item: "questDoneItem" #(3)!
  goldQuestDone: #(4)!
    item: "questDone"
```

1. The `menu_items` section with all items that are displayed in a menu.
2. The name of the item. Used to reference the item in the `slots` section.
3. The name of any [quest item](../../Features/Items.md). This **cannot** be a vanilla item, it must be a quest item.
4. Another item just like the previous one.

### Optional Item Settings
The three basic optional settings.

| <div style="width:90px">Name</div> |          Example          | Description                                                                                                                                          |
|:----------------------------------:|:-------------------------:|------------------------------------------------------------------------------------------------------------------------------------------------------|
|              `amount`              |       `amount: 30`        | The size of the stack that will be displayed in the menu. [Placeholders](../../Scripting/Building-Blocks/Placeholders-List.md) are supported.        |
|            `conditions`            | `conditions: "questDone"` | One or multiple conditions (separated by a `,`) which all have to be true to display the item.                                                       |
|              `close`               |       `close: true`       | If set to `true` the menu will be closed after clicking the item. If this is not set the `default_close` value from the plugins config will be used. |

### The optional `text` setting
By default, the name and description of the quest item is displayed when hovering over the item.
You can overwrite this by using the `text` setting. If you only define one line, only the name will be overwritten.
Both [color codes](https://minecraft.wiki/w/Formatting_codes) and [placeholders](../../Scripting/Building-Blocks/Placeholders-List.md) are supported and carried into the next line, if not overridden.
The text can be provided as a single string with newlines, a multi-line string, or a list of strings, see examples.

=== "List"
    ``` YAML title="List Example"
    skeletonQuestDone:
      item: "questDoneItem"
      text:
        - "&2Reputation: &6&l%point.quest_reputation.amount%"
        - "Make quests to gain reputation!"
    ```

=== "String with Newlines"
    ``` YAML title="String with Newlines Example"
    skeletonQuestDone:
      item: "questDoneItem"
      text: "&2Reputation: &6&l%point.quest_reputation.amount% \nMake quests to gain reputation!"
    ```

=== "Multi-line String"
    ``` YAML title="Multi-line String Example"
    skeletonQuestDone:
      item: "questDoneItem"
      text: |-
        &2Reputation: &6&l%point.quest_reputation.amount%
        Make quests to gain reputation!
    ```

Just like the text in conversations you can provide [translations](../../Features/Conversations.md#translations) for all languages:
``` YAML title="Translation Example"
menu_items:
  skeletonQuestDone:
    item: "questDoneItem"
    text:
       en-US: #(1)!
         - "&7[Quest] &6&lThe lost amulet"
         - "&4&o"
         - "&eLeft click to locate npc"
         - "&eRight click to cancel quest"
       de-DE: #(2)!
         - "&7[Quest] &6&lDas verlorene Amulet"
         - "&4&o"
         - "&eLinksclick um den NPC zu finden"
         - "&eRechstclick um die Quest abzubrechen"
```

1. The name and description of the item in english.
2. The name and description of the item in german.

### The optional `click` setting
You can define one or multiple events (separated by `,`) that are run whenever the item is clicked.    
``` YAML title="Example"
items:
  skeletonQuestDone:
    item: "questDoneItem"
    click: "startQuest,closeMenu"
```

#### Click Types
Different types of clicks can be distinguished:

``` YAML title="Click Types Example"
items:
  skeletonQuestDone:
    item: "questDoneItem"
    click:
      left: "give_xp,msg_give_xp" #(1)!
      shiftLeft: "give_xp,take_xp" #(2)!
      right: "take_xp,msg_take_xp" #(3)! 
      shiftRight: "take_xp,msg_take_xp" #(4)!
      middleMouse: "msg_beautifull_text" #(5)!
```

1. Run when left-clicking the item.
2. Run when shift + left-clicking the item.
3. Run when right-clicking the item.
4. Run when shift + right-clicking the item.
5. Run when middle mouse clicking the item.


## The `slots` section
The slots section defines where the items from the items section should be displayed.    
You can also assign multiple items to the same slot and use conditions in the [items section](#the-menu_items-section) to
specify which one should be used.
If you assign multiple items the first one for which all conditions are true will be displayed.

```YAML
menus:
  myMenuName:
    title: "My Menu Title"
    slots:
      8: "reputation" #(1)!
      9: "questStarted,questCompleted" #(2)!
```

1. The item `reputation` will be displayed in the 8th slot.
2. The first item for which all conditions are true will be displayed in the 9th slot.
   If the conditions for `questStarted` are true, it will be displayed.
   If the condition `questCompleted` is true it will be displayed. 
   If both conditions are true the first item (`questStarted`) will be displayed.

??? info "Slot Numbers"
    Use these numbers to assign items to slots:  
    ![Inventory Slot Numbers](../../../_media/content/Documentation/Menu/DoubleChestSlots.png)


### Row Assignment
You can also assign multiple items to a row of slots. Now the slots are filled up one by one using the items whose conditions are true:

``` YAML
10-12: "quest1,quest2,quest3" #(1)!
```
  
1. Assuming that the conditions for the items determine that `quest1` and `quest3` should be displayed to the player but `quest2` shouldn't,
   then `quest1` would be in the slot 10 and `quest3` in the slot 11. Slot 12 would stay empty.

### Rectangle Assignment
Additionally, you can also assign items to a rectangle of slots. Just like with the row,
the slots in this rectangle are filled up one by one using the items whose conditions are true  

```YAML
14*25: "quest1,quest2,quest3"
```

![RectangleExample](../../../_media/content/Documentation/Menu/RectangleExample.png)
