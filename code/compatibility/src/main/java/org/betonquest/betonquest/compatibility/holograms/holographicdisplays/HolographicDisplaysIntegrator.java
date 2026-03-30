package org.betonquest.betonquest.compatibility.holograms.holographicdisplays;

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.PlaceholderIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.service.placeholder.PlaceholderManager;
import org.betonquest.betonquest.compatibility.holograms.BetonHologramFactory;
import org.betonquest.betonquest.compatibility.holograms.HologramIntegration;
import org.bukkit.plugin.Plugin;

/**
 * Integrates with HolographicDisplays.
 */
public class HolographicDisplaysIntegrator implements HologramIntegration {

    /**
     * The minimum required version of HolographicDisplays.
     */
    public static final String REQUIRED_VERSION = "3.0.0";

    /**
     * The plugin instance to create Holograms.
     */
    private final Plugin plugin;

    /**
     * Creates a new HolographicDisplaysIntegrator for HolographicDisplays.
     *
     * @param plugin the plugin instance to create holograms
     */
    public HolographicDisplaysIntegrator(final Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void enable(final BetonQuestApi api) {
        final HolographicDisplaysAPI holoApi = HolographicDisplaysAPI.get(plugin);
        final BetonQuestLoggerFactory loggerFactory = api.loggerFactory();
        final PlaceholderManager placeholderManager = api.placeholders().manager();
        holoApi.registerIndividualPlaceholder("bq", new HologramPlaceholder(
                loggerFactory.create(HologramPlaceholder.class), placeholderManager, api.profiles()));
        holoApi.registerGlobalPlaceholder("bqg", new HologramGlobalPlaceholder(
                loggerFactory.create(HologramGlobalPlaceholder.class), placeholderManager));
    }

    @Override
    public void postEnable(final BetonQuestApi api) {
        // Empty
    }

    @Override
    public void disable() {
        // Empty
    }

    @Override
    public String getPluginName() {
        return "HolographicDisplays";
    }

    @Override
    public BetonHologramFactory getHologramFactory(final BetonQuestApi api) throws QuestException {
        final BetonQuestLogger log = api.loggerFactory().create(HolographicDisplaysHologramFactory.class);
        final IdentifierFactory<PlaceholderIdentifier> placeholderIdentifierFactory =
                api.identifiers().getFactory(PlaceholderIdentifier.class);
        return new HolographicDisplaysHologramFactory(log, plugin, api.instructions(), placeholderIdentifierFactory);
    }
}
