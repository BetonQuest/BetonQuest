# Installation and Configuration

## Installation

First of all you should install Citizens plugin. You can find it on it's [dev.bukkit.org](http://dev.bukkit.org/bukkit-plugins/citizens/) page. It's not required, you can use NPCs made from clay blocks, but that would be less immersive. Download the BetonQuest plugin, place `.jar` file in your _plugins_ folder and start the server. BetonQuest generated configuration files. If you want to use MySQL for data storage then open _config.yml_ and fill your database informations. If not, just leave these fields blank, the plugin will use SQLite instead. If you don't want to use autoupdater disable it in configuration before restarting the server or reloading the plugin. When you're finished you can reload the plugin (**/q reload**). Now tweak the configuration to your liking and move on to _Quick start tutorial_ chapter. You don't have to read about commands and permissions just yet, you should undestand how the plugin works first.

## Configuration


The configuration of BetonQuest is done mainly in _config.yml_ file. All options are described here. If you don't know about some aspect of BetonQuest you can skip the explanation here. It will be repeated later, in the description of a specific feature.

**Do not touch "version" option! _It may corrupt your files!_**

* First is connection data for MySQL database. Fill it to use MySQL, leave it blank (or incorrect) to use SQLite.
* Language is just translation of the plugin. Currently there are 7 available languages, English (en), Polish (pl), German (de), French (fr), Spanish (es), Chinese (cn), Dutch (nl) and Italian (it).
* `update` section controls the updater. It has the following settings:
    - `enable` option is set to true by default and it controls if the plugin should do anything update-related.
    - `download_bugfixes` controls if BetonQuest should automatically update bugfix versions (like _1.7.3 -> 1.7.4_ or _1.8.1 -> 1.8.3_).
        These versions **do not** change how the plugin works, they only fix bugs, so it's good to set it to `true`.
    - `notify_new_release` option is responsible for displaying a notification at startup about new releases (like _1.7.6 -> 1.8_).
        These can change how the plugin works by introducing new features and changing existing ones, so they won't be downloaded automatically. You can use `/q update` when you're ready.
    - If you're using a development version, there will be a third setting here, `notify_dev_build`.
        This is the same as `notify_new_release` but it checks the development builds instead. There are no specific version checking here, so if the found dev number is higher, it will appear. You download development builds on your own responsibility.
* `default_journal_slot` is a number of slot where the journal will appear after using `/journal` command.
* `max_npc_distance` is a distance you need to walk away from the NPC for the conversation to end (in case of using chat-based conversation interface).
* `default_conversation_IO` is a conversation interface. `simple` is a conversation in chat, `tellraw` is an extension to provide clickable options and `chest` is a conversation in inventory window. Other plugins can add additional IO types.
* `display_chat_after_conversation` this will prevent all chat messages from displaying during a conversation and it will show them once it's finished.
* `combat_delay` is a delay (in seconds) the player must wait before he can start the conversation after combat.
* `notify_pullback` will display a message every time the player is pulled back by the `stop` option in conversations (in case of chat-based conversations). It notifies players that they are in a conversation and the pullback is not some kind of a bug.
* `default_package` is a name of the package that should be used when you don't specify package in /q command. It's for your convenience.
* `remove_items_after_respawn` option should be turned on if you don't use "keepInventory" gamerule. It prevents other plugins from duplicating quest items after death.
    When the player dies, his quest items are removed from drops and stored in the backpack, but some plugins may try to restore all items to the player (for example WorldGuard custom flag keep-inventory). That's why BetonQuest removes the quest items that are in player's inventory after he respawns again, to be sure they were not readded. The "keepInventory" gamerule however works differently - the items are never dropped, so they cannot be added to backpack. Removing them from the inventory would destroy them forever. Sadly, Bukkit does not allow for gamerule checking, so it's up to you to decide. Once again: if you have "keepInventory" gamerule true, this setting has to be false, and vice versa.
* `quest_items_unbreakable` controlls whenever quest items can be broken by usage. It was used in the past, when `unbreakable` tag couldn't be added to items. You can now turn it off and make your quest items unbreakable by vanilla means.
* Sounds define what sounds will be played on these occasions:
    - `start` and `end` refer to starting/ending conversations
    - `journal` is just updating journal
    - `update` is played when there's a changelog file, to draw your attention
    - `full` can be played when the player uses /j command but his inventory is full.
    List of all possible sounds can be found [here](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html).
* `cmd_blacklist` is a list of commands that can't be used while in conversation. Remember that you can type here only single words (command names)!
* `hook` controls compatibility with other plugins. Here you can turn off each hook.
* `journal` controls various settings of the journal:
    - `chars_per_page` is a number of characters before a page break. If you set it too high, the text on a journal page can overflow, becoming invisible.
    - `one_entry_per_page` will make each entry take a single page. Note that it won't expand to other pages even if it overflows, so keep your entries short.
    - `reversed_order` controls the chronological order of entries in the journal. By default the entries are ordered from newest to oldest. You can reverse it, but it will force players to click through a lot of pages to get to the latest entry.
    - `hide_date` hides the date of each entry. Set it to true if you don't want this functionality.
    - `full_main_page` makes the main page take always a full page. If you display a lot of information you should probably make this true. If you use main page only for small notifications, set it to false, so the entries can follow immediately.
* `journal_colors` controls the colors used in the journal. It takes color codes without the `&` character.
    - `date.day` is a day number
    - `date.hour` is a hour number
    - `line` is a delimiter between entries
    - `text` is the text of the entry
* `conversation_colors` controls the colors of the conversation. It takes [color names](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/ChatColor.html). If you want to add a font style (bold, italic etc.) you can add it after a comma.
    - `npc` is the name of the NPC
    - `player` is the name of the player
    - `text` is the NPC's text
    - `answer` is the text of player's answer (after choosing it)
    - `number` is the option number
    - `option` is the text of an option
* `date_format` is the Java [date format](https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html) used in journal dates. It needs to have a space between the day and hour.
* `debug` is responsible for logging plugin's activity to _debug.log_ file in _logs_ directory. You shouldn't turn this on as it can slow your server down. However if you experience any errors turn this on, let the plugin gather the data and send logs to the developer. Note that first run of the plugin will be logged anyway, just as a precaution.

## Updating

The  updating process is safe and easy. After updating to a new version (manually or automatically) configuration files and database will be automatically backed up to a zip file so you never lose all your work due to an error. Then configuration will be converted to a new version. At the end the localization will be updated with new languages and the _changelog.txt_ file will be created for you.

When you enter the server BetonQuest will alert you about changes and ask you to read changelog.txt file located in plugin's main directory. This way you will be always up to date with every changes made by new versions.

All next versions of BetonQuest should have full compatibility with your current version of the plugin and server. This means that after updating the plugin should work _exactly_ the same way as it did before (except for bugs). The changes will be visible only in configuration format or new features. (For example in 1.5 inverting conditions was done by adding `--inverted` argument to the instruction. It was changed to the current format (with exclamation marks before the condition name) in 1.6 version. The plugin had updated the configuration files automaticly when switching to the new version, the only problem the user had was getting used to the new (better) way of negating conditions).

If there were any unexpected errors during an update process just download previous version, restore your configs from backup and disable autoupdating feature. Don't forget to post your error so I can fix it!

## Backups

Every time the plugin updates the configuration there is a backup created. This is especially important if you use development versions, as they may be unstable. You can also create a backup manually by running **/q backup** command. It needs to be run from console on empty server, because it heavily uses the database.

You can find your backups in _backup_ directory in plugin's folder. They are .zip files containing all your configuration and _database-backup.yml_ file, which - as the name says - is your database backup. If you want to replace your configuration with an older backup just delete all the files (except backups and logs) and replace them with the files from .zip file.

If you want your database loaded you should also place _database-backup.yml_ file in plugin's directory. When the plugin sees this file while enabling, it will backup the current database and load all data from that file to the database. You can find a backup of old database in _backups_ folder, so if you ever need to load it back, just rename it to _database-backup.yml_ and place it back in main plugin's directory. Note that _database-backup.yml_ file will be deleted after loading, so it doesn't replace your database on next plugin start.

## Migrating database from SQLite to MySQL and back

Follow these few simple steps to migrate your database easily:

1. Create a backup with **/q backup** command.
2. Extract database backup from it.
3. Turn your server off.
4. Place the _database-backup.yml_ file inside plugin's directory.
5. Edit in configuration which database type you want to use (correct credentials for MySQL, incorrect or empty for SQLite).
6. Start the server.
7. Check for errors.
8. If there are no errors, enjoy your migrated database.
9. If there are any errors, post them to the developer or try to fix them if you know how.
