package org.betonquest.betonquest.quest.condition.entity;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.SimpleArgumentParser;
import org.betonquest.betonquest.api.instruction.argument.parser.EnumParser;
import org.betonquest.betonquest.api.instruction.argument.parser.NumberParser;
import org.betonquest.betonquest.api.quest.condition.NullableConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerlessCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerlessConditionFactory;
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
        final Argument<List<Map.Entry<EntityType, Integer>>> entityAmounts =
                instruction.parse(EntityAmount.ENTITY_AMOUNT).list().distinct(Map.Entry::getKey).get();
        final Argument<Location> location = instruction.location().get();
        final Argument<Number> range = instruction.number().get();
        final Argument<Component> name = instruction.component().get("name").orElse(null);
        final Argument<String> marked = instruction.packageIdentifier().get("marked").orElse(null);
        return new EntityCondition(entityAmounts, location, range, name, marked);
    }

    /**
     * Parses a string to a Spell with level.
     */
    private static final class EntityAmount implements SimpleArgumentParser<Map.Entry<EntityType, Integer>> {

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
