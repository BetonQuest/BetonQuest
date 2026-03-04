package org.betonquest.betonquest.feature;

import org.betonquest.betonquest.api.identifier.ItemIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.text.Text;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

/**
 * A Quest Compass targeting a location.
 *
 * @param name     the display names by their language
 * @param location the compass location
 * @param item     possible item id, when it should be displayed in the backpack
 */
public record DefaultQuestCompass(Text name, Argument<Location> location, @Nullable ItemIdentifier item) {

}
