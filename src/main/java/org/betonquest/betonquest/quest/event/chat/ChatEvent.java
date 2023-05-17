package org.betonquest.betonquest.quest.event.chat;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.entity.Player;

/**
 * The chat event.
 */
public class ChatEvent implements Event {

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
    public void execute(final Profile profile) throws QuestRuntimeException {
        final Player player = profile.getOnlineProfile().get().getPlayer();
        for (final String message : messages) {
            player.chat(message.replace("%player%", player.getName()));
        }
    }
}
