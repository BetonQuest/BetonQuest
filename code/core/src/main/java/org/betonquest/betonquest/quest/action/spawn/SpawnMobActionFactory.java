package org.betonquest.betonquest.quest.action.spawn;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.DecoratedArgumentParser;
import org.betonquest.betonquest.api.instruction.argument.parser.EnumParser;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.quest.action.NullableActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.lib.instruction.argument.DecoratableArgumentParser;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;

import java.util.Collections;
import java.util.List;

/**
 * Factory to create spawn mob actions from {@link Instruction}s.
 */
public class SpawnMobActionFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * The parser for entity types.
     */
    private final DecoratedArgumentParser<EntityType> entityTypeParser;

    /**
     * Create a new factory for {@link SpawnMobAction}s.
     */
    public SpawnMobActionFactory() {
        this.entityTypeParser = new DecoratableArgumentParser<>(new EnumParser<>(EntityType.class))
                .validate(type -> type.getEntityClass() != null && Mob.class.isAssignableFrom(type.getEntityClass()),
                        "EntityType '%s' is not a mob");
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return createSpawnMobAction(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return createSpawnMobAction(instruction);
    }

    /**
     * Creates a new spawn mob action from the given instruction.
     *
     * @param instruction the instruction to create the action from
     * @return the created action
     * @throws QuestException if the instruction could not be parsed
     */
    public NullableActionAdapter createSpawnMobAction(final Instruction instruction) throws QuestException {
        final Argument<Location> loc = instruction.location().get();
        final Argument<EntityType> type = instruction.parse(entityTypeParser).get();
        final Argument<Number> amount = instruction.number().get();
        final Argument<Component> name = instruction.component().get("name").orElse(null);
        final Argument<String> marked = instruction.packageIdentifier().get("marked").orElse(null);
        final Argument<ItemWrapper> helmet = instruction.item().get("h").orElse(null);
        final Argument<ItemWrapper> chestplate = instruction.item().get("c").orElse(null);
        final Argument<ItemWrapper> leggings = instruction.item().get("l").orElse(null);
        final Argument<ItemWrapper> boots = instruction.item().get("b").orElse(null);
        final Argument<ItemWrapper> mainHand = instruction.item().get("m").orElse(null);
        final Argument<ItemWrapper> offHand = instruction.item().get("o").orElse(null);
        final Argument<List<ItemWrapper>> drops = instruction.item().list().get("drops", Collections.emptyList());
        final Equipment equipment = new Equipment(helmet, chestplate, leggings, boots, mainHand, offHand, drops);
        final SpawnMobAction action = new SpawnMobAction(loc, type, equipment, amount, name, marked);
        return new NullableActionAdapter(action);
    }
}
