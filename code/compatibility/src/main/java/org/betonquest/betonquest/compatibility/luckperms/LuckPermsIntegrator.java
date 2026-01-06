package org.betonquest.betonquest.compatibility.luckperms;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ContextCalculator;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.luckperms.permission.LuckPermsActionFactory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Integrates LuckPerms to BetonQuest.
 */
@SuppressWarnings("NullAway.Init")
public class LuckPermsIntegrator implements Integrator {

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
     */
    public LuckPermsIntegrator() {
    }

    @Override
    public void hook(final BetonQuestApi api) {
        final RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPermsAPI = provider.getProvider();
            tagCalculator = TagCalculatorUtils.getTagContextCalculator();
            luckPermsAPI.getContextManager().registerCalculator(tagCalculator);
            api.getQuestRegistries().action().register("luckperms", new LuckPermsActionFactory(luckPermsAPI));
        }
    }

    @Override
    public void reload() {
        // Empty
    }

    @Override
    public void close() {
        luckPermsAPI.getContextManager().unregisterCalculator(tagCalculator);
    }
}
