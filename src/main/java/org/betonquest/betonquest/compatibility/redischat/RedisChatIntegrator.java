package org.betonquest.betonquest.compatibility.redischat;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.exceptions.HookException;
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
        plugin.registerInterceptor("redischat", RedisChatInterceptor.class);
    }

    @Override
    public void reload() {

    }

    @Override
    public void close() {
    }
}
