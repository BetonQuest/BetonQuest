# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).


## [Unreleased] - ${current-date}
### Added
- Tags and Objectives can now be removed with a static event for all players, even if they are not online
  * deletepoint event can now also be called to delete all points for all players
  * journal del event can now also be called as static
- Added integration for MMOGroup plugins (MMOCore, MMOItems, MMOLib)
  * MMOClass condition (type & class)
  * MMOProfession condition
  * MMOAttribute condition
  * MMOItems item condition (item in inventory)
  * MMOItems hand condition (item in main/offhand)
  * MMOLib stats condition  (a ton of stats from Core and Items combined)
- equal argument for condition 'empty'
- Condition 'wand' can now have an option '
- Implementing 1.15 support for Events and Conditions
- New Chat event, that write chat messages for a player
- Added 'pickup' objective
- Added stopnpc event, that will stop the movenpc event
- Added teleportnpc event, that will stop the movenpc event and teleport the npc to a given location
- Added option check_interval for holograms in custom.yml andd added GlobalVariable support
- Added deletepoint event to delete player points
- Added mythicmobdistance condition that will check if a specific MythicMobs entity is near the player
- Added level argument to 'experience' objective and condition
- Added prefix argument in password objective
- Added level argument to 'experience' objective and condition
- Added prefix argument in password objective
- Added fail argument in password objective
- Added notify option to point event
- Added an interceptor that does not intercept: 'none'
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
### Deprecated
### Removed
- Removed Deprecated Exceptions
- Removed RacesAndClasses support
- Removed LegendQuest support
- Removed BoutifulAPI support
- Removed the CLAY NPC
- removed legacy material support
- removed BetonLangAPI support
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
- fixed workaround when chest converationIO has no available start points
- fixed journal line breaking
- fixed movement of movenpc event
- fixed npcmove event
- fixed bug, where a player causes an exception when he spams right left clicks in menu conversationIO
- fixed outdated Brewery dependency
- fixed message dublication when using the packet interceptor
- fixed Journal interaction with Lectern
- fixed QuestItems ignoring durabilty
- fixed QuestItem interaction with Lectern, Campfire and Composter
- fixed journal update after magic inventory is closed
- fixed lever event not toggling the lever
- fixed ConcurrentModificationException in PlayerData
- fixed issue where the PacketInterceptor prints the message tag in the chat
### Security
- fixed issue, where objectives that count things are out of sync with the database. This has also affected BungeeCord support

## [1.11.0] - 2020-01-02
### Added
- Support Minecraft 1.8 - 1.13.2+
- New Block Selector to select blocks by material and attributes. Can use wildcards as well.
- New 'mooncycle' condition - Determine what phase the moon is in
- Chest ConversationIO can now be configured to show NPC text per option.
- New 'extends' keyword in conversation to allow inheritance
- New 'conversation' condition that will return true if there is at least 1 conversation option available to an NPC
- New 'nujobs_canlevel' condition - True if player can level in Jobs Reborn
- New 'nujobs_hasjob' condition - True if player has job in Jobs Reborn
- New 'nujobs_jobfull' condition - True if a job is full in Jobs Reborn
- New 'nujobs_joblevel' condition - True if player has level in Jobs Reborn
- New 'nujobs_addexp' event - Add experience to player in Jobs Reborn
- New 'nujobs_addlevel' event - Add a level to player in Jobs Reborn
- New 'nujobs_dellevel' event - Remove a level from player in Jobs Reborn
- New 'nujobs_joinjob' event - Joins a player to a job in Jobs Reborn
- New 'nujobs_leavejob' event - Leaves a job in Jobs Reborn
- New 'nujobs_setlevel' event - Set a players level in Jobs Reborn
- New 'nujobs_joinjob' objective - Triggers when player joins job in Jobs Reborn
- New 'nujobs_leavejob' objective - Triggers when a player leaves job in Jobs Reborn
- New 'nujobs_levelup' objective - Triggers when a player levels up in Jobs Reborn
- New 'nujobs_payment' objective - Triggers when a player is paid in Jobs Reborn
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
- Global objecties (objectives that  are active for all players directly after start)
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
- 'chest' converstionIO now dosn't display messages to chat for the old behaviour use 'combined'
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
- Fixed a rare crash when NPC was stopped and its target was outside of loaded chunks
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
- Main page now can exceed a single page in the journal
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
- Changes messages system to use simple file as default. If you want to use advanced translation just rename "advanced-messages.yml" to "messages.yml".

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
- Inverting condition is now done by prefixing their name with "!" (in the place where you use them, like conversation, not in conditions.yml)
- Configuration updater is no longer based on plugin's version
- Backup files are now kept in "backups" directory, old ones are moved to it
- Changed internal structure of the code (may matter to developers - QuestEvent, Condition and Objective classes has been moved from "core" package to "api", update your imports)

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
- Books saving format has changed. All books were automatically converted, but you need to check them if everything looks like it's supposed to.
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
- All Objective events has been converted to new format. The objectives.yml file has been deleted, so if it contained any objectives that weren't covered by an event they may seem lost. However there is a backup file and you can easily extract everything from it. Please refer to the wiki to learn how objectives are now defined or just study converted ones (it's pretty straightforward).
- AutoUpdater is now enabled by default. Every future update will be working exactly like before, all changes will be automatically updated by a converter, there is always a backup and you are informed about all changes in this file. So it's pretty safe to say that keeping this plugin up to date won't give you any trouble. If you don't want to have latest fixes and features you can disable updating but this will make the developer sad.
- Because of changes in how books behave since 1.8 you may experience some strange bugs with saving books to items.yml. Generally you should open a book before saving it using /q item command. And don't start or end your books with " character, as it's part of a workaround of this bug/feature.

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
- Added location to Action objective, which checks the location of the block (unlike location condition which checks location of the player)
- Added /q event <eventID> command which fires specified event
- Fixed multiple bugs with conversation starting and ending
- Block NPCs can now be used with Citizens enabled
- Added NPCKill objective for killing NPCs
- Added SetBlock event for setting a block at specified location
- Improved Material matching in configs
- Modified Action objective for greater flexibility:
    - It is now possible to detect clicking in air
    - It is no longer possible to detect clicking on any block (as this accepts clicking on air)
    - Can be used to detect book reading (with help of updated Hand condition)
- Added AutoUpdater; it's disabled by default
Notes:
- Conversion of configuration should have been done automatically, you don't have to worry about anything. If something went wrong you can revert changes from generated backup file, which contains all your previous configs.
- You can enable AutoUpdater by setting "autoupdate" to true in config.yml. It is completely safe because all next versions will generate backups and convert all files automatically. You will be notified on joining the server about new changelog file.
- Please refer to the wiki for changes in formatting instruction strings for various things: https://github.com/Co0sh/BetonQuest/wiki
- You probably should also change names of converted items to something else than "item12". But that works too of course.

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
- Added optional respawn location for cancelled death objective, just add "respawn:100.5;200;300.5;world;90;0" to instruction string.
- Added German translation, thanks to coalaa!
- Added optional movement blocking while in conversation, just add option "stop: true" or "stop: false" in every conversation.
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
