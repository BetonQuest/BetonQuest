---
icon: material/cards-variant
---
Before you make a commit, you should keep in mind that you need to add a changelog entry.

There are two different types of changelog that are maintained within the project:

* [User Changelog](../../Documentation/CHANGELOG.md) - contains user-facing changes
* [Developer Changelog](../../API/CHANGELOG.md) - contains developer-facing api changes

Both are located at the root of the project.

## User Changelog

We have 6 categories in the CHANGELOG.md file for each version.
These are general rules:

* Write user-friendly entries - they are the ones that read the changelog after all.
* Do not repeat the heading for individual entries 
```md hl_lines="3"
Added:
  * {--added--} new conversation style: Hologram
  * new conversation style: Hologram
```
* Mark actions, objectives etc. names with `` ` `` (backticks) around them.

Here is a breakdown of what belongs in each section:

??? info "Added"
    Do not write what class or file was added, describe the feature you added.

    ```MD
    Added:
      - action `teleport` allows teleportation of players
      - Citizens action `movenpc` makes it possible to let a NPC walk to a specific location
    ```

??? info "Changed"
    Give qualified information that indicates what the user may have to be aware of.
    Do not write `action x has now argument y` - that is a new feature and belongs to `Added`. Only add to this section
    if there are changed _behaviours_.

     ```MD
     Changed:
       - action `teleport` now first checks if another plugin canceled the action
       - german translations have been updated
     ```

??? info "Deprecated"
    List things that have been marked for removal. Also mention possible replacements.

     ```MD
     Deprecated:
       - action `message` will be deleted, use the `notify` action instead
       - Minecraft recently replaced material ids with namespaces. Update your items accordingly, ids will stop working soon 
     ```

??? info "Removed"
    After something has been marked for removal in the category `Deprecated` it will end up here eventually.
    Repeat possible replacements.

     ```MD
     Removed:
       - `message` action, use the `notify` action instead
       - old material syntax, use material namespaces instead
     ```

??? info "Fixes"
    Solved bugs are listed in this category.
    Let the users know what the bug did, so they know if they were affected.
    Mention if the fix changed a behaviour.
  
    ```MD
    Fixes:
      - action `notify` did not resolve placeholders correctly
      - Citizens action `movenpc` is now more robust combined with other actions like `stopnpc` and `teleportnpc`
        - you may need to reduce the distance beetween waypoints
    ```

??? info "Security"
    If there was a security issue, you write it down here. It's nearly the same as the category `Fixes`.
    But if something can be abused to effect server security or performance, 
    you keep the way how it can be abused secret.  
    **DO NOT LEAK EXPLOITABLE SECURITY ISSUES!**

     ```MD
     Security:
       - the take action is now threadsafe
       - a deadlock in conversations was fixed
     ```

## Developer Changelog

We generally have 4 categories in the API-CHANGELOG.md file for each version further separated into `API` and `Library`.
The categories `Fixes` and `Security` are considered redundant for most versions and are therefore not included by default.
These are general rules:

* Write developer-friendly entries - they are the ones that read the changelog after all.
* Be concise and clearly state the targeted interfaces or classes
* Do not repeat the heading for individual entries
```md hl_lines="3"
Added:
  * {--added--} `Functions` interface to access functions defined in the user script
  * `Functions` interface to access functions defined in the user script
```
* Mark interface and class names with `` ` `` (backticks) around them.

??? info "Added"
    Do write what class or method was added and describe it shortly.

    ```MD
    Added:
      - `Functions` interface to access functions defined in the user script
      - `BetonQuestApi::functions` to retrieve the `Functions` instance
    ```

??? info "Changed"
    Give qualified information that indicates what the developer may have to be aware of.
    Try to be as concise as possible. Only include changes that affect developers using the API; 
    implementation details of the library are not relevant if the behavior is unchanged.

     ```MD
     Changed:
       - constructor of `FallbackConfigurationSection` from `public` to `protected`
       - `Someclass::myMethod` to `Someclass::myMethodName`
     ```

??? info "Deprecated"
    List things that have been marked for removal. Also mention possible replacements or reasons for deprecation.

     ```MD
     Deprecated:
       - the static `BetonQuest.getInstance()` method is deprecated for api retrieval
       - `InstructionParts` as old api that is kept for compatibility reasons
     ```

??? info "Removed"
    After something has been marked for removal in the category `Deprecated` it will end up here eventually.
    Repeat possible replacements, clearly state the classes or methods that have been removed.

     ```MD
     Removed:
       - `Someclass::myMethod` has been removed as by deprecation since version 1.2.3
       - `OldApi` has been removed as it is no longer used; use `NewApi` instead
     ```


---
## Next Steps
Continue with [Submitting Changes](Submitting-Changes.md) if all your changes are finished.
But maybe you are not finished yet, and you want to go back to change [Code](./Code/Workflow.md) or [Docs](./Docs/Workflow.md).
