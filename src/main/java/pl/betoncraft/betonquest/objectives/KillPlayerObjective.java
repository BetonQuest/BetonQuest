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
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.id.ConditionID;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class KillPlayerObjective extends Objective implements Listener {

    private final int notifyInterval;
    private int amount = 1;
    private String name = null;
    private ConditionID[] required;
    private boolean notify;

    public KillPlayerObjective(Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = KillData.class;
        amount = instruction.getInt();
        if (amount < 1) {
            throw new InstructionParseException("Amount cannot be less than 0");
        }
        name = instruction.getOptional("name");
        required = instruction.getList(instruction.getOptional("required"), e -> instruction.getCondition(e))
                .toArray(new ConditionID[0]);
        notifyInterval = instruction.getInt(instruction.getOptional("notify"), 1);
        notify = instruction.hasArgument("notify") || notifyInterval > 1;
    }

    @EventHandler(ignoreCancelled = true)
    public void onKill(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() == null) {
            return;
        }
        String victim = PlayerConverter.getID(event.getEntity());
        String killer = PlayerConverter.getID(event.getEntity().getKiller());
        if (containsPlayer(killer)) {
            if (name != null && !event.getEntity().getName().equalsIgnoreCase(name)) {
                return;
            }
            for (ConditionID condition : required) {
                if (!BetonQuest.condition(victim, condition)) {
                    return;
                }
            }
            if (!checkConditions(killer)) {
                return;
            }
            KillData data = (KillData) dataMap.get(killer);
            data.kill();
            if (data.getLeft() <= 0) {
                completeObjective(killer);
            } else if (notify && data.getLeft() % notifyInterval == 0) {
                Config.sendNotify(killer, "players_to_kill", new String[]{String.valueOf(data.getLeft())},
                        "players_to_kill,info");
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
        return String.valueOf(amount);
    }

    public static class KillData extends ObjectiveData {

        private int amount;

        public KillData(String instruction, String playerID, String objID) {
            super(instruction, playerID, objID);
            amount = Integer.parseInt(instruction);
        }

        public void kill() {
            amount--;
            update();
        }

        public int getLeft() {
            return amount;
        }

        @Override
        public String toString() {
            return String.valueOf(amount);
        }

    }

}
