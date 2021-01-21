package org.betonquest.betonquest.commands;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Gives the player his journal
 */
public class JournalCommand implements CommandExecutor {

    /**
     * Registers a new executor of the /journal command
     */
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public JournalCommand() {
        BetonQuest.getInstance().getCommand("journal").setExecutor(this);
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if ("journal".equalsIgnoreCase(cmd.getName())) {
            // command sender must be a player, console can't have journal
            if (sender instanceof Player) {
                // giving the player his journal
                BetonQuest.getInstance().getPlayerData(PlayerConverter.getID((Player) sender)).getJournal()
                        .addToInv(Integer.parseInt(Config.getString("config.default_journal_slot")));
            }
            return true;
        }
        return false;
    }

}
