package org.betonquest.betonquest.compatibility.redischat;

import dev.unnm3d.redischat.api.RedisChatAPI;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.compatibility.Integrator;
import org.bukkit.event.Listener;

import java.util.Objects;

/**
 * Integrator for RedisChat.
 */
public class RedisChatIntegrator implements Integrator, Listener {

    /**
     * Creates the RedisChat integrator.
     */
    public RedisChatIntegrator() {
    }

    @Override
    public void hook(final BetonQuestApi api) {
        final RedisChatAPI redisChatAPI = RedisChatAPI.getAPI();
        Objects.requireNonNull(redisChatAPI, "RedisChatAPI is null");
        api.getFeatureRegistries().interceptor().register("redischat", new RedisChatInterceptorFactory(redisChatAPI));
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
