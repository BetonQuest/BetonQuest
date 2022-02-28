package org.betonquest.betonquest.api;

import org.betonquest.betonquest.Journal;
import org.betonquest.betonquest.Pointer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * When player journal add new contents.
 */
public class PlayerJournalAddEvent extends PlayerEvent {

    /**
     * HandlerList of this event.
     */
    private static final HandlerList HANDLERS = new HandlerList();

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
     * @param who     the player who was added journal
     * @param journal the journal of this player
     * @param pointer the pointer for which journal entry was been added for this player
     */
    public PlayerJournalAddEvent(final Player who, final Journal journal, final Pointer pointer) {
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
        return HANDLERS;
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
        return HANDLERS;
    }
}
