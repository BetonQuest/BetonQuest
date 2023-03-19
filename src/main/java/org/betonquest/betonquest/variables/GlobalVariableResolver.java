package org.betonquest.betonquest.variables;

import lombok.CustomLog;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.exceptions.ObjectNotFoundException;
import org.betonquest.betonquest.id.GlobalVariableID;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class resolves all global variables in a string.
 */
@CustomLog
public final class GlobalVariableResolver {
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
        String variableInput = input;

        final Pattern globalVariableRegex = Pattern.compile("\\$([^ $\\s]+)\\$");
        while (true) {
            final Matcher matcher = globalVariableRegex.matcher(variableInput);
            if (!matcher.find()) {
                break;
            }
            final String varName = matcher.group(1);
            final GlobalVariableID variableID;
            try {
                variableID = new GlobalVariableID(pack, varName);
            } catch (final ObjectNotFoundException e) {
                LOG.warn(pack, e.getMessage(), e);
                return variableInput;
            }
            final String varRaw = variableID.getPackage().getConfig().getString("variables." + variableID.getBaseID());
            if (varRaw == null) {
                LOG.warn(pack, String.format("Variable %s not defined in package %s", variableID.getBaseID(), variableID.getPackage().getQuestPath()));
                return variableInput;
            }
            final String varVal = resolve(variableID.getPackage(), varRaw);
            variableInput = variableInput.replace("$" + varName + "$", varVal);
        }

        return variableInput;
    }

    private static String resolve(final QuestPackage pack, final String input) {
        if (input == null) {
            return null;
        }
        final Pattern globalVariableRegex = Pattern.compile("\\$([^ $\\s]+)\\$");
        final Matcher matcher = globalVariableRegex.matcher(input);
        final StringBuilder builder = new StringBuilder();
        while (matcher.find()) {
            final String varName = matcher.group(1);
            final String varVal;
            if ("this".equals(varName)) {
                varVal = pack.getQuestPath();
            } else {
                try {
                    final GlobalVariableID variableID = new GlobalVariableID(pack, varName);
                    varVal = "$" + variableID.getPackage().getQuestPath() + "." + variableID.getBaseID() + "$";
                } catch (final ObjectNotFoundException e) {
                    LOG.warn(pack, e.getMessage(), e);
                    return input;
                }
            }
            matcher.appendReplacement(builder, Matcher.quoteReplacement(varVal));
        }
        matcher.appendTail(builder);
        return builder.toString();
    }
}
