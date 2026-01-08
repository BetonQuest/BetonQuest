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
            return Objects.requireNonNullElse(customStack.itemName(), Component.empty());
        }

        @Override
        public List<Component> getLore() {
            return Objects.requireNonNullElse(customStack.getItemStack().lore(), List.of());
        }

        @Override
        public ItemStack generate(final int stackSize, @Nullable final Profile profile) throws QuestException {
            final ItemStack itemStack = customStack.getItemStack();
            itemStack.setAmount(stackSize);
            return itemStack;
        }

        @Override
        public boolean matches(@Nullable final ItemStack item) {
            final CustomStack otherStack = CustomStack.byItemStack(item);
            if (otherStack == null) {
                return false;
            }
            return otherStack.getNamespacedID().equals(customStack.getNamespacedID());
        }
    }
}
