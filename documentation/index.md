---
template: home.html
title: Home
hide:
  - navigation
  - toc
---
<style>
.bq-inline-example-container {
  width: clamp(300px,50%,100%) !important;
}
.bq-hilight {
  font-weight: 700;
  text-decoration: underline;
}
td, th{
  vertical-align: middle !important;
}
td:nth-child(2) {
  background-color: rgba(0, 0, 0, 0.2);
}
tr:nth-child(10) {
  box-shadow: 0 5px rgba(0, 0, 0, 0.2);
}
</style>

<div style="text-align: center;">
  <video autoplay muted loop poster="media/content/Home/OverviewVideoPreview.jpg" src="media/content/Home/Features/Trailer.mp4" width="100%" style="box-shadow: 0px 0px 30px black;">
    Sorry, your browser doesn't support embedded videos.
  </video>
</div>

## What server owners say

By rating, BetonQuest is in the **top 30 out of 50.000 Spigot plugins**.
<br>
Sometimes, user reviews describe a plugin better than the author can:

!!! quote "Spigot Reviews"
    === "Esron"
        Quick Facts:
        <ul>
        <li> Amazing system for Quests, no matter if they are simple or complex.</li>
        <li> Easy to make Daily/repeatable quests.</li>
        <li> Great Discord community and staff support.</li>
        <li> Can be used to replace every GUI plugin we've had before.</li>
        <li> Can be used for a ton of stuff other then quests if you learn the systems.</li>
        </ul>
      
        The story:
        I've worked with Beton for a year now, and probably (guessing) worked with it for 500 hours.
        We've used it on both a survival and a MMORPG server.
        
        I have 0 complaints. It's well thought through and getting better by the day.
        The day BetonQuest 2.0 (you can do like me and use the dev version) comes out, will be a beautiful day for all server owners.
        
        /Fred, Esron, Znitsarn.

    === "Azfaloth"
        This is a brilliant plugin. It is so much more than just a plugin for quests. It is a set of tools that allow you to do so many things.
        I feel like this is a must have for any server that wants to add functionality to citizens NPCs even without using it for making quests.
        The possibilities are endless. You can have very immersive complex conversations that can be used to explain server functions, create shops,
        teleport players, make portals etc. [...]<br><br>
        **The possibilities are limited only by what you can think of doing.** I started out looking for a quest plugin and after using this,
        I wished I had used it before just for the sake of the immersion it adds. [...]

    === "Maverick2020"
        This is the most comprehensive quest plugin I have seen. I am still amazed this is free. The devs are extremely active,
        continue to make new things available, and have a clear roadmap to offering even more features. I am in awe of their skills and dedication.
        <br><br>
        I am truly blown away by the support team. They are the absolute best. They answer things quickly and thoroughly.
        I really cannot say enough good things about this quest plugin!

    === "Caleb_Britannia"
        This is actually my second review after 2 years which goes to show the complexity and skill behind this plugin.
        Many reviews have stated many times how powerful, complex and overall useful this plugin is.
        Comparing it to other quest plugins is simply not good because its so powerful I would put it on the level on a minor scripting language.
        The versatility and scale of this plugin has seen it develop to the backend of some powerful RPG servers! [...]
        <br><br>
        This is all taken however apart from the most important and influential part of the plugin and that is the developers.
        I have seen an entire range of developers come and go from the OG Co0sh to the current team and all of them have given it 110%.
        From releasing patches on the same day bugs have been reported to spending hours and hours in calls with users it is impossible
        not to realise how much the developers care about the plugin and all its users. Their care and hundreds of hours of dedication are
        shown in this insanely powerful and (mostly) bug free quest plugin. Anyone who aspires to give their players a 
        proper RPG experience needs to check this plugin out. Quest on!!

## BetonQuest alternatives

There are other quest plugins out there. Here you have a compare between some maintained and relevant quest plugins: 

Feature                   | [BetonQuest][L1]         | [Quests by PikaMug][L2]  | [Quests by LMBishop][L3] | [BeautyQuests][L4]       | [MangoQuest][L5]         | [QuestCreator][L6]       | [ProQuest][L7]           |
------------------------- | :----------------------: | :----------------------: | :----------------------: | :----------------------: | :----------------------: | :----------------------: | :----------------------: |
Free                      | :white_check_mark:       | :white_check_mark:       | :white_check_mark:       | :white_check_mark:       | :white_check_mark:       | :x:  (20$)               | :x:  (19.99$)            |
OpenSource                | :white_check_mark:       | :white_check_mark:       | :white_check_mark:       | :white_check_mark:       | :white_check_mark:       | :x:                      | :x:                      |
API                       | :white_check_mark:       | :white_check_mark:       | :white_check_mark:       | :white_check_mark:       | :x:                      |:material-check-bold: [^1]|:material-check-bold: [^1]|
Version Support           | 1.13 - 1.17.1            | 1.7 - 1.17.1             | 1.7 - 1.17.1             | 1.11 - 1.16.5            | 1.13 - 1.17.1            | 1.7 - 1.17.1             | 1.7 - 1.17.1             |
Integrated Plugins        | 29                       | 13                       | 1                        | 15                       | 6                        | 30                       | 1                        |
Ingame Editor             | :x:                      | Chat & GUI(Paid)         | :x:                      | GUI                      | :white_check_mark:       | GUI                      | GUI                      |
External Editor           | :white_check_mark:       | :x:                      | :x:                      | :x:                      | :x:                      | :x:                      | :x:                      |
Database Support          | SQLite & MySQL           | MySQL                    | MySQL                    | MySQL                    | MySQL                    | MySQL                    | SQLite & MySQL           |
Bungee Support            | :white_check_mark:       | :x:                      | :white_check_mark:       | :x:                      | :x:                      | :x:                      | :x:                      |
Organized File Structure  | :white_check_mark:       | :x:                      | :x:                      | :x:                      | :white_check_mark:       | :white_check_mark:       | :x:                      |
<span class="bq-hilight">Unique Features</span>
Multi-Path Conversations  | :white_check_mark:       | :x:                      | :x:                      |:material-check-bold: [^3]| :white_check_mark:       | :white_check_mark:       | :x:                      |
Quest Journal             | :white_check_mark:       | :white_check_mark:       | :x:                      | :x:                      | :white_check_mark:       | :white_check_mark:       | :x:                      |
Inventory GUIs            | :white_check_mark:       | :x:                      | :white_check_mark: [^2]  | :x:                      | :x:                      | :white_check_mark:       | :white_check_mark:       |
Quest Items               | :white_check_mark:       | :x:                      | :x:                      | :x:                      | :x:                      | :x:                      | :x:                      |
Backpack for Quest Items  | :white_check_mark:       | :x:                      | :x:                      | :x:                      | :x:                      | :x:                      | :x:                      |
Per Player Language       | :white_check_mark:       | :x:                      | :x:                      | :x:                      | :x:                      | :x:                      | :x:                      |
Client Side NPCs (Hiding) | :white_check_mark:       | :x:                      | :x:                      | :x:                      | :x:                      | :white_check_mark:       | :x:                      |
Player Hider              | :white_check_mark:       | :x:                      | :x:                      | :x:                      | :x:                      | :x:                      | :x:                      |
HolographicDisplays Hider | :white_check_mark:       | :x:                      | :x:                      | :white_check_mark:       | :x:                      | :white_check_mark:       | :x:                      |
Per Player Particles      | :white_check_mark:       | :x:                      | :x:                      |:material-check-bold: [^4]| :x:                      | :white_check_mark:       | :x:                      |

Enjoy this table with caution, it might be outdated and it only trys to compare BetonQuest with the other plugins.  
<span class="bq-hilight">**This table has been last updated on the 03rh of September 2021. If there are any mistakes let us know!**</span>

You may notice the footnotes, if not you should read them.
You should also notice, that BetonQuest is a more general plugins with the goal to be more like a scripting plugin.
That means that if other plugins have equal features as BetonQuest, the features are probably more quests related than in BetonQuest.

[L1]: https://www.spigotmc.org/resources/betonquest.2117/
[L2]: https://www.spigotmc.org/resources/quests.3711/
[L3]: https://www.spigotmc.org/resources/quests.23696/
[L4]: https://www.spigotmc.org/resources/beautyquests.39255/
[L5]: https://github.com/Cutiemango/MangoQuest
[L6]: https://www.spigotmc.org/resources/questcreator.38734/
[L7]: https://www.spigotmc.org/resources/proquests.18249/
[^1]: Private API, this means that other plugins cannot add support it, which is considered an unfriendly policy.
[^2]: Limited to Quest related context.
[^3]: There are conversations, but they are not Multi-Path.
[^4]: Only particles above NPCs and a limited configuration.

### Scripting-Plugin alternatives

There is one more type of plugins that could be used instead of BetonQuest.
But the most people use them in addition to BetonQuest. We talk about Scripting plugins.
If you want to make as much custom content as possible without coding java, you should check
[Denizen](https://www.spigotmc.org/resources/denizen.21039/) and [Skript](https://github.com/SkriptLang/Skript/).

But you should think about, that a scripting plugin usual take more time to lean and to script as BetonQuest,
because we have a really simple scripting language. And if you want more quest specific content,
you also profit by build in features of BetonQuest and other quest Plugins.

<br>
# Features

!!! example inline end bq-inline-example-container
    <video controls loop src="media/content/Documentation/Conversations/MenuConvIO.mp4"
    width="100%">
    Sorry, your browser doesn't support embedded videos.
    </video>

## Beautiful multi-path Conversations


Players can have immersive conversation with NPC's by utilizing BetonQuests Multi-Path-Conversation System. Questers can
freely define multi-path stories, narrated with NPC conversations, and with multiple endings that affect a player's
gameplay. Questers can also choose between five different styles to display their conversations.

## Endless Integrations

BetonQuest supports more then 29 other plugins, ranging from standard plugins like WorldGuard/WorldEdit and Citizens to
more advanced ones such as EffectLib. It also offers support for other quest plugins, so you can just keep your old
quests and create further additions to your quest lines based upon the progress in your old plugin. If just Beton isn't
enough you can always just hook into Skript or Denizen too.

See the full list of integrations [here](Documentation/Compatibility.md).

!!! example inline end bq-inline-example-container
    <video controls loop src="media/content/Documentation/Notifications/NotifySystemOverview.mp4"
    width="100%">
    Sorry, your browser doesn't support embedded videos.
    </video>

## Custom Notification System

* Questers can make use of BetonQuests notifyIO system that provides access to all of Minecafts GUI elements to display
  notifications.

* Provide NPC conversations in any language. Each player can set his own language which means that - if translated - all
  conversations will be in the players native language.

* You can give players information about where they are in a quest using the "Journal". The Journal is a book in which
  you can write content based on the players actions.

!!! example inline end bq-inline-example-container
    <video controls loop src="media/content/Documentation/Compatibility/PlayerHider.mp4"
    width="100%">
    Sorry, your browser doesn't support embedded videos.
    </video>

## Player, NPC and Hologram Hider


BetonQuest allows you to hide players from each other based on conditions. This can be used to create story-regions
where players are always alone.  
It's also possible to hide entire Citizens NPC's and HolographicDisplays holograms.

## Training included

This website provides **in-depth learning material** for your staff which teaches them all BetonQuest basics. It also
has guides on related topics such as the setup of a local test server, so your main server's stability will not be threatened
by staff learning / developing quests.

## Examples

Our community created a lot of cool stuff!
Some members even made machines with this quest plugin - that really shows that your imagination is the only limit.
Take a look at this handpicked selection:

<div style="display: grid; grid-column-gap: 50px; grid-template-columns: auto auto;"> 
    <div>
      <h3> Wandering Trader by Ley </h3>
        <video controls loop
        src="media/content/Home/Features/WanderingTrader.mp4"
        width="100%">
        Sorry, your browser doesn't support embedded videos.
      </video>
      The trader appears for a few minutes and then leaves until the next day.
  </div>
  <div>
    <h3> Bookshop by Esron </h3>
      <video controls loop src="media/content/Home/Features/BookShop.mp4"
      width="100%">
      Sorry, your browser doesn't support embedded videos.
      </video>
      A daily quest with NPC voice acting using OpenAudioMC.
  </div>
  <div>
    <h3> Menu by Esron </h3>
      <video controls loop poster="media/content/Home/Features/RPGMenuVideoThumbnail.png" src="media/content/Home/Features/RPGMenu.mp4"
      width="100%">
      Sorry, your browser doesn't support embedded videos.
      </video>
      A server / quest menu made with the menu feature.
  </div>
  <div>
    <h3> Bards by Esron </h3>
      <video controls loop src="media/content/Home/Features/Bards.mp4"
      width="100%">
      Sorry, your browser doesn't support embedded videos.
      </video>
      A bar setup with musicians and other NPC's. The player can use the conversation system to select a song.  The audio is made with OpenAudioMC. 
  </div>
  <div>
    <h3>Water Well by Titanium</h3>
      <video controls loop src="media/content/Home/Features/WaterWell.mp4"
      width="100%">
      Sorry, your browser doesn't support embedded videos.
      </video>
      A functional well as part of a quest.
  </div>
  <div>
    <h3> Elevator by Titanium </h3>
        <video controls loop
        src="media/content/Home/Features/Elevator.mp4"
        width="100%">
        Sorry, your browser doesn't support embedded videos.
      </video>
    A functional elevator.
  </div>
  <div>
      <h3> Corpse Quest by Titanium </h3>
          <video controls loop
          src="media/content/Home/Features/Corpse.mp4"
          width="100%">
          Sorry, your browser doesn't support embedded videos.
        </video>
      The player needs to collect corpses.
  </div>
</div>

## Sponsoring

**This is a community funded project! Our monthly costs (server, domain, software licences) are paid by our Patreons.**
Thanks to them for making this project possible!

[Support us on Patreon!  :heart:](https://patreon.com/betonquest/){: .md-button .md-button--primary }
[Shop on mcmodels!  :fontawesome-solid-shopping-cart:](https://mcmodels.net/?wpam_id=3){: .md-button .md-button--primary } 

* The mcmodels link is an affiliate link - we get a cut of the sales price.
