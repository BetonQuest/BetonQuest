package org.betonquest.betonquest.compatibility.redischat;

import dev.unnm3d.redischat.api.RedisChatAPI;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.conversation.interceptor.Interceptor;
import org.bukkit.entity.Player;

/**
 * Chat Interceptor that works with RedisChat.
 */
public class RedisChatInterceptor implements Interceptor {

    /**
     * The player whose chat is being intercepted.
     */
    protected final Player player;

    /**
     * RedisChatAPI instance.
     */
    private final RedisChatAPI api;

    /**
     * Creates an interceptor for RedisChat.
     * Stops the chat on conversation start and resumes it on conversation end,
     * sending all the missed messages to the player.
     *
     * @param onlineProfile OnlineProfile of the player
     * @param api           RedisChat API
     */
    public RedisChatInterceptor(final OnlineProfile onlineProfile, final RedisChatAPI api) {
        this.player = onlineProfile.getPlayer();
        this.api = api;
        api.pauseChat(player);
    }

    @Override
    public void sendMessage(final String message) {
        player.spigot().sendMessage(TextComponent.fromLegacyText(message));
    }

    @Override
    public void sendMessage(final BaseComponent... message) {
        player.spigot().sendMessage(message);
    }

    @Override
    public void sendMessage(final Component message) {
        player.sendMessage(message);
    }

    @Override
    public void end() {
        api.unpauseChat(player);
    }
}
