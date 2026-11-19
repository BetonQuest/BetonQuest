package org.betonquest.betonquest.api.service.item;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.ItemIdentifier;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.profile.Profile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * The ItemManager is responsible for handling items defined in BetonQuest.
 * <br><br>
 * Each item is identified by an {@link ItemIdentifier}, which consists of the user-defined name
 * in the configuration and the {@link QuestPackage} it belongs to.
 *
 * @since 3.0.0
 */
@FunctionalInterface
public interface ItemManager {

    /**
     * Retrieves a {@link QuestItem} by its {@link ItemIdentifier}.
     * <br><br>
     * The specified {@link Profile} is used to resolve placeholders in the item's instruction.
     * If no profile is provided, placeholders requiring one will cause the resolution to fail.
     *
     * @param profile        the profile to resolve the item for, or {@code null} if no profile is involved
     * @param itemIdentifier the identifier of the item to retrieve
     * @return the {@link QuestItem} for the given identifier
     * @throws QuestException if no item exists with the given identifier or if resolution fails
     * @since 3.0.0
     */
    @Contract(pure = true)
    QuestItem getItem(@Nullable Profile profile, ItemIdentifier itemIdentifier) throws QuestException;
}
