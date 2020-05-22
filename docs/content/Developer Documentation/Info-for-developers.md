# Info for developers

## Accessing the plugin

You can either add BetonQuest.jar directly to your build path or use Maven. First option if you're using Eclipse:

1. Create a folder called lib in your project folder.
2. Put BetonQuest.jar in this folder.
3. Refresh your project in Eclipse.
4. In Eclipse Project Explorer right click on BetonQuest.jar and select `Build Path -> Add to Build Path`.

And if you're using Maven simply add this to your _pom.xml_:

```XML
<repositories>
    <repository>
        <id>betonquest-repo</id>
        <url>https://betonquest.pl/mvn</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>pl.betoncraft.betonquest</groupId>
        <artifactId>BetonQuest</artifactId>
        <version>1.8.5</version>
    </dependency>
</dependencies>
```

## Writing events

Writing events is the easiest. You need to create a class extending `QuestEvent` for each new event. The constructor must take one argument, an `Instruction` object. In the constructor you must extract all information from the instruction, for example skill names, locations etc. The description of the `Instruction` class is down below. Don't worry about checking event conditions, these are handled by the rest of BetonQuest's logic.

Events are not bound to any player so firing it is done through `fire(String playerID)` method. You have to override it with your code responsible for doing stuff your event should do. Here you should use data previously parsed by the constructor. Don't access `Instruction` object here, it will lower the performance. You can convert `playerID` to `Player` object using the `PlayerConverter` class (it's a relict of times when both UUIDs and names could be used in Bukkit to identify players).

If you want your event to be _persistent_, you need to set `super.persistent` variable to `true` in the constructor. This will make BetonQuest run this event even if the `playerID` points to an offline player, so prepare your code for that.

If you want your event to be _static_, you need to set `super.staticness` variable to `true` in the constructor. This will allow BetonQuest to run this event with `playerID` set to `null`, so prepare your code for that.

When you'll finish your class you need to invoke `registerEvents(String name, Class<? extends QuestEvent> class)` from BetonQuest instance (which you can get using `BetonQuest.getInstance()` static method). The name for your event will be used in instruction strings (such as "journal" for journal event). The class argument is the `Class` object of your event. You can get it using `YourEvent.class`. That's it, you created an event. Don't forget to check it for bugs!

## Writing conditions

Writing conditions is easy too. They must extend `Condition` and override `check(String playerID)` method, which should return `true` or `false`, depending on if the condition was met. You register them using `registerConditions(String name, Class<? extends Condition)` method from BetonQuest instance as well. The rest is almost the same, you're defining the constructor which will parse the `Instruction` object and overriding `check(String playerID)` method to check if the player meets the condition. Don't worry about inverting it, as it's automatically done by BetonQuest.

Conditions are always getting an online player in the `check(String playerID)` method, so you don't need to check that manually.

## Writing objectives

Objectives are more complicated because they use event handlers and they must store players' data. They extend `Objective` class. As always, you need to extract all data from supplied `Instruction` object in the constructor. Don't register listeners in the constructor!

If your objective handles changing data (like amount of mobs left to kill) you should create a class extending `ObjectiveData`. For example `block` objective does need to store amount of blocks left to place/break, and it does that using "BlockData" class. In the constructor it receives three strings: data string, ID of the player and ID of the objective. The latter two are used by BetonQuest to correctly save and load the former one from the database.

The data string should contains all the information you need in your objective. You must write a parser which will extract the information, methods used in the objective to alter the information, and override the `toString()` method in so it returns data string in the format parsable by your parser. Everytime the data in your object changes (like when killing a mob), you need to call `update()` method. It will save the data to the database.

Now you should override `getDefaultDataInstruction()` method. It must return the default data instruction understandable by your parser. For example in `tame` objective it will return the amount of mobs to tame. If you don't use data objects, just return an empty string (not `null`, just `""`).

In order for your objective to use the data object you have created you need to set the `template` variable to this object's class. If you're not defining the data object (because you don't need to handle the changing data), you should set the `template` simply to `ObjectiveData.class`.

Every time your objective accepts the player's action (for example killing the right mob in MobKill objective) it must be also verified with `checkConditions()` method. You don't want your objective ignoring all conditions, right? When you decide that the objective is completed you should call `completeObjective()` method. It will fire all events for you, so you don't have to do this manually.

`start()` and `stop()` methods must start objective's listeners and stop them accordingly. It's because the plugin turns the objective's listeners off if there are no players having it active. Here usually you will register/unregister listeners, but some objectives may be different. For example `delay` objective starts and cancels a runnable, instead of using listeners.

If your objective has some properties (used in variables) you should override the `String getProperty(String property, String playerID)` method. At runtime, if anyone uses `%objective.yourObjective.theProperty%` variable, BetonQuest will call that method with `theProperty` keyword as the first argument. Using it you should parse the data of the objective and return it as a String. If the supplied property name is incorrect or there was an error during getting the value, return an empty String and optionally log an error (`Debug.error(String message)`).

Objectives are registered the same way as conditions and events, using `registerObjective(String name, Class<? extends Objective>)` method.

## Reading `Instruction` object

The `Instruction` object parses the instruction string defined by the user and splits it into arguments. You can ask it for required arguments one by one with `next()` method or a parser method like `getQuestItem()`. Required arguments are the ones specified at the very beginning of an instruction string, for example `add someTag` in `tag` event. It will automaticly throw `InstructionParseException` for you if it encounters an error, for example when there were no more arguments in user's instruction or it can't parse the argument to the type you asked for.

You can also ask for optional arguments: if the instruction string contains argument `arg:something` and you ask for optional `arg`, it will give you `something`. If there is no optional argument, it will return `null`. Don't worry about passing that `null` to parser methods like `getLocation(String)`, they won't throw an error, they'll simply return that `null`.

Parser methods are there for your convenience. You could write a location parser for yourself, but there's no need for that, you can just use `getLocation()` or `getLocation(String)` method and receive `LocationData` object. The former method is simply `getLocation(next())`.

If your instruction is more complicated and `Instruction` class doesn't provide necessary methods, you can still parse the instruction string manually. You can get it with `getInstruction()` method. Just remember to throw `InstructionParseException` when the instruction supplied by the user is incorrect. BetonQuest will catch them and display a message in the console.

## Writing variables

All variables need to extend `Variable` class. In the constructor you must parse the instruction and extract all information about your variable's behavior. Then you have to override the `String getValue(String playerID)` method. It should return the value of the variable for the supplied player. If it's impossible, it should return an empty String. Registering variables is done via `BetonQuest.registerVariable(String name, Class<? extends Variable> variable)` method.

## Firing events

The plugin has a static method for firing events - `event(String playerID, EventID eventID)`. First parameter is ID of the player. Second one represents ID of the event. To get it, simply create an instance of the `EventID` class. You can't fire an event directly using an instruction string.

## Checking conditions

BetonQuest has static boolean method `condition(String playerID, String conditionID)`. It works similarly as event method described above.

## Starting objectives

The `newObjective(String playerID, String objectiveID)` method will launch the objective from start. You can however use `resumeObjective(String playerID, String objectiveID, String instruction)` to pass your own `ObjectiveData` instruction to the objective. It will not be saved to the database, because it is assumed that the objective has just been loaded from it and it exists there without any change. You should save it manually.

## Creating additional conversation input/output methods

In order to register an object as the conversation input/output it needs to implement `ConversationIO` interface. The constructor will receive three arguments: Conversation object, playerID String and NPC name String. It needs to parse the required data here and register all needed listeners. The `setResponse(String response)` method will receive NPC's text from the conversation. The `addOption(String option)` method will be called by the conversation for each reply option for this NPC text. The object must store all this data and when `display()` is called, it must use it to display the player the output. When it detects that the player chose an answer, it should pass it to the conversation using `Conversation.passPlayerAnswer(int number)` method. The integer is the number of the answer, starting at 1. `clear()` method will be called at the beginning of the new conversation cycle. It should clear all the previous options, so they do not overlap. `end()` method will be called when the conversation ends and it should unregister all listeners. You can also call that message when you detect that the player forced conversation ending (for example by moving away from the NPC). Remember to notify the conversation about that using `Conversation.end()`.

Registering the conversation inputs/outputs is done in the same way as objectives, events and conditions, through `BetonQuest.registerConversationIO(String name, Class<? extends ConversationIO>)` method.

## Listening to BetonQuest (Bukkit) events

BetonQuest calls Bukkit events on a few occasions: when a conversation is started, finished and when an option is selected. You can find these events in `pl.betoncraft.betonquest.api` package and use them in your plugins. If you need any additional events just open and issue on GitHub or send me a pull request.

## Debugging

You can debug your code using `LogUtils` class by simply call `LogUtils.getlogger().log(..)` to log something.

We use the following levels for this aspects:
- `SEVER` - Anything happen, that breaks the plugin, or a main function of the plugin
- `WARNING` - The most things, where something not normal or unexpected happens, but the plugin still work correctly for the rest(all catch blocks, when not SEVER)
- `INFO` - Anything you want to log, that also should appear in the normal console
- `CONFIG` - Not in use at the moment
- `FINE` - All messages, that you want to only appear in the debug log file, not in the console
- `FINER` - Do not use this, this is reserved, for exception logging, like you call LogUtils.logThrowable or LogUtils.logThrowableIgnore
- `FINEST` - Not in use at the moment

If you have a `catch` block, please log this with a message, that calls `exception.getMessage()` and log the complete exception by using the `Logutils.logThrowable(e)`. The methods `logThrowableReport()` and `logThrowableIgnore()` may only be executed by BetonQuest.
