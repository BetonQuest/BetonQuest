package org.betonquest.betonquest.api.instruction;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestBiFunction;
import org.betonquest.betonquest.api.identifier.ItemIdentifier;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper for {@link QuestItem} to also store target stack amount.
 */
@SuppressWarnings("PMD.ShortClassName")
public class Item implements ItemWrapper {

    /**
     * Feature API function to retrieve items.
     */
    private final QuestBiFunction<ItemIdentifier, Profile, QuestItem> getItemFunction;

    /**
     * Item id to generate the QuestItem with.
     */
    private final ItemIdentifier itemID;

    /**
     * Size of the stack to create.
     */
    private final Argument<Number> amount;

    /**
     * Create a wrapper for Quest Item and target stack size.
     *
     * @param getItemFunction the Feature API function to retrieve items
     * @param itemID          the QuestItemID to create
     * @param amount          the size to set the created ItemStack to
     */
    public Item(final QuestBiFunction<ItemIdentifier, Profile, QuestItem> getItemFunction, final ItemIdentifier itemID, final Argument<Number> amount) {
        this.getItemFunction = getItemFunction;
        this.itemID = itemID;
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
    public ItemIdentifier getID() {
        return itemID;
    }

    @Override
    public QuestItem getItem(@Nullable final Profile profile) throws QuestException {
        return getItemFunction.apply(itemID, profile);
    }

    @Override
    public Argument<Number> getAmount() {
        return amount;
    }
}
