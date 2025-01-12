---
icon: octicons/clock-16
---
# Schedules
@snippet:api-state:unfinished@

If the native [schedule](../Documentation/Scripting/Schedules.md) types are not enough for you, this API enables you to create your own type.

## API Overview
![](../_media/content/API/Schedules/Schedule-dark.svg#only-dark)
![](../_media/content/API/Schedules/Schedule-light.svg#only-light)

## Creating a new schedule type
To implement a new schedule type you have to create both the Schedule and the Scheduler class.

??? abstract "About this guide"
    This guide will show you how to create a custom schedule that runs every `x` ticks, just like a redstone clock.  
    While this does not make that much sense, it is a super simple example to show the principle.

### Schedule Class
The schedule class must hold all the schedules' data. When reloading BetonQuest will try to parse all packages and
construct new instances of this class.

Have a look at this example to see how to implement your own schedule.

```java linenums="1" title="Example Schedule"
public class MyCustomSchedule extends Schedule/* (1)! */ {

    private final int ticks, rebootSleep;

    // (2)!
    public MyCustomSchedule(ScheduleID scheduleID, ConfigurationSection instruction) throws QuestException { 
        super(scheduleID, instruction);
        try {
            ticks = Integer.parseInt(getTime()/* (3)! */);
        } catch (NumberFormatException e) {
            throw new QuestException("Time is not a number");// (4)!
        }

        if (getCatchup() != CatchupStrategy.NONE) {// (5)!
            throw new QuestException("Catchup " + getCatchup() + " is not supported by this schedule type");
        }

        rebootSleep = instruction.getInt("options.rebootSleep");// (6)!
    }

    public int getTicks() { return ticks; }
    public int getRebootSleep() { return rebootSleep; }
}
```

1.  You can extend either `Schedule` or `CronSchedule`.   
    The latter has already implemented all cron parsing logic.

2.  You need to define a Constructor that matches **exactly** this one.  
    **Otherwise BetonQuest can't parse your schedule!**

3.  `getTime()`/`super.time` provides the raw time string.  
    You'll need to parse it and add your own logic.  
    _In this example we just use it as interval of ticks._

4. Make sure to handle all parsing errors & thrown exceptions!  
   If a schedule can't be loaded due to an invalid instruction,
   you should throw a `QuestException` that describes
   the error.

5.  You'll have to implement your own handling of catchup strategies
    in the [`Scheduler`](#scheduler-class) class.  
    **If you don't want that, you can add this check if a CatchupStrategy
    was defined and throw an Exception.**

6.  This is how you add custom options, if needed.

### Scheduler Class

The scheduler will receive parsed schedules using `addSchedule(S)` and hold them in the `schedules` map.  
It should contain all the scheduling & schedule execution logic.  
It is also responsible for catching up missed schedules, if they have a catchup strategy other than `NONE` defined.

Here is a pretty basic example, that does not provide any catchup logic:

```java linenums="1" title="Example Scheduler"
public class MyCustomScheduler extends Scheduler<MyCustomSchedule>/* (1)! */ {

    private List<BukkitTask> tasks;

    @Override
    public void start() {
        super.start();// (2)!

        tasks = new ArrayList<>();
        for (MyCustomSchedule schedule : schedules.values()/* (3)! */) {
            // (4)!
            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    executeEvents(schedule);
                }
            }.runTaskTimer(MyPlugin.getInstance()/* (5)! */, schedule.getRebootSleep(), schedule.getTicks());
            
            tasks.add(task);// (6)!
        }
    }

    @Override
    public void stop() {
        super.stop();// (7)!
        
        for (BukkitTask task : tasks) {
            task.cancel();// (8)! 
        }
    }
} 
```

1.  Your scheduler must extend `Scheduler` class.  
    Between `<>` you have to put the name of your Schedule class.

2.  **Always remember to call `super.start()` in your `start()` method!**

3.  An easy way to iterate over all loaded schedules.

4.  Schedule your events to run when their time instruction says.

5. Pass your plugin instance for the Bukkit-Scheduler.

6. Keep a list of all your active schedules somewhere, so you can easily cancel them.

7. **Always remember to call `super.stop()` in your `stop()` method!**

8. Make sure to cancel all active schedules in `stop()` method.

### Register the type
To register the new schedule type to BetonQuest, use the following method:
```java
BetonQuest.getInstance().registerScheduleType("redstoneScheduler"/* (1)! */,
  MyCustomSchedule.class/* (2)! */,new MyCustomScheduler()/* (3)! */);
```

1. The name of your new schedule type to use in configs.

2. Your [Schedule](#schedule-class) class.

3. A new instance of your [Scheduler](#scheduler-class).

You'll need to call it after BetonQuest was enabled.
