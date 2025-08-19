package org.betonquest.betonquest.api.identifier;

import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.quest.QuestException;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Identifiers are used to identify objects in BetonQuest.
 * A dot separates the package name from the identifier.
 * The package name can be relative or absolute.
 * Navigation in the package hierarchy is done with the dash as separator and the underscore as up navigator.
 */
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
public abstract class Identifier {
    /**
     * The string used to separate the package name from the identifier.
     */
    public static final String SEPERATOR = ".";

    /**
     * The string to separate the package address into parts.
     */
    public static final String PACKAGE_SEPERATOR = "-";

    /**
     * The string used to navigate up in the package hierarchy.
     */
    public static final String PACKAGE_NAVIGATOR = "_";

    /**
     * The package the object is in.
     */
    private final QuestPackage pack;

    /**
     * The identifier of the object without the package name.
     */
    @SuppressWarnings("PMD.AvoidFieldNameMatchingTypeName")
    private final String identifier;

    /**
     * Creates a new Identifier. Handles relative and absolute paths and edge cases with special Identifiers.
     *
     * @param packManager the package manager to resolve packages
     * @param pack        the package the ID is in
     * @param identifier  the identifier string leading to the object
     * @throws QuestException if the identifier could not be parsed
     */
    protected Identifier(final QuestPackageManager packManager, @Nullable final QuestPackage pack,
                         final String identifier) throws QuestException {
        if (identifier.isEmpty()) {
            throw new QuestException("ID is empty!");
        }
        if (identifier.contains(SEPERATOR)) {
            final Pair<String, String> split = splitSeperator(identifier);
            try {
                this.pack = parsePackageFromIdentifier(packManager, pack, split.getKey());
                this.identifier = split.getValue();
                return;
            } catch (final QuestException e) {
                throw new QuestException("ID '%s' could not be parsed: %s".formatted(identifier, e.getMessage()), e);
            }
        }
        if (pack == null) {
            throw new QuestException("ID '%s' has no package specified!".formatted(identifier));
        }
        this.pack = pack;
        this.identifier = identifier;
    }

    private Pair<String, String> splitSeperator(final String identifier) throws QuestException {
        final int dotIndex = identifier.indexOf(SEPERATOR);
        if (identifier.length() == dotIndex + 1) {
            throw new QuestException("ID '%s' has no identifier after the package name!".formatted(identifier));
        }
        if (dotIndex < 0) {
            throw new QuestException("ID '%s' has no package name!".formatted(identifier));
        }
        return Pair.of(identifier.substring(0, dotIndex), identifier.substring(dotIndex + 1));
    }

    private QuestPackage parsePackageFromIdentifier(final QuestPackageManager packManager,
                                                    @Nullable final QuestPackage pack, final String packPath)
            throws QuestException {
        if (pack != null) {
            if (packPath.startsWith(PACKAGE_NAVIGATOR + PACKAGE_SEPERATOR)) {
                return resolveRelativePathUp(packManager, pack, packPath);
            }
            if (packPath.startsWith(PACKAGE_SEPERATOR)) {
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
        final String[] root = pack.getQuestPath().split(PACKAGE_SEPERATOR);
        final String[] path = packPath.split(PACKAGE_SEPERATOR);
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
            packPath.append(root[i]).append(PACKAGE_SEPERATOR);
        }
        for (int i = levelsUp; i < path.length; i++) {
            packPath.append(path[i]).append(PACKAGE_SEPERATOR);
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

    /**
     * Returns the package the object exist in.
     *
     * @return the package
     */
    public QuestPackage getPackage() {
        return pack;
    }

    /**
     * Returns the identifier of the object without the package name.
     *
     * @return the identifier in the format {@code identifier}
     */
    public String get() {
        return identifier;
    }

    /**
     * Returns the full identifier of the object, including the package name.
     *
     * @return the full identifier in the format {@code package.identifier}
     */
    public String getFull() {
        return pack.getQuestPath() + SEPERATOR + get();
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
        final Identifier other = (Identifier) obj;
        return Objects.equals(identifier, other.identifier)
                && Objects.equals(pack.getQuestPath(), other.pack.getQuestPath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, pack.getQuestPath());
    }
}
