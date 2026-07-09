---
icon: material/cash-multiple
---

# How to reward money with Vault

!!!info "Requirements"

    The `money` action, condition, and placeholders are provided by the Vault integration.
    Your server needs:
    
    - Vault
    - an economy plugin supported by Vault
    - BetonQuest with the Vault hook loaded

    After installing or changing economy plugins, restart the server and check `/bq version`.
    It should list Vault as a hooked plugin.


Vault rewards are useful when quests should pay players with the server economy instead of items.
BetonQuest can deposit money, withdraw money, check whether a player has enough money, and show the current balance.

The following example rewards a player with money after finishing a delivery quest.
It also shows how to check if the player can afford an optional fee.

The goal is:

- Reward the player with money when the quest is completed.
- Show a notification when the balance changes.
- Check if the player has enough money for a paid option.
- Withdraw money only after the condition succeeds.
- Display the player's current balance in a message.

```yaml
objectives:
  deliverLetter: "location 120;64;-240;world 3 actions:finishDelivery" #(1)!

actions:
  startDelivery: "folder addDeliveryStarted,addDeliveryObjective,sendDeliveryHint" #(2)!
  addDeliveryStarted: "tag add delivery_started"
  addDeliveryObjective: "objective add deliverLetter"
  sendDeliveryHint: "notify &6Deliver the letter to the old mine."

  finishDelivery: "folder addDeliveryDone,rewardMoney,showBalance" #(3)!
  addDeliveryDone: "tag add delivery_done"
  rewardMoney: "money +250 notify" #(4)!
  showBalance: "notify &aYou now have &f%money.amount%$&a." #(5)!

  buyFastTravel: "if canAffordFastTravel payFastTravel else notEnoughMoney" #(6)!
  payFastTravel: "folder takeTravelFee,teleportToMine"
  takeTravelFee: "money -50 notify" #(7)!
  teleportToMine: "teleport 120;64;-240;world"
  notEnoughMoney: "notify &cYou need &f%money.left:50%$ &cmore for fast travel." #(8)!

conditions:
  canAffordFastTravel: "money 50" #(9)!
  deliveryStarted: "tag delivery_started"
  deliveryDone: "tag delivery_done"
```

1. The delivery objective finishes when the player reaches the target location.
2. Starts the quest, stores the quest state, adds the objective, and tells the player what to do.
3. Runs after the player completes the delivery objective.
4. Deposits 250 money into the player's economy account. `notify` lets BetonQuest show the balance change.
5. Uses the Vault money placeholder to show the player's current balance.
6. Checks the money condition and runs either the paid fast travel or the failure message.
7. Withdraws 50 money from the player's account.
8. Shows how much money the player is still missing for a price of 50.
9. Checks whether the player has at least 50 money.


## Common patterns

Use a positive amount to reward money:

```yaml
actions:
  rewardQuest: "money +100 notify"
```

Use a negative amount to withdraw money:

```yaml
actions:
  payFee: "money -25 notify"
```

Use a money condition before withdrawing money:

```yaml
conditions:
  hasEntryFee: "money 25"

actions:
  enterArena: "folder payEntryFee,startArena conditions:hasEntryFee"
  payEntryFee: "money -25 notify"
  startArena: "objective add arenaFight"
```

Use placeholders when the player needs feedback:

```yaml
actions:
  showMoney: "notify &aBalance: &f%money.amount%$"
  showMissingMoney: "notify &cYou still need &f%money.left:100%$&c."
```

