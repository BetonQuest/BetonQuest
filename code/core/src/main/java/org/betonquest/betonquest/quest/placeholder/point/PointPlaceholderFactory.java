package org.betonquest.betonquest.quest.placeholder.point;

import org.apache.commons.lang3.tuple.Triple;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholderFactory;
import org.betonquest.betonquest.data.PlayerDataStorage;

/**
 * A factory for creating Point placeholders.
 */
public class PointPlaceholderFactory extends AbstractPointPlaceholderFactory<PlayerDataStorage> implements PlayerPlaceholderFactory {

    /**
     * Create a new Point placeholder factory.
     *
     * @param dataStorage the player data storage
     */
    public PointPlaceholderFactory(final PlayerDataStorage dataStorage) {
        super(dataStorage);
    }

    @Override
    public PlayerPlaceholder parsePlayer(final Instruction instruction) throws QuestException {
        final Triple<String, Integer, PointCalculationType> values = parseInstruction(instruction);
        return new PointPlaceholder(dataHolder, values.getLeft(), values.getMiddle(), values.getRight());
    }
}
