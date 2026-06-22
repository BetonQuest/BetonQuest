package org.betonquest.betonquest.api.compass;

import org.betonquest.betonquest.api.identifier.ItemIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.text.Text;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a compass defined in a quest package.
 *
 * @since 3.0.0
 */
public interface QuestCompass {

    /**
     * The display name of the compass as a {@link Text} allowing for multiple localizations.
     *
     * @return the display name
     * @since 3.0.0
     */
    Text name();

    /**
     * The target location of the compass.
     *
     * @return the target location
     * @since 3.0.0
     */
    Argument<Location> location();

    /**
     * The identifier points to the item used for the compass.
     *
     * @return the item identifier or null if no item is set
     * @since 3.0.0
     */
    @Nullable
    ItemIdentifier item();
}
