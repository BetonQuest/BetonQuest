package org.betonquest.betonquest.quest.action.entity;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.quest.action.nullable.NullableActionAdapter;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.List;

/**
 * Factory for {@link RemoveEntityAction} to create from {@link Instruction}.
 */
public class RemoveEntityActionFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * Creates a new KillMobActionFactory.
     */
    public RemoveEntityActionFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return createRemoveEntityAction(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return createRemoveEntityAction(instruction);
    }

    private NullableActionAdapter createRemoveEntityAction(final Instruction instruction) throws QuestException {
        final Argument<List<EntityType>> types = instruction.enumeration(EntityType.class).list().get();
        final Argument<Location> loc = instruction.location().get();
        final Argument<Number> range = instruction.number().get();
        final FlagArgument<Boolean> kill = instruction.bool().getFlag("kill", true);
        final Argument<Component> name = instruction.component().get("name").orElse(null);
        final Argument<String> marked = instruction.packageIdentifier().get("marked").orElse(null);
        return new NullableActionAdapter(new RemoveEntityAction(types, loc, range, name, marked, kill));
    }
}
