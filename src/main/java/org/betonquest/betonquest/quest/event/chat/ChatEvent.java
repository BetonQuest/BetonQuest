package org.betonquest.betonquest.quest.event.chat;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.exceptions.QuestException;
import org.bukkit.entity.Player;

/**
 * The chat event.
 */
public class ChatEvent implements OnlineEvent {

    /**
     * The messages.
     */
    private final String[] messages;

    /**
     * Creates a new chat event.
     *
     * @param messages the messages
     */
    public ChatEvent(final String... messages) {
        this.messages = messages.clone();
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final Player player = profile.getPlayer();
        for (final String message : messages) {
            player.chat(message.replace("%player%", player.getName()));
        }
    }
}
