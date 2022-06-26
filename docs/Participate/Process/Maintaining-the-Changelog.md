---
icon: material/cards-variant
---
Before you make a commit, you should keep in mind, that you need to add a changelog entry.

We have 6 categories in the CHANGELOG.md file for each version.
These are general rules:

* Write user-friendly entries - they are the ones that read the changelog after all.
* Do not repeat the heading for individual entries 
```
Added:
  * {--added--} new conversation style: Hologram
  * new conversation style: Hologram
```
* Mark events, objectives etc. names with `` ` `` around them.

Here is a breakdown of what belongs in each section:

??? info "Added"
    Do not write what class or file was added, describe the feature you added.

    ```MD
    Added:
      - event `teleport` allows teleportation of players
      - Citizens event `movenpc` makes it possible to let a NPC walk to a specific location
    ```

??? info "Changed"
    Give qualified information that indicates what the user may have to be aware of.
    Do not write `event x has now argument y` - that is a new feature and belongs to `Added`. Only add to this section
    if there are changed _behaviours_.

     ```MD
     Changed:
       - event `teleport` now first checks if another plugin canceled the event
       - german translations have been updated
     ```

??? info "Deprecated"
    List things that have been marked for removal. Also mention possible replacements.

     ```MD
     Deprecated:
       - event `message` will be deleted, use the `notify` event instead
       - Minecraft recently replaced material ids with namespaces. Update your items accordingly, ids will stop working soon 
     ```

??? info "Removed"
    After something has been marked for removal in the category `Deprecated` it will end up here eventually.
    Repeat possible replacements.

     ```MD
     Removed:
       - `message` event, use the `notify` event instead
       - old material syntax, use material namespaces instead
     ```

??? info "Fixes"
    Solved bugs are listed in this category.
    Let the users know what the bug did, so they know if they were affected.
    Mention if the fix changed a behaviour.
  
    ```MD
    Fixes:
      - event `notify` did not resolve variables correctly
      - Citizens event `movenpc` is now more robust combined with other events like `stopnpc` and `teleportnpc`
        - you may need to reduce the distance beetween waypoints
    ```

??? info "Security"
    If there was a security issue, you write it down here. It's nearly the same as the category `Fixes`.
    But if something can be abused to effect server security or performance, 
    you keep the way how it can be abused secret.  
    **DO NOT LEAK EXPLOITABLE SECURITY ISSUES!**

     ```MD
     Security:
       - the take event is now threadsafe
       - a deadlock in conversations was fixed
     ```

---
## Where to Continue?
Continue with [Submitting Changes](Submitting-Changes.md) if all your changes are finished.
But maybe you are not finished yet, and you want to go back to change [Code](./Code/Workflow.md) or [Docs](./Docs/Workflow.md).
