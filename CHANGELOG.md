# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased] - ${maven.build.timestamp}
### Added
- `quest compass` location now allows variables
- `point` and `globalpoint` events and conditions now support variables
- `cancel` event now has `bypass` keyword to ignore conditions in Quest Canceler
- `quester` in conversation now supports variables
- Support for Message Parsing Formatter
    - `notify` event
    - `notifyall` event
    - `npc` Variable
    - Compass Names
    - Journal
    - Quest Canceler
    - Conversation
    - Plugin Messages / Translations
    - Menu Items & Title
    - Mob Names for `interact` objective, `spawn` event, `removeentity` event and `entiry` condition
- `region` and `wand` conditions now supports variables
- `JobsReborn`, `Quests`, `McMMO` now supports variables
- notification categories `conversation_start` and `conversation_end`
- Support for Npc plugins
    - `FancyNpcs`
    - `ZNPCsPlus`
    - `MythicMobs`
- `tag` and `globaltag` events and conditions now support variables
- `package.version` for QuestPackages and -Templates
- BlockSelector now supports variables when used in events, conditions, objectives
- Variable support for all lists in events objectives and conditions for things like events conditions items and co.
- `Menu Item` are now in the `menu_items` section, allowing references from multiple Menus
- `Menu` title allows defining different messages
- `eval` event and condition to resolve a string and interpret it again as event/condition
- `constants` to replace the `variables` section including new addressing of them with the new constant variable
- `mmoitem` item type
- ingame message for plugin updates (removed in 2.1.0)
- menu `slots` and bound `item` now support variables, `menu item` now supports variable `item`
- `menu` event now supports the `update` action to update the currently opened menu
- `menu` conversation IO settings to configure the scroll down and up buttons
- `citizens` npcs are now used only from default registry
- `invincible` config option to disable conversation invincibility
- `debug dump` command to only dump the history to the log file
### Changed
- Spigot is no longer supported, paper is now required 
- message.yml file was deleted and instead the lang folder now contains all translations
- menuConfig.yml file was deleted, translations are in the lang folder and the config is part of config.yml 
- `cancel` event now respects by default canceler conditions
- `objectives` can now be removed from database via command even when it does not exist
- `max_npc_distance` config into `max_conversation_distance`
- `acceptNPCLeftClick` and `npcInteractionLimit` configs into `npcs.accept_left_click` and `npcs.interaction_limit`
- `%quester%` variable is used instead of `%npc%` for the quester's name in conversations; `%npc` variable is used now for the new Npc system
- `fish` objective use block selector instead of a QuestItem
- everything that used `Citizens` NPC ids now uses the BQ NpcIDs
- conversations now always print their start and stop conversation message
- `events`, `objectives`, `conditions`, `variables` and co. are now case-insensitive
- underscores aren't replaced anymore, instead quoting needs to be used now
    - `shear` objective - name 
    - `mobkill` objective - name
    - `password` objective - password
    - `interact` objective - custom name and real name
- `items` now have a type like events
- renamed list keys from singular to plural
    - objectives - `event` and `condition` are now `events` and `conditions`
    - events - `condition` is now `conditions`
    - menus - `condition` is now `conditions`
    - conversations - options `condition`, `event`, `pointer` and `extend` are now `conditions`, `events`, `pointers` and `extends`
- `pickrandom` event now use tilde instead of percentage
- `Menu Items` name and lore are no longer italic by default, name can be overridden while keeping original lore
- events, objectives and conditions do not replace underscores anymore, quoting syntax needs to be used now
- `mooncycle` condition is now `moonphase` and uses a variable list of the phases instead of numbers
- `item` command now requires the specification of serializer
- `npcs` in the `npc_holograms` and `npcs`, `conditions` and `locations` in the `effectlib` section now use comma separated lists
- in quest cancelers the events are now executed before the teleport
- `simple` quest items now resolve variables one time on reload to support `constant` variables
    - `owner:%player%` was changed to `owner:` to allow constant pre-parsing
- `math` variable now gives a better exception message and does not return 0 instead
- `folder` event now use the `unit` argument to define the time unit for ticks, seconds and minutes
- custom items in the chest conversation IO are now defined in the `properties` section on an option
- Reworked the config.yml file to be more clean and structured
    - `item.backpack.cancel_button` and `item.backpack.compass_button` do not have a `DEFAULT` value anymore, instead you need to specify an item
- LoadDataEvent is now called before and after the data is loaded with a new enum `LoadDataEvent.State` that indicates the state
- menu conversation IO settings are now defined the config.yml file in the `conversation.io.menu` section
- `chest` conversation IO now actually ends when there is to npc option left or the player closes the inventory
- `menu` Conversation IO displaying and scroll behavior rework
### Deprecated
### Removed
- undocumented prefix feature in conversation
- `citizens_npcs_by_name` config option, which is now part of the id
- start and stop sound in the configuration for conversations were removed in favor of the notification system that now also has the two new build in categories `conversation_start` and `conversation_end`
- `menu reload` command which did not work
- `variables` section in favor for `constants`
- `MMOItems` conditions, events and `mmoitemcraft` objective in favor of the built-in ones
- `journal.chars_per_page` was deleted from the config.yml 
### Fixed
- Reloading plugin did not reload Menu config
- potions generated not extended/upgraded since 2.2.1
- Biome condition on mc 1.21.4 could not parse the biome from the instruction anymore
- `randomnumber` variable now correctly returns lower bound
- `stage` objective 'index' property not working and now also starts at 0
- betonquest command without arguments did not work anymore
- the Fabled hook not working properly
- `compass` event did not work with variables in `compass` location
- `quest canceler` did not resolve global variables
- `quest canceler` used `loc` instead of `location` key for teleportation
- `journal` language only changed after rejoin when changing player language
- `journal` entry renaming or deleting via command now affects all specified pointers and not only one
- `ride` objective did only count vehicles, not all entities
- scrolling the mouse in the menu conversation IO did not work
- `MMOCore` attribute loading with MMOCore 1.13
- missing messages `animals_to_tame` and `times_to_jump` in language files by adding them
- `Menu Item` parsing with newlines lead in some cases to missing lines
- `Menu`s throwing exception on reload when bound commands are used
- `mmoitemcraft` objective NoSuchMethodError with MMOItems 6.10+
- `menu` conversation IO exception in one edge case
- 1.21.5 ProtocolLib support
- `interact` objective's documentations used hookLocation instead of loc for the location
- `randomnumber` variable using locale/eventually comma instead of dots breaking further using like math
- `interact` objective now blocks the interaction with an item frame properly if cancel is set
- `brewery` null pointer exception when a brew has no recipe
### Security

## [2.2.1] - 2025-01-12
### Added
- A Bukkit event which fires when a player's points change
- `folder` event now has an argument `cancelConditions` to cancel a running folder
- `action` objective now supports configuring the hand used for the interaction, preventing multiple objective completions at the same time on right click
### Changed
### Deprecated
### Removed
### Fixed
- fixed NPC navigation by giving better error messages and handling the stuck action correctly
- custom sounds from resourcepacks could not be used in conversation start and end sound
- `chestput` objective caused that no chest could be opened on the server when `multipleaccess` was forbidden(default)
- providing wrong commit sha to the download command now gives a proper error message
- no longer set damage in `QuestItem` if none set
- loading of a backup file has the wrong order of applying the statements, causing exceptions and a broken migration
### Security

## [2.2.0] - 2024-12-01
### Added
- new conversation options
  - `start_new_lines` the amount of new lines that should be printed before the conversation starts
  - `npc_text_fill_new_lines` should lined be filled between the NPC text and the player answer options
- `world` condition now supports variables for the world name
- underscores are now escapable in item names and lore via `\_`
- `weather` condition now allows to define a world, which can be a variable and can now be used in static context
- `time` condition now allows to define a world, which can be a variable and can now be used in static context
- `time` condition now supports minutes
- `paste` event can now rotate schematics
- `permission` condition now supports variables
- `moonCycle` condition now allows to define a world, which can be a variable and can now be used in static context
- global variable support for npc ids and condition ids in `effectlib` section
- `and` condition can now be static
- missing tab-completion and documentation for globalpoints and globaltags command
- `party` condition now supports a location variable, to be used in a static context
- `enitity` condition now supports variables for the entity name
- `mmochangeclass` objective for MMOCore that listens for a player changing their class
- `removeentity` event now supports variables for the entity name
- `variable` condition can now be used in a static context
- TrainCarts integration
    - `traincartslocation` objective that executes when the player passed a location 
    - `traincartsride` objective that executes when the player is riding a train
    - `traincartsexit` objective that executes when the player exited a train
    - `traincartsride` condition to check if the player is riding a specific named train
- `scoretag` event and condition
- `action` objective now supports configuring the hand used for the interaction, preventing multiple objective completions at the same time
### Changed
- `spawn` event now only spawn mobs and no other entities
- ProSkillAPI rename to Fabled
    - this includes the class and level condition
### Deprecated
### Removed
### Fixed
- broken title check in book `QuestItem`
- BlockSelector did not match exact block matches and started regex matching causing performance issues during load and reload
- wrong order of arguments in fire work effects
- non-static `variables` causing cross conversation validation to break
- LuckPerms integration not pushing the permission update to the connected servers correctly.
- `crafting` objective where complex recipes are not recognized
- `hieght` condition where variable locations threw an exception
- `globalpoint` condition where not initialized global points where 0
- `point` condition where not initialized points where 0
- `QuestItem` Potions cannot be saved in 1.20.5+
- Schedules could run twice on some systems by an inaccurate time returned java's Instant.now()
- `Journal` interaction with blocks like lecterns
- sections that are created in templates by write operations, where no edits should actually be done
    - this caused a lot of "ghost" sections that could not be loaded like schedules, menus and manny more
- Conversations work in 1.21.3 again
### Security

## [2.1.3] - 2024-08-06
### Added
- BreweryX compatibility
### Changed
- `mspawnmob` can now be used in static context
### Deprecated
### Removed
### Fixed
- LuckPerms integration not sending the permission updates via the messaging service to connected Servers in the same network.
- LuckPerms TagCalculator trying to use PlayerData even if player is offline
- Memory Leak in CombatTagger
- AdvancementIO not working in mc 1.20.5/6 and 1.21
- chaining of movenpc events did trigger fail events
- effect event duration -1 did not result in infinite duration
- translation in conversation got stored in default language
- incorrect calculation of the width of strings when using characters with certain accents when doing pagination
### Security

## [2.1.2] - 2024-06-23
### Added
- LuckPerms permission events
    - `addPermission` event adds a permission to a player
    - `removePermission` event removes a permission from a player
- Location Vectors can now be chained
- `eval` variable to resolve a variable string as variable
### Changed
- `AureliumSkills` was renamed to `AuraSkills` the following was changed in BQ
    - `aureliumskillslevel` condition was renamed to `auraskillslevel`
    - `aureliumstatslevel` condition was renamed to `auraskillsstatslevel`
    - `aureliumskillsxp` event was renamed to `auraskillsxp`
- prevent reply when the text is not completely displayed for the SlowTellRaw conversation IO
- `resourcepack` objective is now paper only
- `die` objective now support respawns without canceling the actual death of the player
- `ph` variable can now be used static
### Deprecated
### Removed
### Fixed
- NPE if a player replies to a SlowTellRaw conversation IO when the text is not completely displayed
- head `QuestItem` matches even if they are distinct 
- modulo operator in math variable can now be used by escaping the percent sign
- `weather` event not static callable even when providing a world
### Security

## [2.1.1] - 2024-05-09
### Added
### Changed
### Deprecated
### Removed
### Fixed
- math variable with dots (since 2.1.0)
### Security

## [2.1.0] - 2024-05-09
### Added
- `block` objective now supports parameters `loc` and `region` to limit the area
- `block` objective now supports the parameter `ignorecancel` allow counting breaking / placing blocks in protected regions
- command `variable` to list and modify variables on a variable objective
- config option `conversation_IO_config.slowtellraw.message_delay` to set the delay between messages in the SlowTellRaw conversation IO
- `resourcepack` objective - to allow checking when the player accepts, denies, downloads, or fails to download a resource pack 
- `region` objective now supports variables as region name
- `location` objective now allows `entry` and `exit` keywords to track entering and leaving the location
- allowing adjustment of maximum hologram display radius
- support to execute BetonQuest commands from command blocks
### Changed
- player variable
    - `%player%` is now the same as `%player.name%`
    - `%player.name%` will display the name of the player
    - `%player.display%` will use the display name used in chat
    - `%player.uuid%` will display the UUID of the player
- marking of mobs now uses variables in all contexts of spawning killing and removing
- marking of mobs is now stored in the persistent data container of the entity to prevent loss on server restart
- default `default_conversation_IO` in config from `menu,chest` to `menu,tellraw`
- corrected Russian messages in `messages.yml` file
### Deprecated
### Removed
### Fixed
- `variable` objective loads newlines correctly
- exception in `lookAtBlock` condition when omitting the type
- MMOCoreClassCondition, MMOItemsGiveEvent, MMOItemsHandCondition and MMOItemsItemCondition now work with numeric identifiers
- yaml multiline instructions could lead to partly working events, conditions and objectives
- creation of advancement tab when an advancement NotifyIO is sent
- `burn` event throwing unexpected error when omitting duration
- added missing firework meta to string representation of QuestItem
- required flags in `QuestItem` were not relevant in equals check
- `folder` event did not respect the sync / async context that it is executed in
- incorrectly measuring the distance between player and hologram if they are in different worlds
- incorrect calculation of the width of strings when using some special characters when doing pagination 
- previously undocumented quoting in instructions
### Security

## [2.0.1] - 2024-03-24
### Added
- `hunger` event now supports variables
- `time` event now supports variables and new `ticks` argument
### Changed
- Quests by PikaMug got updated from version 4.X to 5.X
- The ProtocolLib dependency was downgraded from 5.2.0-SNAPSHOT-679 to 5.0.0-SNAPSHOT-636
### Deprecated
### Removed
### Fixed
- `weather` event did not use `duration`
- `lever` event does not work with redstone
- papers future remove of CB package relocation
### Security

## [2.0.0] - 2023-12-31
### Added
- Logging
    - Ingame logging
        - Logging during `/q reload`
    - New command `/q debug ingame`
    - Debug logging to separate file
    - Log history length can be freely configured
- Quest Templates
    - A new folder `QuestTemplates` can now be used to define templates for packages
    - templates can also extend templates
- add AureliumSkills Compatiblity
    - added AureliumSkillsLevelCondition
    - All existing users must add aureliumskills: 'true' to their config.yml's hook section.
    - added AureliumStatsLevelCondition
    - added AureliumSkillsLevelExperienceEvent
- add DecentHolograms Compatibility
    - holograms from DecentHolograms are a direct alternative to HolographicDisplays
- add LuckPerms context integration for tags
    - All existing users must add luckperms: 'true' to their config.yml's hook section.
- add FakeBlock Compatibility
    - added `fakeblock` event that has the arguments `showgroup` and `hidegroup` to show and hide FakeBlock groups
    - added `hook.fake-block` config option, default: `true`
- add RedisChat Compatibility
    - added `redischat` chat interceptor
- added Event Schedules to replace an old static-events system
    - static events from schedulers now respect static conditions
    - events called by schedules are always called player independent
    - `runForAll` event can now be used to run events for all players that match a condition
    - `runIndepent` event can be used to run events static (player independent) from player dependent contexts
- added support for Base64 encode custom heads
    - can be created from items in inventory using the BetonQuest `item` command (Paper only, Bukkit/Spigot can be configured manually)
    - can be given to players using the BetonQuest `give` command
- NotifyIO "totemIO"
- Support for MythicLib
- player attribute to QuestCompassTargetChangeEvent
- added PlayerObjectiveChangeEvent, PlayerJournalAddEvent, PlayerJournalDeleteEvent
- Allow %player% variable for PLAYER_HEAD quest items
- allow access to objective variable properties from other packages
- allow point variables from other packages
- API method in objective API that starts and stops it per player
- config option `default_hologram` to set hologram priority if multiple hologram plugins are loaded at once
- config option `journal.show_in_backpack` to remove the journal from the backpack
- config option `journal.lock_default_journal_slot` to lock the journal to the `default_journal_slot`
- config option `journal.give_on_respawn` to add the journal to the inventory after the player respawns
- config option `journal.custom_model_data` to give the journal a ressource-pack-based skin
- config option `mysql.reconnect_interval` to set the reconnect interval if the connection to the database gets lost
- compass now supports items from other packages
- new messages.yml entries `inventory_full_backpack` and `inventory_full_drop` to have more specific messages, when the inventory of the player is full
- `menu` conversationIO option `npc_name_newline_separator`
- added `q download` command
- variable support for menu titles
- configurable cooldown to menu conversation IO
- holograms
    - variable support
    - top lists ordered by point values of players
    - `npcs` list now supports global variables as NPC ID in NPC Holograms
- menus now support global variables
- exposed the Citizen variable and enhanced location capabilities
- menus now support string with newline or string list text values for lore
- amount of objectives now support variables
- added quest item flag recording and restore
- Conversations now support cross-conversation pointers to conversations in other packages
- QuestCancelers can now be referenced across packages
- Chat Interceptor `packet` is now 1.20.4 compatible
- `freeze` event - ProtocolLib compatibility feature: Blocks the player from moving for the specified amount of ticks
- `block` objective - properties: `absoluteAmount`, `absoluteLeft` and `absoluteTotal`
- `command` objective
- `equip` objective
- `delay` objective - now support variables
- `sudo` event - now supports variables
- `opsudo` event - now supports variables
- `variable` condition - now supports variables
- `bossbar` notify style now supports variables for the `progress` and `stay` arguments
- `delay` objective property: `rawSeconds`
- `fish` objective now has `hookLocation` and `range` settings.
- `consume` objective now has `amount` argument.
- `mmoprofessionlevelup` objective can now check the main character level as well
- `burning` condition
- `inconversation` condition
- `language` condition
- `heroesattribute` condition - Heroes compatibility feature: Checks a player's level for a particular attribute against a value
- `variable` condition - regex can now also be a variable
- `mmspawn` event now has argument `target` Makes the spawned mob target the player
- `mmspawn` event now has argument `private` Visually hides the spawned mob from other players. Does not stop sound or particles
- `mmspawn` event now supports the `marked` argument
- `objective` event now supports a comma-separated list of objectives
- `mmobkill` objective now supports the `marked` and `deathRadiusAllPlayers` argument
- `marked` argument now supports %player% variable 
- `globaltag` and `globalpoint` variables
- `burn` event - ignites player for given seconds, supports variables
- `velocity` event - throws the player by a vector (can be variable) with a direction and modification
- `block` objective - added argument `noSafety` which disables removing progress when the player does the opposite of what the objective asks for
- `block` objective - added property variables `absoluteLeft`, `absoluteTotal`, `absoluteAmount` that always return absolute values
- `hunger` condition and event
- `variable` condition - forceSync argument forces the condition to be checked on the main thread
- `variable` condition - now supports variables as both the input and the regular expression
- `command` event - now suppress console output
- `cancelconversation` event
- `time` event - now supports subtraction and world selection, which made it persistent and static
- `globalpoint` event - new syntax for manipulating global points
- `experience` event - it's now possible to change xp, change and set levels and set the xp bar
- `deleteglobalpoint` event
- `score` event - more options for manipulating the scoreboard
- `item` variable - new `name` and `lore` argument and now supports items from other packages
- `randomnumber` variable added - supports variables
- `give` event - new `backpack` argument to place items in the backpack (if a valid QuestItem)
- `party` event - new optional `amount` of maximal affected players
- `drop` event
- `itemdurability` event, condition and variable
- `numbercompare` condition
- `cancelOnLogout` argument for `folder` event
- `npcinteract` objective - now supports the argument `interaction` to choose between left, right or both clicks
- `log` event
- `party` event - new range 0 and -1 to select players in the same world or server wide
- `stage` objective, condition and event
- `first` event - attempts to run a list of events until one successfully runs, like a compressed `if` event
### Changed
- Java 17 is now required
- changed package names from `pl.betoncraft.betonquest` to `org.betonquest.betonquest`
- Changed quest package structure
    - Quest packages are now searched and loaded from the folder `BetonQuest/QuestPackages/`
    - Quest packages can now contain more quest packages in sub folders
    - Relative paths can now navigate downwards and not only upwards
    - `main.yml` was renamed to `package.yml`
    - Any file and folder structure with any file and folder names is now allowed, except `package.yml`, as that file
      indicates a quest package
    - `events`, `objectives`, `conditions`, `journal` and `items` are now defined in a config section matching their
      names
    - `conversations` and `menus` are now defined in a config section matching their names and a unique identifier like
      the file name before
    - The `enabled` boolean is now defined in the `package` section
- all objectives that can be advanced without directly completing now support notify
- all objectives that can be advanced without directly completing now support `left`, `amount` and `total` variables
    - the `left` and `amount` variables of some objectives were swapped and have been corrected: `left` is the amount
      left, `amount` is the amount done
- NPC Holograms are reworked
    - individual refresh rate for each hologram
    - the boolean follow is now set per hologram
    - the vector offset origin has been changed. The hologram is now directly above the NPCs head by default. This means all previously custom vectors are now increased by three blocks on the y-axis.
  - npc_effects are reworked
    - the section changed its naming from `npc_effects` to `effectlib`
    - the section `disabled` got removed
    - the section `check_interval` is now an inner section of the effect and now called `checkinterval`
    - next to the `npcs` section there is now an `locations` section
    - effects will now follow npcs if they're pathing to another spot
- AureliumSkills updated to version Beta1.2.5
- PikaMug Quests updated to version 4.1.3
- Items now support AIR
- Menus now support new `click` options `shiftLeft`, `shiftRight` and `middleMouse` to execute events on item clicks
    - Therefore `left` and `right` in the `click` section no longer include shift clicks
- `folder` event - now executes events immediately if no delay is set
- `weather` event - now has an optional variable duration (in seconds) and an optional world param
- `paste` event - can now be static
- `chestput` objective - can now block other players from accessing a chest while someone is putting items inside
- The location and region objectives now register the movement of players inside a vehicle
- written book is now readable as a quest item
- HolographicDisplays updated to 3.0.0
- Added staticness indicator to variables that can be executed without a direct player connection
- written book quest items can now be read
- `experience` objective event and condition were reworked
  - condition and objective do not support raw experience anymore
  - all allow decimal level and variables now
- changed backpack configuration. "" will hide the compass or canceler
- `smelt` objective - now requires a QuestItem instead of a BlockSelector
- `cancelquest` command - has its own permission now
- `compass` command - has its own permission now
- `language` command - is now persistent
- `point` command - list can now be filtered by beginning letters
- `tag` command - list can now be filtered by beginning letters
- `globalpoint` command - list can now be filtered by beginning letters
- `globaltag` command - list can now be filtered by beginning letters
- `journal` command - list can now be filtered by beginning letters
- `objective` command - list can now be filtered by beginning letters
- `variable` condition supports escapable underscores to avoid replacement
- `kill` event - now kills the player, instead of dealing damage, which is more reliable
- `lightning` event - now has a new noDamage argument
- `clear` and `killmob' events - got replaced by new `removeentity` event
- the line length of `SlowTellRaw` conversation IO from 60 to 50 to fit the actual minecraft chat
- Item enchantments was changed to include zero as a legal value, not just positive numbers
- the objectives mmocorecastskill and mmoitemcastability were merged into the mmoskill objective
- `command` event no longer runs for all players on the server if a variable is used
- `math` and `version` variables - now static
- `alternative` and `check` condition - now static
- `open_events` in a menu are now called before the menu actually opens
- `conversation` event now support a start option
- `riding` condition - renamed to `ride`
- `vehicle` objective - renamed to `ride`
### Deprecated
### Removed
- deprecated internals, code and old features
- Support for MMOLib
- `message` event
- `title` event
- `playsound` event
### Fixed
- added missing spaces to time left property of delay objective 
- catchup now works even if the schedule was never executed before
- q version now works again
- RPGMenu error when teleport events are used as click events
- RPGMenu bound items not always working
- npc_holograms do now show errors during reload and not one tick later
- the first slot in backpack stays empty when journal is in player inventory
- npc_holograms are not shown correctly with multiple defined npcs
- spectator mode is now disabled for chest conversation io to prevent being stuck in the conversation
- packet chat interceptor does not catch action bar anymore
- time event does not work with floating point values
- global variable recursive resolution cross packages
- inaccurate location variable decimal rounding
- NPC holograms from Citizens are not hidden correctly
- `location` objective - is now more robust if the player changes a world
- `brew` objective - now counts newly brewed potions even if there were already some potions of the desired type in
- `chestput` objective - did now work with double chests other slots present
- `menu`(open) event - showed the previous menu again
- non .yml files cause errors when loading quest packages
- `pickrandom` event - did not calculate chance correctly
- `pickrandom` event - did not allow dashes in event names
- `action` objective - ignored offhand at all
- `enchant` objective - did not work at all, now has `amount` and `requirementMode` parameters
- Hologram topX line not working with profiles
- the craft objective could be completed without consuming materials
- LocationObjective resolves variable for a player who does not have the objective, and so maybe also don't have the variable
- performance when using many conditions / events / ... repeatedly
- `/q journal` command allowed invalid journal pointers
- AsyncSaver will shut itself down if the database connection is lost, resulting in data loss
- Journal entries did count color codes as line length, affecting the formatting of pages
- Notification categories could be modified during runtime with the notify event
- Leading spaces are now preserved in conversation messages and journal entries 
- giving `air` with the give command or the give event crashes the server
- duplication of holograms when reloading BetonQuest and the hologram is hidden
- `menu` conversation IO kicked players when conversation started in the air caused by flying detection
- `menu` conversation IO did not stopped scrolling at the bottom and began to scroll from the top again
- removed the hearts of the Armorstand in the Menu Conversation IO
- journal entries now keep their leading whitespaces
- exceptions in `sudo` and `command` events leading to broken conversations that could not be exited
- the NPC's head could be stolen from conversations with the `chest` style
### Security
- it was possible to put a QuestItem into a chest
- bump log4j dependency 2.15.0 to fix CVE-2021-44228
- `take` event - is now threadsafe

## [1.12.11] - 2023-12-25
### Added
### Changed
- the MythicMobs integration was updated to MythicMobs 5.3.5
- the MythicLib integration was updated to MythicLib 1.6-SNAPSHOT
  - the objectives mmocorecastskill and mmoitemcastability were merged into the mmoskill objective
- the MMOCore-API integration was updated to MMOCore-API 1.12-SNAPSHOT
- the MMOItems-API integration was updated to MMOItems-API 6.9.4-SNAPSHOT
### Deprecated
### Removed
### Fixed
- conversation could have a deadlock and a player can get stuck in a conversation
- conversation could not be canceled due to a race condition
- JobsReborn ObjectivePaymentEvent couldn't be used
- ProtocolLib warning for Minecraft 1.20.2
### Security

## [1.12.10] - 2023-02-18
### Added
### Changed
### Deprecated
### Removed
### Fixed
- ProtocolLib's based `packet` interceptor was fixed for MC 1.19, now ProtocolLib 5.0.0 is required
- conversation IO chest did not show the correct NPC heads
- `objective` event - static calls did not remove the objective for online players
- legacy `§x` HEX color format not working in some contexts
- eating of items when entering the chest conversation io actually consumed the item
### Security

## [1.12.9] - 2022-06-22
### Added
### Changed
### Deprecated
### Removed
### Fixed
- `weather` event - storm did not work
- 1.19 ProtocolLib warnings about deprecated packages
### Security

## [1.12.8] - 2022-03-07
### Added
- new line support for `journal_lore` in `messages.yml`
### Changed
- PikaMug Quests updated to version 4.1.3
### Deprecated
### Removed
### Fixed
- reload with an invalid PlayerHider causes a NPE
- QuestItems could be eaten, this was caused by a changed mc behavior
- command, sudo and opsudo events didn't work with conditions
- interact objective did not work with armorstands
- MMOItemsGiveEvent did not check if item actually exists
- notify IO `subtitle` and `title` with only a subtitle was not send
- npcHider not working for Citizens with ModelEngine trait
### Security

## [1.12.7] - 2021-12-11
### Added
### Changed
### Deprecated
### Removed
### Fixes
- exception during reload, when npc_holograms are disabled
- `entities` condition and `clear` event now support not living entities
- mmoitems item creation only worked with uppercase id's
### Security
- updated log4j to 2.15.0 which fixes CVE-2021-44228

## [1.12.6] - 2021-10-14
### Added
### Changed
- `action` objective cancels now the event, before other plugins check for it (better third-party support)
### Deprecated
### Removed
### Fixes
- added missing config options to the default config
- version check for ProtocolLibIntegrator
- quest item empty name comparison
- customized built-in messages that use the advancementIO
- BlockSelector without a namespace but starting with `:` did not work and threw an exception
### Security

## [1.12.5] - 2021-08-11
### Added
- Version checks for ProtocolLib and Shopkeepers support
### Changed
### Deprecated
### Removed
### Fixes
- MMOCoreClassCondition used the class display name instead of the class ID to compare classes.
- the take event called Paper's ItemStack.getI18NDisplayName() instead of Spigot's ItemStack.getItemMeta().getDisplayName
- fixed hooking in ProtocolLib
- max_npc_distance was set to 5.3 to prevent instant quiting of conversations
- conversation IO menu sometimes leave an armorstand spawned
- sometimes messages in a conversation are not sent when the packet interceptor is used
### Security

## [1.12.4] - 2021-07-05
### Added
- Vietnamese translation
- added invOrder setting to (mmoitem)take event
- the mmoitemtake event & mmoitem condition now also check the backpack
    - this will not work until the item rework / until the backpack contains NBT data
### Changed
- `/q create package` command does now create an empty package
### Deprecated
### Removed
### Fixes
- `brew` objective triggers all the time and counts correctly
- only generate default package if BetonQuest folder is empty
- fix backpack passing references instead of clones
- fixed combat event packet that changed with MC 1.17
### Security
- the take event is now threadsafe

## [1.12.3] - 2021-05-05
### Added
- FastAsyncWorldEdit compatibility
- craft objective variable `total`
- curly braces in math.calc variables for using variables with math symbols
- player attribute to QuestCompassTargetChangeEvent
### Changed
### Deprecated
- math variable now allows rounding output with the ~ operator
### Removed
### Fixes
- parsing of math variable
- craft objective: multi-craft, drop-craft, hotbar/offhand-craft, shift-Q-craft and any illegal crafting is correctly detected,
- craft objective variables `left` and `amount` were swapped
- NPC hider for not spawned NPCs
- Conversation IO Chest load NPC skull async from Citizens instead of sync
- block selector didn't respect regex boundary
- block selector regex errors are now properly handled
- `default_journal_slot: -1` now uses the first free slot instead of the last hotbar slot
- mmobkill objective notify argument not working correctly
- `fish` objective didn't count the amount of fish caught in one go (if modified by e.g. mcMMO)
- fixed smelt objective: only taking out normally did count, shift-extract got canceled
- empty values in `variable` objective now don't break on player join
- PacketInterceptor sync wait lag
- notifications using the chatIO were catched by the conversation interceptor
- case insensitive `password` objective did not work if the password contained upper case letters
- global variables didn't work in quester names
- quest items couldn't interact with any blocks, which also prevented them from mining blocks
- the shear objective's sheep couldn't have underscores in their names
- backpack passing references instead of clones
- compass event now allows global variables
### Security
- it was possible to put a QuestItem into a chest

## [1.12.2] - 2021-03-14
### Added
### Changed
### Deprecated
### Removed
### Fixes
- `npcrange` objective is triggered at wrong time
- Citizens compatibility for not spawned NPCs
- NotifyIOs are case-sensitive
- all mmo objectives trigger for everyone
- command event includes 'conditions:...' into the command
- tags and points are now thread safe
- compatibility for packet interceptor on papermc
- fix books not parsing color codes
- action objective for any block ignored location
### Security

## [1.12.1] - 2021-02-05
### Added
- Ingame update notification if the updater found an update
### Changed
### Deprecated
### Removed
### Fixes
- The Autoupdater got a small fix, and the fail safety for broken downloads was improved
- `npcrange` objective does not throw errors when the player is in a different world than the NPC
- The block objectives notify could not be disabled
- fixed ConcurrentModificationException in EntityHider
- fixed notify enabled by default for some objectives
- fixed some grammar mistakes in debug messages
- fixed npc teleport and walk operations in unloaded chunks
- fixed inaccurate location variable decimal rounding
- fixed NullPointerException for NPCs with conversation
- fixed resuming to path finding when conversation interrupt movement
- fixes Die objective teleporting player during the tick
### Security

## [1.12.0] - 2021-01-10
### Added
- Tags and Objectives can now be removed with a static event for all players, even if they are not online
    - deletepoint event can now also be called to delete all points for all players
    - journal del event can now also be called as static
- Added integration for TeamRequiem plugins (MMOCore, MMOItems, MMOLib)
    - Conditions:
        - MMOClass condition (type & class)
        - MMOProfession condition
        - MMOAttribute condition
        - MMOItems item condition (item in inventory)
        - MMOItems hand condition (item in main/offhand)
        - MMOLib stats condition (a ton of stats from Core and Items combined)
    - Objectives:
        - Level X Profession to X Level
        - Craft / Upgrade X Item within Inventory
        - Craft X item
        - Apply Gem Stone to Item
        - Upgrade Item via Consumable
        - Cast Item ability
        - Cast Class ability
        - Mine MMOBlock
    - Events:
        - Add mmo class level or exp
        - Add mmo professional level or exp
        - Add Skill points
        - Add attribute points
        - Add attribute reallocation points
        - Add class points
        - Give Item ️
        - Take Item
- equal argument for condition 'empty'
- Condition 'wand' can now have an option '
- Implementing 1.15 support for Events and Conditions
- New Chat event, that write chat messages for a player
- Added 'pickup' objective
- Added stopnpc event, that will stop the movenpc event
- Added teleportnpc event, that will stop the movenpc event and teleport the npc to a given location
- Added option check_interval for holograms in custom.yml and added GlobalVariable support
- Added deletepoint event to delete player points
- Added mythicmobdistance condition that will check if a specific MythicMobs entity is near the player
- Added level argument to 'experience' objective and condition
- Added prefix argument in password objective
- Added level argument to 'experience' objective and condition
- Added prefix argument in password objective
- Added fail argument in password objective
- Added notify option to point event
- Added an interceptor that does not intercept: 'none'
- Added ConditionVariable. It returns true or false based on whether a player meets a condition.
- Improved bStats
- Added login objective
- Added period argument to folder event
- Added variable support to the Notify system
- Added variable support to the PickRandomEvent
- Added "acceptNPCLeftClick: true / false" config option
- Added optional "minlevel" and "maxlevel" arguments to mmobkill objective
- Added new options 'inside/outside' for npcrange objective, support for multiple npcs and improved performance
- Added new Event QuestCompassTargetChangeEvent that is triggered when a new CompassTarget is set. It is also possible to cancel it
- added multi language support for Notify system
- Added 'notifyall' event to broadcast a notification
- Added new notification IO 'sound'
- Added 'jump' objective
- Added left, amount and total properties to player kill objective
- Added 'neutralMobDeathAllPlayers' argument to the `mmobkill` objective
- Added custom model data support for items
- Added new config option 'npcInteractionLimit' default 500 that limits the click on an NPC to every x milliseconds
- Added PlayerHider to hide specific players for specified players
### Changed
- devbuilds always show notifications for new devbuilds, even when the user is not on a _DEV strategy
- Items for HolographicDisplays are now defines in items.yml
- Command 'bq rename' can now be used for globalpoints
- The old updater was replaced with a new one
- AchievementCondition is replaced with AdvancementCondition
- Renamed objective Potion to Brew
- Renamed 'monsters' condition to 'entities'
- Renamed 'xp' event to 'experience'
- new config option mysql.enabled
    - if you already have an installation, you can add this manually to get rid of the mysql warning during startup
- events in conversation options are now executed before npc or player responses are printed
- message event now ignores chat interceptors during conversation
- tame objective now works with all tamable mobs, including possible future ones
- improved chestput waring for locations without a chest
- reworked location variable: %location.(xyz|x|y|z|yaw|pitch|world|ulfShort|ulfLong)(.NUMBER)%
- multiple conditions and objectives now use the block selector. The same applies for the setblock event.
- static events now allow comma separated event list
- changed the `npc_effects` behavior to be package wide instead of global if no NPC is defined in the custom.yml
- EventHandlers in general updated to ignore canceled events
- improved performance for condition checks (Bug where it took seconds to check for conditions)
- improved performance for conversation checks (Bug where it took seconds to check for conversation options)
- The plugin will no longer be loaded before the worlds are loaded
- Citizens Holograms are now more robust on reload and reload faster
- Added player death/respawn behavior to Region Objective and improved performance
- changed smelting and fish objective from material to BlockSelector
### Deprecated
- Marked message event for removal in BQ 2.0
- Marked playsound event for removal in BQ 2.0
- Marked title event for removal in BQ 2.0
### Removed
- Removed Deprecated Exceptions
- Removed RacesAndClasses support
- Removed LegendQuest support
- Removed BoutifulAPI support
- Removed the CLAY NPC
- removed legacy material support
- removed BetonLangAPI support
- removed PlayerPoints support (this can still be used via Vault)
### Fixes
- event priority for block objective
- linebreaks in strings
- notify:1 for block objective did not work
- asynchronous database access for objectives
- Renaming an NPC will not cause an NPE for a NPC Hologram
- Objective 'craft' now supports shift-clicking
- Fixed generation of default package
- fixed line breaks
- fixed events notify interval of 1
- fixed potion/brew objective notify
- fixed the bug and removed its workaround when chest converationIO has no available start points
- fixed journal line breaking
- fixed movement of movenpc event
- fixed npcmove event
- fixed a bug, where a player causes an exception when he spams right left clicks in menu conversationIO
- fixed outdated Brewery dependency
- fixed message duplication when using the packet interceptor
- fixed Journal interaction with Lectern
- fixed QuestItems ignoring durability
- fixed QuestItem interaction with Lectern, Campfire and Composter
- update journal after closing magic inventory
- fixed lever event not toggling the lever
- fixed ConcurrentModificationException in PlayerData
- fixed issue where the PacketInterceptor prints the message tag in the chat
- fixed database backups breaking with some languages
- fixed when PlaceholderAPI variables contains dots
- fixed quester name not support & as color code
- fixed Region Objective listen to player teleport event
- packet Interceptor stops 1 second AFTER the end of the conversation to allow slow messages to still have its chat protection
- fixed notify couldn't use variables that contain `:`
- improved stability for brew objective when other plugins affect brewing
- fixed region and npcregion condition
- fixed debugging does not start on server startup
- fixed ghost holograms caused by reloading BQ
- fixed deadlock (Server crash) in Conversations with a large amount of npc and player options with a large amount of conditions
- fixed door event not working correctly
- fixed `1 give` command exceptions
### Security
- fixed issue, where objectives that count things are out of sync with the database. This has also affected BungeeCord support

## [1.11.0] - 2020-01-02
### Added
- Support Minecraft 1.8 - 1.13.2+
- New Block Selector to select blocks by material and attributes. Can use wildcards as well.
- New 'mooncycle' condition - Determine what phase the moon is in
- Chest ConversationIO can now be configured to show NPC text per option.
- New 'extends' keyword in conversation to allow inheritance
- New 'conversation' condition that will return true if there is at least one conversation option available to an NPC
- New 'nujobs_canlevel' condition - True if player can level in Jobs Reborn
- New 'nujobs_hasjob' condition - True if player has job in Jobs Reborn
- New 'nujobs_jobfull' condition - True if a job is full in Jobs Reborn
- New 'nujobs_joblevel' condition - True if player has level in Jobs Reborn
- New 'nujobs_addexp' event - Add experience to player in Jobs Reborn
- New 'nujobs_addlevel' event - Add a level to player in Jobs Reborn
- New 'nujobs_dellevel' event - Remove a level from player in Jobs Reborn
- New 'nujobs_joinjob' event - Joins a player to a job in Jobs Reborn
- New 'nujobs_leavejob' event - Leaves a job in Jobs Reborn
- New 'nujobs_setlevel' event - Set a player's level in Jobs Reborn
- New 'nujobs_joinjob' objective - Triggers when player joins job in Jobs Reborn
- New 'nujobs_leavejob' objective - Triggers when a player leaves job in Jobs Reborn
- New 'nujobs_levelup' objective - Triggers when a player levels up in Jobs Reborn
- New 'nujobs_payment' objective - Triggers when a player receives money from Jobs Reborn
- New Notification System
- New 'notify' event - Create custom notifications on the ActionBar, BossBar, Title, Subtitle and Achievement
- New 'menu' conversation IO - Requires ProtocolLib. See: https://www.youtube.com/watch?v=Qtn7Dpdf4jw&lc
- New 'packet' chat interceptor - Requires ProtocolLib.
- new '/q debug' command - Enable or disable the debug mode
### Changes
- Event 'effect' can have 'ambient', 'hidden' and 'noicon' parameters
- Event 'effect' has '--ambient' parameter deprecated with a non fatal warning.
- Priority for 'journal_main_page' entries not unique anymore, it only orders the entries. Same priority sort it alphabetic
- Objective 'interact' can have 'loc', 'range' parameters
- Objective 'region' can optionally have 'entry' and/or 'exit' to only trigger when entering or exiting named region
- The old 'Debug' class was replaced by a more useful and powerful 'LogUtils' class
### Fixed
- Resolve variables in journal pages.
- WATER and LAVA can be specified in Action Objective
- Journals without dates now don't leave blank lines
- Journal separator can be disabled or customized
- NPCs now spawn correct, if they have a npc_hologram
- fixed NPE when no journal entry exists
- The default package is now compatible with all versions

## [1.10] - 2019-09-16
- Development versions can be full of bugs. If you find any, please report them on GitHub Issues.
- This version is only compatible to Shopkeepers v2.2.0 and above
### Added
- npc holograms above the head that follow the npc (requires HolographicDisplays)
- New 'facing' condition - check if player is facing a direction 
- New 'looking' condition - check if player looks at a block
- New 'deleffect' event - delete potion effects of a player
- New '%citizen%' variable - display a npcs name or coordinates (requires Citizens)
- New 'npcrange' objective - player has to go towards a npc (requires Citizens)
- New 'npcdistance' condition - check if a player is close to a npc (requires Citizens)
- New 'npclocation' condition - check if a npc is at a location (requires Citizens)
- New 'npcregion' condition - check if a npc is inside a region (requires Citizens & WorldGuard)
- New 'killmob' event - remove the mobs that you spawned with 'spawn' event
- New '/q version' command - get the version used
- New 'partialdate' condition - check if the date matches a pattern
- New 'dayofweek' condition - check if its weekend or monday
- New 'realtime' condition - check if its a specific time
- New 'xp' event - give a player xp.
- Global objecties (objectives that are active for all players directly after start)
- Global tags and points (tags ad points that are not set for one specific player)
- New 'globaltag' event 
- New 'globaltag' condition
- New 'globalpoint' event 
- New 'globalpoint' condition
- New 'opsudo' event - Sudo commands with op permissions
- Brewery integration ('drunk', 'drunkquality' and 'hasbrew'conditions, 'givebrew' and 'takebrew' events) 
- New 'title' event - display titles without the whole command hassle
- New 'playsound' event - plays a sound
- New 'fly' condition - check if the player is flying with Elytra
- New 'biome' condition - check the player's current biome
- New 'interact' objective - interact with an entity
- Conversations can individually override conversation IO type
- NPCs can be individually hidden from players if ProtocolLib is installed
### Changes
- 'compass' event can now directly set a players compass
- holograms from HolographicDisplays now can display items
- 'movenpc' event now allows multiple locations to create a path
- 'enchant' objective now allows multiple enchantments
- 'particle' event can now create client side only particles
- 'chest' converstionIO now dosn't display messages to chat for the old behavior use 'combined'
- 'money' event can now notify you about how much you recieved
- 'mmobkill' objective now allows multiple mobs
- Translation system is integrated with BetonLangAPI
- NPC heads in "chest" conversation IO will display correct Citizens skin
- NPC particles (EffectLib integration) can be displayed to individual players
- Condition command allows checking static conditions
- 'testforblock' condition can now check for specific data value
- 'delay' objective and 'folder' event accept more time units
- 'password' objective also accepts commands
- Commands can be tab-completed
### Fixed
- Fixed bug where players could take out items from the chest conversationIO
- Removed possibilities of dropping/transfering quest items and the journal
- Lots of smaller bugfixes

## [1.9.6] - 2017-11-27
### Fixed
- Update version to 1.9.6

## [1.9.5] - 2017-11-27
### Fixed
- Fixed global locations loading before the worlds
- Fixed loading order of Citizens/EffectLib integration
- Fixed restarting of persistent objectives not working correctly
- Fixed "unbreakable" tag not being read from items

## [1.9.4] - 2017-11-02
### Fixed
- Fixed broken integration loading

## [1.9.3] - 2017-11-01
### Fixed
- NPC and mob kills will be correctly registered when killed by indirect means
- Replaced error with a nice message when config updating fails to start
- Unbreakable items are no longer breakable in newer Spigot releases
- Moved compatibility hooks to the first server tick to hook into lazy plugins
- Colors of text in "chest" conversations are now correctly applied over text breaks
- Added a nice message when conversation option is missing "text"
- Fixed a rare crash when NPC was stopped and its target was outside loaded chunks
- Fixed checking item amounts in the backpack
- Allowed negative data in items for compatibility with dark magics
- Removed Denizen script checking, since it didn't work sometimes

## [1.9.2] - 2017-07-09
### Fixed
- Conversations won't allow taking items from GUI windows
- When using wrong 'point' or 'item' variable there will be a nice error message
- NPCs can be safely despawned while in the middle of a conversation
- Error on '/q reload' when NPC particles are disabled is now gone
- Items for compass buttons are now correctly loaded
### Changes
- These events are now correctly persistent: clear, explosion, lightning, setblock, spawn
- BetonQuest is using bStats instead of McStats

## [1.9.1] - 2017-04-18
### Fixed
- Holograms are now correctly loaded

## [1.9] - 2017-04-03
Notes:
- This version breaks compatibility with plugins hooking into BetonQuest. I'm sorry for that. Ask devs to update these plugins.
- The error reporting feature was improved. If you see a lot of error messages when reloading the plugin (not stack traces, just regular, human-readable messages), it's probably because there are real problems in your quests.
- BetonQuest won't accept ".yml" extensions at the end of conversation names in "main.yml". If your conversations aren't working (the plugin says they don't exist), check if you have these extensions IN THE "MAIN.YML" file and remove them.
### Fixed
- 'action' objective now detects fire interaction
- 'empty' condition now skips armor and off-hand slots
- Items can be used cross-package
- New sound names are now used by default
- Fixed doubled quest items when dropping them is blocked by another plugin
- Lore and name now appear on heads and written books with custom data
- Fix error when trying to add air (empty hand) with "/q item" command
- The main page now can exceed a single page in the journal
- The plugin will reconnect to the database if something goes wrong
- Fishing objective now only accepts stuff from water
- Properties in 'mobkill' objective (left and amount) has switched places
### Changes
- Complete rewrite of item conditioning - read the docs to discover new features (previous syntax is still working without any behavior changes)
- Books in items.yml now automatically wrap pages, like the journal and main page
- Main page and entries in the journal can manually split pages with '|' character
- New lines in conversations can be made with "\n"
- Interval in 'delay' objective is now configurable
- 'craft' and 'potion' objectives now use items defined in items.yml file
- Potion items are now defined with 'type:' argument instead of data value
- You can now use spaces between "first" options in conversations
- Static events can now be fired with "/q event - eventID" command
- Locations can have vectors defined directly in instruction strings
- Locations can be variables which resolve to location format
- Point condition can now check exact point amount with 'equal' argument
- In 'chest' conversation IO items can be specified with durability values after a colon
- Mobs spawned with 'spawn' event can have armor, items in hands and custom drops
- Unbreakability of quest items can be disabled (if you want to use "unbreakable" tag instead)
- Ranges in locations are now a separate argument ("10;20;30;world;4" is now "10;20;30;world 4")
- "main.yml" is now the only required file in the package. Empty files can be deleted
- Custom settings (i.e. EffectLib particle effects) are moved from "main.yml" to "custom.yml"
### Added
- Compatibility with Shopkeepers ('shopkeeper' event, 'shopamount' condition)
- Compatibility with PlaceholderAPI ('ph' variable and 'betonquest' placeholder)
- Compatibility with HolographicDisplays (holograms visible based on conditions)
- Compatibility with RacesAndClasses (race, class, exp, level, mana conditions/events/variables)
- Compatibility with LegendQuest (race, class, attribute, karma conditions/variables)
- Compatibility with WorldEdit ('paste' a schematic event)
- New condition 'riding' - check if the player is riding an entity
- New condition 'world' - check the world in which the player is
- New condition 'gamemode' - check player's game mode
- New condition 'achievement' - check if the player has an achievement
- New condition 'variable' - check if a variable matches a pattern
- New event 'lever' - switches a lever
- New event 'door' - opens/closes doors, trapdoors and gates
- New event 'if' - run one of two events, depending on condition
- New event 'movenpc' - move Citizens NPC to a location
- New event 'variable' - set a variable in "variable" objective
- New objective 'vehicle' - entering a vehicle entity
- New objective 'variable' - lets players define their own variables for you to use
- New objective 'kill' - kill players who meet specified conditions
- New objective 'breed' - breed animals (only 1.10.2+)
- New variable '%location%' - resolves to player's location
- Keyword "unbreakable" can be used in items to make them unbreakable
- When a conversation option is selected, a Bukkit event is called (for developers)
- Chat can be paused while in conversation, it will display when finished
- Objectives can be completed for players with "/q objective player complete"
- Option 'full_main_page' controls if the main page is a separate page in the journal
- Mobs spawned with 'spawn' can be "marked"; you can require marked mobs in 'mobkill' objective
- Firework support in items
- Relative package paths, where '_' means "one package up"

## [1.8.5] - 2016-05-14
### Fixed
- Objectives are now correctly deleted with "objective delete" event and do notreappear after "/q reload".
- Objectives are no longer duplicated in the database when using "/q reload".

## [1.8.4] - 2016-05-06
### Fixed
- Conversations are no longer started twice

## [1.8.3] - 2016-05-06
### Fixed
- Events are no longer run in async thread when completing "password" objective
- Replaced stacktrace with error message when objective is incorrect in "objective" event
- Made color codes work with "one_entry_per_page" setting enabled
- Fixed a bug where taken backpack items were not removed from the database
- Quest items can now be equipped
- "die" objective now correctly handles damage done to the player
- Fixed error when conversation is started without any possible options
- Fixed error when killing NPCs with equipment
- Fixed problems with relogging while in conversations with "stop" option enabled
- Fixed error when loading corrupted item from the database
### Changes
- Updater is now based on GitHub Releases, no longer downloads major updates automatically, it is more configurable and can also download development versions with "/q update --dev"
### Added
- Added console message about the cause of "/q give" errors (tells you what is wrong with item instruction string)

## [1.8.2] - 2016-02-18
### Fixed
- Fixed NPE when killing a mob without any "mobkill" objectives

## [1.8.1] - 2016-02-18
### Fixed
- Removing journal entries from the database now works correctly
- MobKill objective now correctly handles kills
- Nested package names are now correctly resolved
- The formatting at the end of every main page line is reset
- Fixed Apache dependency problem
- Material name is no longer displayed in "chest" GUI conversations
- Fixed "notify" option in give/take events

## [1.8] - 2016-02-13
Notes:
- As always in big updates, compatibility with plugins hooking into BetonQuest is broken. You need to check if everything is working.
### Fixed
- Die objective now reacts to death caused by other plugins
- Static events now are started correctly
- Static events now are canceled correctly
- Action objective now correctly checks locations
- Combat tag is removed after death
- Block, Craft and MythicMobs MobKill objectives now correctly save data
- Take event now correctly takes items from inventory, armor slots and backpack
### Added
- New variable system in conversations (check out the documentation)
- More options for journal, including one entry per page and removing date
- Compatibility with mcMMO (level condition and experience event)
- Compatibility with EffectLib ('particle' event, NPC particles)
- Compatibility with PlayerPoints (points event and condition)
- Compatibility with Heroes (class and skill condition, experience event, Heroes kills in 'mobkill' objective)
- Compatibility with Magic ('wand' condition)
- Compatibility with Denizen (running task scripts with 'script' event)
- Compatibility with SkillAPI (class and level condition)
- Compatibility with Quests (checking for done quests, starting them, custom event reward, custom condition requirement)
- Optional prefix for conversations (contributed by Jack McKalling)
- Optional material for buttons in "chest" conversation IO
- Configurable main page in the journal
- New argument in objectives: "persistent" - makes them repeat after completing
- New condition 'check' - allows for specifying multiple instructions in one
- New condition 'objective' - checks if the player has an active objective
- New condition 'score' - check scores on scoreboards
- New condition 'chestitem' - checks if a chest contains items
- New event 'run' - allows for specifying multiple instructions in one
- New event 'givejournal' - gives journal to the player
- New event 'sudo' - forces the player to run a command
- New event 'compass' - point player's compass to a location
- New event 'cancel' - cancels a quest (as in main.yml)
- New event 'score' - modify scores on scoreboards
- New events 'chestgive', 'chesttake' and 'chestclear' - put and remove items in chests
- New objective 'logout' - the player needs to leave the server
- New objective 'password' - the player needs to type the password in the chat
- New objective 'fish' - catching fish
- New objective 'enchant' - enchanting an item
- New objective 'shear' - shearing a sheep
- New objective 'chestput' - putting items in a chest
- New objective 'potion' - brewing a potion
- New commands: /cancelquest and /compass - directly open backpack sub-pages
- New subcommand '/q delete' - delete all specific tags/points/objectives/entries
- New subcommand '/q rename' - rename all specific tags/points/objectives/entries
- New subcommand '/q give' - gives you an item from items.yml
### Changes
- Administrative messages are now English-only in new installations
- Journal event can remove entries from the journal
- In conversations, %quester% variable changed to %npc%
- In inventory GUI there is NPC's text in every option, for convenience
- Conversations can point to NPC options in other conversations within the package
- You can use spaces between events, conditions and pointers in conversations
- All tags and points are internally associated with a package now
- Some conditions are now static and persistent (just like events)
- Point event can now multiply points
- Vault Money event can now multiply money
- Journal event can now use "update" argument for updating variables on the main page
- Packages can now be moved to another directories
- Quest cancelers are now defined in a more convenient way
- /q command renamed to /betonquest, /j to /journal; previous forms are now aliases
- Conditions and events in objective instructions (and conditions in event instructions) can now be defined with "condition:" and "event:" argument (without "s" at the end)

## [1.7.6] - 2015-10-17
### Fixed
- Conversation can no longer be started multiple times at once if it happens on the same tick
### Added
- Dutch translation by Jack McKalling

## [1.7.5] - 2015-09-12
### Fixed
- Restored compatibility with MythicMobs 2.1.0

## [1.7.4] - 2015-08-29
### Fixed
- Fixed error when player was quitting with active "stop" conversation while he had not changed his language with /ql command
### Changes
- Inventory GUI will close itself if there's nothing left to display

## [1.7.3] - 2015-08-20
### Fixed
- Combat tagging does not work if the attack has been canceled
### Changes
- Options in conversation can also be defined using "event:", "condition:" and "pointers:" argument names (with and without 's' at the end). "text:" argument is unchanged.

## [1.7.2] - 2015-07-27
### Fixed
- "mobkill" objective now displays correct amount of mobs left to kill
- "delay" objective can be set to 0 delay

## [1.7.1] - 2015-07-19
### Fixed
- Quests are loaded after other plugins register their types
- Journal condition correctly resolves package names
### Changes
- Updated French translation

## [1.7] - 2015-07-17
Notes:
- BetonQuest no longer supports servers without UUID handling
- There were a lot of changes since previous version, check carefully if everything is working
- Compatibility with plugins hooking INTO BetonQuest is broken, they need to update
### Fixed
- Objectives no longer mysteriously double events
- Greatly improved performance in almost every aspect
- Finally fixed issues with special characters on some servers
- Fixed database saving/loading issues
- Fixed player options in conversations being white on next lines when using tellraw
### Added
- Quest canceling system
- New inventory GUI for conversations
- Added the "random" parameter in "folder" event - choose randomly X events to fire
- Action objective can be "canceled" - the click will not do anything
- Added "static events" mechanism for firing events at specified time of the day
- Optional message when the player is pulled back by stop option
- Optional message for take and give events
- Optional message when advancing in "block" and "mobkill" objectives
- Variable system for quick changing quest parameters (for example location of a quest)
- "/q vector" command for easy calculating location vector variables
- New "empty" condition - amount of empty inventory slots
- New "party" condition - manages the conditions in the party
- New "monsters" condition - true if there are monsters in the area
- New "clear" event - kills specified monsters in the area
- New "region" objective - reach WorldGuard region
- Blacklist of commands which cannot be used while in conversation
- Option to disable compatibility with other plugins
- Added remove_items_after_respawn option - for servers using keepInventory gamerule
### Changes
- The plugin now uses package system: configuration has been moved into "default" package
- Objectives has returned to "objectives.yml" - it's improving performance
- The database is now updated in real time
- All quests can (but don't have to) be translated into multiple languages
- Players can change their language with /questlang command
- Conversations with stop option are resumed when the player logs out and in again
- Metrics are now toggled in PluginMetrics/config.yml
- All conditions, events, objectives, conversations etc. are loaded when the plugin starts/reloads
- Citizens NPC will stop when talked to
- Quest blocks cannot be placed, quest items will not break
- Conversations cannot be started while in combat
- Cannot fight while in conversation
- Tellraw conversations no longer spam the console
- Mobs can be spawned with a name (spawnmob event, "name:" argument)
- /q command is now more beautiful
- Removed unnecessary argument prefixes from conditions and events
- Removed "tag:" from objective instruction strings
- Conversations no longer need those empty lines everywhere ('')
- Dependencies updated: WorldGuard/WorldEdit 6.1, MythicMobs 2.0.4

## [1.6.2] - 2015-04-10
- Fixed errors on data loading when MySQL is being used.
- Changes messages system to use simple file as default. If you want to use advanced translation rename "advanced-messages.yml" to "messages.yml".

## [1.6.1] - 2015-03-26
- Fixed errors on updating journals when using MySQL.

## [1.6] - 2015-03-16
Notes:
- There is a bug/feature in 1.8 which adds '§0' at the end of every line in books generated by plugins. This breaks the conditions/events based on books with more than one line of text. The detailed instruction on how to work it around is in "Other important stuff" chapter, in the part about items.
### Fixed
- Items given by event that don't fit in the inventory will now drop instead of being deleted This does not apply to quest items, they will be added to backpack
- Events fired from conversations won't throw async errors
- Conversation can be started after plugin's reload without relogging
- /q reload no longer lags the server
- Corrected description in /q command
- Added input validation for global locations - if event is incorrect it will display an error instead of breaking the whole functionality
- The plugin should run fine on machines not supporting some special characters
- Inverted item condition now behave correctly
- Time condition now checks time correctly
### Added
- Added backpack for storing quest items, which cannot be dropped in any way
- Added database backups
- Added prefix for the database. New installations will use "betonquest_" prefix for tables, existing configuration will use empty prefix to maintain compatibility with other programs
- Players can chat while in conversations by prefixing their messages with '#' character
- New "random" condition - true with specified probability
- New "sneak" condition - true if player is sneaking
- New "journal" condition - true if player has journal entry
- New "testforblock" condition - true if block at given location matches given material
- New "arrow" objective - completed when arrow hits the specified target
- New "experience" objective - completed when player reaches certain level
- New "npcinteract" objective - completed when player right-clicks Citizens NPC
- New "damage" event - damages the player
- Skript support (event, effect and condition)
- WorldGuard support (region condition)
- Errors are logged to the "error.log" file in "logs" directory
- Debug option in config.yml for logging plugin's activity to "debug.log" file
- New commands for opening backpack: b, bb, backpack, bbackpack or betonbackpack
- Items are now aware of leather armor color, head owner and enchantments in books
### Changes
- Added and changed a lot of subcommands in /q command:
    - event and condition can be run for every online player
    - tag, point, objective and (new) journal can edit every (even offline) player
    - config (new) can set configuration files from command line
    - backup (new) backups the whole configuration and database
- Folder event now runs these events even after the player logs out: command, tag, objective, delete, point, setblock
- Changed /j command to open the backpack instead of just giving the journal
- Tellraw clicking on options in conversation now ignores old (used) options
- Using color codes in journal entries is now possible
- Give/take events and item condition can now check for multiple items with syntax 'give stick:2,stone:4,sword'
- Give/take events and item/hand conditions can now check for items only without enchantments/effects/name/lore etc.
- Inverting conditions is now done by prefixing their name with "!" (in the place where you use them, like conversation, not in conditions.yml)
- Configuration updater is no longer based on plugin's version
- Backup files are now kept in "backups" directory, old ones are moved to it
- Changed internal structure of the code (may matter to developers - QuestEvent, Condition and Objective classes have been moved from "core" package to "api", update your imports)

## [1.5.4] - 2015-03-12
- This version is almost the same as 1.5.3. The only difference is that it can load database backups created by 1.6 version. When updating to 1.6, the database format will change, so it won't be possible to go back, unless by loading the backup using this version of the plugin.

## [1.5.3] - 2014-12-26
- Small fix of /q purge command not working on offline players.

## [1.5.2] - 2014-12-23
- Fixed errors that were spamming the console when a player with active Location objective was teleporting to other worlds.

## [1.5.1] - 2014-12-22
### Changes
- Multiple tags in one event are now possible
- Change /q event command to run from console
- Add color codes to item's name and lore
- Fix "stop" option in conversations not working
- Fix NPE on unknown answer in conversations

## [1.5] - 2014-12-21
### Changes
- Added support for MythicMobs and Vault (see wiki for more info)
- AutoUpdater is now enabled by default! If you want you can change this and reload the plugin, nothing will be downloaded in that case
- Book saving format has changed. All books were automatically converted, but you need to check them if everything looks like it's supposed to.
- Command event accepts multiple commands separated by "|", eg. "command say beton|say quest"
- Event command now accepts optional <name> argument at the end; this will fire event for <name> player. eg. "/q event wood_reward Steve"
- Journal title and lore can now use colors (&4 etc.) and journal is colorful; options in config.yml
- Added aliases for /q command: bq, bquest, bquests, betonquest, betonquests, quest, quests
- Added aliases for /j command: bj, journal, bjournal, betonjournal
- Objectives are now defined directly in event instruction, not in objectives.yml (which was deleted, if you want to restore something check the backup)
- Replies in conversations are now optionally clickable (tellraw option in config.yml)
- Added permission for starting a conversation: betonquest.conversation
- Conversation starting/ending, updating journal, plugin's update and full inventory can now make sounds; you can find a list of possible values here: jd.bukkit.org/rb/apidocs/org/bukkit/Sound.html
- Conditions for events are now defined as 'event_conditions:' instead of simply 'conditions:'. This is to distinguish conditions for objectives and for events, as both of them can exist in one instruction
- Updater is now run when disabling the plugin (it does matter if your server restarts every night)
Notes:
- All Objective events has been converted to new format. The objectives.yml file has been deleted, so if it contained any objectives not covered by an event they may seem lost. However there is a backup file and you can extract everything from it. Please refer to the wiki to learn how objectives are now defined or just study converted ones (it's pretty straightforward).
- AutoUpdater is now enabled by default. Every future update will be working exactly like before, all changes will be automatically updated by a converter, there is always a backup and you are informed about all changes in this file. So it's pretty safe to say that keeping this plugin up to date won't give you any trouble. If you don't want to have latest fixes and features you can disable updating, but this will make the developer sad.
- Because of changes in how books behave since 1.8, you may experience some strange bugs with saving books to items.yml. Generally, you should open a book before saving it using /q item command. And don't start or end your books with " character, as it's part of a workaround of this bug/feature.

## [1.4.3] - 2014-12-15
- Removed debug messages from ActionObjective. You could have told me, any of you guys...

## [1.4.2] - 2014-12-09
- Really fixed an updater.

## [1.4.1] - 2014-12-09
- Fixed few bugs in Action objective.
- Fixed updater, hopefully.

## [1.4] - 2014-12-07
### Changes
- Conversations are now divided into multiple files in "conversations" directory
- Items are now saved to items.yml file and referenced by "take", "give", "item" and "hand" events/conditions
- Added /q item <itemID> command which saves currently held item to the config as specified itemID
- Added location to Action objective, which checks the location of the block (unlike location condition which checks the location of the player)
- Added /q event <eventID> command which fires specified event
- Fixed multiple bugs with conversation starting and ending
- Block NPCs can now be used with Citizens enabled
- Added NPCKill objective for killing NPCs
- Added SetBlock event for setting a block at specified location
- Improved Material matching in configs
- Modified Action objective for greater flexibility:
    - It is now possible to detect clicking in air
    - It is no longer possible to detect clicking on any block (as this accepts clicking on air)
    - It can be used to detect book reading (with the help of updated Hand condition)
- Added AutoUpdater; it's disabled by default
Notes:
- Conversion of configuration should have been done automatically, you don't have to worry about anything. If something went wrong, you can revert changes from generated backup file, which contains all your previous configs.
- You can enable AutoUpdater by setting "autoupdate" to true in config.yml. It is completely safe because all next versions will generate backups and convert all files automatically. You will be notified on joining the server about the new changelog file.
- Please refer to the wiki for changes in formatting instruction strings for various things: https://github.com/Co0sh/BetonQuest/wiki
- You probably should also change the names of converted items to something else than "item12". But that works too.

## [1.3] - 2014-11-30
### Changes
- UUID support (optional)
- NPCs made from a clay block, head and sign, for servers without Citizens2 plugin
- Global, long and persistent delay for events (as an objective)
- Folder event for multiple events, with optional short delay
- French translation (thanks to fastlockel)
- If you want to convert names to UUIDs run the plugin once and then change in the config "uuid: false" to true. Do not touch the "convert: true" option unless you want your database wiped! Conversion will happen on next plugin reload (eg. /q reload). This is not revertable!
- Remember to backup your config files before updating! It shouldn't destroy anything but you never know.

## [1.2] - 2014-11-23
- Global locations now automatically run only once, no need for blocking it with tags and conditions. They use however tags that follow the syntax "global_<tag>", where <tag> is global location objective tag.
- Added optional respawn location for canceled death objective, just add "respawn:100.5;200;300.5;world;90;0" to instruction string.
- Added German translation, thanks to coalaa!
- Added optional movement blocking while in conversation, add option "stop: true" or "stop: false" in every conversation.
- Changed priority of conversation chat event to lowest, should work even for muted players.
- Fixed data values in block objective.
- Added metrics, you can disable them by setting "metrics: false" in config.yml
- Added support for SQLite, plugin will use it when connecting to MySQL fails.
- Fixed death objective not working every time and not removing all effects.

## [1.1] - 2014-11-08
- Fixed many bugs including but not limited to:
    - negated conjunction condition
    - unnecessary debug messages
    - not working global locations
- Replaced config examples with default quest
- Leaving data values in item's definition will make plugin ignore data value in most cases
- Improved journal to stop text leaks
- Item names now replace _ with spaces

## [1.0] - 2014-11-06
- Initial release
