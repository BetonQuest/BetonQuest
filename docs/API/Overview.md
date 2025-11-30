---
icon: octicons/info-16
---
!!! caution "Work in Progress! :construction: :construction_worker:  :construction_site:"

    **Both the plugin and the API are currently getting redesigned.**
    
    Therefore, required docs and API's are likely missing and will be added one by one.
    Newly added API's are subject to change and will have an API state assigned to them.

    We appreciate any feedback!
    The [old API page](Legacy-API.md) is still available and explains API that was not reworked yet.

    ??? question "API States"
        @snippet:api-state:state-explanation@

 
## Adding BetonQuest as a dependency

You can add BetonQuest as a dependency using your build system. Here is the config for Maven:

```XML title="Add this to your repositories tag"
<repository>
    <id>betonquest-repo</id>
    <url>https://nexus.betonquest.org/repository/betonquest/</url>
</repository>
```

```XML title="Add this to your dependencies tag"
<dependency>
    <groupId>org.betonquest</groupId>
    <artifactId>betonquest</artifactId>
    <version>${plugin.version}</version>
    <scope>provided</scope>
</dependency>
```

## Ensuring that BetonQuest is loaded

!!! warning "Plugin Load Order"
    BetonQuest must already be loaded by the Minecraft server when you access any API.
    If it isn't your code will fail hard with a `ClassNotFoundException`.
    
    Therefore, declare BetonQuest as a soft dependency or hard dependency inside your plugin's *plugin.yml* file.
    A hard dependency will prevent your plugin from loading if BetonQuest is not installed. If your plugin is just a 
    BetonQuest addon, you should use a hard dependency. If your plugin is a standalone plugin that can work without
    BetonQuest, you should use a soft dependency.
    
    === "BetonQuest as a required dependency"
        
        ```yaml title="plugin.yml"
        name: "My BetonQuest Addon"
        depend:
          - BetonQuest
        # ...
        ```
    
    === "BetonQuest as an optional dependency"
        
        ```yaml title="plugin.yml"
        name: "My Standalone Plugin"
        softdepend:
          - BetonQuest
        # ...
        ```

---
## Next Steps
Now you should read how to [Obtain the API](Obtaining-API.md).
