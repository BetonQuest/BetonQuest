package org.betonquest.betonquest.command.quest;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.VariableReplacement;
import org.betonquest.betonquest.api.config.Localizations;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Common methods used in the quest sub commands.
 */
@SuppressWarnings({"PMD.AvoidLiteralsInIfCondition", "PMD.CouplingBetweenObjects"})
public abstract class QuestCommandPart implements SubCommand {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    protected final BetonQuestLogger log;

    /**
     * Provider for Player Profiles.
     */
    protected final ProfileProvider profileProvider;

    /**
     * The {@link Localizations} instance.
     */
    protected final Localizations localizations;

    /**
     * Storage for player data.
     */
    protected final PlayerDataStorage playerDataStorage;

    /**
     * The quest package manager.
     */
    protected final QuestPackageManager questPackageManager;

    /**
     * The identifier registry.
     */
    private final Identifiers identifiers;

    /**
     * Primary name and aliases.
     */
    private final List<String> commandNames;

    /**
     * Primary name to suggest with syntax.
     */
    private final Map.Entry<String, String> commandSyntax;

    /**
     * Registers a new executor and a new tab completer of the /betonquest command.
     *
     * @param log               the logger to use
     * @param constructorParams the constructor parameters
     * @param names             the primary name and aliases
     * @param syntax            the command syntax
     */
    public QuestCommandPart(final BetonQuestLogger log, final ConstructorParams constructorParams,
                            final List<String> names, final String syntax) {
        this.log = log;
        this.playerDataStorage = constructorParams.playerDataStorage();
        this.profileProvider = constructorParams.profileProvider();
        this.localizations = constructorParams.localizations();
        this.questPackageManager = constructorParams.questPackageManager();
        this.identifiers = constructorParams.identifiers();
        this.commandNames = names;
        final String primaryName = names.get(0);
        this.commandSyntax = Map.entry(primaryName, primaryName + " " + syntax);
    }

    @Override
    public List<String> names() {
        return commandNames;
    }

    @Override
    public Map.Entry<String, String> syntax() {
        return commandSyntax;
    }

    /* default */
    @Nullable
    Profile getTargetProfile(final CommandSender sender, final String... args) {
        if (args.length < 2) {
            log.debug("Player's name is missing");
            sendMessage(sender, "specify_player");
            return null;
        }
        return profileProvider.getProfile(Bukkit.getOfflinePlayer(args[1]));
    }

    /* default */
    @Nullable
    PlayerData getTargetPlayerData(final CommandSender sender, final String... args) {
        final Profile profile = getTargetProfile(sender, args);
        if (profile == null) {
            return null;
        }
        if (profile.getOnlineProfile().isEmpty()) {
            log.debug("Profile is offline, loading his data");
        }
        return playerDataStorage.get(profile);
    }

    /* default */
    void sendMessage(final CommandSender sender, final String messageName, final VariableReplacement... replacements) {
        final OnlineProfile profile = sender instanceof final Player player ? profileProvider.getProfile(player) : null;
        try {
            sender.sendMessage(localizations.getMessage(profile, messageName, replacements));
        } catch (final QuestException e) {
            log.warn("Failed to send message '" + messageName + "': " + e.getMessage(), e);
            sender.sendMessage("Failed to send message '" + messageName + "': " + e.getMessage());
        }
    }

    /* default */
    <T> Predicate<T> createListFilter(final String[] args, final int filterIndex, final Function<T, String> getId) {
        if (args.length > filterIndex) {
            return createCaseInsensitivePrefixPredicate(args[filterIndex], getId);
        }
        return pointer -> true;
    }

    private <T> Predicate<T> createCaseInsensitivePrefixPredicate(final String prefix, final Function<T, String> getId) {
        return element -> getId.apply(element).regionMatches(true, 0, prefix, 0, prefix.length());
    }

    /* default */
    <I extends Identifier> I getIdentifier(final Class<I> identifierClass, final String identifier) throws QuestException {
        final IdentifierFactory<I> identifierFactory = identifiers.getFactory(identifierClass);
        return identifierFactory.parseIdentifier(null, identifier);
    }

    /**
     * Returns a list including all possible tab complete options for ids.
     *
     * @param type - the type of the ID, null for unspecific
     */
    /* default */
    Optional<List<String>> completeId(final String[] args, @Nullable final AccessorType type) {
        final String last = args[args.length - 1];
        if (last == null || !last.contains(Identifier.SEPARATOR)) {
            return completePackage();
        }
        final String pack = last.substring(0, last.indexOf(Identifier.SEPARATOR));
        final QuestPackage configPack = questPackageManager.getPackages().get(pack);
        if (configPack == null) {
            return Optional.of(new ArrayList<>());
        }
        if (type == null) {
            final List<String> completions = new ArrayList<>();
            completions.add(pack + Identifier.SEPARATOR);
            return Optional.of(completions);
        }
        final ConfigurationSection configuration = configPack.getConfig()
                .getConfigurationSection(type.name().toLowerCase(Locale.ROOT));
        final List<String> completions = new ArrayList<>();
        if (configuration != null) {
            for (final String key : configuration.getKeys(type.allowNested)) {
                if (type.allowNested && configuration.isConfigurationSection(key)) {
                    continue;
                }
                completions.add(pack + Identifier.SEPARATOR + key);
            }
        }
        return Optional.of(completions);
    }

    /**
     * Returns a list of all packages for the tab completer.
     */
    /* default */
    Optional<List<String>> completePackage() {
        return Optional.of(new ArrayList<>(questPackageManager.getPackages().keySet()));
    }
}
