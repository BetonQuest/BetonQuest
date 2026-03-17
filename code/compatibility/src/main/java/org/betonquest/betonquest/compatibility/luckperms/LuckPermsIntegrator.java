package org.betonquest.betonquest.compatibility.luckperms;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ContextCalculator;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.compatibility.luckperms.permission.LuckPermsActionFactory;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;

/**
 * Integrates LuckPerms to BetonQuest.
 */
@SuppressWarnings("NullAway.Init")
public class LuckPermsIntegrator implements Integration {

    /**
     * Bukkit services manager.
     */
    private final ServicesManager servicesManager;

    /**
     * The {@link LuckPerms} API.
     */
    private LuckPerms luckPermsAPI;

    /**
     * The {@link ContextCalculator} for tags.
     */
    private ContextCalculator<Player> tagCalculator;

    /**
     * Creates the {@link LuckPermsIntegrator} instance.
     *
     * @param servicesManager the Bukkit services manager
     */
    public LuckPermsIntegrator(final ServicesManager servicesManager) {
        this.servicesManager = servicesManager;
    }

    @Override
    public void enable(final BetonQuestApi api) {
        final RegisteredServiceProvider<LuckPerms> provider = servicesManager.getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPermsAPI = provider.getProvider();
            tagCalculator = TagCalculatorUtils.getTagContextCalculator(api.persistence(), api.profiles());
            luckPermsAPI.getContextManager().registerCalculator(tagCalculator);
            api.actions().registry().register("luckperms", new LuckPermsActionFactory(luckPermsAPI));
        }
    }

    @Override
    public void postEnable(final BetonQuestApi api) {
        // Empty
    }

    @Override
    public void disable() {
        luckPermsAPI.getContextManager().unregisterCalculator(tagCalculator);
    }
}
