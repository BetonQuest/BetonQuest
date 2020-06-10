# Reference

This chapter describes all aspects of BetonQuest in one place. You should read it at least once to know what you're dealing with and where to search for information if you ever have any problems.

## Conversations

Each conversation must define name of the NPC (some conversations can be not bound to any NPC, so it’s important to specify it even though an NPC will have a name) and his initial options.
```YAML
quester: Name
first: option1, option2
stop: 'true'
final_events: event1, event2
interceptor: simple
NPC_options:
  option1:
    text: Some text in default language
    events: event3, event4
    conditions: condition1, !condition2
    pointers: reply1, reply2
  option2:
    text: '&3This ends the conversation'
player_options:
  reply1:
    text:
      en: Text in English
      pl: Tekst po polsku
    event: event5
    condition: '!condition3'
    pointer: option2
  reply2:
    text: 'Text containing '' character'
```

!!! note
    Configuration files use YAML syntax. Google it if you don't know anything about it. Main rule is that you must use two spaces instead of tabs when going deeper into the hierarchy tree. If you want to write `'` character, you must double it and surround the whole text with another `'` characters. When writing `true` or `false` it also needs to be surrounded with `'`. If you want to start the line with `&` character, the whole line needs to be surrounded with `'`. You can check if the file is correct using [this tool](http://www.yamllint.com).

* `quester` is name of NPC. It should be the same as name of NPC this conversation is assigned to for greater immersion, but it's your call.
* `first` are pointers to options the NPC will use at the beginning of the conversation. He will choose the first one that meets all conditions. You define these options in `npc_options` branch.
* `final_events` are events that will fire on conversation end, no matter how it ends (so you can create e.g. guards attacking the player if he tries to run). You can leave this option out if you don't need any final events.
* `stop` determines if player can move away from an NPC while in this conversation (false) or if he's stopped every time he tries to (true). If enabled, it will also suspend the conversation when the player quits, and resume it after he joins back in. This way he will have to finish his conversation no matter what. _It needs to be in `''`!_ You can modify the distance at which the conversation is ended / player is moved back with `max_npc_distance` option in the _config.yml_.
* `interceptor` optionally set a chat interceptor for this conversation. Multiple interceptors can be provided in a comma-separated list with the first valid one used.
* `NPC_options` is a branch with texts said by the NPC.
* `player_options` is a branch with options the player can choose.
* `text` defines what will display on screen. If you don't want to set any events/conditions/pointers to the option, just skip them. Only `text` is always required.
* `conditions` are names of conditions which must be met for this option to display, separated by commas.
* `events` is a list of events that will fire when an option is chosen (either by NPC or a player), defined similarly to conditions.
* `pointer` is list of pointers to the opposite branch (from NPC branch it will point to options player can choose from when answering, and from player branch it will point to different NPC reactions).

When an NPC wants to say something he will check conditions for the first option (in this case `option1`). If they are met, he will choose it. Otherwise, he will skip to next option (note: conversation ends when there are no options left to choose). After choosing an option NPC will execute any events defined in it, say it, and then the player will see options defined in `player_options` branch to which `pointers` setting points, in this case `reply1` and `reply2`. If the conditions for the player option are not met, the option is simply not displayed, similar to texts from NPC. Player will choose option he wants, and it will point back to other NPC text, which points to next player options and so on.

If there are no possible options for player or NPC (either from not meeting any conditions or being not defined) the conversations ends. If the conversation ends unexpectedly, check the console - it could be an error in the configuration.

This can and will be a little confusing, so you should name your options, conditions and events in a way which you will understand in the future. Don't worry though, if you make some mistake in configuration, the plugin will tell you this in console when testing a conversation. Also, study the default conversation included with the plugin to fully understand how powerful this system can be.

### Cross-conversation pointers

If you want to create a conversation with multiple NPCs at once or split a huge conversation into smaller, more focused files, you can point to NPC options in other conversation. Just type the pointer as `conversation.npc_option`.

Keep in mind that you can only cross-point to NPC options. It means that you can use those pointers only in `first` starting options and in all player options. Using them in NPC options will throw errors.

### Conversation variables

You can use variables in the conversations. They will be resolved and displayed to the player when he starts a conversation. A variable generally looks like that: `%type.optional.arguments%`. Type is a mandatory argument, it defines what kind of variable it is. Optional arguments depend on the type of the variable, i.e. `%npc%` does not have any additional arguments, but `%player%` can also have `display` (it will look like that: `%player.display%`). You can find a list of all available variable types in the "Variables List" chapter.

!!! note
    If you use a variable incorrectly (for example trying to get a property of an objective which isn't active for the player, or using %npc% in `message` event), the variable will be replaced with empty string ("").

### Translations

As you can see in default conversation, there are additional messages in other languages. That's because you can translate your conversations into multiple languages. The players will be albe to choose their preferred one with **/questlang** command. You can translate every NPC/player option and quester's name. You do this like this:

```YAML
quester:
  en: Innkeeper
  pl: Karczmarz
  de: Gastwirt
```

As said before, the same rule applies to all options and quester's name. The player can choose only from languages present in _messages.yml_, and if there will be no translation to this language in the conversation, the plugin will fall back to the default language, as defined in _config.yml_. If that one is not defined, there will be an error.

You can also translate journal entries, quest cancelers and `message` events, more about that later.

### Conversation displaying

By default BetonQuest uses the most native and safe way of displaying a conversation, which is the Minecraft chat. You choose the option by typing their number in. You can however change it with `default_conversation_IO` option in _config.yml_ file. Default value is `simple`. By changing it to `tellraw` you will add a possibility to click on options. Keep in mind that if the chat is quickly flowing, players will sometimes "miss" an option and click another one. There is a display type that doesn't suffer from this problem at all, it's called `chest`. It will display the conversation in an inventory GUI, where the NPC's text and options will be shown as item lore. Alternatively use `slowtellraw` which provides the npc responses line by line delayed by 0.5 seconds. If you have `protocollib` then you can use `menu`.

You can control the colors of conversation elements in the _config.yml_ file, in `conversation_colors` section. Here you must use names of the colors.

If you're using the `chest` display method you can change the option's item to something else than Ender Pearl by adding a prefix to that option's text. The prefix is a name of the material (like in _items.yml_) inside curly brackets, with optional damage value after a colon. Example of such option text: `{diamond_sword}I want to start a quest!` or `{wool:10}Purple!`

In case you want to use a different type of conversation display for a specific conversation you can add `conversationIO: <type>` setting to the conversation file at the top of the YAML hierarchy, which is the same level as `quester` or `first` options).

### Advanced: Extends
Conversation also supports the concept of inheritance. Any option can include the key `extends` with a comma delimited list of other options of the same time. The first option that does not have any false conditions will have it's text, pointers and events merged with the extending option. The extended option may itself extend other options. Infinite loops are detected.

```YAML
NPC_options:

  ## Normal Conversation Start
  start:
    text: 'What can I do for you'
    extends: tonight, today
    
  ## Useless addition as example
  tonight:
    # Always false
    condition: random 0-1
    text: ' tonight?'
    extends: main_menu

  today:
    text: ' today?'
    extends: main_menu

  ## Main main_menu
 main_menu:
   pointers: i_have_questions, bye
```
In the above example, the option _start_ is extended by both _tonight_ and _today_, both of whom are extended by _main_menu_. As _tonight_ has a false condition the _today_ option will win. The _start_ option will have the pointers in main_menu added to it just as if they were defined directly in it and the text will be joined together from _today_. If you structure your conversation correctly you can make use of this to minimize duplication.

### Chat Interceptors
During chat it can be distracting when messages from other players or system message interfere with the dialogue. A chat interceptor provides a method of intercepting these message and then playing them back later.

A default chat interceptor is specified in `config.yml` by setting `default_interceptor`. This defaults to `simple` for the simple interceptor. The chat interceptor can also be set per conversation though the use of `interceptor` key in the conversation. If you have `protocollib` installed then you can use the `packet` interceptor that should intercept anything. 

## Conditions, Events and Objectives

Conditions, events and objectives are defined with an "instruction string". It's a piece of text, formatted in a specific way, containing the instruction for the condition/event/objective. Thanks to this string they know what should they do. To define the instruction string you will need a reference, few pages below. It describes how something behaves and how it should be created. All instruction strings are defined in appropriate files, for example all conditions are in _conditions.yml_ config. The syntax used to define them looks like this: `name: 'the instruction string containing the data'`. Apostrophes are optional in most cases, you can find out when to use them by looking up "YAML syntax" in Google.

### Conditions

Conditions are the most versatile and useful tools in creating advanced quests. They allow you to control what options are available to player in conversations, how the NPC responds or if the objective will be completed. The reference of all possible conditions is down below.

You can negate the condition (revert its output) by adding an exclamation mark (`!`) at the beginning of it's name (in the place you use it, i.e. in conversations, not in the _conditions.yml_ file).

You can use conversation variables instead of numeric arguments in conditions. If the variable fails to resolve (i.e. it will return an empty string) BetonQuest will use 0 instead.

### Events

In certain moments you will want something to happen. Updating the journal, setting tags, giving rewards, all these are done using events. You define them just like conditions, by specifying a name and instruction string. You can find instruction strings to all events in the event reference. At the end of the instruction string you can add `conditions:` or `condition:` (with or without `s` at the end) attribute followed by a list of condition names separated by commas, like `conditions:angry,!quest_started`. This will make an event fire only when these conditions are met.

You can use conversation variables instead of numeric arguments in events. If the variable fails to resolve (i.e. it will return an empty string) BetonQuest will use 0 instead.

### Objectives

Objectives are the main things you will use when creating complex quests. You start them with a special event, `objective`. You define them in the _objectives.yml_ file, just as you would conditions or events. At the end of the instruction string you can add conditions and events for the objective. Conditions will limit when the objective can be completed (e.g. killing zombies only at given location in quest for defending city gates), and events will fire when the objective is completed (e.g. giving a reward, or setting a tag which will enable collecting a reward from an NPC). You define these like that: `conditions:con1,con2 events:event1,event2` at the end of instruction string . Separate them by commas and never use spaces! You can also use singular forms of these arguments: `condition:` and `event:`.

If you want to start an objective right after it was completed (for example `die` objective: when you die, teleport you to a special spawnpoint and start `die` objective again), you can add `persistent` argument at the end of an instruction string. It will prevent the objective from being completed, although it will run all its events. To cancel such objective you will need to use `objective delete` event.

Objectives are loaded at start-up, but they do not consume resources without player actually having them active. This means that if you have 100 objectives defined, and 20 players doing one objective, 20 another players doing second objective, and the rest objectives are inactive (no one does them), then only 2 objectives will be consuming your server resources, not 100, not 40.

## Packages

All the content you create is organized into packages. Each package must contain the _main.yml_ file with package-specific settings. Additionally it can have _conversations_ directory with conversation files, _events.yml_, _conditions.yml_, _objectives.yml_, _items.yml_ and _journal.yml_. The default package is called simply "default". It is always present, and if you delete it, it will be regenerated with a sample quest.

If you want, you can simply ignore the existence of packages and write all your quests in the default one. You will however come to a point, when your files contain hundreds of lines and it gets a little bit confusing. That's why it's better to split your quests into multiple packages, for example "main" for the quests in the main city, "dungeon" for some interesting dungeon story etc.

Each package can be disabled/enabled in the _main.yml_ file, by setting `enabled` to `true` or `false`.

It would be limiting if you couldn't interact between packages. That's why you can always access stuff from other packages by prefixing its name with package name. If you're writing a conversation in package `village` and you want to fire an event `reward` from package `beton`, you simply name the event as `beton.reward`. The plugin will search for `reward` in `beton` package instead of the one in which the conversation is defined. All events, conditions, objectives, items and conversations behave this way. Note that you can't cross-reference journal entries!

Packages can be nested together in folders. A folder can either contain packages or be a package, never both. The name of such nested package is prefixed by names of all folders, separated with a dash. This directory tree (_main.yml_ represents a package):

```
    BetonQuest/
    ├─default/
    └─quests/
      ├─village1/
      │ ├─quest1/
	  │ │ └─main.yml
      │ └─quest2/
	  │   └─main.yml
      └─village2/
        └─quest1/
		  └─main.yml
```

contains these packages:

- _default_
- _quests-village1-quest1_
- _quests-village1-quest2_
- _quests-village2-quest1_

### Relative paths

You don't have to always specify a full package name. In the example above, if you wanted to reference package `quests-village1-quest2` from `quests-village1-quest1`, you could write it as `_-quest2` - this means "from the current package (`quest1`) go up one time and find there package `quest2`". Name `_` has special meaning here. Analogously, if you wanted to reference `quests-village2-quest1` from that package, you would use `_-_-village2-quest1`. Using `_` twice will get you to `quests` package, and from there you're going into `village2` and then `quest1`.

Relative paths can be useful if you want to move your packages between directories. Instead of rewriting every package name to match current directory tree, you can use relative paths and it will just work. It can also be useful if you want to create a downloadable quest package which can be placed anywhere and not just on the root directory (_BetonQuest/_) or when working with BetonQuest-Editor + BetonQuestUploader combo - quest writers don't have to prefix every package call with their name (this feature will work soon, needs an update in the editor).

## Unified location formating

Whenever you want to define some location in your events, conditions, objectives or any other things, you will define it with this specific format. The location can consist of 2 things: base and vector. Only the base is always required.

The base is a core location. Currently there are two types: absolute coordinates and variables. Absolute coordinates are defined like `100;200;300;world`, where `100` is X coordinate, `200` is Y, `300` is Z and `world` is the name of the world. These can have decimal values. If you want you can also add two more numbers at the end, yaw and pitch (these are controlling the rotation, for example in teleportation event, both are needed if you decide to add them; example: `0.5;64;0.5;world;90;-270`).

To use a variable as the location's base it must resolve to a valid absolute coordinates. An example of such variable is `%location%`, which shows player's exact location. Simply place it instead of coordinates. There is one rule though: you can't use variable base types in events running without players (for example static events or the ones run from folder event after their player left the server). BetonQuest won't be able to resolve the location variable without the player!

The vector is a modification of the location. It will be useful if you use global variables instead of base (described in the next subsection) or the base itself is variable, like `player`. Vectors look like `->(10;2.5;-13)` and are added to the end of the base. This will modify the location, X by 10, Y by 2.5 and Z by -13. For example, location written as `100;200;300;world_nether->(10;2.5;-13)` will generate a location with X=110, Y=202.5 and Z=287 on `world_nether` world.

## Global variables

You can insert a global variable in any instruction string. It looks like this: `$beton$` (and this one would be called "beton"). When the plugin loads that instruction string it will replace those variables with values assigned to them in _main.yml_ file **before** the instruction string is parsed. This is useful for example when installing a package containing a WorldEdit schematic of the quest building. Instead of going through the whole code to set those locations, names or texts you will only have to specify a few variables (that is, of course, if the author of the package used those variables properly in his code).

Note that these variables are something entirely different than conversation variables. Global ones use `$` characters and conversation ones use `%` characters. The former is resolved before the instruction string is parsed while the latter is resolved when the quests are running, usually on a per-player basis.

```YAML
variables:
  village_location: 100;200;300;world
  village_name: Concrete
```

## Canceling quests

If you want to let your players cancel their quest there is a function for that. In _main.yml_ file there is `cancel` branch. You can specify there quests, which can be canceled, as well as actions that need to be done to actually cancel them. You can find an example in the `default` package. The arguments you can specify are:

* `name` - this will be the name displayed to the player. All `_` characters will be converted to spaces. If you want to include other languages you can add here additional options (`en` for English etc.)
* `conditions` - this is a list of conditions separated by commas. The player needs to meet all those conditions to be able to cancel this quest. Place there the ones which detect that the player has started the quest, but he has not finished it yet. 
* `objectives` - list of all objectives used in this quest. They will be canceled without firing their events.
* `tags` - this is a list of tags that will be deleted. Place here all tags that you use during the quest.
* `points` - list of all categories that will be entirely deleted.
* `journal` - these journal entries will be deleted when canceling the quest.
* `events` - if you want to do something else when canceling the quest (like punishing the player), list the events here.
* `loc` - this is a location to which the player will be teleported when canceling the quest (defined as in teleport event);

To cancel the quest you need to open your backpack and select a "cancel" button (by default a bone, can be changes by naming an item "cancel_button" inside default package). There will be a list of quests which can be canceled. Just select the one that interests you and it will be canceled.

## Global objectives

If you want a objective to be active for every player right after joining you can create a global objective. This is done by adding `global` argument to the instruction of the objective. When you then reload BetonQuest it is started for all online players and also will be started for every player who joins.

To prevent the objective from being started every time a player joins a tag is set for the player whenever the objective is started and as long as the player has the tag the objective wont be started again if the player joins.  
These tags follow syntax `<package>.global-<id>`, where `<id>` is the objectives id and `<package>` the package where the objective is located.

Possible use cases would be a quest which starts if a player reaches a specific location or breaks a specific block.

**Example:**
```YAML
start_quest_mine: 'location 100;200;300;world 5 events:start_quest_mine_folder global'
```
## Static events

Static events are events that will fire at the specified time of the day. They are not tied to a specific player, so not all of event types can be used as static. (Which player should receive a tag or objective? From which one should the items be taken?) Also, static events cannot have conditions defined (`event-conditions:` argument), as the plugin cannot check any condition without the player. Events, that can be used as static are flagges with `static` keyword in this documentation. You can define your static events in _main.yml_ file under `static` section, as such:

```YAML
static:
  '09:00': beton
  '23:59': lightning_strike
  '11:23': some_command
```

The hour must be in `''` to avoid problems, it needs leading zero if less than 10. `beton`, `lightnint_strike` etc. are IDs of events. There can only be one event specified, but it can be of type "folder".

## Journal

The journal is a book in which all your adventures are described. You can obtain it by typing **/j** command or **/b** and selecting it from backpack. You cannot put it into any chests, item frames and so on. If you ever feel the need to get rid of your journal, just drop it - it will return to your backpack. The journal is updated with the `journal` event, and the text inside is defined in _journal.yml_ config file. If you update these texts and reload the plugin, all players' journals will reflect changes. Colors in the journal can be altered in config.yml. The entries can use color codes, but the color will be lost between pages.

The journal by default appears in the last slot of the hotbar. If you want to change that use `default_journal_slot` option in _config.yml_, experiment with different settings until you're ok with it.

If you want to translate the entry do the same thing as with conversation option - go to new line, add language ID and the journal text for every language you want to include.

You can control behavior of the journal in _config.yml_ file, in `journal` section. `chars_per_page` specifies how many characters will be placed on a single page. If you set it too high, the text will overflow outside of the page, too low, there will be too much pages. `one_entry_per_page` allows you to place every entry on a single page. The `chars_per_page` setting is in this case ignored, BetonQuest will put entire entry on that page. `reversed_order` allows you to reverse order of entries and `hide_date` lets you remove the date from journal entries.

You can control colors in the journal in `journal_colors` section in _config.yml_: `date` is a color of date of every entry, `line` is a color of lines separating entries and `text` is just a color of a text. You need to use standard color codes without `&` (eg. `'4'` for dark red).

You can also add a main page to the journal. It's a list of texts, which will show only if specified conditions are met. You can define them in the _main.yml_ file, in the `journal_main_page` section:

```YAML
journal_main_page:
  title:
    priority: 1
    text:
      en: '&eThe Journal'
      pl: '&eDziennik'
    conditions: 'quest_started,!quest_completed'
```

Each string can have text in different languages, list of conditions separated by commas (these must be met for the text to show in the journal) and `priority`, which controls the order of texts. You can use conversation variables in the texts, but they will only be updated when the player gets his journal with the **/journal** command. Color codes are supported.

If you want your main page take a separate page (so entries will be displayed on next free page), set `full_main_page` in _config.yml_ to "true".

## Tags

Tags are little pieces of text you can assign to player and then check if he has them. They are particularly useful to determine if player has started or completed quest. They are given with `tag` event and checked with `tag` condition. All tags are bound to a package, so if you add `beton` tag from within the `default` package, the tag will look like `default.beton`. If you're checking for `beton` tag from within `default` package, you're actually checking for `default.beton`. If you want to check a tag from another package, then you just need to prefix it's name with that package, for example `quest.beton`.

## Points

Points are like tags, but with amount. You can earn them for doing quest, talking with NPC’s, basically for everything you want. You can also take the points away, even to negative numbers. Points can be divided to categories, so the ones from _beton_ category won’t mix with points from _quests_ group. Of course then you can check if player has (or doesn’t have) certain amount and do something based on this condition. They can be used as counter for specific number of quest done, as a reputation system in villages and even NPC’s attitude to player.

## NPCs

Conversations can be assigned to NPCs. You do it in the _main.yml_ file inside a package, in "npcs" section:

```YAML
npcs:
  '0': innkeeper
  'Innkeeper': innkeeper
```

The first string is the name of the NPC, second one is the corresponding conversation name. In case you use Citizens, name is the ID of an NPC (_don't try to put Citizens NPC's name here, it must be the ID_). To acquire it just select the NPC and type `/npc`. If you don't want to use Citizens, you can also build NPCs as any other building in Minecraft:

Place somewhere a block of stained clay, no matter the color. Then place a head on top of it (type doesn't matter, it must be head). Now place a sign on the side of the clay block (it can be on it's back) and type in the first line `[NPC]`, and on the second line the ID of the NPC (in case of the above code example, the ID would be `Innkeeper`). You need to have permission `betonquest.createnpc` for that. Congratulations, you have created the NPC. Now you can add levers (hands) to it and maybe even a fence gate (legs). Conversation is started by right clicking it's head.

!!! note
    When using Citizens ID's they must be enclosed in quotes.

## Items

Items in BetonQuest are defined in _items.yml_ file. Each item has an instruction string, similarly to events, conditions etc. Basic syntax is very simple:

```YAML
item: BLOCK_SELECTOR other arguments...
```

[BLOCK_SELECTOR](#block-selectors) is a type of the item. It doesn't have to be all in uppercase. Other arguments specify data like name of the item, lore, enchantments or potion effects. There are two categories of these arguments: the ones you can apply to every item and type specific arguments. Examples would be name (for every item type) and text (only in books).

Every argument is used in two ways: when creating an item and when checking if some existing item matches the instruction. The first case is pretty straightforward - BetonQuest takes all data you specified and creates an item, simple as that. Second case is more complicated. You can require some property of the item to exist, other not to exist, or skip this property check altogether. You can also accept an item only if some value (like enchantment level) is greater/less than _x_. You can use wildcards in the BLOCK_SELECTOR to match multiple types of items.

These are arguments that can be applied to every item:

- `name` - the display name of the item. All underscores will be replaced with spaces and you can use `&` color codes. If you want to specifically say that the item must not have any name, use `none` keyword.

- `lore` - text under the item's name. Default styling of lore is purple and italic. All underscores will be replaced with spaces and you can use `&` color codes. To make a new line use `;` character. If you require the item not to have lore at all, use `none` keyword. By default lore will match only if all lines are exactly the same. If you want to accept all items which contain specified lines (and/or more lines), add `lore-containing` argument to the instruction string.

- `data` - data value of the item. An example of this would be different wool colors or damage of a pickaxe. You can require an exact value by specifying a number, or a value greater/less (and equal) than specified by adding `+`/`-` character at the end of the number.

- `enchants` - a list of enchantments and their levels. Each enchantment consists of these things, separated by colons:
    - [name](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/enchantments/Enchantment.html)
    - level (a positive number)
    
    For example `damage_all:3` is _Sharpness III_. You can specify additional enchantments by separating them with commas.
    
    You can require the item not to have any enchantments by using `none` keyword. You can also add `+`/`-` character to the enchantment level to make the check require levels greater/less (and equal) than specified. If you don't care about the level, replace the number with a question mark.
    
    By default all specified enchantments are required. If you want to check if the item contains a matching enchantment (and/or more enchants), add `enchants-containing` argument to the instruction string. Each specified enchantment will be required on the item by default unless you prefix its name with `none-`, for example `none-knockback` means that the item must not have any knockback enchantment. **Do not use `none-` prefix unless you're using `enchants-containing` argument**, it doesn't make any sense and will break the check!

- `unbreakable` - this makes the item unbreakable. You can specify it either as `unbreakable` or `unbreakable:true` to require an item to be unbreakable. If you want to check if the item is breakable, use `unbreakable:false`.

**Examples**:

```YAML
name:&4Sword_made_of_Holy_Concrete
name:none
lore:&cOnly_this_sword_can_kill_the_Lord_Ruler
lore:&2Quest_Item lore-containing
lore:none
data:5
data:500-
enchants:damage_all:3+,none-knockback
enchants:power:? enchants-containing
enchants:none
unbreakable
unbreakable:false
```

These are the arguments that can be applied only to specific item types:

### Books

_This applies to a written book and a book and quill._

- `title` - the title of a book. All underscores will be replaced with spaces and you can use `&` color codes. If you want to specifically say that the book must not have any title, use `none` keyword.

- `author` - the author of a book. All underscores will be replaced with spaces, you cannot use color codes here. If you want to specifically say that the book must not have any author, use `none` keyword.

- `text` - the text of the book. All underscores will be replaced with spaces and you can use `&` color codes. The text will wrap to the next page if amount of characters exceeds `journal.chars_per_page` setting in _config.yml_. If you want to manually wrap the page, use `|` character. To go to new line use `\n`. Keep in mind that you can't use any spaces here, you must only use underscores (`_`). This needs to be a single argument, even if it's really long. If you don't want the book to have any text, use `none` keyword instead.

**Examples**:

```YAML
title:Malleus_Maleficarum
author:&eGallus_Anonymus
text:Lorem_ipsum_dolor_sit_amet,\nconsectetur_adipiscing_elit.|Pellentesque_ligula_urna(...)
```

### Potions

_This applies to potions, splash potions and lingering potions._

- `type` - type of a potion. Here's [the list](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/potion/PotionType.html) of possible types. Do not mistake this for a custom effect, this argument corresponds to the default vanilla potion types.

- `extended` - extended property of the potion (you can achieve it in-game by adding redstone). It can be specified as `extended` or `extended:true`. If you want to check the potion that is NOT extended, use `upgraded:false`.

- `upgraded` - upgraded property of the potion (you can achieve it in-game by adding glowstone). It can be specified as `upgraded` or `upgraded:true`. If you want to check the potion that is NOT upgraded, use `upgraded:false`.

- `effects` - a list of custom effects. These are independent of the potion type. The effects must be separated by commas. Each effect consists of these things, separated by colons:

    - [type](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html) (this is different stuff that the link above!)
    - power
    - duration (in seconds)
    
    An example would be `WITHER:2:30`, which is a wither effect of level 2 for 30 seconds.

    If you want to target only potions without custom effects, use `none` keyword. You can target potions with level and time greater/less (and equal) than specified with `+`/`-` character after the number. If you don't care about the level/time, you can replace them with question mark.
    
    By default all specified effects are required. If you want to check if the potion contains these effects among others, add `effects-containing` argument to the instruction string. Now if you want to make sure the potion doesn't contain a specific effect, prefix the effect name with `none-`. **Don't use that prefix unless you're also using `effects-containing` argument**, it doesn't make any sense and it will break the check.

**Examples**:

```YAML
type:instant_heal
extended
upgraded:false
effects:poison:1+:?,slow:?:45-
effects:none-weakness,invisibility:?:? effects-containing
```

### Heads

_This applies to human heads._

- `owner` - this is the name of the head owner. It will **not** use color codes nor replace underscores with spaces. If you want to check for heads without any owner, use `none` keyword.

**Examples**:

```YAML
owner:Co0sh
owner:none
```

### Leather armor

_This applies to all parts of leather armor._

- `color` - this is the color of the armor piece. It can be either one of [these values](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/DyeColor.html), a hexadecimal RGB value prefixed with `#` character or its decimal representation without the prefix. You can also check if the armor piece doesn't have any color with `none` keyword.

**Examples**:

```YAML
color:light_blue
color:#ff00ff
color:none
```

### Fireworks

_This applies to fireworks._

- `firework` - this is a list of effects of the firework rocket. They are separated by commas. Each effect consists of these things separated by colons:

    - [effect type](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/FireworkEffect.Type.html)
    - a list of main colors (refer to leather armor colors above for syntax) separated by semicolons
    - a list of fade colors
    - `true`/`false` keyword for trail effect
    - `true`/`false` keyword for flicker.

    Note the separation characters, this is important: commas separate effects, colons separate effect properties, semicolons separate colors.
    
    If you want to target fireworks without any effects, use `none` keyword. If you want to target any effect type, use question mark instead of the effect name. If you don't want the effect to have any main/fade colors, use `none` keyword in the place of colors. If you don't care about main/fade colors, use question marks in that place. If you don't care about trail/flicker effect, use question marks instead of `true`/`false` keyword.
    
    By default the check will require all specified effects to be present on the firework. You can check if the firework contains specified effects among others by adding `firework-containing` argument to the instruction string. To match the item which must not have an effect, prefix the effect name with `none-` keyword. **Don't use that prefix unless you're also using `firework-containing` argument**, it doesn't make any sense and will break the check.

- `power` - flight duration of the firework, in levels. You can use `+`/`-` character to target greater/less (and equal) levels.

**Examples**:

```YAML
firework:ball:red;white:green;blue:true:true,ball_large:green;yellow:pink;black:false:false
firework:burst:?:none:?:? firework-containing
firework:none-creeper firework-containing
firework:none
power:3
power:2+
```

### Firework charges

_This applies to firework charges._

- `firework` - this is almost the same as fireworks. You can only specify a single effect and the `power` argument has no effects.

## Backpack

Sometimes you'll want some items to be persistent over death. If the player has lost them the quest would be broken. You can add a apecific line to item's lore to make it persistent (`&2Quest_Item` by default, `_` is a space in item's definition). Note that this must be a whole new line in the lore! Such item wouldn't be dropped on death, instead it would be placed in player's backpack. **Example**: `:::YAML important_sword: 'DIAMOND_SWORD name:Sword_for_destroying__The_Concrete lore:Made_of_pure_Mithril;&2Quest_Item'`

To open your backpack just type **/j** command. The inventory window will open, displaying your stored items. The first slot is always the journal, and if you get it, the slot will stay empty. You can transfer quest items back and forth between inventories by clicking on them. Left click will transfer just one item, right click will try to transfer all items. Normal items cannot be stored into the backpack, so it's not an infinite inventory.

If you will ever have more than one page of quest items, the buttons will appear. You can customize those buttons by creating `previous_button` and `next_button` items in _items.yml_ file. Their name will be overwritten with the one defined in _messages.yml_.

Quest items cannot be dropped in any way other than using them. This way you can create a quest for eating cookies by giving the player a stack of cookies flagged as quest items and not continuing until there are no more cookies in his inventory/backpack. The player cannot drop the cookies, so he must eat every one of them to complete the quest.

Don't worry if the item-dropping filter isn't working for your items when you're in creative mode - it's not a bug. It's a feature. Creative-mode players should be able to easily put quest items in containers like TreasureChests.

## Party

Parties are very simple. So simple, that they are hard to understand if you already know some other party system. Basically, they don't even have to be created before using them. Parties are defined directly in conditions/events (`party` event, `party` conditions, check them out in the reference lists below). In such instruction strings the first argument is a number - range. It defines the radius where the party members will be looked for. Second is a list of conditions. Only the players that meet those conditions will be considered as members of the party. It's most intuitive for players, as they don't have to do anything to be in a party - no commands, no GUIs, just starting the same quest or having the same item - you choose what and when makes the party.

To understand better how it works I will show you an example of `party` event. Let's say that every player has an objective of pressing a button. When one of them presses it, this event is fired:

```YAML
party_reward: party 50 quest_started cancel_button,teleport_to_dungeon
```

Now, it means that all players that: are in radius of 50 blocks around the player who pressed the button AND meet `quest_started` condition will receive `cancel_button` and `teleport_to_dungeon` events. The first one will cancel the quest for pressing the button for the others (it's no longer needed), the second one will teleport them somewhere. Now, imagine there is a player on the other side of the world who also meets `quest_started` condition - he won't be teleported into the dungeon, because he was not with the other players (not in 50 blocks range). Now, there were a bunch of other players running around the button, but they didn't meet the `quest_started` condition. They also won't be teleported (they didn't start this quest).

## Block Selectors

When specifying a way of matching a block, a `block selector` is used. This differs a little depending if your Minecraft version is older than 1.13 or newer.

### Pre 1.13 Minecraft

The format of a block selector is: `material:data`

Where:

  - `material` - What material the block is made of. You can look this up in [this list](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html).

  - `data` - (optional) A number representing the data of the block. If left out then the selector will match all data types.

Examples:

  - `LOG` - Matches all LOGS
  
  - `LOG:1` - Matches SPRUCE LOGS


### 1.13 and above
The format of a block selector is: `prefix:material[state=value,...]`

Where:

  - `prefix` - (optional) The material prefix. If left out then it will be assumed to be 'minecraft'
  
  - `material` - The material the block is made of. You can look this up in [this list](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html). Wildcards are supported (both * and ?).
  
  - `state` - (optional) The block states can be provided in a comma separated list surrounded by square brackets. You can look up states in [this list](https://minecraft.gamepedia.com/1.13/Flattening#Block_states). Any states left out will be ignored when matching.

Examples:

  - `minecraft:stone` - Matches all blocks of type STONE
  
  - `redstone_wire` - Matches all blocks of type REDSTONE_WIRE
  
  - `redstone_wire[power=5]` - Matches all blocks of type REDSTONE_WIRE and which have a power of 5
  
  - `redstone_wire[power=5,facing=1]` - Matches all blocks of type REDSTONE_WIRE and which have both a power of 5 and are facing 1
  
  - `*_LOG` - Matches all LOGS
  
  - `*` - Matches everything
  
  - `*[waterlogged=true]` - Matches all waterlogged blocks
