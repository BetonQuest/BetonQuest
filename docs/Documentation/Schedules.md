---
icon: octicons/clock-16
---
# Schedules
Schedules allow you to run some events periodically at specific times.

### Static Events

When running events from a schedule it is unclear how events should behave:  
Should an event be run once for each player on the server?  
For events like `setblock` this would mean that the event is executed 20 times if 20 players are online.  

This problem is solved by dividing all events into two categories:

1.  **Static events** are not tied to a specific player, meaning they could be run independent.  
    `setblock` for example always changes the same block, no matter for who it was called.  
    When run by a schedule a static event will fire exactly once.

2.  **Non-static events** are always tied to a specific player. 
    They will be run once for each online player.

!!! warning
    **Static events used by schedules cannot have conditions defined, 
    as the plugin cannot check any condition without the player.**


All static events have a static flag in the docs, so you can easily distinguish them from non-static ones.

!!! example
    ## Set Block: `setblock` { data-search-exclude #markdown data-toc-label='Example: static event flag'}

    **persistent**, ==**static**== (1)
    { .annotate }

    Changes the block at the given position.

    1. This flag states that `setblock` can be used as static event.

## Simple schedule: `simple`
A super simple to use type of schedule, but also limited in its functionality.  
Just specify the time of the day when the events should run, and they will run every day at that same time.


=== "Simple Example"

    ```YAML
    schedules:
      sayGoodNight: #(1)!
        type: simple #(2)!
        time: '22:00' #(3)!
        events: bell_ring,notify_goodNight #(4)!
    ```

    1.  The name of the schedule.

    2.  The `type` of this schedule is always `simple`.

    3.  `time` is the time of day when the schedule should be run in format `HH:mm`.

    4.  An event (or multiple separated by `,`) that should run at the given time.

    _Runs every day at 10pm, will ring a bell and wish everyone a good night._

=== "Full Example"

    ```YAML
    variables:
      lever_location: '18;4;7;world'
    events:
      bell_ring: 'folder bell_lever_toggle,bell_lever_toggle,bell_lever_toggle,bell_lever_toggle period:0.5'
      bell_lever_toggle: 'lever $lever_location$ toggle'
      notify_goodNight: 'notify &6Good night, sleep well!'
    schedules:
      sayGoodNight:
        type: simple
        time: '22:00'
        events: bell_ring,notify_goodNight
    ```

!!! warning
    **The time must always be in `''` to avoid problems. It needs leading zero if less than 10.**

While the server is shut down, of course no events can be run so some runs of this schedule might get missed.  
If you want to be sure that a schedule will be executed even if the server has been shut down at that time  
you can define a **catchup strategy**

On startup, it is checked which schedules have been missed and (if needed) they will be run on the first tick.  
Schedules of the same type will be run in the order they were missed.
For mixed types the **order** can **not** be **guaranteed**.

=== "NONE"

    ```YAML title="Example"
    schedules:
      sayGoodNight: 
        type: simple
        time: '22:00'
        events: bell_ring,notify_goodNight
        catchup: none #(1)!
    ```

    1.  Don't catch up any missed schedules after reboot.  
        _(default value, can be ommitted)_

    _As it's just an announcement we don't care what hapens after restart._

=== "ONCE"

    ```YAML title="Example"
    schedules:
      resetQuarryArea: 
        type: simple
        time: '03:00'
        events: pasteQuarry
        catchup: once #(1)!
    ```

    1.  If the schedule was missed (no matter how often) run it **once** after reboot.

    _The quarry should be reset every day at 3am. If the server was shut down at that time, run the event once at reboot._

=== "ALL"

    ```YAML title="Example"
    schedules:
      greedyMarchant: 
        type: simple
        time: '03:00'
        events: increaseFuelPrice
        catchup: all #(1)!
    ```

    1.  Run the schedule as often as it was missed after reboot.

    _Inflation! The fuel marchant increases it's price every day at 3am. If the sever was shut down, prices should increase at the same pace._

!!! warning
    **If the server was shut down for a long time, running all missed schedules can be a too heavy task for the server to handle.**  
    **Especially if you have a lot of schedules with catchup strategy `ALL`.**

    **So be very cautious when using `ALL` catchup strategy!**

    By deleting `.cache/schedules.yml` before startup you can make BetonQuest forget about any missed schedules :wink:

## Realtime schedule: `realtime`

The realtime schedule is an incredibly flexible tool to define when events shall run.  
It is similar to the [`simple`](#simple-schedule-simple) schedule but the time is defined as [**cron expression**](https://en.wikipedia.org/wiki/Cron).  
The supported syntax is identical to the original unix crontab syntax.

!!! tip
    **https://crontab.guru/** is a great tool for learning and testing cron expressions.
    It also provides a long list of [**examples**](https://crontab.guru/examples.html).  
    We do support all features listed there, **even the non-standard ones!**

=== "Simple Example"

    ```YAML
    schedules:
      sayGoodNight: #(1)!
        type: realtime #(2)!
          time: '0 22 * * *' #(3)!
          events: bell_ring,notify_goodNight #(4)!
          catchup: none #(5)!
    ```

    1.  The name of the schedule.

    2.  The `type` of this schedule is always `realtime`.

    3.  `time` is the cron expression that defines when the schedule should be run.  
        **Use [crontab.guru](https://crontab.guru/)!**

    4.  An event (or multiple separated by `,`) that should run at the given time.

    5.  ***(optional)*** Catchup strategy, works just like in [`simple`](#simple-schedule-simple) schedules.

    _Runs every day at 10pm, will ring a bell and wish everyone a good night._

=== "Full Example"

    ```YAML
    variables:
      lever_location: '18;4;7;world'
    events:
      bell_ring: 'folder bell_lever_toggle,bell_lever_toggle,bell_lever_toggle,bell_lever_toggle period:0.5'
      bell_lever_toggle: 'lever $lever_location$ toggle'
      notify_goodNight: 'notify &6Good night, sleep well!'
    schedules:
      sayGoodNight:
        type: realtime
        time: '0 22 * * *'
        events: bell_ring,notify_goodNight
    ```
The following special expressions were added for extended functionality or simpler usage:

| Expression              | Description                                                    | Equivalent to |
| :---------------------- | :------------------------------------------------------------- | :-----------: |
| `@reboot`               | Run at server startup, before catching up any missed schedules | _-_           |
| `@hourly`               | Run once an hour at the beginning of the hour                  | `0 * * * *`   |
| `@daily` / `@midnight`  | Run once a day at 00:00                                        | `0 0 * * *`   |
| `@weekly`               | Run once a week at 00:00 on Sunday morning                     | `0 0 * * 0`   |
| `@monthly`              | Run once a month at 00:00 of the first day of the month        | `0 0 1 * *`   |
| `@yearly` / `@annually` | Run once a year at 00:00 of 1 January                          | `0 0 1 1 *`   |

!!! warning
    Realtime schedule allows you to create schedules that run several times a day. `* * * * *` will even run every minute.  
    **Combining such a schedule with catchup strategy `ALL` can easily cause heavy load on reboot after a long shutdown.**  
    So use strategy `ALL` very cautious.
