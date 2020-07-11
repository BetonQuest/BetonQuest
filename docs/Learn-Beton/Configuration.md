# Configuration

The configuration of BetonQuest is done mainly in _config.yml_ file. All of its options are described on this page.
There is also additional information about backups, updates and how to revert changes that you made to the config.
If you don't understand some options yet just keep their default values. You can always change them when
you have gained a more complete understanding of this plugin. 


## Config Options
!!! warning
    Do not touch "version" option! It may corrupt your files!


### MySQL Database

In order to use a MySQL Database for saving all the data you need to fill out the mysql config section.
``` YAML linenums="1"
mysql:
  enabled: true   #Set this to true.
  host: ''        #This is the IP of your MySQL server. If it runs on the same machine as your server use localhost or 127.0.0.1 
  port: ''        #This is the port your MySQL server runs on.
  user: ''        #The name of the database user that BetonQuest will use.
  pass: ''        #The password of that user.
  base: ''        #The database that BetonQuest will write to.
  prefix: betonquest_  #The table prefix of BetonQuests data in the database.
```

### Migrating database from SQLite to MySQL and back

Follow these few simple steps to migrate your database easily:

1. Create a backup with **/q backup** command.
2. Extract database backup from it.
3. Turn the server off.
4. Place the _database-backup.yml_ file inside the plugin's directory.
5. Edit which database type you want to use by setting the `enabled` option in the ´mysql´ section to true or false.
6. Start the server.
7. Check for errors.
8. If there are no errors, enjoy your migrated database.
9. If there are any errors, post them to the developer or try to fix them if you know how.

### Default Language
 
Language is just the currently used translation of the plugin. Currently there are 7 languages available: 
English (en), Polish (pl), German (de), French (fr), Spanish (es), Chinese (cn), Dutch (nl) and Italian (it).

### Updating
The `update` section controls the updater. It has the following settings:
    - `enabled` (default `true`). Enables or disables the Updater. If set to false, it is not possible to update with the updater and no version checks are executed.
    - `strategy` (default `MINOR`). The update strategy is more difficult to understand.
    Each plugin version is a number, that consists of three parts. For example `2.4.3`, the  first number (`2` in this example)
    is named `MAYOR`, the second `MINOR` (`4` in this example) and the third `PATCH` (`3` in this example).
    When we release a new version of BetonQuest we will change these numbers in a specific way. Each number has a fixed meaning. 
    The table below shows you what's included in the update when we increase any of the three digits:    
  
        
        | Update Strategy  | `MAYOR`                | `MINOR`                | `PATCH`                |
        |------------------|------------------------|------------------------|------------------------|
        | Bug Fixes        | :white_check_mark:     | :white_check_mark:     | :white_check_mark:     | 
        | New Features     | :white_check_mark:     | :white_check_mark:     | :x:                    | 
        | Breaking Changes | :white_check_mark:     | :x:                    | :x:                    | 
        
        You can also append `_DEV` to each strategy. This will download the dev builds for the corresponding version. This
         is not recommended for production/live servers, as devbuilds can contain bugs. 
    
      
    - `automatic` (default `true`). If true the updater will download new Versions automatically. Otherwise, 
    the updater will only download new versions when the update command is executed.   
    
### Journal slots
`default_journal_slot` is a number of slots where the journal will appear after using `/journal` command.
### Citizens identifier
* `citizens_npcs_by_name` sets whether NPCs from citizens2 should be identified in main.yml by their name instead of their id.
### Conversation End Distance
* `max_npc_distance` is the distance you need to walk away from the NPC for the conversation to end (in the case of using chat-based conversation interface).
### Default conversation style
* `default_conversation_IO` is a comma-separated list of conversation interfaces with the first valid one used. `simple` is a conversation in chat. `tellraw` is an extension to provide clickable options, and `chest` is a conversation in inventory window. If you want to use chest and also write the conversation to the players chat, use `combined`. Others, like `menu` are available if you have the required plugins and other plugins can add additional IO types.
### Default Chat interceptor
* `default_interceptor` is a comma-separated list of chat interceptors with the first valid one used. `simple` attempts to catch chat events. `packet` uses protocollib to intercept packets before they reach the player. 
### Conversation Chat Display options
* `display_chat_after_conversation` this will prevent all chat messages from displaying during a conversation and it will show them once it's finished.
### Combat Delay
* `combat_delay` is a delay (in seconds) the player must wait before starting a conversation after combat.
### Conversation pullback message
* `notify_pullback` will display a message every time the player is pulled back by the `stop` option in conversations (in the case of chat-based conversations). It notifies players that they are in a conversation, and the pullback is not a bug.
### Default Package name
* `default_package` is a name of the package that should be used when a package is not specified in /q command. This is for your convenience.
### Keep 
* `remove_items_after_respawn` option should be turned on if "keepInventory" gamerule is not being used. 
    It prevents other plugins from duplicating quest items after death.
    When a player dies, their quest items are removed from drops and stored in the backpack, but some plugins may try to restore all items to the player (for example WorldGuard custom flag keep-inventory). That is why BetonQuest removes the quest items that are in a player's inventory after they respawn again, to be sure they were not readded. The "keepInventory" gamerule, however, works differently - the items are never dropped, so they cannot be added to backpack. Removing them from the inventory would destroy them forever. Sadly, Bukkit does not allow for gamerule checking, so it is up to you to decide. Once again, if you have "keepInventory" gamerule true, this setting has to be false and vice versa.
### Quest Items break behaviour
* `quest_items_unbreakable` controls whether quest items can be broken by usage. This was used in the past, when `unbreakable` tag couldn't be added to items. Turn it off and make your quest items unbreakable by vanilla means.
### Sounds
* Sounds define what sounds will be played on these occasions:
    - `start` and `end` refer to starting and ending conversations
    - `journal` is updating journal
    - `update` is played when there is a changelog file, used to draw your attention
    - `full` is played when the player uses /j command but his inventory is full.
    List of all possible sounds can be found [here](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Sound.html).
### Conversation Command Banlist
* `cmd_blacklist` is a list of commands that can not be used while in conversation. Remember that you can only type single words (command names) here!
### Compatibility Hooks
* `hook` controls compatibility with other plugins. You can turn off each hook here.
### Journal
* `journal` controls various settings of the journal:
    - `chars_per_page` is the number of characters before a page break. If it is set too high, the text on a journal page can overflow and become invisible. **This was replaced by `chars_per_line` and `lines_per_page` and is only required if you don't like the new behaviour.**
    - `chars_per_line` is the number of characters before a line break. If it is set too high, the text on a journal page can overflow and become invisible. If this is not set, BQ will fall back on the old page wrapping behaviour configured through `chars_per_page`.
    - `lines_per_page` is the number of lines before a new page. If it is set too high, the text on a journal page can overflow and become invisible. This is only required if `chars_per_line` is set.
    - `one_entry_per_page` makes each entry take a single page. Note that it will not expand to other pages even if it overflows, so keep your entries short.
    - `reversed_order` controls the chronological order of entries in the journal. By default, the entries are ordered from newest to oldest. It is reversible, but this will force players to click through many pages to get to the most recent entry.
    - `hide_date` hides the date of each entry. Set it to true if you don't want this functionality.
    - `full_main_page` makes the main page always take a full page. If a lot of information is being displayed, it is advised to make this true. If you use the main page only for small notifications, set it to false, so the entries can follow immediately.
    - `show_separator` shows a separator between journal entries (default: true). Customize the separator in `messages.yml` with the key `journal_separator`.
### Journal Colors
* `journal_colors` controls the colors used in the journal. It takes color codes without the `&` character.
    - `date.day` is the day number
    - `date.hour` is the hour number
    - `line` is the delimiter between entries
    - `text` is the text of the entry
### Conversation Colors
* `conversation_colors` controls the colors of the conversation. It takes [color names](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/ChatColor.html). If you want to add a font style (bold, italic etc.) you can add it after a comma.
    - `npc` is the name of the NPC
    - `player` is the name of the player
    - `text` is the NPC's text
    - `answer` is the text of player's answer (after choosing it)
    - `number` is the option number
    - `option` is the text of an option
* `date_format` is the Java [date format](https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html) used in journal dates. It needs to have a space between the day and hour.
* `debug` is responsible for logging the plugin's activity to _debug.log_ file in _logs_ directory. Turning this on can slow your server down. However, if you experience any errors, turn this on and let the plugin gather the data and send logs to the developer. Note that the first run of the plugin will be logged anyway, just as a precaution.
* `conversation_IO_config` manages settings for individual conoversation IO's:
    - `chest` manages settings for the chest conversation IO
        - `show_number` will show the player number option if true (default: true)
        - `show_npc_text` will show the npc text in every player option if true (default: true)

## Updating

The update process is safe and easy. After updating to a new version (manually or automatically),
configuration files and database will be automatically backed up to a zip file to prevent losing your work due to errors.
Then, configuration will be converted to a new version. At the end, the localization will be updated with new languages and the _changelog.txt_ file will be created.

**Disclaimer for all versions below 2.0**: All comments in your configurations will be lost during the update process.

When you enter the server, BetonQuest will alert you about changes and ask you to read changelog.txt file located in plugin's main directory.
This helps players be aware of every change made by new versions. To learn more about how to recieve updates please read the `update` section in the
[Configuration Section](#configuration) on this page. 

All future versions of BetonQuest should have full compatibility with the current version of the plugin and server. 
This means that the plugin should work _exactly_ the same way as it did before without bugs after updating. 
The changes will be visible only in configuration format or new features.
(For example in 1.5 inverting conditions was done by adding `--inverted` argument to the instruction.
It was changed to the current format (with exclamation marks before the condition name) in 1.6 version. 
The plugin had updated the configuration files automatically when switching to the new version.
The only problem the user had was getting used to the new (better) way of negating conditions).

If there were any unexpected errors during an update process, download the previous version,
restore your configs from backup, and disable autoupdating feature. Don't forget to post your error so I can fix it!

## Backups

Every time the plugin updates the configuration, a backup will be created. 
This is especially important if a development version is being used because they may be unstable. 
A backup can also be created manually by running **/q backup** command. 
It needs to be run from the console on an empty server because it heavily uses the database.

You can find your backups in _backup_ directory in the plugin's folder.
They are .zip files containing all your configuration and _database-backup.yml_ file, which - as the name says - is your database backup.
To replace your configuration with an older backup, delete all the files (except backups and logs) and replace them with the files from .zip file.

If you want your database loaded, place _database-backup.yml_ file in plugin's directory.
When the plugin sees this file while enabling, it will backup the current database and load all data from that file to the database. 
A backup of the old database can be found in _backups_ folder, so if you ever need to load it back,
just rename it to _database-backup.yml_ and place it back in main plugin's directory. Note that _database-backup.yml_ file will be deleted after loading,
so it does not replace your database on next plugin start.
