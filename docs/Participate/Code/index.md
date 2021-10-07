After you done [Setup Project](../Process/Setup-Project.md) and before you start changing code you should create a new branch 
everytime, you want to develop a new feature, fix a bug or making other changes.


## Maven verify
Before you [make changes](#make-changes) you should run `mvn verify` as explained
[here](../Process/Setup-Project.md#building-the-plugin-jar) to ensure, the plugins successfully build, before you made any changes.

After you made changes, you should also run `mvn verify` to check our requirements.
If you have problems solving issues with our requirements there is also a page
[Checking Requirements](../Process/Checking-Requirements.md), that you will probably read later.

??? success "Improve Build Speed"
    If `mvn verify` take too long, and you just want a jar, you want to build without checking our requirements.
    You can execute `package` instead of `verify` for that.
    But don't forget, you need to successfully run `verify`, before you make a pull request on GitHub!

## Make changes
Here we will now look at what you need to fulfill, if you change code, and you want to [Submit the Changes](../Process/Submitting-Changes.md).


## Commit

