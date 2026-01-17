---
icon: fontawesome/solid/play
toc_depth: 2
tags:
  - Action
---
# Actions List

## Burn a player

__Context__: @snippet:action-meta:online@  
__Syntax__: `burn <duration>`  
__Description__: Ignite the player for the specified duration.

| Parameter  | Syntax            | Default Value          | Explanation                                     |
|------------|-------------------|------------------------|-------------------------------------------------|
| _duration_ | `duration:number` | :octicons-x-circle-16: | The duration the player will burn (in seconds). |

```YAML title="Example"
actions:
  burn: "burn duration:4"
  punishing_fire: "burn duration:%point.punishment.amount%"
```

@snippet:actions:cancel@

## Cancel a conversation

__Context__: @snippet:action-meta:online@  
__Syntax__: `cancelconversation`  
__Description__: Cancel a conversation that is currently active for the player.

```YAML title="Example"
actions:
  cancel: "cancelconversation"
```

## Chat as a player

__Context__: @snippet:action-meta:online@  
__Syntax__: `chat <messages>`  
__Description__: Send the message in chat as the player.

Therefore, it will look like as if the player did send the message.
The instruction string is the command, without leading slash. You can only use `%player%` as a placeholder in this action.
Additional messages can be defined by separating them with `|` character. If you want to use a `|` character in the message use `\|`.

If a plugin does not work with the sudo / command action you need to use this action.

```YAML title="Example"
actions:
  sendMSG: "chat Hello!"
  sendMultipleMSGs: "chat Hi %player%|ban %player%|pardon %player%"
  sendPluginCommand: "chat /someCommand x y z"
```

## Clear a chest

__Context__: @snippet:action-meta:independent@  
__Syntax__: `chestclear <location>`  
__Description__: Remove all items from the chest at the specified location.

```YAML title="Example"
actions:
  chestclear: "chestclear 100;200;300;world"
```

## Put items into a chest

__Context__: @snippet:action-meta:independent@  
__Syntax__: `chestgive <location> <items>`  
__Description__: Put items into the chest at the specified location.

This works the same as `give` action, but it puts the items in a chest at specified location.
The first argument is a location, the second argument is a list of items, like in `give` action.
If the chest is full, the items will be dropped on the ground.
The chest can be any other block with inventory, i.e. a hopper or a dispenser.
BetonQuest will log an error to the console when this action is fired but there is no chest at a specified location.

```YAML title="Example"
actions:
  chestgive: "chestgive 100;200;300;world emerald:5,sword"
```

## Take items from a chest

__Context__: @snippet:action-meta:independent@  
__Syntax__: `chesttake <location> <items>`  
__Description__: Take items from the chest at the specified location.

This action works the same as `take` action, but it takes items from a chest at specified location. The instruction string is defined in the same way as in `chestgive` action.

```YAML title="Example"
actions:
  chesttake: "chesttake 100;200;300;world emerald:5,sword"
```

## Manage compass targets

__Context__: @snippet:action-meta:online-offline@  
__Syntax__: `compass <operation> <target>`  
__Description__: Manage the compass destinations for the player.

When you run this action, you can add or remove a compass destination for the player. You may also directly set the player's compass destination as well.
When a destination is added the player will be able to select a specified location as a target of his compass.
To select the target the player must open his backpack and click on the compass icon.
The first argument is `add`,`del` or `set`, and second one is the name of the target, as defined in the _compass_ section.
Note that if you set a target the player will not automatically have it added to their choices.

The destination must be defined in `compass` section.
You can specify a name for the target in each language or just give a general name, and optionally add a custom item (from _items_ section) to be displayed in the backpack.

```YAML title="Example compass configuration"
compass:
  beton:
    name:
      en-US: Target
      pl-PL: Cel
    location: 100;200;300;world
    item: scroll
```

```YAML title="Example"
actions:
  compassBeton: "compass add beton"
```

## Execute console commands

__Context__: @snippet:action-meta:independent@  
__Syntax__: `command <commands>`  
__Description__: Run the specified commands from the console.

The instruction string is the command, without leading slash.
You can use placeholders here, but placeholders other than `%player%` won't resolve if the action is fired from
delayed `folder` and the player is offline now.
You can define additional commands by separating them with `|` character.
If you want to use a `|` character in the command use `\|`.

Looking for [execute as player commands](#execute-console-commands)?
Looking for [execute as operator commands](#execute-operator-commands)?

```YAML title="Example"
actions:
  killAndBan: "command kill %player%|ban %player%"
```

## Start a conversation

__Context__: @snippet:action-meta:online@  
__Syntax__: `conversation <conversation> <option>`  
__Description__: Start a conversation.

The first argument is ID of the conversation. This bypasses the conversation permission!

The optional `option` argument is a NPC option where the conversation will start.
When using this argument the conversation will start without its header.

```YAML title="Example"
actions:
  startConversation: "conversation village_smith"
  startConversationOption: "conversation tutorial option:explain_world"
```

## Damage a player

__Context__: @snippet:action-meta:online@  
__Syntax__: `damage <amount>`  
__Description__: Apply the specified amount of damage to the player.

The only argument is a number (can have a floating point).

```YAML title="Example"
actions:
  dealDamage: "damage 20"
```

## Delete a point category

__Context__: @snippet:action-meta:online-offline-independent@    
__Syntax__: `deletepoint <category>`  
__Description__: Delete the point category for the player.

The independent context will delete the points for all players in the database (even if offline).

```YAML title="Example"
actions:
  deletePoints: "deletepoint npc_attitude"
```

## Delete a global point category

__Context__: @snippet:action-meta:independent@  
__Syntax__: `deleteglobalpoint <category>`  
__Description__: Delete the global point category.

```YAML title="Example"
actions:
  deleteBonus: "deleteglobalpoint bonus"
```

## Control openable blocks

__Context__: @snippet:action-meta:independent@  
__Syntax__: `door <location> <operation>`  
__Description__: Open and close doors, trapdoors, and fence gates.

The syntax is exactly the same as in `lever` action above.

```YAML title="Example"
actions:
  close: "door 100;200;300;world off"
```

## Drop items

__Context__: @snippet:action-meta:independent@  
__Syntax__: `drop <items> <location>`  
__Description__: Drop the specified items at the specified location.

The action takes two parameters: `items` and `location`. Items is a list of [items](../../Features/Items.md) to be
dropped.
Every item can optionally be followed by a colon to define an amount `<item>:<amount>` otherwise the amount is 1.
The optional location defines where the items will be dropped. It must be specified in the [unified location format](../Data-Formats.md#unified-location-formating).
If no location is given then the items will be dropped at the player's current location.

If the drop action is used in a schedule then the items will be dropped at the given location.
If no location is given then the items will be dropped for **every** player at their respective locations.

```YAML title="Example"
actions:
  dropSword: "drop items:magical_sword location:200;17;300;world"
  dropRare: "drop items:loot_rare,loot_common:3"
  dropMyItem: "drop items:myItem location:%objective.MyQuestPlaceholder.DropLocation%"
```

## Remove potion effects

__Context__: @snippet:action-meta:online@  
__Syntax__: `deleffect <effects>`  
__Description__: Remove the specified potion effects from the player.

Use `any` instead of a list of types to remove all potion effects from the player.
Alternatively to `any`, you just can leave it blank.

```YAML title="Example"
actions:
  deleteEffects: "deleffect ABSORPTION,BLINDNESS"
  deleteAny: "deleffect any"
  deleteAll: "deleffect"
```

## Apply a potion effect

__Context__: @snippet:action-meta:online@  
__Syntax__: `effect <effect> <duration> <level> [ambient] [icon] [hidden]`  
__Description__: Apply the specified potion effect to the player.

First argument is potion type. You can find all available types [here](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/potion/PotionEffectType.html).
Second is integer defining how long the effect will last in seconds.
Third argument, also integer, defines level of the effect (1 means first level).
Add a parameter `ambient` to make potion particles appear more invisible (just like beacon effects).
To hide particles add a parameter `hidden`. To hide the icon for the effect add `noicon`.

```YAML title="Example"
actions:
  effectBlindness: "effect BLINDNESS 30 1 ambient icon"
```

## Evaluate an expression

__Context__: @snippet:action-meta:independent@  
__Syntax__: `eval <expression>`  
__Description__: Evaluate the expression and execute the resulting action.

This action allows you to resolve an expression containing placeholders, and the result will then be interpreted
again as an action.

```YAML title="Example"
actions:
  simpleEval: 'eval notify "This is actually an eval action evaluating to a notify action."'
  complexEval: "eval point ranking 5 action:add %objective.settings.notify%" #(1)!
```

1. This could evaluate to `point ranking 5 action:add notify` and will add 5 points to the ranking category and notify the player.
   But the placeholder could also be empty and add 5 points without notifying the player. This is not possible in a normal action.

## Manipulate experience

__Context__: @snippet:action-meta:online@  
__Syntax__: `experience <amount> <action>`  
__Description__: Manipulate the player's experience bar.

This action allows you to manipulate player's experience. First you specify a number as the amount, then the modification action.
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
actions:
  add15XP: "experience 15 action:addExperience"
  add4andAHalfLevel: "experience 4.5 action:addLevel"
  remove2Level: "experience -2 action:addLevel"
  setXPBar: "experience 0.5 action:setExperienceBar"
  resetLevel: "experience 0.01 action:setLevel"
```

## Create an explosion

__Context__: @snippet:action-meta:independent@  
__Syntax__: `explosion <fire> <block> <power> <location>`  
__Description__: Create an explosion at the specified location.

You can also define power, so be careful not to blow your server away.
Default TNT power is 4, while Wither on creation is 7.
First argument can be 0 or 1 and states if explosion will generate fire (like Ghast's fireball).
Second is also 0 or 1 but this defines if block will be destroyed or not.
Third argument is the power (float number). At the end (4th attribute) there is location.

```YAML title="Example"
actions:
  explosion: "explosion 0 1 4 100;64;-100;survival"
```

## Run multiple actions

__Context__: @snippet:action-meta:independent@  
__Syntax__: `folder <actions> [delay] [period] [unit] [random] [cancelOnLogout] [cancelConditions]`  
__Description__: Run multiple actions in sequence.

This action wraps multiple actions inside itself. Once triggered, it simply executes it's actions.
This is usefully to easily refer to a bunch of actions at once, e.g. in a conversation.

Actions marked as _persistent_ will be fired even after the player logs out.
Beware though, all conditions are false when the player is offline (even inverted ones),
so those actions should not be blocked by any conditions!  
You can use the `cancelOnLogout` argument to stop the folder executing any remaining actions if the player disconnects.


| Parameter          | Syntax                       | Default Value          | Explanation                                                                           |
|--------------------|------------------------------|------------------------|---------------------------------------------------------------------------------------|
| _actions to run_   | actionName1,action2          | :octicons-x-circle-16: | One or multiple actions to run. Contains action names seperated by commas.            |
| _delay_            | Keyword                      | without delay          | The delay before the folder starts executing it's actions.                            |
| _period_           | period:number                | without delay          | The time between each action of the folder.                                           |
| _time unit_        | unit:unit                    | Seconds                | The unit of time to use for delay and period. Either `ticks`, `minutes` or `seconds`. |
| _random_           | random:number                | Disabled               | Enables "random mode". Will randomly pick the defined amount of actions .             |
| _cancelOnLogout_   | Keyword                      | Disabled               | If enabled, the folder will stop executing actions if the player disconnects.         |
| _cancelConditions_ | cancelConditions:cond1,cond2 | Disabled               | If enabled, the folder will stop executing actions if the conditions are true.        |


```YAML title="Example"
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

## Run the first possible action

__Context__: @snippet:action-meta:independent@  
__Syntax__: `first <actions>`  
__Description__: Execute the first possible action.

This action wraps multiple actions inside itself, similar `folder`. Unlike `folder`, it attempts to execute each action,
starting from the first onward. Once it successfully executes one action, it stops executing the rest. This is useful for
collapsing long if-else chains into single actions.

This action is especially powerful when it is used in conjunction with the `conditions:` keyword,
which can be used with any action.

```YAML title="Example"
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

## Give items

__Context__: @snippet:action-meta:online@  
__Syntax__: `give <items> [notify] [backpack]`  
__Description__: Give the player all specified items.

They are specified exactly as in `item` condition -
list separated by commas, every item can have amount separated by colon. Default amount is 1.
If the player doesn't have required space in the inventory, the items are dropped on the ground,
unless they are quest items. Then they will be put into the backpack. You can also specify `notify` keyword to display a
simple message to the player about receiving items.
The optional `backpack` argument forces quest items to be placed in the backpack.

```YAML title="Example"
actions:
  giveEmeralds: "give emerald:5,emerald_block:9"
  giveSign: "give important_sign notify backpack"
```

## Give the journal

__Context__: @snippet:action-meta:online@  
__Syntax__: `givejournal`  
__Description__: Give the journal to the player.

It acts the same way as **/journal** command would.

```YAML title="Example"
actions:
  giveJournal: "givejournal"
```

## Manage a global point category

__Context__: @snippet:action-meta:independent@  
__Syntax__: `globalpoint <category> <amount> <action>`  
__Description__: Manage global points.

This works the same way as the normal [point action](#manage-a-point-category) but instead to manipulating the points for a category of a specific
player it manipulates points in a global category. These global categories are player independent, so you could for
example add a point to such a global category every time a player does a quest and give some special rewards for
the 100th player who does the quest.

```YAML title="Example"
actions:
  increaseUserCount: "globalpoint global_knownusers 1 action:add"
  resetDailyLogins: "globalpoint daily_login 0 action:set"
  doubleReputation: "globalpoint reputation 2 action:multiply"
```

## Manage global tags

__Context__: @snippet:action-meta:independent@  
__Syntax__: `globaltag <operation> <tags>`  
__Description__: Manage global tags.

Works the same way as a normal tag action, but instead of setting a tag for one player it sets it globally for all players.

```YAML title="Example"
actions:
  setNpcsAggressive: "globaltag add global_areNPCsAggressive"
```

## Manage player hunger

__Context__: @snippet:action-meta:online@  
__Syntax__: `hunger <operation> <amount>`  
__Description__: Manage the food level of the player.

The second argument is the modification type.
There are `give`, `take` and `set`. The second argument is the amount. With `set` can the food level be anything.
If `give` or `take` is specified the final amount won't be more than 20 or less than 0.
If the hunger level is below 7, the player cannot sprint.

```YAML title="Example"
actions:
  set20: "hunger set 20"
  give5: "hunger give 5"
```

## Alternate between actions

__Context__: @snippet:action-meta:independent@  
__Syntax__: `if <condition> <action1> else <action2>`  
__Description__: Check a condition and run one of two actions.

This action will check a condition, and based on the outcome it will run the first or second action. The instruction
string is `if condition action1 else action2`, where `condition` is a condition ID and `action1` and `action2` are action IDs.
 `else` keyword is mandatory between actions for no practical reason. Keep in mind that this action is `persistent`
 and `static` but probably the condition or the actions are not.

```YAML title="Example"
actions:
  toggleWeather: "if sun rain else sun"
```

## Manage item durability

__Context__: @snippet:action-meta:online@  
__Syntax__: `itemdurability <slot> <operation> <amount> [ignoreUnbreakable] [ignoreEvents]`  
__Description__: Manage the durability of an item in the specified slot.

The first argument is the slot, the second the change of durability and the third the amount.
Optional arguments are `ignoreUnbreakable` to ignore the unbreakable flag and unbreaking enchantment
and `ignoreEvents` to bypass action logic, so other plugins will not be able to interfere.
Available slot types: `HAND`, `OFF_HAND`, `HEAD`, `CHEST`, `LEGS`, `FEET`.

!!! info
    Both increasing and decreasing durability will be affected by the unbreaking enchantment.
    To prevent this behaviour use the `ignoreUnbreakable` argument.

```YAML title="Example"
actions:
  add1ToHand: "itemdurability HAND ADD 1"
  damageChest: "itemdurability CHEST SUBTRACT %randomnumber.whole.15~30% ignoreUnbreakable ignoreEvents"
```

## Manage journal entries

__Context__: @snippet:action-meta:online-offline-independent@  
__Syntax__: `journal <operation> <entry>`  
__Description__: Manage entries in the player's journal.

Journal entries have to be defined in the `journal` section. The
first argument is the action to perform, the second one is the name of the entry if required. Changing journal entries
will also reload the journal.

Possible actions are:
- `add`: Adds a page to the journal.
- `delete`: Deletes a page from the journal.
- `update`: Refreshes the journal. This is especially useful when you need to update the main page.

The independent context is only available for the `delete` operation,
and delete the journal enty for all players in the database (even if offline).

```YAML title="Example"
actions:
  questStarted: "journal add quest_started"
  questAvailable: "journal delete quest_available"
  update: "journal update"
```

## Kill a player

__Context__: @snippet:action-meta:online@  
__Syntax__: `kill`  
__Description__: Kill the player.

```YAML title="Example"
actions:
  killPlayer: "kill"
```

## Set a language

__Context__: @snippet:action-meta:online-offline@  
__Syntax__: `language <language>`  
__Description__: Set the player's language to the specified one.

There is only one argument, the language name.

```YAML title="Example"
actions:
  setLangEn: "language en-US"
```

## Control a lever

__Context__: @snippet:action-meta:independent@  
__Syntax__: `lever <location> <state>`  
__Description__: Control the lever at the specified location.

The first argument is a location, and the second one is a state: `on`, `off` or `toggle`.

```YAML title="Example"
actions:
  toggle: "lever 100;200;300;world toggle"
```

## Strike lightning

__Context__: @snippet:action-meta:independent@  
__Syntax__: `lightning <location> [noDamage]`  
__Description__: Strike lightning at specified location.

The first argument is the location. By adding `noDamage` the lightning is only
an effect and therefor does no damage.

```YAML title="Example"
actions:
  strikeLightning: "lightning 100;64;-100;survival"
  showEntrance: "lightning 200;65;100;survival noDamage"
```

@snippet:actions:notify@

@snippet:actions:notify-all@

## Log a message

__Context__: @snippet:action-meta:independent@  
__Syntax__: `log <message> [level]`  
__Description__: Print the specified message to the server's console.

| Parameter | Syntax           | Default Value | Explanation                                                                                                                               |
|-----------|------------------|---------------|-------------------------------------------------------------------------------------------------------------------------------------------|
| _level_   | `level:logLevel` | `INFO`        | Optionally the log level can be specified but only **before** the message. <br>There are 4 levels: `debug`, `info`, `warning` and `error` |

```YAML title="Example"
actions:
    logPlayer: "log %player% completed first quest."
    debug: "log daily quests have been reset level:DEBUG "
```

## Teleport an npc

__Context__: @snippet:action-meta:independent@  
__Syntax__: `npcteleport <Npc> <Location> [spawn]`  
__Description__: Teleport the npc to the specified location.

| Parameter  | Syntax                                                                       | Default Value          | Explanation                                      |
|------------|------------------------------------------------------------------------------|------------------------|--------------------------------------------------|
| _Npc_      | Npc                                                                          | :octicons-x-circle-16: | The ID of the Npc                                |
| _Location_ | [Unified Location Formatting](../Data-Formats.md#unified-location-formating) | :octicons-x-circle-16: | The location to which the Npc will be teleported |
| _Spawn_    | Keyword (`spawn`)                                                            | Disabled               | If the NPC should be spawned if not in the world |

```YAML title="Example"
actions:
  teleportToSpawn: "npcteleport mayorHans 100;200;300;world"
```

## Manage objectives

__Context__: @snippet:action-meta:online-offline-independent@  
__Syntax__: `objective <operation> <objectives>`  
__Description__: Manage objectives.

The independent context is only available for the `remove` operation,
and removes the objective for all players in the database (even if offline).

| Parameter      | Syntax                             | Default Value          | Explanation                             |
|----------------|------------------------------------|------------------------|-----------------------------------------|
| _action_       | Keyword: `add`,`remove`,`complete` | :octicons-x-circle-16: | The action to do with the objective(s). |
| _objective(s)_ | `objectiveName` or `obj1,obj2`     | :octicons-x-circle-16: | The objective(s) to run the action on.  |


```YAML title="Example"
actions:
  startQuest: "objective add killTheDragon,goToDungeon"
  progressQuest: "objective complete killTheDragon"
```

## Execute operator commands

__Context__: @snippet:action-meta:online@  
__Syntax__: `opsudo <commands>`  
__Description__: Execute the commands as the player with temporary operator permissions.

This action is similar to the `sudo` action, the only difference is that it will fire a command as the player with temporary OP permissions.
Additional commands can be defined by separating them with `|` character. If you want to use a `|` character in the message use `\|`.

Looking for [execute as player commands](#execute-console-commands)?
Looking for [execute as console commands](#execute-console-commands)?

```YAML title="Example"
actions:
  spawn: "opsudo spawn"
```

## Run actions for the party

__Context__: @snippet:action-meta:online@  
__Syntax__: `party <range> <conditions> <actions> [amount]`  
__Description__: Run actions for all players in the party.

This is part of the [party system](../Parties.md).
Runs the specified list of actions (third argument) for every player in the party.
The last optional argument `amount` specifies a maximum number of players to select.
Selected players will be picked from the party if they are in range and meet the conditions.
Players are selected according to their distance from the player who triggered the action.
For example, if the 'amount' is two, the player who triggered the action and the player closest to that player will be selected.
A negative amount will select all players and therefore act as if there was no amount given.

```YAML title="Example"
actions:
  givePartyReward: "party 10 has_tag1,!has_tag2 give_reward"
  giveLimitedPartyReward: "party 10 has_tag1,!has_tag2 give_special_reward amount:3"
```

## Pick actions randomly

__Context__: @snippet:action-meta:independent@  
__Syntax__: `pickrandom <actions> [amount]`  
__Description__: Pick actions randomly from the specified list of actions.

Another container for actions. It picks one (or multiple) of the given actions and runs it.
You must specify how likely it is that each action is picked by adding the weighting before the action's id.
The weighting is a floating point number, that is the ratio of the action's chance to be picked.

It picks one action from the list by default, but you can add an optional `amount:` if you want more to be picked.
Note that only as many actions as specified can be picked and `amount:0` will do nothing.

```YAML title="Example"
actions:
  pickTwo: "pickrandom 20.5~action1,0.5~action2,79~action3 amount:2"
  PickThree: "pickrandom %point.factionXP.amount%~action1,0.5~action2,79~action3,1~action4 amount:3"
```

## Manage a point category

__Context__: @snippet:action-meta:online-offline@  
__Syntax__: `point <category> <amount> <action>`  
__Description__: Manage the points in the specific category.

First you can specify a number of points, then the modification action.
For that, you can use `action:add`, `action:subtract`, `action:set` and `action:multiply`
as modification types. This action also supports an optional `notify` argument that will display information about the
change using the notification system.

```YAML title="Example"
actions:
  gainAttitude: "point npc_attitude 5 action:add"
  loseAttitude: "point npc_attitude 2 action:subtract"
  resetCombo: "point combo 0 action:set"
  boostPoints: "point points 1.25 action:multiply notify"
```

## Remove entities

__Context__: @snippet:action-meta:independent@  
__Syntax__: `removeentity <entitys> <location> <radius> [name] [marked] [type]`  
__Description__: Remove or kill all entities of the specified type at the specified location.

Here you can look up all [type's of entity's](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/entity/EntityType.html).

Can only effect loaded entities!

| Parameter   | Syntax                                                                       | Default Value          | Explanation                                                                                                            |
|-------------|------------------------------------------------------------------------------|------------------------|------------------------------------------------------------------------------------------------------------------------|
| _entity(s)_ | `entity,entity`                                                              | :octicons-x-circle-16: | Required. List of entity's (separated by `,`).                                                                         |
| _location_  | [Unified Location Formatting](../Data-Formats.md#unified-location-formating) | :octicons-x-circle-16: | Required. The center location of the target entity's.                                                                  |
| _radius_    | Number                                                                       | :octicons-x-circle-16: | Required. The radius around the location.                                                                              |
| _name_      | `name:name`                                                                  | :octicons-x-circle-16: | Name of the entity.                                                                                                    |
| _marked_    | `marked:mark`                                                                | :octicons-x-circle-16: | Mark of the entity (from the [spawn action](../../Scripting/Building-Blocks/Actions-List.md#spawn-a-mob) for example). |
| _kill_      | `kill`                                                                       | :octicons-x-circle-16: | Whether to remove or actually kill the entity (if possible).                                                           |

```YAML title="Example"
actions:
  killArenaMobs: "removeentity ZOMBIE 100;200;300;world 10 name:Monster kill"
  clearGameArea: "removeentity ARROW,SNOWBALL,WOLF,ARMOR_STAND 100;200;300;world 50 marked:minigame"
```

## Run actions inline

__Context__: @snippet:action-meta:independent@  
__Syntax__: `run <actions>`  
__Description__: Allows you to specify multiple instructions in one long instruction.

Each instruction must be started with the `^` character (it divides all the instructions).
It's not the same as the `folder` action, because you have to specify the actual instruction, not an action name.
Don't use conditions here, it behaves strangely.

```YAML title="Example"
actions:
  eliminate: "run ^tag add beton ^journal add beton ^give emerald:5 ^kill"
```

## Run for online players

__Context__: @snippet:action-meta:independent@  
__Syntax__: `runForAll <actions> <where>`  
__Description__: Run the specified actions once for each online player.

The most common use case is to run an action for all online players from a [schedule](../Schedules.md).
But you can also use it in conversations, objectives, or other actions.

To run the actions only for a selection of players, use the `where:` option to filter for players that meet specific conditions.

| Parameter | Syntax             | Default Value          | Explanation                                                                                                                                                                                      |
|-----------|--------------------|------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| _actions_ | `actions:actions`  | :octicons-x-circle-16: | Required. The actions to be run, separated by `,`.                                                                                                                                               |
| _where_   | `where:conditions` | :octicons-x-circle-16: | A list of optional conditions (separated by `,`) that are checked for every player. <br>The actions supplied in `actions:` are only executed for the players that meet all the given conditions. |

!!! warning
    You can still append conditions to the `runForAll` action (e.g. `runForAll actions:kickPlayer conditions:!isOp`).  
    **This won't check the conditions for each player!**  
    Instead it will check the conditions for the player that triggered the action or check them player independent if
    triggered player independent (e.g. by a schedule).


```YAML title="Example"
actions:
  kickAll: "runForAll where:!isOp actions:kickPlayer,restartQuest"
```

## Run actions as independent

__Context__: @snippet:action-meta:independent@  
__Syntax__: `runIndependent <actions>`  
__Description__: Run the specified actions as if they were independent.

Runs the specified action (or list of actions) player independent (as if it was run from a [schedule](../Schedules.md)).

This is usefully for actions that behave differently when run player independent.

??? abstract "Actions that behave different if run player independent"
    * [`tag delete`](#manage-tags) - deletes the tag for all players in the database (even if offline)
    * [`objective remove`](#manage-objectives) - removes the objective for all players in the database (even if offline)
    * [`journal delete`](#manage-journal-entries) - deletes the journal entry for all players in the database (even if offline)
    * [`deletepoint`](#delete-a-point-category) - clears points of a given category for all players in the database (even if offline)

| Parameter | Syntax            | Default Value          | Explanation                                        |
|-----------|-------------------|------------------------|----------------------------------------------------|
| _actions_ | `actions:actions` | :octicons-x-circle-16: | Required. The actions to be run, separated by `,`. |

```YAML title="Example"
actions:
  resetQuestForAll: "runIndependent actions:removeObjective,clearTags,resetJournal"
```

!!! warning
    There are a lot of actions and conditions that cannot be run (or checked) player independent.  
    If you try to run such an action player independent (or check such a condition) this won't work,
    and you will get an error message in the console.
    
    For more information on player independent actions [check this](../Schedules.md#player-independent-actions).

## Manage a scoreboard objective

__Context__: @snippet:action-meta:online-offline@  
__Syntax__: `score <objective> <number> <action>`  
__Description__: Manage the scoreboard objective of the player.

This action works in the same way as [point](#manage-a-point-category), the only difference being that it uses scoreboards instead of points.
You can use `action:add`, `action:subtract`, `action:set` and `action:multiply` to change the value.
It's only possible to change the value, you have to create the scoreboard target first.

```YAML title="Example"
actions:
  gotKill: "score kill 1 action:add"
  gotKilled: "score kill 1 action:subtract"
  resetKill: "score kill 0 action:set"
  applyBonus: "score kill 1.2 action:multiply"
```

## Manage a scoreboard tag

__Context__: @snippet:action-meta:online@  
__Syntax__: `scoretag <operation> <tag>`  
__Description__: Manage the scoreboard tag of the player.

The kind of tags that are used by vanilla Minecraft and not the [betonquest tags](#manage-tags).

| Parameter        | Syntax            | Default Value          | Explanation                       |
|------------------|-------------------|------------------------|-----------------------------------|
| _modifier_       | `add` or `remove` | :octicons-x-circle-16: | Whether to add or remove the tag. |
| _scoreboard tag_ | Tag name          | :octicons-x-circle-16: | The name of the scoreboard tag.   |

```YAML title="Example"
actions:
  addVanillaTag: "scoretag add vanilla_tag"
  removeVanillaTag: "scoretag remove vanilla_tag"
```

## Set a block

__Context__: @snippet:action-meta:independent@  
__Syntax__: `setblock <block> <location> [ignorePhysics]`  
__Description__: Set the block at the specified location.

The first argument is a [Block Selector](../Data-Formats.md#block-selectors), the second a location. It's possible to
deactivate the physics of the block by adding `ignorePhysics` at the end.
Very powerful if used to trigger redstone contraptions.

```YAML title="Example"
actions:
  setRedstoneBlock: "setblock REDSTONE_BLOCK 100;200;300;world"
  flyingSand: "setblock SAND 100;200;300;world ignorePhysics"
```

## Manage a stage objective

__Context__: @snippet:action-meta:online-offline@  
__Syntax__: `stage <objective> <operation> <stage|amount>`  
__Description__: Manage the specified stage objective of the player.

The objective will not automatically complete when using `set`.
By increasing it the player will be able to complete the objective. When increasing or decreasing the stage
you can optionally specify an amount to increase or decrease by.  
When decreasing the objective it will do nothing when the first stage is reached.  
When the conditions of the stage objective are not met, the stage of the player can not be modified.  
For more take a look at the [stage objective](./Objectives-List.md#stages-stage).

| Parameter         | Syntax                          | Default Value          | Explanation                                     |
|-------------------|---------------------------------|------------------------|-------------------------------------------------|
| _stage objective_ | Objective                       | :octicons-x-circle-16: | The name of the stage objective                 |
| _action_          | `set`, `increase` or `decrease` | :octicons-x-circle-16: | The action to perform                           |
| _stage_           | Stage                           | :octicons-x-circle-16: | The name of the stage to set when `set` is used |
| _amount_          | Number                          | 1                      | The amount to increase or decrease by           |

```YAML title="Example"
actions:
  setCookCookies: "stage bakeCookies set cookCookies"
  increase: "stage bakeCookies increase"
  decrease2: "stage bakeCookies decrease 2"
```

## Spawn a mob

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

```YAML title="Example"
actions:
  spawnSkeleton: "spawn 100;200;300;world SKELETON 5 marked:targets"
  spawnZombie: "spawn 100;200;300;world ZOMBIE name:Bolec 1 h:blue_hat c:red_vest drops:emerald:10,bread:2"
```

## Execute player commands

__Context__: @snippet:action-meta:online@  
__Syntax__: `sudo <commands>`  
__Description__: Execute the commands as the player.

This action is similar to `command` action, the only difference is that it will fire a command as the player (often referred to as player commands).
Additional commands can be defined by separating them with `|` character. If you want to use a `|` character in the message use `\|`.

Looking for [execute as operator commands](#execute-operator-commands)?
Looking for [execute as console commands](#execute-console-commands)?

```YAML title="Example"
actions:
  spawn: "sudo spawn"
```

## Manage tags

__Context__: @snippet:action-meta:online-offline-independent@  
__Syntax__: `tag <operation> <tags>`  
__Description__: Manage the tags of the player.

The first argument after action's name must be `add` or `delete`. Next goes the tag name.
It can't contain spaces (though `_` is fine).
Multiple tags can be added and deleted separated by commas (without spaces).

The independent context is only available for the `delete` operation,
and delte the tag for all players in the database (even if offline).

```YAML title="Example"
actions:
  addStartTags: "tag add quest_started,new_entry"
```

## Take items

__Context__: @snippet:action-meta:online@  
__Syntax__: `take <items> [invOrder] [notify]`  
__Description__: Take items from the player’s inventory or backpack.

The items itself must be defined in the `items` section, optionally with an amount after a colon.
Which inventory types are checked is defined by the `invOrder:`
option. You can use `Backpack`, `Inventory`, `Offhand` and `Armor` there. One after another will be checked if multiple types are defined.

Note: If the items aren't quest items don't use `take` action with player options in conversations!
The player can drop items before selecting the option and pickup them after the action fires.
Validate it on the NPC’s reaction!

You can also specify `notify` keyword to display a simple message to the player about loosing items.

```YAML title="Example"
actions:
  emeraldsAndSword: "take emerald:120,sword"
  nuggets: "take nugget:6 notify"
  wand: "take wand notify invOrder:Backpack"
  money: "take money:50 invOrder:Backpack,Inventory"
  armor: "take armor invOrder:Armor,Offhand,Inventory,Backpack"
```

## Manage a world's time

__Context__: @snippet:action-meta:independent@  
__Syntax__: `time <time> [world] [ticks]`  
__Description__: Manage the time of the specified world.

The time is represented in 24 hours format as a float number, so 0 is midnight, 12 is
noon, and 23 is 11 PM. For minutes, you can use floating point numbers, so 0.5 is half past midnight, 0.25 is quarter
past midnight, and so on. (0.1 hours is 6 minutes). It's possible to add or subtract time by using `+` or `-` prefix or
to set the time by setting no prefix.
Additionally, you can specify the world in which the time will be changed, by adding `world:`.
Using the `ticks` argument changes the time like the vanilla command.

```YAML title="Example"
actions:
  set6: "time 6"
  increase: "time +0.1"
  decreaseRpgWorld: "time -12 world:rpgworld"
  increaseRandom: "time +%randomnumber.whole.100~2000% world:pvpworld ticks"
```

## Teleport a player

__Context__: @snippet:action-meta:online@  
__Syntax__: `teleport <location>`  
__Description__: Teleport the player to the specified location.

Ends any active conversations.

Do you only want to [cancel the conversation](#cancel-a-conversation)?

| Parameter  | Syntax                                                                       | Default Value          | Explanation                                          |
|------------|------------------------------------------------------------------------------|------------------------|------------------------------------------------------|
| _location_ | [Unified Location Formatting](../Data-Formats.md#unified-location-formating) | :octicons-x-circle-16: | The location to which the player will be teleported. |

```YAML title="Example"
actions:
  toCity: "teleport 432;121;532;world" # (1)!
  toHell: "teleport 123;32;-789;world_the_nether;180;45" # (2)!
```

1. Teleport the player to X: 432, Y: 121, Z: 532 in the world named 'world'.
2. Teleport the player to X: 123, Y: 32, Z: -789 in the world named 'world_the_nether'. Also set the head rotation to yaw 180 and pitch 45.

## Manage a variable objective

__Context__: @snippet:action-meta:online-offline@  
__Syntax__: `variable <objective> <key> <value>`  
__Description__: Manage values that are stored in `variable` objective variables.

The first argument is the ID of the `variable` objective. The second argument is the name of the variable to set.
The third argument is the value to set. Both the name and value can use `%...%` placeholders.
To delete a variable you can use `""`.
To store more complex values you can use [quoting](../Quoting-&-YAML.md#quoting).
Refer to the [`variable` objective](Objectives-List.md#variable-variable) documentation for more information about storing variables.
This action will do nothing if the player does not already have a `variable` objective assigned to them.

```YAML title="Example"
actions:
  goody: "variable CustomVariable MyFirstVariable Goodbye!"
  playerName: "variable variable_objectiveID name %player%"
  delete: 'variable other_var_obj desc ""'
```

## Manage player's velocity

__Context__: @snippet:action-meta:online@  
__Syntax__: `velocity <vector> <direction> <modification>`  
__Description__: Manage the velocity of the player.

| Parameter      | Syntax                          | Default Value          | Explanation                                                                                                                                                                                                                                                                                                               |
|----------------|---------------------------------|------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| _vector_       | `vector:(x;y;z)`                | :octicons-x-circle-16: | The values of the vector, which are decimal numbers, can be interpreted as absolute numbers like the coordinate or as relative directions. For more understanding the relative direction is similar to `^ ^ ^` in minecraft or in other words `(sideways;upwards;forwards)`.                                              |
| _direction_    | `direction:directionType`       | `absolute`             | There are 3 types how the vector can get applied to the player:<br> `absolute` won't change the vector at all.<br> `relative` will redirect the vector to the view of the player.<br> `relative_y` is a mix between absolute and relative. It will still direct to the view but only horizontally, so y will be absolute. |
| _modification_ | `modification:modificationType` | `set`                  | Possible modifications are `set` and `add`. The modification type determines how the vector should be merged with the player's velocity. The player's velocity is the external force applied on the player.                                                                                                               |

@snippet:general:relativeAxisExplanation@

```YAML title="Example"
actions:
  jumppad: "velocity vector:(2;0.8;4)"
  dash: "velocity vector:(0;0.1;1.3) direction:relative_y"
  individual_dash: "velocity vector:%objective.customPlaceholder.dashLength% direction:relative_y"
  fly: "velocity vector:(0;0.1;2) direction:relative modification:add"
```


## Manage a world's weather

__Context__: @snippet:action-meta:independent@  
__Syntax__: `weather <type> [duration] [world]`  
__Description__: Manage the weather of the world.

Sets the weather in the world the player is currently in. The argument is `sun` for clear, sunny weather, `rain` for pure rain,
`storm` for storm with rain, lightning, and thunder.
Durations less than 1 is equal to no duration.

| Parameter  | Syntax            | Default Value               | Explanation                                                                                 |
|------------|-------------------|-----------------------------|---------------------------------------------------------------------------------------------|
| _type_     | Keyword           | :octicons-x-circle-16:      | The type of weather to set. Either `sun`, `rain` or `storm`.                                |
| _duration_ | `duration:number` | Minecraft decides randomly. | The duration the weather will last (in seconds). <br> Is handled from minecraft afterwards. |
| _world_    | `world:worldName` | The player's current world. | The world to change the weather in.                                                         |

```YAML title="Example"
actions:
  setSun: "weather sun"
  setShortRain: "weather rain duration:60 world:rpgworld"
  setStorm: "weather storm duration:%point.tribute.left:150%"
```
