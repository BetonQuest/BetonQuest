package org.betonquest.betonquest.compatibility.nexo.item;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.item.QuestItemWrapper;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record NexoItemWrapper(Variable<String> itemName) implements QuestItemWrapper {

    @Override
    public QuestItem getItem(@Nullable final Profile profile) throws QuestException {
        return new NexoItemWrapper.NexoItem(itemName.getValue(profile));
    }

    public class NexoItem implements QuestItem {

        private final String itemId;

        private final ItemBuilder itemBuilder;

        public NexoItem(final String itemId) {
            this.itemId = itemId;
            this.itemBuilder = NexoItems.itemFromId(itemId);
        }

        @Override
        public Component getName() {
            return itemBuilder.getItemName();
        }

        @Override
        public List<Component> getLore() {
            return itemBuilder.getLore();
        }

        @Override
        public ItemStack generate(final int stackSize, @Nullable final Profile profile) {
            return itemBuilder.setAmount(stackSize).build().clone();
        }

        @Override
        public boolean matches(@Nullable final ItemStack item) {
            final String matchedId = NexoItems.idFromItem(item);
            return itemId.equals(matchedId);
        }
    }
}
