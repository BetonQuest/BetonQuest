---
icon: material/table-of-contents
---
## Events
### Menu Event: `menu`
This event can be used to open, close or update menus.
The first argument is the type of action that should be done.
It is either `open` to open a new menu, `close` to close the currently opened menu, or `update` to update the 
content of the currently opened menu.  
If you want to open a menu you have to add a second argument which should be the id of a menu.
If you want to open menus from other packages just use the cross package format.

**Example:** `menu open quest_gui`

**Example:** `menu close`

**Example:** `menu update`

## Conditions
### Menu Condition: `menu`
This condition can be used to check if the player has currently opened any menu.
You can add `id:` optional and specify the id of a menu to check if the player has opened the menu with this id.
If you want to check for menus from other packages just use the cross package format.

**Example:** `menu id:quest_gui`

## Objectives
### Menu Objective: `menu`
This objective is completed when the player opens the menu with the given id.
The only required argument is the id of the menu.
If you want to use menus from other packages just use the cross package format.

The objective also has the property `menu` which can be used by the
[objective properties](../../Scripting/Building-Blocks/Placeholders-List.md#objective-property-placeholder).
It returns the title of the menu which should be opened.

**Example:** `menu quest_gui`

## Placeholders
### Menu Placeholder: `menu`
This placeholder displays the title of the menu that is currently opened by the player.
If no menu is opened it will be just empty.

**Example:** `%menu%`
