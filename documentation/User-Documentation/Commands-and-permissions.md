# Commands and permissions

## Commands

* **/j** - gives the journal
* **/backpack** - opens the backpack
* **/q** - lists all available admin commands
* **/q reload** - reloads the plugin
* **/q objectives {playerName} [list/add/del/complete] [instruction]** - shows player's currently active objectives
* **/q tags {playerName} [list/add/del] [tag]** - lists all player's tags
* **/q points {playerName} [list/add/del] [category] [amount]** - lists all player's points in all categories
* **/q journal {playerName} [list/add/del] [package.pointer] [date]** - lists 
* **/q event {playerName} {package.eventID}** - fires an event for the player
* **/q condition {playerName} {package.conditionID}** - shows if the player meet specified condition or not
* **/q item {package.itemID}** - creates an item based on what you're holding in hand
* **/q give {package.itemID}** - gives you an item defined in the configuration
* **/q config {set/add/read} {path} [value]** - sets, adds or reads values from configuration
* **/q purge {playerName}** - deletes all player's data from the database
* **/q rename {tag/point/globalpoint/objective/entry} {oldName} {newName}** - renames all specified things in the database
* **/q delete {tag/point/objective/entry} {name}** - deletes all specified things in the database
* **/q backup** - creates a backup of configuration files and database
* **/q update** - updates the plugin to the newest version.
* **/q create {package}**: creates new package with given name, filled with default quest
* **/q vector {packname.variable} {newvariable}**: calculates the vector from first location variable to you position and saves it as second variable
* **/q version**: displays the versions of BetonQuest, the server and all hooked plugins
* **/q debug [true/false/ingame]**: enable debug mode and write all down in a log file or disable the debug mode
* **/questlang {lang}** - changes the language for the player (and globally if used from console). `default` language will use the language defined in _config.yml_.

## Aliases

* **/j**: bj, journal, bjournal, betonjournal, betonquestjournal
* **/backpack**: b, bb, bbackpack, betonbackpack, betonquestbackpack
* **/q**: bq, bquest, bquests, betonquest, betonquests, quest, quests
    * **objective**: o, objectives
    * **tag**: t, tags
    * **point**: p, points
    * **event**: e, events
    * **condition**: c, conditions
    * **journal**: j, journals
    * **item**: i, items
    * **give**: g
    * **rename**: r
    * **delete**: d, del
    * **create**: package
* **/questlang**: ql

## Permissions


* **betonquest.admin** - allows using admin commands (/q ...) and creating an NPC from blocks
* **betonquest.journal** - allows using /j command (default for players)
* **betonquest.backpack** - allows using /backpack command (default for players)
* **betonquest.conversation** - allows talking with NPCs (default for players)
* **betonquest.language** - allows changing the language (default for players)

!!! warning
    Don't give **betonquest.admin** permission to people you don't fully trust. They can use **/q config** command to add a `command` event, and this way execute any command as the console. This might be dangerous.

## Main command details

Reloading loads all data from configuration, but not everything is updated. Player's data isn't touched to avoid lags made by database saving. The database is also the same, you will have to reload/restart the whole server for the database to change.

Tags subcommand allows you to easily list and modify tags. '`/q tags Beton`' would list tags for player Beton. '`/q tags Beton add test`' would add "test" tag for that player, and '`/q tags Beton del test`' would remove it.

Points subcommand is similar - listing points is done the same way. Adding points to a category looks like that: '`/q points Beton add reputation 20`' (adding 20 points to "reputation" category). You can also subtract points with negative amounts. Removing the whole point category can be achieved by '`/q points Beton del reputation`'.

Journal subcommand works in the same way as those two above. Adding and removing looks like `/q journal Beton add default.wood_started` (or `del`), and you can also specify the date of entry when adding it, by appending date written like this: `23.04.2014_16:52` at the end of the command. Note that there is `_` character instead of space!

Objective subcommand allows you to list all active objectives (shown as their labels) of the player. It can also directly add or cancel objectives using instruction strings. You can also complete the objective for the player using `complete` argument - it will run all events and remove the objective.

Running events for online players can be done with event argument: '`/q event Beton give_emeralds`' would run "give_emeralds" for player Beton (if he's online) from default package (not necessarily "default" but rather the default one specified in _config.yml_). If you want to run a static event, replace player's name with `-`.

There is also condition argument for checking conditions, for example '`/q condition Beton has_food`'. Events and conditions need to be defined in their files, this command doesn't accept raw instructions. You can skip package name, the plugin will assume you're reffering to package specified in `default_package` option in _config.yml_ file. If you want to check a static condition replace the player's name with `-`.

If you need to create for example "Nettlebane" quest item, just hold it in your hand and type '`/q item nettlebane`'. It will copy the item you're holding into the _items.yml_ file and save it there with the name you specified (in this case "nettlebane"). You can skip the package name here as well.

The '`/q give package.item`' command will simply give you specified item.

Config subcommand is used to modify or display values in configuration files. `set` option replaces the value with what you typed, `add` simply adds your string to the existing value. (Note on spaces: by default the plugin won't insert a space between existing and added value. You can however achieve that by prefixing the string with `_` character. For example: existing string is `objective location`, and you want to add `100;200;300;world;10`. Your command will look like `/q config add default.events.loc_obj _100;200;300;world;10`). `read` option allows you to display config value without modifying it.

Path in this command is like an address of the value. Next branches are separated by dots. For example language setting in main configuration has path `config.language`, and a text in "bye" player option in default quest has path `default.conversations.innkeeper.player_options.bye.text`

You can purge specific player with '`/q purge Beton`' command, where Beton is the name of the player. To purge the entire database at once simply change the prefix in _config.yml_ or delete _database.db_ file.

Delete command ('`/q delete`') allows you to delete from the database every tag, point, objective or journal entry with specified name.

Rename command ('`/q rename`') allows you to rename every tag, point, globalpoint, objective or journal entry in the database. In case of an objective it will also rename the objective in _objectives.yml_, so it continues to work correctly.

If you want to backup your configuration and database make sure that your server is empty (this process requires all data to be saved to database -> all players offline) and run '`/q backup`' command. You will get a zip file containing all your data, ready to be unzipped for restoring the plugin.

Update command ('`/q update`') will try to download the newest version of the plugin and save it to the update folder. This folder is then handled by Spigot to update the plugin. If you accidentally use this command but do not wish to update the plugin, you should remove `BetonQuest.jar` file from the `plugins/update` folder before restarting/reloading the server.

Using '`/q create beton`' command you will create new package named '`beton`'. It will contain the default quest.

The `/q vector` command allows you to create vector variables from the specified in first argument location variable to your position. The result will be saved to the "vectors.{second argument}" variable.

The debug command ('`/q debug`') allows you to enable or disable the debug mode. If the debug mode is enabled after
server startup ('`/q debug true`'), the last 10 minutes of log entries are written down to the
`/plugins/BetonQuest/logs/latest.log` file as history and writing will be continued until the debug mode is disabled
using ('`/q debug false`'). The `latest.log` is renamed to the current date and time on server startup.
It's useful if you search for more information about an issue and can help developers to fix bugs.

The command ('`/q debug ingame`') allows you to manage your ingame debugging.
The ingame debugging sends you live information about your quests to your chat.
Running the command without any argument shows your active filters.
If you don't have any filters active you will see all console output from `/q reload`.
If you have filters active you only see information from the selected packages.
Appending a package name activates the filter for that package.
You can also use `*` / `MyFolder-*` instead of a package name to address all packages / all packages of a folder.
Appending a level allows you to select which types of messages are displayed.
The default level `error` shows all `WARNINGS` and `ERRORS` from the log.
If you want to see more information use the levels `info` or `debug`.
Beware though, the debug level might be spammy.


