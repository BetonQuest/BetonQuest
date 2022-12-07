---
icon: octicons/book-16
---

## Fundamental BetonQuest Types

Conditions, events and objectives are defined with an "instruction string". It's a piece of text, formatted in a specific way, containing the instruction for the condition/event/objective. Thanks to this string they know what should they do. To define the instruction string you will need a reference, few pages below. It describes how something behaves and how it should be created. All instruction strings are defined in appropriate sections, for example all conditions are in the _conditions_ section. The syntax used to define them looks like this: `name: 'the instruction string containing the data'`. Apostrophes are optional in most cases, you can find out when to use them by looking up "YAML syntax" in Google.

### Conditions

Conditions are the most versatile and useful tools in creating advanced quests. They allow you to control what options are available to player in conversations, how the NPC responds or if the objective will be completed. The reference of all possible conditions is down below.

You can negate the condition (revert its output) by adding an exclamation mark (`!`) at the beginning of it's name (in the place you use it, i.e. in conversations, not in the _conditions_ section).

You can use conversation variables instead of numeric arguments in conditions. If the variable fails to resolve (i.e. it will return an empty string) BetonQuest will use 0 instead.

### Events

In certain moments you will want something to happen. Updating the journal, setting tags, giving rewards, all these are done using events. You define them just like conditions, by specifying a name and instruction string. You can find instruction strings to all events in the event reference. At the end of the instruction string you can add `conditions:` or `condition:` (with or without `s` at the end) attribute followed by a list of condition names separated by commas, like `conditions:angry,!quest_started`. This will make an event fire only when these conditions are met.

You can use conversation variables instead of numeric arguments in events. If the variable fails to resolve (i.e. it will return an empty string) BetonQuest will use 0 instead.

### Objectives

Objectives are the main things you will use when creating complex quests. You start them with a special
event, `objective`. You define them in the _objectives.yml_ file, just as you would conditions or events. At the end of
the instruction string you can add conditions and events for the objective. Conditions will limit when the objective can
be completed (e.g. killing zombies only at given location in quest for defending city gates), and events will fire when
the objective is completed (e.g. giving a reward, or setting a tag which will enable collecting a reward from an NPC).
You define these like that: `conditions:con1,con2 events:event1,event2` at the end of instruction string . Separate them
by commas and never use spaces! You can also use singular forms of these arguments: `condition:` and `event:`.

If you want to start an objective right after it was completed (for example `die` objective: when you die, teleport you
to a special spawnpoint and start `die` objective again), you can add `persistent` argument at the end of an instruction
string. It will prevent the objective from being completed, although it will run all its events. To cancel such
objective you will need to use `objective delete` event.

Objectives are loaded at start-up, but they do not consume resources without player actually having them active. This
means that if you have 100 objectives defined, and 20 players doing one objective, 20 another players doing second
objective, and the rest objectives are inactive (no one does them), then only 2 objectives will be consuming your server
resources, not 100, not 40.


#### Global objectives

If you want an objective to be active for every player right after joining you can create a global objective.
This is done by adding `global` argument to the instruction of the objective.
When you then reload BetonQuest it is started for all online players and also will be started for every player who joins.

To prevent the objective from being started every time a player joins a tag is set for the player whenever the objective
is started and as long as the player has the tag the objective won't be started again if the player joins.  
These tags follow syntax `<package>.global-<id>`, where `<id>` is the objectives id and `<package>` the package where
the objective is located.

Possible use cases would be a quest which starts if a player reaches a specific location or breaks a specific block.

```YAML title="Example"
objectives:
  start_quest_mine: 'location 100;200;300;world 5 events:start_quest_mine_folder {++global++}'
```

## Tags

Tags are little pieces of text you can assign to player and then check if he has them. They are particularly useful to determine if player has started or completed quest. They are given with `tag` event and checked with `tag` condition. All tags are bound to a package, so if you add `beton` tag from within a package named `example`, the tag will look like `example.beton`. If you're checking for `beton` tag from within `example` package, you're actually checking for `example.beton`. If you want to check a tag from another package, then you just need to prefix it's name with that package, for example `quest.beton`.

## Points

Points are like tags, but with amount. You can earn them for doing quest, talking with NPC’s, basically for everything you want. You can also take the points away, even to negative numbers. Points can be divided to categories, so the ones from _beton_ category won’t mix with points from _quests_ group. Of course then you can check if player has (or doesn't have) certain amount and do something based on this condition. They can be used as counter for specific number of quest done, as a reputation system in villages and even NPC’s attitude to player.

## Party

Parties are very simple. So simple, that they are hard to understand if you already know some other party system. Basically, they don't even have to be created before using them. Parties are defined directly in conditions/events (`party` event, `party` conditions, check them out in the reference lists below). In such instruction strings the first argument is a number - range. It defines the radius where the party members will be looked for. Second is a list of conditions. Only the players that meet those conditions will be considered as members of the party. It's most intuitive for players, as they don't have to do anything to be in a party - no commands, no GUIs, just starting the same quest or having the same item - you choose what and when makes the party.

To understand better how it works I will show you an example of `party` event. Let's say that every player has an objective of pressing a button. When one of them presses it, this event is fired:

```YAML
party_reward: party 50 quest_started cancel_button,teleport_to_dungeon
```

Now, it means that all players that: are in radius of 50 blocks around the player who pressed the button AND meet `quest_started` condition will receive `cancel_button` and `teleport_to_dungeon` events. The first one will cancel the quest for pressing the button for the others (it's no longer needed), the second one will teleport them somewhere. Now, imagine there is a player on the other side of the world who also meets `quest_started` condition - he won't be teleported into the dungeon, because he was not with the other players (not in 50 blocks range). Now, there were a bunch of other players running around the button, but they didn't meet the `quest_started` condition. They also won't be teleported (they didn't start this quest).

## Canceling quests

If you want to let your players cancel their quest there is a function for that. In _package.yml_ file there is `cancel`
branch. You can specify there quests, which can be canceled, as well as actions that need to be done to actually cancel
them. The arguments you can specify are:

* `name` - this will be the name displayed to the player. All `_` characters will be converted to spaces. If you want to include other languages you can add here additional options (`en` for English etc.)
* `conditions` - this is a list of conditions separated by commas. The player needs to meet all those conditions to be able to cancel this quest. Place there the ones which detect that the player has started the quest, but he has not finished it yet. 
* `objectives` - list of all objectives used in this quest. They will be canceled without firing their events.
* `tags` - this is a list of tags that will be deleted. Place here all tags that you use during the quest.
* `points` - list of all categories that will be entirely deleted.
* `journal` - these journal entries will be deleted when canceling the quest.
* `events` - if you want to do something else when canceling the quest (like punishing the player), list the events here.
* `loc` - this is a location to which the player will be teleported when canceling the quest (defined as in teleport event);

To cancel the quest you need to open your backpack and select a "cancel" button. There will be a list of quests which can be canceled. Just select the one that interests you and it will be canceled.


## :octicons-location-16: Unified location formating

Whenever you want to define some location in your events, conditions, objectives or any other things, you will define it
with this specific format. The location consists of 2 things: base and vector. Only the base is always required.

### Base Location
The base is a core location. There are two types: absolute coordinates and variables. Absolute coordinates are
defined like `100;200;300;world`, where `100` is X coordinate, `200` is Y, `300` is Z and `world` is the name of the
world. These can have decimal values. If you want you can also add two more numbers at the end, yaw and pitch 
(these are controlling the rotation, for example in teleportation event, both are needed if you decide to add them;
example: `0.5;64;0.5;world;90;-270`).

### Variables as Base Location
To use a variable as the location's base it must resolve to valid absolute coordinates. An example of such variable 
is `%location%`, which shows player's exact location. Simply place it instead of coordinates. There is one rule though:
you can't use variable base types in events running without players (for example static events or the ones run from
folder event after the player left the server). BetonQuest won't be able to resolve the location variable without the
player!

### Vectors
The vector is a modification of the location. Vectors look like `->(10;2.5;-13)` and are added
to the end of the base. This will modify the location, X by 10, Y by 2.5 and Z by -13. For example, location written as
`100;200;300;world_nether->(10;2.5;-13)` will generate a location with X=110, Y=202.5 and Z=287 in the world `world_nether`.

## Block Selectors

When specifying a way of matching a block, a `block selector` is used.

### Format

The format of a block selector is: `namespace:material[state=value,...]`

Where:

  - `namespace` - (optional) The material namespace. If left out then it will be assumed to be 'minecraft'.
   Can be a [regex](#regex-regular-expressions).
  
  - `material` - The material the block is made of. All materials can be found in
  [Spigots Javadocs](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html). 
  It can be a [regex](#regex-regular-expressions).
  If the regex ends with square brackets you have to add another pair of empty square brackets even if you don't want to 
  use the state argument (`[regex][]`).  
  Instead of using a regex to match multiple materials you can also define a [tag](https://minecraft.gamepedia.com/Tag).
  Every tag matches a special group of blocks or items that can be grouped together logically. They can be used using this format `:blocks:flowers` or `minecraft:blocks:flowers`.
  Be aware that a tag always starts with either `:` or a namespace. 
  
  - `state` - (optional) The block states can be provided in a comma separated `key=value` list surrounded by square brackets.
   You can look up states in the Minecraft [wiki](https://minecraft.gamepedia.com/Block_states).
   Any states left out will be ignored when matching.
   *Values* can be a [regex](#regex-regular-expressions).

Examples:

  - `minecraft:stone` - Matches all blocks of type STONE
  
  - `redstone_wire` - Matches all blocks of type REDSTONE_WIRE
  
  - `redstone_wire[power=5]` - Matches all blocks of type REDSTONE_WIRE and which have a power of 5
  
  - `redstone_wire[power=5,facing=1]` - Matches all blocks of type REDSTONE_WIRE and which have both a power of 5 and are facing 1
  
  - `.*_LOG` - Matches all LOGS
  
  - `.*` - Matches everything
  
  - `.*[waterlogged=true]` - Matches all waterlogged blocks
  
  - `minecraft:blocks:flowers` - Matches all flowers
  
  - `:blocks:crops[age=0]` - Matches all crops with an age of 0 meaning, not grown / just planted

### Setting behaviour

A block selector with a regex or tag as it's material name results in a random block out of all blocks that match that regex or tag.
You cannot use a regex in block states when the block selector is used for placing blocks.

### Matching behaviour

The block state will ignore all additional block states on the block it's compared with by default.
Example: `fence[facing=north] matches fence[facing=north] and fence[facing=north,waterlogged=true]`
You can add an `exactMatch` argument if you only want to match blocks that exactly match the block state. 
A regex is allowed in any block state value when the block selector is used to match blocks.

## Hiding Players

<video controls loop src="../../_media/content/Documentation/Compatibility/PlayerHider.mp4" width="100%">
  Sorry, your browser doesn't support embedded videos.
</video>

You can also hide players for specific players in the `player_hider` section of your package. When the `source_player` meets the conditions,
every player that meets the `target_player` conditions will be completely hidden from them. 
This is really useful if you want a lonely place on your server 
or your quests break when multiple players can see or affect each other.
You can configure the interval which checks the conditions in the [config.yml](./Configuration.md#player-hider-interval).

Special behaviour:

* A player that meets the `source_player`conditions can no longer be pushed by other players.
* By leaving the e.g. `source_player` argument empty it will match all players.

```YAML
player_hider:
  example_hider:  #All players in a special region cannot see any other players in that region. If a player is outside the region, they can still see the `target_player`.
    source_player: in_StoryRegion
    target_player: in_StoryRegion
  another_hider: #No one can see any players inside a secret room.
    #The source_player argument is left out to match all players.    
    target_player: in_secretRoom
  empty_hider: #in_Lobby is a world condition. Therefore, the lobby world appears empty for everyone that is in it.
    source_player: in_Lobby
    #The target_player argument is left out to match all players.
```

## Regex (Regular Expressions)
A regular expression is a sequence of characters that specifies a search pattern for text. It's used in BetonQuest to
check if game objects match a user-defined input. For example, [Block Selectors](#block-selectors) use a regex to match
multiple materials or block states. You can also use regular expressions in the 
[variable condition](Conditions-List.md#variable-variable) or the 
[password objective](Objectives-List.md#password-password) to match player names, item names, etc. These expressions are
a very powerful tool, but can be confusing at first.

### Common Use Cases

| Use Case                                                      | Regex                  |
|---------------------------------------------------------------|------------------------|
| A specific text e.g. `STONE`                                  | `STONE`                |
| A text starting with `STONE`                                  | `STONE.*`              |
| A text ending with `_LOG`                                     | `.*_LOG`               |
| A specific number e.g. `42`                                   | `^42$`                 |
| A specific range of numbers, e.g. any number between 0 and 99 | `[0-9]{1,2}`           |
| Positive numbers only                                         | `^\d+$`                |
| Negative numbers only                                         | `^-\d+$`               |
| Any number                                                    | `[-+]?[0-9]+\.?[0-9]+` |

### More complex use cases

If you want to use complex patterns you must learn more about regular expressions. There are countless resources online,
for example you could read this 
[cheatsheet](https://medium.com/factory-mind/regex-tutorial-a-simple-cheatsheet-by-examples-649dc1c3f285).
