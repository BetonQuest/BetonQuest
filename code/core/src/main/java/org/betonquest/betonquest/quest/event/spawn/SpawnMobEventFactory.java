package org.betonquest.betonquest.quest.event.spawn;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.DecoratedArgumentParser;
import org.betonquest.betonquest.api.instruction.argument.parser.EnumParser;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadEvent;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadPlayerlessEvent;
import org.betonquest.betonquest.lib.instruction.argument.DecoratableArgumentParser;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;

import java.util.Collections;
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
    private final DecoratedArgumentParser<EntityType> entityTypeParser;

    /**
     * Create a new factory for {@link SpawnMobEvent}s.
     *
     * @param data the primary server thread data required for main thread checking
     */
    public SpawnMobEventFactory(final PrimaryServerThreadData data) {
        this.data = data;
        this.entityTypeParser = new DecoratableArgumentParser<>(new EnumParser<>(EntityType.class))
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
        final Variable<Location> loc = instruction.location().get();
        final Variable<EntityType> type = instruction.parse(entityTypeParser).get();
        final Variable<Number> amount = instruction.number().get();
        final Variable<Component> name = instruction.component().get("name").orElse(null);
        final Variable<String> marked = instruction.packageIdentifier().get("marked").orElse(null);
        final Variable<ItemWrapper> helmet = instruction.item().get("h").orElse(null);
        final Variable<ItemWrapper> chestplate = instruction.item().get("c").orElse(null);
        final Variable<ItemWrapper> leggings = instruction.item().get("l").orElse(null);
        final Variable<ItemWrapper> boots = instruction.item().get("b").orElse(null);
        final Variable<ItemWrapper> mainHand = instruction.item().get("m").orElse(null);
        final Variable<ItemWrapper> offHand = instruction.item().get("o").orElse(null);
        final Variable<List<ItemWrapper>> drops = instruction.item().getList("drops", Collections.emptyList());
        final Equipment equipment = new Equipment(helmet, chestplate, leggings, boots, mainHand, offHand, drops);
        final SpawnMobEvent event = new SpawnMobEvent(loc, type, equipment, amount, name, marked);
        return new NullableEventAdapter(event);
    }
}
