---
icon: octicons/clock-16
---
# Schedules
Schedules allow you to run events periodically at specific times for the entire server.

## Player independent events
Whenever events are run from a conversation or an objective, they are always run for a specific player.
For events run from a schedule this is not the case as there is no specific player involved.   
This means you can only use events that are player independent, like `setblock` or `globaltag`, in schedules.
The same applies to the conditions used by these events.

To determine if an event is player independent (and can be used in schedules), look for the **static** flag in the docs.

!!! example annotate
    <h2>Set Block: `setblock`</h2>

    **persistent**, ==**static**== (1)

    Changes the block at the given position.

1.  This flag states that `setblock` can be used player independent.

Some events behave differently when called from a schedule in independent mode.
For example, `tag delete` will include offline players.
A list of all events that act differently can be found in the [`runIndependent`](Building-Blocks/Events-List.md#run-events-player-independent-runindependent) docs.

But sometimes you might want your schedule to run a player dependent event, like `message` or `give` for all players on the server.
To do this you can use the [`runforall`](Building-Blocks/Events-List.md#run-events-for-all-online-players-runforall) event. It will run the given events for all players on the server. 
You can even use conditions to filter out players.

## Realtime schedules

Realtime schedules are, as the name already says, schedules that run at a specific real world time, for example at 12
o'clock each day.
Do not confuse these with Minecraft's ingame time!

The time is provided by the system time of the computer your minecraft server is running on, in the systems time zone.  

### Daily realtime schedule: `realtime-daily`

A super simple to use type of schedule, but also limited in its functionality.  
Just specify the time of the day when the events should run, and they will run every day at that same time.

=== "Simple Example"

    ```YAML
    schedules:
      sayGoodNight: #(1)!
        type: realtime-daily #(2)!
        time: '22:00' #(3)!
        events: bell_ring,notify_goodNight #(4)!
    ```

    1.  The name of the schedule.

    2.  The `type` of this schedule is always `realtime-daily`.

    3.  `time` is the time of day when the schedule should be run in format `HH:mm`.

    4.  An event (or multiple separated by `,`) that should run at the given time.

    _Runs every day at 10pm, will ring a bell and wish everyone a good night._

=== "Full Example"

    ```YAML
    # This example works out of the box. Copy-paste to try out how it works.
    events:
      bell_sound: 'notifyall io:sound sound:block.bell.use'
      bell_ring: 'folder bell_sound,bell_sound,bell_sound,bell_sound period:0.5'
      notify_goodNight: 'notifyall &6Good night, sleep well!'
    schedules:
      sayGoodNight:
        type: realtime-daily
        time: '22:00'
        events: bell_ring,notify_goodNight
    ```
---

!!! warning
    **The time must always be in `''` to avoid problems. It needs leading zero if less than 10.**

### Cron realtime schedule: `realtime-cron`

The cron realtime schedule is an incredibly flexible tool to define when events shall run.  
It is similar to the [`realtime-daily`](#daily-realtime-schedule-realtime-daily) schedule but the time is defined as a
[cron expression](https://en.wikipedia.org/wiki/Cron).  
The supported syntax is identical to the original unix crontab syntax.

!!! tip

    **[Crontab Guru](https://crontab.guru/)**
    is a great tool for learning and testing cron expressions.
    It also provides a long list of
    **[examples](https://crontab.guru/examples.html)**.
    BetonQuest supports all features listed there, **even the non-standard ones!**

=== "Simple Example"

    ```YAML
    schedules:
      sayGoodNight: #(1)!
        type: realtime-cron #(2)!
        time: '0 22 * * *' #(3)!
        events: bell_ring,notify_goodNight #(4)!
    ```

    1.  The name of the schedule.

    2.  The `type` of this schedule is always `realtime-cron`.

    3.  `time` is the cron expression that defines when the schedule should be run.  
        **Use [crontab.guru](https://crontab.guru/)!**

    4.  An event (or multiple separated by `,`) that should run at the given time.

    _Runs every day at 10pm, will ring a bell and wish everyone a good night._

=== "Full Example"

    ```YAML
    # This example works out of the box. Copy-paste to try out how it works.
    events:
      bell_sound: 'notifyall io:sound sound:block.bell.use'
      bell_ring: 'folder bell_sound,bell_sound,bell_sound,bell_sound period:0.5'
      notify_goodNight: 'notifyall &6Good night, sleep well!'
    schedules:
      sayGoodNight:
        type: realtime-cron
        time: '0 22 * * *'
        events: bell_ring,notify_goodNight
    ```
---

The following special expressions were added for extended functionality or simpler usage:

| Expression              | Description                                                    | Equivalent to |
|:------------------------|:---------------------------------------------------------------|:-------------:|
| `@reboot`               | Run at server startup, before catching up any missed schedules |      _-_      |
| `@hourly`               | Run once an hour at the beginning of the hour                  |  `0 * * * *`  |
| `@daily` / `@midnight`  | Run once a day at 00:00                                        |  `0 0 * * *`  |
| `@weekly`               | Run once a week at 00:00 on Sunday morning                     |  `0 0 * * 0`  |
| `@monthly`              | Run once a month at 00:00 of the first day of the month        |  `0 0 1 * *`  |
| `@yearly` / `@annually` | Run once a year at 00:00 of 1 January                          |  `0 0 1 1 *`  |

### Catchup Strategies

Obviously, scheduled events can't be run while the server is shut down.  
If you want to be sure that a schedule will nevertheless be run, you can define a **catchup strategy**.

On startup, BetonQuest checks which schedules have been missed and (if needed) they will be run on the first tick.  
Schedules of the same type will be run in the order they were missed.
For mixed types the **order** can **not** be **guaranteed**.

=== "NONE"

    ```YAML title="Example"
    schedules:
      sayGoodNight: 
        type: realtime-daily
        time: '22:00'
        events: bell_ring,notify_goodNight
        catchup: none #(1)!
    ```

    1.  Don't catch up any missed schedules after reboot.  
        _(default value, can be ommitted)_

    _As it's just an announcement we don't need to repeat it. The right time has passed._

=== "ONE"

    ```YAML title="Example"
    schedules:
      resetQuarryArea: 
        type: realtime-daily
        time: '03:00'
        events: pasteQuarry
        catchup: one #(1)!
    ```

    1.  If the schedule was missed (no matter how often) run it **once** after reboot.

    _The quarry should be reset every day at 3am. Even if the server was shut down at that time, run the event once at reboot._

=== "ALL"

    ```YAML title="Example"
    schedules:
      greedyMarchant: 
        type: realtime-daily
        time: '03:00'
        events: increaseFuelPrice
        catchup: all #(1)!
    ```

    1.  Run the schedule as often as it was missed after reboot.

    _Inflation! The fuel marchant increases it's price every day at 3am. If the sever was shut down, prices should increase at the same pace._
---

!!! danger

    **If the server was shut down for a long time, running all missed schedules can be a too heavy task for the server to
    handle.**

    **For example using `realtime-daily` type with a syntax like `* * * * *` (run every minute) and catchup strategy `ALL` will create `86 400` missed shedules per day!**

    **So be very cautious when using `ALL` catchup strategy!**

    By deleting `.cache/schedules.yml` before startup you can make BetonQuest forget about any missed schedules :wink:
