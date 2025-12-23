package org.betonquest.betonquest.api.item;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
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
    Component getName();

    /**
     * Gets the lore.
     *
     * @return the list of lore lines, can be empty
     */
    List<Component> getLore();

    /**
     * Generates this quest item as ItemStack with given amount.
     *
     * @param stackSize size of generated stack
     * @return the ItemStack equal to this quest item
     * @throws QuestException when there is an exception while resolving profile specific data
     */
    default ItemStack generate(final int stackSize) throws QuestException {
        return generate(stackSize, null);
    }

    /**
     * Generates this quest item as ItemStack with given amount.
     *
     * @param stackSize size of generated stack
     * @param profile   profile parameter
     * @return the ItemStack equal to this quest item
     * @throws QuestException when there is an exception while resolving profile specific data
     */
    ItemStack generate(int stackSize, @Nullable Profile profile) throws QuestException;

    /**
     * Compares ItemStack to the quest item.
     *
     * @param item ItemStack to compare
     * @return true if the item matches
     */
    boolean matches(@Nullable ItemStack item);
}
