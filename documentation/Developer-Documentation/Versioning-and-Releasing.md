#Versioning and Releasing

##Versioning
BetonQuest follows the [Semantic Versioning](https://semver.org/) specification.

We adapt the specification with these version types:
* `1.12.0` for git version tags `v*` (in the official repository)
* `1.12.0-DEV-1` for commits to `main` or branches called `main_v*` (in the official repository)
* `1.12.0-DEV-ARTIFACT-5522` for commits to other branches (or repos) and Pull requests
* `1.12.0-DEV-UNOFFICAL` for local builds

Only use the three digits(Major, Minor, Patch) to specify versions in files like the pom.xml. All suffixes are set
by the build-pipeline.

The officially distributed jar does not contain the version in its name due to limits with the Spigot updater.
Therefore, it's named `BetonQuest.jar`. The version can only be seen ingame by using `/q version`.

##Releasing

This project has a custom build-pipeline that utilizes GitHub('s) actions.

###Step 1: Prerequisites

Check out the remote branch or commit, that you want to use for the release.
Make sure that the current version in all the files (pom.xml etc.) is the correct version that should be used for the release.

###Step 2: Build a release

Run the script `./.github/scripts/release.sh`. It leads you through the creation of a release.
If you run into errors you may need to do the script's release steps manually. 
