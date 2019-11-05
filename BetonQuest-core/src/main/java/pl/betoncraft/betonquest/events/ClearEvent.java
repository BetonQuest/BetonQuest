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
package pl.betoncraft.betonquest.events;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.MetadataValue;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LocationData;
import pl.betoncraft.betonquest.utils.Utils;

import java.util.Collection;
import java.util.List;

/**
 * Clears all specified monsters in a certain location
 *
 * @author Jakub Sapalski
 */
public class ClearEvent extends QuestEvent {

    private EntityType[] types;
    private LocationData loc;
    private VariableNumber range;
    private String name;
    private boolean kill;
    private String marked;

    public ClearEvent(Instruction instruction) throws InstructionParseException {
        super(instruction);
        staticness = true;
        persistent = true;
        String[] entities = instruction.getArray();
        types = new EntityType[entities.length];
        for (int i = 0; i < types.length; i++) {
            try {
                types[i] = EntityType.valueOf(entities[i].toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InstructionParseException("Entity type '" + entities[i] + "' does not exist", e);
            }
        }
        loc = instruction.getLocation();
        range = instruction.getVarNum();
        name = instruction.getOptional("name");
        kill = instruction.hasArgument("kill");
        marked = instruction.getOptional("marked");
        if (marked != null) {
            marked = Utils.addPackage(instruction.getPackage(), marked);
        }
    }

    @Override
    public void run(String playerID) throws QuestRuntimeException {
        Location location = loc.getLocation(playerID);
        Collection<Entity> entities = location.getWorld().getEntities();
        loop:
        for (Entity entity : entities) {
            if (!(entity instanceof LivingEntity)) {
                continue;
            }
            if (name != null && (entity.getCustomName() == null || !entity.getCustomName().equals(name))) {
                continue;
            }
            if (marked != null) {
                if (!entity.hasMetadata("betonquest-marked")) {
                    continue;
                }
                List<MetadataValue> meta = entity.getMetadata("betonquest-marked");
                for (MetadataValue m : meta) {
                    if (!m.asString().equals(marked)) {
                        continue loop;
                    }
                }
            }
            double r = range.getDouble(playerID);
            if (entity.getLocation().distanceSquared(location) < r * r) {
                EntityType entityType = entity.getType();
                for (EntityType allowedType : types) {
                    if (entityType == allowedType) {
                        if (kill) {
                            LivingEntity living = (LivingEntity) entity;
                            living.damage(living.getHealth() + 10);
                        } else {
                            entity.remove();
                        }
                        break;
                    }
                }
            }
        }
    }

}
