---
icon: fontawesome/solid/lock
---
# Commands and permissions

## Commands
Required arguments are displayed as such: `<argument>`.
Optional arguments are displayed as such: `[argument]`.

* `/j` - Gives the journal
* `/backpack` - Opens the backpack
* `/compass` - Opens the quest tracking compass
* `/cancelquest` - Opens the quest canceler menu
* `/q` - Lists all available admin commands
* `/q reload` - Reloads the plugin
* `/q objectives <playerName> <list/add/del/complete> <objectiveName/filter>` - Shows player's currently active objectives
* `/q tags <playerName> <list/add/del> <tag/filter>` - Lists all player's tags
* `/q globaltags <list/add/del/purge> <tag/filter>` - Manges global tags
* `/q points <playerName> <list/add/del> <category/filter> <amount>` - Lists all player's points in all categories
* `/q globalpoints <list/add/del/purge> <category/filter> <amount>` - Manges global points
* `/q journal <playerName> <list/add/del> <package.pointer/filter> <date>` 
* `/q event <playerName> <package.eventID>` - Fires an event for the player
* `/q condition <playerName> <package.conditionID>` - Shows if the player meet specified condition or not
* `/q item <package.itemID> <serializer>` - Creates an item based on what you're holding in hand
* `/q variable <playerName> <package.objectiveID> <list/set/del> [key/filter] [value]` - Manage variables stored in
[`variable`](../Scripting/Building-Blocks/Objectives-List.md#variable-variable) objectives
* `/q give <package.itemID>` - Gives you an item defined in the configuration
* `/q purge <playerName>` - Deletes all player's data from the database
* `/q rename <tag/point/globalpoint/objective/entry> <oldName> <newName>` - Renames all specified things in the database
* `/q delete <tag/point/objective/entry> <name>` - Deletes all specified things in the database
* `/q backup` - Creates a backup of configuration files and database
* `/q update` - Updates the plugin to the newest version.
* `/q version`: Displays the versions of BetonQuest, the server and all hooked plugins
* `/q debug [true/false/ingame]`: Enable debug mode and write all down in a log file or disable the debug mode
* `/q download <gitHubNamespace> <ref> <type> <sourcePath> [targetPath] [recursive] [overwrite]`: Download quests and templates from a GitHub repository
* `/questlang <lang>` - Changes the language for the player. `default` language will use the language defined in _config.yml_.
* `/rpgmenu reload <menu>` - Allows reloading all configuration files or just reloading the configuration of one specific menu.
* `/rpgmenu list` - Lists all currently loaded menus and allows opening them just by clicking on them.
* `/rpgmenu open <menu> [player]` - Opens a menu for you or another player. [Opening conditions](../Features/Menus/Menu.md#general-menu-settings) of the menu will be ignored when using this command.

The filter only works on the list and will match all objectives/tags/points that start with the filter. 
Please note, that the names are a composition of the package name and the name of the objective/tag/point.

### Custom Menu Opening commands
The plugin lets you create a new command for each menu which allows all players to open the menu.  
You can also specify BetonQuest conditions so that the menu can only be opened if the player matches specific conditions.
Have a look at the [menu settings](../Features/Menus/Menu.md#general-menu-settings) for more information.

The server must be restarted to unregister command tab completions.

## Aliases

* `/j`: bj, journal, bjournal, betonjournal, betonquestjournal
* `/backpack`: b, bb, bbackpack, betonbackpack, betonquestbackpack
* `/compass`: bc, bcompass, betoncompass, betonquestcompass
* `/cancelquest`: bcq, bcancelquest, betoncancelquest, betonquestcancelquest
* `/q`: bq, bquest, bquests, betonquest, betonquests, quest, quests
    * `objective`: o, objectives
    * `tag`: t, tags
    * `point`: p, points
    * `event`: e, events
    * `condition`: c, conditions
    * `journal`: j, journals
    * `item`: i, items
    * `give`: g
    * `variable`: var
    * `rename`: r
    * `delete`: d, del
    * `create`: package
* `/questlang`: ql
* `/rpgmenu`: rpgmenus, menu, menus, rpgm, qm

## Permissions

* `betonquest.admin` - allows using admin commands (/q, /rpgmenu ...)
* `betonquest.journal` - allows using /j command (default for players)
* `betonquest.backpack` - allows using /backpack command (default for players)
* `betonquest.compass` - allows using /compass command (default for players)
* `betonquest.cancelquest` - allows using /cancelquest command (default for players)
* `betonquest.conversation` - allows talking with NPCs (default for players)
* `betonquest.language` - allows changing the language (default for players)

## BetonQuest administration command: `/betonquest`

**Aliases:** `/q`, `/bq`, `/quest`, `/quests`, `/bquest`, `/bquests`, `/betonquest`, `/betonquests`

**Permission:** `betonquest.admin`

### Reload the plugin: `reload`
Reloading loads all data from configuration, but not everything is updated. Player's data isn't touched to avoid lags made by database saving.
The database is also the same, you will have to reload/restart the whole server for the database to change.

### Interact with quest primitives
#### Manage objectives: `objective`
Objective subcommand allows you to list all active objectives (shown as their labels) of the player.
It can also directly add or cancel objectives using instruction strings.
You can also complete the objective for the player using `complete` argument - it will run all events and remove the objective.

#### Manage tags: `tag`
Tags subcommand allows you to easily list and modify tags. '`/q tags Beton`' would list tags for player Beton.
'`/q tags Beton add test`' would add "test" tag for that player, and '`/q tags Beton del test`' would remove it.

#### Manage points: `point`
Points subcommand is similar - listing points is done the same way. Adding points to a category looks like that:
'`/q points Beton add reputation 20`' (adding 20 points to "reputation" category). You can also subtract points with negative amounts.
Removing the whole point category can be achieved by '`/q points Beton del reputation`'.

#### Run events: `event`
Running events for online players can be done with event argument:
'`/q event Beton quest.give_emeralds`' would run `give_emeralds` for player Beton (if he's online) from the package `quest`.
If you want to run a static event, replace player's name with `-`.

#### Check conditions: `condition`,
There is also condition argument for checking conditions, for example '`/q condition Beton has_food`'.
Events and conditions need to be defined in their files, this command doesn't accept raw instructions.
If you want to check a static condition replace the player's name with `-`.

#### Manage the journal: `journal`
Journal subcommand works in the same way as those two above.
Adding and removing looks like `/q journal Beton add quest.wood_started` (or `del`), and you can also specify the date
of entry when adding it, by appending date written like this: `23.04.2014_16:52` at the end of the command.
Note that there is `_` character instead of space!

#### Manage items: `give` and `item`
If you need to create for example "Nettlebane" quest item, just hold it in your hand and type '`/q item nettlebane <serializer>`'.
It will copy the item you're holding into the _items.yml_ file and save it there with the name you specified (in this case "nettlebane").
The _serializer_ defines the format in which the item is saved (and loaded).
You can skip the package name here as well.

The '`/q give package.item`' command will simply give you specified item.

#### Manage variables: `variable`
This command relates to active [variable objectives](../Scripting/Building-Blocks/Objectives-List.md#variable-variable).
You can list all `key-value` pairs with an optional objective filter.
Further can such a value be set for a key with `set <key> <value>`or an existing pair removed by using `del <key>`.

### Purge player data: `purge`
You can purge specific player with '`/q purge Beton`' command, where Beton is the name of the player.
To purge the entire database at once simply change the prefix in _config.yml_ or delete _database.db_ file.

### Delete data from the database: `delete`
Delete command ('`/q delete`') allows you to delete from the database every tag, point, objective or journal entry with specified name.

### Rename a quest-primitive without loosing data: `rename`
Rename command ('`/q rename`') allows you to rename every tag, point, globalpoint, objective or journal entry in the database.
In case of an objective it will also rename the objective in _objectives_ section in the configuration file, so it continues to work correctly.

### Create a backup: `backup`
If you want to backup your configuration and database make sure that your server is empty 
(this process requires all data to be saved to database -> all players offline) and run '`/q backup`' command.
You will get a zip file containing all your data, ready to be unzipped for restoring the plugin.

### Update the plugin: `update`
Update command ('`/q update`') will try to download the newest version of the plugin and save it to the update folder.
This folder is then handled by the server to update the plugin. If you accidentally use this command but do not wish to update the plugin,
you should remove `BetonQuest.jar` file from the `plugins/update` folder before restarting/reloading the server.

### Debug quests and BetonQuest: `debug`
The debug command ('`/q debug`') allows you to enable or disable the debug mode. If the debug mode is enabled after
server startup ('`/q debug true`'), all log entries from the configured log history time frame are written to the
`/plugins/BetonQuest/logs/latest.log` file as history and writing will be continued until the debug mode is disabled
using ('`/q debug false`'). The `latest.log` is renamed to the current date and time on server startup.
It's useful if you search for more information about an issue and can help developers to fix bugs.

The command ('`/q debug ingame`') allows you to manage your ingame debugging.
The ingame debugging sends you live information about quests to your chat. Running the command without any argument
shows your active filters. If you don't have any filters active you will see all console output from `/q reload`. If you
have filters active you only see information from the selected packages. Appending a package name activates the filter
for that package. You can also use `*` / `MyFolder-*` instead of a package name to address all packages / all packages
of a folder. Appending a level allows you to select which types of messages are displayed. The default level `error`
shows all `WARNINGS` and `ERRORS` from the log. If you want to see more information use the levels `info` or `debug`.
Beware though, the debug level might be spammy.


### Download from GitHub: `download`
The download command (`/q download`) can be used to download tutorial quests & quest templates from
the [Quest-Tutorials](https://github.com/BetonQuest/Quest-Tutorials) repository. For
example `/q download BetonQuest/Quest-Tutorials refs/tags/v2.0.0 QuestPackages /default` will download the `default` tutorial quest and
place it in the same folder. The first argument (`gitHubNamespace`) is the GitHub repository in the format user/repo or
organisation/repo. Before you can download from a repo you need to add the namespace to
the [`repo_whitelist`](Configuration.md#quest-downloader) in the BetonQuest config. This is a security measure that
prevents users from screwing up all your quests or downloading malicious files if they get the permission to run this
command by accident. The second argument (`ref`) is either a commit SHA or a git reference to a specific commit that
should be downloaded. For a branch (e.g. `main`)  `refs/heads/main` works. For a tag it is `refs/tags/tagname`. Pull request references (
e.g. `refs/pull/1731/head`) are also possible but must be enabled in the [config](Configuration.md#quest-downloader).
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
