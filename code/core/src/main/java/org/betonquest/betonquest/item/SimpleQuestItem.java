package org.betonquest.betonquest.item;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.type.BlockSelector;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.item.QuestItemWrapper;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.item.handler.Attribute;
import org.betonquest.betonquest.item.handler.LoreMetaHandler;
import org.betonquest.betonquest.item.handler.NameMetaHandler;
import org.betonquest.betonquest.item.handler.ResolvedAttribute;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Represents a Quest item handled by the standard BetonQuest configuration.
 */
public class SimpleQuestItem implements QuestItemWrapper {

    /**
     * The base Material Selector for the ItemStack generation.
     */
    private final Argument<BlockSelector> selector;

    /**
     * Providing display name for placeholders.
     */
    private final NameMetaHandler.NameAttribute name;

    /**
     * Providing lore lines for placeholders.
     */
    private final LoreMetaHandler.LoreAttribute lore;

    /**
     * Handlers defining the QuestItem.
     */
    private final List<Attribute<?>> attributes;

    /**
     * Creates a new QuestItem with "Vanilla Handlers".
     *
     * @param selector   the base Material Selector for the ItemStack generation
     * @param name       providing display name for placeholders
     * @param lore       providing lore lines for placeholders
     * @param attributes the populated attributes defining the QuestItem, excluding explicit given one (name and lore)
     */
    public SimpleQuestItem(final Argument<BlockSelector> selector, final NameMetaHandler.NameAttribute name,
                           final LoreMetaHandler.LoreAttribute lore, final List<Attribute<?>> attributes) {
        this.selector = selector;
        this.attributes = attributes;
        this.name = name;
        this.lore = lore;
    }

    @Override
    public QuestItem getItem(@Nullable final Profile profile) throws QuestException {
        final BlockSelector blockSelector = this.selector.getValue(profile);
        final NameMetaHandler.ResolvedNameAttribute name = this.name.resolve(profile);
        final LoreMetaHandler.ResolvedLoreAttribute lore = this.lore.resolve(profile);
        final List<ResolvedAttribute<?>> resolvedHandlers = new ArrayList<>();
        for (final Attribute<? extends ItemMeta> attribute : this.attributes) {
            resolvedHandlers.add(attribute.resolve(profile));
        }
        resolvedHandlers.add(name);
        resolvedHandlers.add(lore);
        return new ResolvedSimpleQuestItem(blockSelector, name, lore, resolvedHandlers);
    }

    /**
     * Represents a Quest item handled by the standard BetonQuest configuration with all variables resolved.
     */
    private record ResolvedSimpleQuestItem(BlockSelector selector, NameMetaHandler.ResolvedNameAttribute name,
                                           LoreMetaHandler.ResolvedLoreAttribute lore,
                                           List<ResolvedAttribute<?>> handlers) implements QuestItem {

        @Override
        public boolean matches(@Nullable final ItemStack item) {
            if (item == null) {
                return selector.match(Material.AIR);
            }
            if (!selector.match(item.getType())) {
                return false;
            }
            final ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                return true;
            }

            for (final ResolvedAttribute<? extends ItemMeta> handler : handlers) {
                if (!handler.rawCheck(meta)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public ItemStack generate(final int stackSize) throws QuestException {
            if (stackSize <= 0) {
                return new ItemStack(Material.AIR);
            }
            final Material material = selector.getRandomMaterial();
            if (!material.isItem()) {
                throw new QuestException(material + " is not a valid item!");
            }

            final ItemStack item = new ItemStack(material, stackSize);
            final ItemMeta meta = item.getItemMeta();
            if (meta == null) {
                return item;
            }

            for (final ResolvedAttribute<? extends ItemMeta> handler : handlers) {
                handler.rawPopulate(meta);
            }

            item.setItemMeta(meta);
            return item;
        }

        @Override
        public Component getName() {
            final Component name = this.name.get();
            if (name != null) {
                return name;
            }
            return Component.text(selector.getRandomMaterial().toString().toLowerCase(Locale.ROOT).replace("_", " "));
        }

        @Override
        public List<Component> getLore() {
            return lore.get();
        }
    }
}
