# Events List

## Message: `message` _static_

This event simply displays a message to the player. The instruction string is the message. All `&` color codes are respected. You can add additional translations by starting them with `{lang}` argument, just like in the example. The player will see his language or the default one if it's not defined. You can use conversation variables with this event. Just make sure not to use `%npc%`.

**Example**: `message {en} &4You are banned, %player%! {pl} &4Jestes zbanowany, %player%! {de}&4Ich weiß nicht.`

## Command: `command` _persistent_, _static_

Runs specified command from the console. The instruction string is the command, without leading slash. You can use variables here, but variables other than `%player%` won't resolve if the event is fired from delayed `folder` and the player is offline now. You can define additional commands by separating them with `|` character.

**Example**: `command kill %player%|ban %player%`

## Teleport: `teleport`

Teleports the player to a specified location, with or without head rotation. It will also end the conversation, if the player has one active. The first and only argument must be location. It's a good idea to use yaw and pitch here.

**Example**: `teleport 123;32;-789;world_the_nether;180;45`

## Point: `point` _persistent_

Gives the player a specified amount of points in a specified category. Amount can be negative if you want to subtract points. You can also use an asterisk to do multiplication (or division, if you use a fraction). First argument after the event name must be a category, and the second one - amount of points to give/take/multiply.

**Example**: `point npc_attitude 10`
**Example**: `point village_reputation *0.75`

## Tag: `tag` _persistent_

This event adds (or removes) a tag to the player. The first argument after event's name must be `add` or `del`. Next goes the tag name. It can't contain spaces (though `_` is fine). Additional tags can be added, separated by commas (without spaces).

**Example**: `tag add quest_started,new_entry`

## Objective: `objective` _persistent_

Manages the objectives. Syntax is `objective <action> name`, where `<action>` can be _start_/_add_ (one of the two), _delete_/_remove_ or _complete_/_finish_. Name is the name of the objective, as defined in _objectives.yml_.

**Example**: `objective start wood`

## Journal: `journal`

Adds or deletes an entry to/from player’s journal. Entries are defined in `journal.yml` The first argument is action (add/del), the second one is name of the entry. You can also use only one argument, `update`, it will simply update the journal without addin any entries. It's useful when you need to update the main page.

**Example**: `journal add quest_started`
**Example**: `journal update`

## Lightning: `lightning` _static_

Strikes a lightning at given location. The only argument is the location.

**Example**: `lightning 100;64;-100;survival`

## Explosion: `explosion` _static_

Creates an explosion. It can make fire and destroy blocks. You can also define power, so be careful not to blow your server away. Default TNT power is 4, while Wither on creation is 7. First argument can be 0 or 1 and states if explosion will generate fire (like Ghast’s fireball). Second is also 0 or 1 but this defines if block will be destroyed or not. Third argument is the power (float number). At the end (4th attribute) there is location.

**Example**: `explosion 0 1 4 100;64;-100;survival`

## Give Items: `give`

Gives the player predefined items. They are specified exactly as in `item` condition - list separated by commas, every item can have amount separated by colon. Default amount is 1. If the player doesn't have required space in the inventory, the items are dropped on the ground, unless they are quest items. Then they will be put into the backpack. You can also specify `notify` keyword to display a simple message to the player about receiving items.

**Example**: `give emerald:5,emerald_block:9`

## Take Items: `take`

Removes items from player’s inventory or backpack (in that order). If the items aren't quest items don't use `take` event with player options in conversations! The player can drop items before selecting the option and pickup them after the event fires. Validate it on NPC’s reaction! Defining instruction string is the same as in give event.  You can also specify `notify` keyword to display a simple message to the player about loosing items.

**Example**: `take emerald:120,sword`

## Potion Effect: `effect`

Adds a specified potion effect to player. First argument is potion type. You can find all available types [here](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html). Second is integer defining how long the effect will last in seconds. Third argument, also integer, defines level of the effect (1 means first level). You can also add `--ambient` parameter to make potion particles appear more invisible (just like beacon effects).

**Example**: `effect ABSORPTION 120 1 --ambient`

## Conversation: `conversation`

Starts a conversation at location of the player. The only argument is ID of the conversation. This bypasses the conversation permission!

**Example**: `conversation village_smith`

## Kill: `kill`

Kills the player. Nothing else.

**Example**: `kill`

## Spawn Mob: `spawn` _static_

Spawns specified amount of mobs of given type at the location. First argument is a location. Next is [type of the mob](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html). The last, third argument is integer for amount of mobs to be spawned. You can also specify `name:` argument, followed by the name of the mob. All `_` characters will be replaced with spaces. You can also mark the spawned mob with a keyword using `marked:` argument. It won't show anywhere, and you can check for only marked mobs in `mobkill` objective.

You can specify armor which the mob will wear and items it will hold with `h:` (helmet), `c:` (chestplate), `l:` (leggings), `b:` (boots), `m:` (main hand) and `o:` (off hand) optional arguments. These take a single item without amount, as defined in _items.yml_. You can also add a list of drops with `drops:` argument, followed by a list of items with amounts after colons, separated by commas.

**Example**: `spawn 100;200;300;world SKELETON 5 marked:targets`

**Example**: `spawn 100;200;300;world ZOMBIE name:Bolec 1 h:blue_hat c:red_vest drops:emerald:10,bread:2`

## Time: `time`

Sets or adds time. The only argument is time to be set (integer) or time to be added (integer prefixed with +), in 24 hours format. Subtracting time is done by adding more time (if you think of this, it actually makes sense). Minutes can be achieved with floating point.

**Example**: `time +6`

## Weather: `weather`

Sets weather. The argument is `sun`, `rain` or `storm`.

**Example**: `weather rain`

## Folder: `folder` _persistent_, _static_

It's something like a container for multiple events. You can use it to clarify your code. It also features optional delay measured in seconds (you can use ticks or minutes if you add `ticks` or `minutes` argument). It is persistent for events marked as _persistent_, which means that the events will be fired even after the player logs out. Beware though, all conditions are false then the player is offline (even inverted ones), so those events should not be blocked by any conditions! The only required argument is a list of events separated by commas. There are also two optional arguments: `delay:` and `random:`. Delay is a number of seconds and it's optional (leaving it blank is the same as `delay:0`. Random is the amount of events, that will be randomly chosen to fire. If set to 0 or omited, it does nothing (all events will fire).

**Example**: `folder event1,event2,event3 delay:5 random:1`

## Set Block: `setblock` _persistent_, _static_

Sets a block at given location to specified material. Useful for triggering redstone contraptions. There are two required arguments. First is required, and should be  material's name ([List of materials](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html)). Second is a location and is also required. Last, optional is `data:` with an integer, which defines block's data value. Default is 0.

**Example**: `setblock REDSTONE_BLOCK 100;200;300;world`

## Damage player: `damage`

Damages the player by specified amount of damage. The only argument is a number (can have floating point).

**Example**: `damage 20`

## Party event: `party`

Runs the specified list of events (third argument) for every player in a party. More info about parties in "Party" chapter in **Reference** section.

**Example**: `party 10 has_tag1,!has_tag2 give_reward`

## Clear mobs: `clear`

This event removes all specified mobs from the specified area. The first required argument is a list of mobs (taken from [here](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html)) separated by commas. Next is location. After that there is the radius around the location (a positive number or a variable). You can also optionally specify `name:` argument, followed by name which removed mobs must have. You can use `marked:` argument to remove only mobs marked in `spawn` event.

**Example**: `clear ZOMBIE,CREEPER 100;200;300;world 10 name:Monster`

## Run events: `run`

This event allow for specifying multiple instruction strings in one, longer string. Each instruction must be started with `^` character and no other dividers should be used. It's not the same as `folder` condition, because you can specify an instruction string, not an event name. It is also fired on the same tick, not on the next one like in `folder`. Don't use conditions here, it behaves strangely. I'll fix this in 1.9 version.

**Example**: `run ^tag add beton ^give emerald:5 ^entry add beton ^kill`

## Give journal: `givejournal`

This event simply gives the player his journal. It acts the same way as **/j** command would.

**Example**: `givejournal`

## Sudo: `sudo`

This event is similar to `command` event, the only difference is that it will fire a command as the player.

**Example**: `sudo spawn`

## Chest Give: `chestgive` _persistent_, _static_

This works the same as `give` event, but it puts the items in a chest at specified location. The first argument is a location, the second argument is a list of items, like in `give` event. If the chest is full, the items will be dropped on the ground. The chest can be any other block with inventory, i.e. a hopper or a dispenser. BetonQuest will log an error to the console when this event is fired but there is no chest at specified location.

**Example**: `chestgive 100;200;300;world emerald:5,sword`

## Chest Take: `chesttake` _persistent_, _static_

This event works the same as `take` event, but it takes items from a chest at specified location. The instruction string is defined in the same way as in `chestgive` event.

**Example**: `chesttake 100;200;300;world emerald:5,sword`

## Chest Clear: `chestclear` _persistent_, _static_

This event removes all items from a chest at specified location. The only argument is a location.

**Example**: `chestclear 100;200;300;world`

## Compass: `compass`

When you run this event, the player will be able to select a specified location as a target of his compass. To select the target the player must open his backpack and click on the compass icon. The first argument is either `add` or `del`, and second one is the name of the target, as defined in _main.yml_.

The destination must be defined in the _main.yml_ file in `compass` section. You can specify a name for the target in each language or just give a general name, and optionally add a custom item (from _items.yml_) to be displayed in the backpack. Example of a compass target:

    compass:
      beton:
        name:
          en: Target
          pl: Cel
        location: 100;200;300;world
        item: scroll

**Example**: `compass add beton`

## Cancel quest: `cancel`

This event works in the same way as a quest canceler in the backpack. Running it is equal to the player clicking on the bone. The only argument is a name of a quest canceler, as defined in _main.yml_

**Example**: `cancel wood`

## Scoreboard: `score`

This event works in the same way as `point` event, the only difference is that is uses scoreboards instead of points. You can add, subtract, multiply and divide scores in objectives on the scoreboard. The first argument is the name of the objective, second one is a number. It can be positive for additon, negative for subtraction or prefixed with an asterisk for multiplication. Multiplying by fractions is the same as dividing.

**Example**: `score kills 1`

## Lever: `lever` _persistent_, _static_

This event can switch a lever. The first argument is a location and the second one is state: `on`, `off` or `toggle`.

**Example**: `lever 100;200;300;world toggle`

## Door: `door` _persistent_, _static_

This event can open and close doors, trapdoors and fence gates. The syntax is exactly the same as in `lever` event above.

**Example**: `door 100;200;300;world off`

## If else: `if`

This event will check a condition, and based on the outcome it will run the first or second event. The instruction string is `if condition event1 else event2`, where `condition` is a condition ID and `event1` and `event2` are event IDs. `else` keyword is mandatory between events for no practical reason.

**Example**: `if sun rain else sun`

## Variable: `variable`

This event has only one purpose - to change variables stored in `variable` objective. The first argument is the ID of a `variable` objective (if you use any other type you will get an error). Second one is the key of the variable and the third is the value. Both can use `%...%` variables. Refer to `variable` objective documentation for information about storing variables.

**Example**: `variable some_var_obj name %player%`

## Title: `title`

This event displays a title or a subtitle. The first argument is the type (`title` or `subtitle`), second argument are title's duration times (in ticks) separated by semicolons - fade in, stay and fade out: `20;100;20`. If you set it to three zeros (`0;0;0`) the plugin will use default Minecraft values. After these two required arguments there is a title message, formatted like in the `message` event, which supports multiple languages, color codes and variables. Keep in mind that the subtitle will only appear if the title is visible - that's how Minecraft works.

**Example**: `title subtitle 0;0;0 {en} Lobby joined! {pl} Dołączono do lobby!`

## Language: `language`

This event changes player's language to the specified one. There is only one argument, the language name.

**Example**: `language es`

## Play sound: `playsound`

This event will play a specified sound for the player. The only required argument is the sound name (can take custom values if you're using a resource pack). There are also a few optional arguments. `location:` makes the sound play at specified location, `category:` is the [sound category](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/SoundCategory.html) (if not specified it will use `MASTER`), `volume:` is a decimal responsible for the sound's volume and `pitch:` specifies the pitch.
