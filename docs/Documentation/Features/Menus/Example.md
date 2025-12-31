---
icon: material/menu-open
---
# Basic Menu Example

This is an example of a basic menu that displays the progress of two quests.

![Example](../../../_media/content/Documentation/Menu/ResultOverview.png)

## Usage
You can copy and paste this example into any file in a package. 
Then reload and execute the command `/bq give YOUR_PACKAGE>openMenuItem` to get the item that opens the menu.

Read the related docs in the menu section to learn more about these configuration options.
  
## Menu Definition
``` YAML
menus:
  questMenu:
    height: 4
    title: "&6&lQuests"
    bind: "openMenuItem"
    command: "/quests"
    slots:
      0-3: "filler,filler,filler,filler"
      4: "reputation"
      5-8: "filler,filler,filler,filler"
      9: "skeletonQuestActive,skeletonQuestDone"
      10: "goldQuestActive,goldQuestDone"
      27-35: "filler,filler,filler,filler,filler,filler,filler,filler,filler"

menu_items:
  skeletonQuestActive:
    item: "skeletonQuestActiveItem"
    amount: 1
    conditions: "!skeletonQuestDone"
    text:
        - "&7[Quest] &f&lBone ripper"
        - "&f&oRipp some skeletons off"
        - "&f&otheir bones to complete"
        - "&f&othis quest."
        - "&f&o"
        - "&eLeft click to locate NPC."
    click:
      left: "locationNotify"
    close: true
  skeletonQuestDone:
    item: "questDone"
    amount: 1
    conditions: "skeletonQuestDone"
    text:
        - "&2[Quest] &f&lBone ripper"
        - "&f&oRipp some skeletons off"
        - "&f&otheir bones to complete"
        - "&f&othis quest."
        - "&f&o"
        - "&2Quest completed!"
    close: false
  goldQuestActive:
    item: "goldQuestActiveItem"
    amount: 1
    conditions: "!goldQuestDone"
    text:
        - "&7[Quest] &f&lGold rush"
        - "&f&oMine some gold"
        - "&f&oto complete this quest."
    click:
      left: "locationNotify"
    close: true
  goldQuestDone:
    item: "questDone"
    amount: 1
    conditions: "goldQuestDone"
    text:
        - "&2[Quest] &f&lGold rush"
        - "&f&oMine some gold"
        - "&f&oto complete this quest."
        - "&2Quest completed!"
    close: false
  reputation:
    item: "xpBottle" 
    amount: 1
    text:
        - "&2Quest Level: &6&l%point.quest_reputation.amount%"
    close: true
  filler: 
    text: "&a "
    item: "filler"

conditions:
  skeletonQuestDone: "tag skeletonQuestDone"
  goldQuestDone: "tag goldQuestDone"
events:
  locationNotify: "notify &cThe skeletons roam at x\\:123 z\\:456!"
items:
  openMenuItem: "simple BOOK title:Quests"

  xpBottle: "simple EXPERIENCE_BOTTLE"
  filler: "simple GRAY_STAINED_GLASS_PANE"

  skeletonQuestActiveItem: "simple BONE"
  goldQuestActiveItem: "simple RAW_GOLD"
  questDone: "simple LIME_CONCRETE"
```
