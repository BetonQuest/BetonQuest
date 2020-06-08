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
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerShearEntityEvent;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Requires the player to shear a sheep.
 *
 * @author Jakub Sapalski
 */
public class ShearObjective extends Objective implements Listener {

    private final String color;
    private final String name;
    private final int amount;
    private final boolean notify;
    private final int notifyInterval;

    public ShearObjective(Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = SheepData.class;
        amount = instruction.getPositive();
        String rawName = instruction.getOptional("name");
        name = rawName == null ? null : rawName.replace('_', ' ');
        color = instruction.getOptional("color");
        notifyInterval = instruction.getInt(instruction.getOptional("notify"), 1);
        notify = instruction.hasArgument("notify") || notifyInterval > 1;
    }

    @EventHandler(ignoreCancelled = true)
    public void onShear(PlayerShearEntityEvent event) {
        if (event.getEntity().getType() != EntityType.SHEEP)
            return;
        String playerID = PlayerConverter.getID(event.getPlayer());
        if (!containsPlayer(playerID))
            return;
        if (name != null && (event.getEntity().getCustomName() == null || !event.getEntity().getCustomName().equals(name)))
            return;
        if (color != null && !((Sheep) event.getEntity()).getColor().toString().equalsIgnoreCase(color))
            return;
        SheepData data = (SheepData) dataMap.get(playerID);

        if (checkConditions(playerID)) {
            data.shearSheep();
            // complete quest or notify
            if (data.getAmount() <= 0)
                completeObjective(playerID);
            else if (notify && data.getAmount() % notifyInterval == 0)
                Config.sendNotify(playerID, "sheep_to_shear", new String[]{String.valueOf(data.getAmount())},
                        "sheep_to_shear,info");
        }
    }

    @Override
    public String getProperty(String name, String playerID) {
        if (name.equalsIgnoreCase("left")) {
            return Integer.toString(((SheepData) dataMap.get(playerID)).getAmount());
        } else if (name.equalsIgnoreCase("amount")) {
            return Integer.toString(amount - ((SheepData) dataMap.get(playerID)).getAmount());
        }
        return "";
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

    public static class SheepData extends ObjectiveData {

        private int amount;

        public SheepData(String instruction, String playerID, String objID) {
            super(instruction, playerID, objID);
            amount = Integer.parseInt(instruction);
        }

        public void shearSheep() {
            amount--;
            update();
        }

        public int getAmount() {
            return amount;
        }

        @Override
        public String toString() {
            return String.valueOf(amount);
        }

    }

}
