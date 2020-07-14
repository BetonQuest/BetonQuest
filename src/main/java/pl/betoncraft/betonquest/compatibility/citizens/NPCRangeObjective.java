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
package pl.betoncraft.betonquest.compatibility.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Player has to walk towards/away form a npc
 * <p>
 * Created on 30.09.2018.
 *
 * @author Jonas Blocher
 */
public class NPCRangeObjective extends Objective implements Listener {

    private final int id;
    private final Trigger trigger;
    private final VariableNumber radius;

    public NPCRangeObjective(Instruction instruction) throws InstructionParseException {
        super(instruction);
        super.template = ObjectiveData.class;
        id = instruction.getInt();
        trigger = instruction.getEnum(Trigger.class);
        radius = instruction.getVarNum();
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        qreHandler.handle(() -> {
            final Player player = event.getPlayer();
            final String playerID = PlayerConverter.getID(player);
            if (!containsPlayer(playerID)) return;
            final NPC npc = CitizensAPI.getNPCRegistry().getById(id);
            if (npc == null)
                throw new QuestRuntimeException("NPC with ID " + id + " does not exist");
            final double radius = this.radius.getDouble(playerID);
            final Entity npcEntity = npc.getEntity();
            if (npcEntity == null) return;
            if (!npcEntity.getWorld().equals(event.getTo().getWorld())) return;
            final double distanceSqrd = npcEntity.getLocation().distanceSquared(event.getTo());
            final double radiusSqrd = radius * radius;
            if ((trigger == Trigger.ENTER && distanceSqrd <= radiusSqrd)
                    || (trigger == Trigger.LEAVE && distanceSqrd >= radiusSqrd)) {
                if (checkConditions(playerID)) completeObjective(playerID);
            }
        });
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
        return "";
    }

    private enum Trigger {
        ENTER,
        LEAVE
    }
}
