package org.betonquest.betonquest.api.service;

import org.betonquest.betonquest.api.BetonQuestApiInstance;
import org.betonquest.betonquest.api.BetonQuestApiService;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The default implementation of the {@link BetonQuestApiService}.
 */
public class DefaultBetonQuestApiService implements BetonQuestApiService {

    /**
     * The {@link Supplier} for the {@link BetonQuestApiInstance}.
     */
    private final Function<Plugin, BetonQuestApiInstance> apiSupplier;

    /**
     * The cached {@link BetonQuestApiInstance} instances for each {@link Plugin}.
     */
    private final Map<Plugin, BetonQuestApiInstance> cachedApiInstances;

    /**
     * Creates a new instance of the {@link DefaultBetonQuestApiService}.
     *
     * @param apiSupplier the {@link Supplier} for the {@link BetonQuestApiInstance}.
     */
    public DefaultBetonQuestApiService(final Function<Plugin, BetonQuestApiInstance> apiSupplier) {
        this.apiSupplier = apiSupplier;
        this.cachedApiInstances = new HashMap<>();
    }

    @Override
    public BetonQuestApiInstance getApi(final Plugin plugin) {
        return cachedApiInstances.computeIfAbsent(plugin, apiSupplier);
    }
}
