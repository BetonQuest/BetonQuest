package org.betonquest.betonquest.quest.condition.entity;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerlessCondition;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.entity.EntityType;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

/**
 * Factory for {@link EntityCondition}s.
 */
public class EntityConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * Data used for condition check on the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * Processor to create new variables.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Create the entity condition factory.
     *
     * @param data              the data used for checking the condition on the main thread
     * @param variableProcessor the variable processor to create new variables with
     */
    public EntityConditionFactory(final PrimaryServerThreadData data, final VariableProcessor variableProcessor) {
        this.data = data;
        this.variableProcessor = variableProcessor;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadPlayerCondition(new NullableConditionAdapter(parseEntityCondition(instruction)), data);
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadPlayerlessCondition(new NullableConditionAdapter(parseEntityCondition(instruction)), data);
    }

    private EntityCondition parseEntityCondition(final Instruction instruction) throws QuestException {
        final Map<EntityType, VariableNumber> entityAmounts = getEntityAmounts(instruction);
        final VariableLocation location = instruction.getLocation();
        final VariableNumber range = instruction.getVarNum();
        final String nameString = instruction.getOptional("name");
        final VariableString name = nameString == null ? null : new VariableString(variableProcessor,
                instruction.getPackage(),
                Utils.format(nameString, true, false).replace('_', ' ')
        );
        final String markedString = instruction.getOptional("marked");
        final VariableString marked = markedString == null ? null : new VariableString(variableProcessor,
                instruction.getPackage(),
                Utils.addPackage(instruction.getPackage(), markedString)
        );
        return new EntityCondition(entityAmounts, location, range, name, marked);
    }

    private Map<EntityType, VariableNumber> getEntityAmounts(final Instruction instruction) throws QuestException {
        final Map<EntityType, VariableNumber> entityAmounts = new EnumMap<>(EntityType.class);
        final String[] rawTypes = instruction.getArray();
        for (final String rawType : rawTypes) {
            final String[] typeParts = rawType.split(":");
            try {
                final EntityType type = EntityType.valueOf(typeParts[0].toUpperCase(Locale.ROOT));
                final VariableNumber amount = typeParts.length == 2 ? instruction.getVarNum(typeParts[1]) : new VariableNumber(variableProcessor, instruction.getPackage(), "1");
                entityAmounts.put(type, amount);
            } catch (final IllegalArgumentException e) {
                throw new QuestException("Invalid entity type: " + typeParts[0], e);
            } catch (final QuestException e) {
                throw new QuestException("Could not parse entity amount: " + typeParts[1], e);
            }
        }
        return entityAmounts;
    }
}
