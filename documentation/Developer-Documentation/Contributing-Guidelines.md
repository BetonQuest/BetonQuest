# Contributing Guidelines

The BetonQuest organisation recommends IntelliJ as the preferred IDE.
This makes it possible to ship a complete project setup that ensures a consistent code quality and style.
The project has been setup using maven and will build immediately on your device without any extra work (like manually adding dependencies).

Everyone that wants to use another IDE can do so but has to ensure that their code meets the guidelines.
**Notice that everything that is prescribed in the Coding Guidelines will be checked by the Build-Pipeline.
The Build-Pipeline will not accept commits that fail to respect the guidelines.**


## Walkthrough of the Guidelines

### Setup

The first thing you should do is to clone the project from our GitHub repository:

```
git clone https://github.com/BetonQuest/BetonQuest.git
```

**Make sure to set this git config setting in your project before touching any code:**
Windows:
```
git config core.autocrlf true
```
Linux:
``` 
git config core.autocrlf input
```


### Get ready to create a Pull Request!

#### Test your changes!
Pretty self-explanatory, isn't it?

#### Does your change require updates to users configs?
This would be the case if you removed arguments or renamed e.g. an event.
You then have to write code to update any related configs to your new syntax.
Search for the ConfigUpdater class in the project to see how its done.

#### Validate your codes quality
Please check you code with PMD. IntelliJ's plugin store offer the _QAPlug_ & _QAPlug - PMD_ plugins for free.
We provide our own ruleset for PMD (_config/pmd-ruleset.xml_) that needs to be imported in QAPlug. 
To do so open IntelliJ's settings and navigate to  QAPlug -> Coding Rules. Click the plus and select Project Profile.
Now you just need to tick "Import Profile" to add our ruleset (_config/pmd-ruleset.xml_).

Please also remove any TODO's along with commented out code.


#### Update the Changelog
You need to add your changes to the _Changelog.md_ file that can be found in the projects root folder.

#### Update the documentation
You also need to document changes or additions to the plugins feature set in our documentation. Please notice that the documentation 
also has [it's own guidelines](../Contributing/Documentation/Guidelines.md).

#### Clean the commit history
You need to have a clean commit history in order to get you PR accepted.
Check out this <a href="https://medium.com/@catalinaturlea/clean-git-history-a-step-by-step-guide-eefc0ad8696d" target="_blank">guide</a> if you don't know how to do so.
