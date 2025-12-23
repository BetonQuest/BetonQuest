package org.betonquest.betonquest.api.instruction;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.item.QuestItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * @author Juyas
 * @version 23.12.2025
 * @since 23.12.2025
 */
public interface QuestItemWrapper {

    /**
     * Generates the item stack.
     *
     * @param profile the profile for variable resolving
     * @return the generated bukkit item
     * @throws QuestException when the generation fails
     */
    ItemStack generate(@Nullable Profile profile) throws QuestException;

    /**
     * Checks if the Item matches.
     *
     * @param item    the item to compare
     * @param profile the profile to resolve the item
     * @return true if the given item matches the quest item
     * @throws QuestException when there is no QuestItem for the ID
     */
    boolean matches(@Nullable ItemStack item, @Nullable Profile profile) throws QuestException;

    /**
     * Gets the stored ID used to generate the Quest Item.
     *
     * @return item id of the item
     */
    ItemID getID();

    /**
     * Gets the Quest Item.
     *
     * @param profile the profile to resolve the item
     * @return the stored quest item
     * @throws QuestException when there is no QuestItem for the ID
     */
    QuestItem getItem(@Nullable Profile profile) throws QuestException;

    /**
     * Gets the amount to set.
     *
     * @return the stores amount
     */
    Variable<Number> getAmount();
}
