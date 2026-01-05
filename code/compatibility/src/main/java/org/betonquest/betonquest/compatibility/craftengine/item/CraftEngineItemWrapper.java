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
 * @param customItemArgument the argument containing the custom item
 */
public record CraftEngineItemWrapper(Argument<CustomItem<ItemStack>> customItemArgument) implements QuestItemWrapper {

    /**
     * Gets the {@link QuestItem} for the given profile.
     * @param profile the player profile
     * @return the quest item instance
     * @throws QuestException if item retrieval fails
     */
    @Override
    public QuestItem getItem(@Nullable final Profile profile) throws QuestException {
        return new CraftEngineItem(customItemArgument.getValue(profile));
    }

    /**
     * Implementation of {@link QuestItem} for CraftEngine.
     */
    class CraftEngineItem implements QuestItem {

        private final CustomItem<ItemStack> customItem;
        private final ItemMeta itemMeta;

        /**
         * @param customItem the base custom item
         */
        public CraftEngineItem(final CustomItem<ItemStack> customItem) {
            this.customItem = customItem;
            this.itemMeta = customItem.buildItemStack().getItemMeta();
        }

        @Override
        public Component getName() {
            return Objects.requireNonNull(itemMeta.displayName());
        }

        @Override
        public List<Component> getLore() {
            return Objects.requireNonNull(itemMeta.lore());
        }

        /**
         * Generates an {@link ItemStack} with the specified size.
         * @param stackSize the amount to generate
         * @param profile the player profile
         * @return the generated item stack
         */
        @Override
        public ItemStack generate(final int stackSize, @Nullable final Profile profile) throws QuestException {
            return customItem.buildItemStack(stackSize);
        }

        /**
         * Checks if the given item matches this custom item's ID.
         * * @param item the item to check
         * @return true if IDs match
         */
        @Override
        public boolean matches(@Nullable final ItemStack item) {
            return Objects.equals(customItem.id(), CraftEngineItems.getCustomItemId(item));
        }
    }
}
