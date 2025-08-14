---
icon: octicons/gear-16
---
!!! warning
    All described API on this page will break in the future of BetonQuest.
    Main parts are already reworked in BetonQuest 2.0, other parts will mainly be reworked in 3.0 and 4.0 in the far future.
    All API on this page will get replacements, but there are none at the moment, so if you use this API,
    you will have to rewrite your code in the future.
    The documentation on this page may not be up to date and may not be complete anymore.
    
    If we change something we will update the JavaDocs, so make sure to check them out if something seems to be missing here.
    This is the case because we prefer to delete outdated content from this page instead of updating it.
    New APIs will instead get their own page.

## (Re-)Moved Methods in 3.0

### Using Quest Types

The methods for using Conditions, Events and Objectives was moved into `BetonQuest.getInstance().getQuestTypeApi()`.

### Instruction

Hard coded get methods were removed in favor of a more dynamic approach. 
Read how to use them in the [Instruction Arguments](Instruction.md#argument) section.

### Variable getting

Creating and parsing variables is now done in the `VariableProcessor`.
It is accessed from the BetonQuest instance with `#getVariableProcessor()`.

## Profiles
Currently, profiles are in development. So at the moment you can use the `ProfileProvider` class to get a profile,
which can be accessed by `BetonQuest.getInstance().getProfileProvider()`.

## Base concepts
How to write and register new base concepts (events, conditions, objectives, variables) in BetonQuest.

For Conditions, Events and Variables there is the [new API](Writing-Implementations.md#writing-new-quest-type-implementations) available.

### Writing objectives
Objectives are more complicated because they often use event handlers, and they must store players' data.
They extend `Objective` class.
As always, you need to extract all data from supplied `Instruction` object in the constructor.
Don't register listeners in the constructor!

If your objective handles changing data (like amount of mobs left to kill) you should create a class extending `ObjectiveData`.
For example `block` objective does need to store amount of blocks left to place/break, and it does that using "BlockData" class.
In the constructor it receives three strings: data string, ID of the player and ID of the objective.
The latter two are used by BetonQuest to correctly save and load the former one from the database.

The data string should contain all the information you need in your objective.
You must write a parser which will extract the information, methods used in the objective to alter the information,
and override the `toString()` method in so it returns data string in the format parsable by your parser.
Everytime the data in your object changes (like when killing a mob), you need to call `update()` method.
It will save the data to the database.

Now you should override `getDefaultDataInstruction()` method.
It must return the default data instruction understandable by your parser.
For example in `tame` objective it will return the amount of mobs to tame.
If you don't use data objects, just return an empty string (not `null`, just `""`).

In order for your objective to use the data object you have created you need to set the `template` variable to this object's class.
If you're not defining the data object (because you don't need to handle the changing data), you should set the `template` simply to `ObjectiveData.class`.

Every time your objective accepts the player's action (for example killing the right mob in MobKill objective)
it must be also verified with `checkConditions()` method.
You don't want your objective ignoring all conditions, right?
When you decide that the objective is completed you should call `completeObjective()` method.
It will fire all events for you, so you don't have to do this manually.

`start()` and `stop()` methods must start objective's listeners and stop them accordingly.
It's because the plugin turns the objective's listeners off if there are no players having it active.
Here usually you will register/unregister listeners, but some objectives may be different.
For example `delay` objective starts and cancels a runnable, instead of using listeners.

If your objective has some properties (used in variables) you should override the `String getProperty(String property, String playerID)` method.
At runtime, if anyone uses `%objective.yourObjective.theProperty%` variable,
BetonQuest will call that method with `theProperty` keyword as the first argument.
Using it you should parse the data of the objective and return it as a String.
If the supplied property name is incorrect or there was an error during getting the value,
throw a `QuestException` with an informative message.

Objectives are registered the same way as conditions and events, see [register base concepts](#base-concepts).

## Firing events
The plugin has a static method for firing events - `event(Profile profile, EventID eventID)`.

You can't fire an event directly using an instruction string.

```JAVA title="Example"
final QuestPackage questPackage = BetonQuest.getInstance().getPackages().get("myPackage"); //(1)!
final Profile playerProfile = BetonQuest.getInstance().getProfileProvider().getProfile(player); //(2)!

BetonQuest.getInstance().getQuestTypeApi().event(playerProfile, new EventID(questPackage, eventID));
```

1. You can get the package from the `BetonQuest` class. It's a map of all packages, so you can get the one you need by its
   name.
2. You can get the player's profile from the `ProfileProvider` class. You can use the player object to obtain a players 
   profile.

When the eventID already contains the full path you can just pass `null` as package.

## Checking conditions
BetonQuest has static boolean method `condition(String playerID, String conditionID)`.
It works similarly to the event method described above.

## Starting objectives
The `newObjective(Profile profile, ObjectiveID objectiveID)` method will launch the objective from start.
You can however use `resumeObjective(Profile profile, ObjectiveID objectiveID, String instruction)`
to pass your own `ObjectiveData` instruction to the objective.
It will not be saved to the database, because it is assumed that the objective has just been loaded from it, and it exists there without any change.
You should save it manually.

## Creating additional conversation input/output methods
In order to register an object as the conversation input/output it needs to implement `ConversationIO` interface.
The constructor will receive three arguments: Conversation object, playerID String and NPC name String.
It needs to parse the required data here and register all needed listeners.
The `setResponse(Component response)` method will receive NPC's text from the conversation. The `addOption(Component option)`
method will be called by the conversation for each reply option for this NPC text.
The object must store all this data and when `display()` is called, it must use it to display the player the output.
When it detects that the player chose an answer, it should pass it to the conversation using `Conversation.passPlayerAnswer(int number)` method.
The integer is the number of the answer, starting at 1. `clear()` method will be called at the beginning of the new conversation cycle.
It should clear all the previous options, so they do not overlap. `end()` method will be called when the conversation ends, and it should unregister all listeners.
You can also call that message when you detect that the player forced conversation ending (for example by moving away from the NPC).
Remember to notify the conversation about that using `Conversation.end()`.

Registering the conversation inputs/outputs is done in the same way as objectives, events and conditions,
see [Registry](./Writing-Implementations.md#registry).
