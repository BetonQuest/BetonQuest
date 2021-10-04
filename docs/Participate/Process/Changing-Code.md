After you done [Setup Project](./Setup-Project.md) and before you start changing code you should create a new branch 
everytime, you want to develop a new feature, fix a bug or making other changes.

## Create a new Branch
A new branch should always be created from an up to date `master` branch.
For that you [added the BetonQuest repositry](./Setup-Project.md#adding-remote-repository) `upstream`.
Now you click on your current branch in the right bottom corner, probably `master`.
Then you click on `upstream/master` and click on `New Branch from Selected...`.
Now give the branch a name that makes sense and click `CREATE`.

## Maven verify
Before you [make changes](#make-changes) you should run `maven verify` as explained
[here](./Setup-Project.md#building-the-plugin-jar) to ensure, the plugins successfully build, before you made any changes.

After you made changes, you should also run `maven verify` to check our requirements.
If you have problems solving issues with your requirements there is also a page 
[Checking Requirements](./Checking-Requirements.md), that you will probably read later.

If `maven verify` take too long, and you just want a jar, you want to build without checking our requirements.
You can execute `package` instead of `verify` for that.
But don't forget, you need to successfully run `verify`, before you make a pull request on GitHub!

## Make changes
If you make changes, you should first test your changes

... test your changes?
... update the changelog?
... update the documentation?
... adjust the ConfigUpdater?
... solve all TODOs?
... remove any commented out code?
... add debug messages?

## Commit
(... clean the commit history?)
