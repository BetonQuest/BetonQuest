package org.betonquest.betonquest.feature;

import org.betonquest.betonquest.api.text.Text;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

/**
 * A Quest Compass targeting a location.
 *
 * @param names    the display names by their language
 * @param location the compass location
 * @param itemID   possible item id, when it should be displayed in the backpack
 */
public record QuestCompass(Text names, Variable<Location> location, @Nullable ItemID itemID) {
}
