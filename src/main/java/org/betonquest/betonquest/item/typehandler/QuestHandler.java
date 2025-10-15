package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.util.Utils;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Handles Quest Item state.
 */
public class QuestHandler implements ItemMetaHandler<ItemMeta> {
    /**
     * The quest string.
     */
    private static final String QUEST = "quest-item";

    /**
     * If the item is a "Quest Item".
     */
    private Existence questItem = Existence.WHATEVER;

    /**
     * The empty default constructor.
     */
    public QuestHandler() {
    }

    @Override
    public Class<ItemMeta> metaClass() {
        return ItemMeta.class;
    }

    @Override
    public Set<String> keys() {
        return Set.of(QUEST);
    }

    @Nullable
    @Override
    public String serializeToString(final ItemMeta meta) {
        if (meta.getPersistentDataContainer().has(Utils.QUEST_ITEM_KEY)) {
            return QUEST;
        }
        return null;
    }

    @Override
    public void set(final String key, final String data) throws QuestException {
        if (!QUEST.equals(key)) {
            throw new QuestException("Unknown unbreakable key: " + key);
        }
        if (HandlerUtil.isKeyOrTrue(QUEST, data)) {
            questItem = Existence.REQUIRED;
        } else {
            questItem = Existence.FORBIDDEN;
        }
    }

    @Override
    public void populate(final ItemMeta meta) {
        if (questItem == Existence.REQUIRED) {
            meta.getPersistentDataContainer().set(Utils.QUEST_ITEM_KEY, PersistentDataType.BYTE, (byte) 1);
        }
    }

    @Override
    public boolean check(final ItemMeta meta) {
        return switch (questItem) {
            case WHATEVER -> true;
            case REQUIRED -> meta.getPersistentDataContainer().has(Utils.QUEST_ITEM_KEY);
            case FORBIDDEN -> !meta.getPersistentDataContainer().has(Utils.QUEST_ITEM_KEY);
        };
    }
}
