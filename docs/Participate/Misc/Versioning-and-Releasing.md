---
icon: material/factory
---
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

The officially distributed jar does not contain the version in its name due to limits with the server update mechanism.
Therefore, it's named `BetonQuest.jar`. The version can only be seen ingame by using `/bq version`.

##Releasing

This project has a custom build-pipeline that utilizes GitHub('s) actions.

###Step 1: Prerequisites

- [x] Accessing the repository through SSH is required. You might wanna use 
[guides from GitHub](https://docs.github.com/en/authentication/connecting-to-github-with-ssh/generating-a-new-ssh-key-and-adding-it-to-the-ssh-agent) to set this up.
- [x] A bash shell capable of Git is required. (especially on Windows, have a look at [ssh-agent](https://docs.github.com/en/authentication/connecting-to-github-with-ssh/working-with-ssh-key-passphrases#auto-launching-ssh-agent-on-git-for-windows))
- [x] A Maven installation is required to enable features like automatic version detection. That should be already installed on your system.
- [x] A GitHub-CLI must be installed to enable features like automatic pull request creation and release date fetching. You can install it from [here](https://cli.github.com/).

###Step 2: Before Releasing

- [x] Check out the remote branch or commit, that you want to use for the release.
- [x] Make sure that the current version in all the files (pom.xml etc.) is the correct version that should be used for the
release. 

###Step 3: Build a release

Run the script `./.github/scripts/release.sh` using some sort of bash shell (e.g. Git Bash) from the root dir of the
project. It guides you through the creation of the release and prepares the next version.  
If you run into errors you may need to do the script's release steps manually or fix the script / your setup. 
