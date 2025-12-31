---
icon: fontawesome/solid/lock
---
# Commands and permissions

## Commands
Required arguments are displayed as such: `<argument>`.  
Optional arguments are displayed as such: `[argument]`.  
Arguments referring to a package are prefixed in the format `package>` and marked with `(ID)`.

* `/journal` - Gives the journal.
* `/backpack` - Opens the backpack.
* `/compass` - Opens the quest compass menu.
* `/cancelquest` - Opens the quest canceler menu.
* `/questlang <lang>` - Changes the language for the player.
  `default` will use the language defined in the "_config.yml_" file.

* `/bq` - Lists all available admin commands.
* `/bq reload` - Reloads the plugin.
* `/bq version` - Displays the versions of BetonQuest, the server and all hooked plugins.
* `/bq update` - Updates the plugin to the newest version.
* `/bq debug [true/false/ingame/dump]` - Enable debug mode and write all down in a log file or disable the debug mode.
* `/bq backup` - Creates a backup of the configuration files and the database.
* `/bq download <gitHubNamespace> <ref> <type> <sourcePath> [targetPath] [recursive] [overwrite]` - Download quests 
  and templates from a GitHub repository.

* `/bq condition <playerName> <condition(ID)>` - Checks a condition for the player.
* `/bq event <playerName> <event(ID)>` - Fires an event for the player.
* `/bq objective <playerName> <list/add/del/complete> <objective(ID)/filter(ID)>` - Manages player's objectives.
* `/bq tag <playerName> <list/add/del> <tag(ID)/filter(ID)>` - Manges player's tags.
* `/bq globaltag <list/add/del/purge> <tag(ID)/filter(ID)>` - Manges global tags.
* `/bq point <playerName> <list/add/del> <category(ID)/filter(ID)> <amount>` - Manges player's points.
* `/bq globalpoint <list/add/del/purge> <category(ID)/filter(ID)> <amount>` - Manges global points.
* `/bq journal <playerName> <list/add/del> <pointer(ID)/filter(ID)> <date>` - Manges player's journal entries.
* `/bq item <item(ID)> <serializer>` - Creates an item under the item(ID), based on the item you are holding.
* `/bq give <item(ID)>` - Gives you the item defined under the item(ID).
* `/bq variable <playerName> <objective(ID)> <list/set/del> [key/filter] [value]` - Manage variables stored in a 
[`variable`](../Scripting/Building-Blocks/Objectives-List.md#variable-variable) objective.
* `/bq purge <playerName>` - Deletes all player's data from the database.
* `/bq rename <tag/point/globalpoint/objective/entry> <oldName(ID)> <newName(ID)>` - Renames the specified thing in 
  the database.
* `/bq delete <tag/point/objective/entry> <name(ID)>` - Deletes the specified thing from the database.
* `/rpgmenu list` - Lists all currently loaded menus and allows opening them just by clicking on them.
* `/rpgmenu open <menu(ID)> [player]` - Opens a menu for you or another player.
  [Open conditions](../Features/Menus/Menu.md#general-menu-settings) will be ignored when using this command.
* `/betonquestanswer` - Selects an answer in a conversation, can not be used manually

The filter only works on the list and will match all objectives/tags/points that start with the filter. 
Please note, that the names are a composition of the package name and the name of the objective/tag/point.

### Custom Menu Opening commands
The plugin lets you create a new command for each menu which allows all players to open the menu.  
You can also specify BetonQuest conditions so that the menu can only be opened if the player matches specific conditions.
Have a look at the [menu settings](../Features/Menus/Menu.md#general-menu-settings) for more information.

The server must be restarted to unregister command tab completions.

## Aliases

* `/journal` - `j`, `bj`, `bjournal`, `betonjournal`, `betonquestjournal`
* `/backpack` - `b`, `bb`, `bbackpack`, `betonbackpack`, `betonquestbackpack`
* `/compass` - `bc`, `bcompass`, `betoncompass`, `betonquestcompass`
* `/cancelquest` - `bcq`, `bcq`, `bcancelquest`, `betoncancelquest`, `betonquestcancelquest`
* `/questlang` - `ql`

* `/betonquest` - `bquest`, `bquests`, `betonquests`, `quest`, `quests`
    * `version` - `v`, `ver`
    * `condition` - `c`, `conditions`
    * `event` - `e`, `events`
    * `objective` - `o`, `objectives`
    * `tag` - `t`, `tags`
    * `globaltag` - `gt`, `gtags`, `gtag`, `globaltags`
    * `point` - `p`, `points`
    * `globalpoint` - `gp`, `gpoint`, `gpoints`, `globalpoints`
    * `journal` - `j`, `journals`
    * `item` - `items`
    * `give` - `g`
    * `variable` - `var`
    * `rename` - `r`
    * `delete` - `d`, `del`
* `/rpgmenu` - `qm`, `menu`, `menus`, `rpgmenus`, `rpgm`

## Permissions

* `betonquest.conversation` - allows talking with NPCs (default for players)
* `betonquest.journal` - allows using /bjcommand (default for players)
* `betonquest.backpack` - allows using /backpack command (default for players)
* `betonquest.compass` - allows using /compass command (default for players)
* `betonquest.cancelquest` - allows using /cancelquest command (default for players)
* `betonquest.language` - allows changing the language (default for players)
* `betonquest.admin` - allows using admin commands (/bq, /rpgmenu ...)

## BetonQuest administration command: `/betonquest`

### Reload the plugin: `reload`
Reloading loads all data from configuration, but not everything is updated. Player's data isn't touched to avoid lags made by database saving.
The database is also the same, you will have to reload/restart the whole server for the database to change.

### Update the plugin: `update`
Update command ('`/bq update`') will try to download the newest version of the plugin and save it to the update folder.
This folder is then handled by the server to update the plugin. If you accidentally use this command but do not wish to update the plugin,
you should remove `BetonQuest.jar` file from the `plugins/update` folder before restarting the server.

### Debug quests and BetonQuest: `debug`
The debug command ('`/bq debug`') allows you to enable or disable the debug mode. If the debug mode is enabled after
server startup ('`/bq debug true`'), all log entries from the configured log history time frame are written to the
`/plugins/BetonQuest/logs/latest.log` file as history and writing will be continued until the debug mode is disabled
using ('`/bq debug false`'). The `latest.log` is renamed to the current date and time on server startup.
It's useful if you search for more information about an issue and can help developers to fix bugs.  
With the dump command ('`/bq debug dump`') you can write the history of the debug log to the file, without actually 
enabling the debug mode. This is useful if you want to share the log with developers without enabling the debug mode.

The command ('`/bq debug ingame`') allows you to manage your ingame debugging.
The ingame debugging sends you live information about quests to your chat. Running the command without any argument
shows your active filters. If you don't have any filters active you will see all console output from `/bq reload`. If you
have filters active you only see information from the selected packages. Appending a package name activates the filter
for that package. You can also use `*` / `MyFolder-*` instead of a package name to address all packages / all packages
of a folder. Appending a level allows you to select which types of messages are displayed. The default level `error`
shows all `WARNINGS` and `ERRORS` from the log. If you want to see more information use the levels `info` or `debug`.
Beware though, the debug level might be spammy.

### Create a backup: `backup`
If you want to backup your configuration and database make sure that your server is empty 
(this process requires all data to be saved to database -> all players offline) and run '`/bq backup`' command.
You will get a zip file containing all your data, ready to be unzipped for restoring the plugin.

### Download from GitHub: `download`
The download command (`/bq download`) can be used to download tutorial quests & quest templates from
the [Quest-Tutorials](https://github.com/BetonQuest/Quest-Tutorials) repository. For
example `/bq download BetonQuest/Quest-Tutorials refs/tags/v2.0.0 QuestPackages /default` will download the `default` tutorial quest and
place it in the same folder. The first argument (`gitHubNamespace`) is the GitHub repository in the format user/repo or
organisation/repo. Before you can download from a repo you need to add the namespace to
the [`repo_whitelist`](Plugin-Config.md#downloader-the-downloader-settings) in the BetonQuest config. This is a security measure that
prevents users from screwing up all your quests or downloading malicious files if they get the permission to run this
command by accident. The second argument (`ref`) is either a commit SHA or a git reference to a specific commit that
should be downloaded. For a branch (e.g. `main`)  `refs/heads/main` works. For a tag it is `refs/tags/tagname`. Pull request references (
e.g. `refs/pull/1731/head`) are also possible but must be enabled with [`pull_request`](Plugin-Config.md#downloader-the-downloader-settings).
Keep in mind that anyone can open a pull request so use this very carefully. Third argument (`type`) is
either `QuestPackages` or `QuestTemplates` depending on what type you want to download. As 4th argument (`sourcePath`)
you define what folders to download from the repo. It is appended to the type to get the full Path in the repo.
Optionally you may add a 5th parameter:
`targetPath` is where in your BetonQuest folder the files shall be put, relative to either the QuestPackages or
QuestTemplates folder defined as `type`. If you want to place some QuestTemplates inside `QuestPackages` you can
do this by adding `../QuestTemplates/` to the beginning of the target path.  
Additionally you can add tags to the end of the command to control behavior of the downloader:
If `recursive` is added [nested packages](../Scripting/Packages-&-Templates.md#__tabbed_1_3) or templates will be downloaded while by default they
will be skipped. The tag `overwrite` defines that already existing files may be overwritten. By default, an error is
logged and the download is stopped.

### Interact with quest primitives

#### Check conditions: `condition`
You can check for conditions for a given player with for example: '`/bq condition Beton QuestPackage>has_started`'.  
If you want to check a non player related condition replace the player's name with `-`.

#### Run events: `event`
You can run events for a given player with for example: '`/bq event Beton QuestPackage>give_emeralds`'.
If you want to run a non player related event replace the player's name with `-`.

#### Manage objectives: `objective`
You can manage objectives for a given player with for example: '`/bq objective Beton list`'.
This would list all active objectives for player Beton.
You can also add or remove objectives with '`/bq objective Beton add QuestPackage>find_sword`' or
'`/bq objective Beton del QuestPackage>find_sword`'.
It is also possible to complete an objective with '`/bq objective Beton complete QuestPackage>find_sword`',
what would run all the events from the objective.

#### Manage tags: `tag`
You can manage tags for a given player with for example: '`/bq tag Beton list`'.
This would list all tags for player Beton.
You can also add or remove tags with '`/bq tag Beton add QuestPackage>test`' or '`/bq tag Beton del QuestPackage>test`'.

#### Manage points: `point`
You can manage points for a given player with for example: '`/bq point Beton list`'.
This would list all point categories for player Beton.
You can also add or remove points with '`/bq point Beton add QuestPackage>reputation 20`' or
'`/bq point Beton add QuestPackage>reputation -20`'.
To delete a point category completely use '`/bq point Beton del QuestPackage>reputation`'.

#### Manage the journal: `journal`
Journal subcommand works in the same way as those two above.
Adding and removing looks like `/bq journal Beton add QuestPackage>wood_started` (or `del`), and you can also specify the date
of entry when adding it, by appending date written like this: `23.04.2014_16:52` at the end of the command.
Note that there is `_` character instead of space!

#### Manage items: `item` and `give`
If you need to create for example "Nettlebane" quest item, just hold it in your hand and type '`/bq item QuestPackage>nettlebane <serializer>`'.
It will copy the item you're holding into the "_items.yml_" file and save it there with the name you specified (in this case "nettlebane").
The _serializer_ defines the format in which the item is saved (and loaded).

The '`/bq give QuestPackage>nettlebane`' command will simply give you specified item.

#### Manage variables: `variable`
This command relates to active [variable objectives](../Scripting/Building-Blocks/Objectives-List.md#variable-variable).
You can list all `key-value` pairs with an optional objective filter.
Further can such a value be set for a key with `set <key> <value>` or an existing pair removed by using `del <key>`.

### Purge player data: `purge`
You can purge specific player with the '`/bq purge Beton`' command, where Beton is the name of the player.
To purge the entire database at once simply change the prefix in "_config.yml_" or delete "_database.db_" file.

### Rename a quest-primitive without loosing data: `rename`
Rename command ('`/bq rename`') allows you to rename every tag, point, globalpoint, objective or journal entry in the database.
In case of an objective it will also rename the objective in _objectives_ section in the configuration file, so it continues to work correctly.

### Delete data from the database: `delete`
Delete command ('`/bq delete`') allows you to delete from the database every tag, point, objective or journal entry with specified name.
