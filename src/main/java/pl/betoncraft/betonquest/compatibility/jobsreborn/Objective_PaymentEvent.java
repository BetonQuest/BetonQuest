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

package pl.betoncraft.betonquest.compatibility.jobsreborn;

import com.gamingmesh.jobs.api.JobsPaymentEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class Objective_PaymentEvent extends Objective implements Listener {
    private final double nAmount;

    public Objective_PaymentEvent(Instruction instructions) throws InstructionParseException {
        super(instructions);
        template = ObjectiveData.class;
        if (instructions.size() < 2) {
            throw new InstructionParseException("Not enough arguments");
        }
        try {
            nAmount = Double.parseDouble(instructions.getPart(1));
        } catch (NumberFormatException e) {
            throw new InstructionParseException("Could not parse amount", e);
        }
        if (nAmount < 1) {
            throw new InstructionParseException("Amount needs to be one or more");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onJobsPaymentEvent(JobsPaymentEvent event) {
        String playerID = PlayerConverter.getID(event.getPlayer().getPlayer().getPlayer());
        if (containsPlayer(playerID) && checkConditions(playerID)) {
            PaymentData playerData = (PaymentData) dataMap.get(playerID);
            Bukkit.getServer().broadcastMessage("Amount: " + playerData.getAmount());
            playerData.subtract(event.getAmount());

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
        return Double.toString(nAmount);
    }

    public String getProperty(String name, String playerID) {
        if (name.equalsIgnoreCase("left")) {
            return Double.toString(nAmount - ((PaymentData) dataMap.get(playerID)).getAmount());
        } else if (name.equalsIgnoreCase("amount")) {
            return Double.toString(((PaymentData) dataMap.get(playerID)).getAmount());
        }
        return "";
    }

    public static class PaymentData extends ObjectiveData {

        private Double amount;

        public PaymentData(String instruction, String playerID, String objID) {
            super(instruction, playerID, objID);
            amount = Double.parseDouble(instruction);
        }

        private Double getAmount() {
            return amount;
        }

        private void subtract(Double amount) {
            this.amount -= amount;
            update();
        }

        private boolean isZero() {
            return amount <= 0;
        }

        @Override
        public String toString() {
            return Double.toString(amount);
        }

    }
}
