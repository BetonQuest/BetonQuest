package org.betonquest.betonquest.item;

import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A Quest Item to use in BetonQuest.
 */
public interface QuestItem {
    /**
     * Gets the effective name to show.
     *
     * @return the name to display
     */
    String getName();

    /**
     * Gets the lore.
     *
     * @return the list of lore lines, can be empty
     */
    List<String> getLore();

    /**
     * Generates this quest item as ItemStack with given amount.
     *
     * @param stackSize size of generated stack
     * @return the ItemStack equal to this quest item
     */
    default ItemStack generate(final int stackSize) {
        return generate(stackSize, null);
    }

    /**
     * Generates this quest item as ItemStack with given amount.
     *
     * @param stackSize size of generated stack
     * @param profile   profile parameter
     * @return the ItemStack equal to this quest item
     */
    ItemStack generate(int stackSize, @Nullable Profile profile);

    /**
     * Compares ItemStack to the quest item.
     *
     * @param item ItemStack to compare
     * @return true if the item matches
     */
    boolean matches(@Nullable ItemStack item);
}
