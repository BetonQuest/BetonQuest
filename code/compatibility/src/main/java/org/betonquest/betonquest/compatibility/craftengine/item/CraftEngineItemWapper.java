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

public class CraftEngineItemWapper implements QuestItemWrapper {

    private final Argument<CustomItem<ItemStack>> customItemArgument;

    public CraftEngineItemWapper(final Argument<CustomItem<ItemStack>> customItemArgument) {
        this.customItemArgument = customItemArgument;
    }

    @Override
    public QuestItem getItem(@Nullable final Profile profile) throws QuestException {
        return new CraftEngineItem(customItemArgument.getValue(profile));
    }

    class CraftEngineItem implements QuestItem {

        private final CustomItem<ItemStack> customItem;
        private final ItemMeta itemMeta;

        CraftEngineItem(final CustomItem<ItemStack> customItem) {
            this.customItem = customItem;

            final ItemStack itemStack = customItem.buildItemStack();
            this.itemMeta = itemStack.getItemMeta();
        }

        @Override
        public Component getName() {
            return Objects.requireNonNull(itemMeta.displayName());
        }

        @Override
        public List<Component> getLore() {
            return Objects.requireNonNull(itemMeta.lore());
        }

        @Override
        public ItemStack generate(final int stackSize, @Nullable final Profile profile) throws QuestException {
            return customItem.buildItemStack(stackSize);
        }

        @Override
        public boolean matches(@Nullable final ItemStack item) {
            return Objects.equals(customItem.id(), CraftEngineItems.getCustomItemId(item));
        }
    }
}
