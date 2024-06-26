---
icon: material/upload
---
This guide explains how to migrate from the latest BetonQuest 2.X version to BetonQuest 3.X.

**The majority of changes will be migrated automatically. However, some things must be migrated manually.**

!!! warning 
    Before you start migrating, you should **backup your server**!

## Changes

Steps marked with :gear: are migrated automatically. Steps marked with :exclamation: must be done manually.

- [3.0.0-DEV-12 - Rename Constants](#300-dev-12-rename-constants) :gear:

### 3.0.0-DEV-12 - Rename Constants :gear:
??? info "Automated Migration"
    *The migration is automated. You shouldn't have to do anything.*
    
    -------------
    
    "Global Variables" were renamed to "Constants" to better reflect their purpose,
    and to also integrate them in the existing variable system.
    
    <div class="grid" markdown>
    
    ```YAML title="Old Syntax"
    variables:
      MyVariable: Hello
      MyCustomVariable: $MyVariable$ World
    events:
      sendNotify: notify $MyCustomVariable$
    ```
    
    ```YAML title="New Syntax"
    constants:
      MyVariable: Hello
      MyCustomVariable: %constant.MyVariable% World
    events:
      sendNotify: notify %constant.MyCustomVariable%
    ```
    
    </div>
