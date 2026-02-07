---
icon: octicons/codescan-checkmark-16
tags:
  - Objective
search:
  boost: 2
---
# Objectives List

## <span hidden>`action` -</span> Interact with blocks

__Context__: @snippet:condition-meta:online@  
__Syntax__: `action <action> <block> [location] [range] [cancel] [hand]`  
__Description__: The player has to interact with the specified block.
 
It works great with the location condition and the item in hand condition to further limit the counted clicks.

| Parameter    | Syntax                                                        | Default Value           | Explanation                                                                                                   |
|--------------|---------------------------------------------------------------|-------------------------|---------------------------------------------------------------------------------------------------------------|
| _Click Type_ | `right`, `left` or `any`                                      | :octicons-x-circle-16:  | What type of click should be handled                                                                          |
| _Block Type_ | [Block Selector](../Data-Formats.md#block-selectors) or `any` | :octicons-x-circle-16:  | The block which must be clicked, or `any` for even air                                                        |
| _Location_   | loc:[Location](../Data-Formats.md#unified-location-formating) | Optional. Default: none | Adds an optional location to the objective, only counting blocks clicked at the specific location.            |
| _range_      | range:number                                                  | 0                       | The range around the location where to count the clicks.                                                      |
| _cancel_     | Keyword (`cancel`)                                            | Not Set                 | Prevents the player from interacting with the block.                                                          |
| _hand_       | hand:(`hand`,`off_hand`, `any`)                               | `hand`                  | The hand the player must use to click the block, `any` can the objective cause to be completed multiple times |

```YAML title="Example"
objectives:
  door: "action right DOOR conditions:holding_key loc:100;200;300;world range:5"
  customWand: "action any any conditions:holding_magicWand actions:fireSpell"
```

<h5> Placeholder Properties </h5> 

The objective contains one property, `location`. It's a string formatted like `X: 100, Y: 200, Z:300`. It does not
show the radius.

## <span hidden>`arrow` -</span> Shoot an arrow 

__Context__: @snippet:condition-meta:online@  
__Syntax__: `arrow <location> <radius>`  
__Description__: The player has to shoot an arrow into the specified area.

There are two arguments, location of the target and precision number (radius around location where the arrow must 
land, should be small). Note that the position
of an arrow after hit is on the wall of a _full_ block, which means that shooting not full blocks (like heads) won't
give accurate results. Experiment with this objective a bit to make sure you've set the numbers correctly.

```YAML title="Example"
objectives:
  shootTarget: "arrow 100.5;200.5;300.5;world 1.1 actions:reward conditions:correct_player_position"
```

## <span hidden>`block` -</span> Break or place blocks

__Context__: @snippet:condition-meta:online@  
__Syntax__: `block <block> <amount> [safetyCheck] [notifications] [location] [region] [ignorecancel]`  
__Description__: The player has to break or place the specified amount of blocks.

| Parameter        | Syntax                                               | Default Value                                  | Explanation                                                                                                                                                                                                                                                               |
|------------------|------------------------------------------------------|------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| _Block Type_     | [Block Selector](../Data-Formats.md#block-selectors) | :octicons-x-circle-16:                         | The block which must be broken / placed.                                                                                                                                                                                                                                  |
| _Amount_         | Number                                               | :octicons-x-circle-16:                         | The amount of blocks to break / place. Less than 0 for breaking and more than 0 for placing blocks.                                                                                                                                                                       |
| _Safety Check_   | Keyword (`noSafety`)                                 | Safety Check Enabled                           | The Safety Check prevents faking the objective. The progress will be reduced when the player does to opposite of what they are supposed to do. Example: Player must break 10 blocks. They place 10 of their stored blocks. Now the total amount of blocks to break is 20. |
| _Notifications_  | Keyword (`notify`)                                   | Disabled                                       | Displays messages to the player each time they progress the objective. Optionally with the notification interval after colon.                                                                                                                                             |
| _Location_       | loc:location                                         | Optional. Default: none                        | Adds an optional location to the objective, only counting blocks broken/placed at the specific location.                                                                                                                                                                  |
| _Region definer_ | region:location                                      | Optional. Default: none                        | Adds an optional second location to only count blocks broken/placed in a rectangle between the specified location and this location. This won't have an effect if parameter location isn't set.                                                                           |
| _ignorecancel_   | Keyword (`ignorecancel`)                             | Protected blocks will not affect the objective | Allows the objective to progress, even if the action is cancelled by the Server. For example if the player is not allowed to build.                                                                                                                                       |

```YAML title="Example"
objectives:
  breakLogs: "block .*_LOG -16 actions:reward notify"
  placeBricks: "block BRICKS 64 actions:epicReward notify:5"
  breakIron: "block IRON_ORE -16 noSafety notify actions:dailyReward"
```

<h5> Placeholder Properties </h5> 

Note that these follow the same rules as the amount argument, meaning that blocks to break are a negative number!

| Name     | Example Output | Explanation                                                                                         |
|----------|----------------|-----------------------------------------------------------------------------------------------------|
| _amount_ | -6 / 6         | Shows the amount of blocks already broken / placed.                                                 |
| _left_   | -4 / 4         | Shows the amount of blocks that still need to be broken / placed for the objective to be completed. |
| _total_  | -10 / 10       | Shows the initial amount of blocks that needed to be broken / placed.                               |

You can use these placeholders to always get positive values:

| Name              | Example Output | Explanation                                                                                                  |
|-------------------|----------------|--------------------------------------------------------------------------------------------------------------|
| _absoluteAmount_  | 6              | Shows the absolute amount of blocks already broken / placed.                                                 |
| _absoluteLeft_    | 4              | Shows the absolute amount of blocks that still need to be broken / placed for the objective to be completed. |
| _absoluteTotal_   | 10             | Shows the initial absolute amount of blocks that needed to be broken / placed.                               |


## <span hidden>`breed` -</span> Breed animals

__Context__: @snippet:condition-meta:online@  
__Syntax__: `breed <animal> <amount>`  
__Description__: The player has to breed animals of the specified type.

The first argument is the [animal type](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html) 
and the second argument is the amount (positive integer).
You can add the `notify` argument to display a message with the remaining amount each time
the animal is bred, optionally with the notification interval after a colon. While you can specify any entity, the
objective will be completable only for breedable ones.

This objective has three properties: `amount`, `left` and `total`. `amount` is the amount of animals already breed,
`left` is the amount of animals still needed to breed and `total` is the amount of animals initially required.

```YAML title="Example"
objectives:
  10Cows: "breed cow 10 notify:2 actions:reward"
```

## <span hidden>`chestput` -</span> Put items into a chest

__Context__: @snippet:condition-meta:online@  
__Syntax__: `chestput <location> <items> [items-stay]`  
__Description__: The player has to put the specified items into the specified chest.

First argument is a location of the chest, second argument is a list of items (from _items_ section),
separated with a comma.
You can also add amount of items after a colon.
The items will be removed upon completing the objective unless you add `items-stay` optional argument.
By default, only one player can look into the chest at the same time. You can change it by adding the key 
`multipleaccess`.

```YAML title="Example"
objectives:
  emeraldsAndSword: "chestput 100;200;300;world emerald:5,sword actions:tag,message"
  apples: "chestput 0;50;100;world apple:42 actions:message multipleaccess:true"
```

## <span hidden>`consume` -</span> Consume an item

__Context__: @snippet:condition-meta:online@  
__Syntax__: `consume <item> [amount]`  
__Description__: The player has to consume the specified item. 

| Parameter | Syntax                                | Default Value          | Explanation                               |
|-----------|---------------------------------------|------------------------|-------------------------------------------|
| _Item_    | [Quest Item](../../Features/Items.md) | :octicons-x-circle-16: | The item or potion that must be consumed. |
| _Amount_  | amount:number                         | 1                      | The amount of items to consume.           |


```YAML title="Example"
objectives:
  eatApple: "consume apple actions:faster_endurance_regen"
  eatSteak: "consume steak amount:4 actions:health_boost"
```

<h5> Placeholder Properties </h5> 

| Name     | Example Output | Explanation                                                                                 |
|----------|----------------|---------------------------------------------------------------------------------------------|
| _amount_ | 6              | Shows the amount of items already consumed.                                                 |
| _left_   | 4              | Shows the amount of items that still need to be consumed for the objective to be completed. |
| _total_  | 10             | Shows the initial amount of items that needed to be consumed.                               |


## <span hidden>`craft` -</span> Craft an item

__Context__: @snippet:condition-meta:online@  
__Syntax__: `craft <item> [amount]`  
__Description__: The player has to craft the specified item.

First argument is ID of the item, as in the _items_ section.
Next is amount (integer). You can use the `notify` keyword to display a message each time the player advances the
objective, optionally with the notification interval after a colon.

This objective has three properties: `amount`, `left` and `total`. `amount` is the amount of items already crafted,
`left` is the amount of items still needed to craft and `total` is the amount of items initially required.

```YAML title="Example"
objectives:
  craftSaddle: "craft saddle 5 actions:reward"
```

## <span hidden>`enchant` -</span> Enchant an item

__Context__: @snippet:condition-meta:online@  
__Syntax__: `enchant <item> <enchants> [requirementMode] [amount]`  
__Description__: The player has to enchant the specified item with the specified enchantment. 

| Parameter         | Syntax                                | Default Value          | Explanation                                                                                                                                                                                                                                                                                                                                        |
|-------------------|---------------------------------------|------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| _item_            | [Quest Item](../../Features/Items.md) | :octicons-x-circle-16: | The quest item that must be enchanted.                                                                                                                                                                                                                                                                                                             |
| _enchants_        | enchantment:level                     | :octicons-x-circle-16: | The enchants that must be added to the item. [Enchantment names](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/enchantments/Enchantment.html) are different from the vanilla ones. If a level is present, the enchanted level must be equal or bigger then the specified one. Multiple enchants are supported: `ARROW_DAMAGE:1,ARROW_FIRE:1` |
| _requirementMode_ | requirementMode:mode                  | `all`                  | Use `one` if any enchantment from `enchants` should complete the objective. Use `all` if all are required at the same time.                                                                                                                                                                                                                        |
| _amount_          | amount:number                         | 1                      | The amount of items to enchant.                                                                                                                                                                                                                                                                                                                    |

```YAML title="Example"
objectives:
  lordSword: "enchant lordsSword damage_all,knockback actions:rewardLord"
  kingSword: "enchant kingsSword damage_all:2,knockback:1 actions:rewardKing"
  massProduction: "enchant ironSword sharpness amount:10 actions:blacksmithLevel2Reward"
```

<h5> Placeholder Properties </h5> 

| Name     | Example Output | Explanation                                                                                  |
|----------|----------------|----------------------------------------------------------------------------------------------|
| _amount_ | 6              | Shows the amount of items already enchanted.                                                 |
| _left_   | 4              | Shows the amount of items that still need to be enchanted for the objective to be completed. |
| _total_  | 10             | Shows the initial amount of items that needed to be enchanted.                               |

## <span hidden>`experience` -</span> Gain experience

__Context__: @snippet:condition-meta:online@  
__Syntax__: `experience <amount>`  
__Description__: The player has to reach at least the specified amount of experience levels.

You can also define decimal numbers, for example `experience 1.5` will complete when the player reaches 1.5 experience levels or more.
If you want to check for an absolute amount of experience points you can convert it to decimal levels.
The objective is checked every time the player gets experience naturally, such as killing mobs or mining blocks.
Additionally, it is checked if the player reaches a new level in any way (vanilla level up, commands or other plugins).
The objective will also imminently complete if the player already has the experience level or more.
And it will also be completed if the player joins the game with the specified amount of experience levels or more.
You can use the `notify` keyword to display a message each time the player advances the objective,
optionally with the notification interval after a colon.

This objective has three properties: `amount`, `left` and `total`. `amount` is the current amount of experience levels,
`left` is the amount of experience levels still needed and `total` is the amount of experience required.

```YAML title="Example"
objectives:
  25Level: "experience 25 actions:reward"
```

## <span hidden>`delay` -</span> Wait real time

__Context__: @snippet:condition-meta:online@  
__Syntax__: `delay <time> [unit] [precision]`  
__Description__: The player has to wait for certain amount of real time.

The player must be online and meet all conditions. If the player is not online the objective is completed on the player's
next login.

| Parameter   | Syntax          | Default Value          | Explanation                                                                                                                                        |
|-------------|-----------------|------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------|
| _time_      | Any Number      | :octicons-x-circle-16: | The time after which the objective is completed.                                                                                                   |
| _unit_      | Keyword         | minutes                | The unit of time. Either `minutes`, `seconds` or `ticks`.                                                                                          |
| _precision_ | interval:number | interval:200           | The interval in which the objective checks if the time is up. Measured in ticks. Low values cost more performance but make the objective preciser. |

```YAML title="Example"
objectives:
  waitDay: "delay 1440 actions:resetDaily" #(1)!
  wait50sec: "delay 1000 ticks interval:5 actions:failQuest" #(2)! 
```
   
1. Runs the `resetDaily` action after 1440 minutes (24 hours).
2. Runs the `failQuest` action after 1000 ticks (50 seconds) have passed. The objective checks every 5 ticks (250ms) if the time is up.

<h5> Placeholder Properties </h5> 

| Name         | Example Output                        | Explanation                                                                                                                                                    |
|--------------|---------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| _left_       | 23 days 5 hours 45 minutes 17 seconds | Shows the time left until the objective is completed.                                                                                                          |
| _date_       | 17.04.2022 16:14                      | Shows the date the objective is completed at using the config's [`date_format` setting](../../Configuration/Plugin-Config.md#date_format-the-format-of-dates). |
| _rawSeconds_ | 5482                                  | Shows the amount of seconds until objective completion.                                                                                                        |


## <span hidden>`die` -</span> Die

__Context__: @snippet:condition-meta:online@  
__Syntax__: `die [respawn] [cancel]`  
__Description__: The player has to die. 

If you set the `respawn` location the player will spawn at that location, after pressing respawn,
and the objective will be completed then, not immediately on death.

Optionally you can also add the `cancel` argument to prevent the player from dying.
In this case, the player will be healed and all status effects will be removed.
You can also specify the `respawn` location to which the player will be teleported to.

```YAML title="Example"
objectives:
  respawn: "die respawn:100;200;300;world;90;0 actions:respawned"
  preventDying: "die cancel respawn:100;200;300;world;90;0 actions:respawned"
```

## <span hidden>`fish` -</span> Fish an item

__Context__: @snippet:condition-meta:online@  
__Syntax__: `fish <item> <amount> [hookLocation] [range]`  
__Description__: The player has to catch something with the fishing rod.

It doesn't have to be a fish, it can also be any other item.

| Parameter       | Syntax                                                                 | Default Value          | Explanation                                                                                                            |
|-----------------|------------------------------------------------------------------------|------------------------|------------------------------------------------------------------------------------------------------------------------|
| _Item_          | [Quest Item](../../Features/Items.md)                                  | :octicons-x-circle-16: | The item that must be caught.                                                                                          |
| _amount_        | Any Number                                                             | :octicons-x-circle-16: | The amount that must be caught.                                                                                        |
| _notifications_ | notify:number                                                          | notify:0               | Add `notify` to display a notification when a fish is caught. Optionally with the notification interval after a colon. |
| _hookLocation_  | hookLocation:[Location](../Data-Formats.md#unified-location-formating) | Everywhere             | The location at which the item must be caught. Range must also be defined.                                             |
| _range_         | range:number                                                           | Everywhere             | The range around the `hookLocation`.                                                                                   |



```YAML title="Example"
objectives:
  fisherman: "fish SALMON 5 notify actions:tag_fish_caught" #(1)!
  fishAtPond: "fish COD 5 hookLocation:123;456;789;fishWorld range:10 actions:giveSpecialFish" #(2)!
```

1. Requires the player to catch 5 salmon. The player will get a notification for every caught fish.
2. Requires the player to catch 5 cod. The rod's hook must be used in a 10 block radius around `x:123 y:456 z:789` in a world named `fishWorld`.

<h5> Placeholder Properties </h5>

| Name   | Example Output | Explanation                                                |
|--------|----------------|------------------------------------------------------------|
| left   | 4              | The amount of fish still left to be caught.                |
| amount | 6              | The amount of already caught fish.                         |
| total  | 10             | The initially required amount of fish needed to be caught. |

## <span hidden>`interact` -</span> Interact with an entity

__Context__: @snippet:condition-meta:online@  
__Syntax__: `interact <type> <entity> <amount> [name] [realname] [marked] [hand] [cancel] [location] [range]`  
__Description__: The player has to interact with the specified entities.

| Parameter       | Syntax                                                                                        | Default Value          | Explanation                                                                                                                                       |
|-----------------|-----------------------------------------------------------------------------------------------|------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| _Click Type_    | `right`, `left` or `any`                                                                      | :octicons-x-circle-16: | What type of click should be handled                                                                                                              |
| _Entity Type_   | [EntityType type](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html) | :octicons-x-circle-16: | The entity which must be clicked                                                                                                                  |
| _amount_        | number                                                                                        | :octicons-x-circle-16: | The amount of different entities which must be interacted with.                                                                                   |
| _name_          | name:text                                                                                     | Disabled               | Only count named mobs.                                                                                                                            |
| _realname_      | realname:text                                                                                 | Disabled               | To check for the real name (e.g. if you renamed players to include their rank).                                                                   |
| _marked_        | marked:text                                                                                   | Disabled               | If the clicked entity needs to be marked by the [spawn action](./Actions-List.md#spawn-spawn-a-mob) (see its description for marking explanation) |
| _hand_          | hand:(`hand`,`off_hand`, `any`)                                                               | `hand`                 | The hand the player must use to click the block, `any` can the objective cause to be completed multiple times                                     |
| _Notifications_ | Keyword (_notify_)                                                                            | Disabled               | Displays messages to the player each time they progress the objective. Optionally with the notification interval after colon.                     |
| _Cancel_        | Keyword (_cancel_)                                                                            | Disabled               | if the click shouldn't do what it usually does (i.e. left click won't hurt the entity).                                                           |
| _Location_      | loc:[Location](../Data-Formats.md#unified-location-formating)                                 | Everywhere             | The location at which the entity must be interacted.                                                                                              |
| _range_         | range:number                                                                                  | 1                      | The range around the `loc`. Requires defined `loc`.                                                                                               |

```YAML title="Example"
objectives:
  rightCreeper: "interact right creeper 1 marked:sick conditions:syringeInHand cancel"
```

<h5> Placeholder Properties </h5>

| Name   | Example Output | Explanation                                                |
|--------|----------------|------------------------------------------------------------|
| amount | 7              | The amount of already interacted entities.                 |
| left   | 13             | The amount of entities still needed to be interacted with. |
| total  | 20             | The initially required amount of entities to interact.     |

## <span hidden>`resourcepack` -</span> Have resource pack state

__Context__: @snippet:condition-meta:online@  
__Syntax__: `resourcepack <state>`  
__Description__: The player has to have the specified resource pack state.

The first argument is the state of the resource pack.
It can be `successfully_loaded`, `declined`, `failed_download` and `accepted`.

```YAML title="Example"
objectives:
  successful: "resourcepack successfully_loaded actions:reward"
  declined: "resourcepack declined actions:declined"
```

## <span hidden>`kill` -</span> Kill a player

__Context__: @snippet:condition-meta:online@  
__Syntax__: `kill <amount> [name] [required]`  
__Description__: The player has to kill another player.

The first argument is amount of players to kill.
You can also specify additional arguments: `name:` followed by the name will only accept killing players with this name,
`required:` followed by a list of conditions separated with commas will only accept killing players meeting these conditions
and `notify` will display notifications when a player is killed, optionally with the notification interval after a colon.

The kill objective has three properties: `left` is the amount of players still left to kill, `amount` is the amount of
already killed players and `total` is the initially required amount to kill.

```YAML title="Example"
objectives:
  kill5: "kill 5 required:team_B"
```

## <span hidden>`location` -</span> Reach a location

__Context__: @snippet:condition-meta:online@  
__Syntax__: `location <location> [range] [entry] [exit]`  
__Description__: The player has to reach the specified location.

It is not required to specify `entry` or `exit` then the objective also completes
if the player just moves inside the location's range.

| Parameter  | Syntax                                               | Default Value          | Explanation                                                                               |
|------------|------------------------------------------------------|------------------------|-------------------------------------------------------------------------------------------|
| _location_ | [ULF](../Data-Formats.md#unified-location-formating) | :octicons-x-circle-16: | The location to go to                                                                     |
| _range_    | number                                               | :octicons-x-circle-16: | The range around the location where the player must be.                                   |
| _entry_    | entry                                                | Disabled               | The player must enter (go from outside to inside) the location to complete the objective. |
| _exit_     | exit                                                 | Disabled               | The player must exit (go from inside to outside) the location to complete the objective.  |

```YAML title="Example"
objectives:
  welcome: "location 100;200;300;world 5 conditions:started actions:notifyWelcome,start"
  goodBy: "location 100;200;300;world 5 exit conditions:started actions:notifyBye"
```

<h5> Placeholder Properties </h5> 

| Name       | Example Output        | Explanation                              |
|------------|-----------------------|------------------------------------------|
| _location_ | X: 100, Y: 200, Z:300 | The target location of this objective    |

## <span hidden>`login` -</span> Login

__Context__: @snippet:condition-meta:online@  
__Syntax__: `login`  
__Description__: The player has to log in.

If you use `global` this objective will be also completed directly when a new player joins for the first time.
If you use `persistent` it will be permanent.
Don't forget that if you use global and persistent you can still remove the objective explicitly.

```YAML title="Example"
objectives:
  welcome: "login actions:welcome_message"
```

## <span hidden>`logout` -</span> Logout

__Context__: @snippet:condition-meta:online@  
__Syntax__: `logout`  
__Description__: The player has to log out.

```YAML title="Example"
objectives:
  clean: "logout actions:delete_objective"
```

## <span hidden>`npcinteract` -</span> Interact with an NPC 

__Context__: @snippet:condition-meta:online@  
__Syntax__: `npcinteract <npc> [cancel] [interaction]`  
__Description__: The player has to interact with the specified NPC. 

| Parameter     | Syntax              | Default Value          | Explanation                                                                         |
|---------------|---------------------|------------------------|-------------------------------------------------------------------------------------|
| _Npc_         | Npc                 | :octicons-x-circle-16: | The ID of the Npc.                                                                  |
| _Cancel_      | `cancel`            | False                  | If the interaction with the Npc should be cancelled, so a conversation won't start. |
| _Interaction_ | interaction:Keyword | `right`                | The interaction type. Either `left`, `right` or `any`.                              |

```YAML title="Example"
objectives:
  stealItem: "npcinteract mayor cancel conditions:sneak actions:steal"
  punchThief: "npcinteract thief interaction:left actions:poke"
```

## <span hidden>`npcrange` -</span> Get in range of an NPC

__Context__: @snippet:condition-meta:online@  
__Syntax__: `npcrange <npc <action> <range>`  
__Description__: The player has to enter or leave the specified area around the NPC.

It is also possible to define multiple NPCs separated with `,`.
The objective will be completed as soon as you meet the requirement of just one NPC.

| Parameter | Syntax   | Default Value          | Explanation                                                          |
|-----------|----------|------------------------|----------------------------------------------------------------------|
| _Npcs_    | Npc List | :octicons-x-circle-16: | The IDs of the Npcs                                                  |
| _Action_  | Keyword  | :octicons-x-circle-16: | The required action. Either `enter`, `leave`, `inside` or `outside`. |
| _Range_   | Number   | :octicons-x-circle-16: | The maximum distance to a Npc                                        |

!!! info
    The types `enter`, `leave` force the player to actually enter the radius after you were outside of it and vice versa.
    This means that `enter` is not completed when the player gets the objective and is already in the range, while `inside` is instantly completed.

```YAML title="Example"
objectives:
  goToVillage: "npcrange farmer,guard enter 20 actions:master_inRange"
```

## <span hidden>`password` -</span> Enter a password

__Context__: @snippet:condition-meta:online@  
__Syntax__: `password <password> [prefix] [ignoreCase] [fail]`  
__Description__: The player has to write the specifed password in chat.

All attempts of a player will be hidden from public chat.
The password consists of a prefix followed by the actual secret word:
```
Solution: The Cake is a lie!     
^prefix   ^secret word(s)
```

The objective's instruction string is defined as follows:

1. The first argument is the password, use [quoting](../Quoting-&-YAML.md#quoting) for spaces
   The password is a [regular expression](../Data-Formats.md#regex-regular-expressions). 

2. The prefix can be changed: The default (when no prefix is set) is the translated prefix from the *messages.yml* config in the user's language.             
   Note that every custom prefix is suffixed with `:⠀`, so `prefix:Library_password` will require the user to enter `Library password: myfancypassword`.     
   To disable the prefix use an empty `prefix:` declaration, e.g. `password myfancypassword prefix: actions:success`.
   Be aware of these side effects that come with disabling the prefix:
    
    * Nothing will be hidden on failure, so tries will be visible in chat and commands will get executed!
    * If a command was used to enter the password, the command will not be canceled on success and thus still be executed!    
    * This ensures that even if your password is `quest` you can still execute the `/quest` command. 
   
3. You can also add the `ignoreCase` argument if you want a password's capitalization to be ignored. This is especially important for regex matching.

4. If you want to trigger one or more actions when the player failed to guess the password you can use the argument `fail` with a list of actions (comma separated).
   With disabled prefix every command or chat message will trigger these actions!


```YAML title="Example"
objectives:
  theBetonPassword: "password beton ignoreCase prefix:secret fail:failAction1,failAction2 actions:message,reward"
  theBetonPasswordSpaced: 'password "beton quest" ignoreCase prefix:secret fail:failAction1,failAction2 actions:message,reward'
```

## <span hidden>`pickup` -</span> Pick up an item

__Context__: @snippet:condition-meta:online@  
__Syntax__: `pickup <items> [amount]`  
__Description__: The player has to pick up the specified items.
 
The first argument must be the internal name of an item defined in the `items` section. This can also be a comma-separated list of multiple items.
You can optionally add the `amount:` argument to specify how many of these items the player needs to pickup. 
This amount is a total amount though, it does not count per each individual item. You can use the `notify` keyword to
display a message each time the player advances the objective, optionally with the notification interval after a colon.

You can also add the `notify` keyword to display how many items are left to pickup.

This objective has three properties: `amount`, `left` and `total`. `amount` is the amount of items already picked up,
`left` is the amount of items still needed to pick up and `total` is the amount of items initially required.

```YAML title="Example"
objectives:
  emeralds: "pickup emerald amount:3 actions:reward notify"
  emeraldsAndDiamonds: "pickup emerald,diamond amount:6 actions:reward notify"
```

## <span hidden>`point` -</span> Reach amount of points

__Context__: @snippet:condition-meta:online@  
__Syntax__: `point <category> <amount> [mode] [operation]`  
__Description__: The player has to have the specified amount of points in the specified category.

If the player is not online the objective is completed on the player's next login.

| Parameter   | Syntax           | Default Value          | Explanation                                                                                                                            |
|-------------|------------------|------------------------|----------------------------------------------------------------------------------------------------------------------------------------|
| _category_  | category         | :octicons-x-circle-16: | The category to have the points in.                                                                                                    |
| _amount_    | Number           | :octicons-x-circle-16: | The required amount of points.                                                                                                         |
| _mode_      | mode:mode        | ABSOLUTE               | How the amount should be interpreted. Either `ABSOLUTE` or `RELATIVE`. With relative the current points are added to the target value. |
| _operation_ | operation:symbol | Greater or Equals (>=) | How the actual value is compared to the wanted. The valid operations are: `<`, `<=`, `=`, `!=`, `>=`, `>`.                             |

```YAML title="Example"
objectives:
  reach100: "point counter 100"
  punish: "point reputation -100 operation:<"
  progressFive: "point reputation 5 mode:relative"
```

<h5> Placeholder Properties </h5> 

| Name     | Example Output | Explanation                                              |
|----------|----------------|----------------------------------------------------------|
| _amount_ | 100            | Shows the amount of points to reach.                     |
| _left_   | 8              | Shows the amount of points that still need to be gained. |

## <span hidden>`mobkill` -</span> Kill an entity 

__Context__: @snippet:condition-meta:online@  
__Syntax__: `mobkill <type> <amount> [name] [marked]`  
__Description__: The player has to kill the specified living entities.

All entities work, make sure to use their [correct types](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html).

| Parameter | Syntax                  | Default Value          | Explanation                                                                                                       |
|-----------|-------------------------|------------------------|-------------------------------------------------------------------------------------------------------------------|
| _type_    | ENTITY_TYPE,ENTITY_TYPE | :octicons-x-circle-16: | A list of entities, e.g. `ZOMBIE,SKELETON`.                                                                       |
| _amount_  | Positive Number         | :octicons-x-circle-16: | Amount of mobs to kill in total.                                                                                  |
| _name_    | name:text               | Disabled               | Only count named mobs.                                                                                            |
| _marked_  | marked:keyword          | Disabled               | Only count marked mobs. See the [spawn action](Actions-List.md#spawn-spawn-a-mob) for more information.                 |
| _notify_  | notify:interval         | Disabled               | Display a message to the player each time they kill a mob. Optionally with the notification interval after colon. |

```YAML title="Example"
objectives:
  monsterHunter: "mobkill ZOMBIE,SKELETON,SPIDER 10 notify" #(1)!
  specialMob: "mobkill PIG 1 marked:special" #(2)!
  bossZombie: "mobkill ZOMBIE 1 name:Uber_Zombie" #(3)!
```
   
1. The player must kill a zombie, skeleton or a spider to progress this objective. In total, they must kill 10 entities. Additionally, there will be a notification after each kill.
2. The player must kill a pig that was spawned with the [spawn action](Actions-List.md#spawn-spawn-a-mob) and has a marker. 
3. The player must kill a zombie named "Uber Zombie".


<h5> Placeholder Properties </h5> 

| Name     | Example Output | Explanation                                            |
|----------|----------------|--------------------------------------------------------|
| _amount_ | 2              | Shows the amount of mobs already killed.               |
| _left_   | 8              | Shows the amount of mobs that still need to be killed. |
| _total_  | 10             | Shows the amount of mobs initially required to kill.   |



## <span hidden>`brew` -</span> Brew a potion 

__Context__: @snippet:condition-meta:online@  
__Syntax__: `brew <item> [amount]`  
__Description__: The player has to brew the specified items.

The first argument is a potion ID from the _items_ section. Second argument is amount of potions.
You can optionally add `notify` argument to make the objective display progress to players,
optionally with the notification interval after a colon.

Progress will be counted for the player who last added or changed an item before the brew process completed. Only newly
created potions are counted.

This objective has three properties: `amount`, `left` and `total`. `amount` is the amount of potions already brewed,
`left` is the amount of potions still needed to brew and `total` is the amount of potions initially required.

```YAML title="Example"
objectives:
  weird: "brew weird_concoction 4 actions:add_tag"
```

## <span hidden>`shear` -</span> Shear a sheep 

__Context__: @snippet:condition-meta:online@  
__Syntax__: `shear <amount> [name] [color]`  
__Description__: The player has to shear the specified amount of sheep.

The first, required argument is amount (integer). Optionally, you can add a `name:` argument to only count specific sheep.
If you want to use spaces use [quoting](../Quoting-&-YAML.md#quoting) syntax.
You can also check for the sheep's `color:` using these [color names](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/DyeColor.html).
You can use the `notify` keyword to display a message each time the player advances the objective,
optionally with the notification interval after a colon.

This objective has three properties: `amount`, `left` and `total`. `amount` is the amount of sheep already sheared,
`left` is the amount of sheep still needed to shear and `total` is the amount of sheep initially required.

```YAML title="Example"
objectives:
  bob: "shear 1 name:Bob color:black"
  jeb: "shear 1 name:jeb"
  jeb2: 'shear 1 "name:jeb 2"'
```

## <span hidden>`smelt` -</span> Smelt an item

__Context__: @snippet:condition-meta:online@  
__Syntax__: `smelt <item> [amount]`  
__Description__: The player has to gain the specified item by smelting.

Note that you must define the output item, not the ingredient. The first argument is the name of a [Quest Item](../../Features/Items.md).
The second one is the amount (integer).

You can use the `notify` keyword to display a message each time the player advances the objective,
optionally with the notification interval after a colon.

This objective has three properties: `amount`, `left` and `total`. `amount` is the amount of items already smelted,
`left` is the amount of items still needed to smelt and `total` is the amount of items initially required.

```YAML title="Example"
objectives:
  smeltIron: "smelt ironIngot 5 actions:reward"
```

## <span hidden>`stage` -</span> Complete stages

__Context__: @snippet:condition-meta:online@  
__Syntax__: `stage <stages> [preventCompletion]`  
__Description__: The player has to complete the specified stages.

The Stage objective is a special objective that can be used to track the progress of a quest or a part of a quest.
It can be completed in two ways, the first one is by increasing the stage more than there are stages defined
and the second one is by completing the objective with the [objective action](./Actions-List.md#objective-manage-objectives).
The behaviour of completing the objective by increasing the stage can be disabled by setting the `preventCompletion` flag.

When the conditions of the stage objective are not met, the stage of the player can not be modified.  
You can modify the stages with the [stage action](./Actions-List.md#stage-manage-a-stage-objective) and check it's state with the [stage condition](./Conditions-List.md#stage-compare-stage).

| Parameter           | Syntax              | Default Value          | Explanation                                                          |
|---------------------|---------------------|------------------------|----------------------------------------------------------------------|
| _stages_            | List of stage names | :octicons-x-circle-16: | The stages that must be completed.                                   |
| _preventCompletion_ | Keyword             | Completion Enabled     | Prevents the objective from being completed by increasing the stage. |

```YAML title="Example"
objectives:
  questProgress: "stage part1,part2,part3"
  bakeCookies: "stage collectIngredients,cookCookies,deliverCookies preventCompletion"
```

<h5> Placeholder Properties </h5> 

| Name       | Example Output     | Explanation                                                                    |
|------------|--------------------|--------------------------------------------------------------------------------|
| _index_    | 2                  | The index of the players current stage beginning at 0.                         |
| _current_  | cookCookies        | The current stage name of the player or empty if the objective is not active.  |
| _next_     | deliverCookies     | The next stage name of the player or empty if the objective is not active.     |
| _previous_ | collectIngredients | The previous stage name of the player or empty if the objective is not active. |


## <span hidden>`step` -</span> Step on a pressure plate

__Context__: @snippet:condition-meta:online@  
__Syntax__: `step <location>`  
__Description__: The player has to step on a pressure plate at the specified location.

The type of plate does not matter. The first and only required argument is a location.
If the pressure plate is not present at that location,
the objective will not be completable and will log errors in the console.

Step objective contains one property, `location`. It shows the exact location of the pressure plate in a string
formatted like `X: 100, Y: 200, Z:300`.

```YAML title="Example"
objectives:
  step: "step 100;200;300;world actions:done"
```

## <span hidden>`tag` -</span> Receive a tag

__Context__: @snippet:condition-meta:online@  
__Syntax__: `tag <tag>`  
__Description__: The player has to receive the specified tag. 

The only argument is the tag to receive.

If the player is not online the objective is completed on the player's next login.

```YAML title="Example"
objectives:
  finish: "tag finishedTag"
```

<h5> Placeholder Properties </h5>

The `name` property of the objective is the tag to receive.

## <span hidden>`tame` -</span> Tame an animal

__Context__: @snippet:condition-meta:online@  
__Syntax__: `tame <entity> <amount>`
__Description__: The player has to tame the specified animals.

First argument is type, second is amount. The mob must be tamable for the objective to be valid, e.g.: `CAT`, 
`DONKEY`, `HORSE`, `LLAMA`, `PARROT` or `WOLF`. You
can use the `notify` keyword to display a message each time the player advances the objective, optionally with the
notification interval after a colon.

This objective has three properties: `amount`, `left` and `total`. `amount` is the amount of animals already tamed,
`left` is the amount of animals still needed to tame and `total` is the amount of animals initially required.

```YAML title="Example"
objectives:
  wolf: "tame WOLF 2 actions:wolfs_tamed"
```

## <span hidden>`timer` -</span> Wait ingame time

__Context__: @snippet:condition-meta:online@  
__Syntax__: `timer [name] [interval] [amount] [done]`  
__Description__: The player has to wait for a specified amount of ingame time.

Tracks time in seconds from the start of the objective to the completion of the objective.
If you simply want to have something like wait for 10 minutes, you can use the `amount` argument.
If you don't define the amount, the objective will run indefinitely until you complete it with the [objective action](./Actions-List.md#objective-manage-objectives).

| Parameter  | Syntax          | Default Value | Explanation                                                                         |
|------------|-----------------|---------------|-------------------------------------------------------------------------------------|
| _name_     | name:text       | Disabled      | A display name for the objective that can be accessed as property.                  |
| _interval_ | interval:number | interval:20   | How often the objective checks the conditions and adds time, in seconds.            |
| _amount_   | amount:number   | Disabled      | The amount of time in seconds to track before the objective is completed.           |
| _done_     | done:actions    | Disabled      | Actions that will be executed when the objective is done, but before it is removed. |

If you want to access the time tracked by this objective in seconds, you can use the `amount`, `left` and `total` properties. 
They are only available while the objective is active, this is still the case in the `done` actions, but not in the 
normal `actions` as they are executed after the objective is already removed.

```YAML title="Example"
objectives:
	track: 'timer "name:This is the Display Name" interval:10 done:done_in actions:done conditions:in_region'
```

## <span hidden>`jump` -</span> Jump

__Context__: @snippet:condition-meta:online@  
__Syntax__: `jump <amount>`  
__Description__: The player has to jump.

The only argument is amount. You can use the `notify` keyword to display a
message each time the player advances the objective, optionally with the notification interval after a colon.

This objective has three properties: `amount`, `left` and `total`. `amount` is the amount of jumps already done,
`left` is the amount of jumps still needed and `total` is the amount of jumps initially required.

```YAML title="Example"
objectives:
  jump: "jump 15 actions:legExerciseDone"
```

## <span hidden>`ride` -</span> Ride an entity

__Context__: @snippet:condition-meta:online@  
__Syntax__: `ride <entity>`  
__Description__: The player has to ride the specified [entity](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html).

`any` is also a valid input and matches any entity.

```YAML title="Example"
objectives:
  horse: "ride horse"
  any: "ride any"
```

## <span hidden>`command` -</span> Execute a command

__Context__: @snippet:condition-meta:online@  
__Syntax__: `command <command> [ignoreCase] [exact] [cancel] [failActions]`  
__Description__: The player has to execute the specified command.

It can be both an existing or a new, custom command. The first argument is the command text.
To allow spaces use [quoting](../Quoting-&-YAML.md#quoting) syntax.
The command argument is case-sensitive and also supports using placeholders.
The second required argument is a list of actions to execute when the objective ismet.

```YAML title="Example"
objectives:
  warp: 'command "/warp %player% farms" actions:action1,action2'
  replace: 'command "//replace oak_wood" actions:action1,action2'
```

With this configuration, the command objective requires the player to execute `/warp MyName farms` to be completed. The
command objective matches from the start of the command that was executed, therefore if the player executed
`/warp MyName farms other arguments` it would still be completed.

Optional arguments:

* `ignoreCase`: If provided, instructs the objective to ignore case for the command to match.
* `exact`: If provided, requires an exact command match, not just the command start.
* `cancel`: If provided, the objective will cancel the execution of the command on a match. This needs to be enabled to suppress the `Unknown Command` message when using custom commands.
* `failActions`: If provided, specifies a list of actions to execute if a non-matching command is run and conditions are met.

```YAML title="Example"
objectives:
  warp: 'command "/warp %player% farms" ignoreCase exact cancel failActions:failAction1,failAction2 actions:action1,action2'
```

## <span hidden>`equip` -</span> Equip armor

__Context__: @snippet:condition-meta:online@  
__Syntax__: `equip <slot> <item>`  
__Description__: The player has to equip the specified item to the specified slot.

The item must be any quest item as defined in the _items_ section.
Available slot types: `HEAD`, `CHEST`, `LEGS`, `FEET`.

```YAML title="Example"
objectives:
  eqHelm: "equip HEAD amazing_helmet actions:action1,action2"
  equipBody: "equip CHEST amazing_armor actions:action1,action2"
```

## <span hidden>`variable` -</span> Variable storage

__Context__: @snippet:condition-meta:online@  
__Syntax__: `variable [no-chat]`  
__Description__: This objective is unable to complete.

This objective is different. You cannot complete it, it will also ignore defined actions and conditions. You can start it and that's it.
While this objective is active though, everything the player types in chat (and matches a special pattern) will become a variable.
The pattern is `key: value`. So if the player types `MyFirstVariable: Hello!`, it will create a variable called `MyFirstVariable`, which will resolve as a `Hello!` string.
You can access them as objective properties. Let's say you defined this objective as `CustomVariable` in your _objectives.yml_ file.
You can access the placeholder everywhere with `%objective.CustomVariable.MyFirstVariable%` - and in this example, it will resolve to `Hello!`.
The player can type something else and the variable will change its value. Variables are per-player, so the value of one player's `MyFirstVariable`
will be different from other players' `MyFirstVariable` values, depending on what they typed in chat. There is no limit to the amount of variables that can be created and assigned to players.
To remove this objective, use `objective delete` action - there is no other way.

You can also use `variable` action to change variables stored in this objective. There is one optional argument, `no-chat`. If you use it, the objective won't be modified 
by what players type in chat which is only useful when you're also using the `variable` action.

Also, the key is interpreted in lower case. That means there is no difference between `MyFirstVariable`, `myfirstvariable` or `MYfirstVARIABLE`.

```YAML title="Example"
objectives:
  storage: "variable"
  storeChat: "variable no-chat"
```
