package org.betonquest.betonquest.quest.condition.tag;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.modules.data.PlayerDataStorage;

/**
 * A condition that checks if a player has a certain tag.
 */
public class TagCondition implements PlayerCondition {

    /**
     * The tag to check for.
     */
    private final String tag;

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Constructor for the tag condition.
     *
     * @param tag         the tag to check for
     * @param dataStorage the storage providing player data
     */
    public TagCondition(final String tag, final PlayerDataStorage dataStorage) {
        this.tag = tag;
        this.dataStorage = dataStorage;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        return dataStorage.get(profile).hasTag(tag);
    }
}
