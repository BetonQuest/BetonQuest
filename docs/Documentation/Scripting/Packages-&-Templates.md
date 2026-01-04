---
icon: material/folder-open
---
## Packages

All quests you create are organized into packages. A single package can contain one or multiple quests - it's up to your
liking. **It is very important to have a good understand of packages. Read the packages chapter carefully.**

### Structure

A package is a folder with a "_package.yml_" file. It must be placed inside the "_BetonQuest/QuestPackages_" directory.   
Additionally, you can create extra files or sub-folders inside a package to organize your quest the way you want.
Sub-folders of packages that contain a "_package.yml_" are separate packages, they do not belong to the surrounding
package in any way. 
 
Let's take a look at a few examples:

!!! example "Structure Examples"
    Every quest package is surrounded with a blue box.
   
    === "Simple Package"
        A very simple package. It's defined by the _package.yml_ and has two additional files.       
        <img src="../../../_media/content/Documentation/Reference/PackageSimple.png" width=450>
        
    === "Complex Package"
        The package `storyLine` is defined by the _package.yml_. It contains two sub-folders, both of them
        (including their files) are part of the package.<br>
        <img src="../../../_media/content/Documentation/Reference/PackageComplex.png" width=450>
        
    === "Nested Packages"
        The package `weeklyQuests` is defined by the _package.yml_. It contains two sub-folders, they are **not** part
        of the package `weeklyQuests`. This is the case because they have their own _package.yml_ files. Because of that they are 
        separate packages.<br> 
        <img src="../../../_media/content/Documentation/Reference/PackagesNested.png" width=450>

### Defining features

You can freely define features (actions, conversations, items etc.) in all files 
of a quest package. However, they need to be defined in a section that defines their type.

The names of these features must be unique in that package, no matter which file they are in.

??? example

    ```YAML
    actions:
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
    
### Working across Packages

Accessing features from other packages can be very helpful to link quests together.
All actions, conditions, objectives, items and conversations can be accessed.

You never need to access a specific file since feature names are unique within a package.

#### Top-Level Packages

You can access **top-level packages** (placed directly in "_QuestPackages_") by prefixing the feature's name with a
greater than (`>`) and the package name. 

??? example
    Let's assume you have a `rewards` package that contains player reward actions.  
    Let's run the `easyMobObjective` action of the `rewards` package from another package:
    
    1. Add a greater than (`>`) before the action name :arrow_right: `{==>==}easyMobObjective`
    2. Add the package name in front of the greater than :arrow_right: `{==rewards==}>easyMobObjective`
    
    An example usage could look like this:
    ````YAML
    zombieObjective: "mobkill ZOMBIE 5 actions:{==rewards>easyMobObjective==}"
    ````
    Note that this only works for top-level packages (the `rewards` package is placed directly in the `QuestPackages`
    folder).
    Check the next paragraph to see how it's done for other packages.

#### Packages in Sub-folders

You can access packages in sub-folders by prefixing the feature's name with
the package name and the path from the "_QuestPackages_" folder to the package.

??? example

    === "One Nested Package"
        Let's assume you have a `dailyQuests` package that contains a `dailyQuestOne` package. The `dailyQuests` package
        is located in the `QuestPackages` folder.
        Let's run the `startDailyQuest` action of the `dailyQuestOne` package from a third package:
        
        1. Combine the action name with the package name :arrow_right: `{==dailyQuestOne>==}startDailyQuest`
        2. Add the path from the `QuestPackages` folder to the `dailyQuestOne` package seperated by dashes (`-`).
        :arrow_right: `{==dailyQuests-==}dailyQuestOne>startDailyQuest`
        
        An example usage could look like this:
        ````YAML
        zombieObjective: "mobkill ZOMBIE 5 actions:{==dailyQuests-dailyQuestOne>startDailyQuest==}"
        ````
        
    === "Multiple Nested Packages"
        Let's assume you have a `dailyQuests` package that contains a `dailyQuestOne` package. The `dailyQuests` package
        is contained inside a folder called `repeatable` which is located in the `QuestPackages` folder.
        Let's run the `startDailyQuest` action of the `dailyQuestOne` package from a third package:
        
        1. Combine the action name with the package name :arrow_right: `{==dailyQuestOne>==}startDailyQuest`
        2. Add the path from the `QuestPackages` folder to the `dailyQuestOne` package seperated by dashes (`-`).
        :arrow_right: `{==repetable-dailyQuests-==}dailyQuestOne>startDailyQuest`
        
        An example usage could look like this:
        ````YAML
        zombieObjective: "mobkill ZOMBIE 5 actions:{==repetable-dailyQuests-dailyQuestOne>startDailyQuest==}"
        ````
    
#### Relative paths

You can specify relative paths to a package instead of full paths. The underscore (`_`) means "one folder up" from 
the current packages "_package.yml_". In turn, a leading dash (`-`) combined with a folder name navigates 
"one folder down" into the given folder.
Each package in the path must be separated by a dash.

This can be useful when distributing or moving packages. Instead of rewriting every package path
to match the current location, relative paths will still work.

??? example

    === "Going Upwards"
        Let's assume you have a `weeklyQuests` folder that contains a `weeklyQuestOne` and a `weeklyQuestTwo` package.
        Let's run the `startQuestTwo` action of the `weeklyQuestTwo` package from the `weeklyQuestOne` package.
        
        1. Combine the action name with the package name :arrow_right: `{==weeklyQuestTwo>==}startQuestTwo`
        2. Add the path from the current _package.yml_ to the folder the package of interested lies in. This is done using
        underscores ("go one folder up"). A dash must be added after each underscore (`-`).
        :arrow_right: `{==_-==}weeklyQuestTwo>startQuestTwo`
        
        An example usage could look like this:
        ````YAML
        zombieObjective: "mobkill ZOMBIE 50 actions:{==_-weeklyQuestTwo>startQuestTwo==}"
        ````
        
    === "Going Downwards"
        Let's assume you have a `weeklyQuests` package that contains a `weeklyQuestTwo` package which contains another
        package called `subQuest`.
        Let's run the `startQuest` action of the `subQuest` package from the `weeklyQuests` package.
        
        1. Combine the action name with the package name :arrow_right: `{==subQuest>==}startQuest`
        2. Add the path from the current _package.yml_ to the folder the package of interest lies in. Package names 
        must be seperated by dashes (`-`). The path must also be started with a dash to signal "from the current package
        downwards". :arrow_right: `{==-weeklyQuestTwo-==}subQuest>startQuest`
        
        An example usage could look like this:
        ````YAML
        zombieObjective: "mobkill ZOMBIE 50 actions:{==-weeklyQuestTwo-subQuest>startQuest==}"
        ````
    
### Disabling Packages

Packages are enabled by default, you can disable a package if you don't want it to be loaded.
Set `enabled` inside the `package` section to `true` or `false` to enable or disable the package.

```YAML
package:
  ## Optionally add this to the package.yml
  enabled: false
```

### Package Version

Each package has a `version` inside the `package` section that is used by the automatic migrator.
When no version is set the newest version will be set on loading.
Any new `package` section will be added at the end of the file, so you probably want to move that to the file's top.

!!! info Legacy Migrations
    When updating from a version before versioning see [Migration to BQ 3.0](../Configuration/Version-Changes/Migration-2-3.md).

```YAML
package:
  version: 3.0.0-QUEST-1 # Don't change this! The plugin's automatic quest updater handles it.
```

## Templates

You should have experience creating and using [packages](#packages) before you start using templates.
Templates are a way to create packages that can be used as a base for other packages to reduce the amount of
repetitive work. Therefore, they are a great way to centralize logic or create utilities.

### Using Templates
Templates work exactly like packages, except that they are placed in the "_BetonQuest/QuestTemplates_" folder instead of
the "_BetonQuest/QuestPackages_" folder and that they are not loaded as a ready to use package.
Instead, they are used as a base for other packages by referring to them in the `templates` section inside the `package` section.

````YAML
package:
  templates:
    - MyTemplate
    - SecondTemplate
````

If you use the above in a package, the `MyTemplate` and `SecondTemplate` templates would be used as a base for
the package. This means that all the actions, objectives, conditions, etc. from the templates would be added to the
package. If the package already contains an action/objective/condition with the same name as one from the template,
the package's actions, objectives, conditions, etc. will be used instead of the one from the template.

If the same actions, objectives, conditions, etc. is defined in multiple templates, the one from the lists first template
will be used.

You can also use templates in templates. Also in this case, the actions, objectives, conditions, etc. that are defined
in the current template will be used instead of the ones from the template that is being used as a base.
