package org.betonquest.betonquest.api.instruction;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.instruction.type.QuestItemWrapper;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.id.ItemID;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper for {@link QuestItem} to also store target stack amount.
 */
@SuppressWarnings("PMD.ShortClassName")
public class Item implements QuestItemWrapper {

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

    @Override
    public ItemStack generate(@Nullable final Profile profile) throws QuestException {
        return getItem(profile).generate(amount.getValue(profile).intValue(), profile);
    }

    @Override
    public boolean matches(@Nullable final ItemStack item, @Nullable final Profile profile) throws QuestException {
        return getItem(profile).matches(item);
    }

    @Override
    public ItemID getID() {
        return itemID;
    }

    @Override
    public QuestItem getItem(@Nullable final Profile profile) throws QuestException {
        return featureApi.getItem(itemID, profile);
    }

    @Override
    public Variable<Number> getAmount() {
        return amount;
    }
}
