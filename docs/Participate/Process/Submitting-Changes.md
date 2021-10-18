The last step of the contributing process is to submit your changes. This is done via a pull request on GitHub. 
A pull request basically means that you ask us to pull your changes into our codebase. Let's create one!

## Push your changes
The first step is to push (upload) your changes to your fork on GitHub.
You can do this using
<a href="https://www.jetbrains.com/help/idea/commit-and-push-changes.html#push" target="_blank">IntelliJ's Git integration</a>.


## Creating a Pull Request
There are three ways to create a pull request:

1. If you recently pushed to your fork, you see a `Create Pull Request` hint on our
   [repository page](https://github.com/BetonQuest/BetonQuest).

2. You can go to the [Pull Request](https://github.com/BetonQuest/BetonQuest/pulls) page on our repository and click on
   `New Pull Request`. But then you need to click on `Compare across Forks` and then select your `Head Repository`.

3. You can also click on `New Pull Request` in your fork, then it already compares it to the BetonQuest repository.

Now give the pull request a short but meaningful title.
Then add a description that indicated what you added, removed or changed.
If there are related issues, link them with the keyword `Closes #issue-id-here`,
so the issue is automatically closed when the pull request is merged.

### Did you...
The pull request template contains a list of typical problems. We will check these during the review.
Do not check any of these boxes on GitHub, we will do that during the review. Please control these things though.

??? success "... run Maven verify?"
    The most basic check is [Maven Verify](Code/Checking-Requirements.md). Run it in you IDE and make sure it SUCCEEDS!

??? success "... test your changes?"
    Always test your changes in-game! Keep in mind, that there could be some special cases in your code that need to be
    tested specifically.

??? success "... update the changelog?"
    Don't forget to add a changelog entry for concept every change you made.  
    For more information you should read [Maintaining Changelog](Maintaining-the-Changelog.md).
    
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
    We use the version control syt, and it will remember any code.
    So don't event start to check in code, that is commented out!

??? success "... add debug messages?"
    In general, BetonQuest already have a lot debug. So normally you do not need anything here.
    But in case you have something that is new or code that is more complicated, you should add debug messages.

??? success "... clean the commit history?"
    You did multiple commits, that may look like `progress fixing the bug`?  
    Or you did a commit with a wrong description?  
    Then you now need to do an `Interactive Rebase`. With this you change the history of commits. Here is the general
    <a href="https://git-scm.com/book/en/v2/Git-Tools-Rewriting-History" target="_blank">git documentation</a>
    for changing the history. But if you use IntelliJ, you should read
    [Edit Git project history](https://www.jetbrains.com/help/idea/edit-project-history.html).  
    In case you don't understand what we want, you should read this
    <a href="https://medium.com/@catalinaturlea/clean-git-history-a-step-by-step-guide-eefc0ad8696d" target="_blank">guide</a>
    that explain, why and how you clean the commit history.

### Submit your Pull Request
Now click on `Create Pull Request` to submit your request. 

If you want to share work-in-progress changes to get early feedback, create a "draft pull request".
Click on the arrow next to `Create Pull Request` and then select `Create draft Pull Request`.
This indicates, that this pull request is not finished, and you can click on `Ready for review` once you are ready.

----

Congrats, you are done! Thank you for contributing! :heart:

Keep an eye out for reviews of your pull request.
