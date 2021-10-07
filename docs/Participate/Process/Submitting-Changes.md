The last step of the contributing process is to submit your changes. This is done via a Pull Request on GitHub. 
A Pull Request basically means that you ask us to pull your changes into our codebase. Let's create one!

# Creating a Pull Request


### Did you...

??? success "... test your changes?"
    Always test your changes in-game! Keep in mind, that there could be some special cases in your code that need to be
    tested specifically.

??? success "... update the changelog?"
    You should add a short and user-friendly description of your changes here.
    We have 6 categories in the CHANGELOG.md file for each version.
    In general do not repeat a keyword like `Added` and mark events, objectives and so on with `` ` `` around it.
    
    Here is a breakdown of what belongs in each section:

    ??? info "Added"
        Do not write what class or file was added, describe the feature you added.

        !!! example
            ```MD
            - event `teleport` allows teleportation of players
            - Citizens event `movenpc` makes it possible to let a NPC walk to a specific location
            ```

    ??? info "Changed"
        Give qualified information that indicates, what the users may have to be aware of.
        Do not write `event x has now argument y`, that is a new feature and belongs to `Added`,
        write about changed behaviours.
    
        !!! example
            ```MD
            - event `teleport` now first check if an other plugin canceled the event
            - german translation has been updated
            ```
    
    ??? info "Deprecated"
        Mention things that have been marked for being removed. Also mention a replacement, if there is one.
    
        !!! example
            ```MD
            - event `message` will be deleted, use the `notify` event instead
            - Minecraft removed the old material syntx, therefore it will not be possible anymore in a future versions
            ```
    
    ??? info "Removed"
        After something has been marked for removal in the category `Deprecated` it will end up here eventually.
        Repeat possible replacements.
    
        !!! example
            ```MD
            - event `message` was deleted, use the `notify` event instead
            - Minecraft removed the old material syntax
            ```
    
    ??? info "Fixes"
        Solved bugs are listed in this category.
        Let the users know what the bug did, so users know if they were affected.
        Mention if the fix changed a behaviour.
    
        !!! example
            ```MD
            - event `notify` did not resolve variables correctly
            - Citizens event `movenpc` is now more robust combined with other events like `stopnpc` and `teleportnpc`
            - you may need to reduce the distance beetween waypoints
            ```
    
    ??? info "Security"
        If there was a security issue, you write it down here. It's nearly the same as the category `Fixes`.
        But if something can be abused to effect server security or performance, 
        you keep the way how it can be abused secret.  
        DO NOT LEAK CRITICAL SECURITY ISSUES!
    
        !!! example
            ```MD
            - the take event is now threadsafe
            - a deadlock in conversations was fixed
            ```
    
??? success "... update the documentation?"
    You need to adjust the documentation everytime you changed, added or removed any feature. Remember: Things that 
    aren't documented don't exist to the user. There can also be other reasons why you should update the documentation,
    like writing a new tutorial or adjusting a bad example.

??? success "~~... adjust the ConfigUpdater?~~"
    Deprecated, we are working on a complete rework of the updater. Therefore, this is not needed.

??? success "... solve all TODOs?"
    You shouldn't have any TODOs in our code because it indicates that your changed code is not finished. Unfinished
    code is not added to the plugin.
    If you think about the different types of TODOs, you have things like:
    - `FIXME`: You should really fix it or open an issue on GutHub
    - `DEPRECATED`: just deprecate it with `@Deprecated` and document it in the javadocs
    - `Auto-gernerated code`: implement something, don't leave this TODO there
    - `NOT IMPLEMENTED`: implement it or leave it empty

??? success "... remove any commented out code?"
    We use the version control syt, and it will remember any code. So don't event start to check in code, that is commented out!

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
