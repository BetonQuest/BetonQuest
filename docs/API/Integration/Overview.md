---
icon: material/application-export
status: new
---
# Overview

@snippet:api-state:unfinished@

??? abstract "Integration API Classes"
    * `org.betonquest.betonquest.api.integration.IntegrationService`

## Introduction

The integration API allows integrating a plugin with BetonQuest supporting policies to manage partial integration 
based on conditions.

!!! warning "You should have viewed these pages"
    - [How to access the api](../Obtaining-API.md)
    - [How to use the API](./Overview.md)

<div class="grid" markdown>
!!! success "Use Case"
    Integrations are primarily used to extend BetonQuest with new functionality provided by other plugins.
    
!!! failure "Do Not Use"
    Integrations are not intended to grant access to features of BetonQuest in a read-only manner.
    Please use an API instance provided by BetonQuest instead.
</div>

## Access

Similar to [obtaining the API](../Obtaining-API.md), you can access the integration API by loading the `IntegrationService` instance from the 
`ServicesManager`.

```java title="Get the API Service"
final ServicesManager servicesManager = getServer().getServicesManager(); //(1)!
final IntegrationService apiService = servicesManager.load(IntegrationService.class);
```

1. Alternatively, you can use `Bukkit.getServicesManager()` to obtain the `ServicesManager` instance.

This can only be called **after** the `onLoad` method of BetonQuest and should be called **before** the `onEnable` method.
Obtaining the API directly is not supported this early in the loading process; however, integrations are built in such a way that 
they are provided with the API instance when they are enabled by BetonQuest.
