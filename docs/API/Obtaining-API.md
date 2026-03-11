---
icon: material/widgets
---
@snippet:api-state:unfinished@

To obtain the API make yourself familiar with the `org.bukkit.plugin.ServicesManager`.

## Bukkit's ServicesManager
The ServicesManager is a part of the Bukkit API that allows plugins to provide services to other plugins.
To avoid a static singleton pattern, the ServicesManager is designed to be used as a dependency injection container.
BetonQuest is using the ServicesManager to provide its API.

## Obtaining the `BetonQuestApiService`
The `BetonQuestApiService` is the **single source of truth** for BetonQuest that allows other plugins to obtain the API.

```java title="Get the API Service"
final ServicesManager servicesManager = getServer().getServicesManager(); // or Bukkit.getServicesManager()
final BetonQuestApiService apiService = servicesManager.load(BetonQuestApiService.class);
```
This can only be called **after** the `onEnable` method of BetonQuest.
This is usually the case when your plugin's `onEnable` method is called by Bukkit,
assuming you defined a _dependency_ on BetonQuest.
The `BetonQuestApiService` registers its default implementation with the `Highest` ServicePriority, since it is not 
meant to be overridden.

??? "The unrecommended way in case of an emergency"
    If for some reason you need to obtain the API service in fewer steps, you can use a static method in 
    `BetonQuestApiService`, which essentially does the same thing as the code above, but under the hood:
    ```java title="Static method to obtain the API"
    final BetonQuestApiService apiService = BetonQuestApiService.get();
    ```

## Obtaining the `BetonQuestApi`
The `BetonQuestApiService` provides a method to obtain the actual API instance:
```java title="Obtain the API instance"
final Plugin yourPluginInstance;
final BetonQuestApiService apiService;
final BetonQuestApi betonQuestApi = apiService.api(yourPluginInstance);
```

## The API Hints
The following hint can be found on many API pages (usually as collapsed):

!!! abstract "Logging API Classes"
    * `org.betonquest.betonquest.api.logger.BetonQuestLogger`
    * `org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory`

It lists all interfaces that are used from the API on that page.

## Working with the API
We recommend that you inject instances you obtained from the `ServicesManager` into your classes when they need them.
You might want to learn about "Dependency Injection" as a programming technique,
but as a quick start here's a simple example:

This plugin injects an instance of `BetonQuestApi` and `BetonQuestLogger` that was created by the 
`BetonQuestApiService` into a class implementing some feature.

```java linenums="1"
public class MyAddon extends JavaPlugin {

    @Override
    public void onEnable() {
        final ServicesManager servicesManager = getServer().getServicesManager();
        final BetonQuestApiService betonQuestApiService 
            = servicesManager.load(BetonQuestApiService.class);
        final BetonQuestApi betonQuestApi = betonQuestApiService.api(this);
        final BetonQuestLogger betonQuestLogger = betonQuestApi.loggerFactory().create(MyFeature.class);
        new MyFeature(betonQuestLogger, betonQuestApi);
    }
}

public class MyFeature {
    private final BetonQuestLogger betonQuestLogger;
    private final BetonQuestApi betonQuestApi;

    public MyFeature(final BetonQuestLogger betonQuestLogger,
                     final BetonQuestApi betonQuestApi) {
        this.betonQuestLogger = betonQuestLogger;
        this.betonQuestApi = betonQuestApi;
    }
}
```

---

<div class="grid" markdown style="text-align: center;">
[:octicons-arrow-left-16: Introduction](./Overview.md){ .md-button .md-button--primary style="width: 49%;" }
[Learn about API concepts :octicons-arrow-right-16:](./Quest/Overview.md){ .md-button .md-button--primary style="width: 49%;" }
</div>
