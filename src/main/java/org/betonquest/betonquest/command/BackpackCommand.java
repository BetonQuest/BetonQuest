package org.betonquest.betonquest.command;

import org.betonquest.betonquest.Backpack;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.util.PlayerConverter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * The backpack command. It opens profile's backpack.
 */
@SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
public class BackpackCommand implements CommandExecutor {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Creates a new executor for the /backpack command.
     *
     * @param log the logger that will be used for logging
     */
    public BackpackCommand(final BetonQuestLogger log) {
        this.log = log;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if ("backpack".equalsIgnoreCase(cmd.getName())) {
            // command sender must be a player, console can't have a backpack
            if (sender instanceof Player) {
                final OnlineProfile onlineProfile = PlayerConverter.getID((Player) sender);
                log.debug("Executing /backpack command for " + onlineProfile);
                new Backpack(onlineProfile);
            }
            return true;
        }
        return false;
    }
}
