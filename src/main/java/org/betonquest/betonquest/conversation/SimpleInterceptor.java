package org.betonquest.betonquest.conversation;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.exceptions.QuestException;
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
    protected final Conversation conv;

    protected final Player player;

    private final List<String> messages = new ArrayList<>();

    public SimpleInterceptor(final Conversation conv, final OnlineProfile onlineProfile) throws QuestException {
        this.conv = conv;
        this.player = onlineProfile.getPlayer();
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    /**
     * Send message, bypassing Interceptor
     */
    @Override
    public void sendMessage(final String message) {
        player.spigot().sendMessage(TextComponent.fromLegacyText(message));
    }

    @Override
    public void sendMessage(final BaseComponent... message) {
        player.spigot().sendMessage(message);
    }

    /**
     * This method prevents concurrent list modification
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
