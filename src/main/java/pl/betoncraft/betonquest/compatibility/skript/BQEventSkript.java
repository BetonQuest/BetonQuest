package pl.betoncraft.betonquest.compatibility.skript;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Fires the custom event for Skript to listen to
 */
@SuppressWarnings("PMD.CommentRequired")
public class BQEventSkript extends QuestEvent {

    private final String identifier;

    public BQEventSkript(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        identifier = instruction.next();
    }

    @Override
    protected Void execute(final String playerID) {
        final Player player = PlayerConverter.getPlayer(playerID);
        final CustomEventForSkript event = new CustomEventForSkript(player, identifier);
        Bukkit.getServer().getPluginManager().callEvent(event);
        return null;
    }

    /**
     * Custom event, which runs for Skript to listen.
     */
    public static class CustomEventForSkript extends PlayerEvent {

        private static final HandlerList HANDLERS = new HandlerList();
        private final String identifier;

        public CustomEventForSkript(final Player who, final String identifier) {
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
