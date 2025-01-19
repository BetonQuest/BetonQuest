package org.betonquest.betonquest.command;

import org.betonquest.betonquest.Backpack;
import org.betonquest.betonquest.Backpack.DisplayType;
import org.betonquest.betonquest.util.PlayerConverter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * The /cancelquest command. It opens the list of quests.
 */
@SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
public class CancelQuestCommand implements CommandExecutor {

    /**
     * Creates a new executor for the /cancelquest command.
     */
    public CancelQuestCommand() {
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if ("cancelquest".equalsIgnoreCase(cmd.getName())) {
            if (sender instanceof Player) {
                new Backpack(PlayerConverter.getID((Player) sender), DisplayType.CANCEL);
            }
            return true;
        }
        return false;
    }
}
