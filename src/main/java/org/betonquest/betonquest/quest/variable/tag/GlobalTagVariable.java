package org.betonquest.betonquest.quest.variable.tag;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.database.GlobalData;

/**
 * Exposes the presence of global tags as a variable.
 * Originally implemented for use with the PAPI integration.
 */
public class GlobalTagVariable extends AbstractTagVariable<GlobalData> implements PlayerlessVariable {
    /**
     * Constructs a new GlobalTagVariable.
     *
     * @param data         the data holder
     * @param tagName      the tag to check for
     * @param questPackage the quest package to check for the tag
     * @param papiMode     whether to return true/false or the configured messages
     */
    public GlobalTagVariable(final GlobalData data, final String tagName, final QuestPackage questPackage, final boolean papiMode) {
        super(data, tagName, questPackage, papiMode);
    }

    @Override
    public String getValue() throws QuestException {
        return getValueFor(data.getTags());
    }
}
