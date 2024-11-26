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
* `/q item <package.itemID>` - Creates an item based on what you're holding in hand
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
* `/questlang <lang>` - Changes the language for the player (and globally if used from console). `default` language will use the language defined in _config.yml_.
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
If you need to create for example "Nettlebane" quest item, just hold it in your hand and type '`/q item nettlebane`'.
It will copy the item you're holding into the _items.yml_ file and save it there with the name you specified (in this case "nettlebane").
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
This folder is then handled by Spigot to update the plugin. If you accidentally use this command but do not wish to update the plugin,
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
The download command (`/q download`) can be used to download tutorial quests & quest templates from GitHub repositories.
!!! example
    To download the `default` tutorial quest from the [Quest-Tutorials](https://github.com/BetonQuest/Quest-Tutorials)
    repository and place it in the local package `default` you can run:
    ```
    /q download BetonQuest/Quest-Tutorials main QuestPackages /default
    ```

#### Synopsis
```
/q download [options] <gitHubRepository> <gitBranchOrReference>
```

#### Description
`gitHubRepository` is the GitHub repository name in the format `<namespace>/<project>` where `namespace` is either a
GitHub user or organization. Repositories that can be downloaded from must be whitelisted in the
[configuration](Configuration.md#quest-downloader) first, read more about this in the
[security consideration](#security-considerations) section.

`gitBranchOrReference` is either the git branch or a fully qualified git reference that should be downloaded. Thus, to
download from a branch (e.g. `main`) you can either use the branch name directly (e.g. `main`) or use a git reference
(e.g. `refs/head/main`). For all other git objects you need to use a reference, e.g. to download from tag `v1.2.3` use
`refs/tags/1.2.3`. Additionally, pull requests (e.g. `refs/pull/1731/head`) are also supported but must be enabled in
the [configuration](Configuration.md#quest-downloader), read more about this in the
[security consideration](#security-considerations) section.

#### Options
`-T`, `--download-template` download template files; if either both or none of this option and `-T` is given then both
templates and packages will be downloaded

`-P`, `--download-package` download package files; if either both or none of this option and `-P` is given then both
templates and packages will be downloaded

`-R`, `--raw` do not expect the folders `QuestPackages` and `QuestTemplates` at the source and do not give them special
meaning even if they are present; exactly one of `-T` or `-P` must be present as well when using this option to define
where to put the downloaded files

`-S`, `--structured` expect at least one of the folders `QuestPackages` and `QuestTemplates` to be present at the source
and use them like they are used in the BetonQuest plugin folder

`-s <path>`, `--source=<path>` source path to start the package search from; the path is relative to the git repository
root that is being downloaded from

`-b <basePackage>`, `--base-package=<basePackage>` base package within the quest sources to resolve packages and files;
the base path will not be mirrored locally

`-p <package>`, `--package=<package>` select the packages that should be downloaded; this option can be provided
multiple times to download more than one package at once

`-F <file>`, `--file=<file>` select the files that should be downloaded; this option can be provided multiple times to
download more than one file at once

`-l <localBasePackage>`, `--local=<localBasePackage>` local base package to put the downloaded packages and files into

`-r`, `--recursive` also include [nested packages](../Scripting/Packages-&-Templates.md#__tabbed_1_3) in the download

`-f`, `--force` allow overwriting local files; if not set an error will be logged and the download will be aborted

#### Layout auto-detection
By default, the download command will try to detect if the repository at the given source location is structured like
the BetonQuest plugin folder. This is done by checking if there is a folder called `QuestPackages` or `QuestTemplates`,
if any one of them is present then the download command will teat the quests sources like the BetonQuest plugin folder
and is able to download both templates and packages at the same time. Otherwise, it will be treated as raw and requires
the definition of what kind of files they are so that they can be put into the correct local directory, this can be done
by using either the `-P` or the `-T` option.

#### Security considerations
To prevent the download of arbitrary quest files you need to add repositories you want to be able to download from to a
whitelist. This is a security measure to prevent anyone from screwing up all your quests or downloading malicious files
even if they have the permission to run this command.

Also note that the download of pull requests needs to be manually enabled in the config if required as anyone could open
a pull request to any of the whitelisted repositories and thus circumvent the whitelisting measure described above.
