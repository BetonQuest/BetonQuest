---
icon: material/chat-question
---
#FAQ
If you have any questions please read this page first. You can easily look for your questions using the table of contents 
to the right. It's very likely that it has been already asked and answered. 
If not, feel free to ask us in the
[Discord :fontawesome-brands-discord:](https://discordapp.com/invite/rK6mfHq)

## Where is the command for creating quests?
There is no such command. BetonQuest is too complex to edit it using commands or chest GUI's.

## Can you add particles over NPCs' heads like in "Quests" plugin?
Yes! Check out the [EffectLib](../Documentation/Scripting/Building-Blocks/Integration-List.md/#effectlib) compatibility documentation.


## Can I assign multiple conversation files to one NPC?
No. You can use [cross-conversation-pointers](../Documentation/Features/Conversations.md#cross-conversation-pointers) though.

## Can I delete all tags from a player at once?
No. You either need to run all `/q t del PLAYER PACKAGE.TAG` commands, or you can use `/q purge PLAYER` to reset an entire player profile.
If you want this as a scripted part of your quest however:
Either use a folder event that holds all tag-deleting events.
Or use a `run` event in which you specify all tag-deleting events at once.
(We will improve this don't worry)

## How do I start an objective from a conversation?
Use the [objective event](../Documentation/Scripting/Building-Blocks/Events-List.md/#objective-objective).

## Why is X thing not working?
If something stopped working before asking for help please do /q reload and READ THE OUTPUT IN CONSOLE!
You will be able to see which events etc. loaded and which didn't with the reasons why.
You may be able to fix it yourself from this or use this information, so we can better help you!

Also double check you saved your files and if needed re-upload them to your server.
This is insanely common and can be overlooked!

## I have an error which says "Cannot load plugins/BetonQuest/{someFile}.yml", what is wrong?
You have incorrect YAML syntax in your conversation file.
Usually it's because you started a line with `!` or `&`, forgot colons or made some weird things with apostrophes.

## How to get a formatted version of the BlockObjective's variables?
The math variable is perfect for this.
`/papi parse USER %betonquest_BlockVar:math.calc:|objective.test.left|%`

## How to display "1 / 10" in objective notify
This problem can be solved by scripting a small custom message system.

The core ideas of that system are:

* An objective always has an amount of 1 as we want to show a message upon each progression towards our goal.

* The objective instantly restarts after it is finished thanks to the `persistent` argument. 
  It will be deleted using the objective event once it's finished.
    
* The systems logic is essentially just a point variable that is increased on each completion and a notify event being run.
  That notify event displays the current objective progress.

* The objective gets reset with an event that has a condition bound to it. That means that the event will only be run once
  the condition is true. in this case the player needs 10 points / needs to have mined 10 blocks.

Here is an example for the block objective.
```YAML
objectives:
  mineStone: "block stone -1 persistent events:blockBroken"

events:
  blockBroken: "folder addPoint,sendNotify,checkForCompletion"
  
  addPoint: "point blockCounter 1"
  sendNotify: "notify &a%point.blockCounter.amount%&8/&210 &7stone broken. io:chat"
  
  checkForCompletion: "folder deleteObjective,deletePoint conditions:has10Points"
  deleteObjective: "objective remove mineStone"
  deletePoint: "deletepoint blockCounter"

conditions:
  has10Points: "point blockCounter 10"
``` 

## How can I let the NPC say things across multiple lines?
You need to use the PIPE `|` character at the start of the multiline string.
```YAML
text: |
  This is line one.
  This is line two.
          
          You
        can also
  format this using spaces.
```

## How to deal with unresolvable variables that show "0"?
You can just add them together using `math.calc`.
```
%math.calc:objective.PickWheat1.left+objective.PickWheat2.left+objective.PickWheat3.left+objective.PickWheat4.left'
```

## How to match different items with just one condition?
If you want a player to have e.g. `potato + poisonous_potato = 64` in his inventory you can make a special item in your 
`items` section file that matches items based of their names. 
More specifically, you can have a [*Block Selector*](../Documentation/Scripting/Data-Formats.md#block-selectors) that is a *regex*.
It would look like this in the example:
```YAML
items:
  anyPotato: ".*potato.*"

conditions:
  hasAnyPotato: "item anyPotato"
```

## How to store custom text in a variable / How to use the variable objective?
1. Start a variable objective for the player. It serves as a variable storage:
```YAML
objectives:
  myVariableStorage: "variable no-chat"
```

2. Assign values to that storage using a key and a value. Both can be any text you like:
```YAML
events:
  addBlock: "variable myVariableStorage blockName REDSTONE"
  addLocation: "variable myVariableStorage location 123;456;789;world"
```

3. Read from your variable storage using the storages name and the data key.
```YAML
conditions:
  hasHeartBlock: "testforblock %objective.myVariableStorage.location% %objective.myVariableStorage.blockName%"
```

## Error "Quester is not defined"
You either actually did not define the `quester: someName` option at the top of your conversation, or your YAML syntax is invalid.
YAML Syntax Errors are the HUGE red lines that you see when you do /q reload.
They lead to the file not being properly read -> All kinds of errors like "can't find quester etc."
Copy your file into http://www.yamllint.com/ to confirm that it is actually a YAML error and fix your syntax.
Best practise is to define all options like this: `myOptionName: "myData"` The double quotes prevent YAMl issues with e.g. `!`.

## Other plugins override BetonQuest commands / BetonQuest overrides other commands!
You can change which command is used, using a Bukkit feature: https://bukkit.fandom.com/wiki/Commands.yml

## Handling death in your quests

Sometimes, while writing a dangerous quest you will want something specific to happen when the player dies. If it's a boss battle you may want to fail the quest, if it's a dungeon you may want to respawn the player at the beginning of a level etc. You can do that with `die` objective - simply start it for the player at the beginning of the quest and make it fire events that will do the thing you want (like teleporting the player to desired respawn point, removing tags set during the quest etc). You can add `persistent` argument to the objective instruction string to make it active even after completing it. Remember to `delete` it after the quest is done!

## Creating regions for one player at the time

Imagine you have a room to which the player is teleported. Then suddenly mobs start to spawn and the player must kill them (because it's a trap or something). The player has killed all the mobs, he got a tag and wants to proceed but all of the sudden another player teleports into the room and all the mobs start to spawn again. The first player is quickly killed and the second one easily kills all mobs. You can prevent such situations by using `party` condition. Just check with it if the party consisting of "players inside the room" has greater amount of players than 1. Set the range to something big enough so it covers the room and the party condition can be tag or location.

## Racing with folder event

Since `folder` event can run `tag` events even for offline players you can create races. Create `location` objective where you want the finish line to be and condition it with negated "race_failed" tag (or similar). It will mean that "if the player has not failed the race, he can win it by reaching the location". Now when the race starts fire `folder` event with the amount of time you want to give your players to complete the race. This event should set "race_failed" tag. If the player reaches the location before this tag is set, he will fire all events in that `location` objective, but if the time has passed, the objective will not be completed. You can figure the rest out for yourself.

## Random daily quests

Starting the random quest must be blocked with a special tag. If there is no such tag, the conversation option should appear. Create a few quests, each of them started with single `folder` event (they **must** be started by single event!). Now add those events to another `folder` event and make it `random:1`. At the end of every quest add `delay` which will reset the special blocking tag. Now add that `folder` event to the conversation option. When the player chooses it he will start one random quest, and the conversation option will become available after defined in `delay` objective time after completing the quest.

## The same random daily quest for every player

To do this use something called "[Schedules](../Documentation/Scripting/Schedules.md)
Run a scheduled `folder` event every day at some late hour (for example 4am).
The `folder` event should be `random:1` and contain several different `globaltag` events.
These events will set a specific tag. Now when the player starts the conversation and asks about the daily quest the NPC
should check (using the `globaltag` condition) which tag is currently set and give the player different quests based on that.
Of course, the scheduled folder event also needs to remove the current tag before setting a new one.

## Global Quests (all players work together)

There is no easy way to do this (yet). Additionally, every use case differs. Let's assume you have some sort of event
on your server where your player's need to fish 100 salmons. The quest package is only installed during the event.

Create an objective that is immediately fired upon the first interaction (this means setting the amount to one for most objectives).
That objective must be `persistent` so it restarts immediately upon completion. It also has to be `global` so every
player will receive it upon joining the server.
```YAML
# fish a salmon to progress the global quest
gQuest: fish SALMON 1 events:gQuestProgress global persistent
```
The objective would trigger a folder event that increases a `globalpoint` variable by one and tries to run the
events that are fired upon completion. That globalpoint variable tracks the players combined progress.
The "completion events" must be limited by a `globalpoint` condition that checks whether the `globalpoint` variable has
reached a certain value.

=== "events"
    ```YAML
    # 1. increase the global variable 2. wait one tick for the change to process 3. attempt to run the completion events
    gQuestProgress: folder gQuestIncrementCounter,gQuestCheckCompletion period:1 ticks
    # Adds 1 to the global variable
    gQuestIncrementCounter: globalpoint gQuest 1
    # Runs completion events only when the condition is met (= the global variable reached X points)
    gQuestCheckCompletion: folder gQuestNotify,gQuestOnCompletion,gDeleteObjective condition:gQuestComplete
    # Deletes the objective from everyone that fished a salmon after the goal was met
    qDeleteObjective: "objective delete gQuest"
    ```
=== "conditions"
    ```YAML
    # Complete at one hundred collected
    gquest_complete: globalpoint gquest 100

    ```
Downsides to this approach:

* Only the player that fished the final salmon (number 100) will get the reward immediately. All other players need to fish an additional
salmon to trigger the completion logic. Therefore a central NPC that also gives out rewards and shows the
progress is recommended.

* Since some players logged off during the event while still having the objective, a clean-up package should be installed
after the event. It will remove the objective from them - this is important as BetonQuest will complain about objectives
that are still active for a player but are not referenced in any quest package. This will happen since you have to
remove the event package after the event.

Such a package holds the original objective and clean-up objective:

=== "objectives"
    ```YAML
    # Old objective just without global & persistent to make sure no one get's it automatically
    gQuest: fish SALMON 1 events:gQuestProgress
    # Cleanup objective that is immediately completed when someone joins
    login events:deleteOldObjective global
    ```

=== "events"
    ```YAML
    # Deletes the old objective from the current player
    deleteOldObjective: "objective delete gQuest"
    ```

## Make the NPC react randomly

Imagine you want to lie to NPC and he has 15% chance of believing you completely, 35% of being suspicious and 50% of not believing at all. The common denominator for those percentages is 20, so we can write it as 3/20, 7/20 and 10/20. The NPC will check options one after another until it finds one which meets all conditions. We will use `random` condition with our options. The first one will have `3-20` chance (that's the format used by `random` condition). If this condition fails, the NPC will check next option. But it won't be `7-20`, because we already "used" 3 of 20. If you wrote it like that, the chance would be too low. That's why it will be `7-17`. The third option should have `10-10` (because `17 - 7 = 10` and 50% is 10/20), but as you can see it will always be true. It's because we want the last option to be shown if both previous fail. You don't have to add the last condition at all.

## Quest GUI

If you want your players to be able to choose a quest everywhere, every time, then you can create a conversation which can be started with an item. This one is a little hacky but it shows flexibility of BetonQuest. First you need a conversation which behaves as a quest choosing GUI. Name the NPC "Quester", add one option for each quest etc. Now you need an objective which will start this conversation using `conversation` event. It should be `action` objective, set to right click on any block. Add `hand` condition to make it accept only clicks with a specific item and make the objective `persistent` (so players can use it multiple times). The item used here should be marked as Quest Item so players can't drop it. Now define new global location covering your whole map and using it start the objective and give players the item. This way all players (existing and new) will get the quest item, which opens a GUI with quests when right clicked.

## Non-Linear Objectives in Quests

If ever you're making a quest that has the player completing multiple objectives at once in order to complete the quest
itself, you may want to add the option of being able to complete the objectives in a non-linear fashion (Objective C ->
Objective A -> Objective B -> Completed). There are multiple ways of doing this but this one is probably the simplest.
Firstly, create as many objectives as you want. We are going to be working with three objectives:

=== "objectives"
    ```YAML
    Objective_A: Objective_Arguments events:Rewards
    Objective_B: Objective_Arguments events:Rewards
    Objective_C: Objective_Arguments events:Rewards
    ```

Now that the player has been given these three objectives, we will also create three `objective` conditions that check
the player for these objectives:

=== "conditions"
    ```YAML
    Has_Objective_A: objective Objective_A
    Has_Objective_B: objective Objective_B
    Has_Objective_C: objective Objective_C
    ```
We will also create one `and` condition, which means a player must (or must not, depending on negation)  meet all
conditions in order for it to return as true. In this case, the player must *not* be in the process of completing these
objectives. The `!` in front of the ConditionIDs negates the arguments within the condition. Make sure you have wrapped
the condition with `'` or `"` depending on your preferences.

=== "conditions"
    ```YAML
    All_Objectives_Done: 'and !Has_Objective_A,!Has_Objective_B,!Has_Objective_C'
    ```

Finally, create the event that you wanted to use to give the quest rewards to the player. To this event, you will add
the `All_Objectives_Done` condition. This ensures that the event will not be fired unless the player has completed all
objectives.

=== "events"
    ```YAML
    Rewards: RewardEventArguments condition:All_Objectives_Done
    ```

Now, simply add this `Rewards` event to every one of your objectives and you have now created a way for players to
complete a quest's objective in a non-linear fashion! You can add as many or as little objectives as you want, you just
have to add the additional objectives to the conditions.

## Creating quest menus
To create a menu that gives the player an overview of his open quests just define one menu item for each quest.
Set the [conditions](../Documentation/Features/Menus/Menu.md#the-items-section) for this item, so it is only displayed if the quest is not finished (use the [tag condition](../Documentation/Scripting/Building-Blocks/Conditions-List.md#tag-tag)).  
Then assign all those items to [a row of slots](../Documentation/Features/Menus/Menu.md#the-slots-section) so that they are sorted perfectly.

You can also add click events to display npc locations, add compass targets, directly open the conversations or cancel the quest.

Or you could define separate items for open and finished quests or even to show the progress. Just be a bit creative.

## Menus displaying players stats
You may also use menus to display the stats of a player. Just use [variables](../Documentation/Scripting/Building-Blocks/Variables-List.md) in the text or for the amount of an item.

For example try displaying a players money using the variable from [Vault integration](http://dev.bukkit.org/bukkit-plugins/vault/)
or use [PlaceholderAPI](../Documentation/Scripting/Building-Blocks/Integration-List.md#placeholderapi) to show placeholders from many other plugins.
