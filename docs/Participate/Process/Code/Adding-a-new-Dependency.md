---
icon: octicons/package-dependencies-16
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

## Finishing up
We speed our builds up using our own mirror repository.
If you add new repositories, we also need to add it as mirror to our maven repository.
Normally we do this, when you open your PullRequest on GitHub.  

If you need to exclude your newly added dependency from the mirrors (because we did not add it yet),
so it will be downloaded from the original repository, you have three options:

First option, you temporarily add the following to the `mirrorOf` entry in the file
`.mvn/settings.xml` to only exclude the new repository:
```xml
      <mirrorOf>*,!betonquest-repoName-repo</mirrorOf>
```

Second option, you add the following to the command line when executing maven to disable our mirrors
````
-DskipProjectSettings=true
````

Third option, you ask in GitHub (Issue or PullRequest) to add the new repository to our mirrors.
