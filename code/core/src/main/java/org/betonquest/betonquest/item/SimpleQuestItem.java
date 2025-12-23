package org.betonquest.betonquest.item;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.item.typehandler.ItemMetaHandler;
import org.betonquest.betonquest.item.typehandler.LoreHandler;
import org.betonquest.betonquest.item.typehandler.NameHandler;
import org.betonquest.betonquest.util.BlockSelector;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Represents a Quest item handled by the standard BetonQuest configuration.
 */
public class SimpleQuestItem implements org.betonquest.betonquest.api.item.QuestItem {

    /**
     * The base Material Selector for the ItemStack generation.
     */
    private final BlockSelector selector;

    /**
     * Providing display name for variables.
     */
    private final NameHandler name;

    /**
     * Providing lore lines for variables.
     */
    private final LoreHandler lore;

    /**
     * Handlers defining the QuestItem.
     */
    private final List<ItemMetaHandler<? extends ItemMeta>> handlers;

    /**
     * Creates a new QuestItem with "Vanilla Handlers".
     *
     * @param selector the base Material Selector for the ItemStack generation
     * @param handlers the populated handlers defining the QuestItem, including name and lore
     * @param name     providing display name for variables
     * @param lore     providing lore lines for variables
     */
    public SimpleQuestItem(final BlockSelector selector, final List<ItemMetaHandler<?>> handlers,
                           final NameHandler name, final LoreHandler lore) {
        this.selector = selector;
        this.handlers = handlers;
        this.name = name;
        this.lore = lore;
    }

    @Override
    public boolean equals(@Nullable final Object other) {
        return other instanceof final SimpleQuestItem item && item.handlers.equals(handlers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(selector, handlers);
    }

    @Override
    public boolean matches(@Nullable final ItemStack item) {
        if (item == null || !selector.match(item.getType())) {
            return false;
        }
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return true;
        }

        for (final ItemMetaHandler<? extends ItemMeta> handler : handlers) {
            if (!handler.rawCheck(meta)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack generate(final int stackSize, @Nullable final Profile profile) throws QuestException {
        final Material material = selector.getRandomMaterial();
        if (!material.isItem()) {
            throw new QuestException(material + " is not a valid item!");
        }

        final ItemStack item = new ItemStack(material, stackSize);
        final ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        for (final ItemMetaHandler<? extends ItemMeta> handler : handlers) {
            handler.rawPopulate(meta, profile);
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
