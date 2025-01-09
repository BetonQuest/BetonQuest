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

# (Re-)Moved Methods in 3.0

## Variable getting

Creating and parsing variables is now done in the `VariableProcessor`.
It is accessed from the BetonQuest instance with `#getVariableProcessor()`.

## Profiles
Currently, profiles are in development. So at the moment you can use the `PlayerConverter` class to get a profile.

## Base concepts
How to write and register new base concepts (events, conditions, objectives, variables) in BetonQuest.

### Writing events
Use the `BetonQuest.registerEvent(String name, EventFactory eventFactory, StaticEventFactory staticEventFactory)`
and `BetonQuest.registerNonStaticEvent(String name, EventFactory eventFactory)` method.
Read the Javadocs for more information and see the implementation of the existing events.
to call an event, you need to use the `BetonQuest.event(Profile profile, EventID eventID)` method.

### Writing objectives
Objectives are more complicated because they use event handlers and they must store players' data.
They extend `Objective` class.
As always, you need to extract all data from supplied `Instruction` object in the constructor.
Don't register listeners in the constructor!

If your objective handles changing data (like amount of mobs left to kill) you should create a class extending `ObjectiveData`.
For example `block` objective does need to store amount of blocks left to place/break, and it does that using "BlockData" class.
In the constructor it receives three strings: data string, ID of the player and ID of the objective.
The latter two are used by BetonQuest to correctly save and load the former one from the database.

The data string should contains all the information you need in your objective.
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
return an empty String and optionally log an error (`LogUtils.getLogger().log(...)`).

Objectives are registered the same way as conditions and events,
using `registerObjective(String name, Class<? extends Objective>)` method.

!!! warning
    IDE's typically autocomplete the wrong constructor. A correct constructor takes a single `Instruction` argument.

### Writing conditions
Writing conditions is easy too.
They must extend `Condition` and override `execute(String playerID)` method, which should return `true` or `false`,
depending on if the condition was met.
You register them using `registerConditions(String name, Class<? extends Condition)` method from BetonQuest instance as well.
The rest is almost the same, you're defining the constructor which will parse the `Instruction` object
and overriding `execute(String playerID)` method to check if the player meets the condition.
Don't worry about inverting it, as it's automatically done by BetonQuest.

Conditions are always getting an online player in the `execute(String playerID)` method, so you don't need to check that manually.

!!! warning
    IDE's typically autocomplete the wrong constructor. A correct constructor takes a single `Instruction` argument.


### Writing variables
All variables need to extend `Variable` class.
In the constructor you must parse the instruction and extract all information about your variable's behavior.
Then you have to override the `String getValue(String playerID)` method.
It should return the value of the variable for the supplied player.
If it's impossible, in the old it should return an empty String and in the new throw with a descriptive message.
Registering variables is done via `BetonQuest.registerVariable(String name, Class<? extends Variable> variable)` method.

## Reading `Instruction` object
The `Instruction` object parses the instruction string defined by the user and splits it into arguments.
You can ask it for required arguments one by one with `next()` method or a parser method like `getQuestItem()`.
Required arguments are the ones specified at the very beginning of an instruction string, for example `add someTag` in `tag` event.
It will automatically throw `QuestException` for you if it encounters an error,
for example when there were no more arguments in user's instruction or it can't parse the argument to the type you asked for.

You can also ask for optional arguments: if the instruction string contains argument `arg:something`
and you ask for optional `arg`, it will give you `something`. If there is no optional argument, it will return `null`.
Don't worry about passing that `null` to parser methods like `getLocation(String)`,
they won't throw an error, they'll simply return that `null`.

Parser methods are there for your convenience.
You could write a location parser for yourself, but there's no need for that,
you can just use `getLocation()` or `getLocation(String)` method and receive `LocationData` object.
The former method is simply `getLocation(next())`.

If your instruction is more complicated and `Instruction` class doesn't provide necessary methods,
you can still parse the instruction string manually. You can get it with `getInstruction()` method.
Just remember to throw `QuestException` when the instruction supplied by the user is incorrect.
BetonQuest will catch them and display a message in the console.

## Firing events
The plugin has a static method for firing events - `event(String playerID, EventID eventID)`.

You can't fire an event directly using an instruction string.

```JAVA title="Example"
final QuestPackage questPackage = Config.getPackages().get("myPackage") //(1)!
final Profile playerProfile = PlayerConverter.getID(player); //(2)!

BetonQuest.event(playerProfile, new EventID(questPackage, eventID)); 
```

1. You can get the package from the `Config` class. It's a map of all packages, so you can get the one you need by its
   name.
2. You can get the player's profile from the `PlayerConverter` class. You can use the player object to obtain a players 
   profile.

## Checking conditions
BetonQuest has static boolean method `condition(String playerID, String conditionID)`.
It works similarly as event method described above.

## Starting objectives
The `newObjective(String playerID, String objectiveID)` method will launch the objective from start.
You can however use `resumeObjective(String playerID, String objectiveID, String instruction)`
to pass your own `ObjectiveData` instruction to the objective.
It will not be saved to the database, because it is assumed that the objective has just been loaded from it and it exists there without any change.
You should save it manually.

## Creating additional conversation input/output methods
In order to register an object as the conversation input/output it needs to implement `ConversationIO` interface.
The constructor will receive three arguments: Conversation object, playerID String and NPC name String.
It needs to parse the required data here and register all needed listeners.
The `setResponse(String response)` method will receive NPC's text from the conversation. The `addOption(String option)`
method will be called by the conversation for each reply option for this NPC text.
The object must store all this data and when `display()` is called, it must use it to display the player the output.
When it detects that the player chose an answer, it should pass it to the conversation using `Conversation.passPlayerAnswer(int number)` method.
The integer is the number of the answer, starting at 1. `clear()` method will be called at the beginning of the new conversation cycle.
It should clear all the previous options, so they do not overlap. `end()` method will be called when the conversation ends, and it should unregister all listeners.
You can also call that message when you detect that the player forced conversation ending (for example by moving away from the NPC).
Remember to notify the conversation about that using `Conversation.end()`.

Registering the conversation inputs/outputs is done in the same way as objectives, events and conditions,
through `BetonQuest.registerConversationIO(String name, Class<? extends ConversationIO>)` method.

## Listening to BetonQuest (Bukkit) events

BetonQuest exposes some of its actions as Bukkit events.  
You can find these events in `org.betonquest.betonquest.api.bukkit.events` package.
[Use them as you would use any other Bukkit event](https://bukkit.fandom.com/wiki/Event_API_Reference#The_Basics).

If you need any additional events just open an issue or pull request on GitHub.
