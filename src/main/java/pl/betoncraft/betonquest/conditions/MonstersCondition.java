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
package pl.betoncraft.betonquest.conditions;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.MetadataValue;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LocationData;
import pl.betoncraft.betonquest.utils.Utils;

import java.util.Collection;
import java.util.List;

/**
 * Checks if there are specified monsters in the area
 *
 * @author Jakub Sapalski
 */
public class MonstersCondition extends Condition {

    private EntityType[] types;
    private VariableNumber[] amounts;
    private LocationData loc;
    private VariableNumber range;
    private String name;
    private String marked;

    public MonstersCondition(Instruction instruction) throws InstructionParseException {
        super(instruction);
        staticness = true;
        persistent = true;
        String[] rawTypes = instruction.getArray();
        types = new EntityType[rawTypes.length];
        amounts = new VariableNumber[rawTypes.length];
        for (int i = 0; i < rawTypes.length; i++) {
            try {
                if (rawTypes[i].contains(":")) {
                    String[] typeParts = rawTypes[i].split(":");
                    if (typeParts.length == 0) {
                        throw new InstructionParseException("Type not defined");
                    } else if (typeParts.length < 2) {
                        types[i] = EntityType.valueOf(typeParts[0].toUpperCase());
                        amounts[i] = new VariableNumber(1);
                    } else {
                        types[i] = EntityType.valueOf(typeParts[0].toUpperCase());
                        try {
                            amounts[i] = new VariableNumber(instruction.getPackage().getName(), typeParts[1]);
                        } catch (NumberFormatException e) {
                            throw new InstructionParseException("Could not parse amount", e);
                        }
                    }
                } else {
                    types[i] = EntityType.valueOf(rawTypes[i].toUpperCase());
                    amounts[i] = new VariableNumber(1);
                }
            } catch (IllegalArgumentException e) {
                throw new InstructionParseException("Unknown mob type: " + rawTypes[i], e);
            }
        }
        loc = instruction.getLocation();
        range = instruction.getVarNum();
        name = instruction.getOptional("name");
        marked = instruction.getOptional("marked");
        if (marked != null) {
            marked = Utils.addPackage(instruction.getPackage(), marked);
        }
    }

    @Override
    public boolean check(String playerID) throws QuestRuntimeException {
        Location location = loc.getLocation(playerID);
        int[] neededAmounts = new int[types.length];
        for (int i = 0; i < neededAmounts.length; i++) {
            neededAmounts[i] = 0;
        }
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
                EntityType theType = entity.getType();
                for (int i = 0; i < types.length; i++) {
                    if (theType == types[i]) {
                        neededAmounts[i]++;
                        break;
                    }
                }
            }
        }
        for (int i = 0; i < amounts.length; i++) {
            if (neededAmounts[i] < amounts[i].getInt(playerID)) {
                return false;
            }
        }
        return true;
    }

}
