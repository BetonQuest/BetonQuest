![BetonQuest](https://betonquest.pl/assets/logo.png)

**Documentation for 1.10-dev version**

BetonQuest is advanced and powerful quests plugin. It offers RPG-style conversations with NPCs and a very flexible quest system. Instead of being limited to creating "quest" objects with taking requirements and rewards upon completion, BetonQuest allows you to freely define what should happen (events), if it should happen (conditions) and what needs to be done for it to happen (objectives). Your quests don't have to look like "kill, bring, get reward": you can create multi-threaded stories, narrated with NPC conversations, with multiple endings affecting the player's gameplay in many ways.

## Features

* Minecraft **1.9**, **1.10**, **1.11** and **1.12** support
* **Multiple choice conversations** with NPCs using an inventory GUI
* **Powerful event system**: anything you want can happen anywhere in a quest
* Even more **powerful condition system**: you can limit whenever something should (or shouldn't) happen
* **Journal** in a book
* **Backpack** for quest items
* Advanced item handling which considers even text in books
* **Party system** allowing for creation of group quests
* Ability to create various **reputation systems** (points)
* Firing events for a players when they enter specified area (global locations)
* **Daily quests** or repeatable reward collection (`delay` objective)
* Variables in conversations - let the NPC tell how much more wood he needs!
* Quests can be organized into distributable packages
* **Citizens2** NPC support
* Integrated with [BetonLangAPI](https://github.com/Co0sh/BetonLangAPI), [Citizens](https://dev.bukkit.org/bukkit-plugins/citizens/), [Denizen](https://dev.bukkit.org/bukkit-plugins/denizen/), [EffectLib](https://dev.bukkit.org/bukkit-plugins/effectlib/), [Heroes](https://dev.bukkit.org/bukkit-plugins/heroes/), [HolographicDisplays](https://dev.bukkit.org/bukkit-plugins/holographic-displays/), [LegendQuest](https://dev.bukkit.org/bukkit-plugins/legendquest/), [Magic](https://dev.bukkit.org/bukkit-plugins/magic/), [McMMO](https://dev.bukkit.org/bukkit-plugins/mcmmo/), [MythicMobs](https://dev.bukkit.org/bukkit-plugins/mythicmobs/), [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/), [PlayerPoints](https://dev.bukkit.org/bukkit-plugins/playerpoints/), [Quests](https://dev.bukkit.org/bukkit-plugins/quests/), [RacesAndClasses](https://dev.bukkit.org/bukkit-plugins/racesandclasses/), [Shopkeepers](https://dev.bukkit.org/bukkit-plugins/shopkeepers/), [SkillAPI](https://dev.bukkit.org/bukkit-plugins/skillapi/), [Skript](https://dev.bukkit.org/bukkit-plugins/skript/), [Vault](https://dev.bukkit.org/bukkit-plugins/vault/), [WorldEdit](https://dev.bukkit.org/bukkit-plugins/worldedit/) and [WorldGuard](https://dev.bukkit.org/bukkit-plugins/worldguard/)
* Multiple languages and easy translating
* API for creating your own events, conditions and objectives
* SQLite and **MySQL** support
* Last but not least, an **active, open source** project with development builds available

## Overview

Imagine you have a conversation with an NPC. You can choose from multiple options, and the NPC will react differently, for example he will tell you to cut some trees when asked for a job. If you tell him that you accept his offer, an event will be fired. It will start an objective for getting wood. It will also "tag" you as someone who started the quest. From now on the NPC will check for that tag, and use different options in the conversation, for example telling you to hurry up.

When you complete the objective (by breaking wood blocks), the objective will fire another event. This one will "tag" you as someone who collected the wood. When you go back to the NPC and tell him about it, he will check (using a condition) if you actually have the wood in your inventory. If so, he will fire another event, giving you the reward.

There was no single "quest" object. This was only a conversation, which was firing events and checking conditions. The objective also wasn't a "quest" - it only added a tag when you collected the wood, nothing more. It could not exist on it's own. The same conversation on the other hand could start some other quests afterwards (for example mining some ore), so it's also not a "quest".

Don't be disappointed by my examples of getting wood and mining ore. These were only simplifications, so it's easier to explain the system. BetonQuest is capable of much more. You can add entries to player's journal based on the quests he's doing like in Morrowind, the conversations can be as multi-threaded as in Baldur's Gate and quests can be started by entering specific location like in Skyrim. You can create reputation systems, unique quest items, books that react to reading them and so on. Your quests can have multiple ways to different endings depending on players' decisions and they can require multiple players to do something.

You don't have to use BetonQuest for quests only. Conversations with NPCs can help your players, teleports them around the map, describe server features, buy or sell stuff, give ranks etc. The only limit is your imagination!
