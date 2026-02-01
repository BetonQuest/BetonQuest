package org.betonquest.betonquest.api.feature;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.CompassIdentifier;
import org.betonquest.betonquest.api.identifier.ItemIdentifier;
import org.betonquest.betonquest.api.identifier.JournalEntryIdentifier;
import org.betonquest.betonquest.api.identifier.JournalMainPageIdentifier;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.identifier.QuestCancelerIdentifier;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.npc.Npc;
import org.betonquest.betonquest.api.quest.npc.NpcHider;
import org.betonquest.betonquest.api.text.Text;
import org.betonquest.betonquest.feature.QuestCanceler;
import org.betonquest.betonquest.feature.QuestCompass;
import org.betonquest.betonquest.feature.journal.JournalMainPageEntry;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * The FeatureApi provides access to more complex features, often based on basic features.
 */
public interface FeatureApi {

    /**
     * Get the Api for Conversation interaction.
     *
     * @return conversation api
     */
    ConversationApi conversationApi();

    /**
     * Get the loaded Quest Canceler.
     *
     * @return quest cancelers in a new map
     */
    Map<QuestCancelerIdentifier, QuestCanceler> getCancelers();

    /**
     * Gets stored Quest Canceler.
     *
     * @param cancelerID the compass id
     * @return the loaded QuestCanceler
     * @throws QuestException if no QuestCanceler is loaded for the ID
     */
    QuestCanceler getCanceler(QuestCancelerIdentifier cancelerID) throws QuestException;

    /**
     * Get the loaded Compasses.
     *
     * @return compasses in a new map
     */
    Map<CompassIdentifier, QuestCompass> getCompasses();

    /**
     * Gets stored Journal Entry.
     *
     * @param journalEntryID the journal entry id
     * @return the loaded text
     * @throws QuestException if no text is loaded for the ID
     */
    Text getJournalEntry(JournalEntryIdentifier journalEntryID) throws QuestException;

    /**
     * Renames the Journal Entry instance.
     *
     * @param name   the current name
     * @param rename the name it should have now
     */
    void renameJournalEntry(JournalEntryIdentifier name, JournalEntryIdentifier rename);

    /**
     * Get the loaded Journal Main Page Entries.
     *
     * @return pages in a new map
     */
    Map<JournalMainPageIdentifier, JournalMainPageEntry> getJournalMainPages();

    /**
     * Gets a Npc by its id.
     *
     * @param npcID   the id of the Npc
     * @param profile the profile to resolve the Npc
     * @return the betonquest Npc
     * @throws QuestException when there is no Npc with that id
     */
    Npc<?> getNpc(NpcIdentifier npcID, @Nullable Profile profile) throws QuestException;

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
    QuestItem getItem(ItemIdentifier itemID, @Nullable Profile profile) throws QuestException;
}
