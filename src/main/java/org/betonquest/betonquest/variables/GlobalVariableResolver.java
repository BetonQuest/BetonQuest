package org.betonquest.betonquest.variables;

import lombok.CustomLog;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.GlobalVariableID;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class resolves all global variables in a string.
 * <p>
 * The format of a global variable is {@code $<package>.<variable>$}.
 * The package part is optional and defaults to the current package.
 * Example: {@code $myVariable$}
 * Example: {@code $my.package.myVariable$}
 * <p>
 * The variables are defined in the {@code variables} section.
 */
@CustomLog
public final class GlobalVariableResolver {

    /**
     * A regex pattern to match global variables.
     */
    public static final Pattern GLOBAL_VARIABLE_PATTERN = Pattern.compile("\\$(?<variable>[^ $\\s]+)\\$");

    private GlobalVariableResolver() {
    }

    /**
     * Resolved all global variables recursively in the given string.
     *
     * @param pack  the package to resolve the variables in
     * @param input the input string
     * @return the string with all global variables resolved
     */
    public static String resolveGlobalVariables(final QuestPackage pack, final String input) {
        if (input == null) {
            return null;
        }
        final Matcher matcher = GLOBAL_VARIABLE_PATTERN.matcher(input);
        final StringBuilder variableInput = new StringBuilder();
        while (matcher.find()) {
            final String variable = matcher.group("variable");
            final String replacement = getReplacement(pack, variable);
            matcher.appendReplacement(variableInput, replacement);
        }
        matcher.appendTail(variableInput);
        return variableInput.toString();
    }

    private static String getReplacement(final QuestPackage pack, final String variable) {
        try {
            return new GlobalVariableID(pack, variable).generateInstruction().getInstruction();
        } catch (final ObjectNotFoundException e) {
            LOG.warn(pack, e.getMessage(), e);
            return variable + "(not found)";
        }
    }
}
