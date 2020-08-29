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
package pl.betoncraft.betonquest.compatibility.skript;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Fires the custom event for Skript to listen to
 *
 * @author Jakub Sapalski
 */
public class BQEventSkript extends QuestEvent {

    private final String identifier;

    public BQEventSkript(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        identifier = instruction.next();
    }

    @Override
    protected Void execute(final String playerID) {
        final Player player = PlayerConverter.getPlayer(playerID);
        final CustomEventForSkript event = new CustomEventForSkript(player, identifier);
        Bukkit.getServer().getPluginManager().callEvent(event);
        return null;
    }

    /**
     * Custom event, which runs for Skript to listen.
     *
     * @author Jakub Sapalski
     */
    public static class CustomEventForSkript extends PlayerEvent {

        private static final HandlerList HANDLERS = new HandlerList();
        private final String identifier;

        public CustomEventForSkript(final Player who, final String identifier) {
            super(who);
            this.identifier = identifier;
        }

        public static HandlerList getHandlerList() {
            return HANDLERS;
        }

        public String getID() {
            return identifier;
        }

        public HandlerList getHandlers() {
            return HANDLERS;
        }

    }
}
