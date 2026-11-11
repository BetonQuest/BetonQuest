package org.betonquest.betonquest.quest.placeholder.world;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.placeholder.NullablePlaceholder;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

/**
 * A placeholder that reads a value from a world.
 */
public class WorldPlaceholder implements NullablePlaceholder {

    /**
     * The world property reader.
     */
    private final Argument<WorldPlaceholderProperty> worldProperty;

    /**
     * The world for the placeholder.
     */
    private final Argument<World> world;

    /**
     * Create a new world placeholder.
     *
     * @param worldProperty the property reader to read value from a world
     * @param world         the world to read the value from
     */
    public WorldPlaceholder(final Argument<WorldPlaceholderProperty> worldProperty, final Argument<World> world) {
        this.worldProperty = worldProperty;
        this.world = world;
    }

    @Override
    public String getValue(@Nullable final Profile profile) throws QuestException {
        final World worldValue = world.getValue(profile);
        return worldProperty.getValue(profile).getValue(worldValue);
    }
}
