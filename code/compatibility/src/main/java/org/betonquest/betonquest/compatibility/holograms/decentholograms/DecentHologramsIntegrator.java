package org.betonquest.betonquest.compatibility.holograms.decentholograms;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.PlaceholderIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.compatibility.holograms.BetonHologramFactory;
import org.betonquest.betonquest.compatibility.holograms.HologramIntegration;
import org.bukkit.Bukkit;

/**
 * Integrates with DecentHolograms.
 */
public class DecentHologramsIntegrator implements HologramIntegration {

    /**
     * The minimum required version of DecentHolograms.
     */
    public static final String REQUIRED_VERSION = "2.7.5";

    /**
     * The empty default constructor.
     */
    public DecentHologramsIntegrator() {
    }

    @Override
    public void enable(final BetonQuestApi api) throws QuestException {
        // Empty
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
        return "DecentHolograms";
    }

    @Override
    public BetonHologramFactory getHologramFactory(final BetonQuestApi api) throws QuestException {
        final BetonQuestLogger log = api.loggerFactory().create(DecentHologramsHologramFactory.class);
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            log.warn("Holograms from DecentHolograms will not be able to use BetonQuest placeholders in text lines "
                    + "without PlaceholderAPI plugin! Install it to use holograms with placeholders!");
        }
        final IdentifierFactory<PlaceholderIdentifier> placeholderIdentifierFactory =
                api.identifiers().getFactory(PlaceholderIdentifier.class);
        return new DecentHologramsHologramFactory(log, placeholderIdentifierFactory, api.instructions());
    }
}
