---
icon: fontawesome/solid/wrench
hide:
  - footer
---
# Plugin configuration
The plugin's config is stored in a file called `menuConfig.yml` which is located in the plugin folder of BetonQuest, right near the config file of BetonQuest.  
It contains some default settings as well as all messages which are sent to the player by the plugin.  
On first start of the plugin the default config file will be created including all default settings which you are then able to change to customize the plugin.

## The config options
* `default_close`:  *(boolean)*  
  Sets if menus should close by default when an item is clicked (`true`) or if they should stay open (`false`).  
  This can also be overridden by each individual menu.    
  **Default value:** `true`

## The messages section
This section contains all messages which are displayed to the player by the plugin.  
You can change them to fit all your needs.  
It's also possible to add additional languages, it works the same way as with BetonQuests messages.yml:  
Just add another section with the short name of your language as key and the translated messages.  
It's not required to specify all messages, if a message is missing for your language it will just pick the message in BetonQuests default language.

