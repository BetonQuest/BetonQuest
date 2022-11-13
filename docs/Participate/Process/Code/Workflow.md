---
icon: octicons/workflow-16
---
# Changing Code

Make sure to [set up the project](../../Setup-Project.md) before doing this step. 
You should always [create a new branch](../Create-a-new-Branch.md) everytime you develop a new feature,
fix a bug or make other changes.

## Make changes
Now go ahead and make your changes. Take a look at the sub-pages of this page for more information about specific topics.

## Maven verify
After you made changes, you should also run `mvn verify` to check our requirements.
If you have problems solving issues with our requirements there is also a page
[Checking Requirements](Checking-Requirements.md), that you will probably read later.

??? success "Improve Build Speed"
    If `mvn verify` takes too long, and you just want a jar, you want to build without checking our requirements.
    You can execute `mvn package` instead of `mvn verify` for that.
    But don't forget, you need to successfully run `mvn verify`, before you make a pull request on GitHub!

### Maven profiles
We have some maven profiles, that you can use to change the behavior of `mvn verify` and `mvn package`.
For you the two profiles `Test-All` and `Test-None` are the most important ones.
If you run `mvn verify` or `mvn package` normally it will only run a selected scope of our tests.

You can use the `Test-None` profile to skip all tests, to get a big speedup improvement.
Run `mvn verify -PTest-None` or `mvn package -PTest-None` or activate the profile in your IDEs maven tab on the right side.
This will skip all tests. This is not really recommended, but if you want to build a jar,
and you don't want to wait for the tests, you can use this profile.

The `Test-All` profile will run all tests, even the ones that are normally skipped.
Run `mvn verify -PTest-All` or `mvn package -PTest-All` or activate the profile in your IDEs maven tab on the right side.
This will run all tests. This is not really recommended, but if you did a lot of big changes,
may affect the whole project, you can use this profile to make sure that everything is working.

There are more profiles, but you don't need to know them for now.

??? success "The other Profiles"
    There are other profiles prefixed with `Test-`.
    You can use them to select a specific scope of tests that you want to run, after changed code of that scope.
    
    There is also a profile `Skip-Verification`, that will skip all verifications.
    This is onöy used in the build pipeleine prevent suplicate executions of verifications.
    

## Commit

After you made changes, don't forget to run `mvn verify` again.
You should also add a [changelog](../Maintaining-the-Changelog.md) entry at this point.

You need to commit your changes once they are done.
You can do this with
[IntelliJ's Git integration](https://www.jetbrains.com/help/idea/commit-and-push-changes.html).

**Here are a few tips how to make good commits:**

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
If you also want to adjust the documentation switch to [Changing Docs](../Docs/Workflow.md).
Once you are done with all changes, continue with [Maintaining Changelog](../Maintaining-the-Changelog.md)
In case you already did that: Continue with [Submitting Changes](../Submitting-Changes.md).  

