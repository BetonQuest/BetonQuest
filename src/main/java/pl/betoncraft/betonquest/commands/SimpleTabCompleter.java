package pl.betoncraft.betonquest.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

/**
 * Interface which handles tab complete for commands.
 */
public interface SimpleTabCompleter extends TabCompleter {


    @Override
    default List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        final List<String> completations = this.simpleTabComplete(sender, command, alias, args);
        if (completations == null) {
            return null;
        }
        final List<String> out = new ArrayList<>();
        final String lastArg = args[args.length - 1];
        for (final String completation : completations) {
            if (lastArg == null || lastArg.matches(" *") || completation.toLowerCase().startsWith(lastArg.toLowerCase())) {
                out.add(completation);
            }
        }
        return out;
    }

    List<String> simpleTabComplete(CommandSender sender, Command command, String alias, String[] args);
}
