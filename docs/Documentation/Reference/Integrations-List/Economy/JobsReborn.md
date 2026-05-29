# [JobsReborn](https://www.spigotmc.org/resources/4216/)

## Actions

### `NuJobs_AddExp`

__Context__: @snippet:action-meta:online-offline@  
__Syntax__: `nujobs_addexp <jobname> <exp>`  
__Description__: Gives the player experience.

### `NuJobs_AddLevel`

__Context__: @snippet:action-meta:online-offline@  
__Syntax__: `nujobs_addlevel <jobname> <amount>`  
__Description__: Increases the player level by amount.

### `NuJobs_DelLevel`

__Context__: @snippet:action-meta:online-offline@  
__Syntax__: `nujobs_dellevel <jobname> <amount>`  
__Description__: Decreases the players level by amount.

### `NuJobs_JoinJob`

__Context__: @snippet:action-meta:online-offline@  
__Syntax__: `nujobs_joinjob <jobname>`  
__Description__: Joins the player to job.

### `NuJobs_LeaveJob`

__Context__: @snippet:action-meta:online-offline@  
__Syntax__: `nujobs_leavejob <jobname>`  
__Description__: Removes the player from job.

### `NuJobs_SetLevel`

__Context__: @snippet:action-meta:online-offline@  
__Syntax__: `nujobs_setlevel <jobname> <level>`  
__Description__: Set the player to level.

## Objectives

### `NuJobs_JoinJob`

__Context__: @snippet:objective-meta:online@  
__Syntax__: `nujobs_joinjob <jobname>`  
__Description__: Triggers when player joins job.

### `NuJobs_LeaveJob`

__Context__: @snippet:objective-meta:online@  
__Syntax__: `nujobs_leavejob <jobname>`  
__Description__: Triggers when player leaves job.

This is not triggered by `/jobs leaveall`

### `NuJobs_LevelUp`

__Context__: @snippet:objective-meta:online@  
__Syntax__: `nujobs_levelup <jobname>`  
__Description__: Triggers when player levels up.

### `NuJobs_Payment`

__Context__: @snippet:objective-meta:online@  
__Syntax__: `nujobs_payment <amount>`  
__Description__: Triggers when the player makes the specified amount of money from jobs.

You can use the `notify` keyword to display a message each time the player advances the objective,
optionally with the notification interval after a colon.

This objective has three properties: `amount`, `left` and `total`. `amount` is the amount of money already received,
`left` is the amount of money still needed to receive and `total` is the amount of money initially required.

## Conditions

### `NuJobs_CanLevel`

__Context__: @snippet:condition-meta:online-offline@  
__Syntax__: `nujobs_canlevel <jobname>`  
__Description__: Whether the player can level up.

### `NuJobs_HasJob`

__Context__: @snippet:condition-meta:online-offline@  
__Syntax__: `nujobs_hasjob <jobname>`  
__Description__: Whether the player has this job

```YAML title="Example"
conditions:
  hasWoodcutter: "nujobs_hasjob Woodcutter"
```

### `NuJobs_JobFull`

__Context__: @snippet:condition-meta:online-offline@  
__Syntax__: `nujobs_jobfull <jobname>`  
__Description__: Whether the job is at the maximum slots

### `NuJobs_JobLevel`

__Context__: @snippet:condition-meta:online-offline@  
__Syntax__: `nujobs_joblevel <jobname> <min> <max>`  
__Description__: Wether the player has this job, and at a level equal to or between the min/max

```YAML title="Example"
conditions:
  woodcutterLevel5: "nujobs_joblevel Woodcutter 5 10"
```
