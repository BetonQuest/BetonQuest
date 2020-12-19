package pl.betoncraft.betonquest.commands;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Backpack;
import pl.betoncraft.betonquest.Backpack.DisplayType;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * The /cancelquest command. It opens the list of quests.
 */
public class CancelQuestCommand implements CommandExecutor {

    /**
     * Registers a new executor of the /cancelquest command
     */
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public CancelQuestCommand() {
        BetonQuest.getInstance().getCommand("cancelquest").setExecutor(this);
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if ("cancelquest".equalsIgnoreCase(cmd.getName())) {
            if (sender instanceof Player) {
                final Player player = (Player) sender;
                final String playerID = PlayerConverter.getID(player);
                new Backpack(playerID, DisplayType.CANCEL);
            }
            return true;
        }
        return false;
    }
}
