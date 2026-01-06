package org.betonquest.betonquest.quest.action.chat;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.online.OnlineAction;
import org.bukkit.entity.Player;

/**
 * The chat event.
 */
public class ChatAction implements OnlineAction {

    /**
     * The messages.
     */
    private final String[] messages;

    /**
     * Creates a new chat event.
     *
     * @param messages the messages
     */
    public ChatAction(final String... messages) {
        this.messages = messages.clone();
    }

    @Override
    public void execute(final OnlineProfile profile) {
        final Player player = profile.getPlayer();
        for (final String message : messages) {
            player.chat(message.replace("%player%", player.getName()));
        }
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
