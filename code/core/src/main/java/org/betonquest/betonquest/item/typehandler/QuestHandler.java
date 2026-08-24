package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.item.LoreConsumer;
import org.betonquest.betonquest.item.handler.Attribute;
import org.betonquest.betonquest.item.handler.Existence;
import org.betonquest.betonquest.item.handler.ItemMetaHandler;
import org.betonquest.betonquest.item.handler.ResolvedAttribute;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Handles Quest Item state.
 */
public class QuestHandler implements ItemMetaHandler.Standard {

    /**
     * Key indicating an ItemStack should be treated as "Quest Item".
     */
    public static final NamespacedKey QUEST_ITEM_KEY = new NamespacedKey("betonquest", "quest_item");

    /**
     * The quest string.
     */
    private static final String QUEST = "quest-item";

    /**
     * Consumer to use when the item to generate is a quest item.
     */
    private final Argument<LoreConsumer> questItemLore;

    /**
     * The constructor.
     *
     * @param questItemLore the consumer to use when the item to generate is a quest item
     */
    public QuestHandler(final Argument<LoreConsumer> questItemLore) {
        this.questItemLore = questItemLore;
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
    @Nullable
    public Attribute.Standard parse(final Instruction instruction) throws QuestException {
        final Argument<Existence> questItem = HandlerUtil.getIsKeyOrTrue(QUEST, instruction);
        if (questItem == null) {
            return null;
        }
        return new NonResolved(questItemLore, questItem);
    }

    /**
     * Indicates whether the quest item tag is set and changes the lore.
     *
     * @param profile the optional profile for resolving arguments
     * @return if the item has an additional lore line
     * @throws QuestException when there is an exception while resolving profile specific data
     */
    public boolean isLoreSet(@Nullable final Profile profile) throws QuestException {
        return questItem.getValue(profile) == Existence.REQUIRED && questItemLore.getValue(profile) instanceof LoreConsumer.Lore;
    }

    /**
     * The attribute with placeholders.
     *
     * @param questItem If the item is a "Quest Item".
     */
    private record NonResolved(Argument<LoreConsumer> questItemLore, Argument<Existence> questItem)
            implements Attribute.Standard {

        @Override
        public ResolvedAttribute<ItemMeta> resolve(@Nullable final Profile profile) throws QuestException {
            final Existence existence = questItem.getValue(profile);
            final LoreConsumer loreConsumer = questItemLore.getValue(profile);
            return new Resolved(existence, loreConsumer);
        }
    }

    /**
     * The resolved attribute.
     */
    private record Resolved(Existence existence, LoreConsumer loreConsumer) implements ResolvedAttribute.Standard {

        @Override
        public void populate(final ItemMeta meta) {
            if (existence == Existence.REQUIRED) {
                meta.getPersistentDataContainer().set(QUEST_ITEM_KEY, PersistentDataType.BYTE, (byte) 1);
                loreConsumer.accept(meta);
            }
        }

        @Override
        public boolean check(final ItemMeta meta) {
            return switch (existence) {
                case WHATEVER -> true;
                case REQUIRED -> meta.getPersistentDataContainer().has(QUEST_ITEM_KEY);
                case FORBIDDEN -> !meta.getPersistentDataContainer().has(QUEST_ITEM_KEY);
            };
        }
    }
}
