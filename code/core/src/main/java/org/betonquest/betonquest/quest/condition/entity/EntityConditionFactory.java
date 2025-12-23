package org.betonquest.betonquest.quest.condition.entity;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.argument.PackageArgument;
import org.betonquest.betonquest.api.instruction.argument.parser.EnumParser;
import org.betonquest.betonquest.api.instruction.argument.parser.NumberParser;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.instruction.variable.VariableList;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
import org.betonquest.betonquest.api.quest.condition.nullable.NullableConditionAdapter;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.List;
import java.util.Map;

/**
 * Factory for {@link EntityCondition}s.
 */
public class EntityConditionFactory implements PlayerConditionFactory, PlayerlessConditionFactory {

    /**
     * Create the entity condition factory.
     */
    public EntityConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        return new NullableConditionAdapter(parseEntityCondition(instruction));
    }

    @Override
    public PlayerlessCondition parsePlayerless(final Instruction instruction) throws QuestException {
        return new NullableConditionAdapter(parseEntityCondition(instruction));
    }

    private EntityCondition parseEntityCondition(final Instruction instruction) throws QuestException {
        final Variable<List<Map.Entry<EntityType, Integer>>> entityAmounts = instruction.getList(EntityAmount.ENTITY_AMOUNT, VariableList.notDuplicateKeyChecker());
        final Variable<Location> location = instruction.get(instruction.getParsers().location());
        final Variable<Number> range = instruction.get(instruction.getParsers().number());
        final Variable<Component> name = instruction.getValue("name", instruction.getParsers().component());
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
            final EntityType type = new EnumParser<>(EntityType.class).apply(parts[0]);
            final int amount = parts.length == 2 ? NumberParser.DEFAULT.apply(parts[1]).intValue() : 1;
            return Map.entry(type, amount);
        }
    }
}
