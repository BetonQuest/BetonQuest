---
icon: material/chat-question
---
#FAQ
If you have any questions please read this page first. You can easily look for your questions using the table of contents 
to the right. It's very likely that it has been already asked and answered. 
If not, feel free to ask us in the
[Discord :fontawesome-brands-discord:](https://discordapp.com/invite/rK6mfHq)


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
