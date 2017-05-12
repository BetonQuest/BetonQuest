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
package pl.betoncraft.betonquest.objectives;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.metadata.MetadataValue;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.objectives.MobKillObjective.MobData;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Utils;

/**
 * Player has to interact with specified amount of specified mobs. It can also
 * require the player to interact with specifically named mobs and notify them
 * about the required amount. It can be specified if the player has to
 * rightclick or damage the entity. Each entity can only be interacted once.
 * 
 * @author Jonas Blocher
 */
public class EntityInteractObjective extends Objective {

	protected EntityType mobType;
	protected int amount;
	protected String name;
	protected String marked;
	protected boolean notify;
	protected Interaction interaction;

	private RightClickListener rightClickListener;
	private DamageListener damageListener;

	public enum Interaction {
		RIGHT_CLICK, DAMAGE, ANY
	}

	public EntityInteractObjective(Instruction instruction) throws InstructionParseException {
		super(instruction);
		template = EntityInteractData.class;
		interaction = instruction.getEnum(Interaction.class);
		mobType = instruction.getEnum(EntityType.class);
		amount = instruction.getPositive();
		name = instruction.getOptional("name");
		if (name != null) {
			name = name.replace('_', ' ');
		}
		marked = instruction.getOptional("marked");
		if (marked != null) {
			marked = Utils.addPackage(instruction.getPackage(), marked);
		}
		notify = instruction.hasArgument("notify");
	}

	@Override
	public void start() {
		switch (interaction) {
		case RIGHT_CLICK:
			rightClickListener = new RightClickListener();
			break;
		case DAMAGE:
			damageListener = new DamageListener();
			break;
		case ANY:
			rightClickListener = new RightClickListener();
			damageListener = new DamageListener();
			break;
		}
	}

	private void onInteract(Player player, Entity entity) {
		// check if it's the right entity type
		if (!entity.getType().equals(mobType)) {
			return;
		}
		// if the entity should have a name and it does not match, return
		if (name != null && (entity.getCustomName() == null || !entity.getCustomName().equals(name))) {
			return;
		}
		// check if the entity is correctly marked
		if (marked != null) {
			if (!entity.hasMetadata("betonquest-marked")) {
				return;
			}
			List<MetadataValue> meta = entity.getMetadata("betonquest-marked");
			for (MetadataValue m : meta) {
				if (!m.asString().equals(marked)) {
					return;
				}
			}
		}
		// check if the player has this objective
		String playerID = PlayerConverter.getID(player);
		if (containsPlayer(playerID) && checkConditions(playerID)) {
			// get data off the player
			EntityInteractData playerData = (EntityInteractData) dataMap.get(playerID);
			// check if player already interacted with entity
			if (playerData.containsEntity(entity))
				return;
			// right mob is interacted with, handle data update
			playerData.subtract();
			playerData.addEntity(entity);
			if (playerData.isZero()) {
				completeObjective(playerID);
			} else if (notify) {
				// send a notification
				Config.sendMessage(playerID, "mobs_to_kill", new String[] { String.valueOf(playerData.getAmount()) });
			}
		}
	}

	@Override
	public void stop() {
		if (rightClickListener != null)
			HandlerList.unregisterAll(rightClickListener);
		if (damageListener != null)
			HandlerList.unregisterAll(damageListener);
	}

	@Override
	public String getDefaultDataInstruction() {
		return Integer.toString(amount);
	}

	@Override
	public String getProperty(String name, String playerID) {
		if (name.equalsIgnoreCase("left")) {
			return Integer.toString(((EntityInteractData) dataMap.get(playerID)).getAmount());
		} else if (name.equalsIgnoreCase("amount")) {
			return Integer.toString(amount - ((EntityInteractData) dataMap.get(playerID)).getAmount());
		}
		return "";
	}

	private static class EntityInteractData extends ObjectiveData {

		private int amount;
		private Set<UUID> entitys;

		public EntityInteractData(String instruction, String playerID, String objID) {
			super(instruction, playerID, objID);
			String[] args = instruction.split("-");
			amount = Integer.parseInt(args[0].trim());
			entitys = new HashSet<>();
			if (args.length > 1) {
				String[] uuids = args[1].trim().split(" ");
				for (String uuid : uuids) {
					entitys.add(UUID.fromString(uuid));
				}
			}
		}

		public void addEntity(Entity entity) {
			entitys.add(entity.getUniqueId());
			update();
		}

		public boolean containsEntity(Entity entity) {
			return entitys.contains(entity.getUniqueId());
		}

		public int getAmount() {
			return amount;
		}

		public void subtract() {
			amount--;
			update();
		}

		public boolean isZero() {
			return amount <= 0;
		}

		@Override
		public String toString() {
			String string = Integer.toString(amount);
			if (!entitys.isEmpty()) {
				string += " -";
				for (UUID uuid : entitys) {
					string += " " + uuid.toString();
				}
			}
			return string;
		}

	}

	private class DamageListener implements Listener {
		public DamageListener() {
			Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
		}

		@EventHandler
		public void onDamage(EntityDamageByEntityEvent e) {
			Player player;
			// check if entity is damaged by a Player
			if (e.getDamager() instanceof Player) {
				player = (Player) e.getDamager();
			} else {
				return;
			}
			onInteract(player, e.getEntity());
		}
	}

	private class RightClickListener implements Listener {
		public RightClickListener() {
			Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
		}

		@EventHandler
		public void onRightClick(PlayerInteractEntityEvent e) {
			onInteract(e.getPlayer(), e.getRightClicked());
		}
	}
}
