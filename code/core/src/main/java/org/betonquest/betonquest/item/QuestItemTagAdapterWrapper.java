package org.betonquest.betonquest.item;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.item.QuestItemWrapper;
import org.betonquest.betonquest.api.profile.Profile;
import org.jetbrains.annotations.Nullable;

/**
 * Adds the tag identifying an ItemStack as BetonQuest QuestItem.
 *
 * @param original     the quest item wrapper to add the tag to the created item
 * @param loreConsumer the Consumer to (possibly) add the "Quest Item"-Lore to the generated item
 */
public record QuestItemTagAdapterWrapper(QuestItemWrapper original,
                                         LoreConsumer loreConsumer) implements QuestItemWrapper {

    @Override
    public QuestItem getItem(@Nullable final Profile profile) throws QuestException {
        return new QuestItemTagAdapter(original.getItem(profile), loreConsumer);
    }
}
