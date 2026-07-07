---
icon: material/account-voice
---

# How to bind conversations to NPCs

Binding a conversation to an NPC makes the NPC start that conversation when a player interacts with it.
The binding has two parts: first define the NPC, then assign a conversation to that NPC.

The following example uses Citizens NPCs.
It creates two NPC bindings and assigns one conversation to each NPC.

The goal is:

- Define BetonQuest NPC IDs for existing Citizens NPCs.
- Assign conversations to those NPC IDs.
- Create simple conversations that start on right-click.
- Use conditions to show different NPC text after the player accepted a quest.
- Reuse the same conversation from more than one NPC when needed.

```yaml
npcs:
  foresterNpc: "citizens 12" #(1)!
  guardNpc: "citizens 18" #(2)!
  secondGuardNpc: "citizens 19" #(3)!

npc_conversations:
  foresterNpc: "ForesterConversation" #(4)!
  guardNpc: "GuardConversation" #(5)!
  secondGuardNpc: "GuardConversation" #(6)!

conversations:
  ForesterConversation:
    quester: "Forester"
    first: "firstMeeting,questReminder" #(7)!
    NPC_options:
      firstMeeting:
        text: "Hello %player%! Could you gather 5 oak logs for me?"
        conditions: "!logQuestStarted"
        pointers: "acceptLogQuest,declineLogQuest"
      questReminder:
        text: "You already accepted my request. Please bring me 5 oak logs."
        conditions: "logQuestStarted,!logQuestDone"
    player_options:
      acceptLogQuest:
        text: "I will gather the logs."
        actions: "startLogQuest"
      declineLogQuest:
        text: "Not right now."

  GuardConversation:
    quester: "Town Guard"
    first: "greeting"
    NPC_options:
      greeting:
        text: "Stay safe. The forest path is dangerous today."
        pointers: "askForest"
    player_options:
      askForest:
        text: "What happened near the forest?"
        actions: "warnAboutForest"

objectives:
  gatherLogs: "block oak_log -5 notify actions:finishLogQuest" #(8)!

actions:
  startLogQuest: "folder addLogQuestStarted,addGatherLogsObjective,notifyStarted"
  addLogQuestStarted: "tag add log_quest_started"
  addGatherLogsObjective: "objective add gatherLogs"
  notifyStarted: "notify &aQuest started. Gather 5 oak logs."

  finishLogQuest: "folder addLogQuestDone,rewardPlayer,notifyDone"
  addLogQuestDone: "tag add log_quest_done"
  rewardPlayer: "give reward:3"
  notifyDone: "notify &aYou gathered enough logs. Return to the forester."

  warnAboutForest: "notify &eThe guard points toward the old forest path."

conditions:
  logQuestStarted: "tag log_quest_started" #(9)!
  logQuestDone: "tag log_quest_done" #(10)!

items:
  reward: "simple EMERALD"
```

1. `foresterNpc` is the BetonQuest NPC ID. `citizens 12` points to the Citizens NPC with ID `12`.
2. `guardNpc` is another BetonQuest NPC ID for a different Citizens NPC.
3. A second guard can point to another Citizens NPC.
4. This assigns `ForesterConversation` to `foresterNpc`.
5. This assigns `GuardConversation` to `guardNpc`.
6. Multiple NPCs can use the same conversation.
7. BetonQuest checks `firstMeeting` first. If its conditions are false, it tries `questReminder`.
8. This objective is started when the player accepts the forester's quest.
9. This condition controls whether the first quest offer is still shown.
10. This condition can be used to hide reminder text after the quest is done.

To find a Citizens NPC ID, select the NPC with `/npc select` and run `/npc id`.
After editing the package, reload BetonQuest and right-click the NPC.
By default, conversations start on right-click.
