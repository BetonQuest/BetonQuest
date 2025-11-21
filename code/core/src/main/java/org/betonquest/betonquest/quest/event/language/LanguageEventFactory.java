package org.betonquest.betonquest.quest.event.language;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.data.PlayerDataStorage;

/**
 * Factory to create language events from {@link Instruction}s.
 */
public class LanguageEventFactory implements PlayerEventFactory {

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Create the language event factory.
     *
     * @param dataStorage the storage providing player data
     */
    public LanguageEventFactory(final PlayerDataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<String> language = instruction.get(Argument.STRING);
        return new LanguageEvent(language, dataStorage);
    }
}
