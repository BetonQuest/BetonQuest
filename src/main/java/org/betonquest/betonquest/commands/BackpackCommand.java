package org.betonquest.betonquest.commands;

import org.betonquest.betonquest.Backpack;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.betonquest.betonquest.utils.PlayerConverter;
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
     * The variable processor that the command should use for creating variables.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Creates a new executor for the /backpack command.
     *
     * @param variableProcessor the processor that will be used for creating variables
     * @param log               the logger that will be used for logging
     */
    public BackpackCommand(final VariableProcessor variableProcessor, final BetonQuestLogger log) {
        this.variableProcessor = variableProcessor;
        this.log = log;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if ("backpack".equalsIgnoreCase(cmd.getName())) {
            // command sender must be a player, console can't have a backpack
            if (sender instanceof Player) {
                final OnlineProfile onlineProfile = PlayerConverter.getID((Player) sender);
                log.debug("Executing /backpack command for " + onlineProfile);
                new Backpack(variableProcessor, onlineProfile);
            }
            return true;
        }
        return false;
    }
}
