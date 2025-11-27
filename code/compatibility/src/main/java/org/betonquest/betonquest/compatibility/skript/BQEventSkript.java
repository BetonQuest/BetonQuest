package org.betonquest.betonquest.compatibility.skript;

import org.betonquest.betonquest.api.bukkit.event.ProfileEvent;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.bukkit.event.HandlerList;

/**
 * Fires the custom event for Skript to listen to.
 */
public class BQEventSkript implements PlayerEvent {

    /**
     * Identifier for the event.
     */
    private final Variable<String> identifier;

    /**
     * Create a new Skript Event.
     *
     * @param identifier the identifier for the custom event
     */
    public BQEventSkript(final Variable<String> identifier) {
        this.identifier = identifier;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        new CustomEventForSkript(profile, identifier.getValue(profile)).callEvent();
    }

    /**
     * Custom event, which runs for Skript to listen.
     */
    public static class CustomEventForSkript extends ProfileEvent {

        /**
         * HandlerList of this event.
         */
        private static final HandlerList HANDLER_LIST = new HandlerList();

        /**
         * Event identifier.
         */
        private final String identifier;

        /**
         * Create a new Custom Event.
         *
         * @param who        the profile for the event
         * @param identifier the identifier for the event
         */
        public CustomEventForSkript(final Profile who, final String identifier) {
            super(who);
            this.identifier = identifier;
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
         * Get the event identifier.
         *
         * @return the identifier
         */
        public String getID() {
            return identifier;
        }

        @Override
        public HandlerList getHandlers() {
            return HANDLER_LIST;
        }
    }
}
