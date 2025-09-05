package org.betonquest.betonquest.api.feature;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.NpcID;
import org.betonquest.betonquest.api.quest.npc.feature.NpcHider;
import org.betonquest.betonquest.api.text.Text;
import org.betonquest.betonquest.conversation.ConversationData;
import org.betonquest.betonquest.conversation.ConversationID;
import org.betonquest.betonquest.feature.QuestCanceler;
import org.betonquest.betonquest.feature.QuestCompass;
import org.betonquest.betonquest.feature.journal.JournalMainPageEntry;
import org.betonquest.betonquest.id.CompassID;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.id.JournalEntryID;
import org.betonquest.betonquest.id.JournalMainPageID;
import org.betonquest.betonquest.id.QuestCancelerID;
import org.betonquest.betonquest.item.QuestItem;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * The FeatureApi provides access to more complex features, often based on basic features.
 */
public interface FeatureApi {
    /**
     * Gets stored Conversation Data.
     *
     * @param conversationID the id of the conversation
     * @return the loaded ConversationData
     * @throws QuestException if no ConversationData is loaded for the ID
     */
    ConversationData getConversation(ConversationID conversationID) throws QuestException;

    /**
     * Get the loaded Quest Canceler.
     *
     * @return quest cancelers in a new map
     */
    Map<QuestCancelerID, QuestCanceler> getCancelers();

    /**
     * Gets stored Quest Canceler.
     *
     * @param cancelerID the compass id
     * @return the loaded QuestCanceler
     * @throws QuestException if no QuestCanceler is loaded for the ID
     */
    QuestCanceler getCanceler(QuestCancelerID cancelerID) throws QuestException;

    /**
     * Get the loaded Compasses.
     *
     * @return compasses in a new map
     */
    Map<CompassID, QuestCompass> getCompasses();

    /**
     * Gets stored Journal Entry.
     *
     * @param journalEntryID the journal entry id
     * @return the loaded text
     * @throws QuestException if no text is loaded for the ID
     */
    Text getJournalEntry(JournalEntryID journalEntryID) throws QuestException;

    /**
     * Renames the Journal Entry instance.
     *
     * @param name   the current name
     * @param rename the name it should have now
     */
    void renameJournalEntry(JournalEntryID name, JournalEntryID rename);

    /**
     * Get the loaded Journal Main Page Entries.
     *
     * @return pages in a new map
     */
    Map<JournalMainPageID, JournalMainPageEntry> getJournalMainPages();

    /**
     * Gets a Npc by its id.
     *
     * @param npcID   the id of the Npc
     * @param profile the profile to resolve the Npc
     * @return the betonquest Npc
     * @throws QuestException when there is no Npc with that id
     */
    Npc<?> getNpc(NpcID npcID, @Nullable Profile profile) throws QuestException;

    /**
     * Gets the NpcHider.
     *
     * @return the active npc hider
     */
    NpcHider getNpcHider();

    /**
     * Gets a QuestItem by their id.
     *
     * @param itemID  the id
     * @param profile the profile to resolve the item
     * @return the stored quest item
     * @throws QuestException if there exists no QuestItem with that id
     */
    QuestItem getItem(ItemID itemID, @Nullable Profile profile) throws QuestException;
}
