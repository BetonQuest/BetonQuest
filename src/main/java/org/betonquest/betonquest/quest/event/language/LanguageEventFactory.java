package org.betonquest.betonquest.quest.event.language;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.instruction.Instruction;

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
        final String language = instruction.next();
        return new LanguageEvent(language, dataStorage);
    }
}
