package org.betonquest.betonquest.api.instruction;

import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.item.QuestItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper for {@link QuestItem} to also store target stack amount.
 */
@SuppressWarnings("PMD.ShortClassName")
public class Item {

    /**
     * Feature API.
     */
    private final FeatureApi featureApi;

    /**
     * Item id to generate the QuestItem with.
     */
    private final ItemID itemID;

    /**
     * Size of the stack to create.
     */
    private final Variable<Number> amount;

    /**
     * Create a wrapper for Quest Item and target stack size.
     *
     * @param featureApi the feature api creating new items
     * @param itemID     the QuestItemID to create
     * @param amount     the size to set the created ItemStack to
     */
    public Item(final FeatureApi featureApi, final ItemID itemID, final Variable<Number> amount) {
        this.itemID = itemID;
        this.featureApi = featureApi;
        this.amount = amount;
    }

    /**
     * Generates the item stack.
     *
     * @param profile the profile for variable resolving
     * @return the generated bukkit item
     * @throws QuestException when the generation fails
     */
    public ItemStack generate(@Nullable final Profile profile) throws QuestException {
        return getItem(profile).generate(amount.getValue(profile).intValue(), profile);
    }

    /**
     * Checks if the Item matches.
     *
     * @param item    the item to compare
     * @param profile the profile to resolve the item
     * @return true if the given item matches the quest item
     * @throws QuestException when there is no QuestItem for the ID
     */
    public boolean matches(@Nullable final ItemStack item, @Nullable final Profile profile) throws QuestException {
        return getItem(profile).matches(item);
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
     * @param profile the profile to resolve the item
     * @return the stored quest item
     * @throws QuestException when there is no QuestItem for the ID
     */
    public QuestItem getItem(@Nullable final Profile profile) throws QuestException {
        return featureApi.getItem(itemID, profile);
    }

    /**
     * Gets the amount to set.
     *
     * @return the stores amount
     */
    public Variable<Number> getAmount() {
        return amount;
    }
}
