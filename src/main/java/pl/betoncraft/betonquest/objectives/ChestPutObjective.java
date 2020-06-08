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
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.conditions.ChestItemCondition;
import pl.betoncraft.betonquest.events.ChestTakeEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.ObjectNotFoundException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.id.NoID;
import pl.betoncraft.betonquest.utils.LocationData;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.logging.Level;

/**
 * Requires the player to put items in the chest. Items can optionally NOT
 * disappear once the chest is closed.
 *
 * @author Jakub Sapalski
 */
public class ChestPutObjective extends Objective implements Listener {

    private final Condition chestItemCondition;
    private final QuestEvent chestTakeEvent;
    private final LocationData loc;

    public ChestPutObjective(Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = ObjectiveData.class;
        // extract location
        loc = instruction.getLocation();
        String location = instruction.current();
        String items = instruction.next();
        try {
            chestItemCondition = new ChestItemCondition(new Instruction(instruction.getPackage(), new NoID(instruction.getPackage()), "chestitem " + location + " " + items));
        } catch (InstructionParseException | ObjectNotFoundException e) {
            throw new InstructionParseException("Could not create inner chest item condition: " + e.getMessage(), e);
        }
        if (instruction.hasArgument("items-stay")) {
            chestTakeEvent = null;
        } else {
            try {
                chestTakeEvent = new ChestTakeEvent(new Instruction(instruction.getPackage(), new NoID(instruction.getPackage()), "chesttake " + location + " " + items));
            } catch (ObjectNotFoundException e) {
                throw new InstructionParseException("Could not create inner chest take event: " + e.getMessage(),e );
            }
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onChestClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player))
            return;
        String playerID = PlayerConverter.getID((Player) event.getPlayer());
        if (!containsPlayer(playerID))
            return;
        try {
            Block block = loc.getLocation(playerID).getBlock();
            InventoryHolder chest;
            try {
                chest = (InventoryHolder) block.getState();
            } catch (ClassCastException e) {
                LogUtils.getLogger().log(Level.WARNING, "Could not cast the chest inventory: " + e.getMessage());
                LogUtils.logThrowable(e);
                return;
            }
            if (event.getInventory() == null || event.getInventory().getHolder() == null)
                return;
            if (!event.getInventory().getHolder().equals(chest))
                return;
            if (chestItemCondition.check(playerID) && checkConditions(playerID)) {
                completeObjective(playerID);
                if (chestTakeEvent != null)
                    chestTakeEvent.run(playerID);
            }
        } catch (QuestRuntimeException e) {
            LogUtils.getLogger().log(Level.WARNING, "Error while handling '" + instruction.getID() + "' objective: " + e.getMessage());
            LogUtils.logThrowable(e);
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
        return "";
    }

}
