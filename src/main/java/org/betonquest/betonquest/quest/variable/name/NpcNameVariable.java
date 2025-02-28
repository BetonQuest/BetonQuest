package org.betonquest.betonquest.quest.variable.name;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.data.PlayerDataStorage;

/**
 * This variable resolves into the name of the NPC.
 */
public class NpcNameVariable implements PlayerVariable {

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Create a NpcName variable.
     *
     * @param dataStorage the class to get the {@link org.betonquest.betonquest.database.PlayerData}
     */
    public NpcNameVariable(final PlayerDataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    @Override
    public String getValue(final Profile profile) throws QuestException {
        final Conversation conv = Conversation.getConversation(profile);
        if (conv == null) {
            return "";
        }
        return conv.getData().getPublicData().quester().getResolved(dataStorage.get(profile).getLanguage(), profile);
    }
}
