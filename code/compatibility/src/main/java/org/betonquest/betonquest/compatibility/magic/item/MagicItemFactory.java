package org.betonquest.betonquest.compatibility.magic.item;

import com.elmakers.mine.bukkit.api.item.ItemData;
import com.elmakers.mine.bukkit.api.magic.MageController;
import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.item.QuestItemWrapper;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.TypeFactory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Factory for creating Magic items.
 */
public class MagicItemFactory implements TypeFactory<QuestItemWrapper> {

    /**
     * The MageController instance to use.
     */
    private final MageController mageController;

    /**
     * Mapper to retrieve the item data from the Magic API using the item ID.
     */
    private final QuestFunction<String, ItemData> itemDataMapper;

    /**
     * Create a new QuestItemFactory for the Magic integration.
     *
     * @param mageController the MageController instance retrieved from the Magic API
     */
    public MagicItemFactory(final MageController mageController) {
        this.mageController = mageController;
        this.itemDataMapper = key -> {
            final ItemData item = mageController.getItem(key);
            if (item == null) {
                throw new QuestException("Magic item not found: '" + key + "'");
            }
            return item;
        };
    }

    @Override
    public QuestItemWrapper parseInstruction(final Instruction instruction) throws QuestException {
        final Argument<ItemData> itemData = instruction.string().map(itemDataMapper).get();
        return profile -> {
            final ItemData dataValue = itemData.getValue(profile);
            final ItemStack itemStack = dataValue.getItemStack();
            if (itemStack == null) {
                throw new QuestException("Magic item not found: '" + dataValue.getKey() + "'");
            }
            return new MagicItem(mageController, itemStack, itemData);
        };
    }

    /**
     * Implementation of {@link QuestItem} for Magic items.
     *
     * @param controller the MageController instance
     * @param itemStack  the underlying Magic item
     * @param itemData   the item data from the Magic API
     */
    private record MagicItem(MageController controller, ItemStack itemStack,
                             Argument<ItemData> itemData) implements QuestItem {

        @Override
        public Component getName() {
            return itemStack.displayName();
        }

        @Override
        public List<Component> getLore() {
            final List<Component> lore = itemStack.lore();
            return lore == null ? List.of() : lore;
        }

        @Override
        public ItemStack generate(final int stackSize, @Nullable final Profile profile) throws QuestException {
            final ItemData value = itemData.getValue(profile);
            final ItemStack itemStack = value.getItemStack();
            if (itemStack == null) {
                throw new QuestException("Magic item not found: '" + value.getKey() + "'");
            }
            return itemStack;
        }

        @Override
        public boolean matches(@Nullable final ItemStack item) {
            return controller.itemsAreEqual(item, itemStack);
        }
    }
}
