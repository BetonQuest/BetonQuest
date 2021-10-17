# Changing Docs
Run this command in IntelliJ's terminal window at the bottom to start a live preview of the documentation.
It will be available on [127.0.0.1:8000](http://127.0.0.1:8000/) by default.
You should work with the live preview as the documentation is not just markdown, there are many custom elements.
The preview updates whenever you click outside of IntelliJ or trigger a file save.

```bash 
mkdocs serve
```

We use the [Material for MkDocs theme](https://squidfunk.github.io/mkdocs-material/) for our documentation.
Check [their documentation](https://squidfunk.github.io/mkdocs-material/) to see all custom elements and features.

??? info "Hosting on your entire local network"
    You can also execute this variation to host the website in your local network.
    This can be useful for testing changes on different devices but is not needed for most tasks.
    Make sure the hosting device's firewall exposes the port 8000.
    ```BASH
    mkdocs serve -a 0.0.0.0:8000
    ```




---
## Where to Continue?
If you also want to adjust the code switch to [Changing Code](../Code/index.md).
Once you are done with all changes, continue with [Maintaining Changelog](../Maintaining-Changelog.md)
In case you already did that: Continue with [Submitting Changes](../Submitting-Changes.md).  
