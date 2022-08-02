package org.betonquest.betonquest.commands;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Gives the player his journal
 */
@CustomLog(topic = "JournalCommand")
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
                try {
                    BetonQuest.getInstance().getPlayerData(PlayerConverter.getID((Player) sender)).getJournal().addToInv();
                } catch (final QuestRuntimeException e) {
                    LOG.warn("Couldn't create new Backpack due to: " + e.getMessage(), e);
                }
            }
            return true;
        }
        return false;
    }

}
