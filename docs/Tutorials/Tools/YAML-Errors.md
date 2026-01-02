---
icon: simple/yaml
---
# Handling YAML Syntax Errors

What if you make a YAML mistake? How can you find it?

<div class="grid" markdown>
!!! danger "Requirements"
    * [YAML Basics](../Getting-Started/Basics/YAML-Basics.md)

!!! example "Related Docs"
    * No related documentation
</div>

## Example YAML Errors

Let me show you an example of a small quest with a few typical YAML errors. You might be able to see that something is off.
```YAML title="Example Quest with YAML Errors" linenums="1"
conversations:
  Jack:
    NPC_options:
      completeQuest:
       text: "Hello, how are you?"
       conditions: !hasEnoughFish

actions:
  giveFishObj "objective add fishObj"
  notifyPlayer: 'notify You've completed the quest!'
  addTag: "tag add enoughFish"

```

You will notice that two actions in the actions section are written in green instead of blue.
That's because of a YAML Syntax error.
Do you already see the mistake here? It is simple: There is a colon (`:`) missing after
the key `giveFishObj` in line 9. Because of the missing colon YAML will fail to parse this file.

But now let's have a look at the same file in VSCode. The YAML Syntax extension will clearly highlight the error:

![yaml errors 1](../../_media/content/Tutorials/VSCode-Setup/yaml_errors_1.png)

If you hover over the error, you will see more information:

![yaml errors 2](../../_media/content/Tutorials/VSCode-Setup/yaml_errors_2.png)

Whilst these are quite technical and hard to understand, the highlighting will clearly show you where errors need to be fixed.

Let's have a look at the condition `!hasEnoughFish` in the conversations part:

![yaml errors 3](../../_media/content/Tutorials/VSCode-Setup/yaml_errors_3.png)

This will give us an _unresolved tag: ..._ error because special characters like the exclamation mark (`!`) 
cannot be written without surrounding (`" "`) double quotes.

Another common mistake is to use single quotes to surround a value and then also use it inside the value itself
like this:

![yaml errors 4](../../_media/content/Tutorials/VSCode-Setup/yaml_errors_4.png)

Instead, the line should be written like this: `notifyPlayer: {++"++}notify You've completed the quest!{++"++}`
**To prevent those errors we highly recommend to always use double quotes.**
