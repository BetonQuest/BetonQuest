package org.betonquest.betonquest.compatibility.holograms.fancyholograms;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.PlaceholderIdentifier;
import org.betonquest.betonquest.api.integration.policy.Policy;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.compatibility.holograms.BetonHologramFactory;
import org.betonquest.betonquest.compatibility.holograms.HologramIntegration;
import org.betonquest.betonquest.lib.integration.policy.Policies;
import org.betonquest.betonquest.lib.version.DefaultVersionType;
import org.betonquest.betonquest.lib.version.VersionParser;
import org.bukkit.Bukkit;

/**
 * Integrates with FancyHolograms.
 */
public class FancyHologramsIntegrator implements HologramIntegration {

    /**
     * The minimum required version of FancyHolograms.
     */
    public static final String REQUIRED_VERSION = "2.8.0";

    /**
     * The name of the integrated plugin.
     */
    public static final String NAME = "FancyHolograms";

    /**
     * The empty default constructor.
     */
    public FancyHologramsIntegrator() {
    }

    /**
     * Checks for the valid version range of the 'FancyHolograms' plugin.
     *
     * @return whether the correct version is installed or not
     */
    public static Policy[] getPolicies() {
        return Policies.pluginVersionRange(NAME,
                VersionParser.parse(DefaultVersionType.SIMPLE_SEMANTIC_VERSION, REQUIRED_VERSION),
                VersionParser.parse(DefaultVersionType.SIMPLE_SEMANTIC_VERSION, "2.999"));
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
        return NAME;
    }

    @Override
    public BetonHologramFactory getHologramFactory(final BetonQuestApi api) throws QuestException {
        final BetonQuestLogger log = api.loggerFactory().create(FancyHologramsHologramFactory.class);
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            log.warn("Holograms from FancyHolograms will not be able to use BetonQuest placeholders in text lines "
                    + "without PlaceholderAPI plugin! Install it to use holograms with placeholders!");
        }
        final IdentifierFactory<PlaceholderIdentifier> placeholderIdentifierFactory =
                api.identifiers().getFactory(PlaceholderIdentifier.class);
        return new FancyHologramsHologramFactory(log, api.instructions(), placeholderIdentifierFactory);
    }
}
