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

    /**
     * Gets the effective name to show.
     *
     * @return the name to display
     * @since 3.0.0
     */
    Component getName();

    /**
     * Gets the lore.
     *
     * @return the list of lore lines, can be empty
     * @since 3.0.0
     */
    List<Component> getLore();

    /**
     * Generates this quest item as ItemStack with given amount.
     * <p>
     * That method will <b>no longer have a default implementation</b> with {@code 4.0.0}.
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
     * @deprecated for removal in {@code 4.0.0}, the item's contained profile specific data which needs to be respected
     * should already be fully resolved for a profile from a {@link QuestItemWrapper}
     */
    @Deprecated(forRemoval = true, since = "3.3.0")
    default ItemStack generate(final int stackSize, @Nullable final Profile profile) throws QuestException {
        return generate(stackSize);
    }

    /**
     * Compares ItemStack to the quest item.
     *
     * @param item ItemStack to compare
     * @return true if the item matches
     * @since 3.0.0
     */
    boolean matches(@Nullable ItemStack item);
}
