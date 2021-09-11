# Contributing Guidelines

The BetonQuest organisation recommends <a href="https://www.jetbrains.com/idea/" target="_blank">IntelliJ Community
Edition</a> as the preferred IDE. This makes it possible to ship a complete project setup that ensures a consistent code
quality and style. The project has been setup using maven and will build immediately on your device without any extra
work (like manually adding dependencies).

Everyone that wants to use another IDE can do so but has to ensure that their code meets the guidelines.
**Notice that everything that is prescribed in the Coding Guidelines will be checked by the Build-Pipeline. The
Build-Pipeline will not accept commits that fail to respect the guidelines.**

## Walkthrough of the Guidelines

### Setup

You can clone the project from our GitHub repository:

```
git clone https://github.com/BetonQuest/BetonQuest.git
```

### Get ready to create a Pull Request!

#### Test your changes!

Please ensure your changes actually work and haven't introduced any bugs. Therefore, you should compile the plugin and
run it on a local 1.13+ test server. Please install any related dependencies! If you changed code related to MySQL then
setup a MySQL on your machine and test if BetonQuest still saves its data correctly.

#### Does your change require updates to users configs?

This would be the case if you removed arguments or renamed e.g. an event. You then have to write code to update any
related configs to your new syntax. Search for the ConfigUpdater class in the project to see how its done.

#### Validate your codes quality

Please check you code with PMD. IntelliJ's plugin store offer the _QAPlug_ & _QAPlug - PMD_ plugins for free. We provide
our own ruleset for PMD (_config/pmd-ruleset.xml_) that needs to be imported in QAPlug. To do so open IntelliJ's
settings and navigate to QAPlug -> Coding Rules. Click the plus and select Project Profile. Now you just need to tick "
Import Profile" to add our ruleset (_config/pmd-ruleset.xml_).

Please also remove any TODO's along with commented out code.

You can run `mvn verify` locally to ensure that you meet (nearly) all conditions before creating a PR.

#### Update the Changelog

You need to add your changes to the _Changelog.md_ file that can be found in the projects root folder.

#### Update the documentation

You also need to document changes or additions to the plugins feature set in our documentation. Please notice that the
documentation also has [it's own guidelines](../Docs/Guidelines.md).

#### Clean the commit history

You need to have a clean commit history in order to get you PR accepted. Check out
this <a href="https://medium.com/@catalinaturlea/clean-git-history-a-step-by-step-guide-eefc0ad8696d" target="_blank">
guide</a> if you don't know how to do so.

#### Add new repositories to Maven's settings.xml

New repositories need to be added in this format: `betonquest-<repoName>-repo`.
Additionally, you need to add that repositories ID to Maven's `settings.xml` configuration.
Please see [this guide](../SetupIDE.md#build-speed-up) for an example configuration.

Make sure to add the new repository ID to the linked guide too!
