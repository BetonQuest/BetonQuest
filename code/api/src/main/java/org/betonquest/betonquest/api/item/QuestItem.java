package org.betonquest.betonquest.api.item;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A Quest Item to use in BetonQuest.
 *
 * @since 3.0.0
 */
public interface QuestItem {
    // TODO move all that stuff again in the Wrapper and do not throw here but there?

    /**
     * Gets the effective name to show.
     *
     * @param profile the optional profile for resolving arguments
     * @return the name to display
     * @throws QuestException when there is an exception while resolving profile specific data
     * @since 3.0.0
     */
    Component getName(@Nullable Profile profile) throws QuestException;

    /**
     * Gets the lore.
     *
     * @param profile the optional profile for resolving arguments
     * @return the list of lore lines, can be empty
     * @throws QuestException when there is an exception while resolving profile specific data
     * @since 3.0.0
     */
    List<Component> getLore(@Nullable Profile profile) throws QuestException;

    /**
     * Generates this quest item as ItemStack with given amount.
     *
     * @param stackSize size of generated stack
     * @return the ItemStack equal to this quest item
     * @throws QuestException when there is an exception while resolving profile specific data
     * @since 3.0.0
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
     * @since 3.0.0
     */
    ItemStack generate(int stackSize, @Nullable Profile profile) throws QuestException;

    /**
     * Compares ItemStack to the quest item.
     *
     * @param item ItemStack to compare
     * @return true if the item matches
     * @throws QuestException when there is an exception while resolving profile specific data
     * @since 3.0.0
     * @deprecated for removal in {@code 4.0.0}, items can contain profile specific data which needs to be respected
     */
    @Deprecated(forRemoval = true, since = "3.3.0")
    boolean matches(@Nullable ItemStack item) throws QuestException;

    /**
     * Compares ItemStack to the quest item.
     *
     * @param item    ItemStack to compare
     * @param profile profile parameter
     * @return true if the item matches
     * @throws QuestException when there is an exception while resolving profile specific data
     * @since 3.3.0
     */
    boolean matches(@Nullable ItemStack item, @Nullable Profile profile) throws QuestException;
}
