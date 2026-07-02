---
icon: octicons/clock-16
tags:
  - Schedules
  - Time
  - Automation
---

Schedules let BetonQuest run actions at fixed real-world times. They are useful when the server should change quest
progress without a player starting the action manually.

Use schedules when:

- a quest should reset at the same time for everyone,
- a server-wide phase should open or close automatically,
- an event should start and end at fixed times,
- players should see the same world state after a scheduled change.

Schedules are different from personal timers. A schedule runs from the server, not from one player. That means the
actions in a schedule must either be player independent, like `globaltag`, `globalpoint`, `notifyall`, or be wrapped
with `runforall` when they should affect online players.


## Schedule Tutorials

<div class="grid cards" markdown>

 -   :material-calendar-refresh:{ .lg .middle } __Daily Repetition__

     ---

     Reset player progress at a fixed server time so daily quests become available again.

     [:octicons-arrow-right-24: Daily Repetition Tutorial](./Daily-Repetition.md)

 -   :material-timer-sand:{ .lg .middle } __Delayed Quest Steps__

     ---

     Unlock the next step for everyone when a scheduled server time is reached.

     [:octicons-arrow-right-24: Delayed Quest Steps Tutorial](./Delayed-Quest-Steps.md)

 -   :material-calendar-clock:{ .lg .middle } __Limited Time Events__

     ---

     Start and end temporary event progress with schedules.

     [:octicons-arrow-right-24: Limited Time Events Tutorial](./Limited-Time-Events.md)

</div>
