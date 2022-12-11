---
icon: octicons/info-16
hide:
  - footer
---
!!! caution "Work in Progress! :construction: :construction_worker:  :construction_site:"

    **Both the plugin and the API are currently getting redesigned.**
    
    Therefore, required docs and API's are likely missing and will be added one by one.
    Newly added API's are subject to change and will have an API state assigned to them.

    We appreciate any feedback!
    The [old API page](API.md) is still available.

    ??? question "API States"
        --8<-- "API-State/State-Explanation.md"

 
## Adding BetonQuest as a dependency

You can add BetonQuest as a dependency using your build system. Here is the config for Maven:

```XML title="Add this to your repositories tag"
<repository>
    <id>betonquest-repo</id>
    <url>https://betonquest.org/nexus/repository/betonquest/</url>
</repository>
```

```XML title="Add this to your dependencies tag"
<dependency>
    <groupId>org.betonquest</groupId>
    <artifactId>betonquest</artifactId>
    <version>2.0.0-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```
