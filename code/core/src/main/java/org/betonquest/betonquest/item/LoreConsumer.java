package org.betonquest.betonquest.item;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.Localizations;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A BiConsumer on ItemMeta and nullable Profile.
 */
@FunctionalInterface
public interface LoreConsumer extends Consumer<ItemMeta> {

    /**
     * Consumer that does nothing.
     */
    LoreConsumer EMPTY = (meta) -> {
    };

    /**
     * Argument that provides {@link #EMPTY}.
     */
    Argument<LoreConsumer> EMPTY_ARGUMENT = profile -> EMPTY;

    @Override
    void accept(ItemMeta meta);

    /**
     * Adds the quest item lore to the item meta.
     *
     * @param localizations the Localizations instance to get the lore line
     */
    record LoreArgument(Localizations localizations) implements Argument<LoreConsumer> {

        @Override
        public Lore getValue(@Nullable final Profile profile) throws QuestException {
            return new Lore(localizations.getMessage(profile, "quest_item"));
        }
    }

    /**
     * Adds the quest item lore to the item meta.
     *
     * @param loreLine the actual line to add
     */
    record Lore(Component loreLine) implements LoreConsumer {

        @Override
        public void accept(final ItemMeta meta) {
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
