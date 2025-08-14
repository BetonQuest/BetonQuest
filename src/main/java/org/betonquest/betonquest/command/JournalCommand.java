package org.betonquest.betonquest.command;

import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
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
     * Plugin Message instance to create the journal.
     */
    private final PluginMessage pluginMessage;

    /**
     * The text parser used to parse text.
     */
    private final TextParser textParser;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * Create a new executor for the /journal command.
     *
     * @param dataStorage     the storage providing player data
     * @param pluginMessage   the plugin message to create the journal
     * @param textParser      the text parser used to parse text
     * @param profileProvider the profile provider instance
     */
    public JournalCommand(final PlayerDataStorage dataStorage, final PluginMessage pluginMessage,
                          final TextParser textParser, final ProfileProvider profileProvider) {
        this.dataStorage = dataStorage;
        this.pluginMessage = pluginMessage;
        this.textParser = textParser;
        this.profileProvider = profileProvider;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if (JOURNAL.equalsIgnoreCase(cmd.getName())) {
            if (sender instanceof Player) {
                dataStorage.get(profileProvider.getProfile((Player) sender)).getJournal(pluginMessage, textParser).addToInv();
            }
            return true;
        }
        return false;
    }
}
