---
icon: material/compass
---

# How to create a quest compass navigation hint

Quest compass targets are useful when players should find the next quest location without reading coordinates.
You can add a target to the player's selectable compass targets and also set it as the active compass target directly.

The following example sends the player to an old mine entrance and removes the compass target after they arrive.

The goal is:

- A compass target points to the next quest location.
- The target has a name and an icon for the compass selection menu.
- Starting the quest adds and sets the compass target.
- An actionbar notification tells the player what to do.
- Reaching the destination removes the target again.

```yaml
compass:
  oldMineEntrance:
    name:
      en-US: Old Mine Entrance #(1)!
    location: 120;64;-240;world #(2)!
    item: mineCompassIcon #(3)!

objectives:
  reachOldMine: "location 120;64;-240;world 5 actions:arriveAtOldMine" #(4)!

actions:
  startMineQuest: "folder addOldMineCompass,setOldMineCompass,addOldMineObjective,showOldMineHint" #(5)!

  addOldMineCompass: "compass add oldMineEntrance" #(6)!
  setOldMineCompass: "compass set oldMineEntrance" #(7)!
  addOldMineObjective: "objective add reachOldMine"
  showOldMineHint: "notify &6Follow your compass to the Old Mine Entrance. io:actionbar sound:block.note_block.pling" #(8)!

  arriveAtOldMine: "folder removeOldMineCompass,oldMineArrivedMessage" #(9)!
  removeOldMineCompass: "compass del oldMineEntrance" #(10)!
  oldMineArrivedMessage: "notify &aDestination reached!\n&7The mine entrance is ahead. io:title sound:ui.toast.challenge_complete"

items:
  mineCompassIcon: "simple COMPASS title:&6Old_Mine"
```

1. The display name shown in the compass selection menu. You can add more languages below `name`.
2. The target location in unified location format: `x;y;z;world`.
3. The optional item shown for this target in the compass selection menu.
4. The player completes this objective when they are within 5 blocks of the target.
5. Starts the navigation flow when the quest begins.
6. Adds the target to the player's selectable compass targets.
7. Immediately points the player's compass to this target.
8. Sends a short actionbar hint without interrupting gameplay.
9. Runs after the player reaches the destination.
10. Removes the target because it is no longer relevant.

Players can open the compass selection menu with:

```text
/compass
```

They can also open the backpack and click the compass button if it is enabled in the BetonQuest config.

Use both `compass add` and `compass set` when players should be able to select the target again later.
If you only use `compass set`, the compass target changes immediately, but it is not added to the player's selectable
compass targets.
