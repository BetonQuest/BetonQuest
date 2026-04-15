package org.betonquest.betonquest.quest.placeholder.tag;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.Localizations;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholder;
import org.betonquest.betonquest.database.GlobalData;

/**
 * Exposes the presence of global tags as a placeholder.
 * Originally implemented for use with the PAPI integration.
 */
public class GlobalTagPlaceholder extends AbstractTagPlaceholder<GlobalData> implements PlayerlessPlaceholder {

    /**
     * Constructs a new GlobalTagPlaceholder.
     *
     * @param localizations the {@link Localizations} instance
     * @param data          the data holder
     * @param tagName       the tag to check for
     * @param questPackage  the quest package to check for the tag
     * @param papiMode      whether to return true/false or the configured messages
     */
    public GlobalTagPlaceholder(final Localizations localizations, final GlobalData data, final String tagName,
                                final QuestPackage questPackage, final FlagArgument<Boolean> papiMode) {
        super(localizations, data, tagName, questPackage, papiMode);
    }

    @Override
    public String getValue() throws QuestException {
        return getValueFor(null, data.tags().get());
    }
}
