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
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Player needs to get specified experience level
 *
 * @author Jakub Sapalski
 */
public class ExperienceObjective extends Objective implements Listener {

    private final int amount;
    private final boolean checkForLevel;

    public ExperienceObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = ObjectiveData.class;
        this.amount = instruction.getInt();
        if (amount < 1) {
            throw new InstructionParseException("Amount cannot be less than 1");
        }
        this.checkForLevel = instruction.hasArgument("level") || instruction.hasArgument("l");
    }

    @EventHandler(ignoreCancelled = true)
    public void onLevelChangeEvent(final PlayerLevelChangeEvent event) {
        if(!checkForLevel) {
            return;
        }
        final String playerID = PlayerConverter.getID(event.getPlayer());
        onExperienceChange(playerID, event.getNewLevel());
    }

    @EventHandler(ignoreCancelled = true)
    public void onExpChangeEvent(final PlayerExpChangeEvent event) {
        if(checkForLevel) {
            return;
        }
        final String playerID = PlayerConverter.getID(event.getPlayer());
        onExperienceChange(playerID, event.getPlayer().getTotalExperience() + event.getAmount());
    }

    private void onExperienceChange(final String playerID, final int newAmount) {
        if (!containsPlayer(playerID)) {
            return;
        }
        if (newAmount >= amount && checkConditions(playerID)) {
            completeObjective(playerID);
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
