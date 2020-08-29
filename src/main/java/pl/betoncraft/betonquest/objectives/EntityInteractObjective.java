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
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.metadata.MetadataValue;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LocationData;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Player has to interact with specified amount of specified mobs. It can also
 * require the player to interact with specifically named mobs and notify them
 * about the required amount. It can be specified if the player has to
 * rightclick or damage the entity. Each entity can only be interacted once.
 * The interaction can optionally be canceled by adding the argument cancel.
 *
 * @author Jonas Blocher
 */
public class EntityInteractObjective extends Objective {

    private final int notifyInterval;
    protected EntityType mobType;
    protected int amount;
    protected String name;
    protected String marked;
    protected boolean notify;
    protected Interaction interaction;
    protected boolean cancel;
    private LocationData loc;
    private VariableNumber range;

    private RightClickListener rightClickListener;
    private LeftClickListener leftClickListener;

    public EntityInteractObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = EntityInteractData.class;
        interaction = instruction.getEnum(Interaction.class);
        mobType = instruction.getEnum(EntityType.class);
        amount = instruction.getPositive();
        name = instruction.getOptional("name");
        if (name != null) {
            name = name.replace('_', ' ');
        }
        marked = instruction.getOptional("marked");
        if (marked != null) {
            marked = Utils.addPackage(instruction.getPackage(), marked);
        }
        notifyInterval = instruction.getInt(instruction.getOptional("notify"), 1);
        notify = instruction.hasArgument("notify") || notifyInterval > 0;
        cancel = instruction.hasArgument("cancel");
        loc = instruction.getLocation(instruction.getOptional("loc"));
        final String stringRange = instruction.getOptional("range");
        range = instruction.getVarNum(stringRange == null ? "1" : stringRange);
    }

    @Override
    public void start() {
        switch (interaction) {
            case RIGHT:
                rightClickListener = new RightClickListener();
                break;
            case LEFT:
                leftClickListener = new LeftClickListener();
                break;
            case ANY:
                rightClickListener = new RightClickListener();
                leftClickListener = new LeftClickListener();
                break;
        }
    }

    private boolean onInteract(final Player player, final Entity entity) {
        // check if it's the right entity type
        if (!entity.getType().equals(mobType)) {
            return false;
        }
        // if the entity should have a name and it does not match, return
        if (name != null && (entity.getCustomName() == null || !entity.getCustomName().equals(name))) {
            return false;
        }
        // check if the entity is correctly marked
        if (marked != null) {
            if (!entity.hasMetadata("betonquest-marked")) {
                return false;
            }
            final List<MetadataValue> meta = entity.getMetadata("betonquest-marked");
            for (final MetadataValue m : meta) {
                if (!m.asString().equals(marked)) {
                    return false;
                }
            }
        }
        // check if the player has this objective
        final String playerID = PlayerConverter.getID(player);
        if (containsPlayer(playerID) && checkConditions(playerID)) {
            // Check location matches
            if (loc != null) {
                try {
                    final Location location = loc.getLocation(playerID);
                    final double pRange = range.getDouble(playerID);
                    if (!entity.getWorld().equals(location.getWorld())
                            || entity.getLocation().distance(location) > pRange) {
                        return false;
                    }
                } catch (QuestRuntimeException e) {
                    LogUtils.getLogger().log(Level.WARNING, "Error while handling '" + instruction.getID() + "' objective: " + e.getMessage());
                    LogUtils.logThrowable(e);
                }
            }


            // get data off the player
            final EntityInteractData playerData = (EntityInteractData) dataMap.get(playerID);
            // check if player already interacted with entity
            if (playerData.containsEntity(entity)) {
                return false;
            }
            // right mob is interacted with, handle data update
            playerData.subtract();
            playerData.addEntity(entity);
            if (playerData.isZero()) {
                completeObjective(playerID);
            } else if (notify && playerData.getAmount() % notifyInterval == 0) {
                // send a notification
                Config.sendNotify(playerID, "mobs_to_click", new String[]{String.valueOf(playerData.getAmount())},
                        "mobs_to_click,info");
            }
            return true;
        }
        return false;
    }

    @Override
    public void stop() {
        if (rightClickListener != null) {
            HandlerList.unregisterAll(rightClickListener);
        }
        if (leftClickListener != null) {
            HandlerList.unregisterAll(leftClickListener);
        }
    }

    @Override
    public String getDefaultDataInstruction() {
        return Integer.toString(amount);
    }

    @Override
    public String getProperty(final String name, final String playerID) {
        if (name.equalsIgnoreCase("left")) {
            return Integer.toString(((EntityInteractData) dataMap.get(playerID)).getAmount());
        } else if (name.equalsIgnoreCase("amount")) {
            return Integer.toString(amount - ((EntityInteractData) dataMap.get(playerID)).getAmount());
        }
        return "";
    }

    public enum Interaction {
        RIGHT, LEFT, ANY
    }

    public static class EntityInteractData extends ObjectiveData {

        private int amount;
        private Set<UUID> entitys;

        public EntityInteractData(final String instruction, final String playerID, final String objID) {
            super(instruction, playerID, objID);
            final String[] args = instruction.split(" ");
            amount = Integer.parseInt(args[0].trim());
            entitys = new HashSet<>();
            for (int i = 1; i < args.length; i++) {
                entitys.add(UUID.fromString(args[i]));
            }
        }

        public void addEntity(final Entity entity) {
            entitys.add(entity.getUniqueId());
            update();
        }

        public boolean containsEntity(final Entity entity) {
            return entitys.contains(entity.getUniqueId());
        }

        public int getAmount() {
            return amount;
        }

        public void subtract() {
            amount--;
            update();
        }

        public boolean isZero() {
            return amount <= 0;
        }

        @Override
        public String toString() {
            String string = Integer.toString(amount);
            for (final UUID uuid : entitys) {
                string += " " + uuid.toString();
            }
            return string;
        }

    }

    private class LeftClickListener implements Listener {
        public LeftClickListener() {
            Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
        }

        @EventHandler(ignoreCancelled = true)
        public void onDamage(final EntityDamageByEntityEvent event) {
            final Player player;
            // check if entity is damaged by a Player
            if (event.getDamager() instanceof Player) {
                player = (Player) event.getDamager();
            } else {
                return;
            }
            final boolean succes = onInteract(player, event.getEntity());
            if (succes && cancel) {
                event.setCancelled(true);
            }
        }
    }

    private class RightClickListener implements Listener {
        public RightClickListener() {
            Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
        }

        @EventHandler(ignoreCancelled = true)
        public void onRightClick(final PlayerInteractEntityEvent event) {
            final boolean success = onInteract(event.getPlayer(), event.getRightClicked());
            if (success && cancel) {
                event.setCancelled(true);
            }
        }
    }
}
