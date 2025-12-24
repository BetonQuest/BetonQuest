package org.betonquest.betonquest.item;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.item.typehandler.QuestHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Adds the tag identifying an ItemStack as BetonQuest QuestItem.
 *
 * @param original the quest item to add the tag to
 */
public record QuestItemTagAdapter(QuestItem original) implements QuestItem {

    @Override
    public Component getName() {
        return original.getName();
    }

    @Override
    public List<Component> getLore() {
        return original.getLore();
    }

    @Override
    public ItemStack generate(final int stackSize) throws QuestException {
        final ItemStack stack = original.generate(stackSize);
        stack.editMeta(meta -> meta.getPersistentDataContainer()
                .set(QuestHandler.QUEST_ITEM_KEY, PersistentDataType.BYTE, (byte) 1));
        return stack;
    }

    @Override
    public ItemStack generate(final int stackSize, @Nullable final Profile profile) throws QuestException {
        final ItemStack stack = original.generate(stackSize, profile);
        stack.editMeta(meta -> meta.getPersistentDataContainer()
                .set(QuestHandler.QUEST_ITEM_KEY, PersistentDataType.BYTE, (byte) 1));
        return stack;
    }

    @Override
    public boolean matches(@Nullable final ItemStack item) {
        return item != null && item.hasItemMeta()
                && item.getItemMeta().getPersistentDataContainer().has(QuestHandler.QUEST_ITEM_KEY)
                && original.matches(item);
    }
}
