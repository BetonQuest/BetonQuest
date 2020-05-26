# Contributing to BetonQuest

:+1: First of all, thank you for looking at this file :+1:

### Did you found a bug?

Please describe what you did and what happened. The more details you give the better. Don't worry though, we'll ask questions if we need more information :smile:

If you had any errors in the console with a line containing `pl.betoncraft.betonquest`, post it **inside the Issue**. Simply use three backtick characters (` ``` `) to surround the error (known as a _stack trace_), like this:

    ```
    [today] [Server thread/ERROR]: Some error in BetonQuest
    org.bukkit.event.EventException: null
        at pl.betoncraft.betonquest.BetonQuest.method(BetonQuest.java:123) ~[?:?]
        at some.more.lines...
    ```

**Don't upload it to external services like _hastebin.com_ etc.** Not only such links can expire, it's also difficult to view on mobile devices. It's okay to post errors in the issue itself. Really, it's not spam, it's important information.

Also, it's not necessary to prefix the issue title with tags like `[Feature]` or `[BUG]`, there are colorful labels for this sole purpose.

### You'd like to request a feature?

Great, simply open a new issue. Maybe someday someone will find the time to implement it.


### You want to implement a feature / fix a bug?

If that feature does not yet exist on the issue tracker then open a new issue first. Describe the feature and state that you're going to implement it. That way no-one else will accidentally code it at the same time.

If the feature already has an issue, simply comment there saying that you'd like to implement it. Again, reasoning here is the same.

### You implemented a feature / fixed a bug?

Great, thank you so much. Simply submit a pull request and wait for a code review. After your code is perfected (or immediately, who knows?) we'll merge it into the master.

If you want to automatically close the issue related to your pull request (you should have one, see above), simply add `Fix #100` or `Close #100` to your commit message (replace `100` with the issue number of course).

By the way, if the code you've written changes or adds features to the plugin, please take a bit of time to update the documentation in the `docs` directory. It will let the users immediately see what's new and how to use it.

### You're not sure how to start hacking BetonQuest?

Basic Java knowledge is be required. Additionally, all instructions here assume you know how to use your system's command line. If not, there are plenty of resources on the web, simply search for _command line tutorial_ or something similar.

First of all follow the steps described in the _Getting started_ part of the [README](README.md) to set up BetonQuest's code for development. If the plugin compiles then you could open the code with any editor, modify it and

After you're done you're going to need to import the codebase into your editor. I would suggest using Eclipse or IntelliJ - if you know Java then you're probably familiar with at least one of these two. Visual Studio Code is also a nice, modern alternative.

To get the full potential of your IDE it's a good idea to enable Maven integration - that way it will know more about BetonQuest and will be able to help you with code completion.

If you want to implement features like new events, objectives or conditions you can take a look at existing classes - they will tell you how to structure your own code. Read [Info-for-developers page](docs/Info-for-developers.md) for more explanations of the API.

Since BetonQuest is using Git for version control you'll probably need to know the basics of Git in order to push your code to the GitHub repository. There's a great tool which greatly helps with all Git-related stuff and has awesome tutorials - [GitKraken](https://www.gitkraken.com). Check it out!
