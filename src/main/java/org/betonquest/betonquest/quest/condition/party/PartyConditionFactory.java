package org.betonquest.betonquest.quest.condition.party;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

/**
 * Factory to create party conditions from {@link Instruction}s.
 */
public class PartyConditionFactory implements PlayerConditionFactory {

    /**
     * Logger factory to create a logger for events.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create the party condition factory.
     *
     * @param loggerFactory the logger factory to create a logger for the condition
     */
    public PartyConditionFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws InstructionParseException {
        final VariableNumber range = instruction.getVarNum();
        final ConditionID[] conditions = instruction.getList(instruction::getCondition).toArray(new ConditionID[0]);
        final ConditionID[] everyone = instruction.getList(instruction.getOptional("every"), instruction::getCondition).toArray(new ConditionID[0]);
        final ConditionID[] anyone = instruction.getList(instruction.getOptional("any"), instruction::getCondition).toArray(new ConditionID[0]);
        final VariableNumber count = instruction.getVarNum(instruction.getOptional("count"));

        final BetonQuestLogger log = loggerFactory.create(PartyCondition.class);
        return new OnlineConditionAdapter(new PartyCondition(range, conditions, everyone, anyone, count), log, instruction.getPackage());
    }
}
