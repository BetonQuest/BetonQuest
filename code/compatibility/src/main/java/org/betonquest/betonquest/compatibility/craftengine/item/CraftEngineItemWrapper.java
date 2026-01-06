package org.betonquest.betonquest.compatibility.craftengine.item;

import net.kyori.adventure.text.Component;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.item.CustomItem;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.item.QuestItemWrapper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * A wrapper for CraftEngine custom items.
 *
 * @param customItemArgument the argument containing the custom item
 */
public record CraftEngineItemWrapper(Argument<CustomItem<ItemStack>> customItemArgument) implements QuestItemWrapper {

    @Override
    public QuestItem getItem(@Nullable final Profile profile) throws QuestException {
        return new CraftEngineItem(customItemArgument.getValue(profile));
    }

    /**
     * Implementation of {@link QuestItem} for CraftEngine.
     */
    /* package */ static class CraftEngineItem implements QuestItem {

        /** The base custom item from CraftEngine. */
        private final CustomItem<ItemStack> customItem;

        /** The cached item meta for the custom item. */
        private final ItemMeta itemMeta;

        /**
         * @param customItem the base custom item
         */
        /* package */ CraftEngineItem(final CustomItem<ItemStack> customItem) {
            this.customItem = customItem;
            this.itemMeta = customItem.buildItemStack().getItemMeta();
        }

        @Override
        public Component getName() {
            return Objects.requireNonNullElse(itemMeta.displayName(), Component.empty());
        }

        @Override
        public List<Component> getLore() {
            return Objects.requireNonNullElse(itemMeta.lore(), List.of());
        }

        @Override
        public ItemStack generate(final int stackSize, @Nullable final Profile profile) throws QuestException {
            return customItem.buildItemStack(stackSize);
        }

        @Override
        public boolean matches(@Nullable final ItemStack item) {
            return item != null && Objects.equals(customItem.id(), CraftEngineItems.getCustomItemId(item));
        }
    }
}
