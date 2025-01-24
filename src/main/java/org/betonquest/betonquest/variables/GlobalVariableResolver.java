package org.betonquest.betonquest.variables;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.exception.ObjectNotFoundException;
import org.betonquest.betonquest.id.GlobalVariableID;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
public final class GlobalVariableResolver {
    /**
     * A regex pattern to match global variables.
     */
    public static final Pattern GLOBAL_VARIABLE_PATTERN = Pattern.compile("\\$(?<variable>[^ $\\s]+)\\$");

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private static final BetonQuestLogger LOG = BetonQuest.getInstance().getLoggerFactory().create(GlobalVariableResolver.class);

    private GlobalVariableResolver() {
    }

    /**
     * Resolves all global variables recursively in the given string.
     *
     * @param pack  the package in which the input string is defined
     * @param input the input string
     * @return the string with all global variables resolved
     */
    @Contract("_, null -> null; _, !null -> !null")
    @Nullable
    public static String resolve(final QuestPackage pack, @Nullable final String input) {
        if (input == null) {
            return null;
        }
        final Matcher matcher = GLOBAL_VARIABLE_PATTERN.matcher(input);
        final StringBuilder variableInput = new StringBuilder();
        while (matcher.find()) {
            final String variable = matcher.group("variable");
            final String replacement = getReplacement(pack, variable);
            matcher.appendReplacement(variableInput, replacement.replace("\\", "\\\\").replace("$", "\\$"));
        }
        matcher.appendTail(variableInput);
        return variableInput.toString();
    }

    private static String getReplacement(final QuestPackage pack, final String variable) {
        try {
            return new GlobalVariableID(pack, variable).getInstruction().toString();
        } catch (final ObjectNotFoundException | QuestException e) {
            LOG.warn(pack, e.getMessage(), e);
            return variable + "(not found)";
        }
    }

    /**
     * Resolves all global variables recursively in the given strings
     * with the {@link #resolve(QuestPackage, String)} method.
     *
     * @param pack   the package in which the input strings are defined
     * @param inputs the inputs string
     * @return the strings with all global variables resolved
     */
    public static List<String> resolve(final QuestPackage pack, final List<String> inputs) {
        return inputs.stream().map(string -> resolve(pack, string)).collect(Collectors.toList());
    }
}
