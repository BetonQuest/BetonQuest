---
icon: material/pen
---
# Changing Docs

Make sure to [set up the project](../../Setup-Project.md) before doing this step.
You should always [create a new branch](../Create-a-new-Branch.md) everytime you write new documentation,
fix something or make other changes.

## Live Preview
Run this command in IntelliJ's terminal window (at the bottom) to start a live preview of the documentation.
It will be available on [127.0.0.1:8000](http://127.0.0.1:8000/) by default.

``` bash linenums="1" 
mkdocs serve --livereload # (1)!
```

1. `--livereload` is an optional argument that determines that only changed files will be re-build.
   This drastically decreases build time. However, it may lead to inaccurate navigation within your site.
   Serve without this argument to validate your changes once finished.

You should work with the live preview as the documentation does not just contain plain markdown,
there are many custom elements which are only visible in the preview.
The preview updates whenever you click outside of IntelliJ or trigger a file save.

??? info "Hosting on your entire local network"
    You can also execute this variation to host the website in your local network.
    This can be useful for testing changes on different devices but is not needed for most tasks.
    Make sure the hosting device's firewall exposes the port 8000.
    ```BASH linenums="1"
    mkdocs serve -a 0.0.0.0:8000
    ```

We use the [Material for MkDocs theme](https://squidfunk.github.io/mkdocs-material/) for our documentation.
Check [their documentation](https://squidfunk.github.io/mkdocs-material/) to see all custom elements and features.



## Make changes
Now go ahead and make your changes. Take a look at the sub-pages of this page for more information about how to write
good documentation or [tutorials](./Writing-Tutorials.md). Then come back here to commit and submit your changes.

## Commit
You need to commit your changes once they are done.
You can do this with
[IntelliJ's Git integration](https://www.jetbrains.com/help/idea/commit-and-push-changes.html).

**Here are a few tips how to make good commits:**

All docs-only commits need to be prefixed with `[DOCS]`. This helps to distinguish them from technical commits that have
a very similar name. Additionally, commit names should be short. If needed, provide more detail in the description.

A commit needs to be _atomic_ which means it only contains changes that belong together. Large changes
may also be split into multiple commits. This makes it easier to understand your changes.

Example: Originally you just wanted to fix a spelling mistake, but you also added more examples to a different part 
of the documentation while doing so.
Now you should separate these two (logically different) changes into two separate commits.
With other words, don't mix up different changes.


---
## Next Steps
If you also want to adjust the code switch to [Changing Code](../Code/Workflow.md).
If you already made code changes you should continue with [Maintaining Changelog](../Maintaining-the-Changelog.md).

Otherwise, please continue with [Submitting Changes](../Submitting-Changes.md).  
