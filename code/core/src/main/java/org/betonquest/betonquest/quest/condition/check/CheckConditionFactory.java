package org.betonquest.betonquest.quest.condition.check;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.TypeFactory;
import org.betonquest.betonquest.api.quest.condition.NullableConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.kernel.processor.adapter.ConditionAdapter;
import org.betonquest.betonquest.kernel.registry.quest.ConditionTypeRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory for {@link CheckCondition}s.
 */
public class CheckConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * The betonquest instructions creation instance.
     */
    private final Instructions instructions;

    /**
     * The condition type registry providing factories to parse the evaluated instruction.
     */
    private final ConditionTypeRegistry conditionTypeRegistry;

    /**
     * Create the check condition factory.
     *
     * @param instructions          the betonquest instructions creation instance
     * @param conditionTypeRegistry the condition type registry providing factories to parse the evaluated instruction
     */
    public CheckConditionFactory(final Instructions instructions, final ConditionTypeRegistry conditionTypeRegistry) {
        this.instructions = instructions;
        this.conditionTypeRegistry = conditionTypeRegistry;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        return new NullableConditionAdapter(new CheckCondition(parseConditions(instruction)));
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        return new NullableConditionAdapter(new CheckCondition(parseConditions(instruction)));
    }

    private List<ConditionAdapter> parseConditions(final Instruction instruction) throws QuestException {
        final List<ConditionAdapter> internalConditions = new ArrayList<>();
        final List<String> parts = instruction.getValueParts();
        if (parts.isEmpty()) {
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
     * Constructs a condition with a given instruction and returns it.
     */
    @Nullable
    private ConditionAdapter createCondition(final String instruction, final QuestPackage questPackage) throws QuestException {
        final String[] parts = instruction.split(" ");
        if (parts.length == 0) {
            throw new QuestException("Not enough arguments in internal condition");
        }
        final TypeFactory<ConditionAdapter> conditionFactory = conditionTypeRegistry.getFactory(parts[0]);
        try {
            final Instruction innerInstruction = instructions.create(questPackage, instruction);
            return conditionFactory.parseInstruction(innerInstruction);
        } catch (final QuestException e) {
            throw new QuestException("Error in internal condition: %s".formatted(e.getMessage()), e);
        }
    }
}
