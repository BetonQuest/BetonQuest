This migration guide currently need to be done manual. As long as BQ 2.0 is in development, this will not change!

Before you start migrating, you should **backup your system**!

Also check your current version, so you know which migration steps you need to do.

## Changes
Skip to the first version, that is above the version, that you used before starting the migration:

Changes introduced in:

- [2.0.0-DEV-238](#200-dev-238)

### 2.0.0-DEV-238
- Ensure your server is running on __java 17__
- Move your current Quests to the folder `BetonQuest/QuestPackages/`, as quests are now loaded from there
- Rename `main.yml` to `package.yml`
- Change the `events.yml`, `objectives.yml`, `conditions.yml`, `journal.yml` and `items.yml` to the following format
  with an extra prefix matching there file name:
  ```YAML
  events:
    myEvent1: ...
    ....
  ```
- Change the `conversations` and `menus` to the following format with an extra prefix matching there type and the file
  name:
  ```YAML
  conversations:
    FileName:
      NPC_options: ....
      ....
  ```
  or alternatively:
  ```YAML
  conversations.FileName:
    NPC_options: ....
    ....
  ```
- Quest packages can now contain nested quest packages in sub folders. You can also have any file and folder structure
  with any file and folder names you want. Only the `package.yml` is reserved as indicator for a quest
  package. [DOCS](../Reference.md#packages)
