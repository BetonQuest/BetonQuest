package org.betonquest.betonquest;

import org.betonquest.betonquest.api.integration.IntegrationService;
import org.betonquest.betonquest.compatibility.BundledCompatibility;
import org.betonquest.betonquest.kernel.processor.quest.PlaceholderProcessor;
import org.betonquest.betonquest.lib.integration.policy.Policies;
import org.betonquest.betonquest.mc_1_20_6.BundledMC_1_20_6;
import org.betonquest.betonquest.mc_1_21_4.BundledMC_1_21_4;

import java.util.Objects;
import java.util.Set;

/**
 * Represents Multi Module Entry Point for BetonQuest plugin.
 */
@SuppressWarnings("unused")
public class BetonQuestPlugin extends BetonQuest {

    /**
     * All of those classes have to exist to determine the server software to be Paper.
     */
    public static final Set<String> PAPER_IDENTIFYING_CLASSES =
            Set.of("com.destroystokyo.paper.PaperConfig", "io.papermc.paper.configuration.Configuration");

    /**
     * The required default constructor without arguments for plugin creation.
     */
    public BetonQuestPlugin() {
        super();
    }

    @Override
    public void onEnable() {

        if (!isPaper()) {
            getLogger().severe("BetonQuest requires Paper to run!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        try {
            super.onEnable();
        } catch (final IllegalStateException exception) {
            getLoggerFactory().create(this).error("Disabling BetonQuest due to an error: " + exception.getMessage(), exception);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        final IntegrationService integrationService = getServer().getServicesManager().load(IntegrationService.class);
        Objects.requireNonNull(integrationService);
        integrationService.withPolicies(Policies.minimalVanillaVersion("1.20.6")).register(this,
                () -> new BundledMC_1_20_6(this));
        integrationService.withPolicies(Policies.minimalVanillaVersion("1.21.4")).register(this,
                () -> new BundledMC_1_21_4(this));
        new BundledCompatibility(getLoggerFactory().create(BundledCompatibility.class), getPluginConfig(), this, integrationService)
                .registerCompatiblePlugins(getServer().getServicesManager(), getComponentLoader().get(PlaceholderProcessor.class));
    }

    private boolean isPaper() {
        return PAPER_IDENTIFYING_CLASSES.stream().anyMatch(this::testClass);
    }

    private boolean testClass(final String className) {
        try {
            Class.forName(className);
            return true;
        } catch (final ClassNotFoundException exception) {
            return false;
        }
    }
}
