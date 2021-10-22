package org.betonquest.betonquest.commands;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Journal;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Changes the default language for the player
 */
@SuppressWarnings({"PMD.CommentRequired", "PMD.AvoidLiteralsInIfCondition"})
@CustomLog
public class LangCommand implements CommandExecutor, SimpleTabCompleter {

    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public LangCommand() {
        BetonQuest.getInstance().getCommand("questlang").setExecutor(this);
        BetonQuest.getInstance().getCommand("questlang").setTabCompleter(this);
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (!"questlang".equalsIgnoreCase(cmd.getName())) {
            return false;
        }
        if (args.length <= 0) {
            sender.sendMessage(Config.getMessage(Config.getLanguage(), "language_missing"));
            return true;
        }
        if (!Config.getLanguages().contains(args[0]) && !"default".equalsIgnoreCase(args[0])) {
            final StringBuilder builder = new StringBuilder();
            builder.append("default (").append(Config.getLanguage()).append("), ");
            for (final String lang : Config.getLanguages()) {
                builder.append(lang).append(", ");
            }
            if (builder.length() < 3) {
                LOG.warning("No translations loaded, somethings wrong!");
                return false;
            }
            final String finalMessage = builder.substring(0, builder.length() - 2) + ".";
            sender.sendMessage(Config.getMessage(Config.getLanguage(), "language_not_exist") + finalMessage);
            return true;
        }
        if (sender instanceof Player) {
            final String lang = args[0];
            final String playerID = PlayerConverter.getID((Player) sender);
            final PlayerData playerData = BetonQuest.getInstance().getPlayerData(playerID);
            final Journal journal = playerData.getJournal();
            int slot = -1;
            if (Journal.hasJournal(playerID)) {
                slot = journal.removeFromInv();
            }
            playerData.setLanguage(lang);
            if (slot > 0) {
                journal.addToInv(slot);
            }
            try {
                Config.sendNotify(null, playerID, "language_changed", new String[]{lang}, "language_changed,info");
            } catch (final QuestRuntimeException e) {
                LOG.warning("The notify system was unable to play a sound for the 'language_changed' category. Error was: '" + e.getMessage() + "'", e);
            }

        } else {
            BetonQuest.getInstance().getConfig().set("language", args[0]);
            sender.sendMessage(Config.getMessage(args[0], "default_language_changed"));
        }
        return true;
    }

    @Override
    public List<String> simpleTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        if (args.length == 1) {
            return Config.getLanguages();
        }
        return new ArrayList<>();
    }
}
