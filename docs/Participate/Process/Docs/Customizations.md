---
icon: material/folder-wrench
---

We run a customized version of mkdocs-material that allows for some additional features.

## toc_depth per page
You can set the maximum depth of the table of contents per page using the `toc_depth` variable in the page's metadata.
```YAML title="Example"
---
toc_depth: 2
---

# Some Heading 
Page content here...
```
This can be useful for large pages with many headings. 
For example, it is used for the [Integration List](../../../Documentation/Scripting/Building-Blocks/Integration-List.md)
to hide the sub-headings of the individual integrations.

