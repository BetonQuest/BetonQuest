package org.betonquest.betonquest.quest.event.entity;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEventAdapter;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.List;

/**
 * Factory for {@link RemoveEntityEvent} to create from {@link Instruction}.
 */
public class RemoveEntityEventFactory implements PlayerEventFactory, PlayerlessEventFactory {

    /**
     * Creates a new KillMobEventFactory.
     */
    public RemoveEntityEventFactory() {
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return createRemoveEntityEvent(instruction);
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        return createRemoveEntityEvent(instruction);
    }

    private NullableEventAdapter createRemoveEntityEvent(final Instruction instruction) throws QuestException {
        final Argument<List<EntityType>> types = instruction.enumeration(EntityType.class).list().get();
        final Argument<Location> loc = instruction.location().get();
        final Argument<Number> range = instruction.number().get();
        final FlagArgument<Boolean> kill = instruction.bool().getFlag("kill", true);
        final Argument<Component> name = instruction.component().get("name").orElse(null);
        final Argument<String> marked = instruction.packageIdentifier().get("marked").orElse(null);
        return new NullableEventAdapter(new RemoveEntityEvent(types, loc, range, name, marked, kill));
    }
}
