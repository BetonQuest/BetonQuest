package org.betonquest.betonquest.api.feature;

import org.betonquest.betonquest.api.message.Message;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.feature.NpcHider;
import org.betonquest.betonquest.conversation.ConversationData;
import org.betonquest.betonquest.feature.QuestCanceler;
import org.betonquest.betonquest.feature.QuestCompass;
import org.betonquest.betonquest.feature.journal.JournalMainPageEntry;
import org.betonquest.betonquest.id.CompassID;
import org.betonquest.betonquest.id.ConversationID;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.id.JournalEntryID;
import org.betonquest.betonquest.id.JournalMainPageID;
import org.betonquest.betonquest.id.NpcID;
import org.betonquest.betonquest.id.QuestCancelerID;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.kernel.processor.QuestRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * The Feature logic.
 */
public final class FeatureAPI {

    /**
     * Quest Registry providing processors.
     */
    private final QuestRegistry questRegistry;

    /**
     * Create a new Feature API.
     *
     * @param questRegistry the registry containing processors
     */
    public FeatureAPI(final QuestRegistry questRegistry) {
        this.questRegistry = questRegistry;
    }

    /**
     * Gets stored Conversation Data.
     *
     * @param conversationID the id of the conversation
     * @return the loaded ConversationData
     * @throws QuestException if no ConversationData is loaded for the ID
     */
    public ConversationData getConversation(final ConversationID conversationID) throws QuestException {
        return questRegistry.conversations().get(conversationID);
    }

    /**
     * Get the loaded Quest Canceler.
     *
     * @return quest cancelers in a new map
     */
    public Map<QuestCancelerID, QuestCanceler> getCancelers() {
        return new HashMap<>(questRegistry.cancelers().getValues());
    }

    /**
     * Gets stored Quest Canceler.
     *
     * @param cancelerID the compass id
     * @return the loaded QuestCanceler
     * @throws QuestException if no QuestCanceler is loaded for the ID
     */
    public QuestCanceler getCanceler(final QuestCancelerID cancelerID) throws QuestException {
        return questRegistry.cancelers().get(cancelerID);
    }

    /**
     * Get the loaded Compasses.
     *
     * @return compasses in a new map
     */
    public Map<CompassID, QuestCompass> getCompasses() {
        return new HashMap<>(questRegistry.compasses().getValues());
    }

    /**
     * Gets stored Journal Entry.
     *
     * @param journalEntryID the journal entry id
     * @return the loaded Message
     * @throws QuestException if no Message is loaded for the ID
     */
    public Message getJournalEntry(final JournalEntryID journalEntryID) throws QuestException {
        return questRegistry.journalEntries().get(journalEntryID);
    }

    /**
     * Renames the Journal Entry instance.
     *
     * @param name   the current name
     * @param rename the name it should have now
     */
    public void renameJournalEntry(final JournalEntryID name, final JournalEntryID rename) {
        questRegistry.journalEntries().renameJournalEntry(name, rename);
    }

    /**
     * Get the loaded Journal Main Page Entries.
     *
     * @return pages in a new map
     */
    public Map<JournalMainPageID, JournalMainPageEntry> getJournalMainPages() {
        return new HashMap<>(questRegistry.journalMainPages().getValues());
    }

    /**
     * Gets a Npc by its id.
     *
     * @param npcID   the id of the Npc
     * @param profile the profile to resolve the Npc
     * @return the betonquest Npc
     * @throws QuestException when there is no Npc with that id
     */
    public Npc<?> getNpc(final NpcID npcID, @Nullable final Profile profile) throws QuestException {
        return questRegistry.npcs().get(npcID).getNpc(profile);
    }

    /**
     * Gets the NpcHider.
     *
     * @return the active npc hider
     */
    public NpcHider getNpcHider() {
        return questRegistry.npcs().getNpcHider();
    }

    /**
     * Gets a QuestItem by their id.
     *
     * @param itemID  the id
     * @param profile the profile to resolve the item
     * @return the stored quest item
     * @throws QuestException if there exists no QuestItem with that id
     */
    public QuestItem getItem(final ItemID itemID, @Nullable final Profile profile) throws QuestException {
        return questRegistry.items().get(itemID).getItem(profile);
    }
}
