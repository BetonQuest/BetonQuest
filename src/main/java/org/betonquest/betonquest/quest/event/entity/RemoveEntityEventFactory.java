package org.betonquest.betonquest.quest.event.entity;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.argument.PackageArgument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadPlayerlessEvent;
import org.betonquest.betonquest.util.Utils;
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
        final Variable<List<EntityType>> types = instruction.getList(Argument.ENUM(EntityType.class));
        final Variable<Location> loc = instruction.get(Argument.LOCATION);
        final Variable<Number> range = instruction.get(Argument.NUMBER);
        final boolean kill = instruction.hasArgument("kill");
        final String nameString = instruction.getValue("name");
        final Variable<String> name = nameString == null ? null : instruction.get(
                Utils.format(nameString, true, false), Argument.STRING);
        final Variable<String> marked = instruction.get(instruction.getValue("marked"), PackageArgument.IDENTIFIER);
        return new NullableEventAdapter(new RemoveEntityEvent(types, loc, range, name, marked, kill));
    }
}
