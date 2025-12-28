package org.betonquest.betonquest.quest.objective.die;

import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.bukkit.Location;

/**
 * Factory for creating {@link DieObjective} instances from {@link Instruction}s.
 */
public class DieObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the DieObjectiveFactory.
     */
    public DieObjectiveFactory() {
    }

    @Override
    public DefaultObjective parseInstruction(final Instruction instruction) throws QuestException {
        final FlagArgument<Boolean> cancel = instruction.bool().getFlag("cancel", false);
        final Argument<Location> location = instruction.location().get("respawn").orElse(null);
        return new DieObjective(instruction, cancel, location);
    }
}
