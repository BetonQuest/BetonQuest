package org.betonquest.betonquest.quest.condition.check;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.id.NoID;
import org.betonquest.betonquest.quest.legacy.LegacyTypeFactory;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory for {@link CheckCondition}s.
 */
public class CheckConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Create the check condition factory.
     *
     * @param log the logger
     */
    public CheckConditionFactory(final BetonQuestLogger log) {
        this.log = log;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        return new NullableConditionAdapter(new CheckCondition(parseConditions(instruction)));
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        return new NullableConditionAdapter(new CheckCondition(parseConditions(instruction)));
    }

    private List<Condition> parseConditions(final Instruction instruction) throws QuestException {
        final List<Condition> internalConditions = new ArrayList<>();
        final String[] parts = instruction.getAllParts();
        if (parts.length == 0) {
            throw new QuestException("Not enough arguments");
        }
        final QuestPackage questPackage = instruction.getPackage();
        StringBuilder builder = new StringBuilder();
        for (final String part : parts) {
            if (!part.isEmpty() && part.charAt(0) == '^') {
                if (!builder.isEmpty()) {
                    internalConditions.add(createCondition(builder.toString().trim(), questPackage));
                    builder = new StringBuilder();
                }
                builder.append(part.substring(1)).append(' ');
            } else {
                builder.append(part).append(' ');
            }
        }
        internalConditions.add(createCondition(builder.toString().trim(), questPackage));
        return internalConditions;
    }

    /**
     * Constructs a condition with given instruction and returns it.
     */
    @Nullable
    private Condition createCondition(final String instruction, final QuestPackage questPackage) throws QuestException {
        final String[] parts = instruction.split(" ");
        if (parts.length == 0) {
            throw new QuestException("Not enough arguments in internal condition");
        }
        final LegacyTypeFactory<Condition> conditionFactory = BetonQuest.getInstance().getQuestRegistries().getConditionTypes().getFactory(parts[0]);
        if (conditionFactory == null) {
            // if it's null then there is no such type registered, log an error
            throw new QuestException("Condition type " + parts[0] + " is not registered, check if it's"
                    + " spelled correctly in internal condition");
        }
        try {
            final Instruction innerInstruction = new Instruction(BetonQuest.getInstance().getLoggerFactory().create(Instruction.class), questPackage, new NoID(questPackage), instruction);
            return conditionFactory.parseInstruction(innerInstruction);
        } catch (final ObjectNotFoundException e) {
            if (e.getCause() instanceof QuestException) {
                throw new QuestException("Error in internal condition: " + e.getCause().getMessage(), e);
            } else {
                log.reportException(questPackage, e);
            }
        }
        return null;
    }
}
