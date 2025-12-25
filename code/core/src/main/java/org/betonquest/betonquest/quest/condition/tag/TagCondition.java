package org.betonquest.betonquest.quest.condition.tag;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.data.PlayerDataStorage;

/**
 * A condition that checks if a player has a certain tag.
 */
public class TagCondition implements PlayerCondition {

    /**
     * The tag to check for.
     */
    private final Argument<String> tag;

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
    public TagCondition(final Argument<String> tag, final PlayerDataStorage dataStorage) {
        this.tag = tag;
        this.dataStorage = dataStorage;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        return dataStorage.get(profile).hasTag(tag.getValue(profile));
    }
}
