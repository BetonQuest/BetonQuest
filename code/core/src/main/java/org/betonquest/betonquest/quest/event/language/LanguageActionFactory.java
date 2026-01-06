package org.betonquest.betonquest.quest.event.language;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.data.PlayerDataStorage;

/**
 * Factory to create language events from {@link Instruction}s.
 */
public class LanguageActionFactory implements PlayerActionFactory {

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Create the language event factory.
     *
     * @param dataStorage the storage providing player data
     */
    public LanguageActionFactory(final PlayerDataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<String> language = instruction.string().get();
        return new LanguageAction(language, dataStorage);
    }
}
