# [TrainCarts](https://www.spigotmc.org/resources/39592/)

TrainCarts is a plugin that allows you to create trains with advanced features.

### Conditions

#### TrainCarts ride condition: `traincartsride`

Checks if the player is riding a specific named train.

```YAML title="Example"
conditions:
  onTrain: "traincartsride train1"
```

### Objectives

#### TrainCarts location objective: `traincartslocation`

This objective requires the player to be at a specific location while sitting in a train.
It works similarly to the location objective, but the player must be in a TrainCarts train to complete it.

| Parameter  | Syntax       | Default Value          | Explanation                                                                               |
|------------|--------------|------------------------|-------------------------------------------------------------------------------------------|
| _location_ | x;y;z;world  | :octicons-x-circle-16: | The Location the player has to pass whiles sitting in the train.                          |
| _range_    | range:double | 1                      | The optional range around the location where the player must be.                          |
| _entry_    | entry        | Disabled               | The player must enter (go from outside to inside) the location to complete the objective. |
| _exit_     | exit         | Disabled               | The player must exit (go from inside to outside) the location to complete the objective.  |
| _name_     | name:Train1  | :octicons-x-circle-16: | The optional Name of the Train.                                                           |

```YAML title="Example"
objectives:
  checkpoint1: "traincartslocation 100;60;100;world"
  train1: "traincartslocation name:Train1 100;60;100;world range:2"
  enter: "traincartslocation 100;60;100;world entry range:2"
```

#### TrainCarts ride objective: `traincartsride`

This objective requires the player to ride a train for a specific time.
The time starts after the player enters the train and stops when the player exits the train.
The conditions are checked every time the player enters or leaves the train or completes the objective.
If the conditions are not met, the time will not be counted.

| Parameter | Syntax      | Default Value          | Explanation                                                                      |
|-----------|-------------|------------------------|----------------------------------------------------------------------------------|
| _name_    | name:Train1 | :octicons-x-circle-16: | The optional Name of the Train.                                                  |
| _amount_  | amount:20   | 0                      | The optional amount of time in seconds, the player has to ride a specific train. |

```YAML title="Example"
objectives:
  rideTrain: "traincartsride"
  rideTrain1: "traincartsride name:Train1"
  rideTrain20Seconds: "traincartsride name:Train1 amount:20"
```

#### TrainCarts ride objective: `traincartsexit`

This objective requires the player to exit a train.

```YAML title="Example"
objectives:
  exitTrain: "traincartsexit"
  exitTrain1: "traincartsexit name:Train1"
```
