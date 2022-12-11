---
icon: octicons/package-dependencies-16
hide:
  - footer
---
## Requirements 
You can only add support for plugins that have a public API. This means Maven must be able to resolve the dependency 
from an online repository. Adding dependencies from you local hard-drive is NOT allowed as this stops everyone from 
building the plugin.

## Adding a new repo
Open up the pom.xml file located in the project's folder. Check if the new dependencies' repository already exists in our 
list of repositories. If that's the case, search for the dependency block related to that repository - there are comments
above these blocks indicating that.

If there is no such repository tag, add it.
New repository tags need to be added in this format: `betonquest-<repoName>-repo`.
Then add a new dependency block for that repository. There needs to be a comment above that dependency block that indicates
which repository holds this dependency. Take a look at the other blocks for guidance.

## Setting up the mirror for the new repo
We have configured our Maven project to speed our builds up using mirror repositories.
When adding new repositories we need to add them as mirrors to our Maven repository. As long as they are not added, your
local Maven build will fail. We will add the repository as a mirror when you open your pull request on GitHub.  

However, to test your changes you need to compile. 
Therefore, you need to bypass the mirrors so your dependency will be downloaded from the original repository.
There are three ways do this:

The first option is to temporarily add the following to the `mirrorOf` entry in the file
`.mvn/settings.xml`. This allows Maven to find the new repo as it is excluded from our mirror repo.  
```xml
      <mirrorOf>*,!betonquest-repoName-repo</mirrorOf>
```

Second option, you add the following to the command line when executing Maven to disable our mirrors.
````
-DskipProjectSettings=true
````

Third option, you ask us on GitHub (Issue or PullRequest) to add the new repository to our mirrors.
