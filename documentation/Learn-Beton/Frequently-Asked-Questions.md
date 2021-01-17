#FAQ
If you have any questions please read this page first. You can easily look for your questions using the table of contents 
to the right. It's very likely that it has been already asked and answered. 
If not, feel free to ask us in the
<a href="https://discordapp.com/invite/rK6mfHq" target="_blank">discord :fontawesome-brands-discord:</a> !.

## Where is the command for creating quests?
There is no such command. BetonQuest is too complex to edit it using commands or chest GUI's.
We have a [VSCode addon](./Getting-Started/Setting-up-the-editor.md) to make editing easier.


## Can you add particles over NPCs' heads like in "Quests" plugin?
Yes! Check out the [EffectLib](../User-Documentation/Compatibility/#effectlib) compatibility documentation.


## Can I assign multiple conversation files to one NPC?
No. You can use [cross-conversation-pointers](../User-Documentation/Conversations.md#cross-conversation-pointers) though.

## Can I delete all tags from a player at once?
No. You either need to run all `/q t del PLAYER PACKAGE.TAG` commands, or you can use `/q purge PLAYER` to reset an entire player profile.
If you want this as a scripted part of your quest however:
Either use a folder event that holds all tag-deleting events.
Or use a `run` event in which you specify all tag-deleting events at once.
(We will improve this don't worry)

## How do I start an objective from a conversation?
Use the [objective event](../User-Documentation/Events-List.md/#objective-objective).

## Why is X thing not working?
If something stopped working before asking for help please do /q reload and READ THE OUTPUT IN CONSOLE!
You will be able to see which events etc. loaded and which didn't with the reasons why.
You may be able to fix it yourself from this or use this information, so we can better help you!

Also double check you saved your files and if needed reupload them to your server.
This is insanely common and can be overlooked!

## I have an error which says "Cannot load plugins/BetonQuest/{someFile}.yml", what is wrong?
You have incorrect YAML syntax in your conversation file.
Usually it's because you started a line with `!` or `&`, forgot colons or made some weird things with apostrophes.

## How to get a formatted version of the BlockObjective's variables?
The math variable is perfect for this.
`/papi parse USER %betonquest_BlockVar:math.calc:|objective.test.left|`

## How to display "1 / 10" in objective notify
This problem can be solved by scripting a small custom message system.

The core ideas of that system are:

* An objective always has an amount of 1 as we want to show a message upon each progression towards our goal.

* The objective instantly restarts after it is finished thanks to the `persistent` argument. 
  It will be deleted using the objective event once it's finished.
    
* The systems logic is essentially just a point variable that is increased on each completion and a notify event being run.
  That notify event displays a the current objective progress.

* The objective gets reset with an event that has a condition bound to it. That means that the event will only be run once
  the condition is true. in this case the player needs 10 points / needs to have mined 10 blocks.

Here is an example for the block objective.
```YAML
#objectives.yml
mineStone: "block stone -1 persistent events:blockBroken"

#events.yml
blockBroken: "folder addPoint,sendNotify,checkForCompletion"

addPoint: "point blockCounter 1"
sendNotify: "notify &a%point.blockCounter.amount%&8/&210 &7stone broken. io:chat"

checkForCompletion: "run ^objective remove mineStone ^point blockCounter *0 conditions:has10Points"

#conditions.yml
has10Points: "point blockCounter 10"
``` 

## How can I let the NPC say things across multiple lines?
You need to use the PIPE `|` character at the start of the multiline string.
```YAML
text: |
  This is line one 
    line two
      line three
```

## How to get rid of not resolvable variables that show "0"?
You can just add them together using `math.calc`.
```
%math.calc:0-objective.PickWheat1.left-objective.PickWheat2.left-objective.PickWheat3.left-objective.PickWheat4.left'
```

## How to match different items with just one condition?
If you want a player to have e.g. `potato + poisonous_potato = 64` in his inventory you can make a special item in your 
items.yml file that matches items based of their names. 
More specifically, you can have a [*Block Selector*](../User-Documentation/Reference.md#block-selectors) that is a *regex*.
It would look like this in the example:
```YAML
#items.yml
anyPotato: ".*potato.*"

#conditions.yml
hasAnyPotato: "item anyPotato"
```

## How to store custom text in a variable / How to use the variable objective?
1. Start a variable objective for the player. It serves as a variable storage:
```YAML
#objectives.yml
 myVariableStorage: "variable no-chat"`
```

2. Assign values to that storage using a key and a value. Both can be any text you like:
```YAML
#events.yml
addBlock: "variable myVariableStorage blockName REDSTONE"
addLocation: "variable myVariableStorage location 123;456;789;world"
```

3. Read from your variable storage using the storages name and the data key.
```YAML
#conditions.yml
hasHeartBlock: "testforblock %objective.myVariableStorage.location% %objective.myVariableStorage.blockName%"
```

## Error "Quester is not defined"
You either actually did not define the `quester: someName` option at the top of your conversation, or your YAML syntax is invalid.
YAML Syntax Errors are the HUGE red lines that you see when you do /q reload.
They lead to the file not being properly read -> All kinds of errors like "can't find quester etc."
Copy your file into http://www.yamllint.com/ to confirm that it is actually a YAML error and fix your syntax.
Best practise is to define all options like this: `myOptionName: "myData"` The double quotes prevent YAMl issues with e.g. `!`.

## Other plugins override BetonQuest commands / BetonQuest overrides other commands!
You can change which command is used using a Bukkit feature: https://bukkit.gamepedia.com/Commands.yml

## Handling death in your quests

Sometimes, while writing a dangerous quest you will want something specific to happen when the player dies. If it's a boss battle you may want to fail the quest, if it's a dungeon you may want to respawn the player at the beginning of a level etc. You can do that with `die` objective - simply start it for the player at the beginning of the quest and make it fire events that will do the thing you want (like teleporting the player to desired respawn point, removing tags set during the quest etc). You can add `persistent` argument to the objective instruction string to make it active even after completing it. Remember to `delete` it after the quest is done!

## Creating regions for one player at the time

Imagine you have a room to which the player is teleported. Then suddenly mobs start to spawn and the player must kill them (because it's a trap or something). The player has killed all the mobs, he got a tag and wants to proceed but all of the sudden another player teleports into the room and all the mobs start to spawn again. The first player is quickly killed and the second one easily kills all mobs. You can prevent such situations by using `party` condition. Just check with it if the party consisting of "players inside the room" has greater amount of players that 1. Set the range to something big enough so it covers the room and the party condition can be tag or location.

## Racing with folder event

Since `folder` event can run `tag` events even for offline players you can create races. Create `location` objective where you want the finish line to be and condition it with negated "race_failed" tag (or similar). It will mean that "if the player has not failed the race, he can win it by reaching the location". Now when the race starts fire `folder` event with the amount of time you want to give your players to complete the race. This event should set "race_failed" tag. If the player reaches the location before this tag is set, he will fire all events in that `location` objective, but if the time has passed, the objective will not be completed. You can figure the rest out for yourself.

## Random daily quests

Starting the random quest must be blocked with a special tag. If there is no such tag, the conversation option should appear. Create a few quests, each of them started with single `folder` event (they **must** be started by single event!). Now add those events to another `folder` event and make it `random:1`. At the end of every quest add `delay` which will reset the special blocking tag. Now add that `folder` event to the conversation option. When the player chooses it he will start one random quest, and the conversation option will become available after defined in `delay` objective time after completing the quest.

## Each day different quest (same for every player)

To do this use something called "[Static event](../../User-Documentation/Reference/#static-events)". Using the static event run `folder` event every day at some late hour (for example 4am). The `folder` event should be `random:1` and contain several different `setblock` events. These events will set some specific block to several different material types (for example dirt, stone, wood, sand etc). Now when the player starts the conversation and asks about the daily quest the NPC should check (using `testforblock` condition) which type of block is currently set and give the player different quest, depending on the block type.

## Make the NPC react randomly

Imagine you want to lie to NPC and he has 15% chance of believing you completely, 35% of being suspicious and 50% of not believing at all. The common denominator for those percentages is 20, so we can write it as 3/20, 7/20 and 10/20. The NPC will check options one after another until it finds one which meets all conditions. We will use `random` condition with our options. The first one will have `3-20` chance (that's the format used by `random` condition). If this condition fails, the NPC will check next option. But it won't be `7-20`, because we already "used" 3 of 20. If you wrote it like that, the chance would be too low. That's why it will be `7-17`. The third option should have `10-10` (because `17 - 7 = 10` and 50% is 10/20), but as you can see it will always be true. It's because we want the last option to be shown if both previous fail. You don't have to add the last condition at all.

## Quest GUI

If you want your players to be able to choose a quest everywhere, every time, then you can create a conversation which can be started with an item. This one is a little hacky but it shows flexibility of BetonQuest. First you need a conversation which behaves as a quest choosing GUI. Name the NPC "Quester", add one option for each quest etc. Now you need an objective which will start this conversation using `conversation` event. It should be `action` objective, set to right click on any block. Add `hand` condition to make it accept only clicks with a specific item and make the objective `persistent` (so players can use it multiple times). The item used here should be marked as Quest Item so players can't drop it. Now define new global location covering your whole map and using it start the objective and give players the item. This way all players (existing and new) will get the quest item, which opens a GUI with quests when right clicked.
