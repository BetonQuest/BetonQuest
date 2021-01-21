package org.betonquest.betonquest.api;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.utils.LogUtils;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.logging.Level;

/**
 * <p>
 * Superclass for all events. You need to extend it in order to create new
 * custom events.
 * </p>
 * <p>
 * Registering your events is done through
 * {@link pl.betoncraft.betonquest.BetonQuest#registerEvents(String, Class) registerEvents()} method.
 * </p>
 */
@SuppressWarnings("PMD.CommentRequired")
public abstract class QuestEvent extends ForceSyncHandler<Void> {

    /**
     * Stores instruction string for the event.
     */
    protected final Instruction instruction;
    /**
     * Stores conditions that must be met when firing this event
     */
    protected final ConditionID[] conditions;
    /**
     * Describes if the event is static
     */
    protected boolean staticness;
    /**
     * Describes if the event is persistent
     */
    protected boolean persistent;

    /**
     * Creates new instance of the event. The event should parse instruction
     * string without doing anything else. If anything goes wrong, throw
     * {@link InstructionParseException} with error message describing the
     * problem.
     *
     * @param instruction the Instruction object representing this event; you need to
     *                    extract all required data from it and throw
     *                    {@link InstructionParseException} if there is anything wrong
     * @param forceSync   If set to true this executes the event on the servers main thread.
     *                    Otherwise it will just keep the current thread (which could also be the main thread!).
     * @throws InstructionParseException when the is an error in the syntax or argument parsing
     */
    public QuestEvent(final Instruction instruction, final boolean forceSync) throws InstructionParseException {
        super(forceSync);
        this.instruction = instruction;
        final String[] tempConditions1 = instruction.getArray(instruction.getOptional("condition"));
        final String[] tempConditions2 = instruction.getArray(instruction.getOptional("conditions"));
        final int length = tempConditions1.length + tempConditions2.length;
        conditions = new ConditionID[length];
        for (int i = 0; i < length; i++) {
            final String condition = i >= tempConditions1.length ? tempConditions2[i - tempConditions1.length] : tempConditions1[i];
            try {
                conditions[i] = new ConditionID(instruction.getPackage(), condition);
            } catch (final ObjectNotFoundException exception) {
                throw new InstructionParseException("Error while parsing event conditions: " + exception.getMessage(), exception);
            }
        }
    }

    /**
     * This method should contain all logic for firing the event and use the
     * data parsed by the constructor. When this method is called all the
     * required data is present and parsed correctly.
     *
     * @param playerID ID of the player for whom the event will fire
     * @throws QuestRuntimeException when there is an error while running the event (for example a
     *                               numeric variable resolved to a string)
     */
    @Override
    protected abstract Void execute(String playerID) throws QuestRuntimeException;

    /**
     * Fires an event for the player. It checks event conditions, so there's no need to
     * do that in {@link #execute(String)} method.
     *
     * @param playerID ID of the player for whom the event will fire
     * @throws QuestRuntimeException passes the exception from the event up the stack
     */
    public final void fire(final String playerID) throws QuestRuntimeException {
        if (playerID == null) {
            // handle static event
            if (staticness) {
                handle(null);
            } else {
                LogUtils.getLogger().log(Level.FINE, "Static event will be fired once for every player:");
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    final String onlinePlayerID = PlayerConverter.getID(player);
                    if (!BetonQuest.conditions(onlinePlayerID, conditions)) {
                        LogUtils.getLogger().log(Level.FINE, "Event conditions were not met for player " + player.getName());
                        continue;
                    }
                    LogUtils.getLogger().log(Level.FINE, "  Firing this static event for player " + player.getName());
                    handle(onlinePlayerID);
                }
            }
        } else if (PlayerConverter.getPlayer(playerID) == null) {
            // handle persistent event
            if (!persistent) {
                LogUtils.getLogger().log(Level.FINE, "Player " + playerID + " is offline, cannot fire event because it's not persistent.");
                return;
            }
            handle(playerID);
        } else {
            // handle standard event
            if (!BetonQuest.conditions(playerID, conditions)) {
                LogUtils.getLogger().log(Level.FINE, "Event conditions were not met.");
                return;
            }
            handle(playerID);
        }
    }

    protected String getFullId() {
        return instruction.getID().getFullID();
    }
}
