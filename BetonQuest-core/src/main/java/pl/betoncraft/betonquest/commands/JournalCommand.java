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
package pl.betoncraft.betonquest.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.concurrent.CompletableFuture;

/**
 * Gives the player his journal
 *
 * @author Jakub Sapalski
 */
@CommandAlias("journal")
public class JournalCommand extends BaseCommand {
    @Dependency
    BetonQuest plugin;

    @Default
    public CompletableFuture<Void> onCommand(Player player) {
        // giving the player his journal
        return plugin.getPlayerData(PlayerConverter.getID(player)).thenAccept(data -> data.getJournal()
                .addToInv(Integer.parseInt(Config.getString("config.default_journal_slot"))));

    }

}
