package org.betonquest.betonquest.command.quest;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.VariableReplacement;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.database.PlayerData;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Purges profile's data.
 */
public class PurgeSubCommand extends QuestCommandPart {

    /**
     * Registers a new executor and a new tab completer of the /betonquest command.
     *
     * @param log               the logger to use
     * @param constructorParams the constructor parameters
     */
    public PurgeSubCommand(final BetonQuestLogger log, final ConstructorParams constructorParams) {
        super(log, constructorParams, List.of("purge"), "<player>");
    }

    @Override
    public void handle(final CommandSender sender, final String... args) throws QuestException {
        final PlayerData playerData = getTargetPlayerData(sender, args);
        if (playerData == null) {
            return;
        }
        log.debug("Purging player " + args[1]);
        playerData.purgePlayer();
        sendMessage(sender, "purged", new VariableReplacement("player", Component.text(args[1])));
    }

    @Override
    public Optional<List<String>> complete(final String... args) {
        return args.length == 2 ? Optional.empty() : Optional.of(new ArrayList<>());
    }
}
