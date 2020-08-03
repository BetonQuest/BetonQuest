##Setup 

##Quick Edits

Quick changes can be made by clicking the pencil icon at the top of each page. This lets you edit the docs directly in 
your browser. The [Guidelines](Guidelines.md) still apply!
 
##Advanced Edits

More advanced edits can be done with <a href="https://www.jetbrains.com/idea/download/" target="_blank"> IntelliJ</a> 
or any other IDEA. 
This guide will show the process with IntelliJ and includes some neat tricks that are exclusive to IntelliJ.
 
Clone in IntelliJ and select the "Docs" project scope.
[IMG]

Create a new branch before you start editing anything.

All the files are markdown files. Markdown is the stuff you partially know from discord text highlighting.
`**Bold text**`  **result**
`_Underlinded text_` _result_

The final docs are generated using mkdocs-material!

Make sure <a href="https://www.python.org/downloads/" target="_blank">Python3</a> is installed on your local system.
If you use Python for more thn this you might want to look into <a href="https://docs.python.org/3/library/venv.html" target="_blank">python virtual environments</a> to avoid conflicts.
This should not be the case for all non-devs though.

Install all other dependencies by entering `pip install -r requirements.txt` in the console.

### See your changes live
Mkdocs enables you to create a website that shows you your changes while you make them.
To start an http document server on `http://127.0.0.1:8000` execute:

```
mkdocs serve
```
