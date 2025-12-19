package org.betonquest.betonquest.kernel.registry.feature;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.api.text.TextParserRegistry;
import org.betonquest.betonquest.kernel.registry.FactoryRegistry;

/**
 * A registry for text parsers.
 */
public class TextParserRegistryImpl extends FactoryRegistry<TextParser> implements TextParserRegistry {

    /**
     * Create a new type registry.
     *
     * @param log the logger that will be used for logging
     */
    public TextParserRegistryImpl(final BetonQuestLogger log) {
        super(log, "TextParser");
    }

    @Override
    public TextParser get(final String name) throws QuestException {
        return getFactory(name);
    }
}
