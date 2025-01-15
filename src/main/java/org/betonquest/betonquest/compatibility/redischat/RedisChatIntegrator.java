package org.betonquest.betonquest.compatibility.redischat;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.exception.HookException;
import org.bukkit.event.Listener;

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
        plugin.getFeatureRegistries().interceptor().register("redischat", RedisChatInterceptor.class);
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
