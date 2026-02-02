# [EffectLib](http://dev.bukkit.org/bukkit-plugins/effectlib/)

If you install this plugin on your server you will be able to play particle effects on NPCs and locations.
You can also use the `particle` action to trigger particle.

!!! info EffectLib Documentation
    EffectLib is not a normal plugin, it's a powerful developer tool - there are no official docs. However, the Magic plugin has a
    [wiki](https://reference.elmakers.com/#effectlib) for EffectLib.
    It does contain a few magic specific settings though so please don't be confused if some stuff does not work.
    There is also a [magic editor](https://sandbox.elmakers.com/#betonquestEffectLibTemplate) with autocompletion for EffectLib.

```YAML title="Example"
effectlib: #(1)!
   farmer: #(2)!
      class: VortexEffect #(3)!
      iterations: 20 #(4)!
      particle: crit_magic
      helixes: 3
      circles: 1
      grow: 0.1
      radius: 0.5
      pitch: -60 #(9)!
      yaw: 90 #(10)!
      interval: 30 #(8)!
      checkinterval: 80 #(11)!
      npcs: NPC1,NPC2 #(5)!
      locations: 171;72;-127;world #(6)!
      conditions: '!con_tag_started,!con_tag_finished' #(7)!
```

1. All effects need to be defined in this section.
2. Each effect is defined as a separate subsection. You can choose any name for it.
3. Any EffectLib effect class.
4. This and all following options until `interval` are EffectLib parameters. You can find them in the 3rd party documentation linked above.
5. A list of all NPCs on which this effect is displayed. This section is optional.
6. A list of all locations on wich the effect is displayed. Optional.
7. The conditions that must be true so that the player can see this effect.
8. Controls after how many ticks the effect is restarted. Optional, default: 100 ticks
9. Controls the vertical direction of the effect.
10. Controls the horizontal direction of the effect.
11. Controls how often the conditions should be checked (in ticks). Optional, default: 100 ticks

### Actions

#### Particle: `particle`

This action will load an effect defined in `effects` section
and display it on player's location. The only argument
is the name of the effect. You can optionally add `loc:` argument
followed by a location written like `100;200;300;world;180;-90` to put
it on that location. If you add `private` argument the effect will only
be displayed to the player for which you ran the action.

```YAML title="Example"
effects:
  beton:
    class: HelixEffect
    iterations: 100
    particle: smoke
    helixes: 5
    circles: 20
    grow: 3
    radius: 30
actions:
  playEffect: "particle beton loc:100;200;300;world;180;-90 private"
```
