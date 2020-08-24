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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LocationData;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.logging.Level;

/**
 * Requires the player to shoot a target with a bow
 *
 * @author Jakub Sapalski
 */
public class ArrowShootObjective extends Objective implements Listener {

    private final LocationData loc;
    private final VariableNumber range;

    public ArrowShootObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = ObjectiveData.class;
        loc = instruction.getLocation();
        range = instruction.getVarNum();
    }

    @EventHandler(ignoreCancelled = true)
    public void onArrowHit(final ProjectileHitEvent event) {
        // check if it's the arrow shot by the player with active objectve
        final Projectile arrow = event.getEntity();
        if (arrow.getType() != EntityType.ARROW) {
            return;
        }
        if (!(arrow.getShooter() instanceof Player)) {
            return;
        }
        final Player player = (Player) arrow.getShooter();
        final String playerID = PlayerConverter.getID(player);
        if (!containsPlayer(playerID)) {
            return;
        }
        try {
            final Location location = loc.getLocation(playerID);
            // check if the arrow is in the right place in the next tick
            // wait one tick, let the arrow land completely
            new BukkitRunnable() {
                @Override
                public void run() {
                    final Location arrowLocation = arrow.getLocation();
                    if (arrowLocation == null) {
                        return;
                    }
                    try {
                        final double pRange = range.getDouble(playerID);
                        if (arrowLocation.getWorld().equals(location.getWorld())
                                && arrowLocation.distanceSquared(location) < pRange * pRange
                                && checkConditions(playerID)) {
                            completeObjective(playerID);
                        }
                    } catch (QuestRuntimeException e) {
                        LogUtils.getLogger().log(Level.WARNING, "Could not resolve range variable: " + e.getMessage());
                        LogUtils.logThrowable(e);
                    }
                }
            }.runTask(BetonQuest.getInstance());
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

    @Override
    public String getProperty(final String name, final String playerID) {
        return "";
    }

}
