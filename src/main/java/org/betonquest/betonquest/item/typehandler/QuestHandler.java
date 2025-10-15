package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.api.quest.QuestException;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Handles Quest Item state.
 */
public class QuestHandler implements ItemMetaHandler<ItemMeta> {
    /**
     * Key indicating an ItemStack should be treated as "Quest Item".
     */
    public static final NamespacedKey QUEST_ITEM_KEY = new NamespacedKey("betonquest", "quest_item");

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

    /**
     * Checks if the ItemStack is a quest item.
     *
     * @param item ItemStack to check
     * @return true if the supplied ItemStack is a quest item, false otherwise
     */
    public static boolean isQuestItem(@Nullable final ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        return item.getItemMeta().getPersistentDataContainer().has(QUEST_ITEM_KEY);
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
        if (meta.getPersistentDataContainer().has(QUEST_ITEM_KEY)) {
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
            meta.getPersistentDataContainer().set(QUEST_ITEM_KEY, PersistentDataType.BYTE, (byte) 1);
        }
    }

    @Override
    public boolean check(final ItemMeta meta) {
        return switch (questItem) {
            case WHATEVER -> true;
            case REQUIRED -> meta.getPersistentDataContainer().has(QUEST_ITEM_KEY);
            case FORBIDDEN -> !meta.getPersistentDataContainer().has(QUEST_ITEM_KEY);
        };
    }
}
