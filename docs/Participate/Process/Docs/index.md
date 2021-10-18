## Live Preview
Run this command in IntelliJ's terminal window (at the bottom) to start a live preview of the documentation.
It will be available on 
<a href="http://127.0.0.1:8000/" target="_blank">127.0.0.1:8000</a>
by default.

```bash linenums="1" 
mkdocs serve
```

??? info "Hosting on your entire local network"
    You can also execute this variation to host the website in your local network.
    This can be useful for testing changes on different devices but is not needed for most tasks.
    Make sure the hosting device's firewall exposes the port 8000.
    ```BASH linenums="1"
    mkdocs serve -a 0.0.0.0:8000
    ```

You should work with the live preview as the documentation does not just contain plain markdown,
there are many custom elements which are only visible in the preview.
The preview updates whenever you click outside of IntelliJ or trigger a file save.


We use the [Material for MkDocs theme](https://squidfunk.github.io/mkdocs-material/) for our documentation.
Check [their documentation](https://squidfunk.github.io/mkdocs-material/) to see all custom elements and features.



## Make changes
Now go ahead and make your changes. Take a look at the sub-pages of this page for more information about how to write
good documentation.

## Commit
You need to commit your changes once they are done.
You can do this with
<a href="https://www.jetbrains.com/help/idea/commit-and-push-changes.html" target="_blank">IntelliJ's Git integration</a>.

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
## Where to Continue?
If you also want to adjust the code switch to [Changing Code](../Code/index.md).
Once you are done with all changes, continue with [Maintaining Changelog](../Maintaining-the-Changelog.md).
In case you already did that: Continue with [Submitting Changes](../Submitting-Changes.md).  
