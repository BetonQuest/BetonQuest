---
icon: material/folder-open
---
All quests you create are organized into packages. A single package can contain one or multiple quests - it's up to your
liking. **It is very important to have a good understand of packages. Read this page carefully.**

## Structure

A package is a folder with a _package.yml_ file. It must be placed inside the `BetonQuest/QuestPackages` directory.   
Additionally, you can create extra files or sub-folders inside a package to organize your quest the way you want.
Sub-folders of packages that contain a _package.yml_ are separate packages, they do not belong to the surrounding
package in any way. 
 
Let's take a look at a few examples:

!!! example "Structure Examples"
    Every quest package is surrounded with a blue box.
   
    === "Simple Package"
        A very simple package. It's defined by the _package.yml_ and has two additional files.       
        <img src="../../_media/content/Documentation/Reference/PackageSimple.png" width=450>
        
    === "Complex Package"
        The package `storyLine` is defined by the _package.yml_. It contains two sub-folders, both of them
        (including their files) are part of the package.<br>
        <img src="../../_media/content/Documentation/Reference/PackageComplex.png" width=450>
        
    === "Nested Packages"
        The package `weeklyQuests` is defined by the _package.yml_. It contains two sub-folders, they are **not** part
        of the package `weeklyQuests`. This is the case because they have their own _package.yml_ files. Because of that they are 
        separate packages.<br> 
        <img src="../../_media/content/Documentation/Reference/PackagesNested.png" width=450>

## Defining features

You can freely define features (events, conversations, items etc.) in all files 
of a quest package. However, they need to be defined in a section that defines their type.

The names of these features must be unique in that package, no matter which file they are in.

??? example

    ```YAML
    events:
      teleportPlayer: "..."
    
    conditions:
      hasDiamondArmor: "..."
      
    objectives:
      killCrepper: "..."
      
    items:
      legendarySword: "..."
      
    conversations:
      bobsConversation:
        quester: Bob
        #...
        
    menus:
      homeMenu:
        height: 3
        #...
    ```
    
## Working across Packages

Accessing features from other packages can be very helpful to link quests together.
All events, conditions, objectives, items and conversations can be accessed. Just journal entries only work in their own
package. 

You never need to access a specific file since feature names are unique within a package.

### Top-Level Packages

You can access **top-level packages** (placed directly in `QuestPackages`) by prefixing the feature's name with a
dot and the package name. 

??? example
    Let's assume you have a `rewards` package that contains player reward events.  
    Let's run the `easyMobObjective` event of the `rewards` package from another package:
    
    1. Add a dot (`.`) before the event name :arrow_right: `{==.==}easyMobObjective`
    2. Add the package name in front of the dot :arrow_right: `{==rewards==}.easyMobObjective`
    
    An example usage could look like this:
    ````YAML
    zombieObjective: "mobkill ZOMBIE 5 events:{==rewards.easyMobObjective==}"
    ````
    Note that this only works for top-level packages (the `rewards` package is placed directly in the `QuestPackages`
    folder).
    Check the next paragraph to see how it's done for other packages.

### Packages in Sub-folders

You can access packages in sub-folders by prefixing the feature's name with
the package name and the path from the `QuestPackages` folder to the package.

??? example

    === "One Nested Package"
        Let's assume you have a `dailyQuests` package that contains a `dailyQuestOne` package. The `dailyQuests` package
        is located in the `QuestPackages` folder.
        Let's run the `startDailyQuest` event of the `dailyQuestOne` package from a third package:
        
        1. Combine the event name with the package name :arrow_right: `{==dailyQuestOne.==}startDailyQuest`
        2. Add the path from the `QuestPackages` folder to the `dailyQuestOne` package seperated by dashes (`-`).
        :arrow_right: `{==dailyQuests-==}dailyQuestOne.startDailyQuest`
        
        An example usage could look like this:
        ````YAML
        zombieObjective: "mobkill ZOMBIE 5 events:{==dailyQuests-dailyQuestOne.startDailyQuest==}"
        ````
        
    === "Multiple Nested Packages"
        Let's assume you have a `dailyQuests` package that contains a `dailyQuestOne` package. The `dailyQuests` package
        is contained inside a folder called `repeatable` which is located in the `QuestPackages` folder.
        Let's run the `startDailyQuest` event of the `dailyQuestOne` package from a third package:
        
        1. Combine the event name with the package name :arrow_right: `{==dailyQuestOne.==}startDailyQuest`
        2. Add the path from the `QuestPackages` folder to the `dailyQuestOne` package seperated by dashes (`-`).
        :arrow_right: `{==repetable-dailyQuests-==}dailyQuestOne.startDailyQuest`
        
        An example usage could look like this:
        ````YAML
        zombieObjective: "mobkill ZOMBIE 5 events:{==repetable-dailyQuests-dailyQuestOne.startDailyQuest==}"
        ````
    
### Relative paths

You can specify relative paths to a package instead of full paths. The underscore (`_`) means "one folder up" from 
the current packages _package.yml_. In turn, a leading dash (`-`) combined with a folder name navigates 
"one folder down" into the given folder.
Each package in the path must be seperated by a dash.

This can be useful when distributing or moving packages. Instead of rewriting every package path
to match the current location, relative paths will still work.

??? example

    === "Going Upwards"
        Let's assume you have a `weeklyQuests` folder that contains a `weeklyQuestOne` and a `weeklyQuestTwo` package.
        Let's run the `startQuestTwo` event of the `weeklyQuestTwo` package from the `weeklyQuestOne` package.
        
        1. Combine the event name with the package name :arrow_right: `{==weeklyQuestTwo.==}startQuestTwo`
        2. Add the path from the current _package.yml_ to the folder the package of interested lies in. This is done using
        underscores ("go one folder up"). A dash must be added after each underscore (`-`).
        :arrow_right: `{==_-==}weeklyQuestTwo.startQuestTwo`
        
        An example usage could look like this:
        ````YAML
        zombieObjective: "mobkill ZOMBIE 50 events:{==_-weeklyQuestTwo.startQuestTwo==}"
        ````
        
    === "Going Downwards"
        Let's assume you have a `weeklyQuests` package that contains a `weeklyQuestTwo` package which contains another
        package called `subQuest`.
        Let's run the `startQuest` event of the `subQuest` package from the `weeklyQuests` package.
        
        1. Combine the event name with the package name :arrow_right: `{==subQuest.==}startQuest`
        2. Add the path from the current _package.yml_ to the folder the package of interest lies in. Package names 
        must be seperated by dashes (`-`). The path must also be started with a dash to signal "from the current package
        downwards". :arrow_right: `{==-weeklyQuestTwo-==}subQuest.startQuest`
        
        An example usage could look like this:
        ````YAML
        zombieObjective: "mobkill ZOMBIE 50 events:{==-weeklyQuestTwo-subQuest.startQuest==}"
        ````
    
## Disabling Packages

Each package can be disabled/enabled in the _package.yml_ file, by setting `enabled` to `true` or `false`.

```YAML
# Optionally add this to the package.yml
enabled: false
```
