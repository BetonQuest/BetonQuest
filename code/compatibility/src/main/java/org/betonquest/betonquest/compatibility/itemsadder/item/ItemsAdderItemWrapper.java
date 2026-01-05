package org.betonquest.betonquest.compatibility.itemsadder.item;

import dev.lone.itemsadder.api.CustomStack;
import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.item.QuestItemWrapper;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * A wrapper for ItemsAdder custom items.
 *
 * @param customItemArgument the argument containing the custom stack
 */
public record ItemsAdderItemWrapper(Argument<CustomStack> customItemArgument) implements QuestItemWrapper {

    /**
     * Gets the {@link QuestItem} for the given profile.
     *
     * @param profile the player profile
     * @return the ItemsAdder quest item
     * @throws QuestException if retrieval fails
     */
    @Override
    public QuestItem getItem(@Nullable final Profile profile) throws QuestException {
        return new ItemsAdderItem(customItemArgument.getValue(profile));
    }

    /**
     * Implementation of {@link QuestItem} for ItemsAdder.
     *
     * @param customStack the underlying ItemsAdder custom stack
     */
    public record ItemsAdderItem(CustomStack customStack) implements QuestItem {

        @Override
        public Component getName() {
            return Objects.requireNonNull(customStack.itemName());
        }

        @Override
        public List<Component> getLore() {
            return Objects.requireNonNull(customStack.getItemStack().lore());
        }

        /**
         * Generates an {@link ItemStack} with the specified size.
         *
         * @param stackSize the amount to generate
         * @param profile the player profile
         * @return the generated item stack
         */
        @Override
        public ItemStack generate(final int stackSize, @Nullable final Profile profile) throws QuestException {
            final ItemStack itemStack = customStack.getItemStack();
            itemStack.setAmount(stackSize);
            return itemStack;
        }

        /**
         * Checks if the given item matches this custom stack's ID.
         *
         * @param item the item to check
         * @return true if namespaced IDs match
         */
        @Override
        public boolean matches(@Nullable final ItemStack item) {
            return Objects.equals(CustomStack.byItemStack(item).getNamespacedID(), customStack.getNamespacedID());
        }
    }
}
