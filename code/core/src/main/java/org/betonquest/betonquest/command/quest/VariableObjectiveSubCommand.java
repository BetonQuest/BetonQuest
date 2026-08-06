package org.betonquest.betonquest.command.quest;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.VariableReplacement;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.identifier.ObjectiveIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.service.objective.ObjectiveManager;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.quest.objective.variable.VariableObjective;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * VariableObjective stuff.
 */
public class VariableObjectiveSubCommand extends QuestCommandPart {

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage playerDataStorage;

    /**
     * The objective manager.
     */
    private final ObjectiveManager objectiveManager;

    /**
     * The quest package manager.
     */
    private final QuestPackageManager questPackageManager;

    /**
     * Registers a new executor and a new tab completer of the /betonquest command.
     *
     * @param log               the logger to use
     * @param constructorParams the constructor parameters
     */
    public VariableObjectiveSubCommand(final BetonQuestLogger log, final ConstructorParams constructorParams) {
        super(log, constructorParams);
        this.playerDataStorage = constructorParams.playerDataStorage();
        this.objectiveManager = constructorParams.objectiveManager();
        this.questPackageManager = constructorParams.questPackageManager();
    }

    @Override
    public List<String> names() {
        return List.of("variable", "var");
    }

    @Override
    @SuppressWarnings("PMD.NcssCount")
    public void handle(final CommandSender sender, final String... args) {
        final Profile profile = getTargetProfile(sender, args);
        if (profile == null) {
            return;
        }

        if (args.length == 2) {
            log.debug("Missing objective instruction string");
            sendMessage(sender, "specify_objective");
            return;
        }

        // get the objective
        final ObjectiveIdentifier objectiveID;
        final Objective tmp;
        try {
            objectiveID = getIdentifier(ObjectiveIdentifier.class, args[2]);
            tmp = objectiveManager.getObjective(objectiveID);
        } catch (final QuestException e) {
            sendMessage(sender, "error",
                    new VariableReplacement("error", Component.text(e.getMessage())));
            log.warn("Could not find objective: " + e.getMessage(), e);
            return;
        }
        if (!(tmp instanceof final VariableObjective variableObjective)) {
            log.debug(tmp.getObjectiveID().getFull() + " is not a variable objective");
            sendMessage(sender, "specify_objective");
            return;
        }
        log.debug("Using variable objective " + variableObjective.getObjectiveID().getFull());

        final boolean isOnline = profile.getOnlineProfile().isPresent();
        final VariableObjective.VariableData data;
        if (isOnline) {
            data = null;
        } else {
            final PlayerData offline = playerDataStorage.get(profile);
            final String instruction = offline.getRawObjectives().get(variableObjective.getObjectiveID().getFull());
            if (instruction == null) {
                log.debug("There is no data for that objective for that player!");
                sendMessage(sender, "error",
                        new VariableReplacement("error", Component.text("There is no data for that objective!")));
                return;
            }
            data = new VariableObjective.VariableData(instruction, profile, objectiveID);
        }

        final String subCommand = args.length == 3 ? "list" : args[3].toLowerCase(Locale.ROOT);
        switch (subCommand) {
            case "list", "l" -> {
                if (data != null) {
                    log.debug("Can't list variable data on offline player");
                    sendMessage(sender, "offline_invalid");
                    return;
                }
                // check for actual values
                final Map<String, String> properties = variableObjective.getProperties(profile);
                if (properties == null) {
                    log.debug("No property for profile");
                    sendMessage(sender, "player_no_property");
                    return;
                }
                // display variable objective keys and values
                log.debug("Listing keys and values");
                final Predicate<String> shouldDisplay = createListFilter(args, 4, Function.identity());
                sendMessage(sender, "player_variables",
                        new VariableReplacement("objective", Component.text(variableObjective.getObjectiveID().getFull())));
                properties.entrySet().stream()
                        .filter(entry -> shouldDisplay.test(entry.getKey()))
                        .sorted((o1, o2) -> o1.getKey().compareToIgnoreCase(o2.getKey()))
                        .forEach(entry -> sender.sendMessage("§b- " + entry.getKey() + "§e: §a" + entry.getValue()));
            }
            case "set", "s" -> {
                if (args.length < 6) {
                    log.debug("Missing amount");
                    sendMessage(sender, "arguments");
                    return;
                }
                final String value = String.join(" ", Arrays.copyOfRange(args, 5, args.length));
                log.debug("Setting value " + value + " for key " + args[4] + " for " + profile + " in " + variableObjective.getObjectiveID().getFull());
                if (data == null) {
                    variableObjective.store(profile, args[4], value);
                } else {
                    data.add(args[4], value);
                }
                sendMessage(sender, "value_set",
                        new VariableReplacement("value", Component.text(value)),
                        new VariableReplacement("key", Component.text(args[4])));
            }
            case "del", "d" -> {
                if (args.length < 5) {
                    log.debug("Missing amount");
                    sendMessage(sender, "arguments");
                    return;
                }
                log.debug("Removing key " + args[4] + " for " + profile + " in " + variableObjective.getObjectiveID().getFull());
                if (data == null) {
                    variableObjective.store(profile, args[4], null);
                } else {
                    data.add(args[4], null);
                }
                sendMessage(sender, "key_remove",
                        new VariableReplacement("key", Component.text(args[4])));
            }
            default -> {
                log.debug("The argument was unknown");
                sendMessage(sender, "unknown_argument");
            }
        }
    }

    @Override
    @SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts")
    public Optional<List<String>> complete(final String... args) {
        if (args.length == 2) {
            return Optional.empty();
        }
        if (args.length == 3) {
            final String last = args[args.length - 1];
            if (last == null || !last.contains(Identifier.SEPARATOR)) {
                return completePackage();
            }
            final String pack = last.substring(0, last.indexOf(Identifier.SEPARATOR));
            final QuestPackage configPack = questPackageManager.getPackages().get(pack);
            if (configPack == null) {
                return Optional.of(Collections.emptyList());
            }
            final ConfigurationSection configuration = configPack.getConfig().getConfigurationSection("objectives");
            final List<String> completions = new ArrayList<>();
            if (configuration != null) {
                for (final String key : configuration.getKeys(true)) {
                    if (configuration.isConfigurationSection(key)) {
                        continue;
                    }
                    final String rawObjectiveInstruction = configuration.getString(key);
                    if (rawObjectiveInstruction != null && rawObjectiveInstruction.stripIndent().startsWith("variable")) {
                        completions.add(pack + Identifier.SEPARATOR + key);
                    }
                }
            }
            return Optional.of(completions);
        }
        if (args.length == 4) {
            return Optional.of(Arrays.asList("list", "set", "del"));
        }
        return Optional.of(Collections.emptyList());
    }
}
