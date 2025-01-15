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
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The {@link VariableProcessor} to use.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Creates a new executor for the /backpack command.
     *
     * @param log               the logger that will be used for logging
     * @param variableProcessor the {@link VariableProcessor} to use
     */
    public BackpackCommand(final BetonQuestLogger log, final VariableProcessor variableProcessor) {
        this.log = log;
        this.variableProcessor = variableProcessor;
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
