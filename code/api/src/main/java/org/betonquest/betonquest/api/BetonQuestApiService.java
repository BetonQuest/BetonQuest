package org.betonquest.betonquest.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicesManager;

import java.util.Optional;

/**
 * The BetonQuest API service represents the single source of truth for all methods related to BetonQuest.
 * Accessing and modifying the current state of BetonQuest will primarily be done through this service
 * and the {@link BetonQuestApiInstance} it is providing.
 * <br> <br>
 * The only valid instance for this interface is available through the {@link ServicesManager}
 * and may be obtained by calling {@link ServicesManager#load(Class)} with this interface as parameter.
 * Alternatively does {@link #get()} invoke {@link ServicesManager#load(Class)} method for you
 * and returns its nullable result wrapped in an {@link Optional}.
 * Be aware that the service manager will return {@code null} if the service is not registered yet.
 * <br> <br>
 * The API service is available and ready to use after BetonQuest itself has finished enabling and may therefore be called
 * the earliest while enabling a plugin explicitly depending on BetonQuest (enabling after BetonQuest).
 */
@FunctionalInterface
public interface BetonQuestApiService {

    /**
     * Attempts to load the {@link BetonQuestApiService} from the bukkit's {@link ServicesManager}.
     * Will return an empty optional if the service is not registered yet, got disabled,
     * or an error caused BetonQuest to fail to load entirely.
     *
     * @return an optional containing the API service or an empty optional if the service is not available
     */
    static Optional<BetonQuestApiService> get() {
        return Optional.ofNullable(Bukkit.getServicesManager().load(BetonQuestApiService.class));
    }

    /**
     * Attempts to get the API instance for the specified plugin.
     * <br> <br>
     * To ensure that the api is functioning correctly, it is advised that only the specified plugin
     * is going to use the resulting instance hereafter.
     *
     * @param plugin the plugin to get the API instance for
     * @return the API instance for the specified plugin
     */
    BetonQuestApiInstance getApi(Plugin plugin);
}
