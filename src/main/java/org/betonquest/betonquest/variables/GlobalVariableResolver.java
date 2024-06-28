package org.betonquest.betonquest.variables;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.List;
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
 *
 * @deprecated use {@link org.betonquest.betonquest.instruction.variable.VariableString} instead
 */
@Deprecated
public final class GlobalVariableResolver {
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
     * @deprecated use {@link org.betonquest.betonquest.instruction.variable.VariableString} instead
     */
    @Deprecated
    @Contract("_, null -> null; _, !null -> !null")
    @Nullable
    public static String resolve(final QuestPackage pack, @Nullable final String input) {
        if (input == null) {
            return null;
        }
        try {
            return new VariableString(BetonQuest.getInstance().getVariableProcessor(), pack, input).getValue(null);
        } catch (QuestRuntimeException | InstructionParseException e) {
            LOG.warn(pack, "Could not resolve global variable in string: " + input, e);
            return input + "(not found)";
        }
    }

    /**
     * Resolves all global variables recursively in the given strings
     * with the {@link #resolve(QuestPackage, String)} method.
     *
     * @param pack   the package in which the input strings are defined
     * @param inputs the inputs string
     * @return the strings with all global variables resolved
     * @deprecated use {@link org.betonquest.betonquest.instruction.variable.VariableString} instead
     */
    @Deprecated
    public static List<String> resolve(final QuestPackage pack, final List<String> inputs) {
        return inputs.stream().map(string -> resolve(pack, string)).collect(Collectors.toList());
    }
}
