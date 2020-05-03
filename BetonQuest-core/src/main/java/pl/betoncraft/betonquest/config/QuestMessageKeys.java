package pl.betoncraft.betonquest.config;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;

public enum QuestMessageKeys implements MessageKeyProvider {
    CONVERSATION_START,
    CONVERSATION_END,
    NEW_JOURNAL_ENTRY,
    JOURNAL_TITLE,
    JOURNAL_LORE,
    QUEST_ITEM,
    BACKPACK_TITLE,
    PREVIOUS,
    NEXT,
    INVENTORY_FULL,
    CHANGELOG,
    PULLBACK,
    ITEMS_GIVE,
    ITEMS_TAKEN,
    BLOCKS_TO_BREAK,
    BLOCKS_TO_PLACE,
    MOBS_TO_KILL,
    PLAYERS_TO_KILL,
    FISH_TO_CATCH,
    SHEEP_TO_SHEAR,
    POTIONS_TO_BREW,
    ANIMALS_TO_BREED,
    BUSY,
    QUEST_CANCELED,
    CANCEL,
    CANCEL_PAGE,
    COMPASS,
    COMPASS_PAGE,
    COMMAND_BLOCKED,
    LANGUAGE_CHANGED,
    LANGUAGE_MISSING,
    LANGUAGE_NOT_EXIST,
    DAYS,
    HOURS,
    MINUTES,
    SECONDS,
    AND,
    PASSWORD;

    public String path() { return "betonquest." + toString().toLowerCase().replace("_", "-"); }

    @Override
    public MessageKey getMessageKey() {
        return MessageKey.of(path());
    }
}
