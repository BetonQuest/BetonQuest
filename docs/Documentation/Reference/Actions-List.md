---
icon: octicons/play-16
tags:
  - Action
search:
  boost: 2
---
# Actions List

## `BoostElytra`

__Context__: @snippet:action-meta:online@  
__Syntax__: `boostelytra [item] [power]`  
__Description__: Boosts the player's elytra using the specified item or power.

This action has no effect if the player is currently not flying with an elytra.
The power overrides the item's power if both are specified.

| Parameter         | Type                          | Explanation                                                           |
|-------------------|-------------------------------|-----------------------------------------------------------------------|
| item<br>[Item]    | Optional<br>[firework_rocket] | The optional item to use as a booster. Has to be a `firework_rocket`. |
| power<br>[Number] | Optional<br>[1]               | An optional power override. Has to be in the inclusive range 0-127.   |

```YAML title="Examples"
actions:
  simpleBoost: "boostelytra"
  justPower: "boostelytra power:2" 
  justItem: "boostelytra item:my_rocket"
  powerOverride: "boostelytra item:my_rocket power:5"
```

## `Burn`

__Context__: @snippet:action-meta:online@  
__Syntax__: `burn <duration> [unit]`  
__Description__: Ignite the player for the specified duration.

| Parameter            | Type                  | Explanation                                        |
|----------------------|-----------------------|----------------------------------------------------|
| duration<br>[Number] | Required              | The duration the player will burn.                 |
| unit<br>[TimeUnit]   | Optional<br>[seconds] | The unit of the duration time. Default is seconds. |

```YAML title="Examples"
actions:
  burn: "burn 30 unit:ticks"
  punishing_fire: "burn %point.punishment.amount%"
```

## `Cancel`

__Context__: @snippet:action-meta:online@  
__Syntax__: `cancel <canceler> {bypass}`  
__Description__: Call the specified quest canceler.

This action works in the same way as a [quest canceler in the backpack](../Advanced/Quest-Cancelers.md).
Running this action is equal to the player canceling a quest using the backpack.

| Parameter                                                            | Type                  | Explanation                                                                                                                   |
|----------------------------------------------------------------------|-----------------------|-------------------------------------------------------------------------------------------------------------------------------|
| canceler<br>[[Identifier]](./Definition-Encyclopedia.md#identifiers) | Required              | The identifier of the quest canceler to execute.                                                                              |
| bypass<br>[Boolean]                                                  | Flag<br>[false, true] | If the canceler conditions should be ignored. If enabled the canceler will be executed, even when its conditions are not met. |

```YAML title="Examples"
actions:
  cancelSmallQuest: "cancel smallQuest"
  cancelWoodQuest: "cancel woodQuest bypass"
  cancelWaterQuest: "cancel _-package>waterQuest bypass:%tag.startedWaterQuest%"
```

## `CancelConversation`

__Context__: @snippet:action-meta:online@  
__Syntax__: `cancelconversation {skipDelay}`  
__Description__: Cancel the conversation that is currently active for the player.

| Parameter              | Type                  | Explanation                                                                                                                            |
|------------------------|-----------------------|----------------------------------------------------------------------------------------------------------------------------------------|
| skipDelay<br>[Boolean] | Flag<br>[false, true] | If the interceptor delay should be skipped and the conversation interceptor ended immediately. This also ends any delayed interceptor. |

```YAML title="Examples"
actions:
  cancel: "cancelconversation"
  cancelInstant: "cancelconversation skipDelay"
```

## `Chat`

__Context__: @snippet:action-meta:online@  
__Syntax__: `chat <messages>`  
__Description__: Send the message in chat as the player.

Therefore, it will look like as if the player did send the message.
The instruction string is the command, without leading slash. You can only use `%player%` as a placeholder in this action.
Additional messages can be defined by separating them with `|` character. If you want to use a `|` character in the message use `\|`.

If a plugin does not work with the sudo / command action you need to use this action.

```YAML title="Examples"
actions:
  sendMSG: "chat Hello!"
  sendMultipleMSGs: "chat Hi %player%|ban %player%|pardon %player%"
  sendPluginCommand: "chat /someCommand x y z"
```

## `ChestClear`

__Context__: @snippet:action-meta:independent@  
__Syntax__: `chestclear <location>`  
__Description__: Remove all items from the chest at the specified location.

| Parameter                                                                      | Type     | Explanation                                                                                                                              |
|--------------------------------------------------------------------------------|----------|------------------------------------------------------------------------------------------------------------------------------------------|
| location<br>[[Location]](./Definition-Encyclopedia.md#unified-location-format) | Required | The location of the chest to be cleared. In case there is no chest at the given location or it could not be cleared, an error is thrown. |

```YAML title="Examples"
actions:
  chestclear: "chestclear 100;200;300;world"
```

## `ChestGive`

__Context__: @snippet:action-meta:independent@  
__Syntax__: `chestgive <location> <items> {dropExcess}`  
__Description__: Put items into the chest at the specified location.

This works the same as the [`give` action](#give), but it puts the items in a block with an inventory at the specified location.  
At the location may be any block with an inventory, i.e., a hopper or a dispenser.  
If the chest is full, the items will be dropped on the ground if the `dropExcess` flag is present or set to `true`.

| Parameter                                                                      | Type                  | Explanation                                                                                                                            |
|--------------------------------------------------------------------------------|-----------------------|----------------------------------------------------------------------------------------------------------------------------------------|
| location<br>[[Location]](./Definition-Encyclopedia.md#unified-location-format) | Required              | The location of the block of which the inventory should be filled. In case there is no chest at the given location an error is thrown. |
| items<br>List[Item]                                                            | Required              | The items with their optional amounts.                                                                                                 |
| dropExcess<br>[Boolean]                                                        | Flag<br>[false, true] | If the excess amount of items that won't fit into the chest should be dropped on the ground.                                           |

```YAML title="Examples"
actions:
  chestgive: "chestgive 100;200;300;world emerald:5,sword dropExcess"
  nodrop: "chestgive 100;200;300;world emerald:5,sword"
```

## `ChestTake`

__Context__: @snippet:action-meta:independent@  
__Syntax__: `chesttake <location> <items> {abort} [fail]`  
__Description__: Take items from the chest at the specified location.

This action works the same as the [`take` action](#take), but it takes the items from a block with an inventory at the specified location.
The instruction accepts the same `abort` and `fail` options.

| Parameter                                                                      | Type                  | Explanation                                                                                                                                           |
|--------------------------------------------------------------------------------|-----------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------|
| location<br>[[Location]](./Definition-Encyclopedia.md#unified-location-format) | Required              | The location of the block with inventory of which the items should be taken from. In case there is no chest at the given location an error is thrown. |
| items<br>List[Item]                                                            | Required              | The items with their optional amounts.                                                                                                                |
| abort<br>[Boolean]                                                             | Flag<br>[false, true] | No items will be taken if not all specified items are present.                                                                                        |
| fail<br>List[Identifier]                                                       | Optional<br>[None]    | If the action fails to take all items, those actions will be executed.                                                                                |

```YAML title="Examples"
actions:
  chesttake: "chesttake 100;200;300;world emerald:5,sword"
  finishRiddle: "chesttake 300;200;100;world stone:8,artifact:2,sand:3 abort fail:thunder"
```

## `Command`

__Context__: @snippet:action-meta:independent@  
__Syntax__: `command <commands>`  
__Description__: Run the specified commands from the console.

The instruction string is the command, without leading slash.
You can use placeholders here, but placeholders other than `%player%` won't resolve if the action is fired from
delayed `folder` and the player is offline now.
You can define additional commands by separating them with `|` character.
If you want to use a `|` character in the command use `\|`.

Looking for [execute as player commands](#command)?
Looking for [execute as operator commands](#opsudo)?

```YAML title="Examples"
actions:
  killAndBan: "command kill %player%|ban %player%"
```

## `Compass`

__Context__: @snippet:action-meta:online-offline@  
__Syntax__: `compass <operation> <target>`  
__Description__: Manage the compass destinations for the player.

When you run this action, you can add or remove a compass destination for the player. You may also directly set the player's compass destination as well.
When a destination is added the player will be able to select a specified location as a target of his compass.
To select the target the player must open his backpack and click on the compass icon.
Note that if you set a target the player will not automatically have it added to their choices.

The destination must be defined in `compass` section.
You can specify a name for the target in each language or just give a general name, and optionally add a custom item (from _items_ section) to be displayed in the backpack.

| Parameter                | Type     | Explanation                                                  |
|--------------------------|----------|--------------------------------------------------------------|
| operation<br>[CompassOp] | Required | The operation to perform.                                    |
| target<br>[Identifier]   | Required | The name of the target, as defined in the `compass` section. |

```YAML title="Example compass configuration"
compass:
  beton:
    name:
      en-US: Target
      pl-PL: Cel
    location: 100;200;300;world
    item: scroll
```

```YAML title="Examples"
actions:
  compassBeton: "compass add beton"
```

*[CompassOp]: add, del, set

## `Conversation`

__Context__: @snippet:action-meta:online@  
__Syntax__: `conversation <conversation> [option]`  
__Description__: Start a conversation.

This bypasses the conversation permission!
When using the optional `option` argument, the conversation will start without its header.

| Parameter                    | Type               | Explanation                                       |
|------------------------------|--------------------|---------------------------------------------------|
| conversation<br>[Identifier] | Required           | The ID of the conversation.                       |
| option<br>[String]           | Optional<br>[None] | The NPC option where the conversation will start. |

```YAML title="Examples"
actions:
  startConversation: "conversation village_smith"
  startConversationOption: "conversation tutorial option:explain_world"
```

## `Damage`

__Context__: @snippet:action-meta:online@  
__Syntax__: `damage <amount>`  
__Description__: Apply the specified amount of damage to the player.

| Parameter          | Type     | Explanation                                                        |
|--------------------|----------|--------------------------------------------------------------------|
| amount<br>[Number] | Required | The absolute amount of damage to apply. Can have a floating point. |

```YAML title="Examples"
actions:
  dealDamage: "damage 20"
```

## `DelEffect`

__Context__: @snippet:action-meta:online@  
__Syntax__: `deleffect <effects>`  
__Description__: Remove the specified potion effects from the player.

| Parameter               | Type     | Explanation                                                   |
|-------------------------|----------|---------------------------------------------------------------|
| effects<br>List[String] | Required | The list of potion effects to remove, or `any` to remove all. |

```YAML title="Examples"
actions:
  deleteEffects: "deleffect ABSORPTION,BLINDNESS"
  deleteAny: "deleffect any"
```

## `DeleteGlobalPoint`

__Context__: @snippet:action-meta:independent@  
__Syntax__: `deleteglobalpoint <category>`  
__Description__: Delete the global point category.

See also the [`deletepoint` action](#deletepoint).

| Parameter                                                            | Type     | Explanation                            |
|----------------------------------------------------------------------|----------|----------------------------------------|
| category<br>[[Identifier]](./Definition-Encyclopedia.md#identifiers) | Required | The name of the global point category. |

```YAML title="Examples"
actions:
  deleteBonus: "deleteglobalpoint bonus"
```

## `DeletePoint`

__Context__: @snippet:action-meta:online-offline-independent@    
__Syntax__: `deletepoint <category>`  
__Description__: Delete the point category for the player.

The independent context will delete the points for all players in the database (even if offline).

See also the [`deleteglobalpoint` action](#deleteglobalpoint).

| Parameter                                                            | Type     | Explanation                     |
|----------------------------------------------------------------------|----------|---------------------------------|
| category<br>[[Identifier]](./Definition-Encyclopedia.md#identifiers) | Required | The name of the point category. |

```YAML title="Examples"
actions:
  deletePoints: "deletepoint npc_attitude"
```

## `Door`

__Context__: @snippet:action-meta:independent@  
__Syntax__: `door <location> <operation>`  
__Description__: Open and close doors, trapdoors, and fence gates.

The syntax is exactly the same as in `lever` action above.

```YAML title="Examples"
actions:
  close: "door 100;200;300;world off"
```

## `Drop`

__Context__: @snippet:action-meta:online-independent@  
__Syntax__: `drop <items> [location]`  
__Description__: Drop the specified items at a location.

If no location is specified, the items are dropped at the player's current position.
In an independent context, omitting the location will cause an error.

| Parameter                                                                      | Type               | Explanation                                                                                              |
|--------------------------------------------------------------------------------|--------------------|----------------------------------------------------------------------------------------------------------|
| items<br>List[Item]                                                            | Required           | A list of items to drop. Each item can have an optional amount separated by a colon (e.g., `emerald:5`). |
| location<br>[[Location]](./Definition-Encyclopedia.md#unified-location-format) | Optional<br>[None] | The location where the items should be dropped. If omitted, the player's current location is used.       |

```YAML title="Examples"
actions:
  dropSword: "drop items:magical_sword location:200;17;300;world"
  dropRare: "drop items:loot_rare,loot_common:3"
  dropMyItem: "drop items:myItem location:%objective.MyQuestPlaceholder.DropLocation%"
```

## `Effect`

__Context__: @snippet:action-meta:online@  
__Syntax__: `effect <effect> <duration> <level> {ambient} {noicon} {hidden}`  
__Description__: Apply the specified potion effect to the player.

| Parameter            | Type                  | Explanation                                                                                               |
|----------------------|-----------------------|-----------------------------------------------------------------------------------------------------------|
| effect<br>[String]   | Required              | See [PotionEffectType](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html). |
| duration<br>[Number] | Required              | How long the effect will last in seconds.                                                                 |
| level<br>[Number]    | Required              | Level of the effect.                                                                                      |
| ambient<br>[Boolean] | Flag<br>[false, true] | Make potion particles appear more often and translucent.                                                  |
| noicon<br>[Boolean]  | Flag<br>[false, true] | Hide the icon for the effect.                                                                             |
| hidden<br>[Boolean]  | Flag<br>[false, true] | Hide particles.                                                                                           |

```YAML title="Examples"
actions:
  effectBlindness: "effect BLINDNESS 30 1 ambient noicon"
```

## `Equip`

__Context__: @snippet:action-meta:online@  
__Syntax__: `equip <item> <slot> {drop} {force}`  
__Description__: Put the specified item into the player's equipment slot.

By default, the item is only equipped if the slot is empty. The `drop` flag drops the new item when the slot is occupied.
The `force` flag replaces an existing item, and combining it with `drop` drops the replaced item.

| Parameter                     | Type                  | Explanation                                                                            |
|-------------------------------|-----------------------|----------------------------------------------------------------------------------------|
| item<br>[Item]                | Required              | The item to equip.                                                                     |
| slot<br>[PlayerEquipmentSlot] | Required              | The equipment slot in which the item should be placed.                                 |
| drop<br>[Boolean]             | Flag<br>[false, true] | Whether an item that cannot remain in the equipment slot should be dropped.            |
| force<br>[Boolean]            | Flag<br>[false, true] | Whether an item already in the equipment slot should be replaced by the specified one. |

```YAML title="Examples"
actions:
  equipHelmet: "equip diamond_helmet HEAD"
  replaceHelmet: "equip quest_helmet HEAD drop force"
```

## `Eval`

__Context__: @snippet:action-meta:independent@  
__Syntax__: `eval <expression>`  
__Description__: Evaluate the expression and execute the resulting action.

This action allows you to resolve an expression containing placeholders, and the result will then be interpreted
again as an action.

```YAML title="Examples"
actions:
  simpleEval: 'eval notify "This is actually an eval action evaluating to a notify action."'
  complexEval: "eval point ranking 5 action:add %objective.settings.notify%" #(1)!
```

1. This could evaluate to `point ranking 5 action:add notify` and will add 5 points to the ranking category and notify the player.
   But the placeholder could also be empty and add 5 points without notifying the player. This is not possible in a normal action.

## `Experience`

__Context__: @snippet:action-meta:online@  
__Syntax__: `experience <amount> <action>`  
__Description__: Manipulate the player's experience bar.

To use this correctly, you need to understand this:

* A player has experience points.
* Experience levels are shown as a number in the experience bar. Every level requires more experience points than the previous.
* The experience bar itself shows the percentage of the experience points needed to reach the next level.

While `action:addExperience` only adds experience points, `action:addLevel` adds a level and keeps the current percentage.
`action:setExperienceBar` sets the progress of the bar. Decimal values between `0` and `1` represent the fill level.
This changes the underlying experience points, it's **not** just a visual change.
`action:setLevel` sets only the level, except if you specify a decimal number, then the experience bar will be
set to the specified percentage.

| Parameter          | Type     | Explanation                                                 |
|--------------------|----------|-------------------------------------------------------------|
| amount<br>[Number] | Required | The amount of experience or levels to use in the operation. |
| action<br>[XPType] | Required | The operation to perform on the player's experience.        |

```YAML title="Examples"
actions:
  add15XP: "experience 15 action:addExperience"
  add4andAHalfLevel: "experience 4.5 action:addLevel"
  remove2Level: "experience -2 action:addLevel"
  setXPBar: "experience 0.5 action:setExperienceBar"
  resetLevel: "experience 0.01 action:setLevel"
```

*[XPType]: addExperience, addLevel, setExperienceBar, setLevel

## `Explosion`

__Context__: @snippet:action-meta:independent@  
__Syntax__: `explosion <fire> <block> <power> <location>`  
__Description__: Create an explosion at the specified location.

You can also define power, so be careful not to blow your server away.
Default TNT power is 4, while Wither on creation is 7.

| Parameter                                                                      | Type     | Explanation                                         |
|--------------------------------------------------------------------------------|----------|-----------------------------------------------------|
| fire<br>[Boolean]                                                              | Required | If the explosion will generate fire.                |
| block<br>[Boolean]                                                             | Required | If blocks will be destroyed.                        |
| power<br>[Number]                                                              | Required | The power of the explosion.                         |
| location<br>[[Location]](./Definition-Encyclopedia.md#unified-location-format) | Required | The location where the explosion should be created. |

```YAML title="Examples"
actions:
  explosion: "explosion 0 1 4 100;64;-100;survival"
```

## `First`

__Context__: @snippet:action-meta:independent@  
__Syntax__: `first <actions>`  
__Description__: Execute the first possible action.

This action wraps multiple actions inside itself, similar `folder`. Unlike `folder`, it attempts to execute each action,
starting from the first onward. Once it successfully executes one action, it stops executing the rest. This is useful for
collapsing long if-else chains into single actions.

This action is especially powerful when it is used in conjunction with the `conditions:` keyword,
which can be used with any action.

```YAML title="Examples"
actions:
  firstExample: "first action1,action2,action3" # (1)!
  action1: "point carry boxes 10 action:add conditions:firstCondition"
  action2: "point carry boxes 20 action:add conditions:secondCondition"
  action3: "point carry boxes 40 action:add conditions:thirdCondition"
```

1. If firstCondition is false, secondCondition is true, and thirdCondition is true, action2 is the only action that will
   be run.

??? info "More intricate variant using another action"
    ```YAML title="Equivalent using if-else"
    actions:
      firstExample: "if firstCondition action1 else firstExample2"
      firstExample2: "if secondCondition action2 else firstExample3"
      firstExample3: "if thirdCondition action3"
      action1: "point carry boxes 10 action:add"
      action2: "point carry boxes 20 action:add"
      action3: "point carry boxes 40 action:add"
    ```

## `Folder`

__Context__: @snippet:action-meta:independent@  
__Syntax__: `folder <actions> [delay] [period] [unit] [random] [cancelOnLogout] [cancelConditions]`  
__Description__: Run multiple actions in sequence.

This action wraps multiple actions inside itself. Once triggered, it simply executes its actions.
This is useful to easily refer to a bunch of actions at once, e.g. in a conversation.

Actions marked with the offline icon @snippet:action-meta:offline@ will be fired even if the player is not online.
Beware though, all conditions are false when the player is offline (even inverted ones),
so those actions should not be blocked by any conditions!  
You can use the `cancelOnLogout` argument to stop the folder executing any remaining actions if the player disconnects.


| Parameter          | Syntax                       | Default Value          | Explanation                                                                           |
|--------------------|------------------------------|------------------------|---------------------------------------------------------------------------------------|
| _actions to run_   | actionName1,action2          | :octicons-x-circle-16: | One or multiple actions to run. Contains action names separated by commas.            |
| _delay_            | Keyword                      | without delay          | The delay before the folder starts executing its actions.                             |
| _period_           | period:number                | without delay          | The time between each action of the folder.                                           |
| _time unit_        | unit:unit                    | Seconds                | The unit of time to use for delay and period. Either `ticks`, `minutes` or `seconds`. |
| _random_           | random:number                | Disabled               | Enables "random mode". Will randomly pick the defined amount of actions .             |
| _cancelOnLogout_   | Keyword                      | Disabled               | If enabled, the folder will stop executing actions if the player disconnects.         |
| _cancelConditions_ | cancelConditions:cond1,cond2 | Disabled               | If enabled, the folder will stop executing actions if the conditions are true.        |


```YAML title="Examples"
actions:
  simpleFolder: "folder action1,action2,action3" # (1)!
  runActions: "folder action1,action2,action3 delay:5 period:1" # (2)!
  troll: "folder killPlayer,banPlayer,kickPlayer delay:5 random:1" # (3)!
  wait: "folder messagePlayer,giveReward delay:1 unit:minutes" # (4)!
```

1. Runs all actions after one tick with a delay of one tick between each action.
2. Runs `action1` after an initial delay of 5 seconds, then waits one second before executing each leftover action.
3. Randomly executes one of the three actions after 5 seconds.
4. Executes the actions after one minute.

## `Give`

__Context__: @snippet:action-meta:online@  
__Syntax__: `give <items> {notify} {backpack}`  
__Description__: Give the player all specified items.

If the player doesn't have required space in the inventory, the items are dropped on the ground,
unless they are quest items. Then they will be put into the backpack.

| Parameter              | Type                  | Explanation                                                                |
|------------------------|-----------------------|----------------------------------------------------------------------------|
| items<br>List[Item]    | Required              | The items with their optional amounts.                                     |
| notify<br>[Boolean]    | Flag<br>[false, true] | Display a simple message to the player about receiving items.              |
| backpack<br>[Boolean]  | Flag<br>[false, true] | Force quest items to be placed in the backpack.                            |

```YAML title="Examples"
actions:
  giveEmeralds: "give emerald:5,emerald_block:9"
  giveSign: "give important_sign notify backpack"
```

## `GiveJournal`

__Context__: @snippet:action-meta:online@  
__Syntax__: `givejournal`  
__Description__: Give the journal to the player.

It acts the same way as **/journal** command would.

```YAML title="Examples"
actions:
  giveJournal: "givejournal"
```

## `GlobalPoint`

__Context__: @snippet:action-meta:independent@  
__Syntax__: `globalpoint <category> <amount> [action]`  
__Description__: Manage points in a global, player-independent context.

Global points are not tied to specific players and can be used to track server-wide progress.

| Parameter                                                              | Type               | Explanation                                                                         |
|------------------------------------------------------------------------|--------------------|-------------------------------------------------------------------------------------|
| category<br>[[Identifier]](./Definition-Encyclopedia.md#identifiers)   | Required           | The name of the global point category.                                              |
| amount<br>[Number]                                                     | Required           | The number of points to use in the operation.                                       |
| action<br>[PointType]                                                  | Optional<br>[ADD]  | The operation to perform on the category.                                           |

```YAML title="Examples"
actions:
  increaseUserCount: "globalpoint global_knownusers 1 action:add"
  resetDailyLogins: "globalpoint daily_login 0 action:set"
  doubleReputation: "globalpoint reputation 2 action:multiply"
```

*[PointType]: ADD, SUBTRACT, SET, MULTIPLY

## `GlobalTag`

__Context__: @snippet:action-meta:independent@  
__Syntax__: `globaltag <operation> <tags>`  
__Description__: Manage tags that apply globally to the entire server.

Unlike regular tags, global tags are not associated with individual players.

| Parameter                | Type               | Explanation                                                                |
|--------------------------|--------------------|----------------------------------------------------------------------------|
| operation<br>[TagOpType] | Required           | Whether to add or delete the global tags.                                  |
| tags<br>List[String]     | Required           | A list of tag names, separated by commas. Tags cannot contain spaces.      |

```YAML title="Examples"
actions:
  enable_aggression: "globaltag add global_npcs_aggressive"
  disable_event: "globaltag delete holiday_event_active"
```

*[TagOpType]: add, delete

## `Heal`

__Context__: @snippet:action-meta:online@  
__Syntax__: `heal <amount> [operation]`  
__Description__: Heal the player for the specified amount.

Use `operation:set` to set the health to a specific amount instead of adding to it.
The health of the player will never be less than 0 or more than the maximum health and will be cropped to match that.
While technically possible, instead of using a negative amount you should consider using the [`damage` action](#damage).
Using negative amounts will skip the damage animation and any related effects.

| Parameter               | Type              | Explanation                   |
|-------------------------|-------------------|-------------------------------|
| amount<br>[Number]      | Required          | The amount of health to heal. |
| operation<br>[HealType] | Optional<br>[add] | The operation to perform.     |

```YAML title="Examples"
actions:
  give5: "heal 5"
  set20: "heal 20 operation:set"
```

*[HealType]: add, set

## `Hunger`

__Context__: @snippet:action-meta:online@  
__Syntax__: `hunger <operation> <amount>`  
__Description__: Manage the food level of the player.

If the hunger level is below 7, the player cannot sprint.

| Parameter                 | Type     | Explanation                                       |
|---------------------------|----------|---------------------------------------------------|
| operation<br>[HungerType] | Required | The operation to perform.                         |
| amount<br>[Number]        | Required | The amount of food level to use in the operation. |

```YAML title="Examples"
actions:
  set20: "hunger set 20"
  give5: "hunger give 5"
```

*[HungerType]: give, take, set

## `If`

__Context__: @snippet:action-meta:independent@  
__Syntax__: `if <condition> <action1> else <action2>`  
__Description__: Check a condition and run one of two actions.

This action will check a condition, and based on the outcome it will run the first or second action. The instruction
string is `if condition action1 else action2`, where `condition` is a condition ID and `action1` and `action2` are action IDs.
 `else` keyword is mandatory between actions for no practical reason. Keep in mind that this action is player-independent 
but probably the condition or the actions are not.

```YAML title="Examples"
actions:
  toggleWeather: "if sun rain else sun"
```

## `ItemDurability`

__Context__: @snippet:action-meta:online@  
__Syntax__: `itemdurability <slot> <operation> <amount> {ignoreUnbreakable} {ignoreEvents}`  
__Description__: Manage the durability of an item in the specified slot.

Available slot types: `HAND`, `OFF_HAND`, `HEAD`, `CHEST`, `LEGS`, `FEET`.

!!! info
    Both increasing and decreasing durability will be affected by the unbreaking enchantment.
    To prevent this behavior, use the `ignoreUnbreakable` argument.

| Parameter                      | Type                  | Explanation                                                          |
|--------------------------------|-----------------------|----------------------------------------------------------------------|
| slot<br>[PlayerEquipmentSlot]  | Required              | The slot of the item to change.                                      |
| operation<br>[PointType]       | Required              | The operation to perform on the durability.                          |
| amount<br>[Number]             | Required              | The amount of durability to use in the operation.                    |
| ignoreUnbreakable<br>[Boolean] | Flag<br>[false, true] | Ignore the unbreakable flag and unbreaking enchantment.              |
| ignoreEvents<br>[Boolean]      | Flag<br>[false, true] | Bypass action logic, so other plugins will not be able to interfere. |

```YAML title="Examples"
actions:
  add1ToHand: "itemdurability HAND ADD 1"
  damageChest: "itemdurability CHEST SUBTRACT %randomnumber.whole.15~30% ignoreUnbreakable ignoreEvents"
```

*[PointType]: ADD, SUBTRACT, SET, MULTIPLY

## `ItemOverride`

__Context__: @snippet:action-meta:online@  
__Syntax__: `itemoverride <slot> <itemId>`  
__Description__: Overrides the values defining the item.

Only `simple` items are valid identifiers for this action.

| Parameter                      | Type     | Explanation                                |
|--------------------------------|----------|--------------------------------------------|
| slot <br>[PlayerEquipmentSlot] | Required | The slot of the item to change.            |
| itemId <br>[Identifier]        | Required | The identifier of the definition to apply. |

```YAML title="Examples"
actions:
  setNameToHand: "itemoverride HAND named"
  updateStatistic: "itemoverride OFF_HAND statistic"
items:
  named: "simple name:Test"
  statistic: 'simple "lore:Used %point.uses.amount% times!"'
```

## `Journal`

__Context__: @snippet:action-meta:online-offline-independent@  
__Syntax__: `journal <operation> [entry]`  
__Description__: Manage entries in the player's journal.

Journal entries have to be defined in the `journal` section.
Changing journal entries will also reload the journal.
The independent context is only available for the `delete` operation,
and deletes the journal entry for all players in the database (even if offline).

| Parameter                | Type               | Explanation                        |
|--------------------------|--------------------|------------------------------------|
| operation<br>[JournalOp] | Required           | The action to perform.             |
| entry<br>[Identifier]    | Optional<br>[None] | The name of the entry if required. |

```YAML title="Examples"
actions:
  questStarted: "journal add quest_started"
  questAvailable: "journal delete quest_available"
  update: "journal update"
```

*[JournalOp]: add, delete, update

## `Kill`

__Context__: @snippet:action-meta:online@  
__Syntax__: `kill`  
__Description__: Kill the player.

```YAML title="Examples"
actions:
  killPlayer: "kill"
```

## `Language`

__Context__: @snippet:action-meta:online-offline@  
__Syntax__: `language <language>`  
__Description__: Set the player's language to the specified one.

| Parameter            | Type     | Explanation       |
|----------------------|----------|-------------------|
| language<br>[String] | Required | The language key. |

```YAML title="Examples"
actions:
  setLangEn: "language en-US"
```

## `Lever`

__Context__: @snippet:action-meta:independent@  
__Syntax__: `lever <location> <state>`  
__Description__: Control the lever at the specified location.

| Parameter                                                                      | Type          | Explanation                    |
|--------------------------------------------------------------------------------|---------------|--------------------------------|
| location<br>[[Location]](./Definition-Encyclopedia.md#unified-location-format) | Required      | The location of the lever.     |
| state<br>[LeverState]                                                          | Required      | The state to set the lever to. |

```YAML title="Examples"
actions:
  toggle: "lever 100;200;300;world toggle"
```

*[LeverState]: on, off, toggle

## `Lightning`

__Context__: @snippet:action-meta:independent@  
__Syntax__: `lightning <location> {noDamage}`  
__Description__: Strike lightning at the specified location.

| Parameter                                                                      | Type                  | Explanation                                                                                  |
|--------------------------------------------------------------------------------|-----------------------|----------------------------------------------------------------------------------------------|
| location<br>[[Location]](./Definition-Encyclopedia.md#unified-location-format) | Required              | The location where the lightning should strike.                                              |
| noDamage<br>[Boolean]                                                          | Flag<br>[false, true] | If the lightning should be purely visual and deal no damage.                                 |

```YAML title="Examples"
actions:
  strikeLightning: "lightning 100;64;-100;survival"
  showEntrance: "lightning 200;65;100;survival noDamage"
```

## `Log`

__Context__: @snippet:action-meta:independent@  
__Syntax__: `log <message> [level]`  
__Description__: Print the specified message to the server's console.

| Parameter | Syntax           | Default Value | Explanation                                                                                                                               |
|-----------|------------------|---------------|-------------------------------------------------------------------------------------------------------------------------------------------|
| _level_   | `level:logLevel` | `INFO`        | Optionally the log level can be specified but only **before** the message. <br>There are 4 levels: `debug`, `info`, `warning` and `error` |

```YAML title="Examples"
actions:
    logPlayer: "log %player% completed first quest."
    debug: "log daily quests have been reset level:DEBUG "
```

## `Notify`

__Context__: @snippet:action-meta:online@  
__Syntax__: `notify <message> [category] [io] [any io specific settings]`  
__Description__: Send the notification to the player using the specified notifyIO.

Every notify IO has its own specific settings. You must understand these too if you want to use the Notify system to its full extent.
Advanced users may also use [Notify Categories](../Advanced/Notification-IO's-&-Categories.md#categories) to make their lives easier.

Also a language can be defined with a language key from the language folder in the format `{Lang-Key}`.
Any text after the language key until the next language key belongs to the specified language.
There must be a space between the language key and the message.
In this example, english users would see `ABC` and german ones would see `DEF`.

!!! warning
    All colons (`:`) in the message part of the notification need to be escaped, including those inside placeholders.
    One backslash (`\`) is required when using no quoting at all (`...`) or single quotes
    (`'...'`). Two backslashes are required (`\\`) when using double quotes (`"..."`).  
    You also need to escape the backslash itself, if you use double quotes for some things like `\n`.

    Examples:<br>
    `actionName: notify Peter:Heya %player%!` :arrow_right: `actionName: notify Peter{++\++}:Heya %player%!`<br>
    `actionName: {=='==}notify Peter:Heya %player%!{=='==}` :arrow_right: `actionName: {=='==}notify Peter{++\++}:Heya %player%!{=='==}`<br>
    `actionName: {=="==}notify Peter:Heya %player%!{=="==}` :arrow_right: `actionName: {=="==}notify Peter{++\\++}:Heya %player%!{=="==}`<br>
    `otherAction: notify You own %math.calc:5% fish!` :arrow_right: `otherAction: You own %math.calc{++\++}:5% fish!`<br>
    `newLine: {=="==}notify Some multiline \n message{=="==}` :arrow_right: `newLine: {=="==}notify Some multiline {++\++}\n message{=="==}`

| Parameter                  | Syntax                | Default Value          | Explanation                                                                                                                      |
|----------------------------|-----------------------|------------------------|----------------------------------------------------------------------------------------------------------------------------------|
| _message_                  | Any text with spaces! | :octicons-x-circle-16: | The message that will be displayed. Supports translations. *Must be first*                                                       |
| _category_                 | `category:info`       | None                   | Will load all settings from that Notification Category. Can be a comma-separated list. The first existent category will be used. |
| _io_                       | `io:bossbar`          | io:chat                | Any NotifyIO Overrides the "category". settings.                                                                                 |
| _any io specific settings_ | `setting:value`       | None                   | Some notifyIO's provide specific settings. Can be used multiple times. Overrides the "category" settings.                        |

```YAML title="Examples"
actions:
  #The simplest of all notify actions. Just a chat message:
  customAction: "notify Hello %player%!"  

  #It's the same as this one since 'chat' is the default IO.
  theSame: "notify Hello %player%! io:chat"

  #This one displays a title and a subtitle:
  myTitle: "notify This is a title.\\nThis is a subtitle. io:title"

  #Plays a sound:
  mySound: "notify io:sound sound:x.y.z"

  #This one explicitly defines an io (bossbar) and adds one bossbarIO option + one soundIO option:
  myBar: "notify This is a custom message. io:bossbar barColor:red sound:block.anvil.use"

  #Some actions with categories.
  myAction1: "notify This is a custom message! category:info"
  myAction2: "notify This is a custom message! category:firstChoice,secondChoice"

  #You can also override category settings:
  myAction3: "notify Another message! category:info io:advancement frame:challenge"

  #Use multiple languages:
  multilanguage: "notify {en-US} Hello english person! {de-de} Hello german person! {es-ES} Hello spanish person!"
```

## `NotifyAll`

__Context__: @snippet:action-meta:independent@  
__Syntax__: `notifyall <message> [category] [io] [any io specific settings]`  
__Description__: Send the notification to all players using the specified notifyIO.

You can broadcast notifications to all online players on the server using the `notifyall` action.
It works just like the notify action.
Placeholders are resolved for each online player, not for the player the action is executed for.

```YAML title="Examples"
actions:
  announceDungeon: "notifyall A new dungeon has opened!"
```

## `NpcTeleport`

__Context__: @snippet:action-meta:independent@  
__Syntax__: `npcteleport <npc> <location> {spawn}`  
__Description__: Teleport the NPC to the specified location.

| Parameter                                                                      | Type                  | Explanation                                       |
|--------------------------------------------------------------------------------|-----------------------|---------------------------------------------------|
| npc<br>[Identifier]                                                            | Required              | The ID of the NPC.                                |
| location<br>[[Location]](./Definition-Encyclopedia.md#unified-location-format) | Required              | The location to which the NPC will be teleported. |
| spawn<br>[Boolean]                                                             | Flag<br>[false, true] | If the NPC should be spawned if not in the world. |

```YAML title="Examples"
actions:
  teleportToSpawn: "npcteleport mayorHans 100;200;300;world"
```

## `Objective`

__Context__: @snippet:action-meta:online-offline-independent@  
__Syntax__: `objective <operation> <objectives>`  
__Description__: Manage objectives.

The independent context is only available for the `remove` operation,
and removes the objective for all players in the database (even if offline).

| Parameter                                                                  | Type     | Explanation                             |
|----------------------------------------------------------------------------|----------|-----------------------------------------|
| operation<br>[ObjectiveOp]                                                 | Required | The action to do with the objective(s). |
| objectives<br>List[[Identifier]](./Definition-Encyclopedia.md#identifiers) | Required | The objective(s) to run the action on.  |

```YAML title="Examples"
actions:
  startQuest: "objective add killTheDragon,goToDungeon"
  progressQuest: "objective complete killTheDragon"
```

*[ObjectiveOp]: add, remove, complete

## `OpSudo`

__Context__: @snippet:action-meta:online@  
__Syntax__: `opsudo <commands>`  
__Description__: Execute the commands as the player with temporary operator permissions.

This action is similar to the `sudo` action, the only difference is that it will fire a command as the player with temporary OP permissions.
Additional commands can be defined by separating them with `|` character. If you want to use a `|` character in the message use `\|`.

Looking for [execute as player commands](#sudo)?
Looking for [execute as console commands](#command)?

```YAML title="Examples"
actions:
  spawn: "opsudo spawn"
```

## `Particle`

__Context__: @snippet:action-meta:independent@  
__Syntax__: `particle <particle> <location> [count] [offset] [range]`  
__Description__: Spawns particles at the specified location.

| Parameter                                                                      | Type                  | Explanation                                                                                                              |
|--------------------------------------------------------------------------------|-----------------------|--------------------------------------------------------------------------------------------------------------------------|
| particle<br>[Particle]                                                         | Required              | The type of particle to spawn. See [Spigot Javadocs](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html). |
| location<br>[[Location]](./Definition-Encyclopedia.md#unified-location-format) | Required              | The location where the particles should be spawned.                                                                      |
| count<br>[Number]                                                              | Optional<br>[1]       | The number of particles to spawn.                                                                                        |
| offset<br>[Vector]                                                             | Optional<br>[(0;0;0)] | The offset of the particles to scatter.                                                                                  |
| range<br>[Number]                                                              | Optional<br>[32]      | The range in which players can see the particles.                                                                        |

```YAML title="Examples"
actions:
  simpleParticles: "particle FLAME 100;64;100;world"
  lotsOfParticles: "particle CLOUD 100;64;100;world count:50 offset:(1;1;1) range:64"
```

## `Party`

__Context__: @snippet:action-meta:online@  
__Syntax__: `party <range> <conditions> <actions> [amount]`  
__Description__: Run actions for all players in the party.

This is part of the [party system](../Advanced/Parties.md).
Runs the specified list of actions (third argument) for every player in the party.
The last optional argument `amount` specifies a maximum number of players to select.
Selected players will be picked from the party if they are in range and meet the conditions.
Players are selected according to their distance from the player who triggered the action.
For example, if the 'amount' is two, the player who triggered the action and the player closest to that player will be selected.
A negative amount will select all players and therefore act as if there was no amount given.

```YAML title="Examples"
actions:
  givePartyReward: "party 10 has_tag1,!has_tag2 give_reward"
  giveLimitedPartyReward: "party 10 has_tag1,!has_tag2 give_special_reward amount:3"
```

## `PickRandom`

__Context__: @snippet:action-meta:independent@  
__Syntax__: `pickrandom <actions> [amount]`  
__Description__: Pick actions randomly from the specified list of actions.

Another container for actions. It picks one (or multiple) of the given actions and runs it.
You must specify how likely it is that each action is picked by adding the weighting before the action's id.
The weighting is a floating point number, that is the ratio of the action's chance to be picked.

It picks one action from the list by default, but you can add an optional `amount:` if you want more to be picked.
Note that only as many actions as specified can be picked and `amount:0` will do nothing.

```YAML title="Examples"
actions:
  pickTwo: "pickrandom 20.5~action1,0.5~action2,79~action3 amount:2"
  PickThree: "pickrandom %point.factionXP.amount%~action1,0.5~action2,79~action3,1~action4 amount:3"
```

## `Point`

__Context__: @snippet:action-meta:online-offline@  
__Syntax__: `point <category> <amount> [action] {notify}`  
__Description__: Manage the points in the specific category.

See also the [`globalpoint` action](#globalpoint).

| Parameter                                                            | Type                  | Explanation                                                           |
|----------------------------------------------------------------------|-----------------------|-----------------------------------------------------------------------|
| category<br>[[Identifier]](./Definition-Encyclopedia.md#identifiers) | Required              | The name of the point category.                                       |
| amount<br>[Number]                                                   | Required              | The number of points to use in the operation.                         |
| action<br>[PointOp]                                                  | Optional<br>[ADD]     | The operation to perform on the category.                             |
| notify<br>[Boolean]                                                  | Flag<br>[false, true] | If a notification about the change should be displayed to the player. |

```YAML title="Examples"
actions:
  gainAttitude: "point npc_attitude 5 action:add"
  loseAttitude: "point npc_attitude 2 action:subtract"
  resetCombo: "point combo 0 action:set"
  boostPoints: "point points 1.25 action:multiply notify"
```

*[PointOp]: ADD, SUBTRACT, SET, MULTIPLY

## `Projectile`

__Context__: @snippet:action-meta:independent@  
__Syntax__: `projectile <type> <location> <direction> [speed] [gravity]`  
__Description__: Launch a projectile entity.

The projectile's shooter is automatically set to the player if the action is run in an online context.
Setting the `speed` parameter will normalize the direction vector to the specified speed. 
Therefore, setting the `speed` parameter to 1 won't be the same as not setting the parameter at all.

| Parameter                                                                       | Type                | Explanation                                                   |
|---------------------------------------------------------------------------------|---------------------|---------------------------------------------------------------|
| type <br>[EntityType]                                                           | Required            | The type of the projectile. Supported types are listed below. |
| location <br>[[Location]](./Definition-Encyclopedia.md#unified-location-format) | Required            | The location from which the projectile should be spawned.     |
| direction <br>[Vector]                                                          | Required            | The direction in which the projectile should be launched.     |
| speed <br>[Number]                                                              | Optional <br>[None] | Speed multiplier for the projectile.                          |
| gravity <br>[Boolean]                                                           | Optional <br>[true] | Whether the projectile should be affected by gravity.         |

```YAML title="Examples"
actions:
  speedArrow: "projectile ARROW 100;64;100;world (1;0;0) speed:2.0"
  magicMissile: "projectile SHULKER_BULLET %location% (0;1;0) gravity:false"
```

## `RemoveEntity`

__Context__: @snippet:action-meta:independent@  
__Syntax__: `removeentity <entities> <location> <radius> [name] [marked] {kill}`  
__Description__: Remove or kill all entities of the specified type at the specified location.

Can only effect loaded entities!

| Parameter                                                                      | Type                  | Explanation                                                                                   |
|--------------------------------------------------------------------------------|-----------------------|-----------------------------------------------------------------------------------------------|
| entities<br>List[String]                                                       | Required              | See [EntityType](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html). |
| location<br>[[Location]](./Definition-Encyclopedia.md#unified-location-format) | Required              | The center location of the target entities.                                                   |
| radius<br>[Number]                                                             | Required              | The radius around the location.                                                               |
| name<br>[String]                                                               | Optional<br>[None]    | Name of the entity.                                                                           |
| marked<br>[String]                                                             | Optional<br>[None]    | Mark of the entity.                                                                           |
| kill<br>[Boolean]                                                              | Flag<br>[false, true] | Whether to remove or actually kill the entity (if possible).                                  |

```YAML title="Examples"
actions:
  killArenaMobs: "removeentity ZOMBIE 100;200;300;world 10 name:Monster kill"
  clearGameArea: "removeentity ARROW,SNOWBALL,WOLF,ARMOR_STAND 100;200;300;world 50 marked:minigame"
```

## `Run`

__Context__: @snippet:action-meta:independent@  
__Syntax__: `run <actions>`  
__Description__: Allows you to specify multiple instructions in one long instruction.

Each instruction must be started with the `^` character (it divides all the instructions).
It's not the same as the `folder` action, because you have to specify the actual instruction, not an action name.
Don't use conditions here, it behaves strangely.

```YAML title="Examples"
actions:
  eliminate: "run ^tag add beton ^journal add beton ^give emerald:5 ^kill"
```

However, consider the usage of implicit actions instead of `run`.
Here are the `eliminate` and `eliminateImplicit` actions equivalent with the important difference that the "sub actions"
can be referenced independent and conditions work there as expected.

```YAML title="As Implicit Action"
actions:
  eliminateImplicit:
    addTag: "tag add beton"
    giveEmerald: "give emerald:5"
    kill: "kill"
```

## `RunForAll`

__Context__: @snippet:action-meta:independent@  
__Syntax__: `runforall [actions] [where]`  
__Description__: Run the specified actions once for each online player.

The most common use case is to run an action for all online players from a [schedule](../Advanced/Schedules.md).
But you can also use it in conversations, objectives, or other actions.

To run the actions only for a selection of players, use the `where:` option to filter for players that meet specific conditions.

| Parameter                   | Type               | Explanation                                                                                                                 |
|-----------------------------|--------------------|-----------------------------------------------------------------------------------------------------------------------------|
| actions<br>List[Identifier] | Optional<br>[None] | The actions to be run.                                                                                                      |
| where<br>List[Identifier]   | Optional<br>[None] | A list of conditions that are checked for every player. The actions are only executed for players that meet all conditions. |

!!! warning
    You can still append conditions to the `runForAll` action (e.g. `runForAll actions:kickPlayer conditions:!isOp`).  
    **This won't check the conditions for each player!**  
    Instead it will check the conditions for the player that triggered the action or check them player independent if
    triggered player independent (e.g. by a schedule).


```YAML title="Examples"
actions:
  kickAll: "runforall where:!isOp actions:kickPlayer,restartQuest"
```

## `RunIndependent`

__Context__: @snippet:action-meta:independent@  
__Syntax__: `runindependent [actions]`  
__Description__: Run the specified actions as if they were independent.

Runs the specified action (or list of actions) player-independent (as if it was run from a [schedule](../Advanced/Schedules.md)).

This is useful for actions that behave differently when run player-independent.

??? abstract "Actions that behave different if run player independent"
    * [`tag delete`](#tag) - deletes the tag for all players in the database (even if offline)
    * [`objective remove`](#objective) - removes the objective for all players in the database (even if offline)
    * [`journal delete`](#journal) - deletes the journal entry for all players in the database (even if offline)
    * [`deletepoint`](#deletepoint) - clears points of a given category for all players in the database (even if offline)

| Parameter                   | Type               | Explanation            |
|-----------------------------|--------------------|------------------------|
| actions<br>List[Identifier] | Optional<br>[None] | The actions to be run. |

```YAML title="Examples"
actions:
  resetQuestForAll: "runindependent actions:removeObjective,clearTags,resetJournal"
```

!!! warning
    There are a lot of actions and conditions that cannot be run (or checked) player independent.  
    If you try to run such an action player independent (or check such a condition) this won't work,
    and you will get an error message in the console.
    
    For more information on player independent actions [check this](../Advanced/Schedules.md#player-independent-actions).

## `Score`

__Context__: @snippet:action-meta:online-offline@  
__Syntax__: `score <objective> <number> [action]`  
__Description__: Manage the scoreboard objective of the player.

This action works in the same way as [point](#point), the only difference being that it uses scoreboards instead of points.
It's only possible to change the value; you have to create the scoreboard target first.

| Parameter             | Type              | Explanation                                           |
|-----------------------|-------------------|-------------------------------------------------------|
| objective<br>[String] | Required          | The name of the scoreboard objective.                 |
| number<br>[Number]    | Required          | The value to use in the operation.                    |
| action<br>[ScoreOp]   | Optional<br>[ADD] | The operation to perform on the scoreboard objective. |

```YAML title="Examples"
actions:
  gotKill: "score kill 1 action:add"
  gotKilled: "score kill 1 action:subtract"
  resetKill: "score kill 0 action:set"
  applyBonus: "score kill 1.2 action:multiply"
```

*[ScoreOp]: ADD, SUBTRACT, SET, MULTIPLY

## `ScoreTag`

__Context__: @snippet:action-meta:online@  
__Syntax__: `scoretag <operation> <tag>`  
__Description__: Manage the scoreboard tag of the player.

The kind of tags that are used by vanilla Minecraft and not the [betonquest tags](#tag).

| Parameter                | Type     | Explanation                       |
|--------------------------|----------|-----------------------------------|
| operation<br>[TagOpType] | Required | Whether to add or remove the tag. |
| tag<br>[String]          | Required | The name of the scoreboard tag.   |

```YAML title="Examples"
actions:
  addVanillaTag: "scoretag add vanilla_tag"
  removeVanillaTag: "scoretag remove vanilla_tag"
```

*[TagOpType]: add, remove

## `SetBlock`

__Context__: @snippet:action-meta:independent@  
__Syntax__: `setblock <block> <location> [ignorePhysics]`  
__Description__: Set the block at the specified location.

The first argument is a [Block Selector](Definition-Encyclopedia.md#block-selectors), the second a location. It's possible to
deactivate the physics of the block by adding `ignorePhysics` at the end.
Very powerful if used to trigger redstone contraptions.

```YAML title="Examples"
actions:
  setRedstoneBlock: "setblock REDSTONE_BLOCK 100;200;300;world"
  flyingSand: "setblock SAND 100;200;300;world ignorePhysics"
```

## `Spawn`

__Context__: @snippet:action-meta:independent@  
__Syntax__: `spawn <location> <type> <amount> [name] [marked] [drops] [h] [c] [l] [b] [m] [o]`  
__Description__: Spawn the specified number of mobs with the specified type at the specified location.

The first argument is a location. Next is [type of the mob](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html).
The last, third argument is integer for amount of mobs to be spawned. You can also specify `name:` argument, followed
by the name of the mob. You can also mark the spawned mob with a keyword using `marked:` argument.
It won't show anywhere, and you can check for only marked mobs in `mobkill` objective.

You can specify armor which the mob will wear and items it will hold with
`h:` (helmet), `c:` (chestplate), `l:` (leggings), `b:` (boots), `m:` (main hand) and `o:` (off hand) optional arguments.
These take a single item without amount, as defined in the _items_ section. You can also add a list of drops with
`drops:` argument, followed by a list of items with amounts after colons, separated by commas.

```YAML title="Examples"
actions:
  spawnSkeleton: "spawn 100;200;300;world SKELETON 5 marked:targets"
  spawnZombie: "spawn 100;200;300;world ZOMBIE name:Bolec 1 h:blue_hat c:red_vest drops:emerald:10,bread:2"
```

## `SpawnParticles`

__Context__: @snippet:action-meta:online-independent@  
__Syntax__: `spawnparticles <identifier> [location]`  
__Description__: Spawns particle animations defined in the `particles` section.

If no location is specified and the action is called in an online context, the player's location will be used.
If the independent context is used, a location must be specified.
See [Particles](../Advanced/Particles.md) for more information.

| Parameter                                                              | Type               | Explanation                                                                                                 |
|------------------------------------------------------------------------|--------------------|-------------------------------------------------------------------------------------------------------------|
| identifier<br>[[Identifier]](./Definition-Encyclopedia.md#identifiers) | Required           | The identifier of the particle animation to spawn, as defined in the particles section.                     |
| location<br>[Location]                                                 | Optional<br>[None] | The location where the particles should be spawned. Defaults to the player's location in an online context. |

```YAML title="Examples"
actions:
  spawnMagic: "spawnparticles magic_effect"
  spawnAtChest: "spawnparticles portal_effect 100;200;300;world"
```

## `Stage`

__Context__: @snippet:action-meta:online-offline@  
__Syntax__: `stage <objective> <operation> <stage|amount>`  
__Description__: Manage the specified stage objective of the player.

The objective will not automatically complete when using `set`.
By increasing it the player will be able to complete the objective. When increasing or decreasing the stage
you can optionally specify an amount to increase or decrease by.  
When decreasing the objective it will do nothing when the first stage is reached.  
When the conditions of the stage objective are not met, the stage of the player can not be modified.  
For more take a look at the [stage objective](Objectives-List.md#stage).

| Parameter         | Syntax                          | Default Value          | Explanation                                     |
|-------------------|---------------------------------|------------------------|-------------------------------------------------|
| _stage objective_ | Objective                       | :octicons-x-circle-16: | The name of the stage objective                 |
| _action_          | `set`, `increase` or `decrease` | :octicons-x-circle-16: | The action to perform                           |
| _stage_           | Stage                           | :octicons-x-circle-16: | The name of the stage to set when `set` is used |
| _amount_          | Number                          | 1                      | The amount to increase or decrease by           |

```YAML title="Examples"
actions:
  setCookCookies: "stage bakeCookies set cookCookies"
  increase: "stage bakeCookies increase"
  decrease2: "stage bakeCookies decrease 2"
```

## `Sudo`

__Context__: @snippet:action-meta:online@  
__Syntax__: `sudo <commands>`  
__Description__: Execute the commands as the player.

This action is similar to `command` action, the only difference is that it will fire a command as the player (often referred to as player commands).
Additional commands can be defined by separating them with `|` character. If you want to use a `|` character in the message use `\|`.

Looking for [execute as operator commands](#opsudo)?
Looking for [execute as console commands](#command)?

```YAML title="Examples"
actions:
  spawn: "sudo spawn"
```

## `Tag`

__Context__: @snippet:action-meta:online-offline-independent@  
__Syntax__: `tag <operation> <tags>`  
__Description__: Manage the tags of the player.

The independent context is only available for the `delete` operation,
and deletes the tag for all players in the database (even if offline).

| Parameter                | Type     | Explanation                                       |
|--------------------------|----------|---------------------------------------------------|
| operation<br>[TagOpType] | Required | Whether to add or delete the tags.                |
| tags<br>List[String]     | Required | A list of tag names. Tags may not contain spaces. |

```YAML title="Examples"
actions:
  addStartTags: "tag add quest_started,new_entry"
```

*[TagOpType]: add, delete

## `Take`

__Context__: @snippet:action-meta:online@  
__Syntax__: `take <items> [invOrder] [notify] [abort] [fail]`  
__Description__: Take items from the player’s inventory or backpack.

The items itself must be defined in the `items` section, optionally with an amount after a colon.
Which inventory types are checked is defined by the `invOrder:`
option. You can use `Backpack`, `Inventory`, `MainHand`, `Offhand` and `Armor` there. One after another will be checked if multiple types are defined.

The `abort` option sets that no items are taken at all, when not all are present.
The `fail` actions will be executed when not all specified items could be taken from the given inventories.
These in combination can be used for example setting a tag which you can use in a later conversation option to check
if the player still had the items when they should be taken.
Alternatively you can check for the tag in a folder's cancel conditions to only execute the following actions when
in fact all items are taken.

Note: If the items aren't quest items don't use `take` action with player options in conversations!
The player can drop items before selecting the option and pickup them after the action fires.
Validate it on the NPC’s reaction or consider the `fail` actions in addition.
However, item movement can be prevented with the `conversation.block_item_transfer` option in the "_config.yml_".

You can also specify `notify` keyword to display a simple message to the player about losing items.

```YAML title="Examples"
actions:
  emeraldsAndSword: "take emerald:120,sword"
  nuggets: "take nugget:6 notify"
  wand: "take wand notify invOrder:Backpack"
  money: "take money:50 invOrder:Backpack,Inventory"
  armor: "take armor invOrder:Armor,Offhand,Inventory,Backpack"
  takeBribe: "take bribe:10 invOrder:mainHand fail:notEnough"
```

## `Teleport`

__Context__: @snippet:action-meta:online@  
__Syntax__: `teleport <location>`  
__Description__: Teleport the player to the specified location.

Ends any active conversations.

Do you only want to [cancel the conversation](#cancelconversation)?

| Parameter                                                                      | Type     | Explanation                                          |
|--------------------------------------------------------------------------------|----------|------------------------------------------------------|
| location<br>[[Location]](./Definition-Encyclopedia.md#unified-location-format) | Required | The location to which the player will be teleported. |

```YAML title="Examples"
actions:
  toCity: "teleport 432;121;532;world" # (1)!
  toHell: "teleport 123;32;-789;world_the_nether;180;45" # (2)!
```

1. Teleport the player to X: 432, Y: 121, Z: 532 in the world named 'world'.
2. Teleport the player to X: 123, Y: 32, Z: -789 in the world named 'world_the_nether'. Also set the head rotation to yaw 180 and pitch 45.

## `Time`

__Context__: @snippet:action-meta:independent@  
__Syntax__: `time <time> [world] [ticks]`  
__Description__: Manage the time of the specified world.

The time is represented in 24 hours format as a float number, so 0 is midnight, 12 is
noon, and 23 is 11 PM. For minutes, you can use floating point numbers, so 0.5 is half past midnight, 0.25 is quarter
past midnight, and so on. (0.1 hours is 6 minutes). It's possible to add or subtract time by using `+` or `-` prefix or
to set the time by setting no prefix.
Additionally, you can specify the world in which the time will be changed, by adding `world:`.
Using the `ticks` argument changes the time like the vanilla command.

```YAML title="Examples"
actions:
  set6: "time 6"
  increase: "time +0.1"
  decreaseRpgWorld: "time -12 world:rpgworld"
  increaseRandom: "time +%randomnumber.whole.100~2000% world:pvpworld ticks"
```

## `UpdateHologram`

__Context__: @snippet:action-meta:online@  
__Syntax__: `updatehologram [mode] [holo] [npcHolo]`  
__Description__: Force an immediate refresh instead of waiting for the next update interval.

This is useful for performance optimizations if used when the update interval is set to high values or is deactivated at all.

When no identifier is given at all, all holograms will be updated.

| Parameter                                                                | Type                | Explanation                    |
|--------------------------------------------------------------------------|---------------------|--------------------------------|
| mode <br>[UpdateMode]                                                    | Optional <br>[ALL]  | The update action.             |
| holo <br>List[[Identifier]](./Definition-Encyclopedia.md#identifiers)    | Optional <br>[None] | The `holograms` to update.     |
| npcHolo <br>List[[Identifier]](./Definition-Encyclopedia.md#identifiers) | Optional <br>[None] | The `npc_holograms` to update. |

```YAML title="Examples"
actions:
  updateAll: "updateHologram mode:all"
  updateLocationHologram: "updateHologram loc:statusBoard"
  updateBoth: "updateHologram loc:questBoard npc:announcerHolo"
```

*[UpdateMode] visibility, content, all
 
## `UpdateVisibility`

__Context__: @snippet:action-meta:online@  
__Syntax__: `updatevisibility`  
__Description__: Force an immediate refresh instead of waiting for the next update interval of hiders.

This is useful for performance optimizations if used when the update interval is set to high values or deactivated at all.

This applies only to hiders, not holograms. For them use the [`updateHologram` action](#updatehologram).

```YAML title="Examples"
actions:
  update: "updatevisibility"
```

## `Variable`

__Context__: @snippet:action-meta:online-offline@  
__Syntax__: `variable <objective> <key> <value>`  
__Description__: Manage values that are stored in `variable` objective variables.

The first argument is the ID of the `variable` objective. The second argument is the name of the variable to set.
The third argument is the value to set. Both the name and value can use `%...%` placeholders.
To delete a variable you can use `""`.
To store more complex values you can use [quoting](../Basics/Quoting-&-YAML.md#quoting).
Refer to the [`variable` objective](Objectives-List.md#variable) documentation for more information about storing variables.
This action will do nothing if the player does not already have a `variable` objective assigned to them.

```YAML title="Examples"
actions:
  goody: "variable CustomVariable MyFirstVariable Goodbye!"
  playerName: "variable variable_objectiveID name %player%"
  delete: 'variable other_var_obj desc ""'
```

## `Velocity`

__Context__: @snippet:action-meta:online@  
__Syntax__: `velocity <vector> <direction> <modification>`  
__Description__: Manage the velocity of the player.

| Parameter      | Syntax                          | Default Value          | Explanation                                                                                                                                                                                                                                                                                                               |
|----------------|---------------------------------|------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| _vector_       | `vector:(x;y;z)`                | :octicons-x-circle-16: | The values of the vector, which are decimal numbers, can be interpreted as absolute numbers like the coordinate or as relative directions. For more understanding the relative direction is similar to `^ ^ ^` in minecraft or in other words `(sideways;upwards;forwards)`.                                              |
| _direction_    | `direction:directionType`       | `absolute`             | There are 3 types how the vector can get applied to the player:<br> `absolute` won't change the vector at all.<br> `relative` will redirect the vector to the view of the player.<br> `relative_y` is a mix between absolute and relative. It will still direct to the view but only horizontally, so y will be absolute. |
| _modification_ | `modification:modificationType` | `set`                  | Possible modifications are `set` and `add`. The modification type determines how the vector should be merged with the player's velocity. The player's velocity is the external force applied on the player.                                                                                                               |

@snippet:general:relativeAxisExplanation@

```YAML title="Examples"
actions:
  jumppad: "velocity vector:(2;0.8;4)"
  dash: "velocity vector:(0;0.1;1.3) direction:relative_y"
  individual_dash: "velocity vector:%objective.customPlaceholder.dashLength% direction:relative_y"
  fly: "velocity vector:(0;0.1;2) direction:relative modification:add"
```


## `Weather`

__Context__: @snippet:action-meta:independent@  
__Syntax__: `weather <type> [duration] [world]`  
__Description__: Manage the weather of the world.

Sets the weather in the world the player is currently in.
Durations less than 1 is equal to no duration.

| Parameter             | Type               | Explanation                                    |
|-----------------------|--------------------|------------------------------------------------|
| type<br>[WeatherType] | Required           | The type of weather to set.                    |
| duration<br>[Number]  | Optional<br>[0]    | The duration the weather will last (in ticks). |
| world<br>[String]     | Optional<br>[None] | The world to change the weather in.            |

```YAML title="Examples"
actions:
  setSun: "weather sun"
  setShortRain: "weather rain duration:60 world:rpgworld"
  setStorm: "weather storm duration:%point.tribute.left:150%"
```

*[WeatherType]: sun, rain, storm

--8<-- "instruction-datatypes.md"
