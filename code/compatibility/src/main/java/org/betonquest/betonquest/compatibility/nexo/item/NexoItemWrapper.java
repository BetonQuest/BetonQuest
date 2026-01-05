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

public class NexoItemWrapper implements QuestItemWrapper {

    private final Argument<ItemBuilder> itemBuilderArgument;

    public NexoItemWrapper(final Argument<ItemBuilder> itemBuilderArgument) {
        this.itemBuilderArgument = itemBuilderArgument;
    }

    @Override
    public QuestItem getItem(@Nullable final Profile profile) throws QuestException {
        return new NexoItem(itemBuilderArgument.getValue(profile));
    }

    class NexoItem implements QuestItem {

        private final ItemBuilder itemBuilder;

        NexoItem(final ItemBuilder itemBuilder) {
            this.itemBuilder = itemBuilder;
        }

        @Override
        public Component getName() {
            return Objects.requireNonNull(itemBuilder.getItemName());
        }

        @Override
        public List<Component> getLore() {
            return Objects.requireNonNull(itemBuilder.getLore());
        }

        @Override
        public ItemStack generate(final int stackSize, @Nullable final Profile profile) throws QuestException {
            return itemBuilder.setAmount(stackSize).build();
        }

        @Override
        public boolean matches(@Nullable final ItemStack item) {
            return itemBuilder == NexoItems.builderFromItem(item);
        }
    }
}
