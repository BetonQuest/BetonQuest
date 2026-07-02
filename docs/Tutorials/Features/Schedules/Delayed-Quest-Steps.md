---
icon: material/timer-sand
tags:
  - Quest-Progress
  - Tracking
  - Schedules
---

Schedules can also unlock quest progress later. This is different from a personal delay timer: a schedule runs at a
fixed server time for everyone. That makes it useful for quests where the world changes at a known time, such as
"the bridge repair continues after the nightly maintenance".

In this tutorial, an engineer asks players to donate stone for a bridge. Players can donate during the day. At 18:00,
a schedule unlocks the next step and the engineer starts accepting final inspection reports.

<div class="grid" markdown>
!!! danger "Requirements"
    It is helpful to understand tags, points, and schedules before starting this tutorial.

    * [Tags Tutorial](../Tracking-Quest-Progress/Tags.md)
    * [Points Tutorial](../Tracking-Quest-Progress/Points.md)
    * [Schedules Documentation](../../../Documentation/Advanced/Schedules.md)

!!! example "Related Docs"
    * [Action GlobalTag](../../../Documentation/Reference/Actions-List.md#globaltag)
    * [Action GlobalPoint](../../../Documentation/Reference/Actions-List.md#globalpoint)
    * [Condition GlobalTag](../../../Documentation/Reference/Conditions-List.md#globaltag)
    * [Condition GlobalPoint](../../../Documentation/Reference/Conditions-List.md#globalpoint)
</div>

## 1. Creating the folder structure for the example quest

Create a new quest package called "_bridgeRepair_".

The file structure should look like this:

* :material-folder-open: bridgeRepair
    - :material-file: package.yml
    - :material-file: actions.yml
    - :material-file: conditions.yml
    - :material-file: items.yml
    - :material-folder-open: conversations
        - :material-file: engineer.yml

@snippet:tutorials:download-complete-files@
    ```
    /bq download BetonQuest/Quest-Tutorials ${ref} QuestPackages /Features/Schedules/Delayed-Quest-Steps/1-Setup /bridgeRepair overwrite
    ```

=== "engineer.yml"

    ``` yaml linenums="1"
    conversations:
      Engineer:
        quester: "Engineer"
        first: "intro"
        NPC_options:
          intro:
            text: "The bridge is almost ready, but I need stone donations before tonight's inspection."
            pointers: "askHow"
          explain:
            text: "Bring me cobblestone. At 18:00 I will close donations and inspect the bridge."
        player_options:
          askHow:
            text: "How can I help?"
            pointers: "explain"
    ```

=== "package.yml"

    ``` yaml linenums="1"
    npcs:
      EngineerNpc: "citizens 32" #(1)!

    npc_conversations:
      EngineerNpc: "Engineer" #(2)!
    ```

    1. Replace `32` with the Citizens ID of your Engineer NPC.
    2. This links the NPC to the `Engineer` conversation.

## 2. Track donations before the scheduled unlock

We store two kinds of progress:

* The player's own donation tag, so they cannot donate repeatedly in this simple example.
* A global point counter, so the server can track how much stone was donated in total.

=== "engineer.yml"

    ``` yaml hl_lines="4 7-18 22" linenums="1"
    conversations:
      Engineer:
        quester: "Engineer"
        first: "inspectionReady,alreadyDonated,donateStone,missingStone,intro" #(1)!
        NPC_options:
          intro:
            text: "The bridge is almost ready, but I need stone donations before tonight's inspection."
            pointers: "askHow"
            conditions: "!bridgeInspectionOpen" #(2)!
          explain:
            text: "Bring me cobblestone. At 18:00 I will close donations and inspect the bridge."
          donateStone:
            text: "If you have 16 cobblestone, I can use it for the bridge supports."
            pointers: "donate"
            conditions: "!donatedBridgeStone,!bridgeInspectionOpen,hasBridgeStoneItems" #(3)!
          missingStone:
            text: "Bring me 16 cobblestone and I can use it for the bridge supports."
            conditions: "!donatedBridgeStone,!bridgeInspectionOpen,!hasBridgeStoneItems"
          alreadyDonated:
            text: "Your stone is already counted. Current bridge stock: %globalpoint.bridgeStone.amount%." #(4)!
            conditions: "donatedBridgeStone,!bridgeInspectionOpen"
          inspectionReady:
            text: "Donations are closed. The inspection is ready now."
            conditions: "bridgeInspectionOpen" #(5)!
        player_options:
          askHow:
            text: "How can I help?"
            pointers: "explain"
          donate:
            text: "Take this cobblestone."
            conditions: "hasBridgeStoneItems"
            actions: "donateBridgeStone" #(6)!
    ```

    1. The scheduled unlock option is checked first. Donation and missing-item responses are checked before the intro.
    2. The normal introduction is only available before inspection opens.
    3. This keeps the same player from donating twice and requires the 16 cobblestone before the donate option appears.
    4. The global point placeholder shows the shared server donation total.
    5. This option appears after the schedule adds the global tag.
    6. This player action is still protected by the item condition, then takes items and updates progress.

=== "actions.yml"

    ``` yaml linenums="1"
    actions:
      donateBridgeStone: "folder takeBridgeStone,addBridgeStonePoint,addDonatedBridgeStone,notifyDonation" #(1)!
      takeBridgeStone: "take bridgeStone:16 abort" #(2)!
      addBridgeStonePoint: "globalpoint bridgeStone 16 action:add" #(3)!
      addDonatedBridgeStone: "tag add donatedBridgeStone" #(4)!
      notifyDonation: "notify &aYou donated 16 cobblestone. Server total: %globalpoint.bridgeStone.amount%. io:chat"
    ```

    1. The donation is split into small actions so each part is easy to reuse or change.
    2. Takes the required cobblestone from the player. `abort` prevents partial item removal if something changed.
    3. Adds to the server-wide donation counter.
    4. Stores that this player already donated.

=== "conditions.yml"

    ``` yaml linenums="1"
    conditions:
      donatedBridgeStone: "tag donatedBridgeStone" #(1)!
      hasBridgeStoneItems: "item bridgeStone:16" #(2)!
      bridgeInspectionOpen: "globaltag bridgeInspectionOpen" #(3)!
    ```

    1. A normal tag stores one player's personal donation state.
    2. Checks whether the player currently has 16 cobblestone to donate.
    3. A global tag stores the server-wide inspection phase.

=== "items.yml"

    ``` yaml linenums="1"
    items:
      bridgeStone: "simple cobblestone"
    ```

The global point category `bridgeStone` is shared by the entire server. The player tag `donatedBridgeStone` belongs
to one player. This separation is important: schedules can change server-wide progress, while conversations still use
player progress to decide what one player has already done.

@snippet:tutorials:download-solution@
    ```
    /bq download BetonQuest/Quest-Tutorials ${ref} QuestPackages /Features/Schedules/Delayed-Quest-Steps/2-DonationProgress /bridgeRepair overwrite
    ```

## 3. Unlock the next step at a fixed time

Now add the schedule. At 18:00, the server sets a global tag called `bridgeInspectionOpen`. After that, the engineer
uses a different conversation option.

=== "package.yml"

    ``` yaml hl_lines="7-11" linenums="1"
    npcs:
      EngineerNpc: "citizens 32"

    npc_conversations:
      EngineerNpc: "Engineer"

    schedules:
      openBridgeInspection: #(1)!
        type: realtime-daily #(2)!
        time: '18:00' #(3)!
        actions: openBridgeInspection #(4)!
    ```

    1. This schedule controls the global unlock.
    2. The schedule runs once per day.
    3. At 18:00 server time, donations close and inspection opens.
    4. The action is executed player independent.

=== "actions.yml"

    ``` yaml hl_lines="6-8" linenums="1"
    actions:
      donateBridgeStone: "folder takeBridgeStone,addBridgeStonePoint,addDonatedBridgeStone,notifyDonation"
      takeBridgeStone: "take bridgeStone:16 abort"
      addBridgeStonePoint: "globalpoint bridgeStone 16 action:add"
      addDonatedBridgeStone: "tag add donatedBridgeStone"
      notifyDonation: "notify &aYou donated 16 cobblestone. Server total: %globalpoint.bridgeStone.amount%. io:chat"
      openBridgeInspection: "folder addBridgeInspectionOpen,announceBridgeInspection" #(1)!
      addBridgeInspectionOpen: "globaltag add bridgeInspectionOpen" #(2)!
      announceBridgeInspection: "notifyall &eThe bridge inspection is now open. Talk to the Engineer. io:chat" #(3)!
    ```

    1. The schedule calls this folder so the unlock and announcement happen together.
    2. This global tag changes the NPC dialogue for everyone.
    3. `notifyall` tells online players that the new phase is available.

=== "engineer.yml"

    ``` yaml hl_lines="16-19 25-27" linenums="1"
    conversations:
      Engineer:
        quester: "Engineer"
        first: "inspectionReady,alreadyDonated,donateStone,missingStone,intro" #(1)!
        NPC_options:
          intro:
            text: "The bridge is almost ready, but I need stone donations before tonight's inspection."
            pointers: "askHow"
            conditions: "!bridgeInspectionOpen"
          explain:
            text: "Bring me cobblestone. At 18:00 I will close donations and inspect the bridge."
          donateStone:
            text: "If you have 16 cobblestone, I can use it for the bridge supports."
            pointers: "donate"
            conditions: "!donatedBridgeStone,!bridgeInspectionOpen,hasBridgeStoneItems"
          missingStone:
            text: "Bring me 16 cobblestone and I can use it for the bridge supports."
            conditions: "!donatedBridgeStone,!bridgeInspectionOpen,!hasBridgeStoneItems"
          alreadyDonated:
            text: "Your stone is already counted. Current bridge stock: %globalpoint.bridgeStone.amount%."
            conditions: "donatedBridgeStone,!bridgeInspectionOpen"
          inspectionReady:
            text: "Donations are closed. We collected %globalpoint.bridgeStone.amount% cobblestone. Can you inspect the bridge?" #(2)!
            conditions: "bridgeInspectionOpen,!bridgeInspectionDone" #(3)!
            pointers: "inspect"
          inspected:
            text: "Thanks for checking the bridge. We can open it safely."
            conditions: "bridgeInspectionDone" #(4)!
        player_options:
          askHow:
            text: "How can I help?"
            pointers: "explain"
          donate:
            text: "Take this cobblestone."
            conditions: "hasBridgeStoneItems"
            actions: "donateBridgeStone"
          inspect:
            text: "The bridge looks stable."
            actions: "finishBridgeInspection" #(5)!
    ```

    1. `inspectionReady` stays first because it should win after the scheduled unlock.
    2. The NPC can read the final global donation total.
    3. The inspection can be done only after the schedule opened it and before this player completed it.
    4. This personal tag stores that the player already finished the inspection step.
    5. This action rewards the player and saves their inspection progress.

=== "actions.yml"

    ``` yaml hl_lines="9-11" linenums="1"
    actions:
      donateBridgeStone: "folder takeBridgeStone,addBridgeStonePoint,addDonatedBridgeStone,notifyDonation"
      takeBridgeStone: "take bridgeStone:16 abort"
      addBridgeStonePoint: "globalpoint bridgeStone 16 action:add"
      addDonatedBridgeStone: "tag add donatedBridgeStone"
      notifyDonation: "notify &aYou donated 16 cobblestone. Server total: %globalpoint.bridgeStone.amount%. io:chat"
      openBridgeInspection: "folder addBridgeInspectionOpen,announceBridgeInspection"
      addBridgeInspectionOpen: "globaltag add bridgeInspectionOpen"
      announceBridgeInspection: "notifyall &eThe bridge inspection is now open. Talk to the Engineer. io:chat"
      finishBridgeInspection: "folder addBridgeInspectionDone,rewardInspector" #(1)!
      addBridgeInspectionDone: "tag add bridgeInspectionDone" #(2)!
      rewardInspector: "give inspectionReward:2" #(3)!
    ```

    1. The finish folder groups progress and reward.
    2. This is a player tag because each player can finish the inspection separately.
    3. Gives the reward item defined in `items.yml`.

=== "conditions.yml"

    ``` yaml hl_lines="4" linenums="1"
    conditions:
      donatedBridgeStone: "tag donatedBridgeStone"
      hasBridgeStoneItems: "item bridgeStone:16"
      bridgeInspectionOpen: "globaltag bridgeInspectionOpen"
      bridgeInspectionDone: "tag bridgeInspectionDone" #(1)!
    ```

    1. This condition checks the player's personal inspection completion.

=== "items.yml"

    ``` yaml hl_lines="3" linenums="1"
    items:
      bridgeStone: "simple cobblestone"
      inspectionReward: "simple emerald" #(1)!
    ```

    1. The reward action gives two of this item with `give inspectionReward:2`.

This pattern is useful when a quest step should unlock at a known time, not after an individual player's timer. If
you need a personal timer like "24 hours after this player finished", use a delay objective instead.

@snippet:tutorials:download-this-part@
    ```
    /bq download BetonQuest/Quest-Tutorials ${ref} QuestPackages /Features/Schedules/Delayed-Quest-Steps/3-FullExample /bridgeRepair overwrite
    ```
