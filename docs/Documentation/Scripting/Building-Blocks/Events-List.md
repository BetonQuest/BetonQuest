---
icon: fontawesome/solid/play
toc_depth: 2
---
# Events List

@snippet:events:cancel@

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

## Clear entities: `clear`

This event removes all specified mobs from the specified area. The first required argument is a list of mobs (taken from [here](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html)) separated by commas. Next is location. After that there is the radius around the location (a positive number or a variable). You can also optionally specify `name:` argument, followed by name which removed mobs must have. You can use `marked:` argument to remove only mobs marked in `spawn` event.

!!! example
    ```YAML
    clear ZOMBIE,CREEPER 100;200;300;world 10 name:Monster
    ```

## Compass: `compass`

When you run this event, you can add or remove a compass destination for the player. You may also directly set the player's compass destination as well. When a destination is added the player will be able to select a specified location as a target of his compass. To select the target the player must open his backpack and click on the compass icon. The first argument is `add`,`del` or `set`, and second one is the name of the target, as defined in the _compass_ section. Note that if you set a target the player will not automatically have it added to their choices.

The destination must be defined in `compass` section. You can specify a name for the target in each language or just give a general name, and optionally add a custom item (from _items_ section) to be displayed in the backpack. Example of a compass target:

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

Looking for [run command as player](#sudo-sudo)?

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

Clear all player points in a specified category.

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
Alternatively to `any`, you just can leave it blank.

!!! example
    ```YAML
    deleffect ABSORPTION,BLINDNESS
    deleffect any
    deleffect
    ```

## Potion Effect: `effect`

Adds a specified potion effect to player. First argument is potion type. You can find all available types [here](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html). Second is integer defining how long the effect will last in seconds. Third argument, also integer, defines level of the effect (1 means first level). Add a parameter `ambient` to make potion particles appear more invisible (just like beacon effects). To hide particles add a parameter `hidden`. To hide the icon for the effect add `noicon`.

!!! example
    ```YAML
    effect BLINDNESS 30 1 ambient icon
    ```

## Explosion: `explosion`

**persistent**, **static**

Creates an explosion. It can make fire and destroy blocks. You can also define power, so be careful not to blow your server away. Default TNT power is 4, while Wither on creation is 7. First argument can be 0 or 1 and states if explosion will generate fire (like Ghast's fireball). Second is also 0 or 1 but this defines if block will be destroyed or not. Third argument is the power (float number). At the end (4th attribute) there is location.

!!! example
    ```YAML
    explosion 0 1 4 100;64;-100;survival
    ```

## :material-folder-play: Run multiple events: `folder`

**persistent**, **static**

This event wraps multiple events inside itself. Once triggered, it simply executes it's events.
This is usefully to easily refer to a bunch of events at once, e.g. in a conversation.

Events marked as _persistent_ will be fired even after the player logs out.
Beware though, all conditions are false when the player is offline (even inverted ones),
so those events should not be blocked by any conditions!


| Parameter       | Syntax            | Default Value          | Explanation                                                                                     |
|-----------------|-------------------|------------------------|-------------------------------------------------------------------------------------------------|
| _events to run_ | eventName1,event2 | :octicons-x-circle-16: | One or multiple events to run. Contains event names seperated by commas.                        |
| _delay_         | Keyword           | without delay          | The delay before the folder starts executing it's events.                                       |
| _period_        | period:number     | 1 tick                 | The time between each event. Minimum value is one tick (~20ms).                                 |
| _time unit_     | Keyword           | Seconds                | The unit of time to use for delay and period. Either `ticks` or `minutes`. Omit to use seconds. |
| _random_        | random:number     | Disabled               | Enables "random mode". Will randomly pick the defined amount of events .                        |


```YAML title="Examples" 
events:
  simpleFolder: "folder event1,event2,event3" # (1)!
  runEvents: "folder event1,event2,event3 delay:5 period:1" # (2)!
  troll: "folder killPlayer,banPlayer,kickPlayer delay:5 random:1" # (3)!
  wait: "folder messagePlayer,giveReward delay:1 minutes" # (4)!
```

1. Runs all events after one tick with a delay of one tick between each event.
2. Runs `event1` after an initial delay of 5 seconds, then waits one second before executing each leftover event.
3. Randomly executes one of the three events after 5 seconds.
4. Executes the events after one minute.

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

This works the same way as the normal [point event](#point-point) but instead to manipulating the points for a category of a specific 
player it manipulates points in a global category. These global categories are player independent, so you could for 
example add a point to such a global category every time a player does a quest and give some special rewards for 
the 100th player who does the quest.

!!! example
    ```YAML
    globalpoint global_knownusers 1 action:add
    globalpoint daily_login 0 action:set
    globalpoint reputaion 2 action:multiply
    ```

## Global tag: `globaltag`

**persistent**, **static**

Works the same way as a normal tag event, but instead of setting a tag for one player it sets it globally for all players.

!!! example
    ```YAML
    globaltag add global_areNPCsAgressive
    ```

## Hunger: `hunger`

This event changes the food level of the player. The second argument is the modification type. There are `give`, `take` and `set`. The second argument is the amount. With `set` can the food level be anything. If `give` or `take` is specified and the final amount won't be more than 20 or less than 0. If the hunger level is below 7, the player cannot sprint.

!!! example
    ```YAML
    hunger set 20
    hunger give 5
    ```

## If else: `if`

**persistent**, **static**

This event will check a condition, and based on the outcome it will run the first or second event. The instruction 
string is `if condition event1 else event2`, where `condition` is a condition ID and `event1` and `event2` are event IDs.
 `else` keyword is mandatory between events for no practical reason. Keep in mind that this event is `persistent` 
 and `static` but probably the condition or the events are not.

!!! example
    ```YAML
    if sun rain else sun
    ```

## Journal: `journal`

**static**

Adds or deletes an entry to/from a player's journal. Journal entries have to be defined in the `journal` section. The
first argument is the action to perform, the second one is the name of the entry if required. Changing journal entries
will also reload the journal.

Possible actions are:
- `add`: Adds a page to the journal.
- `delete`: Deletes a page from the journal.
- `update`: Refreshes the journal. This is especially useful when you need to update the main page.

!!! example
    ```YAML
    journal add quest_started
    journal delete quest_available
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

**persistent**

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

@snippet:events:notify@

## Objective: `objective`

**persistent**, **static**

Adds, removes or completes the specified objective(s). 

| Parameter      | Syntax                             | Default Value          | Explanation                             |
|----------------|------------------------------------|------------------------|-----------------------------------------|
| _action_       | Keyword: `add`,`remove`,`complete` | :octicons-x-circle-16: | The action to do with the objective(s). |
| _objective(s)_ | `objectiveName` or `obj1,obj2`     | :octicons-x-circle-16: | The objective(s) to run the action on.  |


Using this in static contexts only works when removing objectives!


```YAML
events:
  startQuest: "objective add killTheDragon,goToDungeon"
  progressQuest: "objective complete killTheDragon"
```

## OPsudo: `opsudo`

This event is similar to the `sudo` event, the only difference is that it will fire a command as the player with temporary OP permissions. 
Additional commands can be defined by separating them with `|` character. If you want to use a `|` character in the message use `\|`.

Looking for [run as normal player](#sudo-sudo)?
Looking for [console commands](#command-command)?

!!! example
    ```YAML
    opsudo spawn
    ```

## Party event: `party`

Runs the specified list of events (third argument) for every player in a [party](../Parties.md).

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
specify the actual instruction, not an event name.
Don't use conditions here, it behaves strangely.

!!! example
    ```YAML
    run ^tag add beton ^give emerald:5 ^entry add beton ^kill
    ```

## Scoreboard: `score`

This event works in the same way as `point` event, the only difference is that is uses scoreboards instead of points. You can add, subtract, multiply and divide scores in objectives on the scoreboard. The first argument is the name of the objective, second one is a number. It can be positive for addition, negative for subtraction or prefixed with an asterisk for multiplication. Multiplying by fractions is the same as dividing.

!!! example
    ```YAML
    score kills 1
    ```

## Set Block: `setblock`

**persistent**, **static**

Changes the block at the given position.
The first argument is a [Block Selector](../Data-Formats.md#block-selectors), the second a location. It's possible to 
deactivate the physics of the block by adding `ignorePhysics` at the end.
Very powerful if used to trigger redstone contraptions.

!!! example
    ```YAML
    setblock REDSTONE_BLOCK 100;200;300;world
    setblock SAND 100;200;300;world ignorePhysics
    ```

## Spawn Mob: `spawn`

**persistent**, **static**

Spawns specified amount of mobs of given type at the location. First argument is a location. Next is [type of the mob](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html). The last, third argument is integer for amount of mobs to be spawned. You can also specify `name:` argument, followed by the name of the mob. All `_` characters will be replaced with spaces. You can also mark the spawned mob with a keyword using `marked:` argument. It won't show anywhere, and you can check for only marked mobs in `mobkill` objective.

You can specify armor which the mob will wear and items it will hold with `h:` (helmet), `c:` (chestplate), `l:` (leggings), `b:` (boots), `m:` (main hand) and `o:` (off hand) optional arguments. These take a single item without amount, as defined in the _items_ section. You can also add a list of drops with `drops:` argument, followed by a list of items with amounts after colons, separated by commas.

!!! example
    ```YAML
    spawn 100;200;300;world SKELETON 5 marked:targets
    ```

!!! example
    ```YAML
    spawn 100;200;300;world ZOMBIE name:Bolec 1 h:blue_hat c:red_vest drops:emerald:10,bread:2
    ```

## Sudo: `sudo`

This event is similar to `command` event, the only difference is that it will fire a command as the player (often referred to as player commands).
Additional commands can be defined by separating them with `|` character. If you want to use a `|` character in the message use `\|`.

Looking for [run as op](#opsudo-opsudo)?
Looking for [console commands](#command-command)?

!!! example
    ```YAML
    sudo spawn
    ```

## Tag: `tag`

**persistent**, **static**

This event adds a tag to or deletes a tag from the player. The first argument after event's name must be
`add` or `delete`. Next goes the tag name. It can't contain spaces (though `_` is fine).
Multiple tags can be added and deleted separated by commas (without spaces).

!!! example
    ```YAML
    tag add quest_started,new_entry
    ```

## Take Items: `take`

Removes items from the player’s inventory, armor slots or backpack.
The items itself must be defined in the `items` section, optionally with an amount after a colon.
Which inventory types are checked is defined by the `invOrder:`
option. You can use `Backpack`, `Inventory`, `Offhand` and `Armor` there. One after another will be checked if multiple types are defined.

Note: If the items aren't quest items don't use `take`event with player options in conversations!
The player can drop items before selecting the option and pickup them after the event fires.
Validate it on the NPC’s reaction!

You can also specify `notify` keyword to display a simple message to the player about loosing items.

!!! example
    ```YAML
    take emerald:120,sword
    take nugget:6 notify
    take wand notify invOrder:Backpack
    take money:50 invOrder:Backpack,Inventory
    take armor invOrder:Armor,Offhand,Inventory,Backpack
    ```

## Time: `time`

**persistent**, **static**

Changes the time of the world. The time is represented in 24 hours format as a float number, so 0 is midnight, 12 is 
noon and 23 is 11 PM. For minutes, you can use floating point numbers, so 0.5 is half past midnight, 0.25 is quarter 
past midnight and so on. (0.1 hours is 6 minutes). It's possible to add or subtract time by using `+` or `-` prefix or 
to set the time by setting no prefix.
Additionally, you can specify the world in which the time will be changed, by adding `world:`. 

!!! example
    ```YAML
    time 6
    time +0.1
    time -12 world:rpgworld
    ```

## :fontawesome-solid-person-walking-dashed-line-arrow-right: Teleport: `teleport`

Teleports the player to the specified location. 
Ends any active conversations. 

Do you only want to [cancel the conversation](#cancels-the-conversation-cancelconversation)?

| Parameter  | Syntax                                                                       | Default Value          | Explanation                                          |
|------------|------------------------------------------------------------------------------|------------------------|------------------------------------------------------|
| _location_ | [Unified Location Formatting](../Data-Formats.md#unified-location-formating) | :octicons-x-circle-16: | The location to which the player will be teleported. |

```YAML title="Example"
events:
  toCity: "teleport 432;121;532;world" # (1)!
  toHell: "teleport 123;32;-789;world_the_nether;180;45" # (2)!
```

1. Teleports the player to X: 432, Y: 121, Z: 532 in the world named 'world'.
2. Teleports the player to X: 123, Y: 32, Z: -789 in the world named 'world_the_nether'. Also sets the head rotation to yaw 180 and pitch 45.

## Variable: `variable`

This event has only one purpose: Change values that are stored in `variable` objective variables. The first argument is
the ID of the `variable` objective. The second argument is the name of the variable to set. The third argument is the
value to set. Both the name and value can use `%...%` variables. To delete a variable you can use `""`. Refer to the
[`variable` objective](Objectives-List.md#variable-variable) documentation for more information about
storing variables. This event will do nothing if the player does not already have a `variable` objective assigned to them.

!!! example
    ```YAML
    variable CustomVariable MyFirstVariable Goodbye!
    variable variable_objectiveID name %player%
    variable other_var_obj desc ""
    ```

## :fontawesome-solid-cloud-sun-rain: Weather: `weather`

**persistent**, **static**  

Sets the weather in the world the player is currently in. The argument is `sun` for clear, sunny weather, `rain` for pure rain,
`storm` for storm with rain, lightning and thunder or `thunder` for just lighting and thunder with no rain.

| Parameter  | Syntax            | Default Value               | Explanation                                                                                                   |
|------------|-------------------|-----------------------------|---------------------------------------------------------------------------------------------------------------|
| _type_     | Keyword           | :octicons-x-circle-16:      | The type of weather to set. Either `sun`, `rain` or `storm`.                                                  |
| _duration_ | `duration:number` | Minecraft decides randomly. | The duration the weather will last (in seconds). Can be a variable.<br> Is handled from minecraft afterwards. |
| _world_    | `world:worldName` | The player's current world. | The world to change the weather in.                                                                           |

```YAML title="Example"
events:
  setSun: "weather sun"
  setShortRain: "weather rain duration:60 world:rpgworld"
  setStorm: "weather storm duration:%point.tribute.left:150%"
```
    
## Give experience: `experience`

This event allows you to manipulate player's experience. First you specify a number as the amount, then the modification action.
You can use `action:addExperience`, `action:addLevel`, `action:setExperienceBar` and `action:setLevel` as modification types.

To use this correctly, you need to understand this:

* A player has experience points.
* Experience levels, shown are shown as a number in the experience bar. Every level requires more experience points than the previous.  
* The experience bar itself shows the percentage of the experience points needed to reach the next level.

While `action:addExperience` only adds experience points, `action:addLevel` adds a level and keeps the current percentage.
`action:setExperienceBar` sets the progress of the bar. Decimal values between `0` and `1` represent the fill level.
This changes the underlying experience points, it's **not** just a visual change.
`action:setLevel` sets only the level, expect if you specify a decimal number, then the experience bar will be
set to the specified percentage.

```YAML title="Example"
add15XP: "experience 15 action:addExperience"
add4andAHalfLevel: "experience 4.5 action:addLevel"
remove2Level: "experience -2 action:addLevel"
setXPBar: "experience 0.5 action:setExperienceBar"
resetLevel: "experience 0.01 action:setLevel"
```

## Burn: `burn`

| Parameter  | Syntax            | Default Value               | Explanation                                                        |
|------------|-------------------|-----------------------------|--------------------------------------------------------------------|
| _duration_ | `duration:number` | :octicons-x-circle-16:      | The duration the player will burn (in seconds). Can be a variable. |

```YAML title="Example"
events:
  burn: "burn duration:4"
  punishing_fire: "burn duration:%point.punishment.amount%"
```
    
## :fontawesome-solid-wind: Move the player: `velocity`

| Parameter      | Syntax                          | Default Value          | Explanation                                                                                                                                                                                                                                                                                                               |
|----------------|---------------------------------|------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| _vector_       | `vector:(x;y;z)`                | :octicons-x-circle-16: | The values of the vector, which are decimal numbers, can be interpreted as absolute numbers like the coordinate or as relative directions. For more understanding the relative direction is similar to `^ ^ ^` in minecraft or in other words `(sideways;upwards;forwards)`. Can be a variable.                           |
| _direction_    | `direction:directionType`       | `absolute`             | There are 3 types how the vector can get applied to the player:<br> `absolute` won't change the vector at all.<br> `relative` will redirect the vector to the view of the player.<br> `relative_y` is a mix between absolute and relative. It will still direct to the view but only horizontally, so y will be absolute. |
| _modification_ | `modification:modificationType` | `set`                  | Possible modifications are `set` and `add`. The modification type determines how the vector should be merged with the player's velocity. The player's velocity is the external force applied on the player.                                                                                                               |

@snippet:general:relativeAxisExplanation@

```YAML title="Example"
events:
  jumppad: "velocity vector:(2;0.8;4)"
  dash: "velocity vector:(0;0.1;1.3) direction:relative_y"
  variable_dash: "velocity vector:%objective.customVariable.dashLength% direction:relative_y"
  fly: "velocity vector:(0;0.1;2) direction:relative modification:add"
  
```

## Cancels the Conversation: `cancelconversation`

  Cancels the active conversation of the player.
  
```YAML title="Example"
  events:
    cancel: "cancelconversation"
```
