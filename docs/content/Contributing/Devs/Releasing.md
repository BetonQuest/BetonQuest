#Releasing

BetonQuest has a custom build pipeline configured with GitHub('s) actions.     
__**You need to follow all of the following steps for each release otherwise the build pipeline will break!**__

-**Step zero:** Prerequisites.
Decide if the current version number in all of the files (e.g. pom.xml) is the actual version number that should be used for that release.
Use the [Semantic Versioning](https://semver.org/) to do so.

-**Step one:** Build a release.
You can create a release by tagging a commit with a version tag. A version tag needs to 
Tag the successfully commit with the version from the pom.xml

- Post-Release
pom.xml set new version 
pom.xml properties version
changelog.md add unreleased as the template suggest
replace version tag and date + copy template

```CHANGELOG
## [Unreleased] - ${current-date}
### Added
- Condition 'wand' can now have an option 'amount'
### Changed
- Items for HolographicDisplays are now defines in items.yml
- Command 'bq rename' can now be used for globalpoints
### Deprecated
### Removed
- Removed Deprecated Exceptions
### Fixes
- Renaming an NPC will not cause an NPE for a NPC Hologram
- Objective 'craft' now supports shift-clicking
### Security   
```