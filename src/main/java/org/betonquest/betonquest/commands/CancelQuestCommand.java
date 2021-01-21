package org.betonquest.betonquest.commands;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.Backpack;
import org.betonquest.betonquest.Backpack.DisplayType;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * The /cancelquest command. It opens the list of quests.
 */
public class CancelQuestCommand implements CommandExecutor {

    /**
     * Registers a new executor of the /cancelquest command
     */
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public CancelQuestCommand() {
        BetonQuest.getInstance().getCommand("cancelquest").setExecutor(this);
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if ("cancelquest".equalsIgnoreCase(cmd.getName())) {
            if (sender instanceof Player) {
                final Player player = (Player) sender;
                final String playerID = PlayerConverter.getID(player);
                new Backpack(playerID, DisplayType.CANCEL);
            }
            return true;
        }
        return false;
    }
}
