package org.betonquest.betonquest.api.identifier;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.regex.Matcher;

/**
 * Identifiers are used to identify objects in BetonQuest.
 * A dot separates the package name from the identifier.
 * The package name can be relative or absolute.
 * Navigation in the package hierarchy is done with the dash as separator and the underscore as up navigator.
 */
public abstract class DefaultIdentifier implements Identifier {

    /**
     * The package the object is in.
     */
    private final QuestPackage pack;

    /**
     * The identifier of the object without the package name.
     */
    private final String identifier;

    /**
     * Creates a new Identifier. Handles relative and absolute paths and edge cases with special Identifiers.
     *
     * @param packManager the package manager to resolve packages
     * @param pack        the package the ID is in
     * @param identifier  the identifier string leading to the object
     * @throws QuestException if the identifier could not be parsed
     */
    protected DefaultIdentifier(final QuestPackageManager packManager, @Nullable final QuestPackage pack,
                                final String identifier) throws QuestException {
        final RawIdentifier rawIdentifier = splitIdentifier(identifier);
        this.identifier = rawIdentifier.identifier;
        if (rawIdentifier.pack == null) {
            if (pack == null) {
                throw new QuestException("ID '%s' has no package specified!".formatted(identifier));
            }
            this.pack = pack;
        } else {
            try {
                this.pack = parsePackageFromIdentifier(packManager, pack, rawIdentifier.pack);
            } catch (final QuestException e) {
                throw new QuestException("ID '%s' could not be parsed: %s".formatted(identifier, e.getMessage()), e);
            }
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

    private QuestPackage parsePackageFromIdentifier(final QuestPackageManager packManager,
                                                    @Nullable final QuestPackage pack, final String packPath)
            throws QuestException {
        if (pack != null) {
            if (packPath.startsWith(PACKAGE_NAVIGATOR + PACKAGE_SEPARATOR)) {
                return resolveRelativePathUp(packManager, pack, packPath);
            }
            if (packPath.startsWith(PACKAGE_SEPARATOR)) {
                return resolveRelativePathDown(packManager, pack, packPath);
            }
        }
        final QuestPackage packFromPath = packManager.getPackage(packPath);
        if (packFromPath == null) {
            throw new QuestException("No package '%s' found!".formatted(packPath));
        }
        return packFromPath;
    }

    private QuestPackage resolveRelativePathUp(final QuestPackageManager packManager, final QuestPackage pack,
                                               final String packPath) throws QuestException {
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

    private QuestPackage resolveRelativePathDown(final QuestPackageManager packManager, final QuestPackage pack,
                                                 final String packPath) throws QuestException {
        final String resolvedPackPath = pack.getQuestPath() + packPath;

        final QuestPackage resolvedPack = packManager.getPackage(resolvedPackPath);
        if (resolvedPack == null) {
            throw new QuestException("Relative path '%s' resolved to '%s', but this package does not exist!"
                    .formatted(packPath, resolvedPackPath));
        }
        return resolvedPack;
    }

    @Override
    public QuestPackage getPackage() {
        return pack;
    }

    @Override
    public String get() {
        return identifier;
    }

    @Override
    public String getFull() {
        return pack.getQuestPath() + SEPARATOR + get();
    }

    @Override
    public String toString() {
        return getFull();
    }

    @Override
    public boolean equals(@Nullable final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final DefaultIdentifier other = (DefaultIdentifier) obj;
        return Objects.equals(identifier, other.identifier)
                && Objects.equals(pack.getQuestPath(), other.pack.getQuestPath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, pack.getQuestPath());
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
