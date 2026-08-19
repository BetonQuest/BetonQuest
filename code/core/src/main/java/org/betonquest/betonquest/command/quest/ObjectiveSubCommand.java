package org.betonquest.betonquest.command.quest;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.VariableReplacement;
import org.betonquest.betonquest.api.identifier.ObjectiveIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.service.objective.ObjectiveManager;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.PlayerData;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Lists, adds or removes objectives.
 */
@SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
public class ObjectiveSubCommand extends QuestCommandPart {

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage playerDataStorage;

    /**
     * The objective manager.
     */
    private final ObjectiveManager objectiveManager;

    /**
     * Registers a new executor and a new tab completer of the /betonquest command.
     *
     * @param log               the logger to use
     * @param constructorParams the constructor parameters
     */
    public ObjectiveSubCommand(final BetonQuestLogger log, final ConstructorParams constructorParams) {
        super(log, constructorParams, List.of("objective", "objectives", "o"), "<player> [list/add/del] [objective]");
        this.playerDataStorage = constructorParams.playerDataStorage();
        this.objectiveManager = constructorParams.objectiveManager();
    }

    @Override
    public void handle(final CommandSender sender, final String... args) {
        final Profile profile = getTargetProfile(sender, args);
        if (profile == null) {
            return;
        }
        final boolean isOnline = profile.getOnlineProfile().isPresent();
        if (!isOnline) {
            log.debug("Profile is offline, loading his data");
        }
        final PlayerData playerData = playerDataStorage.get(profile);
        // if there are no arguments then list player's objectives
        if (args.length < 3 || "list".equalsIgnoreCase(args[2]) || "l".equalsIgnoreCase(args[2])) {
            // display objectives
            log.debug("Listing objectives");
            final Predicate<String> shouldDisplay = createListFilter(args, 3, Function.identity());
            final Stream<String> objectives;
            if (isOnline) {
                // if the player is online then just retrieve tags from his active objectives
                objectives = objectiveManager.getForProfile(profile).stream()
                        .map(defaultObjective -> defaultObjective.getObjectiveID().getFull());
            } else {
                // if player is offline then convert his raw objective strings to tags
                objectives = playerData.getRawObjectives().keySet().stream();
            }
            sendMessage(sender, "player_objectives");
            objectives.filter(shouldDisplay)
                    .sorted()
                    .forEach(objective -> sender.sendMessage("§b- " + objective));
            return;
        }
        // if there is not enough arguments, display warning
        if (args.length < 4) {
            log.debug("Missing objective instruction string");
            sendMessage(sender, "specify_objective");
            return;
        }
        handle2(sender, args, profile, isOnline, playerData);
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    private void handle2(final CommandSender sender, final String[] args, final Profile profile, final boolean isOnline,
                         final PlayerData playerData) {
        // get the objective
        final ObjectiveIdentifier objectiveID;
        final Objective objective;
        try {
            objectiveID = getIdentifier(ObjectiveIdentifier.class, args[3]);
            objective = objectiveManager.getObjective(objectiveID);
        } catch (final QuestException e) {
            sendMessage(sender, "error",
                    new VariableReplacement("error", Component.text(e.getMessage())));
            log.warn("Could not find objective: " + e.getMessage(), e);
            return;
        }
        switch (args[2].toLowerCase(Locale.ROOT)) {
            case "start", "s", "add", "a" -> {
                log.debug("Adding new objective " + objectiveID + " for " + profile);
                if (isOnline) {
                    objectiveManager.start(profile, objectiveID);
                } else {
                    playerData.addNewRawObjective(objectiveID);
                }
                sendMessage(sender, "objective_added");
            }
            case "remove", "delete", "del", "r", "d" -> {
                log.debug("Deleting objective " + objectiveID + " for " + profile);
                objectiveManager.cancel(profile, objectiveID);
                playerData.removeRawObjective(objectiveID);
                sendMessage(sender, "objective_removed");
            }
            case "complete", "c" -> {
                log.debug("Completing objective " + objectiveID + " for " + profile);
                if (isOnline) {
                    objective.getService().complete(profile);
                } else {
                    playerData.removeRawObjective(objectiveID);
                }
                sendMessage(sender, "objective_completed");
            }
            default -> {
                log.debug("The argument was unknown");
                sendMessage(sender, "unknown_argument");
            }
        }
    }

    @Override
    public Optional<List<String>> complete(final String... args) {
        if (args.length == 2) {
            return Optional.empty();
        }
        if (args.length == 3) {
            return Optional.of(Arrays.asList("list", "add", "del", "complete"));
        }
        if (args.length == 4) {
            return completeId(args, AccessorType.OBJECTIVES);
        }
        return Optional.of(new ArrayList<>());
    }
}
