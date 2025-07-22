package org.betonquest.betonquest.quest.variable.name;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.conversation.Conversation;

/**
 * This variable resolves into the name of the Npc in the conversation.
 */
public class QuesterVariable implements PlayerVariable {

    /**
     * Create a NpcName variable.
     */
    public QuesterVariable() {
    }

    @Override
    public String getValue(final Profile profile) throws QuestException {
        final Conversation conv = Conversation.getConversation(profile);
        if (conv == null) {
            return "";
        }
        return LegacyComponentSerializer.legacySection().serialize(conv.getData().getPublicData().quester().asComponent(profile));
    }
}
