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
  long_text: >- #(1)!
    notify
    This is a very long text.
    It will still be displayed as one single line in chat,
    no matter where you insert a newline.
    Even combined with "quoting
    there will be no newline" unless you "use a double linebreak,"
    
    as that is interpreted as a normal newline by YAML."
```

1. Replace newlines with spaces (folded) & No newline at end (strip)

Also, in conversations or other places where you want to define a longer text of multiple lines,
you can use a YAML syntax feature to write easily readable formatted text that will be printed like you wrote it down.

```YAML title="Literal multi-line block example"
text: |- #(1)!
  This is line one.
  This is line two.
          
          You
        can also
  format this using spaces.
```

1. Keep newlines (literal) & No newline at end (strip)

There is also an excellent reference for [YAML Multiline](https://yaml-multiline.info/) written by
[Wolfgang Faust](https://www.wolfgangfaust.com/).
