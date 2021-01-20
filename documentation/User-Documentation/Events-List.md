# Events List

## Cancel quest: `cancel`

This event works in the same way as a quest canceler in the backpack. Running it is equal to the player clicking on the bone. The only argument is a name of a quest canceler, as defined in _main.yml_

!!! example
    ```YAML
    cancel wood
    ```

## Chat player message `chat`

This event will send the given message as the player. Therefore, it will look like as if the player did send the message. 
The instruction string is the command, without leading slash. You can only use `%player%` as a variable in this event.
Additional messages can be defined by separating them with `|` character. If you want to use a `|` character in the message use `\|`.

If a plugin does not work with the sudo / command event you need to use this event.

!!! example
    ``` YAML
    sendMSG: "chat Hello!"
    sendMultipleMSGs: "chat Hi %player%|ban %player%|pardon %player%"
    sendPluginCommand: "chat /someCommand x y z"
    ```

## Chest Clear: `chestclear`
 
**persistent**, **static**

This event removes all items from a chest at specified location. The only argument is a location.

!!! example
    ```YAML
    chestclear 100;200;300;world
    ```

## Chest Give: `chestgive`

**persistent**, **static**

This works the same as `give` event, but it puts the items in a chest at specified location. The first argument is a location, the second argument is a list of items, like in `give` event. If the chest is full, the items will be dropped on the ground. The chest can be any other block with inventory, i.e. a hopper or a dispenser. BetonQuest will log an error to the console when this event is fired but there is no chest at specified location.

!!! example
    ```YAML
    chestgive 100;200;300;world emerald:5,sword
    ```

## Chest Take: `chesttake`
 
**persistent**, **static**

This event works the same as `take` event, but it takes items from a chest at specified location. The instruction string is defined in the same way as in `chestgive` event.

!!! example
    ```YAML
    chesttake 100;200;300;world emerald:5,sword
    ```

## Clear mobs: `clear`

This event removes all specified mobs from the specified area. The first required argument is a list of mobs (taken from [here](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html)) separated by commas. Next is location. After that there is the radius around the location (a positive number or a variable). You can also optionally specify `name:` argument, followed by name which removed mobs must have. You can use `marked:` argument to remove only mobs marked in `spawn` event.

!!! example
    ```YAML
    clear ZOMBIE,CREEPER 100;200;300;world 10 name:Monster
    ```

## Compass: `compass`

When you run this event, you can add or remove a compass destination for the player. You may also directly set the players's compass destination as well. When a destination is added the player will be able to select a specified location as a target of his compass. To select the target the player must open his backpack and click on the compass icon. The first argument is `add`,`del` or `set`, and second one is the name of the target, as defined in _main.yml_. Note that if you set a target the player will not automatically have it added to their choices.

The destination must be defined in the _main.yml_ file in `compass` section. You can specify a name for the target in each language or just give a general name, and optionally add a custom item (from _items.yml_) to be displayed in the backpack. Example of a compass target:

```YAML
compass:
  beton:
    name:
      en: Target
      pl: Cel
    location: 100;200;300;world
    item: scroll
```

!!! example
    ```YAML
    compass add beton
    ```

## Command: `command`

**persistent**, **static**

Runs specified command from the console. The instruction string is the command, without leading slash.
You can use variables here, but variables other than `%player%` won't resolve if the event is fired from delayed `folder`
and the player is offline now. You can define additional commands by separating them with `|` character.
If you want to use a `|` character in the command use `\|`.

!!! example
    ```YAML
    command kill %player%|ban %player%
    ```

## Conversation: `conversation`

Starts a conversation at location of the player. The only argument is ID of the conversation. This bypasses the conversation permission!

!!! example
    ```YAML
    conversation village_smith
    ```

## Damage player: `damage`

Damages the player by specified amount of damage. The only argument is a number (can have floating point).

!!! example
    ```YAML
    damage 20
    ```

## Delete Point: `deletepoint`

**persistent**, **static**

Delete the player points in a specified category.

!!! example
    ```YAML
    deletepoint npc_attitude
    ```

## Door: `door`

**persistent**, **static**

This event can open and close doors, trapdoors and fence gates. The syntax is exactly the same as in `lever` event above.

!!! example
    ```YAML
    door 100;200;300;world off
    ```

## Remove Potion Effect: `deleffect`

Removes the specified potion effects from the player. Use `any` instead of a list of types to remove all potion effects from the player.

!!! example
    ```YAML
    deleffect ABSORPTION,BLINDNESS
    ```

## Potion Effect: `effect`

Adds a specified potion effect to player. First argument is potion type. You can find all available types [here](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html). Second is integer defining how long the effect will last in seconds. Third argument, also integer, defines level of the effect (1 means first level). Add a parameter `ambient` to make potion particles appear more invisible (just like beacon effects). To hide particles add a parameter `hidden`. To hide the icon for the effect add `noicon`.

!!! example
    ```YAML
    effect BLINDNESS ambient icon
    ```

## Explosion: `explosion`

**static**

Creates an explosion. It can make fire and destroy blocks. You can also define power, so be careful not to blow your server away. Default TNT power is 4, while Wither on creation is 7. First argument can be 0 or 1 and states if explosion will generate fire (like Ghast’s fireball). Second is also 0 or 1 but this defines if block will be destroyed or not. Third argument is the power (float number). At the end (4th attribute) there is location.

!!! example
    ```YAML
    explosion 0 1 4 100;64;-100;survival
    ```

## Folder: `folder`

**persistent**, **static**

It's something like a container for multiple events. You can use it to clarify your code.
It also features optional delay and period measured in seconds (you can use ticks or minutes if you add `ticks` or `minutes` argument).
It is persistent for events marked as _persistent_, which means that the events will be fired even after the player logs out.
Beware though, all conditions are false when the player is offline (even inverted ones),
so those events should not be blocked by any conditions!
The only required argument is a list of events separated by commas.

There are also three optional arguments: `delay:`, `period:` and `random:`.
Delay and Period is a number of seconds. Delay is the time before execution and period is the time between each event. It's optional and leaving it blank is the same as `delay:0` or `period:0`.
Random is the amount of events, that will be randomly chosen to fire. It's optional and leaving it blank or omit it will fire all events.

!!! example
    ```YAML
    folder event1,event2,event3 delay:5 period:5 random:1
    ```

## Give Items: `give`

Gives the player predefined items. They are specified exactly as in `item` condition - list separated by commas, every item can have amount separated by colon. Default amount is 1. If the player doesn't have required space in the inventory, the items are dropped on the ground, unless they are quest items. Then they will be put into the backpack. You can also specify `notify` keyword to display a simple message to the player about receiving items.

!!! example
    ```YAML
    give emerald:5,emerald_block:9
    ```

## Give journal: `givejournal`

This event simply gives the player his journal. It acts the same way as **/j** command would.

!!! example
    ```YAML
    givejournal
    ```

## Global point: `globalpoint`

**persistent**, **static**

This works the same way as the normal point event but instead to manipulating the points for a category of a specific player it manipulates points in a global category. These global categories are player independent, so you could for example add a point to such a global category every time a player does a quest and give some special rewards for the 100th player who does the quest.

!!! example
    ```YAML
    globalpoint global_knownusers 1
    ```

## Global tag: `globaltag`

**persistent**, **static**

Works the same way as a normal tag event, but instead of setting a tag for one player it sets it globaly for all players.

!!! example
    ```YAML
    globaltag add global_areNPCsAgressive
    ```

## If else: `if`

This event will check a condition, and based on the outcome it will run the first or second event. The instruction string is `if condition event1 else event2`, where `condition` is a condition ID and `event1` and `event2` are event IDs. `else` keyword is mandatory between events for no practical reason.

!!! example
    ```YAML
    if sun rain else sun
    ```

## Journal: `journal`

**static**

Adds or deletes an entry to/from player’s journal. Entries are defined in `journal.yml` The first argument is action (add/del), the second one is name of the entry. You can also use only one argument, `update`, it will simply update the journal without addin any entries. It's useful when you need to update the main page.

!!! example
    ```YAML
    journal add quest_started
    ```
    
!!! example
    ```YAML
    journal update
    ```

## Kill: `kill`

Kills the player. Nothing else.

## Kill Mobs: `killmob`
 
**persistent**, **static**

Kills all mobs of given type at the location. First argument is
the [type of the mob](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html). Next argument is the
location. Third argument is the radius around the location, in which the mobs must be to get killed.  
You can also specify `name:` argument, followed by the name of the mob which should get killed. All `_` characters will
be replaced with spaces. If you want to kill only mobs that have been marked using the spawn mob event use `marked:`
argument followed by the keyword.

Only mobs that are in loaded chunks can be killed by using this event.

!!! example
    ```YAML
    killmob ZOMBIE 100;200;300;world 40 name:Bolec
    ```

## Language Event: `language`

This event changes player's language to the specified one. There is only one argument, the language name.

!!! example
    ```YAML
    language en
    ```

## Lever: `lever`

**persistent**, **static**

This event can switch a lever. The first argument is a location and the second one is state: `on`, `off` or `toggle`.

!!! example
    ```YAML
    lever 100;200;300;world toggle
    ```

## Lightning: `lightning`

**static**

Strikes a lightning at given location. The only argument is the location.

!!! example
    ```YAML
    lightning 100;64;-100;survival
    ```

## Notification: `notify`

Displays a notification using the NotifyIO system. 


| Option                                                             | Description                                                                                                                                     |
|--------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------|
| message  	                                                         | The message that will be displayed. Supports variables. *Required, must be first*             	                                                                     |
| category 	                                                         | Will load all settings from that Notification Category. Can be a comma-seperated list. The first existent category will be used. *Optional*                  |   
| io       	                                                         | Any [NotifyIO](https://betonquest.github.io/BetonQuest/RELEASE/User-Documentation/Notification-IO%27s-%26-Categories). Overrides the "category" settings. *Optional*                                                                                     |
| [NotifyIO](Notification-IO's-&-Categories.md#notify-ios) 	         | Any setting from the defined notifyIO. Can be used multiple times. Overrides the "category" settings. *Optional*                                                                     |

The fallback NotifyIO is `chat` if no argument other than `message` is specified.    
`message` is the only argument of this event that is not `key:value` based. You can freely add any text with spaces there. 


!!! warning
    All colons (`:`) in the message part of the notification need to be escaped, including those inside variables, with one backslash (`\`) when using single quotes
    (`'...'`) or no quoting at all (`...`) and with two backslashes (`\\`) when using double quotes (`"..."`).
    Example:  
    `eventName: 'notify Peter:Heya %player%!'` -> `eventName: 'notify Peter{++\++}:Heya %player%!'`    
    `eventName: notify Peter:Heya %player%!` -> `eventName: notify Peter{++\++}:Heya %player%!`
    `eventName: "notify Peter:Heya %player%!"` -> `eventName: "notify Peter{++\\++}:Heya %player%!"`
    `otherEvent: notify You own %math.calc:5% fish!` -> `otherEvent: You own %math.calc{++\++}:5% fish!`

<h3>Examples:</h3>

Check out the documentation about [Notify Categories](Notification-IO's-&-Categories.md#categories) and 
[Notify IO options](Notification-IO's-&-Categories.md#notify-ios) if you haven't yet! You must understand these two to be
able to use the Notify system to it's full extend.
```YAML
#The simplest of all notify events. Just a chat message:
customEvent: "notify Hello %player%!"  

#This one explicitly defines an io (bossbar) and adds one bossbarIO option + one soundIO option:
myEvent1: "notify This is a custom message. io:bossbar barColor:red sound:block.anvil.use"

#Some events with categories.
myEvent2: "notify This is a custom message! category:info"
myEvent3: "notify This is a custom message! category:firstChoice,secondChoice"

#You can also override category settings:
myEvent: "notify Another message! category:info io:advancement frame:challenge"
```

## Broadcast: `notifyall`

This events works just like the [notify](#notification-notify) event but shows the notification for all online players.

## Objective: `objective`

**persistent**, **static**

Manages the objectives. Syntax is `objective <action> name`, where `<action>` can be _start_/_add_ (one of the two),
_delete_/_remove_ or _complete_/_finish_. Name is the name of the objective, as defined in _objectives.yml_.

Using this in static contexts only works when removing objectives!

!!! example
    ```YAML
    objective start wood
    ```

## OPsudo: `opsudo`

This event is similar to the `sudo` event, the only difference is that it will fire a command as the player with temporary OP permissions. 
Additional commands can be defined by separating them with `|` character. If you want to use a `|` character in the message use `\|`.

Looking for [run as normal player](#sudo-sudo)?

!!! example
    ```YAML
    opsudo spawn
    ```

## Party event: `party`

Runs the specified list of events (third argument) for every player in a party. More info about parties in "Party" chapter in **Reference** section.

!!! example
    ```YAML
    party 10 has_tag1,!has_tag2 give_reward
    ```

## Pick random: `pickrandom`

**persistent**, **static**

Another container for events. It picks one (or multiple) of the given events and runs it.
You must specify how likely it is that each event is picked by adding the percentage before the event's id. 
The event won't break if your total percentages are above 100%. 

It picks one event from the list by default, but you can add an optional `amount:` if you want more to be picked.
Note that only as many events as specified can be picked and `amount:0` will do nothing.

There must be two `%%` before the event's name if variables are used, one is from the variable and the other one from the event's syntax.

!!! example
    ```YAML
    pickrandom 20.5%event1,0.5%event2,79%event3 amount:2
    pickrandom %point.factionXP.amount%%event1,0.5%event2,79%event3,1%event4 amount:3
    ```
    
## Point: `point`

**persistent**

Gives the player a specified amount of points in a specified category. Amount can be negative if you want to subtract points.
You can also use an asterisk to do multiplication (or division, if you use a fraction).
First argument after the event name must be a category, and the second one - amount of points to give/take/multiply.
This event also supports an optional `notify` argument that will display information about the change using the notification system.

!!! example
    ```YAML
    point npc_attitude 10
    ```
    
!!! example    
    ```YAML
    point village_reputation *0.75
    ```

## Run events: `run`

**persistent**, **static**

This event allows you to specify multiple instructions in one, long instruction. Each instruction must be started 
with the `^` character (it divides all the instructions). It's not the same as the `folder` event, because you have to
specify the actual instruction, not an event name. It is also fired on the same tick, not on the next one like in `folder`.
Don't use conditions here, it behaves strangely. We will fix this in 2.0.

!!! example
    ```YAML
    run ^tag add beton ^give emerald:5 ^entry add beton ^kill
    ```

## Scoreboard: `score`

This event works in the same way as `point` event, the only difference is that is uses scoreboards instead of points. You can add, subtract, multiply and divide scores in objectives on the scoreboard. The first argument is the name of the objective, second one is a number. It can be positive for additon, negative for subtraction or prefixed with an asterisk for multiplication. Multiplying by fractions is the same as dividing.

!!! example
    ```YAML
    score kills 1
    ```

## Set Block: `setblock`

**persistent**, **static**

Changes the block at the given position.
The first argument is a [Block Selector](../Reference/#block-selectors), the second a location.
Very powerful if used to trigger redstone contraptions.

!!! example
    ```YAML
    setblock REDSTONE_BLOCK 100;200;300;world
    ```

## Spawn Mob: `spawn`

**persistent**, **static**

Spawns specified amount of mobs of given type at the location. First argument is a location. Next is [type of the mob](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html). The last, third argument is integer for amount of mobs to be spawned. You can also specify `name:` argument, followed by the name of the mob. All `_` characters will be replaced with spaces. You can also mark the spawned mob with a keyword using `marked:` argument. It won't show anywhere, and you can check for only marked mobs in `mobkill` objective.

You can specify armor which the mob will wear and items it will hold with `h:` (helmet), `c:` (chestplate), `l:` (leggings), `b:` (boots), `m:` (main hand) and `o:` (off hand) optional arguments. These take a single item without amount, as defined in _items.yml_. You can also add a list of drops with `drops:` argument, followed by a list of items with amounts after colons, separated by commas.

!!! example
    ```YAML
    spawn 100;200;300;world SKELETON 5 marked:targets
    ```

!!! example
    ```YAML
    spawn 100;200;300;world ZOMBIE name:Bolec 1 h:blue_hat c:red_vest drops:emerald:10,bread:2
    ```

## Sudo: `sudo`

This event is similar to `command` event, the only difference is that it will fire a command as the player.
Additional commands can be defined by separating them with `|` character. If you want to use a `|` character in the message use `\|`.

Looking for [run as admin](#opsudo-opsudo)? 

!!! example
    ```YAML
    sudo spawn
    ```

## Tag: `tag`

**persistent**, **static**

This event adds (or removes) a tag to the player. The first argument after event's name must be `add` or `del`.
Next goes the tag name. It can't contain spaces (though `_` is fine). Additional tags can be added, separated by commas (without spaces).

!!! example
    ```YAML
    tag add quest_started,new_entry
    ```

## Take Items: `take`

Removes items from player’s inventory or backpack (in that order). If the items aren't quest items don't use `take` event with player options in conversations!
The player can drop items before selecting the option and pickup them after the event fires.
Validate it on NPC’s reaction! Defining instruction string is the same as in give event.
You can also specify `notify` keyword to display a simple message to the player about loosing items.

!!! example
    ```YAML
    take emerald:120,sword
    ```

## Time: `time`

Sets or adds time. The only argument is time to be set (integer) or time to be added (integer prefixed with +),
in 24 hours format. Subtracting time is done by adding more time (if you think of this, it actually makes sense).
Minutes can be achieved with floating point.

!!! example
    ```YAML
    time +6
    ```

## Teleport: `teleport`

Teleports the player to a specified location, with or without head rotation. It will also end the conversation,
if the player has one active.The first and only argument must be location. It's a good idea to use yaw and pitch here.

!!! example
    ```YAML
    teleport 123;32;-789;world_the_nether;180;45
    ```

## Variable: `variable`

This event has only one purpose - to change variables stored in `variable` objective.
The first argument is the ID of a `variable` objective (if you use any other type you will get an error).
Second one is the key of the variable and the third is the value. Both can use `%...%` variables.
Refer to `variable` objective documentation for information about storing variables.

!!! example
    ```YAML
    variable some_var_obj name %player%
    ```

## Weather: `weather`

Sets weather. The argument is `sun`, `rain` or `storm`.

!!! example
    ```YAML
    weather rain
    ```
    
## Give experience: `experience`

Gives the specified amount of experience points to the player. You can give whole levels by adding the `level` argument.

!!! example
    ```YAML
    experience 4 level
    ```
