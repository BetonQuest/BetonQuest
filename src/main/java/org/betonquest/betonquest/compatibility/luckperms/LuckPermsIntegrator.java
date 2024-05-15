package org.betonquest.betonquest.compatibility.luckperms;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ContextCalculator;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.luckperms.permission.LuckPermsEventFactory;
import org.betonquest.betonquest.exceptions.HookException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

@SuppressWarnings("PMD.CommentRequired")
public class LuckPermsIntegrator implements Integrator {

    private final BetonQuest instance;

    private LuckPerms luckPermsAPI;

    private ContextCalculator<Player> tagCalculator;

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
            instance.registerNonStaticEvent("luckperms", new LuckPermsEventFactory(luckPermsAPI));
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
