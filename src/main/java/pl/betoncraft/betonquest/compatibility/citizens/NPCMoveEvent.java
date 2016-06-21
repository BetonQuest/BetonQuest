/**
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
package pl.betoncraft.betonquest.compatibility.citizens;

import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.ai.event.NavigationCompleteEvent;
import net.citizensnpcs.api.npc.NPC;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.Debug;
import pl.betoncraft.betonquest.utils.LocationData;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Moves the NPC to a specified location, optionally firing doneEvents when it's done.
 * 
 * @author Jakub Sapalski
 */
public class NPCMoveEvent extends QuestEvent implements Listener {
	
	private static LinkedList<NPC> movingNPCs = new LinkedList<>();
	
	private final Listener ths;
	private int id;
	private LocationData loc;
	private int waitTicks = 0;
	private String[] doneEvents = new String[0];
	private String[] failEvents = new String[0];
	private String currentPlayer;

	public NPCMoveEvent(String packName, String instructions) throws InstructionParseException {
		super(packName, instructions);
		ths = this;
		String[] parts = instructions.split(" ");
		if (parts.length < 3) {
			throw new InstructionParseException("Not enough arguments");
		}
		try {
			id = Integer.parseInt(parts[1]);
		} catch (NumberFormatException e) {
			throw new InstructionParseException("Could not parse NPC ID");
		}
		if (id < 0) {
			throw new InstructionParseException("NPC ID cannot be less than 0");
		}
		loc = new LocationData(packName, parts[2]);
		for (String part : parts) {
			if (part.startsWith("wait:")) {
				try {
					waitTicks = Integer.parseInt(part.substring(5));
				} catch (NumberFormatException e) {
					throw new InstructionParseException("Could not parse waiting time");
				}
			} else if (part.startsWith("done:")) {
				String[] events = part.substring(5).split(",");
				for (int i = 0; i < events.length; i++) {
					if (!events[i].contains(".")) {
						events[i] = packName + "." + events[i];
					}
				}
				doneEvents = events;
			} else if (part.startsWith("fail:")) {
				String[] events = part.substring(5).split(",");
				for (int i = 0; i < events.length; i++) {
					if (!events[i].contains(".")) {
						events[i] = packName + "." + events[i];
					}
				}
				failEvents = events;
			}
		}
	}

	@Override
	public void run(String playerID) throws QuestRuntimeException {
		// this event should not run if the player is offline
		if (PlayerConverter.getPlayer(playerID) == null) {
			currentPlayer = null;
			return;
		}
		NPC npc = CitizensAPI.getNPCRegistry().getById(id);
		if (npc == null) {
			BetonQuest.getInstance().getLogger().warning("NPC with ID " + id + " does not exist");
			return;
		}
		if (currentPlayer == null) {
			npc.getNavigator().setTarget(loc.getLocation(playerID));
			currentPlayer = playerID;
			movingNPCs.add(npc);
			Bukkit.getPluginManager().registerEvents(ths, BetonQuest.getInstance());
		} else {
			for (String event : failEvents) {
				BetonQuest.event(playerID, event);
			}
		}
	}
	
	@EventHandler
	public void onNavigationEnd(final NavigationCompleteEvent event) {
		NPC npc = event.getNPC();
		if (npc.getId() != id) {
			return;
		}
		HandlerList.unregisterAll(ths);
		try {
			npc.getNavigator().setTarget(loc.getLocation(currentPlayer));
		} catch (QuestRuntimeException e) {
			Debug.error("Error while finishing NPC " + npc.getId() + " navigation: " + e.getMessage());
		}
		npc.getNavigator().setPaused(true);
		new BukkitRunnable() {
			@Override
			public void run() {
				npc.getNavigator().setPaused(false);
				currentPlayer = null;
				movingNPCs.remove(npc);
				for (String event : doneEvents) {
					BetonQuest.event(currentPlayer, event);
				}
			}
		}.runTaskLater(BetonQuest.getInstance(), waitTicks);
	}
	
	/**
	 * Checks whenever this NPC is moving because of a 'move' event or not.
	 * 
	 * @param npc
	 *            NPC to check
	 * @return true if the NPC is moving because of 'move' event, false if it's
	 *         standing or moving because other reasons
	 */
	public static boolean isNPCMoving(NPC npc) {
		return movingNPCs.contains(npc);
	}

}
