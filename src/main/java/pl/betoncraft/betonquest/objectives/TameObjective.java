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
package pl.betoncraft.betonquest.objectives;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTameEvent;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * The player must tame specified amount of specified mobs
 *
 * @author Jakub Sapalski
 */
public class TameObjective extends Objective implements Listener {

    private final TamableMobs type;
    private final int amount;

    public TameObjective(Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = TameData.class;
        String string = instruction.next();
        try {
            type = TamableMobs.valueOf(string.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InstructionParseException("Unknown type: " + string, e);
        }
        amount = instruction.getInt();
        if (amount < 1) {
            throw new InstructionParseException("Amount cannot be less than 1");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onTaming(EntityTameEvent event) {
        if (event.getOwner() instanceof Player) {
            String playerID = PlayerConverter.getID((Player) event.getOwner());
            if (!dataMap.containsKey(playerID)) {
                return;
            }
            LivingEntity entity = event.getEntity();
            TameData playerData = (TameData) dataMap.get(playerID);
            switch (type) {
                case WOLF:
                    if (entity.getType().equals(EntityType.WOLF) && checkConditions(playerID)) {
                        playerData.subtract();
                    }
                    break;
                case OCELOT:
                    if (entity.getType().equals(EntityType.OCELOT) && checkConditions(playerID)) {
                        playerData.subtract();
                    }
                    break;
                case HORSE:
                    if (entity.getType().equals(EntityType.HORSE) && checkConditions(playerID)) {
                        playerData.subtract();
                    }
                    break;
                default:
                    break;
            }
            if (playerData.isZero()) {
                completeObjective(playerID);
            }
        }
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getDefaultDataInstruction() {
        return Integer.toString(amount);
    }

    @Override
    public String getProperty(String name, String playerID) {
        if (name.equalsIgnoreCase("left")) {
            return Integer.toString(amount - ((TameData) dataMap.get(playerID)).getAmount());
        } else if (name.equalsIgnoreCase("amount")) {
            return Integer.toString(((TameData) dataMap.get(playerID)).getAmount());
        }
        return "";
    }

    private enum TamableMobs {
        WOLF, OCELOT, HORSE
    }

    public static class TameData extends ObjectiveData {

        private int amount;

        public TameData(String instruction, String playerID, String objID) {
            super(instruction, playerID, objID);
            amount = Integer.parseInt(instruction);
        }

        @Override
        public String toString() {
            return Integer.toString(amount);
        }

        private int getAmount() {
            return amount;
        }

        private void subtract() {
            amount--;
            update();
        }

        private boolean isZero() {
            return amount <= 0;
        }

    }

}
