package org.betonquest.betonquest.compatibility.redischat;

import dev.unnm3d.redischat.api.RedisChatAPI;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.PlayerConversationEndEvent;
import org.betonquest.betonquest.api.PlayerConversationStartEvent;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.exceptions.HookException;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@SuppressWarnings({"PMD.AtLeastOneConstructor", "PMD.UncommentedEmptyMethodBody", "PMD.CommentRequired"})
public class RedisChatIntegrator implements Integrator, Listener {
    private final BetonQuest plugin;
    private final RedisChatAPI api;

    public RedisChatIntegrator() {
        plugin = BetonQuest.getInstance();
        api = RedisChatAPI.getAPI();
    }

    @Override
    public void hook() throws HookException {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onConversationStart(final PlayerConversationStartEvent event) {
        api.pauseChat(event.getProfile().getPlayer().getPlayer());
    }

    @EventHandler
    public void onConversationEnd(final PlayerConversationEndEvent event) {
        api.unpauseChat(event.getProfile().getPlayer().getPlayer());

    }

    @Override
    public void reload() {

    }

    @Override
    public void close() {

    }
}
