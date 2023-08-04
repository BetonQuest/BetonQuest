package org.betonquest.betonquest.compatibility.redischat;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.exceptions.HookException;
import org.bukkit.event.Listener;

@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.UncommentedEmptyMethodBody", "PMD.CommentRequired"})
public class RedisChatIntegrator implements Integrator, Listener {
    private final BetonQuest plugin;

    public RedisChatIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() throws HookException {
        plugin.registerInterceptor("redischat", RedisChatInterceptor.class);
    }

    @Override
    public void reload() {

    }

    @Override
    public void close() {
    }
}
