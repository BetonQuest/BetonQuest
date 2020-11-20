##Setup 

##Quick Edits

Quick changes can be made by clicking the pencil icon at the top of each page. This lets you edit the docs directly in 
your browser. The [Guidelines](Guidelines.md) still apply!

All the files are markdown files. Markdown is the stuff you partially know from discord text highlighting.

`**Bold text**`  **result**    
`_Italic text_` _result_

 
##Advanced Edits

More advanced edits can be done with <a href="https://www.jetbrains.com/idea/download/" target="_blank"> IntelliJ</a> 
or any other IDEA. 
This guide will show the process with it and includes some neat tricks that are exclusive to IntelliJ.
 
* Clone in IntelliJ and select the "Docs" project scope.

* Create a new branch before you start editing anything.

Make sure <a href="https://www.python.org/downloads/" target="_blank">Python3</a> is installed on your local system.
If you use Python for more than this you might want to look into
<a href="https://docs.python.org/3/library/venv.html" target="_blank">python virtual environments</a> to avoid conflicts.
This should not be the case for any non-devs though.

You may need to install <a href="https://www.gtk.org/" target="_blank">GTK</a> if you are on Windows.
Install all other dependencies by entering `pip install -r config/docs-requirements.txt` in the console.


In case you are a material-mkdocs insider (paid premium version):
Set your license key by executing `set MKDOCS_MATERIAL_INSIDERS=LICENSE_KEY_HERE` (Windows) in the console.
Then run `pip install -r config/docs-requirements-insiders.txt` instead of `docs-requirements.txt`.


The only thing thats missing once you have done all that is all large files (images & videos). We use 
<a href="https://git-lfs.github.com/" target="_blank">Git LFS</a> to store them, so you need to install that too.
Just run `git lfs install` once you have executed the file that you downloaded from the
<a href="https://git-lfs.github.com/" target="_blank">Git LFS</a> website.  
Then use `git lfs pull` to actually download the files.

Congrats! You should be ready to go.

### See your changes live

You are now primarily working with tools called _mkdocs_ and  _mkdocs-material-theme_ in case you want to google anything.
All files are regular markdown files though.
 
MkDocs enables you to create a website that shows you your changes while you make them.
Execute this to see a preview of the webpage on <a href="http://127.0.0.1:8000" target="_blank">127.0.0.1:8000</a>:

```BASH
#Navigate to ./config from the project root 
cd config
mkdocs serve
```
