package org.betonquest.betonquest.api.identifier;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

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
     * Creates a new identifier without resolving the package.
     *
     * @param pack       the package the object is in
     * @param identifier the identifier of the object without the package name
     */
    protected DefaultIdentifier(final QuestPackage pack, final String identifier) {
        this.pack = pack;
        this.identifier = identifier;
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
}
