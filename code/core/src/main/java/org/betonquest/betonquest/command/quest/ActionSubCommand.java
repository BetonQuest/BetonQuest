package org.betonquest.betonquest.command.quest;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.VariableReplacement;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.service.action.ActionManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Fires an action for an online player. It cannot work for offline players!
 */
public class ActionSubCommand extends QuestCommandPart {

    /**
     * The action manager.
     */
    private final ActionManager actionManager;

    /**
     * Registers a new executor and a new tab completer of the /betonquest command.
     *
     * @param log               the logger to use
     * @param constructorParams the constructor parameters
     */
    public ActionSubCommand(final BetonQuestLogger log, final ConstructorParams constructorParams) {
        super(log, constructorParams);
        this.actionManager = constructorParams.actionManager();
    }

    @Override
    public List<String> names() {
        return List.of("action", "actions", "a");
    }

    @Override
    public void handle(final CommandSender sender, final String... args) throws QuestException {
        if (args.length < 2 || Bukkit.getPlayer(args[1]) == null && !"-".equals(args[1])) {
            log.debug("Player's name is missing or he's offline");
            sendMessage(sender, "specify_player");
            return;
        }
        if (args.length < 3) {
            log.debug("Actions's ID is missing");
            sendMessage(sender, "specify_action");
            return;
        }
        final ActionIdentifier actionID;
        try {
            actionID = getIdentifier(ActionIdentifier.class, args[2]);
        } catch (final QuestException e) {
            sendMessage(sender, "error",
                    new VariableReplacement("error", Component.text(e.getMessage())));
            log.warn("Could not find action: " + e.getMessage(), e);
            return;
        }
        final Profile profile = "-".equals(args[1]) ? null : profileProvider.getProfile(Bukkit.getOfflinePlayer(args[1]));
        actionManager.run(profile, actionID);
        sendMessage(sender, "player_action",
                new VariableReplacement("action", Component.text(actionID.readRawInstruction())));
    }

    @Override
    public Optional<List<String>> complete(final String... args) {
        if (args.length == 2) {
            return Optional.empty();
        }
        if (args.length == 3) {
            return completeId(args, AccessorType.ACTIONS);
        }
        return Optional.of(new ArrayList<>());
    }
}
