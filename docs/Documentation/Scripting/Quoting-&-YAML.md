---
icon: fontawesome/solid/quote-right
---
## Quoting

Sometimes it is important to pass an argument that contains spaces or even a newline as an argument.
For those cases you can use quotes.

```YAML title="Quoting examples"
actions:
  multiline: "notify \"This is the first line.\nAnd here is the second line!\"" #(1)!
  quotes_in_quotes: 'notify "And he said: \"I have to tell you something!\""' #(2)!
  backslash: notify "\\o/" #(3)!
```

1. This is the first line.<br>And here is the second line!
2. And he said: "I have to tell you something!"
3. \o/

## YAML

### Using YAML multiline syntax

Very long instructions can be hard to read, but to improve readability there is a YAML feature that allows you to write
easily readable formatted text that will work perfectly fine with instructions.

```YAML title="Folded multi-line block example"
actions:
  long_text: >-
    notify
    This is a very long text.
    It will still be displayed as one single line in chat,
    no matter where you insert a newline.
    Even combined with "quoting
    there will be no newline" unless you "use a double linebreak,"
    
    as that is interpreted as a normal newline by YAML."
```

There is also an excellent reference for [YAML Multiline](https://yaml-multiline.info/) written by
[Wolfgang Faust](https://www.wolfgangfaust.com/).
