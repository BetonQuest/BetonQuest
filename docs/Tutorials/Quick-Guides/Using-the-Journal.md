---
icon: material/notebook-edit
---

# How to use the journal as a quest log

The journal is useful when players should be able to reread quest information after a conversation is over.
Instead of only sending chat messages, you can add journal entries when the quest state changes.

The following examples show how to use the journal for simple quest notes and for a more detailed quest overview.

=== "Simple quest log"

    This example shows a simple quest where the player has to mine 10 coal and bring it back.

    The goal is:

    - The player receives the journal when the quest starts.
    - A journal entry explains the current task.
    - When the player finishes the objective, the old entry is replaced with a new one.
    - The main page shows a short status text depending on the player's progress.
    - The journal can be refreshed whenever the main page conditions change.

    ```yaml
    journal:
      coal_started: "&0The miner asked me to collect 10 coal from the nearby cave." #(1)!
      coal_collected: "&0I collected the coal. I should return to the miner." #(2)!
      coal_finished: "&0I helped the miner and received my reward." #(3)!

    journal_main_page:
      coal_in_progress: #(4)!
        priority: 1
        text: "&6Active quest:&0 Collect 10 coal for the miner."
        conditions: "coalStarted,!coalCollected,!coalFinished"
      coal_return: #(5)!
        priority: 2
        text: "&6Active quest:&0 Return to the miner."
        conditions: "coalCollected,!coalFinished"

    objectives:
      mineCoal: "block coal_ore -10 notify actions:coalCollected" #(6)!

    actions:
      startCoalQuest: "folder addStartedTag,addStartedEntry,giveJournal,addCoalObjective,updateJournal" #(7)!
      addStartedTag: "tag add coal_started"
      addStartedEntry: "journal add coal_started" #(8)!
      giveJournal: "givejournal" #(9)!
      addCoalObjective: "objective add mineCoal"

      coalCollected: "folder addCollectedTag,replaceStartedEntry,notifyReturn,updateJournal" #(10)!
      addCollectedTag: "tag add coal_collected"
      replaceStartedEntry: "folder deleteStartedEntry,addCollectedEntry"
      deleteStartedEntry: "journal delete coal_started"
      addCollectedEntry: "journal add coal_collected"
      notifyReturn: "notify &aYou collected enough coal. Return to the miner."

      finishCoalQuest: "folder addFinishedTag,replaceCollectedEntry,rewardPlayer,updateJournal" #(11)!
      addFinishedTag: "tag add coal_finished"
      replaceCollectedEntry: "folder deleteCollectedEntry,addFinishedEntry"
      deleteCollectedEntry: "journal delete coal_collected"
      addFinishedEntry: "journal add coal_finished"
      rewardPlayer: "give reward:3"

      updateJournal: "journal update" #(12)!

    conditions:
      coalStarted: "tag coal_started"
      coalCollected: "tag coal_collected"
      coalFinished: "tag coal_finished"

    items:
      reward: "simple emerald"
    ```

    1. This entry is added when the quest starts.
    2. This entry replaces the start entry after the player mines enough coal.
    3. This entry replaces the collection entry when the quest is completed.
    4. This main page text is only shown while the player still needs to mine coal.
    5. This main page text is only shown after the coal was collected, but before the quest is finished.
    6. The objective calls the `coalCollected` action once the player has mined 10 coal ore.
    7. Starts the quest by adding the tracking tag, adding the first journal entry, giving the journal, and starting the objective.
    8. Adds the `coal_started` entry from the `journal` section to the player's journal.
    9. Gives the player the journal item. This works like the `/journal` command.
    10. Updates the quest state after the objective is complete and replaces the outdated journal entry.
    11. Finishes the quest, replaces the last active quest entry with a completed entry, and rewards the player.
    12. Refreshes the journal. This is useful when the main page depends on conditions that changed.

=== "Quest overview"

    This example uses the first journal page as a small quest overview.
    It lists two quests and colors each quest depending on its current state.

    The goal is:

    - The first journal page starts with a legend.
    - Each quest has one visible status line.
    - Red means the quest has not been started yet.
    - Orange means the quest was accepted and is currently active.
    - Green means the quest is finished.
    - Quest detail entries are added and replaced when the player progresses.

    ```yaml
    journal:
      coal_started: "&6Miner's Request\n&0Collect 10 coal from the nearby cave." #(1)!
      coal_finished: "&2Miner's Request\n&0You collected the coal and helped the miner."
      wolves_started: "&6Forest Trouble\n&0Defeat 3 wolves near the old forest path."
      wolves_finished: "&2Forest Trouble\n&0You cleared the forest path."

    journal_main_page:
      legend: #(2)!
        priority: 1
        text: "&0Quest Overview\n&4Red&0 = not started\n&6Orange&0 = accepted\n&2Green&0 = finished\n"

      coal_not_started: #(3)!
        priority: 10
        text: "&4[ ] Miner's Request - not started"
        conditions: "!coalStarted,!coalFinished"
      coal_active:
        priority: 11
        text: "&6[>] Miner's Request - collect 10 coal"
        conditions: "coalStarted,!coalFinished"
      coal_done:
        priority: 12
        text: "&2[x] Miner's Request - completed"
        conditions: "coalFinished"

      wolves_not_started: #(4)!
        priority: 20
        text: "&4[ ] Forest Trouble - not started"
        conditions: "!wolvesStarted,!wolvesFinished"
      wolves_active:
        priority: 21
        text: "&6[>] Forest Trouble - defeat 3 wolves"
        conditions: "wolvesStarted,!wolvesFinished"
      wolves_done:
        priority: 22
        text: "&2[x] Forest Trouble - completed"
        conditions: "wolvesFinished"

    objectives:
      mineCoal: "block coal_ore -10 notify actions:finishCoalQuest" #(5)!
      huntWolves: "mobkill WOLF 3 notify actions:finishWolvesQuest" #(6)!

    actions:
      openQuestJournal: "folder giveJournal,updateJournal" #(7)!
      giveJournal: "givejournal"
      updateJournal: "journal update"

      startCoalQuest: "folder addCoalStartedTag,addCoalEntry,addCoalObjective,openQuestJournal" #(8)!
      addCoalStartedTag: "tag add coal_started"
      addCoalEntry: "journal add coal_started"
      addCoalObjective: "objective add mineCoal"

      finishCoalQuest: "folder addCoalFinishedTag,replaceCoalEntry,rewardCoalQuest,updateJournal" #(9)!
      addCoalFinishedTag: "tag add coal_finished"
      replaceCoalEntry: "folder deleteCoalStartedEntry,addCoalFinishedEntry"
      deleteCoalStartedEntry: "journal delete coal_started"
      addCoalFinishedEntry: "journal add coal_finished"
      rewardCoalQuest: "give reward:2"

      startWolvesQuest: "folder addWolvesStartedTag,addWolvesEntry,addWolvesObjective,openQuestJournal" #(10)!
      addWolvesStartedTag: "tag add wolves_started"
      addWolvesEntry: "journal add wolves_started"
      addWolvesObjective: "objective add huntWolves"

      finishWolvesQuest: "folder addWolvesFinishedTag,replaceWolvesEntry,rewardWolvesQuest,updateJournal" #(11)!
      addWolvesFinishedTag: "tag add wolves_finished"
      replaceWolvesEntry: "folder deleteWolvesStartedEntry,addWolvesFinishedEntry"
      deleteWolvesStartedEntry: "journal delete wolves_started"
      addWolvesFinishedEntry: "journal add wolves_finished"
      rewardWolvesQuest: "give reward:3"

    conditions:
      coalStarted: "tag coal_started"
      coalFinished: "tag coal_finished"
      wolvesStarted: "tag wolves_started"
      wolvesFinished: "tag wolves_finished"

    items:
      reward: "simple emerald"
    ```

    1. These are the detailed quest entries that appear after a quest was accepted or completed.
    2. The legend is always visible because it has no conditions. The trailing line break separates it from the quest list.
    3. Only one `Miner's Request` status line is visible at a time because the conditions exclude each other.
    4. The second quest uses the same pattern: not started, active, and finished.
    5. The coal objective finishes the first quest and refreshes the journal afterwards.
    6. The wolf objective finishes the second quest and refreshes the journal afterwards.
    7. Gives the journal to the player and refreshes the main page.
    8. Starts the first quest, adds its orange detail entry, and updates the overview.
    9. Marks the first quest as finished, replaces the active entry with a green completed entry, and rewards the player.
    10. Starts the second quest, adds its orange detail entry, and updates the overview.
    11. Marks the second quest as finished, replaces the active entry with a green completed entry, and rewards the player.
