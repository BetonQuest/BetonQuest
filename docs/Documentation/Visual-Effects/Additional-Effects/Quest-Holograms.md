---
icon: material/text
---

## Holograms

@snippet:integrations:holograms@


### Hidden Holograms
Installing either of these plugins will enable you to create hidden holograms, which will be shown to players only if they meet specified conditions.

In order to create a hologram, you have to add a `holograms` section. Add a node named as your hologram to this section
and define `lines`, `conditions` and `location` subnodes. The first one should be a list of texts - these will be the lines
of a hologram. Color codes are supported. Second is a list of conditions separated by commas. Third is a location in a standard
format, like in `teleport` event. If `max_range` is specified, the hologram will only be visible in this range, if not,
the default value from the connected hologram plugin will be used.
An example of such hologram definition:

```YAML
holograms:
  beton:
    lines:
    - 'item:custom_item'
    - '&2Top questers this month'
    - 'top:completed_quests;desc;10;&a;§6;2;&6'
    - '&2Your amount: &6%point.completed_quests.amount%'
    - '&Total amount: &6%azerothquests>globalpoint.total_completed_quests.amount%'
    conditions: has_some_quest,!finished_some_quest    
    location: 100;200;300;world
    # How often to check conditions (optional)
    check_interval: 20
    # Maximum hologram display distance (optional)
    max_range: 40
```

#### Item Lines
A line can also represent a floating item. To do so enter the line as 'item:`custom_item`'. It will be replaced with the
`custom_item` defined in the `items` section. If the Item is defined for example as map, a floating map will be seen between two lines of text.

#### Ranking Holograms
Holograms created by BetonQuest can rank users by the score of a point. Such scoreboards (not to be confused with the
Minecraft vanilla scoreboard) are configured as one line and replaced by multiple lines according to the limit definition.
Each scoreboard line comes in the format `#. name - score` The short syntax is 'top:`point`;`order`;`limit`'. The specified
`point` must be located inside the package the hologram is declared in. To use a point from another package, put `package.point`
instead. The `order` is either 'desc' for descending or 'asc' for ascending. If something other is specified, descending will
be used by default. The limit should be a positive number. In the short declaration, the whole line will be white. To color
each of the four elements of a line (place, name, dash and score), the definition syntax can be extended to
'top:`point`;`order`;`limit`;`c1`;`c2`;`c3`;`c4`'. The color codes can be prefixed with either `§` or `&`, but do not have
to be. If for example `c2` is left blank (two following semicolons), it is treated as an 'f' (color code for white).

Each BetonQuest variable can be displayed on a hologram in a text line. These variables use the same definition syntax as
in conversations such that; '`%package.variable%`'. Where the `package` part is optional if the hologram is defined in the
same package as the variable. If you wish to refer to a variable that is *not* in the same package as the hologram, then you
must specify a [package](../../Scripting/Packages-&-Templates.md) before the `variable`.

!!! warning "Potential lags"
    The HolographicDisplays documentations warns against using too many individual hologram variables since they are rendered
    for each player individually. If you are using HolographicDisplays, to save resources, it is recommended to minimise the use of non-static variables.

The hologram's conditions are checked every 10 seconds, meaning a hologram will respond to a condition being met or un-met
every 10 seconds. If you want to make it faster, decrease `hologram.update_interval` option in "_config.yml_" file and set it to a
number of ticks you want to pass between updates (one second is 20 ticks). Don't set it to 0 or negative numbers, it will result in an error.

Keep in mind that each hologram plugin also updates its holograms on a timer individually,
meaning that hologram variables will refresh at a much quicker rate than the above.
