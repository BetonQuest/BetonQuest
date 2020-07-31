<p align="center">
  <a href="https://betonquest.github.io/BetonQuest/"><img src="https://betonquest.pl/assets/logo.png" alt="BetonQuest
  "></a>
</p>

<p align="center">Minecraft RPG Questing Plugin</p>

<p align="center">
    <a href="https://bstats.org/plugin/bukkit/BetonQuest/551/">
        <img src="https://img.shields.io/bstats/servers/551" />
     </a>
    <img src="https://img.shields.io/spiget/stars/2117"/>
    <a href="https://discord.gg/MvmkHEu" target="_blank">
        <img src="https://img.shields.io/badge/discord-join-7289DA.svg?logo=discord&longCache=true&style=flat" />
    </a>
    <a href="https://github.com/BetonQuest/BetonQuest/actions" target="_blank">
        <img src="https://github.com/BetonQuest/BetonQuest/workflows/Build/badge.svg?branch=master&event=push">
    </a>
    <a href="https://github.com/BetonQuest/BetonQuest/blob/master/LICENSE">
        <img src="https://img.shields.io/badge/license-GPLv3-blue" alt="License" />
      </a>
    

</p>

---

**Documentation**: <a href="https://betonquest.github.io/BetonQuest/" target="_blank">https://betonquest.github.io/BetonQuest/</a>

**Source Code**: <a href="https://github.com/BetonQuest/BetonQuest/" target="_blank">https://github.com/BetonQuest/BetonQuest</a>

---

BetonQuest is a Spigot questing plugin. It lets the users create complex quests, NPC conversations and other server mechanics for their players.

This page is contains information important for developers. If you're a user of BetonQuest, head to the [Spigot page](https://www.spigotmc.org/resources/betonquest.2117/) or the [official Wiki](https://github.com/BetonQuest/BetonQuest/wiki). Alternatively, you can ask for support on [Issues](https://github.com/BetonQuest/BetonQuest/issues).

## Getting started

If you're looking for an already compiled binaries visit [Releases](https://github.com/BetonQuest/BetonQuest/releases) for standard releases or [dev website](https://betonquest.pl) for development builds. This section will cover setting up the BetonQuest development workflow.

### Prerequisites

In order to compile BetonQuest you need to have [JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html) installed on your system (version 8 or later). This is basically the Java compiler.

In order to easily build the plugin _.jar_ file you'll need [Maven](https://maven.apache.org), the build automation tool. Simply download a binary zip archive and unpack it somewhere on your system. Don't forget to add `bin` directory [to your PATH](https://www.architectryan.com/2018/03/17/add-to-the-path-on-windows-10/).

Additionally, Maven needs the [`JAVA_HOME` environment variable](https://confluence.atlassian.com/doc/setting-the-java_home-variable-in-windows-8895.html) to point to your JDK installation in order to work.

### Building

You can either download the [source code](https://github.com/BetonQuest/BetonQuest/archive/master.zip) directly and unpack it on your system or clone it with Git (recommended):

```
git clone https://github.com/BetonQuest/BetonQuest.git
```

To compile the plugin in IntelliJ open the command line and navigate to the `BetonQuest-parent` module. In Eclipse, you need to navigate to the `BetonQuest-build` module. Then use Maven to package BetonQuest into a _.jar_ file:

```
mvn package
```

If you want to run an optimised build along with javadocs and source artifacts, run:

```
mvn package -Drelease
```

This is much slower and the resulting binary is only slightly faster, so use it only when compiling for production. In development cycle use the regular build command.

In either case, the final _BetonQuest.jar_ file will appear in the `target` directory.

### Installing

BetonQuest is installed like any other Spigot plugin, by dropping the _BetonQuest.jar_ file into the `plugins` directory and restarting/reloading the server. It will automatically deploy example files. Please read the [Quick start tutorial](https://betonquest.github.io/BetonQuest/en/latest/04-Quick-start-tutorial/) page to learn the basics.

## Documentation

The documentation for the current plugin features is located in [here](https://betonquest.github.io/BetonQuest/). You can also find the same docs compiled into a _.pdf_ file [here](https://betonquest.github.io/BetonQuest/en/latest/pdf/documentation.pdf).

## Contributing

The contributing guidelines are located in the [docs](https://betonquest.github.io/BetonQuest/en/latest/14-Contributing/) file. Please take a brief look at them before opening an issue or pull request, it will make the world a better place.

## License

The project is licensed under GPLv3 license - see the [LICENSE](LICENSE) file for more details.
