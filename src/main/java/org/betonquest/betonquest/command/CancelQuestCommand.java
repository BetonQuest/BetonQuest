package org.betonquest.betonquest.command;

import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.feature.Backpack;
import org.betonquest.betonquest.feature.Backpack.DisplayType;
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
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * Creates a new executor for the /cancelquest command.
     *
     * @param pluginMessage the {@link PluginMessage} instance
     */
    public CancelQuestCommand(final PluginMessage pluginMessage) {
        this.pluginMessage = pluginMessage;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if ("cancelquest".equalsIgnoreCase(cmd.getName())) {
            if (sender instanceof Player) {
                new Backpack(pluginMessage, PlayerConverter.getID((Player) sender), DisplayType.CANCEL);
            }
            return true;
        }
        return false;
    }
}
