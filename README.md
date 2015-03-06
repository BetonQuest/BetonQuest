![BetonQuest](http://i.imgur.com/Gy9ORlk.png)

BetonQuest
==========                                 

BetonQuest is advanced and powerful quests plugin. It doesn't follow traditional
convention, where "quest" is a closed structure object, which can have a set of
tasks, events and rewards on completion. Instead, BetonQuest introduces the net
of objectives which can under certain conditions trigger events, start new
objectives or change NPC's attitude to the player. Of course grouping this all
together and saying "this group is a quest" is still possible.

Getting Started
---------------

Place the plugin .jar file in plugins directory and restart/reload your server.
The plugin should generate default quest for getting wood. You can access it by
creating an NPC: place somewhere a block of stained clay. On top of it place a
head, and attach to the side of clay block a sign. On the sign write first line
`[NPC]`, second line `innkeeper`. You'll need admin rights to do that. Now right
click on NPC's head to start the conversation.

Documentation
-------------

If you want to create your own quests you should read the documentation.
It contains all required information and a tutorial-like description of features.
You can find it [on the wiki](https://github.com/Co0sh/BetonQuest/wiki).

Download
--------

If you prefer already compiled builds head to the
[DBO page](http://dev.bukkit.org/bukkit-plugins/betonquest/)
or [Spigot page](http://www.spigotmc.org/resources/betonquest.2117/).
Development builds can be found [here](http://betoncraft.pl/downloads).
Use it with caution and report all bugs on 
[Issues](https://github.com/Co0sh/BetonQuest/issues)!

Compiling the plugin
--------------------

You need to have JDK for version 1.7 or above and Maven installed. Download
BetonQuest source code to some directory and issue command `mvn package`
inside it. The .jar package should appear in _target_ directory.

License
-------

The plugin is licensed under GPL version 3 (or above).
