package org.betonquest.betonquest.compatibility.luckperms;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ContextCalculator;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.luckperms.permission.LuckPermsEventFactory;
import org.betonquest.betonquest.exception.HookException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Integrates LuckPerms to BetonQuest.
 */
@SuppressWarnings("NullAway.Init")
public class LuckPermsIntegrator implements Integrator {

    /**
     * The {@link BetonQuest} instance.
     */
    private final BetonQuest instance;

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
        instance = BetonQuest.getInstance();
    }

    @Override
    public void hook() throws HookException {
        final RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPermsAPI = provider.getProvider();
            tagCalculator = TagCalculatorUtils.getTagContextCalculator();
            luckPermsAPI.getContextManager().registerCalculator(tagCalculator);
            instance.getQuestRegistries().event().register("luckperms", new LuckPermsEventFactory(luckPermsAPI));
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
