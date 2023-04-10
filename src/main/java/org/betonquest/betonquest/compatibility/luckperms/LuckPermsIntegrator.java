package org.betonquest.betonquest.compatibility.luckperms;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ContextCalculator;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.exceptions.HookException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

@SuppressWarnings({"PMD.CommentRequired", "PMD.AtLeastOneConstructor"})
public class LuckPermsIntegrator implements Integrator {
    private LuckPerms api;

    private ContextCalculator<Player> tagCalculator;

    @Override
    public void hook() throws HookException {
        final RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            api = provider.getProvider();
            tagCalculator = TagCalculatorUtils.getTagContextCalculator();
            api.getContextManager().registerCalculator(tagCalculator);
        }
    }

    @Override
    @SuppressWarnings("PMD.UncommentedEmptyMethodBody")
    public void reload() {
    }

    @Override
    public void close() {
        api.getContextManager().unregisterCalculator(tagCalculator);
    }
}
