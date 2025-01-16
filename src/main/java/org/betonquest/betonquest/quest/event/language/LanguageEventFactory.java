package org.betonquest.betonquest.quest.event.language;

import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.modules.data.PlayerDataStorage;

/**
 * Factory to create language events from {@link Instruction}s.
 */
public class LanguageEventFactory implements EventFactory {

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
    public Event parseEvent(final Instruction instruction) throws QuestException {
        final String language = instruction.next();
        return new LanguageEvent(language, dataStorage);
    }
}
