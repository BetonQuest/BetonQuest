package org.betonquest.betonquest.api.identifier.factory;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.Identifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.regex.Matcher;

import static org.betonquest.betonquest.api.identifier.Identifier.PACKAGE_NAVIGATOR;
import static org.betonquest.betonquest.api.identifier.Identifier.PACKAGE_SEPARATOR;
import static org.betonquest.betonquest.api.identifier.Identifier.SEPARATOR;
import static org.betonquest.betonquest.api.identifier.Identifier.SEPARATOR_PATTERN;

/**
 * A default implementation of {@link IdentifierFactory} providing the baseline method
 * to parse any identifier: {@link #parseIdentifier(QuestPackage, String)}.
 *
 * @param <I> the type of identifier to create
 */
public abstract class DefaultIdentifierFactory<I extends Identifier> implements IdentifierFactory<I> {

    /**
     * The quest package manager to resolve relative paths.
     */
    private final QuestPackageManager packManager;

    /**
     * Create a new identifier factory.
     *
     * @param packManager the quest package manager to resolve relative paths
     */
    public DefaultIdentifierFactory(final QuestPackageManager packManager) {
        this.packManager = packManager;
    }

    /**
     * Ensures the specified section is defined for the identifier.
     *
     * @param resolvedIdentifier the identifier to check
     * @param section            the section to check for
     * @return the given identifier
     * @throws QuestException if the section is not defined
     */
    protected I requireSection(final I resolvedIdentifier, final String section) throws QuestException {
        final MultiConfiguration config = resolvedIdentifier.getPackage().getConfig();
        if (!config.isConfigurationSection(section + config.options().pathSeparator() + resolvedIdentifier.get())) {
            throw new QuestException("'%s' does not define a section under section '%s'".formatted(resolvedIdentifier.getFull(), section));
        }
        return resolvedIdentifier;
    }

    /**
     * Ensures the specified section contains a string instruction.
     *
     * @param resolvedIdentifier the identifier to check
     * @param section            the section to check for
     * @return the given identifier
     * @throws QuestException if the section does not contain a string instruction
     */
    protected I requireInstruction(final I resolvedIdentifier, final String section) throws QuestException {
        final MultiConfiguration config = resolvedIdentifier.getPackage().getConfig();
        if (!config.isString(section + config.options().pathSeparator() + resolvedIdentifier.get())) {
            throw new QuestException("'%s' does not define a string instruction in section '%s'!".formatted(resolvedIdentifier.getFull(), section));
        }
        return resolvedIdentifier;
    }

    /**
     * Directly parse an input string into a package and identifier.
     *
     * @param sourcePackage the package the identifier is in, or null if not specified
     * @param input         the input string to parse
     * @return an entry containing the parsed package and identifier
     * @throws QuestException if the input could not be parsed
     */
    protected Map.Entry<QuestPackage, String> parse(@Nullable final QuestPackage sourcePackage, final String input) throws QuestException {
        if (input.contains(" ")) {
            throw new QuestException("Spaces are invalid for identifier '%s'".formatted(input));
        }
        final RawIdentifier rawIdentifier = splitIdentifier(input);
        if (rawIdentifier.pack() == null) {
            if (sourcePackage == null) {
                throw new QuestException("ID '%s' has no package specified!".formatted(input));
            }
            return Map.entry(sourcePackage, rawIdentifier.identifier());
        }
        try {
            final QuestPackage pack = parsePackageFromIdentifier(sourcePackage, rawIdentifier.pack());
            return Map.entry(pack, rawIdentifier.identifier());
        } catch (final QuestException e) {
            throw new QuestException("ID '%s' could not be parsed: %s".formatted(input, e.getMessage()), e);
        }
    }

    private RawIdentifier splitIdentifier(final String rawIdentifier) throws QuestException {
        if (rawIdentifier.isEmpty()) {
            throw new QuestException("ID is empty!");
        }
        final Matcher matcher = SEPARATOR_PATTERN.matcher(rawIdentifier);
        if (matcher.matches()) {
            final String pack = matcher.group("package").replace("\\" + SEPARATOR, SEPARATOR);
            final String identifier = matcher.group("identifier").replace("\\" + SEPARATOR, SEPARATOR);
            if (identifier.isEmpty()) {
                throw new QuestException("ID '%s' has no identifier after the package name!".formatted(rawIdentifier));
            }
            return new RawIdentifier(pack, identifier);
        }
        return new RawIdentifier(null, rawIdentifier.replace("\\" + SEPARATOR, SEPARATOR));
    }

    private QuestPackage parsePackageFromIdentifier(@Nullable final QuestPackage pack, final String packPath)
            throws QuestException {
        if (pack != null) {
            if (packPath.startsWith(PACKAGE_NAVIGATOR + PACKAGE_SEPARATOR)) {
                return resolveRelativePathUp(pack, packPath);
            }
            if (packPath.startsWith(PACKAGE_SEPARATOR)) {
                return resolveRelativePathDown(pack, packPath);
            }
        }
        final QuestPackage packFromPath = packManager.getPackage(packPath);
        if (packFromPath == null) {
            throw new QuestException("No package '%s' found!".formatted(packPath));
        }
        return packFromPath;
    }

    private QuestPackage resolveRelativePathUp(final QuestPackage pack, final String packPath) throws QuestException {
        final String[] root = pack.getQuestPath().split(PACKAGE_SEPARATOR);
        final String[] path = packPath.split(PACKAGE_SEPARATOR);
        final int levelsUp = getPathUpLevels(root, path, packPath);
        final String resolvedPackPath = resolvePackagePath(root, path, levelsUp);
        final QuestPackage resolvedPack = packManager.getPackage(resolvedPackPath);
        if (resolvedPack == null) {
            throw new QuestException("Relative path '%s' resolved to '%s', but this package does not exist!"
                    .formatted(packPath, resolvedPackPath));
        }
        return resolvedPack;
    }

    private int getPathUpLevels(final String[] root, final String[] path, final String packPath) throws QuestException {
        int levelsUp = 0;
        while (levelsUp < path.length && PACKAGE_NAVIGATOR.equals(path[levelsUp])) {
            levelsUp++;
        }
        if (levelsUp > root.length) {
            throw new QuestException("Relative path '%s' goes up too many levels!".formatted(packPath));
        }
        return levelsUp;
    }

    private String resolvePackagePath(final String[] root, final String[] path, final int levelsUp) {
        final StringBuilder packPath = new StringBuilder();
        for (int i = 0; i < root.length - levelsUp; i++) {
            packPath.append(root[i]).append(PACKAGE_SEPARATOR);
        }
        for (int i = levelsUp; i < path.length; i++) {
            packPath.append(path[i]).append(PACKAGE_SEPARATOR);
        }
        if (!packPath.isEmpty()) {
            packPath.deleteCharAt(packPath.length() - 1);
        }
        return packPath.toString();
    }

    private QuestPackage resolveRelativePathDown(final QuestPackage pack, final String packPath) throws QuestException {
        final String resolvedPackPath = pack.getQuestPath() + packPath;

        final QuestPackage resolvedPack = packManager.getPackage(resolvedPackPath);
        if (resolvedPack == null) {
            throw new QuestException("Relative path '%s' resolved to '%s', but this package does not exist!"
                    .formatted(packPath, resolvedPackPath));
        }
        return resolvedPack;
    }

    /**
     * A record to hold the raw parts of an identifier.
     *
     * @param pack       the package part, or null if not present
     * @param identifier the identifier part
     */
    private record RawIdentifier(@Nullable String pack, String identifier) {

    }
}
