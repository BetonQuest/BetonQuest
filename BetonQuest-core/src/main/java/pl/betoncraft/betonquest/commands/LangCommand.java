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
import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Single;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Journal;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.database.PlayerData;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

/**
 * Changes the default language for the player
 *
 * @author Jakub Sapalski
 */
@CommandAlias("questlang")
public class LangCommand extends BaseCommand {
    public static void registerTabCompletions(BukkitCommandManager manager) {
        manager.getCommandCompletions().registerCompletion("languages", c -> Config.getLanguages());
    }

    @Default
    public CompletableFuture<Void> onCommand(Player player, @Single String lang) {
        return CompletableFuture.completedFuture(null);
        // TODO: Better language implementation. Maybe try to use the language system from ACF
    }
/*
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("questlang")) {
            if (args.length < 1) {
                sender.sendMessage(Config.getMessage(Config.getLanguage(), "language_missing"));
                return true;
            }
            if (!Config.getLanguages().contains(args[0]) && !args[0].equalsIgnoreCase("default")) {
                StringBuilder builder = new StringBuilder();
                builder.append("default (" + Config.getLanguage() + "), ");
                for (String lang : Config.getLanguages()) {
                    builder.append(lang + ", ");
                }
                if (builder.length() < 3) {
                    LogUtils.getLogger().log(Level.WARNING, "No translations loaded, somethings wrong!");
                    return false;
                }
                String finalMessage = builder.substring(0, builder.length() - 2) + ".";
                sender.sendMessage(Config.getMessage(Config.getLanguage(), "language_not_exist") + finalMessage);
                return true;
            }
            if (sender instanceof Player) {
                String lang = args[0];
                String playerID = PlayerConverter.getID((Player) sender);
                PlayerData playerData = BetonQuest.getInstance().getPlayerData(playerID);
                Journal journal = playerData.getJournal();
                int slot = -1;
                if (Journal.hasJournal(playerID)) {
                    slot = journal.removeFromInv();
                }
                playerData.setLanguage(lang);
                journal.generateTexts(lang);
                if (slot > 0)
                    journal.addToInv(slot);
                Config.sendNotify(playerID, "language_changed", new String[]{lang}, "language_changed,info");
            } else {
                BetonQuest.getInstance().getConfig().set("language", args[0]);
                sender.sendMessage(Config.getMessage(args[0], "default_language_changed"));
            }
            return true;
        }
        return false;
    }*/
}
