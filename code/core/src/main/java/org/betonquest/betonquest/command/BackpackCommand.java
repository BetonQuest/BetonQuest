package org.betonquest.betonquest.command;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.feature.BackpackFactory;
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
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Factory to create backpacks.
     */
    private final BackpackFactory backpackFactory;

    /**
     * Creates a new executor for the /backpack command.
     *
     * @param log             the logger that will be used for logging
     * @param profileProvider the profile provider instance
     * @param backpackFactory the factory to create backpacks
     */
    public BackpackCommand(final BetonQuestLogger log, final ProfileProvider profileProvider, final BackpackFactory backpackFactory) {
        this.log = log;
        this.profileProvider = profileProvider;
        this.backpackFactory = backpackFactory;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if ("backpack".equalsIgnoreCase(cmd.getName())) {
            // command sender must be a player, console can't have a backpack
            if (sender instanceof Player) {
                final OnlineProfile onlineProfile = profileProvider.getProfile((Player) sender);
                log.debug("Executing /backpack command for %s.".formatted(onlineProfile));
                backpackFactory.createBackpack(onlineProfile);
            }
            return true;
        }
        return false;
    }
}
