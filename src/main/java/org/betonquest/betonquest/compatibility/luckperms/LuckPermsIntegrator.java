package org.betonquest.betonquest.compatibility.luckperms;

import net.luckperms.api.LuckPerms;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.exceptions.HookException;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

@SuppressWarnings({"PMD.CommentRequired", "PMD.AtLeastOneConstructor"})
public class LuckPermsIntegrator implements Integrator {

    private LuckPerms api;

    @Override
    public void hook() throws HookException {
        final RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            api = provider.getProvider();
            api.getContextManager().registerCalculator(new TagCalculator());
        }
    }

    @Override
    @SuppressWarnings("PMD.UncommentedEmptyMethodBody")
    public void reload() {
    }

    @Override
    public void close() {
        api.getContextManager().unregisterCalculator(new TagCalculator());
    }
}
