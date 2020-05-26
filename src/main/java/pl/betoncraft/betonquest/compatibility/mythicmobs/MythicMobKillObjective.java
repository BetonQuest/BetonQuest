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
package pl.betoncraft.betonquest.compatibility.mythicmobs;

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.HashSet;
import java.util.Set;

/**
 * Player has to kill MythicMobs monster
 *
 * @author Jakub Sapalski
 */
public class MythicMobKillObjective extends Objective implements Listener {

    private final Set<String> names = new HashSet<>();
    private final int amount;
    private final boolean notify;

    public MythicMobKillObjective(Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = MMData.class;
        for (String name : instruction.getArray())
            names.add(name);
        amount = instruction.getInt(instruction.getOptional("amount"), 1);
        notify = instruction.hasArgument("notify");
    }

    @EventHandler(ignoreCancelled = true)
    public void onBossKill(MythicMobDeathEvent event) {
        if (names.contains(event.getMobType().getInternalName()) && event.getKiller() instanceof Player) {
            String playerID = PlayerConverter.getID((Player) event.getKiller());
            if (containsPlayer(playerID) && checkConditions(playerID)) {
                MMData playerData = (MMData) dataMap.get(playerID);
                playerData.kill();
                if (playerData.killed()) {
                    completeObjective(playerID);
                } else if (notify) {
                    // send a notification
                    Config.sendNotify(playerID, "mobs_to_kill",
                            new String[]{String.valueOf(playerData.getAmount())}, "mobs_to_kill,info");
                }
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
            return Integer.toString(((MMData) dataMap.get(playerID)).getAmount());
        } else if (name.equalsIgnoreCase("amount")) {
            return Integer.toString(amount - ((MMData) dataMap.get(playerID)).getAmount());
        }
        return "";
    }

    public static class MMData extends ObjectiveData {

        private int amount;

        public MMData(String instruction, String playerID, String objID) {
            super(instruction, playerID, objID);
            amount = Integer.parseInt(instruction);
        }

        private void kill() {
            amount--;
            update();
        }

        private boolean killed() {
            return amount <= 0;
        }

        private int getAmount() {
            return amount;
        }

        @Override
        public String toString() {
            return String.valueOf(amount);
        }

    }

}
