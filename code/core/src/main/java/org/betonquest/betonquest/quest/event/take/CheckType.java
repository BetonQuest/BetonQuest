package org.betonquest.betonquest.quest.event.take;

/**
 * Represents the type of check that should be performed when checking for items.
 */
public enum CheckType {
    /**
     * Check for the player's inventory.
     */
    INVENTORY,
    /**
     * Check for the player's armor.
     */
    ARMOR,
    /**
     * Check for the player's offhand.
     */
    OFFHAND,
    /**
     * Check for the player's backpack.
     */
    BACKPACK
}
