package org.betonquest.betonquest.api.identifier;

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
            throw new QuestException("ID is null");
        }
        if (identifier.contains(SEPERATOR)) {
            final int dotIndex = identifier.indexOf(SEPERATOR);
            final QuestPackage parsed = parsePackageFromIdentifier(packManager, pack, identifier, dotIndex);
            if (parsed != null) {
                this.pack = parsed;
                this.identifier = identifier.substring(dotIndex + 1);
                return;
            }
            if (pack == null) {
                throw new QuestException("Package in ID '" + identifier + "' does not exist");
            }
        }
        if (pack == null) {
            throw new QuestException("No package specified for ID '" + identifier + "'!");
        }
        this.pack = pack;
        this.identifier = identifier;
    }

    @Nullable
    private QuestPackage parsePackageFromIdentifier(final QuestPackageManager packManager,
                                                    @Nullable final QuestPackage pack, final String identifier,
                                                    final int dotIndex) throws QuestException {
        final String packName = identifier.substring(0, dotIndex);
        if (pack != null) {
            if (packName.startsWith(PACKAGE_NAVIGATOR + PACKAGE_SEPERATOR)) {
                return resolveRelativePathUp(packManager, pack, identifier, packName);
            }
            if (packName.startsWith(PACKAGE_SEPERATOR)) {
                return resolveRelativePathDown(packManager, pack, identifier, packName);
            }
        }
        final QuestPackage packFromName = packManager.getPackage(packName);
        if (packFromName != null) {
            return packFromName;
        }
        if (identifier.length() == dotIndex + 1) {
            throw new QuestException("ID of the pack is null");
        }
        return null;
    }

    private QuestPackage resolveRelativePathUp(final QuestPackageManager packManager, final QuestPackage pack,
                                               final String identifier, final String packName) throws QuestException {
        final String[] root = pack.getQuestPath().split(PACKAGE_SEPERATOR);
        final String[] path = packName.split(PACKAGE_SEPERATOR);
        final int stepsUp = getStepsUp(root, path, identifier);
        final String packagePath = buildPackagePath(root, path, stepsUp);
        try {
            final String absolute = packagePath.substring(0, packagePath.length() - 1);
            final QuestPackage resolved = packManager.getPackage(absolute);
            if (resolved == null) {
                throw new QuestException("Relative path in ID '" + identifier + "' resolved to '"
                        + absolute + "', but this package does not exist!");
            }
            return resolved;
        } catch (final StringIndexOutOfBoundsException e) {
            throw new QuestException("Relative path in ID '" + identifier + "' is invalid!", e);
        }
    }

    private int getStepsUp(final String[] root, final String[] path, final String identifier) throws QuestException {
        int stepsUp = 0;
        while (stepsUp < path.length && PACKAGE_NAVIGATOR.equals(path[stepsUp])) {
            stepsUp++;
        }
        if (stepsUp > root.length) {
            throw new QuestException("Relative path goes out of package scope! Consider removing a few '"
                    + PACKAGE_NAVIGATOR + "'s in ID " + identifier);
        }
        return stepsUp;
    }

    private String buildPackagePath(final String[] root, final String[] path, final int stepsUp) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < root.length - stepsUp; i++) {
            builder.append(root[i]).append(PACKAGE_SEPERATOR);
        }
        for (int i = stepsUp; i < path.length; i++) {
            builder.append(path[i]).append(PACKAGE_SEPERATOR);
        }
        return builder.toString();
    }

    private QuestPackage resolveRelativePathDown(final QuestPackageManager packManager, final QuestPackage pack,
                                                 final String identifier, final String packName) throws QuestException {
        final String currentPath = pack.getQuestPath();
        final String fullPath = currentPath + packName;

        final QuestPackage resolved = packManager.getPackage(fullPath);
        if (resolved == null) {
            throw new QuestException("Relative path in ID '" + identifier + "' resolved to '"
                    + fullPath + "', but this package does not exist!");
        }
        return resolved;
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
