---
icon: material/chat-question
---
#FAQ
If you have any questions please read this page first. You can easily look for your questions using the table of contents 
to the right. It's very likely that it has been already asked and answered. 
If not, feel free to ask us in the
[Discord :fontawesome-brands-discord:](https://discordapp.com/invite/rK6mfHq)

## Random daily quests

Starting the random quest must be blocked with a special tag. If there is no such tag, the conversation option should appear.
Create a few quests, each of them started with single `folder` action (they **must** be started by single action!).
Now add those actions to another `folder` action and make it `random:1`. At the end of every quest add `delay` which will reset the special blocking tag.
Now add that `folder` action to the conversation option. When the player chooses it he will start one random quest,
and the conversation option will become available after defined in `delay` objective time after completing the quest.

## The same random daily quest for every player

To do this use something called "[Schedules](Advanced/Schedules.md)".  
Run a scheduled `folder` action every day at some late hour (for example 4am).
The `folder` action should be `random:1` and contain several different `globaltag` actions.
These actions will set a specific tag. Now when the player starts the conversation and asks about the daily quest the NPC
should check (using the `globaltag` condition) which tag is currently set and give the player different quests based on that.
Of course, the scheduled folder action also needs to remove the current tag before setting a new one.

## Server wide Quests (all players work together)

There is no easy way to do this (yet). Additionally, every use case differs. Let's assume you have some sort of action
on your server where your player's need to fish 100 salmons. The quest package is only installed during the action.

Create an objective that is immediately fired upon the first interaction (this means setting the amount to one for most objectives).
That objective must be `persistent` so it restarts immediately upon completion. It also has to be `auto-once` so every
player will receive it upon joining the server.
```YAML
# fish a salmon to progress the server wide quest
gQuest: fish SALMON 1 actions:gQuestProgress auto-once persistent
```
The objective would trigger a folder action that increases a `globalpoint` counter by one and tries to run the
actions that are fired upon completion. That globalpoint counter tracks the players combined progress.
The "completion actions" must be limited by a `globalpoint` condition that checks whether the `globalpoint` counter has
reached a certain value.

=== "actions"
    ```YAML
    # 1. increase the global points 2. wait one tick for the change to process 3. attempt to run the completion actions
    gQuestProgress: folder gQuestIncrementCounter,gQuestCheckCompletion period:1 unit:ticks
    # Adds 1 to the global points
    gQuestIncrementCounter: globalpoint gQuest 1
    # Runs completion actions only when the condition is met (= the global points reached X points)
    gQuestCheckCompletion: folder gQuestNotify,gQuestOnCompletion,gDeleteObjective conditions:gQuestComplete
    # Deletes the objective from everyone that fished a salmon after the goal was met
    qDeleteObjective: "objective delete gQuest"
    ```
=== "conditions"
    ```YAML
    # Complete at one hundred collected
    gquest_complete: globalpoint gquest 100

    ```
Downsides to this approach:

* Only the player that fished the final salmon (number 100) will get the reward immediately. All other players need to fish an additional
salmon to trigger the completion logic. Therefore, a central NPC that also gives out rewards and shows the
progress is recommended.

* Since some players logged off during the action while still having the objective, a clean-up package should be installed
after the action. It will remove the objective from them - this is important as BetonQuest will complain about objectives
that are still active for a player but are not referenced in any quest package. This will happen since you have to
remove the action package after the action.

Such a package holds the original objective and clean-up objective:

=== "objectives"
    ```YAML
    # Old objective just without auto-once & persistent to make sure no one get's it automatically
    gQuest: fish SALMON 1 actions:gQuestProgress
    # Cleanup objective that is immediately completed when someone joins
    login actions:deleteOldObjective auto-once
    ```

=== "actions"
    ```YAML
    # Deletes the old objective from the current player
    deleteOldObjective: "objective delete gQuest"
    ```

## Non-Linear Objectives in Quests

If ever you're making a quest that has the player completing multiple objectives at once in order to complete the quest
itself, you may want to add the option of being able to complete the objectives in a non-linear fashion (Objective C ->
Objective A -> Objective B -> Completed). There are multiple ways of doing this but this one is probably the simplest.
Firstly, create as many objectives as you want. We are going to be working with three objectives:

=== "objectives"
    ```YAML
    Objective_A: Objective_Arguments actions:Rewards
    Objective_B: Objective_Arguments actions:Rewards
    Objective_C: Objective_Arguments actions:Rewards
    ```

Now that the player has been given these three objectives, we will also create three `objective` conditions that check
the player for these objectives:

=== "conditions"
    ```YAML
    Has_Objective_A: objective Objective_A
    Has_Objective_B: objective Objective_B
    Has_Objective_C: objective Objective_C
    ```
We will also create one `and` condition, which means a player must (or must not, depending on negation)  meet all
conditions in order for it to return as true. In this case, the player must *not* be in the process of completing these
objectives. The `!` in front of the ConditionIDs negates the arguments within the condition. Make sure you have wrapped
the condition with `'` or `"` depending on your preferences.

=== "conditions"
    ```YAML
    All_Objectives_Done: 'and !Has_Objective_A,!Has_Objective_B,!Has_Objective_C'
    ```

Finally, create the action that you wanted to use to give the quest rewards to the player. To this action, you will add
the `All_Objectives_Done` condition. This ensures that the action will not be fired unless the player has completed all
objectives.

=== "actions"
    ```YAML
    Rewards: RewardActionArguments conditions:All_Objectives_Done
    ```

Now, simply add this `Rewards` action to every one of your objectives, and you have now created a way for players to
complete a quest's objective in a non-linear fashion! You can add as many or as little objectives as you want, you just
have to add the additional objectives to the conditions.
