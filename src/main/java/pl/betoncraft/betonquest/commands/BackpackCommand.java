package pl.betoncraft.betonquest.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Backpack;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.logging.Level;

/**
 * The backpack command. It opens player's backpack.
 */
public class BackpackCommand implements CommandExecutor {

    /**
     * Registers a new executor of the /backpack command
     */
    public BackpackCommand() {
        BetonQuest.getInstance().getCommand("backpack").setExecutor(this);
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
        if ("backpack".equalsIgnoreCase(cmd.getName())) {
            // command sender must be a player, console can't have a backpack
            if (sender instanceof Player) {
                LogUtils.getLogger().log(Level.FINE, "Executing /backpack command for " + sender.getName());
                new Backpack(PlayerConverter.getID((Player) sender));
            }
            return true;
        }
        return false;
    }
}
