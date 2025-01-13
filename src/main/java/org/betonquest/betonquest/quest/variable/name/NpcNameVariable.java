package org.betonquest.betonquest.quest.variable.name;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.modules.data.PlayerDataStorage;

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
    public String getValue(final Profile profile) {
        final Conversation conv = Conversation.getConversation(profile);
        if (conv == null) {
            return "";
        }
        return conv.getData().getQuester(dataStorage.get(profile).getLanguage());
    }
}
