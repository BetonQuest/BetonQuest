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
Here we will now look at what you need to fulfill, if you change code, and you want to [Submit the Changes](./Submitting-Changes.md).

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
??? success "~~... adjust the ConfigUpdater?~~"
    Deprecated,
    currently we have planned a complete rework, and therefore this is not needed at the moment.

??? success "... solve all TODOs?"
??? success "... remove any commented out code?"
??? success "... add debug messages?"
??? success "... clean the commit history?"

## Commit

