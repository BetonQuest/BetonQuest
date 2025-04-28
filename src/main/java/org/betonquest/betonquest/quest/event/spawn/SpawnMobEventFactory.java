package org.betonquest.betonquest.quest.event.spawn;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.Item;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.argument.PackageArgument;
import org.betonquest.betonquest.instruction.types.EnumParser;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.instruction.variable.VariableList;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadPlayerlessEvent;
import org.betonquest.betonquest.util.Utils;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;

/**
 * Factory to create spawn mob events from {@link Instruction}s.
 */
public class SpawnMobEventFactory implements PlayerEventFactory, PlayerlessEventFactory {
    /**
     * Data used for primary server access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new factory for {@link SpawnMobEvent}s.
     *
     * @param data the primary server thread data required for main thread checking
     */
    public SpawnMobEventFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadEvent(createSpawnMobEvent(instruction), data);
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadPlayerlessEvent(createSpawnMobEvent(instruction), data);
    }

    /**
     * Creates a new spawn mob event from the given instruction.
     *
     * @param instruction the instruction to create the event from
     * @return the created event
     * @throws QuestException if the instruction could not be parsed
     */
    public NullableEventAdapter createSpawnMobEvent(final Instruction instruction) throws QuestException {
        final Variable<Location> loc = instruction.getVariable(Argument.LOCATION);
        final Variable<EntityType> type = instruction.getVariable(new EntityTypeParser());
        final Variable<Number> amount = instruction.getVariable(Argument.NUMBER);
        final String nameString = instruction.getOptional("name");
        final Variable<String> name = nameString == null ? null : instruction.getVariable(Utils.format(
                nameString, true, false), Argument.STRING);
        final Variable<String> marked = instruction.get(instruction.getOptional("marked"), PackageArgument.IDENTIFIER);
        final Variable<Item> helmet = instruction.get(instruction.getOptional("h"), PackageArgument.ITEM);
        final Variable<Item> chestplate = instruction.get(instruction.getOptional("c"), PackageArgument.ITEM);
        final Variable<Item> leggings = instruction.get(instruction.getOptional("l"), PackageArgument.ITEM);
        final Variable<Item> boots = instruction.get(instruction.getOptional("b"), PackageArgument.ITEM);
        final Variable<Item> mainHand = instruction.get(instruction.getOptional("m"), PackageArgument.ITEM);
        final Variable<Item> offHand = instruction.get(instruction.getOptional("o"), PackageArgument.ITEM);
        final VariableList<Item> drops = instruction.get(instruction.getOptional("drops", ""), PackageArgument.ofList(PackageArgument.ITEM));
        final Equipment equipment = new Equipment(helmet, chestplate, leggings, boots, mainHand, offHand, drops);
        final SpawnMobEvent event = new SpawnMobEvent(loc, type, equipment, amount, name, marked);
        return new NullableEventAdapter(event);
    }

    /**
     * Parser for entity types.
     */
    private static class EntityTypeParser extends EnumParser<EntityType> {

        /**
         * Creates a new parser for enums.
         */
        public EntityTypeParser() {
            super(EntityType.class);
        }

        @Override
        public EntityType apply(final String string) throws QuestException {
            final EntityType type = super.apply(string);
            if (type.getEntityClass() == null || !Mob.class.isAssignableFrom(type.getEntityClass())) {
                throw new QuestException("The entity type must be a mob");
            }
            return type;
        }
    }
}
