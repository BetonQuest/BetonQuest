package org.betonquest.betonquest.quest.variable.name;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.conversation.Conversation;

/**
 * This variable resolves into the name of the NPC.
 */
public class NpcNameVariable implements PlayerVariable {
    /**
     * Class to get the player data from.
     */
    private final BetonQuest plugin;

    /**
     * Create a NpcName variable.
     *
     * @param plugin the class to get the {@link org.betonquest.betonquest.database.PlayerData}
     */
    public NpcNameVariable(final BetonQuest plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getValue(final Profile profile) {
        final Conversation conv = Conversation.getConversation(profile);
        if (conv == null) {
            return "";
        }
        return conv.getData().getQuester(plugin.getPlayerData(profile).getLanguage());
    }
}
