package org.betonquest.betonquest.feature;

import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.message.ParsedSectionMessage;
import org.jetbrains.annotations.Nullable;

/**
 * A Quest Compass targeting a location.
 *
 * @param names    the display names by their language
 * @param location the compass location
 * @param itemID   possible item id, when it should be displayed in the backpack
 */
public record QuestCompass(ParsedSectionMessage names, VariableLocation location, @Nullable ItemID itemID) {
}
