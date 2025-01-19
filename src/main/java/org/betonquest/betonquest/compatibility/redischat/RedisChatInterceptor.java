package org.betonquest.betonquest.compatibility.redischat;

import dev.unnm3d.redischat.api.RedisChatAPI;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.Interceptor;
import org.bukkit.entity.Player;

import java.util.Objects;

/**
 * Chat Interceptor that works with RedisChat.
 */
public class RedisChatInterceptor implements Interceptor {

    /**
     * The conversation this interceptor acts for.
     */
    protected final Conversation conv;

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
     * @param conv          Conversation to intercept
     * @param onlineProfile OnlineProfile of the player
     */
    public RedisChatInterceptor(final Conversation conv, final OnlineProfile onlineProfile) {
        this.conv = conv;
        this.player = onlineProfile.getPlayer();
        this.api = Objects.requireNonNull(RedisChatAPI.getAPI());
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
    public void end() {
        api.unpauseChat(player);
    }
}
