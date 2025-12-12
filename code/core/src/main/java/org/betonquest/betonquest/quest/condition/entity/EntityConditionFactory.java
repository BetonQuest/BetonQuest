package org.betonquest.betonquest.quest.condition.entity;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.argument.PackageArgument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.instruction.variable.VariableList;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.thread.PrimaryServerThreadPlayerCondition;
import org.betonquest.betonquest.api.quest.condition.thread.PrimaryServerThreadPlayerlessCondition;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.List;
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
        final Variable<List<Map.Entry<EntityType, Integer>>> entityAmounts = instruction.getList(EntityAmount.ENTITY_AMOUNT, VariableList.notDuplicateKeyChecker());
        final Variable<Location> location = instruction.get(Argument.LOCATION);
        final Variable<Number> range = instruction.get(Argument.NUMBER);
        final Variable<Component> name = instruction.getValue("name", Argument.MESSAGE);
        final Variable<String> marked = instruction.getValue("marked", PackageArgument.IDENTIFIER);
        return new EntityCondition(entityAmounts, location, range, name, marked);
    }

    /**
     * Parses a string to a Spell with level.
     */
    private static final class EntityAmount implements Argument<Map.Entry<EntityType, Integer>> {
        /**
         * The default instance of {@link EntityAmount}.
         */
        public static final EntityAmount ENTITY_AMOUNT = new EntityAmount();

        @Override
        public Map.Entry<EntityType, Integer> apply(final String string) throws QuestException {
            final String[] parts = string.split(":");
            final EntityType type = Argument.ENUM(EntityType.class).apply(parts[0]);
            final int amount = parts.length == 2 ? NUMBER.apply(parts[1]).intValue() : 1;
            return Map.entry(type, amount);
        }
    }
}
