package org.betonquest.betonquest.compatibility.skript;

import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.bukkit.event.ProfileEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;

/**
 * Fires the custom event for Skript to listen to
 */
@SuppressWarnings("PMD.CommentRequired")
public class BQEventSkript extends QuestEvent {

    private final String identifier;

    public BQEventSkript(final Instruction instruction) throws QuestException {
        super(instruction, true);
        identifier = instruction.next();
    }

    @Override
    protected Void execute(final Profile profile) {
        final CustomEventForSkript event = new CustomEventForSkript(profile, identifier);
        Bukkit.getServer().getPluginManager().callEvent(event);
        return null;
    }

    /**
     * Custom event, which runs for Skript to listen.
     */
    public static class CustomEventForSkript extends ProfileEvent {

        private static final HandlerList HANDLERS = new HandlerList();

        private final String identifier;

        public CustomEventForSkript(final Profile who, final String identifier) {
            super(who);
            this.identifier = identifier;
        }

        public static HandlerList getHandlerList() {
            return HANDLERS;
        }

        public String getID() {
            return identifier;
        }

        @Override
        public HandlerList getHandlers() {
            return HANDLERS;
        }
    }
}
