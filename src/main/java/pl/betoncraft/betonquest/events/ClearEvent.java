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
package pl.betoncraft.betonquest.events;

import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.utils.LocationData;

/**
 * Clears all specified monsters in a certain location
 * 
 * @author Jakub Sapalski
 */
public class ClearEvent extends QuestEvent {

	private final EntityType[] types;
	private final LocationData loc;
	private final String name;
	private final boolean kill;

	public ClearEvent(Instruction instruction) throws InstructionParseException {
		super(instruction);
		staticness = true;
		String[] entities = instruction.getArray();
		types = new EntityType[entities.length];
		for (int i = 0; i < types.length; i++) {
			try {
				types[i] = EntityType.valueOf(entities[i].toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new InstructionParseException("Entity type '" + entities[i] + "' does not exist");
			}
		}
		loc = instruction.getLocation();
		name = instruction.getOptional("name");
		kill = instruction.hasArgument("kill");
	}

	@Override
	public void run(String playerID) throws QuestRuntimeException {
		Location location = loc.getLocation(playerID);
		Collection<Entity> entities = location.getWorld().getEntities();
		for (Entity entity : entities) {
			if (!(entity instanceof LivingEntity)) {
				continue;
			}
			double rangeDouble = loc.getData().getDouble(playerID);
			if (entity.getLocation().distanceSquared(location) < rangeDouble * rangeDouble) {
				EntityType theType = entity.getType();
				for (EntityType type : types) {
					if (theType == type) {
						if (name != null) {
							if (entity.getCustomName() != null && entity.getCustomName().equals(name)) {
								if (kill) {
									LivingEntity living = (LivingEntity) entity;
									living.damage(living.getHealth() + 10);
								} else {
									entity.remove();
								}
							}
						} else {
							if (kill) {
								LivingEntity living = (LivingEntity) entity;
								living.damage(living.getHealth() + 10);
							} else {
								entity.remove();
							}
						}
						break;
					}
				}
			}
		}
	}

}
