package org.betonquest.betonquest.conversation.interceptor;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.bukkit.entity.Player;

/**
 * Interceptor that does not actually intercept.
 */
public class NonInterceptingInterceptor implements Interceptor {

    /**
     * Player to send the messages.
     */
    protected final Player player;

    /**
     * Create a new Non Intercepting Interceptor.
     *
     * @param onlineProfile the online profile to send the messages
     */
    public NonInterceptingInterceptor(final OnlineProfile onlineProfile) {
        this.player = onlineProfile.getPlayer();
    }

    @Override
    public void sendMessage(final Component message) {
        player.sendMessage(message);
    }

    @Override
    public void end() {
        // Empty
    }
}
