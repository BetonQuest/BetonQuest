package pl.betoncraft.betonquest.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

/**
 * Licensed under GNU GENERAL PUBLIC LICENSE Version 3
 * https://gitlab.com/ungefroren/SchemGui/blob/master/LICENSE
 * <p>
 * Created by Jonas on 10.10.2017.
 */
public interface SimpleTabCompleter extends TabCompleter {


    @Override
    default List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completations = this.simpleTabComplete(sender, command, alias, args);
        if (completations == null) return null;
        List<String> out = new ArrayList<>();
        String lastArg = args[args.length - 1];
        for (String completation : completations) {
            if (lastArg == null || lastArg.matches(" *") || completation.toLowerCase().startsWith(lastArg.toLowerCase())) {
                out.add(completation);
            }
        }
        return out;
    }

    List<String> simpleTabComplete(CommandSender sender, Command command, String alias, String[] args);
}
