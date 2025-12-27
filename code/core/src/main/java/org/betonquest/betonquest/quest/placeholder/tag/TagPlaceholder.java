package org.betonquest.betonquest.quest.placeholder.tag;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;

/**
 * Exposes the presence of tags as a placeholder.
 * Originally implemented for use with the PAPI integration.
 */
public class TagPlaceholder extends AbstractTagPlaceholder<PlayerDataStorage> implements PlayerPlaceholder {

    /**
     * Constructs a new TagPlaceholder.
     *
     * @param pluginMessage the {@link PluginMessage} instance
     * @param data          the data holder
     * @param tagName       the name of the tag
     * @param questPackage  the quest package
     * @param papiMode      whether PAPI mode is enabled
     */
    public TagPlaceholder(final PluginMessage pluginMessage, final PlayerDataStorage data, final String tagName,
                       final QuestPackage questPackage, final FlagArgument<Boolean> papiMode) {
        super(pluginMessage, data, tagName, questPackage, papiMode);
    }

    @Override
    public String getValue(final Profile profile) throws QuestException {
        return getValueFor(profile, data.get(profile).getTags());
    }
}
