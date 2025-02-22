---
icon: material/upload
---
This guide explains how to migrate from the latest BetonQuest 2.X version to BetonQuest 3.X.

**The majority of changes will be migrated automatically. However, some things must be migrated manually.**

!!! warning 
    Before you start migrating, you should **backup your server**!

## Changes

Steps marked with :gear: are migrated automatically. Steps marked with :exclamation: must be done manually.

- [3.0.0-DEV-58 - Delete messages.yml](#300-dev-58-delete-messagesyml) :exclamation:
- [3.0.0-DEV-65 - Delete menuConfig.yml](#300-dev-65-delete-menuconfigyml) :exclamation:
- [3.0.0-DEV-71 - Renamed Translation Keys](#300-dev-71-renamed-translation-keys) :gear:

### 3.0.0-DEV-58 - Delete messages.yml :exclamation:

The `messages.yml` file has been removed.
All messages are now stored in the `lang` folder, you can also customize them there and add new languages.
If you still have the old messages.yml file, you get a warning in the console.
If you dont have any custom messages,
you can delete the file safely otherwise you should move the messages to the new location.

### 3.0.0-DEV-65 - Delete menuConfig.yml :exclamation:

The `menuConfig.yml` file has been removed.
If you had the option `default_close` configured, you can now find this option in the `config.yml` file.
All translations in the config where also moved to the `lang` folder,
so also here you need to move your custom translations.

### 3.0.0-DEV-71 - Renamed Translation Keys :gear:

??? info "Automated Migration"
    *The migration is automated. You shouldn't have to do anything.*
    
    -------------

    All translations where moved from the messages.yml file to individual files in the `lang` folder.
    Also all keys for the langusges have been renamed to match the new lang-region format, where lang and region are always two letters.  
    So the mapping of the old to the new keys is as follows:
    
    | Old Key | New Key | Old Key | New Key | Old Key | New Key | Old Key | New Key |
    |---------|---------|---------|---------|---------|---------|---------|---------|
    | en      | en-US   | de      | de-DE   | es      | es-ES   | fr      | fr-FR   |
    | hu      | hu-HU   | it      | it-IT   | nl      | nl-NL   | pl      | pl-PL   |
    | pt-br   | pt-BR   | pt-pt   | pt-PT   | ru      | ru-RU   | vi      | vi-VN   |
    | cn      | zh-CN   |         |         |         |         |         |         |
    
    If you have custom translations, you need to move them to the new `lang` folder.
    Because the keys have been renamed, you need to rename the keys in some parts of config files and scripts.
    
    ```YAML title="new config.yml"
        language: en-US
    ```
    
    <div class="grid" markdown>
    
    ```YAML title="Old Syntax Events"
    events:
      notify: notify {en} English text {de} Deutscher text
    ```
    
    ```YAML title="New Syntax Events"
    events:
      notify: notify {en-US} English text {de-DE} Deutscher text
    ```
    
    </div>
    
    And the most complicated change is everywhere you used multiple languages in sections, here is only one example:
    
    <div class="grid" markdown>
    
    ```YAML title="Old Conversation"
    conversations:
      unkown:
        quester:
          en: Jane Doe
          de: Erika Mustermann
    ```
    
    ```YAML title="New Conversation"
    conversations:
      unkown:
        quester:
          en-US: Jane Doe
          de-DE: Erika Mustermann
    ```
    
    </div>
    
    The same applies to all other sections where you used multiple languages, this should be a complete list:
    
    - `conversations.*.quester`
    - `conversations.*.NPC_options.*.text`
    - `conversations.*.player_options.*.text`
    - `compass.*.name`
    - `cancel.*.name`
    - `journal.*`
    - `journal_main_page.*.text`
    - `menus.*.items.*.text`
