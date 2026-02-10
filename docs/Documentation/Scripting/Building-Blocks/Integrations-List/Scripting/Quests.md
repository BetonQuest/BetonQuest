# [Quests](https://www.spigotmc.org/resources/3711/)

Quests is another questing plugin, which offers very simple creation of quests.
If you don't want to spend a lot of time to write advanced quests in BetonQuest but you need a specific thing from
this plugin you can use Custom Action Reward or Custom Condition Requirement.
Alternatively, if you have a lot of quests written in Quests, but want to integrate them with the conversation system,
you can use `quest` action and `quest` condition.

### Condition Requirement (Quests)

When adding requirements to a quest, choose "Custom requirement" and then select "BetonQuest condition".
Now specify condition's name and it's package (like `package>conditionName`). Quests will check BetonQuest condition when starting the quest.

### Action Reward (Quests)

When adding rewards to a quest or a stage, choose "Custom reward" and then select "BetonQuest action".
Now specify action's name and it's package (like `package>actionName`). Quests will fire BetonQuest action when this reward will run.

### Conditions

#### Quest condition: `quest`

This condition is met when the player has completed the specified quest. The first and only argument is the name of the quest.
If it contains spaces you need to quote it.

```YAML title="Example"
conditions:
  completedQuest: "quest stone_miner"
```

### Actions

#### Quest: `quest`

This action will start the quest for the player.
The first argument must be the name of the quest, as defined in `name` option in the quest.
You can optionally add `check-requirements` argument if you want the action to respect this quest's requirements
(otherwise the quest will be forced to be started).

```YAML title="Example"
actions:
  startStoneMiner: "quest stone_miner check-requirements"
```
