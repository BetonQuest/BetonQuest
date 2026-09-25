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

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * An interceptor which uses the {@link AsyncPlayerChatEvent}.
 */
public class SimpleInterceptor implements Interceptor, Listener {

    /**
     * Player to 'intercept' messages.
     */
    protected final Player player;

    /**
     * Intercepted messages.
     */
    private final Queue<String> messages;

    /**
     * Create a new Simple Interceptor.
     *
     * @param onlineProfile the online profile to send the messages
     */
    public SimpleInterceptor(final OnlineProfile onlineProfile) {
        this.player = onlineProfile.getPlayer();
        messages = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void begin() {
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
        while (!messages.isEmpty()) {
            player.sendMessage(messages.poll());
        }
    }

    @Override
    public void transferTo(final Interceptor next) {
        if (next instanceof final SimpleInterceptor other) {
            other.messages.addAll(messages);
            HandlerList.unregisterAll(this);
            return;
        }
        end();
    }

    /**
     * Removes the player from the chat recipients, if not the sender.
     *
     * @param event the chat event to remove the player from
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(final AsyncPlayerChatEvent event) {
        if (!event.getPlayer().equals(player) && event.getRecipients().contains(player)) {
            event.getRecipients().remove(player);
            addMessage(String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage()));
        }
    }
}
