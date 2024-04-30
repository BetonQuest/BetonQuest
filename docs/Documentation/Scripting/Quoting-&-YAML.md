---
icon: fontawesome/solid/quote-right
---
## Quoting

Sometimes it is important to pass an argument that contains spaces or even a newline as an argument.
For those cases you can use quotes.

```YAML
events:
  multiline: notify "This is the first line.\nAnd here is the second line!" #(1)!
  quotes_in_quotes: 'notify "And he said: \"I have to tell you something!\""' #(2)!
  backslash: notify "\\o/" #(3)!
```

1. This is the first line.<br>And here is the second line!
2. And he said: "I have to tell you something!"
3. \o/
