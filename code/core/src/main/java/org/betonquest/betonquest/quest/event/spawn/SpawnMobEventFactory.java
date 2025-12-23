package org.betonquest.betonquest.quest.event.spawn;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.QuestItemWrapper;
import org.betonquest.betonquest.api.instruction.argument.DecoratableArgument;
import org.betonquest.betonquest.api.instruction.argument.InstructionIdentifierArgument;
import org.betonquest.betonquest.api.instruction.argument.PackageArgument;
import org.betonquest.betonquest.api.instruction.argument.parser.EnumParser;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadEvent;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadPlayerlessEvent;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;

import java.util.List;

/**
 * Factory to create spawn mob events from {@link Instruction}s.
 */
public class SpawnMobEventFactory implements PlayerEventFactory, PlayerlessEventFactory {

    /**
     * Data used for primary server access.
     */
    private final PrimaryServerThreadData data;

    /**
     * The parser for entity types.
     */
    private final DecoratableArgument<EntityType> entityTypeParser;

    /**
     * Create a new factory for {@link SpawnMobEvent}s.
     *
     * @param data the primary server thread data required for main thread checking
     */
    public SpawnMobEventFactory(final PrimaryServerThreadData data) {
        this.data = data;
        this.entityTypeParser = new DecoratableArgument<>(new EnumParser<>(EntityType.class))
                .validate(type -> type.getEntityClass() != null && Mob.class.isAssignableFrom(type.getEntityClass()),
                        "EntityType '%s' is not a mob");
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
        final Variable<Location> loc = instruction.get(instruction.getParsers().location());
        final Variable<EntityType> type = instruction.get(entityTypeParser);
        final Variable<Number> amount = instruction.get(instruction.getParsers().number());
        final Variable<Component> name = instruction.getValue("name", instruction.getParsers().component());
        final Variable<String> marked = instruction.getValue("marked", PackageArgument.IDENTIFIER);
        final Variable<QuestItemWrapper> helmet = instruction.getValue("h", InstructionIdentifierArgument.ITEM);
        final Variable<QuestItemWrapper> chestplate = instruction.getValue("c", InstructionIdentifierArgument.ITEM);
        final Variable<QuestItemWrapper> leggings = instruction.getValue("l", InstructionIdentifierArgument.ITEM);
        final Variable<QuestItemWrapper> boots = instruction.getValue("b", InstructionIdentifierArgument.ITEM);
        final Variable<QuestItemWrapper> mainHand = instruction.getValue("m", InstructionIdentifierArgument.ITEM);
        final Variable<QuestItemWrapper> offHand = instruction.getValue("o", InstructionIdentifierArgument.ITEM);
        final Variable<List<QuestItemWrapper>> drops = instruction.getValueList("drops", InstructionIdentifierArgument.ITEM);
        final Equipment equipment = new Equipment(helmet, chestplate, leggings, boots, mainHand, offHand, drops);
        final SpawnMobEvent event = new SpawnMobEvent(loc, type, equipment, amount, name, marked);
        return new NullableEventAdapter(event);
    }
}
