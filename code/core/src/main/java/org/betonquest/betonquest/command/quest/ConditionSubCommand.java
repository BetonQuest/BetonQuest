package org.betonquest.betonquest.command.quest;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.VariableReplacement;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Checks if specified player meets condition described by ID.
 */
@SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
public class ConditionSubCommand extends QuestCommandPart {

    /**
     * The condition manager.
     */
    private final ConditionManager conditionManager;

    /**
     * Registers a new executor and a new tab completer of the /betonquest command.
     *
     * @param log               the logger to use
     * @param constructorParams the constructor parameters
     */
    public ConditionSubCommand(final BetonQuestLogger log, final ConstructorParams constructorParams) {
        super(log, constructorParams);
        this.conditionManager = constructorParams.conditionManager();
    }

    @Override
    public List<String> names() {
        return List.of("condition", "conditions", "c");
    }

    @Override
    public void handle(final CommandSender sender, final String... args) throws QuestException {
        // the player has to be specified every time
        if (args.length < 2 || Bukkit.getPlayer(args[1]) == null && !"-".equals(args[1])) {
            log.debug("Player's name is missing or he's offline");
            sendMessage(sender, "specify_player");
            return;
        }
        // the condition ID
        if (args.length < 3) {
            log.debug("Condition's ID is missing");
            sendMessage(sender, "specify_condition");
            return;
        }
        final ConditionIdentifier conditionID;
        try {
            conditionID = getIdentifier(ConditionIdentifier.class, args[2]);
        } catch (final QuestException e) {
            sendMessage(sender, "error",
                    new VariableReplacement("error", Component.text(e.getMessage())));
            log.warn("Could not find condition: " + e.getMessage(), e);
            return;
        }
        // display message about condition
        final Profile profile = "-".equals(args[1]) ? null : profileProvider.getProfile(Bukkit.getOfflinePlayer(args[1]));
        sendMessage(sender, "player_condition",
                new VariableReplacement("condition", Component.text((conditionID.isInverted() ? "! " : "") + conditionID.readRawInstruction())),
                new VariableReplacement("result", Component.text(conditionManager.test(profile, conditionID))));
    }

    @Override
    public Optional<List<String>> complete(final String... args) {
        if (args.length == 2) {
            return Optional.empty();
        }
        if (args.length == 3) {
            return completeId(args, AccessorType.CONDITIONS);
        }
        return Optional.of(new ArrayList<>());
    }
}
