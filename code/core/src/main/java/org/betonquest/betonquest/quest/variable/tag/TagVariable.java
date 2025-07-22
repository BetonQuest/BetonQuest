package org.betonquest.betonquest.quest.variable.tag;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;

/**
 * Exposes the presence of tags as a variable.
 * Originally implemented for use with the PAPI integration.
 */
public class TagVariable extends AbstractTagVariable<PlayerDataStorage> implements PlayerVariable {

    /**
     * Constructs a new TagVariable.
     *
     * @param pluginMessage the {@link PluginMessage} instance
     * @param data          the data holder
     * @param tagName       the name of the tag
     * @param questPackage  the quest package
     * @param papiMode      whether PAPI mode is enabled
     */
    public TagVariable(final PluginMessage pluginMessage, final PlayerDataStorage data, final String tagName, final QuestPackage questPackage, final boolean papiMode) {
        super(pluginMessage, data, tagName, questPackage, papiMode);
    }

    @Override
    public String getValue(final Profile profile) throws QuestException {
        return getValueFor(profile, data.get(profile).getTags());
    }
}
