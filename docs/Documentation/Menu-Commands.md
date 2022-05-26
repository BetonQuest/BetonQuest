---
icon: material/run
---
# Commands
On this page you find all commands for the plugin.

## Main plugin command: `/rpgmenu`
**Aliases:** `/rpgmenus`, `/menu`, `/menus`, `/rpgm`, `/qm`

**Permission:** `betonquest.admin`

**Description:**  
Provides various utility commands for menus.

**Subcommands:**

* `/rpgmenu reload [menu]`:  
  Allows reloading all configuration files or just reloading the configuration of one specific menu.

* `/rpgmenu list`:  
  Lists all currently loaded menus and allows opening them just by clicking on them.

*  `/rpgmenu open <menu> [player]`:  
   Opens a menu for you or another player. [Opening conditions](Menu-Menu.md#the-menu-settings) of the menu will be ignored when using this command.

## Bound commands: *(customizable)*
The plugin lets you create a new command for each menu which allows all players to open the menu.  
You can also specify BetonQuest conditions so that the menu can only be opened if the player matches specific conditions (like has tags, permissions or points).
Have a look at the [menu settings](Menu-Menu.md#the-menu-settings) for more information.
