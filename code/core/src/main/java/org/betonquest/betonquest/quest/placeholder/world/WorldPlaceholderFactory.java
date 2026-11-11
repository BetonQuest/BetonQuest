package org.betonquest.betonquest.quest.placeholder.world;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.placeholder.NullablePlaceholderAdapter;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholderFactory;
import org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholderFactory;
import org.betonquest.betonquest.lib.instruction.argument.DefaultArguments;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Optional;

/**
 * Factory to create world placeholders from {@link Instruction}s.
 */
public class WorldPlaceholderFactory implements PlayerlessPlaceholderFactory, PlayerPlaceholderFactory {

    /**
     * Create a World placeholder factory.
     */
    public WorldPlaceholderFactory() {
    }

    private NullablePlaceholderAdapter parseNullable(final Instruction instruction) throws QuestException {
        final Argument<WorldPlaceholderProperty> worldResolver = instruction.enumeration(WorldPlaceholderProperty.class).get();
        final Optional<Argument<World>> worldByName = instruction.world().get("name");
        final Optional<Argument<World>> worldByUuid = instruction.uuid()
                .validate(uuid -> Bukkit.getWorld(uuid) != null, "There is no world by that uuid: %s")
                .map(Bukkit::getWorld).get("uuid");
        if (worldByName.isEmpty() && worldByUuid.isEmpty()) {
            return new NullablePlaceholderAdapter(new WorldPlaceholder(worldResolver, DefaultArguments.PLAYER_WORLD));
        }
        if (worldByName.isPresent() && worldByUuid.isPresent()) {
            throw new QuestException("You may only specify either a 'name' or 'uuid' argument for the world placeholder in an player-independent context.");
        }
        final Argument<World> world = worldByName.or(() -> worldByUuid).get();
        return new NullablePlaceholderAdapter(new WorldPlaceholder(worldResolver, world));
    }

    @Override
    public PlayerlessPlaceholder parsePlayerless(final Instruction instruction) throws QuestException {
        return parseNullable(instruction);
    }

    @Override
    public PlayerPlaceholder parsePlayer(final Instruction instruction) throws QuestException {
        return parseNullable(instruction);
    }
}
