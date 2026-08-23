package org.betonquest.betonquest.compatibility.craftengine.item;

import net.kyori.adventure.text.Component;
import net.momirealms.craftengine.bukkit.api.BukkitAdaptor;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.item.BukkitItemDefinition;
import net.momirealms.craftengine.core.item.ItemBuildContext;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.item.QuestItemWrapper;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * A wrapper for CraftEngine custom items.
 *
 * @param customItemArgument the argument containing the custom item
 */
public record CraftEngineItemWrapper(Argument<BukkitItemDefinition> customItemArgument) implements QuestItemWrapper {

    @Override
    public QuestItem getItem(@Nullable final Profile profile) throws QuestException {
        final BukkitItemDefinition definition = customItemArgument.getValue(profile);
        final ItemBuildContext context;
        if (profile instanceof final OnlineProfile onlineProfile) {
            context = ItemBuildContext.of(BukkitAdaptor.adapt(onlineProfile.getPlayer()));
        } else {
            context = ItemBuildContext.empty();
        }
        return new CraftEngineItem(definition, context);
    }

    /**
     * Implementation of {@link QuestItem} for CraftEngine.
     */
    public static class CraftEngineItem implements QuestItem {

        /**
         * The base custom item from CraftEngine.
         */
        private final BukkitItemDefinition definition;

        /**
         * The cached item meta for the custom item.
         */
        private final ItemMeta itemMeta;

        /**
         * The context to build the item stack.
         */
        private final ItemBuildContext buildContext;

        /**
         * Constructs a CraftEngineItem for the given base custom item.
         *
         * @param definition   the base custom item.
         * @param buildContext the context to build the item stack.
         */
        public CraftEngineItem(final BukkitItemDefinition definition, final ItemBuildContext buildContext) {
            this.definition = definition;
            this.itemMeta = definition.buildBukkitItem().getItemMeta();
            this.buildContext = buildContext;
        }

        @Override
        public Component getName() {
            return Objects.requireNonNullElse(itemMeta.displayName(), Component.empty());
        }

        @Override
        public List<Component> getLore() {
            return Objects.requireNonNullElse(itemMeta.lore(), List.of());
        }

        @Override
        public ItemStack generate(final int stackSize) {
            return definition.buildBukkitItem(buildContext, stackSize);
        }

        @Override
        public boolean matches(@Nullable final ItemStack item) {
            return item != null && Objects.equals(definition.id(), CraftEngineItems.getCustomItemId(item));
        }
    }
}
