package org.betonquest.betonquest.quest.placeholder.tag;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholderFactory;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.config.Translations;
import org.betonquest.betonquest.database.GlobalData;

/**
 * A factory for creating GlobalTag placeholders.
 */
public class GlobalTagPlaceholderFactory extends AbstractTagPlaceholderFactory<GlobalData> implements PlayerlessPlaceholderFactory {

    /**
     * The {@link PluginMessage} instance.
     */
    private final Translations translations;

    /**
     * Create a new GlobalTagPlaceholderFactory.
     *
     * @param dataHolder   the data holder
     * @param translations the {@link PluginMessage} instance
     */
    public GlobalTagPlaceholderFactory(final GlobalData dataHolder, final Translations translations) {
        super(dataHolder);
        this.translations = translations;
    }

    @Override
    public PlayerlessPlaceholder parsePlayerless(final Instruction instruction) throws QuestException {
        return new GlobalTagPlaceholder(translations, dataHolder, instruction.nextElement(), instruction.getPackage(),
                instruction.bool().getFlag("papiMode", true));
    }
}
