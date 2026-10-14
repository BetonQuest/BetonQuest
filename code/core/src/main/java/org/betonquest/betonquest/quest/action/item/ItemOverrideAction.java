package org.betonquest.betonquest.quest.action.item;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.action.OnlineAction;
import org.betonquest.betonquest.item.SimpleQuestItem;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Action to override meta values in an existing item.
 */
public class ItemOverrideAction implements OnlineAction {

    /**
     * The slot to target.
     */
    private final Argument<EquipmentSlot> slot;

    /**
     * Wrapper for the quest item.
     */
    private final Argument<ItemWrapper> itemWrapper;

    /**
     * Create a new action.
     * <p>
     * If the wrapper does not provide a {@link SimpleQuestItem} the execution will fail.
     *
     * @param slot        of the item
     * @param itemWrapper the item wrapper to get the item for
     */
    public ItemOverrideAction(final Argument<EquipmentSlot> slot, final Argument<ItemWrapper> itemWrapper) {
        this.slot = slot;
        this.itemWrapper = itemWrapper;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final ItemWrapper wrapper = itemWrapper.getValue(profile);
        if (!(wrapper.getItem(profile) instanceof final SimpleQuestItem simpleQuestItem)) {
            throw new QuestException("Item '%s' is not a \"simple\" quest item".formatted(wrapper.getID()));
        }
        final ItemStack item = profile.getPlayer().getEquipment().getItem(slot.getValue(profile));
        final ItemMeta itemMeta = item.getItemMeta();
        simpleQuestItem.override(itemMeta);
        item.setItemMeta(itemMeta);
    }
}
