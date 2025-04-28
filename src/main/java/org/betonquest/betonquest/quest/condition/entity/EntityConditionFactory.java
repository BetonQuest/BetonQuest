package org.betonquest.betonquest.quest.condition.entity;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.argument.PackageArgument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerlessCondition;
import org.betonquest.betonquest.util.Utils;
import org.bukkit.Location;
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
     * Create the entity condition factory.
     *
     * @param data the data used for checking the condition on the main thread
     */
    public EntityConditionFactory(final PrimaryServerThreadData data) {
        this.data = data;
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
        final Map<EntityType, Variable<Number>> entityAmounts = getEntityAmounts(instruction);
        final Variable<Location> location = instruction.getVariable(Argument.LOCATION);
        final Variable<Number> range = instruction.getVariable(Argument.NUMBER);
        final String nameString = instruction.getOptional("name");
        final Variable<String> name = nameString == null ? null : instruction.getVariable(
                Utils.format(nameString, true, false), Argument.STRING);
        final Variable<String> marked = instruction.get(instruction.getOptional("marked"), PackageArgument.IDENTIFIER);
        return new EntityCondition(entityAmounts, location, range, name, marked);
    }

    private Map<EntityType, Variable<Number>> getEntityAmounts(final Instruction instruction) throws QuestException {
        final Map<EntityType, Variable<Number>> entityAmounts = new EnumMap<>(EntityType.class);
        for (final String rawType : instruction.getList()) {
            final String[] typeParts = rawType.split(":");
            try {
                final EntityType type = EntityType.valueOf(typeParts[0].toUpperCase(Locale.ROOT));
                final Variable<Number> amount = typeParts.length == 2 ? instruction.getVariable(typeParts[1], Argument.NUMBER) : new Variable<>(1);
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
