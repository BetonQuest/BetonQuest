package org.betonquest.betonquest.quest.event.entity;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
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

import java.util.List;

/**
 * Factory for {@link RemoveEntityEvent} to create from {@link Instruction}.
 */
public class RemoveEntityEventFactory implements PlayerEventFactory, PlayerlessEventFactory {

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Creates a new KillMobEventFactory.
     *
     * @param data the data for primary server thread access
     */
    public RemoveEntityEventFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadEvent(createRemoveEntityEvent(instruction), data);
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadPlayerlessEvent(createRemoveEntityEvent(instruction), data);
    }

    private NullableEventAdapter createRemoveEntityEvent(final Instruction instruction) throws QuestException {
        final Variable<List<EntityType>> types = instruction.enumeration(EntityType.class).getList();
        final Variable<Location> loc = instruction.location().get();
        final Variable<Number> range = instruction.number().get();
        final boolean kill = instruction.hasArgument("kill");
        final Variable<Component> name = instruction.component().get("name").orElse(null);
        final Variable<String> marked = instruction.packageIdentifier().get("marked").orElse(null);
        return new NullableEventAdapter(new RemoveEntityEvent(types, loc, range, name, marked, kill));
    }
}
