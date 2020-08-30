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
import org.bukkit.Location;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.item.QuestItem;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.HashMap;

/**
 * Requires the player to manually brew a potion.
 *
 * @author Jakub Sapalski
 */
public class BrewObjective extends Objective implements Listener {

    private final QuestItem potion;
    private final int amount;
    private final boolean notify;
    private final int notifyInterval;
    private final HashMap<Location, String> locations = new HashMap<>();

    public BrewObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = PotionData.class;
        potion = instruction.getQuestItem();
        amount = instruction.getInt();
        notifyInterval = instruction.getInt(instruction.getOptional("notify"), 1);
        notify = instruction.hasArgument("notify") || notifyInterval > 0;
    }

    @EventHandler(ignoreCancelled = true)
    public void onIngredientPut(final InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.BREWING) {
            return;
        }
        if (event.getRawSlot() == 3 || event.getClick().equals(ClickType.SHIFT_LEFT)) {
            final String playerID = PlayerConverter.getID((Player) event.getWhoClicked());
            if (!containsPlayer(playerID)) {
                return;
            }
            locations.put(((BrewingStand) event.getInventory().getHolder()).getLocation(), playerID);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBrew(final BrewEvent event) {
        final String playerID = locations.remove(event.getBlock().getLocation());
        if (playerID == null) {
            return;
        }
        final PotionData data = (PotionData) dataMap.get(playerID);
        // this tracks how many potions there are in the stand before brewing
        int alreadyExistingTemp = 0;
        for (int i = 0; i < 3; i++) {
            if (checkPotion(event.getContents().getItem(i))) {
                alreadyExistingTemp++;
            }
        }
        // making it final for the runnable
        final int alreadyExisting = alreadyExistingTemp;
        new BukkitRunnable() {
            @Override
            public void run() {
                // unfinaling it for modifications
                boolean brewed = false;
                int alreadyExistingFinal = alreadyExisting;
                for (int i = 0; i < 3; i++) {
                    // if there were any potions before, don't count them to
                    // prevent cheating
                    if (checkPotion(event.getContents().getItem(i))) {
                        if (alreadyExistingFinal <= 0 && checkConditions(playerID)) {
                            data.brew();
                        }
                        alreadyExistingFinal--;
                        brewed = true;
                    }
                }
                // check if the objective has been completed
                if (data.getAmount() >= amount) {
                    completeObjective(playerID);
                } else if (notify && data.getAmount() % notifyInterval == 0) {
                    Config.sendNotify(playerID, "potions_to_brew",
                            new String[]{String.valueOf(amount - data.getAmount())},
                            "potions_to_brew,info");
                }
            }
        }.runTask(BetonQuest.getInstance());
    }

    /**
     * Checks if this ItemStack matches a potion defined in "effects" HashMap.
     */
    private boolean checkPotion(final ItemStack item) {
        if (item == null) {
            return false;
        }
        return potion.compare(item);
    }

    @Override
    public String getProperty(final String name, final String playerID) {
        if (name.equalsIgnoreCase("left")) {
            return Integer.toString(amount - ((PotionData) dataMap.get(playerID)).getAmount());
        } else if (name.equalsIgnoreCase("amount")) {
            return Integer.toString(((PotionData) dataMap.get(playerID)).getAmount());
        }
        return "";
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        locations.clear();
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getDefaultDataInstruction() {
        return "0";
    }

    public static class PotionData extends ObjectiveData {

        private int amount;

        public PotionData(final String instruction, final String playerID, final String objID) {
            super(instruction, playerID, objID);
            amount = Integer.parseInt(instruction);
        }

        public void brew() {
            amount++;
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
