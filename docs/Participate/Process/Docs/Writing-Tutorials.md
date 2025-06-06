---
icon: material/book-education
---

# Writing Tutorials
All tutorials must follow this structure.
An [example for a tutorial series](../../../Tutorials/Getting-Started/Basics/Conversations.md),
that conforms to this standard, can be found in our documentation.

Whenever the tutorial refers to downloadable examples, the `/bq download` feature is meant.
It downloads quest from our [Quest Tutorials repository](https://github.com/BetonQuest/Quest-Tutorials).


## Tutorial Structure
### Intro
* Goal of this tutorial (What is the feature?)
* Requirements
  * Skills from previous tutorials
  * Installed plugins
  * A (downloadable) setup
* Related docs (documentation of the covered features)

??? example "Example"
    ![](../../../_media/content/Participate/Process/Docs/Writing-Tutorials/IntroExample.png)

### Content
It's recommended to split a tutorial into multiple logical steps.
Ideally, once a step has been completed by the user, they should be able to try it out ingame. A step consists of:

* Goal / What and why are we going to do?
* Instruction / How to do it?
* Explanation / How does the config / feature work? This is tightly coupled with the instruction, e.g. using code block annotations.
* (Optional) Downloadable Sample Solution / What to do if it does not work?

Sometimes it may not be practical to provide a downloadable solution for every step. 
Since these downloadable solutions should always contain the progress from previous steps, they can also be provided in a later step.

??? example "Example"
    ![](../../../_media/content/Participate/Process/Docs/Writing-Tutorials/ContentExample.png)

### Outro
* Downloadable Sample Solution for the entire setup
* Summary on what the user learned
* Where to find more information about this feature (Reference & Backlinks, may overlap with the intro's "Related Docs")
* What's next? (Only needed if in a tutorial series)

??? example "Example"
    ![](../../../_media/content/Participate/Process/Docs/Writing-Tutorials/OutroExample.png)

## Download Commands
Whenever you want to provide a downloadable example, you can use the `/bq download` command.
But when you do, always replace the git reference with the placeholder `${ref}`. 
This would then look like this:
````
/bq download BetonQuest/Quest-Tutorials ${ref} QuestPackages /Basics/Conversations/1-DirectoryStructure /tutorialQuest overwrite
````
