package org.betonquest.betonquest.compatibility.nexo.item;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
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
 * A wrapper for Nexo custom items.
 *
 * @param itemBuilderArgument the argument containing the item builder
 */
public record NexoItemWrapper(Argument<ItemBuilder> itemBuilderArgument) implements QuestItemWrapper {

    /**
     * Gets the {@link QuestItem} for the given profile.
     *
     * @param profile the player profile
     * @return the Nexo quest item
     * @throws QuestException if retrieval fails
     */
    @Override
    public QuestItem getItem(@Nullable final Profile profile) throws QuestException {
        return new NexoItem(itemBuilderArgument.getValue(profile));
    }

    /**
     * Implementation of {@link QuestItem} for Nexo.
     *
     * @param itemBuilder the underlying Nexo item builder
     */
    public record NexoItem(ItemBuilder itemBuilder) implements QuestItem {

        @Override
        public Component getName() {
            return Objects.requireNonNull(itemBuilder.getItemName());
        }

        @Override
        public List<Component> getLore() {
            return Objects.requireNonNull(itemBuilder.getLore());
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
            return itemBuilder.setAmount(stackSize).build();
        }

        /**
         * Checks if the given item matches this Nexo item.
         *
         * @param item the item to check
         * @return true if it matches
         */
        @Override
        public boolean matches(@Nullable final ItemStack item) {
            return itemBuilder == NexoItems.builderFromItem(item);
        }
    }
}
