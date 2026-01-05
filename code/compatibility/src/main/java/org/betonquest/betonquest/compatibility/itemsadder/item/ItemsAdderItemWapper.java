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

public class ItemsAdderItemWapper implements QuestItemWrapper {

    private final Argument<CustomStack> customItemArgument;

    public ItemsAdderItemWapper(final Argument<CustomStack> customItemArgument) {
        this.customItemArgument = customItemArgument;
    }

    @Override
    public QuestItem getItem(@Nullable final Profile profile) throws QuestException {
        return new ItemsAdderItem(customItemArgument.getValue(profile));
    }

    class ItemsAdderItem implements QuestItem {

        private final CustomStack customStack;

        public ItemsAdderItem(final CustomStack customStack) {
            this.customStack = customStack;
        }

        @Override
        public Component getName() {
            return Objects.requireNonNull(customStack.itemName());
        }

        @Override
        public List<Component> getLore() {
            return Objects.requireNonNull(customStack.getItemStack().lore());
        }

        @Override
        public ItemStack generate(final int stackSize, @Nullable final Profile profile) throws QuestException {
            final ItemStack itemStack = customStack.getItemStack();
            itemStack.setAmount(stackSize);
            return itemStack;
        }

        @Override
        public boolean matches(@Nullable final ItemStack item) {
            return Objects.equals(CustomStack.byItemStack(item).getNamespacedID(), customStack.getNamespacedID());
        }
    }
}
