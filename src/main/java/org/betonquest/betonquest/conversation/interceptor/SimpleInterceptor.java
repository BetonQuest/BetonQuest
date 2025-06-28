package org.betonquest.betonquest.conversation.interceptor;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("PMD.CommentRequired")
public class SimpleInterceptor implements Interceptor, Listener {
    protected final Player player;

    private final List<String> messages = new ArrayList<>();

    public SimpleInterceptor(final OnlineProfile onlineProfile) {
        this.player = onlineProfile.getPlayer();
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void sendMessage(final Component message) {
        player.sendMessage(message);
    }

    /**
     * This method prevents concurrent list modification.
     */
    @SuppressWarnings("PMD.AvoidSynchronizedStatement")
    private void addMessage(final String message) {
        synchronized (this) {
            messages.add(message);
        }
    }

    @Override
    public void end() {
        HandlerList.unregisterAll(this);

        // Send all messages to player
        for (final String message : messages) {
            player.sendMessage(message);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(final AsyncPlayerChatEvent event) {
        // store all messages so they can be displayed to the player
        // once the conversation is finished
        if (!event.getPlayer().equals(player) && event.getRecipients().contains(player)) {
            event.getRecipients().remove(player);
            addMessage(String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage()));
        }
    }
}
