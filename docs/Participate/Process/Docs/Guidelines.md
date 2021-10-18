You need to follow these rules in order to contribute to the docs. They are important for a good user experience and provide
a consistent baseline for other contributors to work with.

## Markdown Formatting Conventions
Unfortunately, there are different ways to format text in Markdown. Please use the syntax outlined here:

### Bold
**Bold text** is surrounded by two asterisks on each side: ``**Bold text**``

### Italic
_Italic text_ is surrounded by one underscore on each side: ``_Italic text_``

### Lists

Lists must be declared as such:

```
* Top Level
    - Second Level
    - Second Level
* Another Top level
```

Result:

* Top Level
  - Second Level
  - Second Level
* Another Top level

##Line length
All lines must be wrapped after 121 character. You can exceed this limit by a few characters where resonable.
Tables and other special syntax are exempt from this rule.

##Links

###Internal

Links to internal pages can be opened in the same tab. This works using Markdown link syntax:

``` linenums="1"
Click the [highlighted words](Contributing.md).
```
Result: Click the [highlighted words](../../Overview.md).

###External
Links to external sites must be opened in new tabs using this HTML code:

``` HTML linenums="1"
<a href="https://betonquest.org/" target="_blank">Clickable text that opens a new tab</a>
```
Result: <a href="https://betonquest.org/" target="_blank">Clickable text that opens a new tab</a>



##Displaying (YAML) code

You must use codeboxes with the specific language set in the header (`YAML` in this example).
You also have to enable line numbers by adding the `linenums="1"` argument. 
``` linenums="1"
 ``` YAML linenums="1"
 use: "codeboxes for code"
 ```
```

Result:
``` YAML linenums="1"
use: "codeboxes for code"
```
### Refering to YAML elements in written text

An example of this would be to reference an event name in an explanation.

``` YAML linenums="1"
The event `someEvent` prints a message to the player!
```

Result:
The event `someEvent` prints a message to the player!

##File names

Replace all spaces in file and folder names with `-`!

