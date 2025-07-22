package org.betonquest.betonquest.api.bukkit.event;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.feature.journal.Journal;
import org.betonquest.betonquest.feature.journal.Pointer;
import org.bukkit.event.HandlerList;

/**
 * Fires when content is removed from a profile's journal.
 */
public class PlayerJournalDeleteEvent extends ProfileEvent {

    /**
     * HandlerList of this event.
     */
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * Journal which is just deleted an entry of this event.
     */
    private final Journal journal;

    /**
     * Pointer of journal entry which is just deleted to Journal of this event.
     */
    private final Pointer pointer;

    /**
     * Constructor of PlayerJournalDeleteEvent.
     *
     * @param who     the {@link Profile} who was deleted journal
     * @param journal the journal of this profile
     * @param pointer the pointer for which journal entry was deleted for this profile
     */
    public PlayerJournalDeleteEvent(final Profile who, final Journal journal, final Pointer pointer) {
        super(who);
        this.journal = journal;
        this.pointer = pointer;
    }

    /**
     * Get the HandlerList of this event.
     *
     * @return the HandlerList.
     */
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    /**
     * Get this Journal. This is the whole journal of this profile, not a journal entry.
     *
     * @return the journal
     */
    public Journal getJournal() {
        return journal;
    }

    /**
     * Get the pointer of the journal entry. You can use "pointer.getPointer()" method
     * to get the name this journal entry, it will be with package name like "package.name".
     *
     * @return the journal pointer
     */
    public Pointer getPointer() {
        return pointer;
    }

    /**
     * Get the HandlerList of this event.
     *
     * @return the HandlerList.
     */
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
