/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.id.ConditionID;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

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
 *
 * @author Jakub Sapalski
 */
public abstract class QuestEvent {

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
    protected boolean staticness = false;
    /**
     * Describes if the event is persistent
     */
    protected boolean persistent = false;

    /**
     * Creates new instance of the event. The event should parse instruction
     * string without doing anything else. If anything goes wrong, throw
     * {@link InstructionParseException} with error message describing the
     * problem.
     *
     * @param instruction the Instruction object representing this event; you need to
     *                    extract all required data from it and throw
     *                    {@link InstructionParseException} if there is anything wrong
     * @throws InstructionParseException when the is an error in the syntax or argument parsing
     */
    public QuestEvent(Instruction instruction) throws InstructionParseException {
        this.instruction = instruction;
        String[] tempConditions1 = instruction.getArray(instruction.getOptional("condition"));
        String[] tempConditions2 = instruction.getArray(instruction.getOptional("conditions"));
        int length = tempConditions1.length + tempConditions2.length;
        conditions = new ConditionID[length];
        for (int i = 0; i < length; i++) {
            String condition = (i >= tempConditions1.length) ? tempConditions2[i - tempConditions1.length] : tempConditions1[i];
            try {
                conditions[i] = new ConditionID(instruction.getPackage(), condition);
            } catch (ObjectNotFoundException e) {
                throw new InstructionParseException("Error while parsing event conditions: " + e.getMessage(), e);
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
    abstract public void run(String playerID) throws QuestRuntimeException;

    /**
     * Fires an event for the player. It checks event conditions, so there's no need to
     * do that in {@link #run(String) run()} method.
     *
     * @param playerID ID of the player for whom the event will fire
     * @throws QuestRuntimeException passes the exception from the event up the stack
     */
    public final void fire(String playerID) throws QuestRuntimeException {
        if (playerID == null) {
            // handle static event
            if (!staticness) {
                LogUtils.getLogger().log(Level.FINE, "Static event will be fired once for every player:");
                for (Player player : Bukkit.getOnlinePlayers()) {
                    String ID = PlayerConverter.getID(player);
                    for (ConditionID condition : conditions) {
                        if (!BetonQuest.condition(ID, condition)) {
                            LogUtils.getLogger().log(Level.FINE, "  Event conditions were not met for player " + player.getName());
                            continue;
                        }
                    }
                    LogUtils.getLogger().log(Level.FINE, "  Firing this static event for player " + player.getName());
                    run(ID);
                }
            } else {
                run(null);
            }
        } else if (PlayerConverter.getPlayer(playerID) == null) {
            // handle persistent event
            if (!persistent) {
                LogUtils.getLogger().log(Level.FINE, "Player " + playerID + " is offline, cannot fire event because it's not persistent.");
                return;
            }
            run(playerID);
        } else {
            // handle standard event
            for (ConditionID condition : conditions) {
                if (!BetonQuest.condition(playerID, condition)) {
                    LogUtils.getLogger().log(Level.FINE, "Event conditions were not met.");
                    return;
                }
            }
            run(playerID);
        }
    }
}
