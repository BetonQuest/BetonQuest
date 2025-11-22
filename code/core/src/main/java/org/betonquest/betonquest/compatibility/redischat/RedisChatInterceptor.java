package org.betonquest.betonquest.compatibility.redischat;

import dev.unnm3d.redischat.api.RedisChatAPI;
import net.kyori.adventure.text.Component;
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
    }

    @Override
    public void begin() {
        api.pauseChat(player);
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
