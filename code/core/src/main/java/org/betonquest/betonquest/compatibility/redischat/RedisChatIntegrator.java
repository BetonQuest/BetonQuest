package org.betonquest.betonquest.compatibility.redischat;

import dev.unnm3d.redischat.api.RedisChatAPI;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.HookException;
import org.betonquest.betonquest.compatibility.Integrator;
import org.bukkit.event.Listener;

import java.util.Objects;

/**
 * Integrator for RedisChat.
 */
public class RedisChatIntegrator implements Integrator, Listener {

    /**
     * The BetonQuest plugin class instance.
     */
    private final BetonQuest plugin;

    /**
     * Creates the RedisChat integrator.
     */
    public RedisChatIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() throws HookException {
        final RedisChatAPI redisChatAPI = RedisChatAPI.getAPI();
        Objects.requireNonNull(redisChatAPI, "RedisChatAPI is null");
        plugin.getFeatureRegistries().interceptor().register("redischat", new RedisChatInterceptorFactory(redisChatAPI));
    }

    @Override
    public void reload() {
        // no actions required
    }

    @Override
    public void close() {
        // no actions required
    }
}
