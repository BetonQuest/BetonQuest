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
import pl.betoncraft.betonquest.EventID;
import pl.betoncraft.betonquest.Instruction;
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
	private int waitTicks;
	private EventID[] doneEvents;
	private EventID[] failEvents;
	private String currentPlayer;

	public NPCMoveEvent(Instruction instruction) throws InstructionParseException {
		super(instruction);
		ths = this;
		id = instruction.getInt();
		if (id < 0) {
			throw new InstructionParseException("NPC ID cannot be less than 0");
		}
		loc = instruction.getLocation();
		waitTicks = instruction.getInt(instruction.getOptional("wait"), 0);
		doneEvents = instruction.getList(instruction.getOptional("done"), e -> instruction.getEvent(e)).toArray(new EventID[0]);
		failEvents = instruction.getList(instruction.getOptional("fail"), e -> instruction.getEvent(e)).toArray(new EventID[0]);
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
		if (!npc.isSpawned()) {
			return;
		}
		if (currentPlayer == null) {
			npc.getNavigator().setTarget(loc.getLocation(playerID));
			currentPlayer = playerID;
			movingNPCs.add(npc);
			Bukkit.getPluginManager().registerEvents(ths, BetonQuest.getInstance());
		} else {
			for (EventID event : failEvents) {
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
				for (EventID event : doneEvents) {
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
