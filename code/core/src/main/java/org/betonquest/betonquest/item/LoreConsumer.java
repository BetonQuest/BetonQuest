package org.betonquest.betonquest.item;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestBiConsumer;
import org.betonquest.betonquest.api.config.Localizations;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A BiConsumer on ItemMeta and nullable Profile.
 */
@FunctionalInterface
public interface LoreConsumer extends QuestBiConsumer<ItemMeta, Profile> {

    /**
     * Consumer that does nothing.
     */
    LoreConsumer EMPTY = (meta, profile) -> {
    };

    @Override
    void accept(ItemMeta meta, @Nullable Profile profile) throws QuestException;

    /**
     * Adds the quest item lore to the item meta.
     *
     * @param localizations the Localizations instance to get the lore line
     */
    record Lore(Localizations localizations) implements LoreConsumer {

        @Override
        public void accept(final ItemMeta meta, @Nullable final Profile profile) throws QuestException {
            final Component loreLine = localizations.getMessage(profile, "quest_item");
            if (meta.hasLore()) {
                final List<Component> lore = new ArrayList<>(meta.lore());
                lore.add(loreLine);
                meta.lore(lore);
            } else {
                meta.lore(List.of(loreLine));
            }
        }
    }
}
