# Tips and tricks

## Handling death in your quests

Sometimes, while writing a dangerous quest you will want something specific to happen when the player dies. If it's a boss battle you may want to fail the quest, if it's a dungeon you may want to respawn the player at the beginning of a level etc. You can do that with `die` objective - simply start it for the player at the beginning of the quest and make it fire events that will do the thing you want (like teleporting the player to desired respawn point, removing tags set during the quest etc). You can add `persistent` argument to the objective instruction string to make it active even after completing it. Remember to `delete` it after the quest is done!

## Creating regions for one player at the time

Imagine you have a room to which the player is teleported. Then suddenly mobs start to spawn and the player must kill them (because it's a trap or something). The player has killed all the mobs, he got a tag and wants to proceed but all of the sudden another player teleports into the room and all the mobs start to spawn again. The first player is quickly killed and the second one easily kills all mobs. You can prevent such situations by using `party` condition. Just check with it if the party consisting of "players inside the room" has greater amount of players that 1. Set the range to something big enough so it covers the room and the party condition can be tag or location.

## Racing with folder event

Since `folder` event can run `tag` events even for offline players you can create races. Create `location` objective where you want the finish line to be and condition it with negated "race_failed" tag (or similar). It will mean that "if the player has not failed the race, he can win it by reaching the location". Now when the race starts fire `folder` event with the amount of time you want to give your players to complete the race. This event should set "race_failed" tag. If the player reaches the location before this tag is set, he will fire all events in that `location` objective, but if the time has passed, the objective will not be completed. You can figure the rest out for yourself.

## Random daily quests

Starting the random quest must be blocked with a special tag. If there is no such tag, the conversation option should appear. Create a few quests, each of them started with single `folder` event (they **must** be started by single event!). Now add those events to another `folder` event and make it `random:1`. At the end of every quest add `delay` which will reset the special blocking tag. Now add that `folder` event to the conversation option. When the player chooses it he will start one random quest, and the conversation option will become available after defined in `delay` objective time after completing the quest.

## Each day different quest (same for every player)

To do this use something called "[Static event](https://github.com/Co0sh/BetonQuest/wiki/Other-important-stuff#static-events)". Using the static event run `folder` event every day at some late hour (for example 4am). The `folder` event should be `random:1` and contain several different `setblock` events. These events will set some specific block to several different material types (for example dirt, stone, wood, sand etc). Now when the player starts the conversation and asks about the daily quest the NPC should check (using `testforblock` condition) which type of block is currently set and give the player different quest, depending on the block type.

## Make the NPC react randomly

Imagine you want to lie to NPC and he has 15% chance of believing you completely, 35% of being suspicious and 50% of not believing at all. The common denominator for those percentages is 20, so we can write it as 3/20, 7/20 and 10/20. The NPC will check options one after another until it finds one which meets all conditions. We will use `random` condition with our options. The first one will have `3-20` chance (that's the format used by `random` condition). If this condition fails, the NPC will check next option. But it won't be `7-20`, because we already "used" 3 of 20. If you wrote it like that, the chance would be too low. That's why it will be `7-17`. The third option should have `10-10` (because `17 - 7 = 10` and 50% is 10/20), but as you can see it will always be true. It's because we want the last option to be shown if both previous fail. You don't have to add the last condition at all.

## Quest GUI

If you want your players to be able to choose a quest everywhere, every time, then you can create a conversation which can be started with an item. This one is a little hacky but it shows flexibility of BetonQuest. First you need a conversation which behaves as a quest choosing GUI. Name the NPC "Quester", add one option for each quest etc. Now you need an objective which will start this conversation using `conversation` event. It should be `action` objective, set to right click on any block. Add `hand` condition to make it accept only clicks with a specific item and make the objective `persistent` (so players can use it multiple times). The item used here should be marked as Quest Item so players can't drop it. Now define new global location covering your whole map and using it start the objective and give players the item. This way all players (existing and new) will get the quest item, which opens a GUI with quests when right clicked.
