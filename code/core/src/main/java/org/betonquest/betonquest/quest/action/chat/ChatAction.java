package org.betonquest.betonquest.quest.action.chat;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.OnlineAction;
import org.bukkit.entity.Player;

/**
 * The chat action.
 */
public class ChatAction implements OnlineAction {

    /**
     * The messages.
     */
    private final String[] messages;

    /**
     * Creates a new chat action.
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
