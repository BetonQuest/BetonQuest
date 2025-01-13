package org.betonquest.betonquest.quest.condition.tag;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.modules.data.PlayerDataStorage;
import org.betonquest.betonquest.utils.Utils;

/**
 * Factory to create tag conditions from {@link Instruction}s.
 */
public class TagConditionFactory implements PlayerConditionFactory {

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Creates the tag condition factory.
     *
     * @param dataStorage the storage providing player data
     */
    public TagConditionFactory(final PlayerDataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final String tag = Utils.addPackage(instruction.getPackage(), instruction.next());
        return new TagCondition(tag, dataStorage);
    }
}
