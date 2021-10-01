This page describes general rules about formatting and what Markdown syntax to use.
##Links

###Internal

Links to internal pages can be opened in the same tab. This works with the normal markdown link syntax:

``` linenums="1"
Click the [highlighted words](Contributing.md).
```
Result: Click the [highlighted words](../Overview.md).

###External
Links to external sites must be opened in new tabs using this html code:

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

##Lists

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
A single line may only have 170 characters. Please wrap at 121 though.

##File names

Replace all spaces in file and folder names with `-`!

##Lists

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
A single line may only have 170 characters. Please wrap at 121 though.                                                  



















