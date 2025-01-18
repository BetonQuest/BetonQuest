package org.betonquest.betonquest.api.bukkit.event;

import org.betonquest.betonquest.Journal;
import org.betonquest.betonquest.Pointer;
import org.betonquest.betonquest.api.profiles.Profile;
import org.bukkit.event.HandlerList;

/**
 * Fires when new content is added to a profile's journal.
 */
public class PlayerJournalAddEvent extends ProfileEvent {

    /**
     * HandlerList of this event.
     */
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /**
     * Journal which is just added an entry of this event.
     */
    private final Journal journal;

    /**
     * Pointer of journal entry which is just added to Journal of this event.
     */
    private final Pointer pointer;

    /**
     * Constructor of PlayerJournalAddEvent.
     *
     * @param who     the {@link Profile} who was added journal
     * @param journal the journal of this profile
     * @param pointer the pointer for which journal entry was added for this profile
     */
    public PlayerJournalAddEvent(final Profile who, final Journal journal, final Pointer pointer) {
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
     * Get this Journal. This is the whole of journal of this player, not a journal entry.
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
