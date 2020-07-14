#Setting up the editor

In theory, you can edit quests with any editor. However, using Visual Studio Code gives you some neat productivity advantages and is 
highly recommended! It is lightweight and clean but allows the installation of extensions that can make your life **a lot** easier.

##Installation

Head over to <a href="https://code.visualstudio.com" target="_blank">their site</a> to install the latest version.

###Configuration

!!! caution

    **Make sure to set these settings in the bottom right corner of VSCode**:
    
    `Spaces:2` Indent using Spaces
    
    `UTF-8` file encoding
    
    `LF` end of line characters
    
    `YAML` language module

![Shows the location of the settings](/media/content/LearnBeton/vscode.png)

## Code Snippets
To make the process of writing quests a lot easier we created some code snippets for VSCode.  
When writing an event/condition/objective these snippets will allow you to use the ++tab++ key to automatically complete (just like you know it from minecraft commands).  
They will also suggest optional attributes and display a preview with a short description from this wiki.  
Just have a look at this animation:

![GIF shows autocomplete](https://raw.githubusercontent.com/BetonQuest/betonquest-code-snippets/master/assets/demo.gif)

### Installation
To install the snippet extension head over to the <a href="https://marketplace.visualstudio.com/items?itemName=BetonQuest.betonquest-code-snippets" target="_blank">Visual Studio Marketplace</a>.

![Marketplace Screenshot](/media/content/LearnBeton/snippets-marketplace.png)

To install the extension all you have to do is to click on the green install button.  
If this for some reason doesn't work go to the extensions view in VSCode (++ctrl+shift+x++) and search for `BetonQuest`. 
A full guide on extensions can be found <a href="https://code.visualstudio.com/docs/editor/extension-gallery" target="_blank">here</a>.

### Usage
When you start typing the instruction of an event/condition/objective VSCode will list possible completions with their wiki entries. 
You can use the arrow up and down keys to select them.  
If you found what you were looking for press the ++tab++ key.  
Now all arguments will be added. To cycle through them press ++tab++ again.  
To exit and insert everything press ++esc++.