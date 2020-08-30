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
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.GlobalObjectives;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.database.Connector;
import pl.betoncraft.betonquest.database.Saver;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.id.ConditionID;
import pl.betoncraft.betonquest.id.EventID;
import pl.betoncraft.betonquest.id.ObjectiveID;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.HashMap;
import java.util.logging.Level;

/**
 * <p>
 * Superclass for all objectives. You need to extend it in order to create new
 * custom objectives.
 * </p>
 * <p>
 * Registering your objectives is done through
 * {@link pl.betoncraft.betonquest.BetonQuest#registerObjectives(String, Class)
 * registerObjectives()} method.
 * </p>
 *
 * @author Jakub Sapalski
 */
public abstract class Objective {

    protected Instruction instruction;
    protected ConditionID[] conditions;
    protected EventID[] events;
    protected boolean persistent;
    protected boolean global;
    protected QREHandler qreHandler = new QREHandler();

    /**
     * Contains all data objects of the players with this objective active
     */
    protected HashMap<String, ObjectiveData> dataMap = new HashMap<>();
    /**
     * Should be set with the data class used to hold players' information
     */
    protected Class<? extends ObjectiveData> template;

    /**
     * <p>
     * Creates new instance of the objective. The objective should parse
     * instruction string at this point and extract all the data from it.
     * </p>
     * <b>Do not register listeners here!</b> There is a {@link #start()} method
     * for it.
     *
     * @param instruction Instruction object representing the objective; you need to
     *                    extract all required information from it
     * @throws InstructionParseException if the syntax is wrong or any error happens while parsing
     */
    public Objective(final Instruction instruction) throws InstructionParseException {
        this.instruction = instruction;
        // extract events and conditions
        final String[] tempEvents1 = instruction.getArray(instruction.getOptional("event"));
        final String[] tempEvents2 = instruction.getArray(instruction.getOptional("events"));
        persistent = instruction.hasArgument("persistent");
        global = instruction.hasArgument("global");
        if (global) {
            GlobalObjectives.add((ObjectiveID) instruction.getID());
        }
        // make them final
        int length = tempEvents1.length + tempEvents2.length;
        events = new EventID[length];
        for (int i = 0; i < length; i++) {
            final String event = i >= tempEvents1.length ? tempEvents2[i - tempEvents1.length] : tempEvents1[i];
            try {
                events[i] = new EventID(instruction.getPackage(), event);
            } catch (ObjectNotFoundException e) {
                throw new InstructionParseException("Error while parsing objective events: " + e.getMessage(), e);
            }
        }
        final String[] tempConditions1 = instruction.getArray(instruction.getOptional("condition"));
        final String[] tempConditions2 = instruction.getArray(instruction.getOptional("conditions"));
        length = tempConditions1.length + tempConditions2.length;
        conditions = new ConditionID[length];
        for (int i = 0; i < length; i++) {
            final String condition = i >= tempConditions1.length ? tempConditions2[i - tempConditions1.length]
                    : tempConditions1[i];
            try {
                conditions[i] = new ConditionID(instruction.getPackage(), condition);
            } catch (ObjectNotFoundException e) {
                throw new InstructionParseException("Error while parsing objective conditions: " + e.getMessage(), e);
            }
        }
    }

    /**
     * This method is called by the plugin when the objective needs to start
     * listening for events. Register your Listeners here!
     */
    public abstract void start();

    /**
     * This method is called by the plugin when the objective needs to be
     * stopped. You have to unregister all Listeners here.
     */
    public abstract void stop();

    /**
     * This method should return the default data instruction for the objective,
     * ready to be parsed by the ObjectiveData class.
     *
     * @return the default data instruction string
     */
    public abstract String getDefaultDataInstruction();

    /**
     * This method should return various properties of the objective, formatted
     * as readable Strings. An example would be "5h 5min" for "time_left"
     * keyword in "delay" objective or "12" for keyword "mobs_killed" in
     * "mobkill" objective. The method is not abstract since not all objectives
     * need to have properties, i.e. "die" objective. By default it returns an
     * empty string.
     *
     * @param name     the name of the property you need to return; you can parse it
     *                 to extract additional information
     * @param playerID ID of the player for whom the property is to be returned
     * @return the property with given name
     */
    abstract public String getProperty(final String name, final String playerID);

    /**
     * This method fires events for the objective and removes it from player's
     * list of active objectives. Use it when you detect that the objective has
     * been completed. It deletes the objective using delete() method.
     *
     * @param playerID the ID of the player for whom the objective is to be completed
     */
    public final void completeObjective(final String playerID) {
        // remove the objective from player's list
        removePlayer(playerID);
        BetonQuest.getInstance().getPlayerData(playerID).removeRawObjective((ObjectiveID) instruction.getID());
        if (persistent) {
            BetonQuest.getInstance().getPlayerData(playerID).addNewRawObjective((ObjectiveID) instruction.getID());
            addPlayer(playerID, getDefaultDataInstruction());
        }
        LogUtils.getLogger().log(Level.FINE,
                "Objective \"" + instruction.getID().getFullID() + "\" has been completed for player "
                        + PlayerConverter.getName(playerID)
                        + ", firing events.");
        // fire all events
        for (final EventID event : events) {
            BetonQuest.event(playerID, event);
        }
        LogUtils.getLogger().log(Level.FINE,
                "Firing events in objective \"" + instruction.getID().getFullID() + "\" for player "
                        + PlayerConverter.getName(playerID)
                        + " finished");
    }

    /**
     * Checks if all conditions has been met. Use it when the player has done
     * something that modifies data (e.g. killing zombies). If conditions are
     * met, you can safely modify the data.
     *
     * @param playerID ID of the player for whom the conditions are to be checked
     * @return if all conditions of this objective has been met
     */
    public final boolean checkConditions(final String playerID) {
        LogUtils.getLogger().log(Level.FINE, "Condition check in \"" + instruction.getID().getFullID()
                + "\" objective for player " + PlayerConverter.getName(playerID));
        for (final ConditionID condition : conditions) {
            if (!BetonQuest.condition(playerID, condition)) {
                return false;
            }
        }
        // if there are no conditions or all of them are met return true
        return true;
    }

    /**
     * Adds this new objective to the player. Also updates the database with the
     * objective.
     *
     * @param playerID ID of the player
     */
    public final void newPlayer(final String playerID) {
        final String def = getDefaultDataInstruction();
        addPlayer(playerID, def);
        BetonQuest.getInstance().getPlayerData(playerID).addObjToDB(instruction.getID().getFullID(), def);
    }

    /**
     * Adds this objective to the player.
     *
     * @param playerID    ID of the player
     * @param instruction instruction string for player's data
     */
    public final synchronized void addPlayer(final String playerID, final String instruction) {
        ObjectiveData data = null;
        try {
            data = template.getConstructor(String.class, String.class, String.class).newInstance(instruction, playerID,
                    this.instruction.getID().getFullID());
        } catch (Exception e) {
            if (e.getCause() instanceof InstructionParseException) {
                LogUtils.getLogger().log(Level.WARNING, "Error while loading " + this.instruction.getID().getFullID() + " objective data for player "
                        + PlayerConverter.getName(playerID) + ": " + e.getCause().getMessage());
                LogUtils.logThrowable(e);
            } else {
                LogUtils.logThrowableReport(e);
            }
        }
        if (dataMap.isEmpty()) {
            start();
        }
        dataMap.put(playerID, data);
    }

    /**
     * Removes the objective from the player. It does not complete it nor update
     * the database. In order to complete it, use completeObjective() instead.
     * In order to remove it from database use PlayerData.deleteObjective()
     * instead.
     *
     * @param playerID ID of the player
     */
    public final synchronized void removePlayer(final String playerID) {
        dataMap.remove(playerID);
        if (dataMap.isEmpty()) {
            stop();
        }
    }

    /**
     * Checks if the player has this objective
     *
     * @param playerID ID of the player
     * @return true if the player has this objective
     */
    public final boolean containsPlayer(final String playerID) {
        return dataMap.containsKey(playerID);
    }

    /**
     * Returns the data of the specified player
     *
     * @param playerID ID of the player
     * @return the data string for this objective
     */
    public final String getData(final String playerID) {
        final ObjectiveData data = dataMap.get(playerID);
        if (data == null) {
            return null;
        }
        return dataMap.get(playerID).toString();
    }

    /**
     * Returns the label of this objective. Don't worry about it, it's only used
     * by the rest of BetonQuest's logic.
     *
     * @return the label of the objective
     */
    public final String getLabel() {
        return instruction.getID().getFullID();
    }

    /**
     * Sets the label of this objective. Don't worry about it, it's only used by
     * the rest of BetonQuest's logic.
     *
     * @param rename new ID of the objective
     */
    public void setLabel(final ObjectiveID rename) {
        instruction = new Instruction(instruction.getPackage(), rename, instruction.toString());
    }

    /**
     * Should be called at the end of the use of this objective, for example
     * when reloading the plugin. It will unregister listeners and save all
     * player's data to their "inactive" map.
     */
    public void close() {
        stop();
        for (final String playerID : dataMap.keySet()) {
            BetonQuest.getInstance().getPlayerData(playerID).addRawObjective(instruction.getID().getFullID(),
                    dataMap.get(playerID).toString());
        }
    }

    /**
     * @return if the objective is a global objective
     */
    public boolean isGlobal() {
        return global;
    }

    /**
     * A task that may throw a {@link QuestRuntimeException}
     */
    protected interface QREThrowing {

        void run() throws QuestRuntimeException;
    }

    /**
     * Stores player's data for the objective
     *
     * @author Jakub Sapalski
     */
    protected static class ObjectiveData {

        protected String instruction;
        protected String playerID;
        protected String objID;

        /**
         * The ObjectiveData object is loaded from the database and the
         * constructor needs to parse the data in the instruction, so it can be
         * later retrieved and modified by your objective code.
         *
         * @param instruction the instruction of the data object; parse it to get all
         *                    required information
         * @param playerID    ID of the player
         * @param objID       ID of the objective, used by BetonQuest to store this
         *                    ObjectiveData in the database
         */
        public ObjectiveData(final String instruction, final String playerID, final String objID) {
            this.instruction = instruction;
            this.playerID = playerID;
            this.objID = objID;
        }

        /**
         * This method should return the whole instruction string, which can be
         * successfully parsed by the constructor. This method is used by
         * BetonQuest to save the ObjectiveData to the database. That's why the
         * output syntax here must be compatible with input syntax in the
         * constructor.
         *
         * @return the instruction string
         */
        public String toString() {
            return instruction;
        }

        /**
         * <p>
         * Should be called when the data inside ObjectiveData changes. It will
         * update the database with the changes.
         * </p>
         *
         * <p>
         * If you forget it, the objective will still work for players who don't
         * leave the server. However, if someone leaves before completing, they
         * will have to start this objective from scratch.
         * </p>
         */
        protected void update() {
            final Saver saver = BetonQuest.getInstance().getSaver();
            saver.add(new Saver.Record(Connector.UpdateType.REMOVE_OBJECTIVES, new String[]{playerID, objID}));
            saver.add(new Saver.Record(Connector.UpdateType.ADD_OBJECTIVES, new String[]{playerID, objID, toString()}));
            final QuestDataUpdateEvent event = new QuestDataUpdateEvent(playerID, objID, toString());
            Bukkit.getScheduler().runTask(BetonQuest.getInstance(), () -> Bukkit.getPluginManager().callEvent(event));
            // update the journal so all possible variables display correct
            // information
            BetonQuest.getInstance().getPlayerData(playerID).getJournal().update();
        }

    }

    /**
     * Can handle thrown{@link QuestRuntimeException} and rate limits them so
     * they don't spam console that hard
     *
     * @author Jonas Blocher
     */
    protected class QREHandler {

        /**
         * Interval in which errors are logged
         */
        public static final int ERROR_RATE_LIMIT_MILLIS = 5000;

        public long last = 0;

        private QREHandler() {}
        /**
         * Runs a task and logs occurring quest runtime exceptions with a rate
         * limit
         *
         * @param qreThrowing a task that may throw a quest runtime exception
         */
        public void handle(final QREThrowing qreThrowing) {
            try {
                qreThrowing.run();
            } catch (QuestRuntimeException e) {
                if (System.currentTimeMillis() - last < ERROR_RATE_LIMIT_MILLIS) {
                    return;
                }
                last = System.currentTimeMillis();
                LogUtils.getLogger().log(Level.WARNING,
                        "Error while handling '" + instruction.getID() + "' objective: " + e.getMessage());
                LogUtils.logThrowable(e);
            }
        }
    }
}
