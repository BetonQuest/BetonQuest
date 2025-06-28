package org.betonquest.betonquest.conversation.interceptor;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.bukkit.entity.Player;

@SuppressWarnings("PMD.CommentRequired")
public class NonInterceptingInterceptor implements Interceptor {

    protected final Player player;

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
