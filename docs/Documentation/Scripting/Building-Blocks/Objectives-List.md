---
icon: octicons/codescan-checkmark-16
---
# Objectives List

## Action: `action`

This objective completes when the player clicks on the given block type. 
It works great with the location condition and the item in hand condition to further limit the counted clicks.

| Parameter    | Syntax                                                        | Default Value           | Explanation                                                                                                   |
|--------------|---------------------------------------------------------------|-------------------------|---------------------------------------------------------------------------------------------------------------|
| _Click Type_ | `right`, `left` or `any`                                      | :octicons-x-circle-16:  | What type of click should be handled                                                                          |
| _Block Type_ | [Block Selector](../Data-Formats.md#block-selectors) or `any` | :octicons-x-circle-16:  | The block which must be clicked, or `any` for even air                                                        |
| _Location_   | loc:[Location](../Data-Formats.md#unified-location-formating) | Optional. Default: none | Adds an optional location to the objective, only counting blocks clicked at the specific location.            |
| _range_      | range:number                                                  | 0                       | The range around the location where to count the clicks.                                                      |
| _cancel_     | Keyword (`cancel`)                                            | Not Set                 | Prevents the player from interacting with the block.                                                          |
| _hand_       | hand:(`hand`,`off_hand`, `any`)                               | `hand`                  | The hand the player must use to click the block, `any` can the objective cause to be completed multiple times |

!!! example
    ```YAML
    action right DOOR conditions:holding_key loc:100;200;300;world range:5
    action any any conditions:holding_magicWand events:fireSpell #Custom click listener for a wand
    ```

<h5> Variable Properties </h5> 

The objective contains one property, `location`. It's a string formatted like `X: 100, Y: 200, Z:300`. It does not
show the radius.

## Arrow Shooting: `arrow`

To complete this objective the player needs to shoot the arrow into the target. There are two arguments, location of the
target and precision number (radius around location where the arrow must land, should be small). Note that the position
of an arrow after hit is on the wall of a _full_ block, which means that shooting not full blocks (like heads) won't
give accurate results. Experiment with this objective a bit to make sure you've set the numbers correctly.

!!! example
    ```YAML
    arrow 100.5;200.5;300.5;world 1.1 events:reward conditions:correct_player_position
    ```

## :material-pickaxe: Break or Place Blocks: `block`

To complete this objective the player must break or place the specified amount of blocks.

| Parameter        | Syntax                                               | Default Value                                  | Explanation                                                                                                                                                                                                                                                               |
|------------------|------------------------------------------------------|------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| _Block Type_     | [Block Selector](../Data-Formats.md#block-selectors) | :octicons-x-circle-16:                         | The block which must be broken / placed.                                                                                                                                                                                                                                  |
| _Amount_         | Number                                               | :octicons-x-circle-16:                         | The amount of blocks to break / place. Less than 0 for breaking and more than 0 for placing blocks.                                                                                                                                                                       |
| _Safety Check_   | Keyword (`noSafety`)                                 | Safety Check Enabled                           | The Safety Check prevents faking the objective. The progress will be reduced when the player does to opposite of what they are supposed to do. Example: Player must break 10 blocks. They place 10 of their stored blocks. Now the total amount of blocks to break is 20. |
| _Notifications_  | Keyword (`notify`)                                   | Disabled                                       | Displays messages to the player each time they progress the objective. Optionally with the notification interval after colon.                                                                                                                                             |
| _Location_       | loc:location                                         | Optional. Default: none                        | Adds an optional location to the objective, only counting blocks broken/placed at the specific location.                                                                                                                                                                  |
| _Region definer_ | region:location                                      | Optional. Default: none                        | Adds an optional second location to only count blocks broken/placed in a rectangle between the specified location and this location. This won't have an effect if parameter location isn't set.                                                                           |
| _ignorecancel_   | Keyword (`ignorecancel`)                             | Protected blocks will not affect the objective | Allows the objective to progress, even if the event is cancelled by the Server. For example if the player is not allowed to build.                                                                                                                                        |

  
```YAML
objectives:
  breakLogs: "block .*_LOG -16 events:reward notify"
  placeBricks: "block BRICKS 64 events:epicReward notify:5"
  breakIron: "block IRON_ORE -16 noSafety notify events:dailyReward"
```

<h5> Variable Properties </h5> 

Note that these follow the same rules as the amount argument, meaning that blocks to break are a negative number!

| Name     | Example Output | Explanation                                                                                         |
|----------|----------------|-----------------------------------------------------------------------------------------------------|
| _amount_ | -6 / 6         | Shows the amount of blocks already broken / placed.                                                 |
| _left_   | -4 / 4         | Shows the amount of blocks that still need to be broken / placed for the objective to be completed. |
| _total_  | -10 / 10       | Shows the initial amount of blocks that needed to be broken / placed.                               |

You can use these variables to always get positive values:

| Name              | Example Output | Explanation                                                                                                  |
|-------------------|----------------|--------------------------------------------------------------------------------------------------------------|
| _absoluteAmount_  | 6              | Shows the absolute amount of blocks already broken / placed.                                                 |
| _absoluteLeft_    | 4              | Shows the absolute amount of blocks that still need to be broken / placed for the objective to be completed. |
| _absoluteTotal_   | 10             | Shows the initial absolute amount of blocks that needed to be broken / placed.                               |


## Breed animals: `breed`

This objective is completed by breeding animals of specified type. The first argument is the
[animal type](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html) and the second argument is the
amount (positive integer). You can add the `notify` argument to display a message with the remaining amount each time
the animal is bred, optionally with the notification interval after a colon. While you can specify any entity, the
objective will be completable only for breedable ones.

This objective has three properties: `amount`, `left` and `total`. `amount` is the amount of animals already breed,
`left` is the amount of animals still needed to breed and `total` is the amount of animals initially required.

!!! example
    ```YAML
    breed cow 10 notify:2 events:reward
    ```

## Put items in a chest: `chestput`

This objective requires the player to put specified items in a specified chest. First argument is a location of the
chest, second argument is a list of items (from _items_ section), separated with a comma. You can also add amount of
items after a colon. The items will be removed upon completing the objective unless you add `items-stay` optional
argument. By default, only one player can look into the chest at the same time. You can change it by adding the key 
`multipleaccess`.

!!! example
    ```YAML
    chestput 100;200;300;world emerald:5,sword events:tag,message
    chestput 0;50;100;world apple:42 events:message multipleaccess:true
    ```

## :material-food-fork-drink: Eat/drink: `consume`

This objective is completed by eating the specified food or drinking the specified potion. 

| Parameter | Syntax                                | Default Value          | Explanation                               |
|-----------|---------------------------------------|------------------------|-------------------------------------------|
| _Item_    | [Quest Item](../../Features/Items.md) | :octicons-x-circle-16: | The item or potion that must be consumed. |
| _Amount_  | amount:number                         | 1                      | The amount of items to consume.           |


```YAML
objectives:
  eatApple: "consume apple events:faster_endurance_regen"
  eatSteak: "consume steak amount:4 events:health_boost"
```

<h5> Variable Properties </h5> 

| Name     | Example Output | Explanation                                                                                 |
|----------|----------------|---------------------------------------------------------------------------------------------|
| _amount_ | 6              | Shows the amount of items already consumed.                                                 |
| _left_   | 4              | Shows the amount of items that still need to be consumed for the objective to be completed. |
| _total_  | 10             | Shows the initial amount of items that needed to be consumed.                               |


## Crafting: `craft`

To complete this objective the player must craft specified item. First argument is ID of the item, as in the _items_ section.
Next is amount (integer). You can use the `notify` keyword to display a message each time the player advances the
objective, optionally with the notification interval after a colon.

This objective has three properties: `amount`, `left` and `total`. `amount` is the amount of items already crafted,
`left` is the amount of items still needed to craft and `total` is the amount of items initially required.

!!! example
    ```YAML
    craft saddle 5 events:reward
    ```

## :fontawesome-solid-wand-magic-sparkles: Enchant item: `enchant`

This objective is completed when the player enchants the specified quest item with the specified enchantment. 

| Parameter         | Syntax                                | Default Value          | Explanation                                                                                                                                                                                                                                                                                                                                        |
|-------------------|---------------------------------------|------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| _item_            | [Quest Item](../../Features/Items.md) | :octicons-x-circle-16: | The quest item that must be enchanted.                                                                                                                                                                                                                                                                                                             |
| _enchants_        | enchantment:level                     | :octicons-x-circle-16: | The enchants that must be added to the item. [Enchantment names](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/enchantments/Enchantment.html) are different from the vanilla ones. If a level is present, the enchanted level must be equal or bigger then the specified one. Multiple enchants are supported: `ARROW_DAMAGE:1,ARROW_FIRE:1` |
| _requirementMode_ | requirementMode:mode                  | `all`                  | Use `one` if any enchantment from `enchants` should complete the objective. Use `all` if all are required at the same time.                                                                                                                                                                                                                        |
| _amount_          | amount:number                         | 1                      | The amount of items to enchant.                                                                                                                                                                                                                                                                                                                    |

```YAML title="Example"
lordSword: "enchant lordsSword damage_all,knockback events:rewardLord"
kingSword: "enchant kingsSword damage_all:2,knockback:1 events:rewardKing"
massProduction: "enchant ironSword sharpness amount:10 events:blacksmithLevel2Reward"
```

<h5> Variable Properties </h5> 

| Name     | Example Output | Explanation                                                                                  |
|----------|----------------|----------------------------------------------------------------------------------------------|
| _amount_ | 6              | Shows the amount of items already enchanted.                                                 |
| _left_   | 4              | Shows the amount of items that still need to be enchanted for the objective to be completed. |
| _total_  | 10             | Shows the initial amount of items that needed to be enchanted.                               |

## Experience: `experience`

This objective can be completed by reaching the specified amount of experience levels.
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

!!! example
    ```YAML
    experience 25 level events:reward
    ```

##:material-clock-time-two-outline: Wait: `delay` 

This objective completes itself after certain amount of time.
The player must be online and meet all conditions. If the player is not online the objective is completed on the player's
next login.

| Parameter   | Syntax          | Default Value          | Explanation                                                                                                                                        |
|-------------|-----------------|------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------|
| _time_      | Any Number      | :octicons-x-circle-16: | The time after which the objective is completed.                                                                                                   |
| _unit_      | Keyword         | minutes                | The unit of time. Either `minutes`, `seconds` or `ticks`.                                                                                          |
| _precision_ | interval:number | interval:200           | The interval in which the objective checks if the time is up. Measured in ticks. Low values cost more performance but make the objective preciser. |

``` YAML title="Example"
objectives:
  waitDay: "delay 1440 events:resetDaily" #(1)!
  wait50sec: "delay 1000 ticks interval:5 events:failQuest" #(2)! 
```
   
1. Runs the `resetDaily` event after 1440 minutes (24 hours).
2. Runs the `failQuest` event after 1000 ticks (50 seconds) have passed. The objective checks every 5 ticks (250ms) if the time is up.

<h5> Variable Properties </h5> 

| Name         | Example Output                        | Explanation                                                                                                                                  |
|--------------|---------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------|
| _left_       | 23 days 5 hours 45 minutes 17 seconds | Shows the time left until the objective is completed.                                                                                        |
| _date_       | 17.04.2022 16:14                      | Shows the date the objective is completed at using the config's `date_format` [setting](../../Configuration/Configuration.md#misc-settings). |
| _rawSeconds_ | 5482                                  | Shows the amount of seconds until objective completion.                                                                                      |


## Death: `die`

The death objective is completed when a player dies while fulfilling all conditions.
If you set the `respawn` location the player will spawn at that location, after pressing respawn,
and the objective will be completed then, not immediately on death.

Optionally you can also add the `cancel` argument to prevent the player from dying.
In this case, the player will be healed and all status effects will be removed.
You can also specify the `respawn` location to which the player will be teleported to.

!!! example
    ```YAML
    die respawn:100;200;300;world;90;0 events:respawned
    die cancel respawn:100;200;300;world;90;0 events:respawned
    ```

## :fontawesome-solid-fish-fins: Fishing: `fish`

Requires the player to catch something with the fishing rod. It doesn't have to be a fish, it can also be any other item.

| Parameter       | Syntax                                                                 | Default Value          | Explanation                                                                                                            |
|-----------------|------------------------------------------------------------------------|------------------------|------------------------------------------------------------------------------------------------------------------------|
| _item_          | [Block Selector](../Data-Formats.md#block-selectors)                   | :octicons-x-circle-16: | The item that must be caught.                                                                                          |
| _amount_        | Any Number                                                             | :octicons-x-circle-16: | The amount that must be caught.                                                                                        |
| _notifications_ | notify:number                                                          | notify:0               | Add `notify` to display a notification when a fish is caught. Optionally with the notification interval after a colon. |
| _hookLocation_  | hookLocation:[Location](../Data-Formats.md#unified-location-formating) | Everywhere             | The location at which the item must be caught. Range must also be defined.                                             |
| _range_         | range:number                                                           | Everywhere             | The range around the `hookLocation`.                                                                                   |



```YAML title="Example"
objectives:
  fisherman: "fish SALMON 5 notify events:tag_fish_caught" #(1)!
  fishAtPond: "fish COD 5 hookLocation:123;456;789;fishWorld range:10 events:giveSpecialFish" #(2)!
```

1. Requires the player to catch 5 salmon. The player will get a notification for every caught fish.
2. Requires the player to catch 5 cod. The rod's hook must be used in a 10 block radius around `x:123 y:456 z:789` in a world named `fishWorld`.

<h5> Variable Properties </h5>

| Name   | Example Output | Explanation                                                |
|--------|----------------|------------------------------------------------------------|
| left   | 4              | The amount of fish still left to be caught.                |
| amount | 6              | The amount of already caught fish.                         |
| total  | 10             | The initially required amount of fish needed to be caught. |

## Interact with entity: `interact`

The player must click on an entity to complete this objective. The first argument is the type of a click.
Available values are `right`, `left` and `any`.
Second required argument is the [mob type](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html).
Next is an amount of mobs required to click on. These must be unique, so the player can't simply click twenty times on
the same zombie to complete it. There is also an optional `name:` parameter which specifies what custom name the entity must have
(you need to write `_` instead of the space character). To check for the real name (e.g. if you renamed players to include
their rank) you can also use `realname:` instead.
Add `marked:` if the clicked entity needs to be marked by the `spawn` event (see its description for marking explanation). 
You can also add `notify` argument to make the objective notify players whenever they click a correct entity,
optionally with the notification interval after colon and `cancel` if the click shouldn't do what it usually does
(i.e. left click won't hurt the entity). This can be limited with an optional `loc` and `range` attribute to limit within a range of a location.

This objective has three properties: `amount`, `left` and `total`. `amount` is the amount of entities already interacted
with, `left` is the amount of entities still needed to be interacted with and `total` is the amount of entities
initially required.

!!! example
    ```YAML
    interact right creeper 1 marked:sick condition:syringeInHand cancel
    ```
    
## Resource pack state: `resourcepack`
**:fontawesome-solid-list-check:{.task} Objective  ·  :fontawesome-solid-paper-plane: Requires [Paper](https://papermc.io)**

To complete this objective the player must have the specified resource pack state.
The first argument is the state of the resource pack.
It can be `successfully_loaded`, `declined`, `failed_download` and `accepted`.

!!! example
    ```YAML
    resourcepack successfully_loaded events:reward
    resourcepack declined events:declined
    ```

## Kill player: `kill`

To complete this objective the player needs to kill another player. The first argument is amount of players to kill.
You can also specify additional arguments: `name:` followed by the name will only accept killing players with this name,
`required:` followed by a list of conditions separated with commas will only accept killing players meeting these conditions
and `notify` will display notifications when a player is killed, optionally with the notification interval after a colon.

The kill objective has three properties: `left` is the amount of players still left to kill, `amount` is the amount of
already killed players and `total` is the initially required amount to kill.

!!! example
    ```YAML
    kill 5 required:team_B
    ```

## Location: `location`

The specified location where the player needs to be.
It is not required to specify `entry` or `exit` then the objective also completes
if the player just moves inside the location's range.

| Parameter  | Syntax                                               | Default Value          | Explanation                                                                               |
|------------|------------------------------------------------------|------------------------|-------------------------------------------------------------------------------------------|
| _location_ | [ULF](../Data-Formats.md#unified-location-formating) | :octicons-x-circle-16: | The location to go to                                                                     |
| _range_    | number                                               | :octicons-x-circle-16: | The range around the location where the player must be.                                   |
| _entry_    | entry                                                | Disabled               | The player must enter (go from outside to inside) the location to complete the objective. |
| _exit_     | exit                                                 | Disabled               | The player must exit (go from inside to outside) the location to complete the objective.  |

!!! example
    ```YAML
    location 100;200;300;world 5 condition:started events:notifyWelcome,start
    location 100;200;300;world 5 exit conditions:started events:notifyBye
    ```
<h5> Variable Properties </h5> 

| Name       | Example Output        | Explanation                              |
|------------|-----------------------|------------------------------------------|
| _location_ | X: 100, Y: 200, Z:300 | The target location of this objective    |

## Login: `login`

To complete this objective the player simply needs to login to the server.
If you use `global` this objective will be also completed directly when a new player joins for the first time.
If you use `persistent` it will be permanent.
Don't forget that if you use global and persistent you can still remove the objective explicitly.

!!! example
    ```YAML
    login events:welcome_message
    ```

## Logout: `logout`

To complete this objective the player simply needs to leave the server. Keep in mind that running a `folder` event here
will make it run in "persistent" mode, since the player is offline on the next tick.

!!! example
    ```YAML
    logout events:delete_objective
    ```

## Password: `password`

This objective requires the player to write a certain password in chat. All attempts of a player will be hidden from public chat.
The password consists of a prefix followed by the actual secret word:
```
Solution: The Cake is a lie!     
^prefix   ^secret word(s)
```

The objective's instruction string is defined as follows:

1. The first argument is the password, use underscore characters (`_`) instead of spaces.
   The password is a [regular expression](../Data-Formats.md#regex-regular-expressions). 

2. The prefix can be changed: The default (when no prefix is set) is the translated prefix from the *messages.yml* config in the user's language.             
   Note that every custom prefix is suffixed with `:⠀`, so `prefix:Library_password` will require the user to enter `Library password: myfancypassword`.     
   To disable the prefix use an empty `prefix:` declaration, e.g. `password myfancypassword prefix: events:success`.
   Be aware of these side effects that come with disabling the prefix:
    
    * Nothing will be hidden on failure, so tries will be visible in chat and commands will get executed!
    * If a command was used to enter the password, the command will not be canceled on success and thus still be executed!    
    * This ensures that even if your password is `quest` you can still execute the `/quest` command. 
   
3. You can also add the `ignoreCase` argument if you want a password's capitalization to be ignored. This is especially important for regex matching.

4. If you want to trigger one or more events when the player failed to guess the password you can use the argument `fail` with a list of events (comma separated).
   With disabled prefix every command or chat message will trigger these events!


```YAML
objectives:
  theBetonPassword: "password beton ignoreCase prefix:secret fail:failEvent1,failEvent2 events:message,reward"
```

## Pickup item: `pickup`

To complete this objective you need to pickup the specified amount of items. 
The first argument must be the internal name of an item defined in the `items` section. This can also be a comma-separated list of multiple items.
You can optionally add the `amount:` argument to specify how many of these items the player needs to pickup. 
This amount is a total amount though, it does not count per each individual item. You can use the `notify` keyword to
display a message each time the player advances the objective, optionally with the notification interval after a colon.

You can also add the `notify` keyword to display how many items are left to pickup.

This objective has three properties: `amount`, `left` and `total`. `amount` is the amount of items already picked up,
`left` is the amount of items still needed to pick up and `total` is the amount of items initially required.

!!! example
    ```YAML
    pickup emerald amount:3 events:reward notify
    pickup emerald,diamond amount:6 events:reward notify
    ```

## :material-skull: Entity Kill: `mobkill`

The player must kill the specified amount of entities (living creatures).
All entities work, make sure to use their [correct types](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html).

| Parameter | Syntax                  | Default Value          | Explanation                                                                                                             |
|-----------|-------------------------|------------------------|-------------------------------------------------------------------------------------------------------------------------|
| _type_    | ENTITY_TYPE,ENTITY_TYPE | :octicons-x-circle-16: | A list of entities, e.g. `ZOMBIE,SKELETON`.                                                                             |
| _amount_  | Positive Number         | :octicons-x-circle-16: | Amount of mobs to kill in total.                                                                                        |
| _name_    | name:text               | Disabled               | Only count named mobs. Spaces must be replaced with `_`.                                                                |
| _marked_  | marked:keyword          | Disabled               | Only count marked mobs. See the [spawn event](Events-List.md#spawn-mob-spawn) for more information. Supports variables. |
| _notify_  | notify:interval         | Disabled               | Display a message to the player each time they kill a mob. Optionally with the notification interval after colon.       |

``` YAML title="Example"
objectives:
  monsterHunter: "mobkill ZOMBIE,SKELETON,SPIDER 10 notify" #(1)!
  specialMob: "mobkill PIG 1 marked:special" #(2)!
  bossZombie: "mobkill ZOMBIE 1 name:Uber_Zombie" #(3)!
```
   
1. The player must kill a zombie,skeleton or a spider to progress this objective. In total, they must kill 10 entities. Additionally, there will be a notification after each kill.
2. The player must kill a pig that was spawned with the [spawn event](Events-List.md#spawn-mob-spawn) and has a marker. 
3. The player must kill a zombie named "Uber Zombie".


<h5> Variable Properties </h5> 

| Name     | Example Output | Explanation                                            |
|----------|----------------|--------------------------------------------------------|
| _amount_ | 2              | Shows the amount of mobs already killed.               |
| _left_   | 8              | Shows the amount of mobs that still need to be killed. |
| _total_  | 10             | Shows the amount of mobs initially required to kill.   |



## Potion brewing: `brew`

To complete this objective the player needs to brew specified amount of specified potions.
The first argument is a potion ID from the _items_ section. Second argument is amount of potions.
You can optionally add `notify` argument to make the objective display progress to players,
optionally with the notification interval after a colon.

Progress will be counted for the player who last added or changed an item before the brew process completed. Only newly
created potions are counted.

This objective has three properties: `amount`, `left` and `total`. `amount` is the amount of potions already brewed,
`left` is the amount of potions still needed to brew and `total` is the amount of potions initially required.

!!! example
    ```YAML
    brew weird_concoction 4 event:add_tag
    ```

## Sheep shearing: `shear`

To complete this objective the player has to shear specified amount of sheep, optionally with specified color and/or
name. The first, required argument is amount (integer). Optionally, you can add a `name:` argument to only count specific sheep.
All underscores will be replaced by spaces - if you want to use underscores, put a `\` before them.
You can also check for the sheep's `color:` using these [color names](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/DyeColor.html).
You can use the `notify` keyword to display a message each time the player advances the objective,
optionally with the notification interval after a colon.

This objective has three properties: `amount`, `left` and `total`. `amount` is the amount of sheep already sheared,
`left` is the amount of sheep still needed to shear and `total` is the amount of sheep initially required.

!!! example
    ```YAML
    shear 1 name:Bob color:black
    shear 1 name:jeb\_
    "shear 1 name:jeb\\_" #Use two backslashes if quoted
    ```

## Smelting: `smelt`

To complete this objective the player must smelt the specified item. Note that you must define the output item, not the
ingredient. The first argument is the name of a [Quest Item](../../Features/Items.md).
The second one is the amount (integer).

You can use the `notify` keyword to display a message each time the player advances the objective,
optionally with the notification interval after a colon.

This objective has three properties: `amount`, `left` and `total`. `amount` is the amount of items already smelted,
`left` is the amount of items still needed to smelt and `total` is the amount of items initially required.

```YAML title="Example"
smeltIron: "smelt ironIngot 5 events:reward"
```

## Stages: `stage`
The Stage objective is a special objective that can be used to track the progress of a quest or a part of a quest.
It can be completed in two ways, the first one is by increasing the stage more than there are stages defined
and the second one is by completing the objective with the [objective event](./Events-List.md#objective-objective).
The behaviour of completing the objective by increasing the stage can be disabled by setting the `preventCompletion` flag.

When the conditions of the stage objective are not met, the stage of the player can not be modified.  
You can modify the stages with the [stage event](./Events-List.md#modify-stage-stage) and check it's state with the [stage condition](./Conditions-List.md#check-stage-stage).

| Parameter           | Syntax              | Default Value          | Explanation                                                          |
|---------------------|---------------------|------------------------|----------------------------------------------------------------------|
| _stages_            | List of stage names | :octicons-x-circle-16: | The stages that must be completed.                                   |
| _preventCompletion_ | Keyword             | Completion Enabled     | Prevents the objective from being completed by increasing the stage. |

```YAML title="Example"
objectives:
  questProgress: "stage part1,part2,part3"
  bakeCookies: "stage collectIngredients,cookCookies,deliverCookies preventCompletion"
```

<h5> Variable Properties </h5> 

| Name       | Example Output     | Explanation                                                                    |
|------------|--------------------|--------------------------------------------------------------------------------|
| _index_    | 2                  | The index of the players current stage beginning at 1.                         |
| _current_  | cookCookies        | The current stage name of the player or empty if the objective is not active.  |
| _next_     | deliverCookies     | The next stage name of the player or empty if the objective is not active.     |
| _previous_ | collectIngredients | The previous stage name of the player or empty if the objective is not active. |


## Step on pressure plate: `step`

To complete this objective the player has to step on a pressure plate at a given location. The type of plate does not
matter. The first and only required argument is a location. If the pressure plate is not present at that location, the
objective will not be completable and will log errors in the console.

Step objective contains one property, `location`. It shows the exact location of the pressure plate in a string
formatted like `X: 100, Y: 200, Z:300`.

!!! example
    ```YAML
    step 100;200;300;world events:done
    ```

## Taming: `tame`

To complete this objective player must tame some amount of mobs. First argument is type, second is amount. The mob must
be tamable for the objective to be valid, e.g.: `CAT`, `DONKEY`, `HORSE`, `LLAMA`, `PARROT` or `WOLF`. You
can use the `notify` keyword to display a message each time the player advances the objective, optionally with the
notification interval after a colon.

This objective has three properties: `amount`, `left` and `total`. `amount` is the amount of animals already tamed,
`left` is the amount of animals still needed to tame and `total` is the amount of animals initially required.

!!! example
    ```YAML
    tame WOLF 2 events:wolfs_tamed
    ```
   

## Player must Jump: `jump`
**:fontawesome-solid-list-check:{.task} Objective  ·  :fontawesome-solid-paper-plane: Requires [Paper](https://papermc.io)**

To complete this objective the player must jump. The only argument is amount. You can use the `notify` keyword to display a
message each time the player advances the objective, optionally with the notification interval after a colon.

This objective has three properties: `amount`, `left` and `total`. `amount` is the amount of jumps already done,
`left` is the amount of jumps still needed and `total` is the amount of jumps initially required.

!!! example
    ```YAML
    jump 15 events:legExerciseDone
    ```

## Ride an entity: `ride`

This objective can be completed by riding the specified
[entity](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html).
`any` is also a valid input and matches any entity.

!!! example
    ```YAML
    ride horse
    ride any
    ```

## Run a Command: `command`

To complete this objective the player must execute a specified command. It can be both an existing or a new, custom
command. The first argument is the command text. Use `_` in place of spaces for the command. If you need an actual `_`
in your command, you must escape it using a backslash (`\`, see example below). The command argument is case-sensitive
and also supports using placeholders. The second required argument is a list of events to execute when the objective is
met.

!!! example
    ```YAML
    command /warp_%player%_farms events:event1,event2
    command //replace_oak\_wood events:event1,event2
    ```

With this configuration, the command objective requires the player to execute `/warp MyName farms` to be completed. The
command objective matches from the start of the command that was executed, therefore if the player executed
`/warp MyName farms other arguments` it would still be completed.

Optional arguments:

* `ignoreCase`: If provided, instructs the objective to ignore case for the command to match.
* `exact`: If provided, requires an exact command match, not just the command start.
* `cancel`: If provided, the objective will cancel the execution of the command on a match. This needs to be enabled to suppress the `Unknown Command` message when using custom commands.
* `failEvents`: If provided, specifies a list of events to execute if a non-matching command is run and conditions are met.

!!! complex example
    ```YAML
    command /warp_%player%_farms ignoreCase exact cancel failEvents:failEvent1,failEvent2 events:event1,event2
    ```

!!! warning
    Sometimes you want to use actual underscores in your command. These will however be replaced with spaces by default.
    You can "escape" them using backslashes:
    One backslash (`\`) is required when using no quoting at all (`...`) or single quotes
    (`'...'`). Two backslashes are required (`\\`) when using double quotes (`"..."`).

    Examples:<br>
    `eventName: command /enchant_@s_minecraft:aqua_affinity` :arrow_right: `eventName:command /enchant_@s_minecraft:aqua{++\++}_affinity`<br>
    `eventName: {=='==}command /enchant_@s_minecraft:aqua_affinity{=='==}` :arrow_right: `eventName: {=='==}command /enchant_@s_minecraft:aqua{++\++}_affinity{=='==}`<br>
    `eventName: {=="==}command /enchant_@s_minecraft:aqua_affinity{=="==}` :arrow_right: `eventName: {=="==}command /enchant_@s_minecraft:aqua{++\\++}_affinity{=="==}`<br>

## Equip Armor Item: `equip`
**:fontawesome-solid-list-check:{.task} Objective  ·  :fontawesome-solid-paper-plane: Requires [Paper](https://papermc.io)**

The player must equip the specified quest item in the specified slot.
The item must be any quest item as defined in the _items_ section.
Available slot types: `HEAD`, `CHEST`, `LEGS`, `FEET`.

```YAML
equip HEAD amazing_helmet events:event1,event2
equip CHEST amazing_armor events:event1,event2
```

## Variable: `variable`

This objective is different. You cannot complete it, it will also ignore defined events and conditions. You can start it and that's it.
While this objective is active though, everything the player types in chat (and matches a special pattern) will become a variable.
The pattern is `key: value`. So if the player types `MyFirstVariable: Hello!`, it will create a variable called `MyFirstVariable`, which will resolve as a `Hello!` string.
These are not global variables, you can access them as objective properties. Let's say you defined this objective as `CustomVariable` in your _objectives.yml_ file.
You can access the variable in any conversation, event or condition with `%objective.CustomVariable.MyFirstVariable%` - and in the case of this example, it will resolve to `Hello!`.
The player can type something else and the variable will change its value. Variables are per-player, so the value of one player's `MyFirstVariable`
will be different from other players' `MyFirstVariable` values, depending on what they typed in chat. There is no limit to the amount of variables that can be created and assigned to players.
To remove this objective, use `objective delete` event - there is no other way.

You can also use `variable` event to change variables stored in this objective. There is one optional argument, `no-chat`. If you use it, the objective won't be modified 
by what players type in chat which is only useful when you're also using the `variable` event.

!!! example
    ```YAML
    variable
    ```
