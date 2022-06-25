---
icon: octicons/terminal-16
---
# Setting up a local test server

## Why do I need a local server?!

You might think that a local test server is useless because you already have a remote server.
There are multiple reasons why you **really** need one:

- Working on a live server could lead to crashes and bugs that your players will have to deal with.
- Making quests can, especially for new questers, lead to unexpected behavior. This can be anything from spawning
hundreds of mobs to endlessly giving out items to a player. Exactly the stuff you don't want to happen.
- Working with a test server is usually faster and therefore more productive. You can restart it all the time,
change plugin configurations as you wish etc.

##Setup of your local server

- **Step 1:** You have propably heard of Spigot, the biggest server software for 
Minecraft. We are going to install Paper (an improved version of Spigot) on your computer. 
Head over to <a href="https://papermc.io/downloads" target="_blank">Papers download page</a> and click on the button you
see in the image below (the numbers will not match up, ignore that).
<span class="centered">![PaperMCDownload](../../_media/content/Tutorials/papermc.png)</span>

- **Step 2:** Create a new folder for the server in a place you can easily access. 
Making a new folder is important because the server will create a lot of files that would mess up your Desktop etc.
Move the downloaded file in the newly created folder.
 
- **Step 3:**
Rename the file to just "_paper_".
If the file name contains a ".jar" ending make sure to keep it.

- **Step 4:** You need a start script to start your server.
Open your text editor and create a file named "_start.bat_" (For Linux and Mac: Create a "_start.sh_" file instead).
Place it next to the "_paper.jar_".
Open it and copy this into it:
```BAT
java -Xms1G -Xmx1G -jar paper.jar --nogui
pause
```
Make sure to save it as a "_.bat_" file! If you save it as a "_.txt_" file it will not work.

??? question "What does this do?"
    This script tells Java to search for a file named "_paper.jar_".
    The `1G` setting in both the `-Xms` and `-Xmx` options is how much RAM you want to give to the server 
    (`1G` = 1 GigaByte RAM, `2G` = 2 GigaByte RAM, `700M` for 700 MegaBytes, etc.). You should not need more then 1GB in most
    cases.      

- **Step 5:** 
Start the server by double-clicking on the start file. Please wait until the console window has closed itself.
Now check the server's folder. You will find a bunch of new folders and files that have been generated.
You need to accept the EULA (Minecrafts End-User-License-Agreement) to be able to run a Minecraft server.
Open up the "_eula.txt_" file, read the terms and agree by setting `eula=false` to `eula=true`.
The start file now serves as the start button for the server.

---
## What's next?
[:material-arrow-right: Next Step: Install BetonQuest](Installing-BetonQuest.md){ .md-button .md-button--primary }
