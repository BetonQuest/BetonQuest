package org.betonquest.betonquest.commands;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.CustomLog;
import org.betonquest.betonquest.Backpack;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * The backpack command. It opens player's backpack.
 */
@CustomLog
public class BackpackCommand implements CommandExecutor {

    /**
     * Registers a new executor of the /backpack command
     */
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public BackpackCommand() {
        BetonQuest.getInstance().getCommand("backpack").setExecutor(this);
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if ("backpack".equalsIgnoreCase(cmd.getName())) {
            // command sender must be a player, console can't have a backpack
            if (sender instanceof Player) {
                LOG.debug(null, "Executing /backpack command for " + sender.getName());
                new Backpack(PlayerConverter.getID((Player) sender));
            }
            return true;
        }
        return false;
    }
}
