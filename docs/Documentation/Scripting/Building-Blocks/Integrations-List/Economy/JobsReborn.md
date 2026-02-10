# JobsReborn[](https://www.spigotmc.org/resources/4216/)

### Conditions

#### Can Level up: `nujobs_canlevel {jobname}`

Returns true if the player can level up

#### Has Job: `nujobs_hasjob {jobname}`

Returns true if the player has this job

```YAML title="Example"
conditions:
  hasWoodcutter: "nujobs_hasjob Woodcutter"
```

#### Job Full: `nujobs_jobfull {jobname}`

Returns true if the job is at the maximum slots

#### Job Level: `nujobs_joblevel {jobname} {min} {max}`

Returns true if the player has this job, and at a level equal to or between the min/max

```YAML title="Example"
conditions:
  woodcutterLevel5: "nujobs_joblevel Woodcutter 5 10"
```

### Actions

#### Add Jobs Experience: `nujobs_addexp {jobname} {exp}`

Gives the player experience

#### Increase Jobs Level: `nujobs_addlevel {jobname} {amount}`

Increases the player level by amount.

#### Decrease Jobs Level: `nujobs_dellevel {jobname} {amount}`

Decreases the players level by amount.

#### Join Jobs Job Action: `nujobs_joinjob {jobname}`

Joins the player to job.

#### Leave Jobs Job Action: `nujobs_leavejob {jobname}`

Removes the player from job.

#### Set Jobs Level: `nujobs_setlevel {jobname} {level}`

Set the player to level.

### Objectives

#### Join Jobs Job Objective: `nujobs_joinjob {jobname}`

Triggers when player joins job.

#### Leave Jobs Job Objective: `nujobs_leavejob {jobname}`

Triggers when player leaves job.

!!! notice
    This is not triggered by '/jobs leaveall'

#### Jobs Job Levelup: `nujobs_levelup {jobname}`

Triggers when player levels up.

#### Jobs Job Payment: `nujobs_payment {amount}`

Triggers when player makes {amount} of money from jobs. You can use the `notify` keyword to display a message each time
the player advances the objective, optionally with the notification interval after a colon.

This objective has three properties: `amount`, `left` and `total`. `amount` is the amount of money already received,
`left` is the amount of money still needed to receive and `total` is the amount of money initially required.
