package org.betonquest.betonquest.item.handler;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

/**
 * Values to be set on an item meta which may be resolved per {@link Profile}.
 *
 * @param <M> applicable meta
 */
@FunctionalInterface
public interface Attribute<M extends ItemMeta> {

    /**
     * Resolves all placeholders into a definit state.
     *
     * @param profile the profile to resolve placeholders with
     * @return the fully resolved argument
     * @throws QuestException if argument resolving for the profile fails
     */
    ResolvedAttribute<M> resolve(@Nullable Profile profile) throws QuestException;

    /**
     * Attribute for the standard item meta.
     */
    @FunctionalInterface
    interface Standard extends Attribute<ItemMeta> {

    }
}
