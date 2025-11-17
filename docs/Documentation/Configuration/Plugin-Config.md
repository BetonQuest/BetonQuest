---
icon: fontawesome/solid/wrench
---

The configuration of BetonQuest is mainly done in the "_config.yml_" file. All of its options are described on this page.
There is also additional information about backups, updates, and database transfers. 

## `language` - Default plugin language
The language option sets the default language of BetonQuest.  
It is used for all players that do not have a specific language set or as fallback language just in case.  
You can see all available languages in the `lang` folder, and you can add your own language files there.
The language consists of a 2 to 3 character language key (a-z in lowercase), followed by a dash and at least
2 characters from a-z, A-Z, numbers and dashes. 

## `text_parser` - Default text parser
Set the default parser used to format all text in betonquest.
For more information, see the [Text Formatting](../Features/Text-Formatting.md) page.

## `date_format` - The format of dates
The date format is used in the journal and for other date-related features.
If you want to change the date format,
you can use the [SimpleDateFormat](https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html) syntax.
It needs to have a space between the day and hour.

## `default_notify_io` - The default io for notifications
When notifications are set over the notify command,
you can set the fallback IO that will be used if no specific IO is set.
The default is already `chat`, but you can also choose another one from the ones
listed on the [IO's & Categories](../Visual-Effects/Notifications/Notification-IO%27s-%26-Categories.md) page.

## `updater` - The updater settings
The updater section controls the plugin's Auto-Updater.

* `enabled` - If set to `false`, it is not possible to update with the updater and no version checks are executed.
* `strategy` - The update strategy is the most important feature of the Auto-Updater.
  An explanation is available [here](./Version-Changes/Updating.md#choose-an-update-strategy).
* `automatic` - If true the updater will download new Versions automatically.
  Otherwise, the updater will only download new versions when the update command is executed.
  Advice is available [here](./Version-Changes/Updating.md#enable-or-disable-automatic-updates).
* `ingame_notification` - If true players with `betonquest.admin` permission will be notified on join
  when a new version is available. They will only be notified once a day without a server restart.

## `downloader` - The downloader settings
The command [`/q download`](./Commands-and-Permissions.md) can be used to download quest files from GitHub repositories.
This is mainly used by betonquest to download tutorial files.

* `pull_request` - Define if pull requests are allowed to be downloaded.  
  **Only enable this if you really know what you are doing!**
  Everyone can open pull requests that could contain malicious files and if a permission misconfiguration occurs this
  will make your server vulnerable to attacks.
* `repo_whitelist` - A list of trusted GitHub repositories from which quests and templates can be downloaded.  
  By default, only our official tutorial repo [BetonQuest/Quest-Tutorials](https://github.com/BetonQuest/Quest-Tutorials)
  is on this list.

## `debug` - Debugging settings
Debugging writes a debug log file to the `logs` directory of BetonQuest.
The debug log contains a lot of additional information about the plugin's activity.

* `enabled` - If enabled the debug log is written to the `logs/latest.log` file. 
  Turning this on can have an impact on performance, so it is recommended to only enable it temporarily.
* `history` - How many minutes the plugin caches debugging activity to the memory. 
  Once debugging is enabled, this history is printed to the log file, and no longer stored in memory.
  This is invaluable for debugging issues that happened in the past without having debug enabled all the time.
  The downside is that depending on the number of scripts and players, this can be a lot of data occupying your memory.
  So if you have memory issues, you can reduce the time the history is stored in memory down to 0 minutes to disable it.

## `mysql` - Setup MySQL Database
You don't need to configure a mysql database, but it brings some advantages.  
It mainly brings limited support for cross-server support.
In general, MySQL has a lot of advantages over SQLite, such as better performance.  
To use a MySQL Database for saving all the data, you need to fill out the mysql config section.

**This option needs a server restart to take effect when changed!**

``` YAML linenums="1"
mysql:
  enabled: true              #(1)!
  host: ''                   #(2)!
  port: ''                   #(3)!
  user: ''                   #(4)!
  pass: ''                   #(5)!
  base: ''                   #(6)!
  prefix: betonquest_        #(7)!
  reconnect_interval: 1000   #(8)!
```

1. Set this to true.
2. This is the IP of your MySQL server. If it runs on the same machine as your server, use localhost or 127.0.0.1
3. This is the port your MySQL server runs on.
4. The name of the database user that is used to connect to the database server.
5. The password of that user.
6. The database that BetonQuest will write to. You need to create it in your database server.
7. The table prefix of BetonQuest's data in the database.
8. The time interval the database tries to reconnect if the connection gets lost

### Migrating a database from SQLite to MySQL or back
Follow these few steps to migrate your database easily:

1. Create a backup with **/q backup** command.
2. Turn the server off.
3. Navigate to `BetonQuest/Backups` and extract the file `database-backup.yml` from it.
4. Place the `database-backup.yml` file inside the plugin's directory.
5. Edit which database type you want to use by setting the `enabled` option in the `mysql` section to true or false.
6. Start the server and check for errors.
7. If there are any errors, post them to the developer or try to fix them if you know how.

## `profile` - Profile settings
Profiles allow players to have different progress/data active.
Currently, there is no way to switch between profiles, as the feature is still in development.

* `initial_name` - The name of the profile that is created when a player joins for the first time.

## `conversation` - Conversation settings
All conversation related settings.

* `default_io` - A comma-separated list of conversation styles. The first one that is loaded (depending on the 
  available third party plugin integrations) is used. 
  See [conversation styles](../Features/Conversations.md#conversation-displaying) for supported styles.
* `interceptor`  
  Catches chat during a conversation to prevent distraction from the conversation.
    * `default` - A comma-separated list of chat interceptors. The first one that is loaded (depending on the 
      available third party plugin integrations) is used.
      See [chat interceptors](../Features/Conversations.md#chat-interceptors) for supported chat interceptors.
    * `display_history` - If set to `true`, the interceptor will display all previous messages in the chat after
      the conversation ends, like there was no conversation. This is only supported by some interceptors.
      **This option needs a server restart to take effect when changed!**
* `damage`  
  All damage related settings.
    * `combat_delay` - A delay (in seconds) the player must wait before starting a conversation after the last combat.  
      **This option needs a server restart to take effect when changed!**
    * `invincible` - If set to `false` players can get damage from entities when in a conversation.
* `stop`  
  Controls the behavior of the `stop` option in conversations.
    * `distance` - The distance you need to walk away from the NPC for the conversation to end (in the case of using
      chat-based conversation interface).
    * `notify` - Whether to display a message that the player is still in a conversation every time the player is 
      pulled back in the radius of the distance by the `stop` option in conversations.
* `cmd_blacklist` - A list of commands that cannot be used during a conversation.
  Remember that you can only type single words (command names) here!
* `color`  
  The colors use in conversation in the format of [color names](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/ChatColor.html).
  If you want to add a font style (bold, italic, etc.) do so after placing a comma.
    * `npc` - The name of the NPC
    * `player` - The name of the player
    * `text` - The NPC's text
    * `answer` - The text of player's answer (after choosing it)
    * `number` - The option number
    * `option` - The text of an option

### `io` - Conversation IO settings
Every io has its own settings that can be configured in the `io` section.

* `menu`  
  Settings for the menu conversation IO.
    ``` YAML linenums="1"
    menu:
      line_length: 320      #(1)!
      line_count: 10        #(2)!
      line_fill_before: 10  #(3)!
      refresh_delay: 180    #(4)!
      rate_limit: 10        #(5)!
  
      npc_name_type: chat              #(6)!
      npc_name_align: center           #(7)!
      npc_name_separator: true         #(8)!
      options_separator: true          #(9)!
      control_select: jump,left_click  #(10)!
      control_move: scroll,move        #(11)!
      control_cancel: sneak            #(12)!
    
      npc_name: '@[minimessage]<yellow>{npc_name}'                        #(13)!
      npc_text: '@[minimessage] <white>{npc_text}'                        #(14)!
      npc_text_wrap: '@[minimessage] '                                    #(15)!
      option_text: '@[minimessage]    <dark_gray>[ <aqua>{option_text}
        </aqua> ]'                                                        #(16)!
      option_text_wrap: '@[minimessage]    '                              #(17)!
      option_selected_text: '@[minimessage] <gray>» <dark_gray>[ <white>
        <underlined>{option_text}</underlined></white> ]'                 #(18)!
      option_selected_text_wrap: '@[minimessage]    <white><underlined>'  #(19)!
      scroll_up: '@[minimessage]<white>        ↑</white>'                 #(20)!
      scroll_down: '@[minimessage]<white>        ↓</white>'               #(21)!
    ```
    
    1. Maximum length of a line till its wrapped in pixels.
    2. Height of a conversation in lines.
    3. Amount of empty lines before a conversation starts.
    4. Time interval before printing the conversation again in ticks.
    5. Time to wait until a new option can be selected in ticks.
    6. Place to show the NPC name, `chat` or `none`.
    7. For `npc_name_type` `chat`, the alignment of the name, `left`, `right` or `center`.
    8. Separate the NPC name with an empty line from the text.
    9. Separate the NPC text from the player options by filling remaining space with empty lines.
    10. Comma separated actions to select an option, `jump`, `left_click` or `sneak`.
    11. Comma separated actions to move the selection, `move` or `scroll`.
    12. Comma separated actions to cancel the conversation, `jump`, `left_click` or `sneak`.
    13. The format of the NPC name. Placeholder `{npc_name}`
    14. The format of the NPC text. Placeholder `{npc_text}`
    15. A prefix that gets applied to the start of a new line if the actual text is too long.
    16. The format of the player options. Placeholder `{option_text}`
    17. A prefix that gets applied to the start of a new line if the actual text is too long.
    18. The format of the selected player option. Placeholder `{option_text}`
    19. A prefix that gets applied to the start of a new line if the actual text is too long.
    20. The arrow format to scroll up.
    21. The arrow format to scroll down.
  

* `chest`  
  Settings for the chest conversation IO.
    * `show_number` - If set to `true`, the player number will be shown in the chest conversation IO.
    * `show_npc_text` - If set to `true`, the NPC text will be shown in every player option.
* `slowtellraw`
  Settings for the slowtellraw conversation IO.
    * `message_delay` - The delay (in ticks) between each message.

## `npc` - NPC settings
All settings related to NPCs.

* `accept_left_click` - If set to `true`, a conversation with an NPC can be started by left-clicking the NPC.
* `interaction_limit` - The time (in milliseconds) a player must wait before starting a conversation with an NPC again.
  This is to prevent click spamming.

## `hologram` - Hologram settings
All settings related to holograms.

* `default` - The default hologram plugin that is used by BetonQuest.
  If the plugin is not installed, BetonQuest will use the next one in the list.
* `update_interval`- The interval (in ticks) in which the holograms check the conditions and updates their visibility.
  This is to prevent performance issues and cannot be disabled.
  Set a very high value to make it semi-disabled.

## `hider` - Entity hider settings
All settings related to the entity hider.

* `player_update_interval` - The interval (in ticks) in which the PlayerHider checks the conditions and updates their visibility.
  This is to prevent performance issues and cannot be disabled.
  Set a very high value to make it semi-disabled.
* `npc_update_interval` - The interval (in ticks) in which the NPCHider checks the conditions and updates their visibility.
  This is to prevent performance issues and cannot be disabled.
  Set a very high value to make it semi-disabled.

## `item` - Item related settings
Different item settings that are used in BetonQuest.

* `quest`  
  Controls the quest item settings.
    * `lore` - If set to `true`, the `quest-item` option will also add the "Quest Item" lore.  
      This will add the `quest_item` line in the player's language at the end of lore in a new line.
    * `unbreakable` - If set to `true`, quest items will be unbreakable.  
      This was used in the past when the `unbreakable` tag couldn't be added to items.
      Turn it off and make your quest items unbreakable by vanilla means.
    * `remove_after_respawn` - If set to `true`, quest items will be removed from the player's inventory after they respawn.  
      This option should be turned on if "keepInventory" gamerule is not being used. 
      It prevents other plugins from duplicating quest items after death.  
      When a player dies, their quest items are removed from drops and stored in the backpack, but some plugins may try to
      restore all items to the player (for example, WorldGuard custom flag keep-inventory).
      That is why BetonQuest removes the quest items that are in a player's inventory after they respawn again, to be sure they were not re-added. 
      The "keepInventory" gamerule, however, works differently—the items are never dropped, so they cannot be added to the backpack. 
      Removing them from the inventory would destroy them forever. Sadly, Bukkit does not allow for gamerule 
      checking, so it is up to you to decide.  
      Once again, if you have "keepInventory" gamerule true, this setting has to be false and vice versa.
    * `update_legacy_on_join` - If set to `true`, Quest Items will be updated on join.  
      If the inventory should be checked for Quest Items identified by Lore should get the new identifier tag set.
      Only useful when there was BetonQuest used before.
      When there are no such items or a check is not wanted just disable it.
* `backpack`  
  Configuration of items, that are shown in the backpack. You reference them with a full path to an item in a package.
  For example `my_package>my_button`.
    * `previous_button` - The item that is used to go to the previous backpack page, let it empty to use the default one.
    * `next_button` - The item that is used to go to the next backpack page, let it empty to use the default one.
    * `close_button` - The item that is used to close the backpack, let it empty to disable the close button.
    * `compass_button` - The item that is used to open the compass menu, let it empty to disable the compass button.

## `journal` - Journal settings
The journal is a special item used to store quest entries and other information.

* `default_slot` - The inventory slot in which the journal will appear after using the `/journal` command.
  BetonQuest will try to move items out of the way if the slot is occupied. If the inventory is full, the journal will not
  be added. You can disable this behavior by setting the option to `-1`. BetonQuest will then just use any free slot.
* `lock_default_slot` - If set to `true`, it is not possible to move the journal from the `default_slot`.
* `show_in_backpack` - If set to `true`, the journal will be displayed in the backpack when there is no journal in the player's inventory.
* `give_on_respawn` - If set to `true`, the journal will be added to the player's inventory after they respawn.
* `custom_model_data` - The custom model data of the journal item. This is used to change the appearance of the journal item.
* `format`  
  The format setting of the journal.
    * `chars_per_line` - The number of characters before a line break.
      If it is set too high, the text on a journal page can overflow and become invisible.
    * `lines_per_page` - The number of lines before a new page.
      If it is set too high, the text on a journal page can overflow and become invisible.
    * `one_entry_per_page` - If set to `true`, each journal entry will take a single page.
      Note that it will not expand to other pages even if it overflows, so keep your entries short.
    * `reversed_order` - If set to `true`, the journal entries will be ordered from oldest to newest.
      This is reversible, but it will force players to click through many pages to get to the most recent entry.
    * `full_main_page` - If set to `true`, the main page of the journal will always take a full page.
      If a lot of information is being displayed, it is advised to make this true.
      If you use the main page only for small notifications, set it to false, so the entries can follow immediately.
    * `separator` - The separator between journal entries if multiple entries are on one page.
      It is used to visually separate entries in the journal.
    * `hide_date` - If set to `true`, the date of each entry will be hidden.
      Set it to true if you don't want this functionality.
    * `color`  
      The colors used in the journal. It takes color codes without the `&` character.
        * `date`  
          The date color settings.
            * `day` - The color of the day number.
            * `hour` - The color of the hour number.
        * `line` - The color of the line delimiter between entries.
        * `text` - The color of the entry text.

## `menu` - Menu settings
BetonQuest menu settings.

* `default_close` - Sets if menus should close by default when an item is clicked (`true`) or if they should stay 
  open (`false`). Each menu can also override this.

## `hook` - Compatibility Hooks
Controls compatibility with other plugins.
This is a list of all plugins that BetonQuest does support.
By default, all of them are set to `true`, but if you face any issues, you can disable them.
