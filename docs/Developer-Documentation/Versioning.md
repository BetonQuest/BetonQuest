##BetonQuest Versioning

These are the build types that are used in the build pipeline:  
1. Version tags `v*` (In the official repository)  
2. Commits to `master` or branches called `master_v*` (In the official repository)  
3. Commits to other branches (or repos) and Pull requests  
4. Local builds  

These result in a versioning like this:  
1. 1.12.0  
2. 1.12.0-DEV-1  
3. 1.12.0-DEV-ARTIFACT-5522  
4. 1.12.0-DEV-UNOFFICAL  

The output jar does not contain the version in its name due to limits with the Spigot updater.
Another reason is to make it clear which one of the buidpipeline output jars is the correct one for users.