package org.betonquest.betonquest.instruction;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.item.QuestItem;
import org.bukkit.inventory.ItemStack;

/**
 * Wrapper for {@link QuestItem} to also store target stack amount.
 */
@SuppressWarnings("PMD.ShortClassName")
public class Item {
    /**
     * Item id to generate the QuestItem with.
     */
    private final ItemID itemID;

    /**
     * Cached QuestItem.
     */
    private final QuestItem questItem;

    /**
     * Size of the stack to create.
     */
    private final VariableNumber amount;

    /**
     * Create a wrapper for Quest Item and target stack size.
     *
     * @param itemID the QuestItemID to create
     * @param amount the size to set the created ItemStack to
     * @throws QuestException when the QuestItem could not be created
     */
    public Item(final ItemID itemID, final VariableNumber amount) throws QuestException {
        this.itemID = itemID;
        this.questItem = new QuestItem(itemID);
        this.amount = amount;
    }

    /**
     * Generates the item stack.
     *
     * @param profile the profile for variable resolving
     * @return the generated bukkit item
     * @throws QuestException when the generation fails
     */
    public ItemStack generate(final Profile profile) throws QuestException {
        return questItem.generate(amount.getValue(profile).intValue(), profile);
    }

    /**
     * Checks if the Item is equal to the stored one.
     *
     * @param item the item to compare
     * @return true if the quest item is equal to the given item
     */
    public boolean isItemEqual(final ItemStack item) {
        return questItem.compare(item);
    }

    /**
     * Gets the stored ID used to generate the Quest Item.
     *
     * @return item id of the item
     */
    public ItemID getID() {
        return itemID;
    }

    /**
     * Gets the Quest Item.
     *
     * @return quest item
     */
    public QuestItem getItem() {
        return questItem;
    }

    /**
     * Gets the amount to set.
     *
     * @return the stores amount
     */
    public VariableNumber getAmount() {
        return amount;
    }
}
