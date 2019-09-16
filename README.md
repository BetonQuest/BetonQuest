![BetonQuest](https://betonquest.pl/assets/logo.png)

# BetonQuest [![Build Status](https://travis-ci.org/Co0sh/BetonQuest.svg?branch=master)](https://travis-ci.org/Co0sh/BetonQuest)

BetonQuest is a Spigot questing plugin. It lets the users create complex quests, NPC conversations and other server mechanics for their players.

This page is contains information important for developers. If you're a user of BetonQuest, head to the [Spigot page](https://www.spigotmc.org/resources/betonquest.2117/) or the [official Wiki](https://github.com/Co0sh/BetonQuest/wiki). Alternatively, you can ask for support on [Issues](https://github.com/Co0sh/BetonQuest/issues).

## Getting started

If you're looking for an already compiled binaries visit [Releases](https://github.com/Co0sh/BetonQuest/releases) for standard releases or [dev website](https://betonquest.pl) for development builds. This section will cover setting up the BetonQuest development workflow.

### Prerequisites

In order to compile BetonQuest you need to have [JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html) installed on your system (version 8 or later). This is basically the Java compiler.

In order to easily build the plugin _.jar_ file you'll need [Maven](https://maven.apache.org), the build automation tool. Simply download a binary zip archive and unpack it somewhere on your system. Don't forget to add `bin` directory [to your PATH](https://www.google.com/search?q=add+directory+to+path).

Additionally, Maven needs the [`JAVA_HOME` environment variable](www.google.com/searchq=setting+java_home) to point to your JDK installation in order to work.

### Building

You can either download the [source code](https://github.com/Co0sh/BetonQuest/archive/master.zip) directly and unpack it on your system or clone it with Git (recommended):

```
git clone https://github.com/Co0sh/BetonQuest.git
```

To compile the plugin open the command line and navigate to the `BetonQuest` directory. Then use Maven to package BetonQuest into a _.jar_ file:

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

BetonQuest is installed like any other Spigot plugin, by dropping the _BetonQuest.jar_ file into the `plugins` directory and restarting/reloading the server. It will automatically deploy example files. Please read the [Quick start tutorial](docs/Quick-start-tutorial.md) page to learn the basics.

## Documentation

The documentation for the current plugin features is located in [`docs`](docs) directory. You can also find the same docs compiled into a _.pdf_ file on the [dev website](https://betonquest.pl).

If you're looking for documentation for the latest stable release head to the [Wiki](https://github.com/Co0sh/BetonQuest/wiki). You can also view the wiki's history if you clone it locally with:

```
git clone https://github.com/Co0sh/BetonQuest.wiki.git
```

### Contents

* [Home](https://github.com/Co0sh/BetonQuest/wiki/)
* [Installation and Configuration](https://github.com/Co0sh/BetonQuest/wiki/Installation-and-Configuration)
* [Commands and Permissions](docs/Commands-and-permissions.md)
* [**Quick start tutorial**](docs/Quick-start-tutorial.md)
* [Reference](docs/Reference.md.md)
  * [Conversations](docs/Reference.md#conversations)
  * [Conditions](docs/Reference.md#conditions)
  * [Events](docs/Reference.md#events)
  * [Objectives](docs/Reference.md#objectives)
  * [Packages](docs/Reference.md#packages)
  * [Global variables](docs/Reference.md#global-variables)
  * [Canceling quests](docs/Reference.md#canceling-quests)
  * [Global locations](docs/Reference.md#global-locations)
  * [Static events](docs/Reference.md#static-events)
  * [Journal](docs/Reference.md#journal)
  * [Tags](docs/Reference.md#tags)
  * [Points](docs/Reference.md#points)
  * [NPCs](docs/Reference.md#npcs)
  * [Items](docs/Reference.md#items)
  * [Backpack](docs/Reference.md#backpack)
  * [Party](docs/Reference.md#party)
* [_Condition List_](docs/Conditions-List.md)
* [_Events List_](docs/Events-List.md)
* [_Objectives List_](docs/Objectives-List.md)
* [_Variables List_](docs/Variables-List.md)
* [Compatibility](docs/Compatibility.md)
* [Tips and tricks](docs/Tips-and-tricks.md)
* [Frequently Asked Questions](docs/Frequently-Asked-Questions.md)
* [Info for developers](docs/Info-for-developers.md)
* [Contributing](docs/Contributing.md)
* [Images](docs/Images.md)

## Contributing

The contributing guidelines are located in the [CONTRIBUTING.md](CONTRIBUTING.md) file. Please take a brief look at them before opening an issue or pull request, it will make the world a better place.

## License

The project is licensed under GPLv3 license - see the [LICENSE](LICENSE) file for more details.
