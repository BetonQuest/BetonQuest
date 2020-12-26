#Releasing

This project uses a custom buildpipeline that utilizes GitHub('s) actions.     

__**You need to follow all of the following steps for each release otherwise the build pipeline will break!**__

###Step 1: Prerequisites

Decide if the current version number in all of the files (e.g. pom.xml) is the actual version number that should be used for that release.
Use [Semantic Versioning](https://semver.org/) to do so.

###Step 2: Build a release

You can create a release by tagging a commit with a version tag. A version tag needs to 
tag the commit that should be a release with the version from the pom.xml(without `SNAPSHOT`).

###Step 3: Post-Release

Set a new version in the pom.xml following the [Semantic Versioning](https://semver.org/) specification.
Then edit the changelog.md in the projects root folder and replace the current `Unreleased` sections title with the just released version and date.
Now copy the template from below into the file.


```CHANGELOG
## [Unreleased] - CURRENT_DATA_HERE
### Added
### Changed
### Deprecated
### Removed
### Fixes
### Security   
```

