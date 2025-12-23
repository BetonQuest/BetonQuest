package org.betonquest.betonquest.api.identifier;

import org.betonquest.betonquest.api.config.quest.QuestPackage;

import java.util.regex.Pattern;

/**
 * Identifiers are used to identify objects in BetonQuest.
 * A dot separates the package name from the identifier.
 * The package name can be relative or absolute.
 * Navigation in the package hierarchy is done with the dash as separator and the underscore as up navigator.
 */
public interface Identifier {

    /**
     * The string used to separate the package name from the identifier.
     */
    String SEPARATOR = ">";

    /**
     * The string to separate the package address into parts.
     */
    String PACKAGE_SEPARATOR = "-";

    /**
     * The string used to navigate up in the package hierarchy.
     */
    String PACKAGE_NAVIGATOR = "_";

    /**
     * The pattern to find unescaped separators in an identifier.
     */
    Pattern SEPARATOR_PATTERN = Pattern.compile("^(?<package>.*?)(?<!\\\\)(?:\\\\\\\\)*" + SEPARATOR + "(?<identifier>.*)$");

    /**
     * Returns the package the object exists in.
     *
     * @return the package
     */
    QuestPackage getPackage();

    /**
     * Returns the identifier of the object without the package name.
     *
     * @return the identifier in the format {@code identifier}
     */
    String get();

    /**
     * Returns the full identifier of the object, including the package name.
     *
     * @return the full identifier in the format {@code package.identifier}
     */
    String getFull();
}
