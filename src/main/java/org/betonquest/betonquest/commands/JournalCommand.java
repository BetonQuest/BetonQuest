package org.betonquest.betonquest.commands;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Gives the player his journal.
 */
@SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
public class JournalCommand implements CommandExecutor {
    /**
     * Object to get player data.
     */
    private final BetonQuest betonQuest;

    /**
     * Create a new executor for the /journal command.
     *
     * @param betonQuest the object to get player data from
     */
    public JournalCommand(final BetonQuest betonQuest) {
        this.betonQuest = betonQuest;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if ("journal".equalsIgnoreCase(cmd.getName())) {
            // command sender must be a player, console can't have journal
            if (sender instanceof Player) {
                // giving the player his journal
                betonQuest.getPlayerData(PlayerConverter.getID((Player) sender)).getJournal().addToInv();
            }
            return true;
        }
        return false;
    }
}
