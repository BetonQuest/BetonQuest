package org.betonquest.betonquest.command;

import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.util.PlayerConverter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Gives the player his journal.
 */
public class JournalCommand implements CommandExecutor {

    /**
     * The command name.
     */
    private static final String JOURNAL = "journal";

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Create a new executor for the /journal command.
     *
     * @param dataStorage the storage providing player data
     */
    public JournalCommand(final PlayerDataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (JOURNAL.equalsIgnoreCase(cmd.getName())) {
            // command sender must be a player, console can't have journal
            if (sender instanceof Player) {
                // giving the player his journal
                dataStorage.get(PlayerConverter.getID((Player) sender)).getJournal().addToInv();
            }
            return true;
        }
        return false;
    }
}
