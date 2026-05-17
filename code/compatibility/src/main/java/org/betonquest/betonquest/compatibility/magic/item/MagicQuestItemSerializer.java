package org.betonquest.betonquest.compatibility.magic.item;

import com.elmakers.mine.bukkit.api.magic.MageController;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.item.QuestItemSerializer;
import org.bukkit.inventory.ItemStack;

/**
 * Serializes {@link ItemStack}s to their Magic IDs.
 */
public class MagicQuestItemSerializer implements QuestItemSerializer {

    /**
     * The MageController instance to use.
     */
    private final MageController mageController;

    /**
     * Create a new QuestItemSerializer for the Magic integration.
     *
     * @param mageController the MageController instance retrieved from the Magic API
     */
    public MagicQuestItemSerializer(final MageController mageController) {
        this.mageController = mageController;
    }

    @Override
    public String serialize(final ItemStack itemStack) throws QuestException {
        return mageController.getItemKey(itemStack);
    }
}
