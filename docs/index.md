---
template: home.html
title: Home
hide:
  - navigation
  - toc
  - footer
---
<style>
.bq-inline-example-container {
  width: clamp(300px,50%,100%) !important;
}
.examples {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-gap: 1rem;
}
@media (min-width: 1000px) {
  .examples { grid-template-columns: repeat(2, 1fr); }
}
@media (min-width: 2000px) {
  .examples { grid-template-columns: repeat(3, 1fr); }
}
/* compare table - vertical middle */
tr td {
  vertical-align: middle !important;
}
/* compare table - header - color */
thead a, thead p, tr:nth-child(1) th {
  background-color: var(--md-primary-fg-color) !important;
}
/* compare table - header - text formatting */
thead a, thead p {
  font-size: medium;
  font-weight: 700;
  text-decoration: underline;
  color: var(--md-primary-bg-color) !important;
}
/* compare table - div - set fixed size */
.md-typeset__table {
  width: 100%;
}
tbody > tr:nth-child(1) > td:not(:first-child) {
  font-weight: bold;
}

/* compare table - second colum - highlight BQ */
tbody td:nth-child(2) {
  background-color: rgba(0, 0, 0, 0.2);
}
/* compare table - 13 row - set background and force it for colum 1 */
tbody tr:nth-child(13), tr:nth-child(13) td:nth-child(1) {
  background-color: var(--md-primary-fg-color) !important;
}
/* compare table - 13 row - format heading */
tbody tr:nth-child(13) p {
  font-size: medium;
  font-weight: 700;
  text-decoration: underline;
}
/* compare table - header - span format */
thead span {
  text-decoration: none;
  font-weight: 400;
}
</style>

<div class="betonquest-flex-container">
  <div class="betonquest-flex-item">
    <h1 class="betonquest-header betonquest-text-color">Unique Quests and Storylines</h1>
    <ul>
      <li class="betonquest-list"><b>Quest Tools:</b> Logs, Menus, Items, Notifications</li>
      <li class="betonquest-list"><b>Conversations:</b> Interactive, Multi-Path, Translatable</li>
      <li class="betonquest-list"><b>Extensive</b> @snippet:constants:totalIntegratedPluginsNumber@ Plugin Integrations, API</li>
      <li class="betonquest-list"><b>Free</b> and Open Source</li>
    </ul>
    </p>
    <a href="{{ page.next_page.url | url }}" title="Get started with our tutorials"
       class="md-button md-button--primary betonquest-buttons">
      Quick start
    </a>
    <a href="#what-server-owners-say" title="More information" class="md-button betonquest-buttons">
      Read more
    </a>
  </div>
  <div class="betonquest-flex-item">
    <div class="betonquest-flex-logo">
      <img src="_media/brand/Logo/Logo1K.png" alt="" draggable="false">
    </div>
  </div>
</div>

<video controls loop src="_media/content/Home/Features/Trailer.mp4" poster="_media/content/Home/OverviewVideoPreview.jpg" style="box-shadow: 0 0 30px black;" width="100%">
  Sorry, your browser doesn't support embedded videos.
</video>

## What server owners say

By rating, BetonQuest is in the **top 10 out of 57.000 Spigot plugins**.
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
        <li> Can be used for a ton of stuff other than quests if you learn the systems.</li>
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

## BetonQuest Alternatives

<script defer type="text/javascript" src="./_webCode/js/tableRating.js"></script>

| <p>Feature</p>           |                 [BetonQuest][L1]                 | [Quests][L2]<span> PikaMug</span> | [Quests][L3]<span> LMBishop</span> |  [NotQuests][L4]   |     [BeautyQuests][L5]     |  [TypeWriter][L6]  |      [BattlePass][L7]      |       [ProQuest][L8]       |
|--------------------------|:------------------------------------------------:|:---------------------------------:|:----------------------------------:|:------------------:|:--------------------------:|:------------------:|:--------------------------:|:--------------------------:|
| Spigot Rating            |                    Loading...                    |            Loading...             |             Loading...             |     Loading...     |         Loading...         |     Loading...     |         Loading...         |         Loading...         |
| Free                     |                :white_check_mark:                |        :white_check_mark:         |         :white_check_mark:         | :white_check_mark: |     :white_check_mark:     | :white_check_mark: |       :x:  (14.99€)        |       :x:  (14.99$)        |
| OpenSource               |                :white_check_mark:                |        :white_check_mark:         |         :white_check_mark:         | :white_check_mark: |     :white_check_mark:     | :white_check_mark: |            :x:             |            :x:             |
| API                      |                :white_check_mark:                |        :white_check_mark:         |         :white_check_mark:         | :white_check_mark: |     :white_check_mark:     | :white_check_mark: | :material-check-bold: [^1] | :material-check-bold: [^1] |
| Version Support          |                   1.18 - 1.21                    |            1.8 - 1.21             |             1.8 - 1.21             |    1.17 - 1.21     |         1.8 - 1.21         |        1.21        |         1.17-1.21          |         1.7 - 1.21         |
| Integrated Plugins       | @snippet:constants:totalIntegratedPluginsNumber@ |                12                 |                 27                 |         22         |             31             |         3          |             40             |             1              |
| BetonQuest integration   |                                                  |    :material-check-bold: [^5]     |                :x:                 | :white_check_mark: |            :x:             |        :x:         |            :x:             |            :x:             |
| In-game Editor           |                     :x: [^6]                     |         Chat & GUI(Paid)          |                :x:                 |        Chat        |            GUI             |        :x:         |            GUI             |            GUI             |
| Web Editor               |                       :x:                        |                :x:                |                :x:                 |        :x:         |            :x:             | :white_check_mark: |            :x:             |            :x:             |
| Database Support         |                  SQLite & MySQL                  |               MySQL               |            File & MySQL            |   SQLite & MySQL   |           MySQL            |        File        |        File & MySQL        |       SQLite & MySQL       |
| BungeeCord Support       |                :white_check_mark:                |                :x:                |         :white_check_mark:         | :white_check_mark: |            :x:             |        :x:         |     :white_check_mark:     |            :x:             |
| Organized File Structure |                :white_check_mark:                |                :x:                |                :x:                 | :white_check_mark: |            :x:             |        :x:         |            :x:             |            :x:             |
| <p>Unique Features</p>   |                                                  |                                   |                                    |                    |                            |                    |                            |                            |
| Multi-Path Conversations |                :white_check_mark:                |                :x:                |                :x:                 | :white_check_mark: | :material-check-bold: [^3] | :white_check_mark: |            :x:             |            :x:             |
| Quest Journal            |                :white_check_mark:                |        :white_check_mark:         |                :x:                 | :white_check_mark: |            :x:             |        :x:         |     :white_check_mark:     |            :x:             |
| Custom Menus             |                :white_check_mark:                |                :x:                |     :material-check-bold: [^2]     |        :x:         |            :x:             |        :x:         |     :white_check_mark:     |     :white_check_mark:     |
| Quest Items              |                :white_check_mark:                |                :x:                |                :x:                 |        :x:         |            :x:             |        :x:         |            :x:             |            :x:             |
| Backpack for Quest Items |                :white_check_mark:                |                :x:                |                :x:                 |        :x:         |            :x:             |        :x:         |            :x:             |            :x:             |
| Per Player Translations  |                :white_check_mark:                |                :x:                |                :x:                 |        :x:         |            :x:             |        :x:         |            :x:             |            :x:             |
| Clientside NPCs          |                :white_check_mark:                |                :x:                |                :x:                 |        :x:         |            :x:             | :white_check_mark: |            :x:             |            :x:             |
| Clientside Players       |                :white_check_mark:                |                :x:                |                :x:                 |        :x:         |            :x:             |        :x:         |            :x:             |            :x:             |
| Clientside Holograms     |                :white_check_mark:                |                :x:                |                :x:                 |        :x:         |     :white_check_mark:     | :white_check_mark: |            :x:             |            :x:             |
| Clientside Particles     |                :white_check_mark:                |                :x:                |                :x:                 |        :x:         | :material-check-bold: [^4] | :white_check_mark: |            :x:             |            :x:             |


Use this table with caution, it might be outdated (last update <span class="bq-highlight">**17th of January 2025**</span>).
Please let us know if there is any outdated information.

*[Organized File Structure]: Can multiple folders and files be used to organize your quests?
*[Multi-Path Conversations]: Do Conversations have multiple answers that can be chosen by a player?
*[Quest Journal]: Is there a way for the player to track quests and see the progress?
*[Custom Menus]: Are there freely configurable GUI?
*[Backpack for Quest Items]: Is there a way to store Quest Items so they to not need space in the players inventory?
*[Quest Items]: Are there special items that cannot be dropped, eaten, lost etc. and must be used in a quest?
*[Per Player Translations]: Can players choose their language for most gameplay elements (dialogs, notifications etc.)?
*[Clientside Particles]: Can particles for NPCs etc. be displayed exclusively to players that meet certain conditions?
*[Clientside NPCs]: Is it possible to show NPCs only for certain players based on conditions?
*[Clientside Players]: Can player be hidden from each other using conditions?
*[Clientside Holograms]: Can DecentHolograms/HolographicDisplays holograms be hidden using conditions?
[L1]: https://www.spigotmc.org/resources/2117/
[L2]: https://www.spigotmc.org/resources/3711/
[L3]: https://www.spigotmc.org/resources/23696/
[L4]: https://www.spigotmc.org/resources/95872/
[L5]: https://www.spigotmc.org/resources/39255/
[L6]: https://www.spigotmc.org/resources/107748/
[L7]: https://www.spigotmc.org/resources/63076/
[L8]: https://www.spigotmc.org/resources/18249/
[^1]: Private API, this means that other plugins cannot add support.
[^2]: Limited to Quest related context.
[^3]: There are conversations, but they are not multi-path.
[^4]: Only particles above NPCs and a limited configuration.
[^5]: Integration from the BetonQuest side.
[^6]: BetonQuest's scripting is too complex for the limited possibilities of in-game editing.

!!! info "Major Difference"
    BetonQuest is not only a quest plugin. All features and systems work outside of quests too.
    This makes it possible to write scripts that go far beyond what other quest plugins can do.
    This strength is amplified by a large number of integrations for other plugins.

    For many users, BetonQuest does not only power quests but also works as their servers' backbone that links all kinds of
    plugins together.
    Most competitors are "just" quest plugins and therefore limited to simple tasks. That's why BetonQuest skills
    are worth twice as much since you are not limited to writing quests!  
    
    #### BetonQuest vs. Script Plugins
    While BetonQuest can be used for scripting, it stays a quest plugin. It's a compromise, the best of both worlds.
    Plugins with a full focus on scripting - such as
    [Denizen](https://www.spigotmc.org/resources/21039/)
    and [Skript](https://github.com/SkriptLang/Skript/)
    are suited better in certain scenarios. Many users even use these alongside BetonQuest as there is
    integration for such scripting plugins.

<br>

# Features

## Beautiful multi-path Conversations

!!! example inline end bq-inline-example-container note ""
    <video controls loop src="_media/content/Documentation/Conversations/MenuConvIO.mp4" width="100%">
      Sorry, your browser doesn't support embedded videos.
    </video>

Players can have immersive conversation with NPC's by utilizing BetonQuests Multi-Path-Conversation System. Questers can
freely define multi-path stories, narrated with NPC conversations, and with multiple endings that affect a player's
gameplay. Questers can also choose between five different styles to display their conversations.

## Endless Integrations

BetonQuest supports @snippet:constants:totalIntegratedPluginsNumber@ other plugins, ranging from standard plugins like
WorldGuard/WorldEdit and Citizens to
more advanced ones such as EffectLib. It also offers support for other quest plugins, so you can just keep your old
quests and create further additions to your quest lines based upon the progress in your old plugin. If just Beton isn't
enough you can always just hook into Skript or Denizen too.

See the full list of integrations [here](Documentation/Scripting/Building-Blocks/Integration-List.md).

## Custom Notification System

!!! example inline end bq-inline-example-container note ""
    <video controls loop src="_media/content/Documentation/Notifications/NotifySystemOverview.mp4" width="100%">
      Sorry, your browser doesn't support embedded videos.
    </video>

* Questers can make use of BetonQuests notifyIO system that provides access to all of Minecraft's GUI elements to display
  notifications.

* Provide NPC conversations in any language. Each player can set his own language which means that - if translated - all
  conversations will be in the players native language.

* You can give players information about where they are in a quest using the "Journal". The Journal is a book in which
  you can write content based on the players actions.

## Player, NPC and Hologram Hider
!!! example inline end bq-inline-example-container note ""
    <video controls loop src="_media/content/Documentation/Compatibility/PlayerHider.mp4" width="100%">
      Sorry, your browser doesn't support embedded videos.
    </video>

BetonQuest allows you to hide players from each other based on conditions. This can be used to create story-regions
where players are always alone.  
It's also possible to hide entire Citizens NPC's and DecentHolograms/HolographicDisplays holograms.

## Training included

This website provides **in-depth learning material** for your staff which teaches them all BetonQuest basics. It also
has guides on related topics such as the setup of a local test server, so your main server's stability will not be threatened
by staff learning / developing quests.

<p style="clear:both"></p>
# Examples

Our community created a lot of cool stuff!
Some members even made machines with this quest plugin - that really shows that your imagination is the only limit.
Take a look at this handpicked selection:

<div class="examples"> 
  <div>
    <h3> Wandering Trader by Ley </h3>
    <video controls loop src="_media/content/Home/Features/WanderingTrader.mp4" width="100%">
      Sorry, your browser doesn't support embedded videos.
    </video>
    The trader appears for a few minutes and then leaves until the next day.
  </div>
  <div>
    <h3> Bookshop by Esron </h3>
    <video controls loop src="_media/content/Home/Features/BookShop.mp4" width="100%">
      Sorry, your browser doesn't support embedded videos.
    </video>
    A daily quest with NPC voice acting using OpenAudioMC.
  </div>
  <div>
    <h3> Menu by Esron </h3>
    <video controls loop src="_media/content/Home/Features/RPGMenu.mp4" poster="_media/content/Home/Features/RPGMenuVideoThumbnail.png" width="100%">
      Sorry, your browser doesn't support embedded videos.
    </video>
    A server / quest menu made with the menu feature.
  </div>
  <div>
    <h3> Bards by Esron </h3>
    <video controls loop src="_media/content/Home/Features/Bards.mp4" width="100%">
      Sorry, your browser doesn't support embedded videos.
    </video>
    A bar setup with musicians and other NPCs. The player can use the conversation system to select a song.  The audio is made with OpenAudioMC. 
  </div>
  <div>
    <h3>Water Well by Titanium</h3>
    <video controls loop src="_media/content/Home/Features/WaterWell.mp4" width="100%">
      Sorry, your browser doesn't support embedded videos.
    </video>
    A functional well as part of a quest.
  </div>
  <div>
    <h3> Elevator by Titanium </h3>
    <video controls loop src="_media/content/Home/Features/Elevator.mp4" width="100%">
      Sorry, your browser doesn't support embedded videos.
    </video>
    A functional elevator.
  </div>
  <div>
    <h3> Corpse Quest by Titanium </h3>
    <video controls loop src="_media/content/Home/Features/Corpse.mp4" width="100%">
      Sorry, your browser doesn't support embedded videos.
    </video>
    The player needs to collect corpses.
  </div>
</div>

## Donate Money :money_with_wings:

@snippet:general:sponsors@
