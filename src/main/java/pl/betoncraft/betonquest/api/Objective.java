/**
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2015  Jakub "Co0sh" Sapalski
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

import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.utils.Debug;

/**
 * Superclass for all objectives. You need to extend it in order to create new
 * custom objectives.
 * <p/>
 * Registering your objectives is done through {@link
 * pl.betoncraft.betonquest.BetonQuest#registerObjectives(String, Class<?
 * extends Objective>) registerObjectives} method.
 * 
 * @author Co0sh
 */
public abstract class Objective {

    /**
     * Stores ID of the player.
     */
    protected String playerID;
    /**
     * Stores instruction string for the objective.
     */
    protected String instructions;
    /**
     * Stores conditions string with leading "conditions:" label.
     */
    protected String conditions;
    /**
     * Stores events string with leading "events:" label.
     */
    protected String events;
    /**
     * Stores objective's tag (without leading "tag:" label!)
     */
    protected String tag;
    /**
     * Determines if debugging should be turned on for this objective or not
     */
    protected boolean debug = false;

    /**
     * Creates new instance of the objective. The objective should parse
     * instruction string at this point and extract all the data from it. It
     * should also register needed Listeners.
     * 
     * @param playerID
     *            ID of the player this objective is related to. It will be
     *            passed at runtime, you only need to use it according to what
     *            your objective does.
     * @param instructions
     *            instruction string passed at runtime. You need to extract all
     *            required data from it and display errors if there is anything
     *            wrong.
     */
    public Objective(String playerID, String instructions) {
        this.playerID = playerID;
        this.instructions = instructions;
        // extract tag, events and conditions
        for (String part : instructions.split(" ")) {
            if (part.contains("tag:")) {
                tag = part.substring(4);
            }
            if (part.contains("events:")) {
                events = part;
            }
            if (part.contains("conditions:")) {
                conditions = part;
            }
            if (part.equals("--debug")) {
                debug =  true;
            }
        }
    }

    /**
     * This method fires events for the objective and removes it from player's
     * list of active objectives. Use it when you detect that the objective has
     * been completed. Remember to unregister all Listeners you registered!
     */
    protected void completeObjective() {
        Debug.info("Objective \"" + tag + "\" has been completed for player " + playerID
            + ", firing final events");
        // split instructions
        String[] parts = instructions.split(" ");
        String rawEvents = null;
        // find part with events
        for (String part : parts) {
            if (part.contains("events:")) {
                // extract events
                rawEvents = part.substring(7);
                break;
            }
        }
        // if there are any events, do something with them
        if (rawEvents != null && !rawEvents.equalsIgnoreCase("")) {
            // split them to separate ids
            final String[] events = rawEvents.split(",");
            new BukkitRunnable() {
                @Override
                public void run() {
                    // fire all events
                    for (String eventID : events) {
                        BetonQuest.event(playerID, eventID);
                    }
                }
            }.runTask(BetonQuest.getInstance());
        }
        // remove the objective from player's list
        Debug.info("Firing events in objective \"" + tag + "\" for player " + playerID
            + " finished");
        BetonQuest.getInstance().getDBHandler(playerID).deleteObjective(tag);
    }

    /**
     * Checks if all conditions has been met. Use it when the player has done
     * something that modifies data (e.g. killing zombies). If conditions are
     * met, you can safely modify the data.
     * 
     * @return if all conditions of this objective has been met
     */
    protected boolean checkConditions() {
        Debug.info("Condition check in \"" + tag + "\" objective for player "
                + playerID);
        // split instructions
        String[] parts = instructions.split(" ");
        // find part with conditions
        String rawConditions = null;
        for (String part : parts) {
            if (part.contains("conditions:")) {
                // extract conditions
                rawConditions = part.substring(11);
                break;
            }
        }
        // if there are any conditions, do something with them
        if (rawConditions != null && !rawConditions.equalsIgnoreCase("")) {
            // split them to separate ids
            String[] conditions = rawConditions.split(",");
            // if some condition is not met, return false
            for (String conditionID : conditions) {
                if (!BetonQuest.condition(playerID, conditionID)) {
                    return false;
                }
            }
        }
        // if there are no conditions or all of them are met return true
        return true;
    }

    /**
     * This method is called by the plugin when the objective needs to be
     * deleted or saved to the database. You must return a valid instruction
     * string with updated data (if the player killed 4 of 10 zombies, it needs
     * to return 6 more zombies). You also have to unregister all Listeners
     * 
     * @return the instruction string
     */
    abstract public String getInstructions();

    /**
     * Returns the tag of this objective. Don't worry about it, it's only used
     * by the rest of BetonQuest's logic.
     * 
     * @return the tag
     */
    public String getTag() {
        return tag;
    }

}