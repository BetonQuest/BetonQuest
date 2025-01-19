package org.betonquest.betonquest.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Interface which handles tab complete for commands.
 */
@SuppressWarnings("PMD.CommentRequired")
public interface SimpleTabCompleter extends TabCompleter {

    @Override
    @SuppressWarnings("PMD.ReturnEmptyCollectionRatherThanNull")
    @Nullable
    default List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        final Optional<List<String>> completions = this.simpleTabComplete(sender, command, alias, args);
        if (completions.isEmpty()) {
            return null;
        }
        final List<String> out = new ArrayList<>();
        final String lastArg = args[args.length - 1];
        for (final String completion : completions.get()) {
            if (lastArg == null || lastArg.matches(" *") || completion.toLowerCase(Locale.ROOT).startsWith(lastArg.toLowerCase(Locale.ROOT))) {
                out.add(completion);
            }
        }
        return out;
    }

    Optional<List<String>> simpleTabComplete(CommandSender sender, Command command, String alias, String... args);
}
