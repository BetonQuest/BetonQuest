# README #

## TODO ##

### Konstrukcja pluginu ###

* Dziennik w postaci książki
* Komendy do sprawdzania aktualnych zadań itd.
* Zadania globalne, aktywne dla wszystkich graczy (bez ich wiedzy)

### Warunki ###

**Do każdego z warunków musi być argument --inverted jako negacja!**

* posiadanie itemów, z uwzględnieniem rodzaju, ilości i dodatkowych danych, opcjonalnie zamiast ekwipunku sprawdzany jest enderchest (to jest dość skomplikowane)
* aktywny efekt mikstury (albo beacona, chodzi o sam efekt)
* założona zbroja (z uwzględnieniem enchantów / kolorów skóry)
* trzymanie itemu w ręce (z tym samym co posiadanie itemów, bez ilości)
* znajdowanie się w konkretnym miejscu
* konkretna godzina (w minecrafcie, z uwzględnieniem świata!)
* konkretna pogoda (tak samo jak wyżej)
* jakieś warunki związane z scoreboard'ami, nie znam się na tym

### Zdarzenia ###

* dodanie itemów do ekwipunku (z uwzględnieniem wszystkiego co ma item, itemy powinny dropić na ziemię jeśli w ekwipunku nie ma na nie miejsca)
* zabranie itemów z ekwipunku (to samo)
* permisje (ale to kiedyś tam, bo trzeba ogarnąć jak to działa, na razie da się komendami)
* zabicie gracza (opcjonalnie bez utraty ekwipunku jeśli się da)
* spawn moba na koordynatach
* dropienie itemu na koordynatach
* uderzenie pioruna w konkretne miejsce
* wybuch
* dodanie efektu
* zmiana pogody (z uwzględnieniem świata)
* zmiana czasu (jak wyżej)

### Zadania ###

* wykonanie konkretnej akcji (kliknięcie lewym/prawym na konkretny rodzaj bloku trzymając w ręce konkretny item(ilość nie ma znaczenia))
* craftowanie itemu
* przetapianie w piecu
* złowienie konkretnej ryby
* ostrzyżenie owcy (ze wsparciem dla koloru)
* wydojenie krowy xD
* oswojenie zwierzęcia (pies, kot i koń) z uwzględnieniem ewentualnego rodzaju
* zabicie konkretnej ilości konkretnych mobów
* zniszczenie/postawienie konkretnych bloków
* zginięcie (z opcją bez utraty ekwipunku, wsparcie dla powodu śmierci)

## Documentation ##

*Napisane klasy trzeba dokumentować, opisując syntax instrukcji, ogólny schemat działania oraz podając nazwę pod jaką są zarejestrowane, po angielsku!*

### LocationObjective ###
Name: location
Desc: This objective completes when player moves in specified range of specified location and meets all conditions.
Inst: The first argument after objective's name must be location written like 100;200;300;world;5 where 100 is X coordinate, 200 is Y, 300 is Z, world is name of a world and 5 is required range. All conditions and events must be passed after location argument. You can't use these character sequences in location argument: 'conditions:', 'events:', ' ' (space). Coordinates must be valid doubles (floating point must be dot) and world must be name of a loaded world.

### ExperienceCondition ###
Name: experience
Desc: This condition is met when player has specified level (default minecraft experience). It is measured by full levels, not experience points.
Inst: The instruction string must contain argument "exp:X" where X is integer, eg. "exp:20". If you add "--inverted" argument then outcome will be negated.

### PermissionCondition ###
Name: permission
Desc: Player must have certain permission for this condition to return true.
Inst: The instruction string must contain argument
"perm:permission.node". "--inverted" argument negates outcome.

### TagCondition ###
Name: tag
Desc: This one requires player to have a tag set by tag event. Together with "--inverted" negation it is one of the most powerful tools when creating conversations.
Inst: The instruction string must contain "tag:some_text" argument, where some_text it's tag string. As usual the "--inverted" attribute negates outcome.

### MessageEvent ###
Name: message
Desc: This event simply displays a message to player.
Inst: You just pass the message right after event name. %player% will be replaced with player's name, and all & color codes are respected.

### CommandEvent ###
Name: command
Desc: Runs specified command from console.
Inst: You just pass the command without leading slash. All %player% are replaced with player's name.

### TeleportEvent ###
Name: teleport
Desc: Teleports player to specified location, with or without head rotation.
Inst: The first and only argument must be location, created with following syntax: 100;200;300;world or 100;200;300;world;90;45 where the first 3 numbers are coordinates (double), "world" is name of the world and last to numbers are yaw and pitch respectively (float).

### TagEvent ###
Name: tag
Desc: This event adds (or removes) a tag to player. This, along with "--inverted" argument, is one of the most powerful tools for creating dynamic conversations.
Inst: The first argument after event's name must be "add" or "del". It works as it sounds. Next goes tag string. It can't contain spaces (though _ is fine)

### ObjectiveEvent ###
Name: objective
Desc: Creates new objective defined in objectives.yml
Inst: The first and only argument is name of objective defined in objectives.yml