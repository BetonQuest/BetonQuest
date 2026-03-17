package org.betonquest.betonquest.compatibility.redischat;

import dev.unnm3d.redischat.api.RedisChatAPI;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.kernel.registry.feature.InterceptorRegistry;
import org.bukkit.event.Listener;

import java.util.Objects;

/**
 * Integrator for RedisChat.
 */
public class RedisChatIntegrator implements Integration, Listener {

    /**
     * Creates the RedisChat integrator.
     */
    public RedisChatIntegrator() {
    }

    @Override
    public void enable(final BetonQuestApi api) {
        final RedisChatAPI redisChatAPI = RedisChatAPI.getAPI();
        Objects.requireNonNull(redisChatAPI, "RedisChatAPI is null");
        BetonQuest.getInstance().getComponentLoader().get(InterceptorRegistry.class).register("redischat", new RedisChatInterceptorFactory(redisChatAPI));
    }

    @Override
    public void postEnable(final BetonQuestApi api) {
        // no actions required
    }

    @Override
    public void disable() {
        // no actions required
    }
}
