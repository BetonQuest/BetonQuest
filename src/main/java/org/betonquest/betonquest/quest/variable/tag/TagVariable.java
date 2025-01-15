package org.betonquest.betonquest.quest.variable.tag;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.modules.data.PlayerDataStorage;

/**
 * Exposes the presence of tags as a variable.
 * Originally implemented for use with the PAPI integration.
 */
public class TagVariable extends AbstractTagVariable<PlayerDataStorage> implements PlayerVariable {

    /**
     * Constructs a new TagVariable.
     *
     * @param data         the data holder
     * @param tagName      the name of the tag
     * @param questPackage the quest package
     * @param papiMode     whether PAPI mode is enabled
     */
    public TagVariable(final PlayerDataStorage data, final String tagName, final QuestPackage questPackage, final boolean papiMode) {
        super(data, tagName, questPackage, papiMode);
    }

    @Override
    public String getValue(final Profile profile) throws QuestException {
        return getValueFor(data.get(profile).getTags());
    }
}
