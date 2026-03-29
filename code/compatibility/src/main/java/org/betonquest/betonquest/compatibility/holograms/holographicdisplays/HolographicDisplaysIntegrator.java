package org.betonquest.betonquest.compatibility.holograms.holographicdisplays;

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.PlaceholderIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.holograms.BetonHologramFactory;
import org.betonquest.betonquest.compatibility.holograms.HologramIntegration;
import org.betonquest.betonquest.kernel.processor.quest.PlaceholderProcessor;
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
     * The placeholder manager to use.
     */
    private final PlaceholderProcessor placeholderProcessor;

    /**
     * Creates a new HolographicDisplaysIntegrator for HolographicDisplays.
     *
     * @param plugin               the plugin instance to create holograms
     * @param placeholderProcessor the placeholder manager to use
     */
    public HolographicDisplaysIntegrator(final Plugin plugin,
                                         final PlaceholderProcessor placeholderProcessor) {
        this.plugin = plugin;
        this.placeholderProcessor = placeholderProcessor;
    }

    @Override
    public void enable(final BetonQuestApi api) {
        final HolographicDisplaysAPI holoApi = HolographicDisplaysAPI.get(plugin);
        final BetonQuestLoggerFactory loggerFactory = api.loggerFactory();
        holoApi.registerIndividualPlaceholder("bq", new HologramPlaceholder(
                loggerFactory.create(HologramPlaceholder.class), placeholderProcessor, api.profiles()));
        holoApi.registerGlobalPlaceholder("bqg", new HologramGlobalPlaceholder(
                loggerFactory.create(HologramGlobalPlaceholder.class), placeholderProcessor));
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
        return new HolographicDisplaysHologramFactory(log, plugin, api.instructions(), placeholderIdentifierFactory, placeholderProcessor);
    }
}
