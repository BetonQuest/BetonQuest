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

After you made changes, don't forget to run `mvn verify` again before you commit and push the changes.

A commit needs to be _atomic_ which means it only contains changes that belong together. Large changes
may also be split into multiple commits. This makes it easier to understand your changes.

Example: Originally you just wanted to fix a bug, but you also cleaned the code of the class while doing so.
Now you should separate these two (logically different) changes into two separate commits.
With other words, don't mix up different changes.

Another thing to keep in mind is the commit name and description.
If you fixed a bug, don't write `fixed deadlock`.
You should give more qualified information like `fixed deadlock, when a huge amount of conditons are cheked at the same time`.
Also, you shouldn't write `cleaned the code`, instead you should write things like this `renamed methods and variables`.  
