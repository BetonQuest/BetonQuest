---
icon: octicons/workflow-16
---
# Changing Code

Make sure to [set up the project](../../Setup-Project.md) before doing this step. 
You should always [create a new branch](../Create-a-new-Branch.md) everytime you develop a new feature,
fix a bug or make other changes.

## Make changes
Now go ahead and make your changes. Take a look at the sub-pages of this page for more information about specific topics.
Then come back here to verify, commit and finally submit your changes.

## Maven verify
After you made changes, you should also run `./mvnw verify` to check our requirements.
If you have problems solving issues with our requirements there is also a page
[Checking Requirements](Checking-Requirements.md), that you will probably read later.

??? success "Improve Build Speed"
    If `./mvnw verify` takes too long, and you just want a jar, you want to build without checking our requirements.
    You can execute `./mvnw package` instead of `./mvnw verify` for that.
    But don't forget, you need to successfully run `./mvnw verify`, before you make a pull request on GitHub!

### Maven profiles
There are some Maven profiles that you can use to change which tests are executed upon `./mvnw verify` and `./mvnw package`.
Some profiles can be used to speed up the build time.
However, you should always run `./mvnw verify` with no enabled profiles before making a pull request on GitHub.

You can use the `Test-None` profile to speed up the build process by skipping all tests.
This can be useful when rapidly developing and testing changes in game. 
Run `./mvnw verify -PTest-None` or `./mvnw package -PTest-None` or activate the profile in your IDE's Maven tab on the right side.

The `Test-All` profile will run all tests, even the ones that are normally skipped.
This is not really recommended, but if you did lots of big changes that
may affect the whole project, you can use this profile to make sure that everything is working.
kRun `./mvnw verify -PTest-All`, `./mvnw package -PTest-All` or activate the profile in your IDE's Maven tab on the right side.

There are more profiles, but you don't need to know them in most cases.

??? info "The other Profiles"
    There are other profiles prefixed with `Test-`.
    You can use them to select a specific scope of tests that you want to run, after changing code in that scope.
    
    There is also a profile `Skip-Verification`, that will skip all verifications.
    It is only used in the build pipeline to skip verifications that were already done in earlier pipeline steps.
    

## Commit

After you made changes, don't forget to run `./mvnw verify` again.
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
## Next Steps
If you also want to adjust the documentation switch to [Changing Docs](../Docs/Workflow.md).
Once you are done with all changes, continue with [Maintaining Changelog](../Maintaining-the-Changelog.md)
In case you already did that: Continue with [Submitting Changes](../Submitting-Changes.md).  

