After you done [Setup Project](./Setup-Project.md) and before you start changing code you should create a new branch 
everytime, you want to develop a new feature, fix a bug or making other changes.

## Create a new Branch
A new branch should always be created from an up to date `master` branch.
For that you [added the BetonQuest repositry](./Setup-Project.md#adding-remote-repository) `upstream`.
Now you click on your current branch in the right bottom corner, probably `master`.
Then you click on `upstream/master` and click on `New Branch from Selected...`.
Now give the branch a name that makes sense and click `CREATE`.

## Maven verify
Before you [make changes](#make-changes) you should run `mvn verify` as explained
[here](./Setup-Project.md#building-the-plugin-jar) to ensure, the plugins successfully build, before you made any changes.

After you made changes, you should also run `mvn verify` to check our requirements.
If you have problems solving issues with our requirements there is also a page
[Checking Requirements](./Checking-Requirements.md), that you will probably read later.

??? success "Improve Build Speed"
    If `mvn verify` take too long, and you just want a jar, you want to build without checking our requirements.
    You can execute `package` instead of `verify` for that.
    But don't forget, you need to successfully run `verify`, before you make a pull request on GitHub!

## Make changes
If you run `mvn verify` there is a bunch of automatic requirement checks,
but there are also some you have to care on our own manually.
Here we will now look at what you need to fulfill, if you change code,
and you want to [Submit the Changes](./Submitting-Changes.md) beside the automatic requirement checks.

??? success "... test your changes?"
    If you make changes, you should first test your changes.
    Keep in mind, that you should also think about edge cases in your implementation that need to be tested.

??? success "... update the changelog?"
    You should add a short and user-friendly description of your changes here.
    We have 6 categories in you CHANGELOG.md file for each version.
    In general do not repeat a keyword like `Added` and mark events, objectives and so on with `` ` `` around it.
    ??? info "Added"
        Do not write what class or file was added, describe the feature you added.
        !!! example
            ```MD
            - event `teleport` allows teleportation of players
            - Citizens event `movenpc` makes it possible to let a NPC walk to a specciffic location
            ```
    ??? info "Changed"
        Give qualified information that indicate, what the users may have to be aware of.
        Do not write `event x has now argument y`, that is a new feature and belongs to `Added`,
        write about changed behaviours.
        !!! example
            ```MD
            - event `teleport` now first check if an other plugin canceled the event
            - german translation has been updated
            ```
    ??? info "Deprecated"
        Mention things that have been marked for being removed.
        But also mention the replacement, if there is one. 
        !!! example
            ```MD
            - event `message` will be deleted, use the `notify` event instead
            - Minecraft removed the old material syntx, therefore it will not be possible anymore in a future version
            ```
    ??? info "Removed"
        Normally after something was in the category `Deprecated`,
        it will be removed and if this is the case, it will be added here again.
        Mention again a possible replacement if possible.
        !!! example
            ```MD
            - event `message` was deleted, use the `notify` event instead
            - Minecraft removed the old material syntx
            ```
    ??? info "Fixes"
        If you managed to solve a bug, you write it down in this category.
        Mainly say, what the bug was, so users know if they were effected.
        If the fix also changed a behaviour, mention that too.
        !!! example
            ```MD
            - event `notify` did not resolved variabled correctly
            - Citizens event `movenpc` is now more robust combined with other events like `stopnpc` and `teleportnpc`
              - you may need to reduce the distance beetween waypoints
            ```
    ??? info "Security"
        If there was a security issue, you write it down here. It's nearly the same as the category `Fixes`.
        But if something can be abused to effect server security or performance,
        you keep the way how it can be abused secret.  
        DO NOT LEEK CRITICAL SECURITY ISSUES!
        !!! example
            ```MD
            - the take event is now threadsafe
            - a deadlock in conversations was fixed 
            ```

??? success "... update the documentation?"
    You need to adjust the documentation, everytime you changed, added or removed any feature.
    There could also be other reasons, why you should or want to change the documentation,
    like writing a new tutorial or adjust a wrong example.

??? success "~~... adjust the ConfigUpdater?~~"
    Deprecated,  
    currently we have planned a complete rework, and therefore this is not needed at the moment.

??? success "... solve all TODOs?"
    We do not want any TODOs in our code, because it indicates, that your changed code is not finished.
    If you think about the different types of TODOs, you have things like:
    - `FIXME`: You should really fix it or open an issue on GutHub
    - `DEPRECATED`: just deprecate it with `@Deprecated` and document it in the javadocs
    - `Auto-gernerated code`: implement something, don't leave this TODO there
    - `NOT IMPLEMENTED`: implement it or leave it empty

??? success "... remove any commented out code?"
    We do have git, and it will remember any code. So don't event start to check in code, that is commented out!

??? success "... add debug messages?"
    In general, BetonQuest already have a lot debug. So normally you do not need anything here.
    But in case you have something that is new or code that is more complicated, you should add debug messages.

??? success "... clean the commit history?"
    You did multiple commits, that may look like `progress fixing the bug`?  
    Or you did a commit with a wrong description?  
    Then you now need to do an `Interactive Rebase`.
    With this you change the history of commits.
    Here is the general <a href="https://git-scm.com/book/en/v2/Git-Tools-Rewriting-History" target="_blank">git documentation</a>
    for changing the history. But if you use IntelliJ, you should read [Edit Git project history](https://www.jetbrains.com/help/idea/edit-project-history.html).

## Commit
After you made changes, don't forget to run `mvn verify`before you commit and push the changes.

For a commit you should keep in mind, that you want to make as atomic (small) commits as possible.
This means, if you simply fixed a bug, but during this you cleaned the code of a class,
you should separate this two changes if possible into two separate commits.
With other words, don't mix up different changes. 
This makes it much easier to track of changes afterwards.

The next thing is the message for your commit.
If you fixed a bug, don't write `fixed deadlock`.
You should give more qualified information like `fixed deadlock, when a huge amount of conditons are cheked at the same time`.
Also, you don't should write `cleaned the code`, instead you should write things like this `renamed methods and variables`.  
