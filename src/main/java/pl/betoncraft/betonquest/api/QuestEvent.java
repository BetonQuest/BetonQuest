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
 * Superclass for all events. You need to extend it in order to create new
 * custom events.
 * <p/>
 * Registering your events is done through {@link
 * pl.betoncraft.betonquest.BetonQuest#registerEvents(String, Class<? extends
 * QuestEvent>) registerEvents} method.
 * 
 * @author Co0sh
 */
public abstract class QuestEvent {

    /**
     * Stores ID of the player.
     */
    protected String playerID;
    /**
     * Stores instruction string for the event.
     */
    protected String instructions;
    /**
     * Stores the package name from which this event fired
     */
    protected String packName;
    /**
     * ConfigPackage in which this event is defined
     */
    protected ConfigPackage pack;

    /**
     * Creates new instance of the event. The event should parse instruction
     * string and immediately do it's job.
     * 
     * @param playerID
     *            ID of the player this event is related to. It will be passed
     *            at runtime, you only need to use it according to what your
     *            event does.
     * @param instructions
     *            instruction string passed at runtime. You need to extract all
     *            required data from it and display errors if there is anything
     *            wrong.
     */
    public QuestEvent(String playerID, String pack, String instructions) {
        this.playerID = playerID;
        this.instructions = instructions;
        this.packName = pack;
        this.pack = Config.getPackage(packName);
    }
}
