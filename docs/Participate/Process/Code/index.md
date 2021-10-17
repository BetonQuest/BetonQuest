# Changing Code
Make sure to [setup the project](../../Setup-Project.md) before doing this step. 
You should always [create a new branch](../Create-a-new-Branch.md) everytime you develop a new feature,
fix a bug or make other changes.


## Maven verify
Before you [make changes](#make-changes) you should run `mvn verify` as explained
[here](../../Setup-Project.md#building-the-plugin-jar) to ensure, the plugins successfully build, before you made any changes.

After you made changes, you should also run `mvn verify` to check our requirements.
If you have problems solving issues with our requirements there is also a page
[Checking Requirements](Checking-Requirements.md), that you will probably read later.

??? success "Improve Build Speed"
    If `mvn verify` take too long, and you just want a jar, you want to build without checking our requirements.
    You can execute `package` instead of `verify` for that.
    But don't forget, you need to successfully run `verify`, before you make a pull request on GitHub!

## Make changes
Now go ahead and make your changes. Take a look at the sub-pages of this page for more information about specific topics. 

## Commit

After you made changes, don't forget to run `mvn verify` again.
You should also add a [changelog](../Maintaining-Changelog.md) entry at this point of progress,
before you commit and push the changes.

A commit needs to be _atomic_ which means it only contains changes that belong together. Large changes
may also be split into multiple commits. This makes it easier to understand your changes.

Example: Originally you just wanted to fix a bug, but you also cleaned the code of the class while doing so.
Now you should separate these two (logically different) changes into two separate commits.
With other words, don't mix up different changes.

Another thing to keep in mind is the commit name and description.
If you fixed a bug, don't write `fixed deadlock`.
You should give more qualified information like `fixed deadlock, when a huge amount of conditons are cheked at the same time`.
Also, you shouldn't write `cleaned the code`, instead you should write things like this `renamed methods and variables`.  

---
## Where to Continue?
If you also want to adjust the documentation switch to [Changing Docs](../Docs/index.md).
Once you are done with all changes, continue with [Maintaining Changelog](../Maintaining-Changelog.md)
In case you already did that: Continue with [Submitting Changes](../Submitting-Changes.md).  

