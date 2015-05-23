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

import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.config.ConfigPackage;

/**
 * Superclass for all conditions. You need to extend it in order to create new
 * custom conditions.
 * <p/>
 * Registering your condition is done through {@link
 * pl.betoncraft.betonquest.BetonQuest#registerConditions( String, Class<?
 * extends Condition>) registerConditions} method.
 * 
 * @author Co0sh
 */
abstract public class Condition {

    /**
     * Stores ID of the player.
     */
    protected String playerID;
    /**
     * Stores instruction string for the condition.
     */
    protected String instructions;
    /**
     * True if the condition was prepared correctly.
     */
    protected boolean isOk = true;
    /**
     * Stores the package name from which this condition was checked
     */
    protected String packName;
    /**
     * ConfigPackage in which this condition is defined
     */
    protected ConfigPackage pack;

    /**
     * Creates new instance of the condition. The condition should parse
     * instruction string at this point and extract all the data from it.
     * 
     * @param playerID
     *            ID of the player this condition is related to. It will be
     *            passed at runtime, you only need to use it according to what
     *            your condition checks.
     * @param instructions
     *            instruction string passed at runtime. You need to extract all
     *            required data from it and display errors if there is anything
     *            wrong.
     */
    public Condition(String playerID, String pack, String instructions) {
        this.playerID = playerID;
        this.packName = pack;
        this.instructions = instructions;
        this.pack = Config.getPackage(packName);
    }

    /**
     * Checks if the condition is met. It should contain all logic for the
     * condition and use data parsed by the constructor. Don't worry about
     * inverting the condition, it's done by the rest of BetonQuest's logic.
     * 
     * @return if the condition is met
     */
    abstract public boolean isMet();
}
