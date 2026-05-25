package org.betonquest.betonquest.compatibility.mythicmobs.item;

import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.items.ItemManager;
import io.lumine.mythic.api.mobs.GenericCaster;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.drops.DropMetadataImpl;
import io.lumine.mythic.core.items.MythicItem;
import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.item.QuestItemWrapper;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Creates {@link MythicStack}s from item id.
 *
 * @param itemManager the manager instance to get items
 * @param itemName    the id to get
 */
public record MythicItemWrapper(ItemManager itemManager, Argument<String> itemName) implements QuestItemWrapper {

    @Override
    public QuestItem getItem(@Nullable final Profile profile) throws QuestException {
        final String name = itemName.getValue(profile);
        final Optional<MythicItem> mythicItem = itemManager.getItem(name);
        if (mythicItem.isPresent()) {
            final Predicate<ItemStack> predicate = stack -> name.equals(itemManager.getMythicTypeFromItem(stack));
            final ItemStack adapt;
            if (profile == null || profile.getOnlineProfile().isEmpty()) {
                adapt = BukkitAdapter.adapt(mythicItem.get().generateItemStack(1));
            } else {
                final AbstractPlayer abstractPlayer = BukkitAdapter.adapt(profile.getOnlineProfile().get().getPlayer());
                final DropMetadataImpl dropMetadata = new DropMetadataImpl(new GenericCaster(abstractPlayer), abstractPlayer);
                adapt = BukkitAdapter.adapt(mythicItem.get().generateItemStack(dropMetadata, 1));
            }
            return new MythicStack(adapt, predicate);
        }
        throw new QuestException("Could not find mythic item for name " + name);
    }

    /**
     * Quest Item implementation for MythicItems.
     *
     * @param stack     the created bukkit item stack
     * @param predicate to check for matches
     */
    public record MythicStack(ItemStack stack, Predicate<ItemStack> predicate) implements QuestItem {

        @Override
        public Component getName() {
            return stack.displayName();
        }

        @Override
        public List<Component> getLore() {
            return Objects.requireNonNullElse(stack.lore(), List.of());
        }

        @Override
        public ItemStack generate(final int stackSize, @Nullable final Profile profile) {
            return stack.asQuantity(stackSize);
        }

        @Override
        public boolean matches(@Nullable final ItemStack item) {
            return predicate.test(item);
        }
    }
}
