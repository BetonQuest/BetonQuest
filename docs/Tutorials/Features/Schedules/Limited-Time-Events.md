---
icon: material/calendar-clock
tags:
  - Quest-Progress
  - Tracking
  - Schedules
---

Schedules are a good fit for events that start and end at fixed times. In this tutorial, we create a short server-wide
meteor shower. Players can collect meteor fragments only while the event is active. When the event ends, progress is
cleaned up and the NPC switches back to normal dialogue.

<div class="grid" markdown>
!!! danger "Requirements"
    You should know the basics of objectives, points, global tags, and schedules.

    * [Objectives Tutorial](../../../Tutorials/Getting-Started/Basics/Objectives.md)
    * [Points Tutorial](../Tracking-Quest-Progress/Points.md)
    * [Schedules Documentation](../../../Documentation/Advanced/Schedules.md)

!!! example "Related Docs"
    * [Action NotifyAll](../../../Documentation/Reference/Actions-List.md#notifyall)
    * [Action RunForAll](../../../Documentation/Reference/Actions-List.md#runforall)
    * [Action GlobalTag](../../../Documentation/Reference/Actions-List.md#globaltag)
    * [Action DeletePoint](../../../Documentation/Reference/Actions-List.md#deletepoint)
</div>

## 1. Creating the folder structure for the example quest

Create a new quest package called "_meteorShower_".

The file structure should look like this:

* :material-folder-open: meteorShower
    - :material-file: package.yml
    - :material-file: actions.yml
    - :material-file: objectives.yml
    - :material-file: conditions.yml
    - :material-file: items.yml
    - :material-folder-open: conversations
        - :material-file: astronomer.yml

@snippet:tutorials:download-complete-files@
    ```
    /bq download BetonQuest/Quest-Tutorials ${ref} QuestPackages /Features/Schedules/Limited-Time-Events/1-Setup /meteorShower overwrite
    ```

=== "astronomer.yml"

    ``` yaml linenums="1"
    conversations:
      Astronomer:
        quester: "Astronomer"
        first: "waiting"
        NPC_options:
          waiting:
            text: "The sky is calm right now. Come back when the meteor shower begins."
    ```

=== "package.yml"

    ``` yaml linenums="1"
    npcs:
      AstronomerNpc: "citizens 33" #(1)!

    npc_conversations:
      AstronomerNpc: "Astronomer" #(2)!
    ```

    1. Replace `33` with the Citizens ID of your Astronomer NPC.
    2. This connects the NPC to the `Astronomer` conversation.

## 2. Start the event with a schedule

The first schedule starts the event at 20:00. It sets a global tag and uses `runforall` to start the collection
objective for every online player.

=== "package.yml"

    ``` yaml hl_lines="7-11" linenums="1"
    npcs:
      AstronomerNpc: "citizens 33"

    npc_conversations:
      AstronomerNpc: "Astronomer"

    schedules:
      startMeteorShower: #(1)!
        type: realtime-daily #(2)!
        time: '20:00' #(3)!
        actions: startMeteorShower #(4)!
    ```

    1. This is the schedule that starts the temporary event.
    2. `realtime-daily` uses the real server clock.
    3. The event starts at 20:00 server time.
    4. The named action is executed without a specific player.

=== "actions.yml"

    ``` yaml linenums="1"
    actions:
      startMeteorShower: "folder addMeteorEventActive,startMeteorCollectors,announceMeteorStart" #(1)!
      addMeteorEventActive: "globaltag add meteorEventActive" #(2)!
      startMeteorCollectors: "runforall where:!meteorRewardClaimed actions:resetMeteorFragments,addMeteorObjective" #(3)!
      resetMeteorFragments: "point meteorFragments 0 action:set" #(4)!
      addMeteorObjective: "objective add collectMeteorFragments" #(5)!
      addMeteorFragment: "folder addMeteorPoint,notifyMeteorProgress,removeMeteorObjectiveOnComplete" #(6)!
      addMeteorPoint: "point meteorFragments 1 action:add" #(7)!
      notifyMeteorProgress: "notify &bMeteor fragments: %point.meteorFragments.amount%&8/&b5 io:chat"
      removeMeteorObjectiveOnComplete: "objective remove collectMeteorFragments conditions:hasFiveMeteorFragments" #(8)!
      announceMeteorStart: "notifyall &bA meteor shower has started. Collect five amethyst shards! io:chat" #(9)!
    ```

    1. The schedule calls one folder that starts every part of the event.
    2. The global tag marks the server-wide event as active.
    3. `runforall` switches from schedule context to each online player that has not already claimed a reward.
    4. Each player's event progress starts at zero.
    5. Each online player receives the collection objective.
    6. This folder runs whenever one fragment is picked up.
    7. The point stores one player's temporary event progress.
    8. The objective is removed after five fragments so extra pickups do not keep increasing progress.
    9. `notifyall` is safe to call from a schedule because it is player independent.

=== "objectives.yml"

    ``` yaml linenums="1"
    objectives:
      collectMeteorFragments: "pickup meteorFragment amount:1 notify actions:addMeteorFragment persistent" #(1)!
    ```

    1. The objective completes once per picked-up fragment. `persistent` restarts it until the action removes it at five fragments.

=== "conditions.yml"

    ``` yaml linenums="1"
    conditions:
      meteorEventActive: "globaltag meteorEventActive" #(1)!
      hasFiveMeteorFragments: "point meteorFragments 5" #(2)!
      hasMeteorFragmentItems: "item meteorFragment:5" #(3)!
      canClaimMeteorReward: "and hasFiveMeteorFragments,hasMeteorFragmentItems" #(4)!
      meteorRewardClaimed: "tag meteorRewardClaimed" #(5)!
    ```

    1. Checks whether the event is currently active for the whole server.
    2. Checks one player's temporary fragment counter.
    3. Checks whether the player still has five fragments in their inventory.
    4. Requires both tracked progress and the actual items for the hand-in.
    5. Prevents the same player from claiming the reward twice during one event run.

=== "items.yml"

    ``` yaml linenums="1"
    items:
      meteorFragment: "simple amethyst_shard"
      meteorReward: "simple diamond"
    ```

The schedule itself has no player, so it cannot directly add a normal player objective. `runforall` solves that by
running `resetMeteorFragments` and `addMeteorObjective` once for each online player. The global tag
`meteorEventActive` marks the server-wide event state.

@snippet:tutorials:download-solution@
    ```
    /bq download BetonQuest/Quest-Tutorials ${ref} QuestPackages /Features/Schedules/Limited-Time-Events/2-EventStart /meteorShower overwrite
    ```

## 3. Let players claim rewards while the event is active

Now the Astronomer checks whether the event is active and whether the player has collected enough fragments.

=== "astronomer.yml"

    ``` yaml hl_lines="4 7-18 20-23" linenums="1"
    conversations:
      Astronomer:
        quester: "Astronomer"
        first: "claimReward,missingFragments,collecting,alreadyClaimed,waiting" #(1)!
        NPC_options:
          waiting:
            text: "The sky is calm right now. Come back when the meteor shower begins."
            conditions: "!meteorEventActive" #(2)!
          collecting:
            text: "The shower is active. You have %point.meteorFragments.amount% of 5 fragments." #(3)!
            conditions: "meteorEventActive,!canClaimMeteorReward,!meteorRewardClaimed"
          missingFragments:
            text: "You collected enough fragments, but I need five of them in your inventory for the research."
            conditions: "meteorEventActive,hasFiveMeteorFragments,!hasMeteorFragmentItems,!meteorRewardClaimed"
          claimReward:
            text: "Five fragments! That is enough for my research. Take this diamond."
            conditions: "meteorEventActive,canClaimMeteorReward,!meteorRewardClaimed" #(4)!
            actions: "claimMeteorReward" #(5)!
          alreadyClaimed:
            text: "You already claimed your meteor reward for this shower."
            conditions: "meteorRewardClaimed" #(6)!
        player_options:
          thanks:
            text: "Thanks."
    ```

    1. Reward claiming is checked first, then the missing-item response, then the normal progress message.
    2. The waiting message is only shown while the global event tag is missing.
    3. The point placeholder shows this player's current fragment count.
    4. The reward is available only during the event, after enough pickups, while the player still has the fragments.
    5. This gives the reward and saves the claimed state.
    6. This catches players who already claimed their reward.

=== "actions.yml"

    ``` yaml hl_lines="11-14" linenums="1"
    actions:
      startMeteorShower: "folder addMeteorEventActive,startMeteorCollectors,announceMeteorStart"
      addMeteorEventActive: "globaltag add meteorEventActive"
      startMeteorCollectors: "runforall where:!meteorRewardClaimed actions:resetMeteorFragments,addMeteorObjective"
      resetMeteorFragments: "point meteorFragments 0 action:set"
      addMeteorObjective: "objective add collectMeteorFragments"
      addMeteorFragment: "folder addMeteorPoint,notifyMeteorProgress,removeMeteorObjectiveOnComplete"
      addMeteorPoint: "point meteorFragments 1 action:add"
      notifyMeteorProgress: "notify &bMeteor fragments: %point.meteorFragments.amount%&8/&b5 io:chat"
      removeMeteorObjectiveOnComplete: "objective remove collectMeteorFragments conditions:hasFiveMeteorFragments"
      announceMeteorStart: "notifyall &bA meteor shower has started. Collect five amethyst shards! io:chat"
      claimMeteorReward: "folder takeMeteorFragments,giveMeteorReward,addMeteorRewardClaimed,removeMeteorObjective" #(1)!
      takeMeteorFragments: "take meteorFragment:5 abort" #(2)!
      giveMeteorReward: "give meteorReward:1" #(3)!
      addMeteorRewardClaimed: "tag add meteorRewardClaimed" #(4)!
      removeMeteorObjective: "objective remove collectMeteorFragments" #(5)!
    ```

    1. The reward folder groups item hand-in, reward, and cleanup for one player.
    2. Takes the five fragments. `abort` prevents partial removal if something changed.
    3. Gives the configured reward item.
    4. Saves that this player already claimed the event reward.
    5. Removes the objective from this player after they are done.

Players now have temporary progress during the event. The point category stores how many fragments they collected,
and the tag `meteorRewardClaimed` prevents repeated rewards.

## 4. End the event and clean up progress

Add a second schedule at 20:30. It removes the event tag, removes the temporary objective, deletes temporary points,
and clears the reward tag so the next event can be played again.

=== "package.yml"

    ``` yaml hl_lines="12-16" linenums="1"
    npcs:
      AstronomerNpc: "citizens 33"

    npc_conversations:
      AstronomerNpc: "Astronomer"

    schedules:
      startMeteorShower:
        type: realtime-daily
        time: '20:00'
        actions: startMeteorShower
      endMeteorShower: #(1)!
        type: realtime-daily
        time: '20:30' #(2)!
        actions: endMeteorShower #(3)!
    ```

    1. This second schedule closes the temporary event.
    2. The event ends thirty minutes after it starts.
    3. The cleanup action runs player independent.

=== "actions.yml"

    ``` yaml hl_lines="15-19" linenums="1"
    actions:
      startMeteorShower: "folder addMeteorEventActive,startMeteorCollectors,announceMeteorStart"
      addMeteorEventActive: "globaltag add meteorEventActive"
      startMeteorCollectors: "runforall where:!meteorRewardClaimed actions:resetMeteorFragments,addMeteorObjective"
      resetMeteorFragments: "point meteorFragments 0 action:set"
      addMeteorObjective: "objective add collectMeteorFragments"
      addMeteorFragment: "folder addMeteorPoint,notifyMeteorProgress,removeMeteorObjectiveOnComplete"
      addMeteorPoint: "point meteorFragments 1 action:add"
      notifyMeteorProgress: "notify &bMeteor fragments: %point.meteorFragments.amount%&8/&b5 io:chat"
      removeMeteorObjectiveOnComplete: "objective remove collectMeteorFragments conditions:hasFiveMeteorFragments"
      announceMeteorStart: "notifyall &bA meteor shower has started. Collect five amethyst shards! io:chat"
      claimMeteorReward: "folder takeMeteorFragments,giveMeteorReward,addMeteorRewardClaimed,removeMeteorObjective"
      takeMeteorFragments: "take meteorFragment:5 abort"
      giveMeteorReward: "give meteorReward:1"
      addMeteorRewardClaimed: "tag add meteorRewardClaimed"
      removeMeteorObjective: "objective remove collectMeteorFragments"
      endMeteorShower: "folder deleteMeteorEventActive,removeMeteorObjectives,deleteMeteorPoints,deleteMeteorRewardTags,announceMeteorEnd" #(1)!
      deleteMeteorEventActive: "globaltag delete meteorEventActive" #(2)!
      removeMeteorObjectives: "objective remove collectMeteorFragments" #(3)!
      deleteMeteorPoints: "deletepoint meteorFragments" #(4)!
      deleteMeteorRewardTags: "tag delete meteorRewardClaimed" #(5)!
      announceMeteorEnd: "notifyall &7The meteor shower has ended. io:chat" #(6)!
    ```

    1. The end folder keeps the cleanup in one place.
    2. Removes the global event state.
    3. Because this runs from a schedule, it removes the objective for all players.
    4. Deletes the temporary event point category for all players.
    5. Clears reward claim tags so the next event can be played again.
    6. Announces the end to online players.

Because `endMeteorShower` is called by a schedule, `objective remove`, `deletepoint`, and `tag delete` clean up all
players, including offline players. This keeps the next event run clean.

@snippet:tutorials:download-this-part@
    ```
    /bq download BetonQuest/Quest-Tutorials ${ref} QuestPackages /Features/Schedules/Limited-Time-Events/3-FullExample /meteorShower overwrite
    ```

You have now created a limited-time event. The same structure works for weekend dungeons, seasonal gathering windows,
server-wide races, double reward hours, or world-state changes that should start and stop automatically.
