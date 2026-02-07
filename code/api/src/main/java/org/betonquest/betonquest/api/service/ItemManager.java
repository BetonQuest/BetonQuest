package org.betonquest.betonquest.api.service;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.ItemIdentifier;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.profile.Profile;
import org.jetbrains.annotations.Nullable;

/**
 * The ItemManager is responsible for handling items defined in BetonQuest.
 * <br> <br>
 * Each item is identified by an {@link ItemIdentifier} which consists of the user-defined name in the
 * configuration as well as the {@link QuestPackage} the item belongs to.
 */
@FunctionalInterface
public interface ItemManager {

    /**
     * Obtains a {@link QuestItem} by its {@link ItemIdentifier}.
     * <br> <br>
     * The specified {@link Profile} will be used to resolve any placeholders in the item's instruction.
     * If no profile is specified, the item will be resolved without any profile and any related placeholders
     * will be resolved without a profile.
     * If there are placeholders requiring a profile, but none is given, the resolution will fail.
     *
     * @param profile        the profile to resolve the item for or null if no profile is involved
     * @param itemIdentifier the identifier of the item
     * @return the item for the given identifier
     * @throws QuestException if there is no item with the given identifier
     */
    QuestItem getItem(@Nullable Profile profile, ItemIdentifier itemIdentifier) throws QuestException;
}
