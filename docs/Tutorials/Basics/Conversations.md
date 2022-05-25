:octicons-clock-24:  30 minutes · :octicons-tag-16: Basics

In this tutorial you will learn the basics of the conversations in less then 30 minutes.
You don't need any experience in creating quest. This is the very beginning.

!!! danger "Requirements"
    For this part of the tutorial you have to go through the [Setup Guide](../Getting-Started/Setting-up-a-local-test-server.md).
    After you finished setting up your local server, editor and installing the plugin you can start with this tutorial.

##Step 1. Creating the folder structure for the first quest

Let's start with creating new folders for this tutorial. First create a `tutorialQuest` folder and inside this
folder create another folder named `conversations` this is helpful to separate things.
Now we have to create two more files. The first file is called `package.yml` and this goes into the `tutorialQuest` folder.
The second one is called `jack.yml` and goes to the `conversations` folder.
After creating the folders/files your structure should look like this:

<pre>
♦ tutorialQuest(Folder)  
   ♦ conversations(Folder)  
        - jack.yml  
    - package.yml
</pre>

Now that we have the folder and file structure we can move on to create our first conversation.
For the first conversation we need to add a NPC section to the `package.yml` so that the quest package knows which NPC
we want to talk with.

``` markdown title="package.yml"
npcs:
  '1': Jack
```
This is the NPC section where you have to define the NPCs `ID` and the `name` to link it to this package.
After succesfully editing the `package.yml` file save it.

##Step 2. Creating the first conversation

It's time to create the first conversation with the NPC! The goal is to learn how the basics works and to
understand how pointers are working.

Open the file `jack.yml` in the `conversations` folder. Now we will start creating the first conversation. 
The goal is that we can talk to the created NPC and have the first small conversation with him.
We start off with that:

``` markdown title="jack.yml"
conversations:
  Jack:
    quester: Jack
    first: firstGreeting
    NPC_options:
      firstGreeting:
        text: Hello and welcome to my town traveler. Nice to see you. But first where are you from?
        pointer: whereYouFrom
    player_options:
      whereYouFrom:
        text: First I want to know who you are!
```
We've created a `conversations` section above and started with creating the `Jack` conversation. It's important
to define who the quester is with the `quester: Jack` argument. After that we've created the `first` argument.
This one defines in which order the conversation should be played (more about that in the next steps). If no
first argument is defined nothing would happen if you try to talk to the NPC. Dont forget to save and test your
work! You can easily check if your quest is working on the server. Just type `/q reload` in chat and right click
the NPC.

Explanation:  
The `NPC_options` is everything the NPC says and the `player_options` everything that the player could say.
After the `text: ` argument you can define what the NPC says and what the player could answer or ask.
Last but not least there is a `pointer: ` argument. This argument is for pointing to the next conversation and
it is important to have the exact same name. If you create a pointer like `whereYouFrom` then you have to
create it in `player_options` as well as the example shows.

##Step 3. Conversations with multiple choice

It is also possible to have multiple answers for a player when a NPC asks something. This is very helpful for
creating more advanced quests with different endings. In this step we will have a closer look at the `pointer`
argument to see what it could look like.

``` markdown title="jack.yml"
conversations:
  Jack:
    quester: Jack
    first: firstGreeting
    NPC_options:
      firstGreeting:
        text: Hello and welcome to my town traveler. Nice to see you. But first where are you from?
        pointer: whereYouFrom
      whoAmI:
        text: I am &6Jack&r. The mayor of this beautiful town here. We have some big farms and good old taverns and these are well worth checking out! So now where are you from?
        pointer: smallIsland,bigCity
      islandAnswer:
        text: Thats sounds familiar to me because I also grow up in a small town with few people. So we already have a good connection! And because of that I want to give you some food!
      cityAnswer:
        text: Oh I know! I think you're from Kayra, right? Nice city to be honest but I prefer country life. However... You look a bit hungry do you want some food from the best chef out here?
    player_options:
      whereYouFrom:
        text: First I want to know who you are!
        pointer: whoAmI
      smallIsland:
        text: From a small island located east.
        pointer: islandAnswer
      bigCity:
        text: From a big city located west.
        pointer: cityAnswer
```
The player asks the NPC who he is and he's asking again where the player is from. Now we have two choices to answer the question.
The player can either say that he is from a `smallIsland` or from a `bigCity`. In this example case it doesn't matter
because there is no different ending just a specific answer. After choosing the answer there are more `pointer` for having an extended conversation.

Now that the player have two choices to answer we want to go from the split conversation to just one again.
This is very simple! Just define another pointer for that but instead of two different pointers you have to
create just ONE and use it in both parts like this:
``` markdown title="jack.yml"
conversations:
  Jack:
    quester: Jack
    first: firstGreeting
    NPC_options:
      firstGreeting:
        text: Hello and welcome to my town traveler. Nice to see you. But first where are you from?
        pointer: whereYouFrom
      whoAmI:
        text: I am &6Jack&r. The mayor of this beautiful town here. We have some big farms and good old taverns and these are well worth checking out! So now where are you from?
        pointer: smallIsland,bigCity
      islandAnswer:
        text: Thats sounds familiar to me because I also grow up in a small town with few people. So we already have a good connection! And because of that I want to give you some food!
        pointer: yesPlease
      cityAnswer:
        text: Oh I know! I think you're from Kayra, right? Nice city to be honest but I prefer country life. However... You look a bit hungry do you want some food from the best chef out here?
        pointer: yesPlease
      foodAnswer:
        text: Your welcome! Take it... &7*gives food*
    player_options:
      whereYouFrom:
        text: First I want to know who you are!
        pointer: whoAmI
      smallIsland:
        text: From a small island located east.
        pointer: islandAnswer
      bigCity:
        text: From a big city located west.
        pointer: cityAnswer
      yesPlease: 
        text: Oh yes I am starving! Thank you.
        pointer: foodAnswer
```

Here we go! Now we have created a nice conversation between the NPC and player. As you can see there is the same
conversation in both pointers. It doesn't matter if the player says `bigCity` or `smallIsland` because he is
still getting the same possibility to say `yesPlease`.

!!! hint "NEW! You can paste the given link ingame to download the quest package (req. version 294)"
    Download Link
    
You have finished the `Basics Conversation Tutorial` and can now go on with the next tutorial. You've learned the
basics to create simple conversations and split them into different answers. You can even let conversations join
together again and understanding how the system works.  
In the next part of the basics tutorial you will learn how to give the food to the player with `events`!
