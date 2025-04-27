package org.betonquest.betonquest.compatibility.npc.citizens.objective;

import net.citizensnpcs.api.npc.NPC;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.common.function.QuestBiPredicate;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;

/**
 * Factory for creating {@link NPCKillObjective} instances from {@link Instruction}s.
 */
public class NPCKillObjectiveFactory implements ObjectiveFactory {
    /**
     * Logger factory to create a logger for the objectives.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Creates a new instance of the NPCKillObjectiveFactory.
     *
     * @param loggerFactory the logger factory to create a logger for the objectives
     */
    public NPCKillObjectiveFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public Objective parseInstruction(final Instruction instruction) throws QuestException {
        final Instruction npcInstruction = instruction.getID(NpcID::new).getInstruction();
        if (!"citizens".equals(npcInstruction.getPart(0))) {
            throw new QuestException("Cannot use non-Citizens NPC ID!");
        }
        final String argument = npcInstruction.getPart(1);
        final QuestBiPredicate<NPC, Profile> predicate;
        if (npcInstruction.hasArgument("byName")) {
            predicate = (npc, profile) -> argument.equals(npc.getName());
        } else {
            final Variable<Number> npcId = npcInstruction.getVariable(argument, Argument.NUMBER);
            predicate = (npc, profile) -> npcId.getValue(profile).intValue() == npc.getId();
        }
        final Variable<Number> targetAmount = instruction.getVariable(instruction.getOptional("amount", "1"), Argument.NUMBER_NOT_LESS_THAN_ONE);
        final BetonQuestLogger log = loggerFactory.create(NPCKillObjective.class);
        return new NPCKillObjective(instruction, targetAmount, log, predicate);
    }
}
