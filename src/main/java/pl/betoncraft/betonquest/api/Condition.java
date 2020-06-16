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

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;

/**
 * <p>
 * Superclass for all conditions. You need to extend it in order to create new
 * custom conditions.
 * </p>
 *
 * <p>
 * Registering your condition is done through
 * {@link pl.betoncraft.betonquest.BetonQuest#registerConditions(String, Class)
 * registerConditions()} method.
 * </p>
 *
 * @author Jakub Sapalski
 */
abstract public class Condition extends ForceSyncHandler<Boolean>{

    /**
     * Stores instruction string for the condition.
     */
    protected Instruction instruction;
    /**
     * If a condition is static it can be used with null player. Static events
     * can be run with static conditions.
     */
    protected boolean staticness = false;
    /**
     * If a condition is persistent it can be checked for offline player.
     * Persistent conditions can be checked in folder events after the player
     * logs out.
     */
    protected boolean persistent = false;

    /**
     * @deprecated There is a new constructor that handles thread safety.
     * Using this may worsen your conditions performance!
     */
    // TODO Delete in BQ 2.0.0
    @Deprecated
    public Condition(Instruction instruction) {
        this(instruction, true);
    }

    /**
     * Creates new instance of the condition. The condition should parse
     * instruction string at this point and extract all the data from it. If
     * anything goes wrong, throw {@link InstructionParseException} with an
     * error message describing the problem.
     *
     * @param instruction the Instruction object; you can get one from ID instance with
     *                    {@link pl.betoncraft.betonquest.id.ID#generateInstruction()
     *                    ID.generateInstruction()} or create it from an instruction
     *                    string
     * @param forceSync If set to true this executes the condition on the servers main thread.
     *                  Otherwise it will just keep the current thread (which could also be the main thread!).
     */
    public Condition(Instruction instruction, boolean forceSync) {
        super(forceSync);
        this.instruction = instruction;
    }

    /**
     * @return if the condition is static or not. If a condition is static it
     * can be used with null player. Static events can be run with
     * static events.
     */
    public final boolean isStatic() {
        return staticness;
    }

    /**
     * @return if the condition is persistent or not. If a condition is
     * persistent it can be checked for offline player. Persistent
     * conditions can be checked in folder events after the player logs
     * out.
     */
    public final boolean isPersistent() {
        return persistent;
    }

    /**
     * This method should contain all logic for the condition and use data
     * parsed by the constructor. Don't worry about inverting the condition,
     * it's done by the rest of BetonQuest's logic. When this method is called
     * all the required data must be present and parsed correctly.
     *
     * @param playerID ID of the player for whom the condition will be checked
     * @return the result of the check
     * @throws QuestRuntimeException when an error happens at runtime (for example a numeric
     *                               variable resolves to a string)
     */
    @Override
    protected abstract Boolean execute(String playerID) throws QuestRuntimeException;
}
