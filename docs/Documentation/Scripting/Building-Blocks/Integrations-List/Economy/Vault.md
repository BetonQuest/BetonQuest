## Vault[](http://dev.bukkit.org/bukkit-plugins/vault/)

### Conditions

#### Vault Money Condition: `money`

Checks if the player has the specified amount of money.

```YAML title="Example"
conditions:
  hasMoney: "money 1"
  canAffordPlot: "money 10000"
  isRich: "money 1000000"
```

### Actions

#### Vault Money Action: `money`

Deposits, withdraws or multiplies money in the player's account.

| Parameter  | Syntax              | Default Value          | Explanation                                                              |
|------------|---------------------|------------------------|--------------------------------------------------------------------------|
| _amount_   | Number              | :octicons-x-circle-16: | The amount of money to add or remove.                                    |
| _notify_   | Keyword: `notify`   | Disabled               | Display a message to the player when their balance is changed.           |
| _multiply_ | Keyword: `multiply` | Disabled               | Multiplies the current balance with the amount instead simply adding it. |

```YAML title="Example"
actions:
  sellItem: "money +100"
  buyPlot: "money -10000"
  winLottery: "money 7 multiply notify"
```

#### Change Permission (Groups): `permission`

Adds or removes a permission or a group.

| Parameter | Syntax                      | Default Value          | Explanation                                                                                                          |
|-----------|-----------------------------|------------------------|----------------------------------------------------------------------------------------------------------------------|
| _action_  | `add` or `remove`           | :octicons-x-circle-16: | Whether to add or remove the thing specified using the following arguments.                                          |
| _type_    | `perm` or `group`           | :octicons-x-circle-16: | Whether to use a permission or permission group.                                                                     |
| _name_    | The name of the permission. | :octicons-x-circle-16: | The name of the permission or group to add.                                                                          |
| _world_   | The name of the world.      | Global                 | You can limit permissions to certain worlds only. If no world is set the permission will be set everywhere (global). |

```YAML title="Example"
actions:
  allowFly: "permission add perm essentials.fly"
  joinBandit: "permission add group bandit"
  leaveBandit: "permission remove group bandit"
```

### Placeholders

#### Vault Money Placeholder: `money`

Use `%money.amount%` for showing the player's balance.
Use `%money.left:500%` for showing the difference between the player's balance and the specified amount of money.

```YAML title="Example"
actions:
  notifyBalance: "notify You have %money.amount%$!"
  notifyNotEnough: "notify You still need %money.left:10000%$ to buy this plot."
```
