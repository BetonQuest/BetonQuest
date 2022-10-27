---
icon: material/television-guide
---

# Menus
BetonQuest allows the creation of fully custom GUIs using the events and items system.  
Nearly everything can be done with these, from simple selection, over warp systems, guis listing open quests to
information dialogs that display player stats.

<span class="centered">![Menu example](../_media/content/Documentation/Menu/RPGMenuExample.png)</span>

Many new features will be added to the menu system in 2.0. You can see the ongoing discussion on what
features to add on our [GitHub discussion](https://github.com/BetonQuest/BetonQuest/discussions) page.

[:material-play-outline: Try the working example for a quick overview.](./Menu-Example.md){ .md-button }

## Creating a menu
To create a new menu just create a `menus` section in any file.
The name which can be used to identify each menu will be the name of another section as shown below.

``` YAML title="Menu Definition Example"
menus:
  myMenuName:
    title: "My Menu Title"
    #...
```

## The menu settings
This section tells you about each setting which has to be set (or can optionally be set) to configure your menu.

* `title`:   
  Fist of all you have to set the title of your menu.
  It will be displayed in the top left corner of your menu.
  You can use [color codes](https://minecraft.gamepedia.com/Formatting_codes) with `&` instead of `§` to color the
  title. Variables are supported.
  **Example:** `title: '&6&lQuests'`

* `height`:   
  This sets how many lines of slots your menu will have. Minimum 1, Maximum 6. 
  **Example:** `height: 3`

* `bind`: *([Quest Item](Reference.md#items))* ***-optional***   
  You can use this to bind the menu to an item, so that it opens if the player clicks while holding it.
  The item should be defined in the items.yml and you should specify its id here. Use `packageName.id` to use items from other packages.
  This setting is optional so you can leave it out to bind no item.  
  **Example:** `bind: menu_quests_open`

* `command`: ***-optional***  
  You can use this to create a new command which opens the menu if it is executed by a player.
  The command should only contain letters, numbers and `-`. Spaces are not allowed.
  **The server must be restarted to unregister command tab completions.**
  **Example:** `command: '/quests'`

* `open_conditions`: ***-optional***  
  You can add a list of conditions, each one separated by a `,` which all have to be true to open the menu with a bound item or a bound command. Use `packageName.id` to use conditions from other packages and `!` before the id to invert the condition.   
  **Example:** `open_conditions: 'menu_quests_open_tag,!sneaking'`

* `open_events`: ***-optional***  
  You can add a list of events, each one separated by a `,` which all are run when the menu opens. Use `packageName.id` to use events from other packages.   
  **Example:** `open_events: 'menu_quests_open_addpoints,menu_quests_open_effect_resistance_short'`

* `close_events`: ***-optional***  
  You can add a list of events, each one separated by a `,` which all are run when the menu is closed. Use `packageName.id` to use events from other packages.   
  **Example:** `close_events: 'menu_quests_reopen'`

* `items`: *(configuration section)*   
  In this section you define the items which should be displayed in the menu.
  Have a look at [the items section](#the-items-section) part of the wiki which is covering this topic.

* `slots`: *(configuration section)*   
  In this section you define which items form the `items` section should be displayed in which slot.
  Have a look at [the slots section](#the-slots-section) part of the wiki which is covering this topic.

## The `items` section
The items section contains all items which should be displayed in the menu, defined as individual sections of the config. The names of the items should only contain numbers, letters hyphens and underscores. You shouldn't name a item `yes`, `no`, `true` or `false` as those are yml keywords for booleans.  
Each item has the following settings:

* `item`: *([quest item](Reference.md#items))*  
  The id of a quest item as defined in the `items.yml`. This item will be displayed in the menu.
  Use `packageName.id` to display the item from another package.   
  **Example:** `item: menu_quests_dia`

* `amount`: *(variable number)*  ***-optional***  
  The size of the stack that will be displayed in the menu.
  You can also use [variables](Variables-List.md) here to have the amount based on  a variable. Amount is optional so you can leave it out to have a stack size of `1`.  
  **Example:** `amount: '%point.reputation.amount%'`

* `condition`/`conditions`: *(string)*  ***-optional***  
  You can define one or multiple conditions (seperated by `,`) which all have to be true so that the item is displayed in the menu. Use `packageName.id` to use conditions from other packages and `!` before the id to invert the condition.  
  **Example:** `condition: tag_completed`

* `click`: *(string)*  ***-optional***    
  You can define events (each one separated by `,`) that are run whenever the item is clicked. Use `packageName.id` to use events from other packages.  
  **Example:** `click: start_quest_wood`

If you want to have other events when you left click than when you right click create a section with the two settings:

* `left`: events that are run on left click
* `shiftLeft`: events that are run on shift + left click
* `right`: events that are run on right click
* `shiftRight`: events that are run on shift + right click
* `middleMouse`: events that are run on mousewheel click

  ``` YAML title="Example"
  click:
      left: 'give_xp,msg_give_xp'
      shiftLeft: 'give_xp,take_xp'
      right: 'take_xp,msg_take_xp'
      shiftRight: 'take_xp,msg_take_xp'
      middleMouse: 'msg_beautifull_text'
  ```    
* `close`: *(boolean)* ***-optional***   
  If the menu should close after the item was clicked. If this is not set the `default_close` value from the plugins config will be used.  
  **Example:** `close: false`

* `text`: *(list of strings)*  
   In the text section you can specify the text that is displayed if you hover over the item.
   The lore and the display name of the item specified in `items.yml` will be overwritten by this.
   If no text is specified the items default name and lore from `items.yml` will be kept.
   You can use [color codes](https://minecraft.gamepedia.com/Formatting_codes) with `&` and [variables](Variables-List.md)

   ``` YAML title="Example"
    text:
      - '&2Quest reputation: &6&l%point.quest_reputation.amount%'
   ```
   Also like the text in conversations you can provide translations for all languages:
   ```
   text:
      en:
        - '&7[Quest] &6&lThe lost amulet'
        - '&4&o'
        - '&eLeft click to locate npc'
        - '&eRight click to cancel quest'
      de:
        - '&7[Quest] &6&lDas verlorene Amulet'
        - '&4&o'
        - '&eLinksclick um den NPC zu finden'
        - '&eRechstclick um die Quest abzubrechen'
   ```

### An example for a `items` section:
``` YAML
items:
  quest1_active:
    item: menu_quests_1
    amount: 1
    text:
      - '&7[Quest] &f&lBone ripper'
      - '&f&o'
      - '&eLeft click to locate npc'
      - '&eRight click to cancel quest'
    click:
      right: menu_quests_1_cancel,menu_quests_1_cancel_msg
      left: menu_quests_1_compass,menu_quests_1_location_msg
    close: true
  reputation:
    item: menu_quests_reputation
    amount: 1
    text:
      - '&2Quest reputation: &6&l%point.quest_reputation.amount%'
    close: false
```

## The `slots` section
In the slots section you define which items from the items section should be displayed in which slot.  
On [this image](http://wiki.vg/images/b/bb/DoubleChest-slots.png) you can easily find out the number for each slot.  
You can also assign multiple items to the same slot and use conditions in [the items section](#the-items-section) specify which one should be used.
If you assign multiple items the first one for which all conditions are true will be displayed.

Another thing you can do is assigning multiple items to a row of slots. Now the slots are filled up one by one with the items from the list which have all conditions returning true.  
To make this more clear I want to show you this example:  
`0-2: 'quest1,quest2,quest3'`  
Assuming that the conditions for the items say that quest1 and quest3 should be displayed to the player but quest2 shouldn't then quest1 would be in the slot 0 and quest3 in the slot 1. Slot 2 would stay empty.

In addition you can also assign items to a rectangle of slots. Just like with the row the slots in this rectangle are filled up one by one with the items from the list which have all conditions returning true.  
Here is a example:
`14*25: 'quest1,quest2,quest3'`

![RectangleExample](../_media/content/Documentation/Menu/RectangleExample.png)

### An example for a `slots` section:

``` YAML
slots:
  8: reputation
  11-13: 'quest1_active,quest2_active,quest3_active'
```
