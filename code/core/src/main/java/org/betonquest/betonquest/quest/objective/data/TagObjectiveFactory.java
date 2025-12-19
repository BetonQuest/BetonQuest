package org.betonquest.betonquest.quest.objective.data;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.PackageArgument;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.data.PlayerDataStorage;

/**
 * Factory to create {@link TagObjective}s from {@link Instruction}s.
 */
public class TagObjectiveFactory implements ObjectiveFactory {
    /**
     * Storage for player data.
     */
    private final PlayerDataStorage playerDataStorage;

    /**
     * Create a new Tag Objective Factory.
     *
     * @param playerDataStorage the storage for player data
     */
    public TagObjectiveFactory(final PlayerDataStorage playerDataStorage) {
        this.playerDataStorage = playerDataStorage;
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        return new TagObjective(instruction, playerDataStorage, instruction.get(PackageArgument.IDENTIFIER));
    }
}
