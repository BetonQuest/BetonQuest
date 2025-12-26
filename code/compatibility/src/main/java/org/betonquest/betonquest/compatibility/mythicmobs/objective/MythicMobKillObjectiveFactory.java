package org.betonquest.betonquest.compatibility.mythicmobs.objective;

import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;

import java.util.List;

/**
 * Factory for creating {@link MythicMobKillObjective} instances from {@link Instruction}s.
 */
public class MythicMobKillObjectiveFactory implements ObjectiveFactory {

    /**
     * Creates a new instance of the MythicMobKillObjectiveFactory.
     */
    public MythicMobKillObjectiveFactory() {
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Argument<List<String>> names = instruction.string().list().get();
        final Argument<IdentifierMode> mode = instruction.enumeration(IdentifierMode.class).get("mode", IdentifierMode.INTERNAL_NAME);
        final Argument<Number> targetAmount = instruction.number().atLeast(1).get("amount", 1);

        final Argument<Number> deathRadiusAllPlayers = instruction.number().get("deathRadiusAllPlayers", 0);
        final Argument<Number> neutralDeathRadiusAllPlayers = instruction.number().get("neutralDeathRadiusAllPlayers", 0);

        final Argument<Number> minMobLevel = instruction.number().get("minLevel", Double.NEGATIVE_INFINITY);
        final Argument<Number> maxMobLevel = instruction.number().get("maxLevel", Double.POSITIVE_INFINITY);
        final Argument<String> marked = instruction.packageIdentifier().get("marked").orElse(null);
        return new MythicMobKillObjective(instruction, targetAmount, names, mode, minMobLevel, maxMobLevel, deathRadiusAllPlayers, neutralDeathRadiusAllPlayers, marked);
    }
}
