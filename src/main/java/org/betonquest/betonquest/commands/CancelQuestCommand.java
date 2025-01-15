package org.betonquest.betonquest.commands;

import org.betonquest.betonquest.Backpack;
import org.betonquest.betonquest.Backpack.DisplayType;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.betonquest.betonquest.utils.PlayerConverter;
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
     * The {@link VariableProcessor} to use.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Creates a new executor for the /cancelquest command.
     *
     * @param variableProcessor the {@link VariableProcessor} to use
     */
    public CancelQuestCommand(final VariableProcessor variableProcessor) {
        this.variableProcessor = variableProcessor;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if ("cancelquest".equalsIgnoreCase(cmd.getName())) {
            if (sender instanceof Player) {
                new Backpack(variableProcessor, PlayerConverter.getID((Player) sender), DisplayType.CANCEL);
            }
            return true;
        }
        return false;
    }
}
