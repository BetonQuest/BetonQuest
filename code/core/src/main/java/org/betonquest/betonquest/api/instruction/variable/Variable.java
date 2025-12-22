package org.betonquest.betonquest.api.instruction.variable;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.Variables;
import org.betonquest.betonquest.kernel.processor.adapter.VariableAdapter;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Represent a variable that can be resolved in to given type.
 *
 * @param <T> the type of the variable
 */
public class Variable<T> {

    /**
     * The pattern to match variables in a string marked with percent signs.<br>
     * The percentage can be escaped with a backslash, and the backslash can be escaped with another backslash.
     */
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("(?<!\\\\)(?:\\\\\\\\)*(%((?:[^%\\\\]|\\\\.)*?)%)(?<!\\\\)(?:\\\\\\\\)*");

    /**
     * Supplier of the variable value.
     */
    private final ValueResolver<T> value;

    /**
     * Creates a constant variable.
     *
     * @param value the value of the variable
     */
    public Variable(final T value) {
        this.value = profile -> value;
    }

    /**
     * Resolves a string that may contain variables to a variable of the given type.
     *
     * @param variables the processor to create the variables
     * @param pack      the package in which the variable is used in
     * @param input     the string that may contain variables
     * @param resolver  the resolver to convert the resolved variable to the given type
     * @throws QuestException if the variables could not be created or resolved to the given type
     */
    public Variable(final Variables variables, @Nullable final QuestPackage pack, final String input,
                    final ValueParser<T> resolver) throws QuestException {
        final Map<String, VariableAdapter> foundVariables = getVariables(variables, pack, input);
        if (foundVariables.isEmpty()) {
            final T resolved = resolver.apply(replaceEscapedPercent(input));
            value = profile -> resolver.cloneValue(resolved);
        } else {
            value = profile -> resolver.apply(replaceEscapedPercent(getString(input, foundVariables, profile)));
        }
    }

    private Map<String, VariableAdapter> getVariables(final Variables variables,
                                                      @Nullable final QuestPackage pack,
                                                      final String input)
            throws QuestException {
        final Map<String, VariableAdapter> foundVariables = new HashMap<>();
        for (final String variable : resolveVariables(input)) {
            try {
                final VariableAdapter variableAdapter = variables.create(pack, replaceEscapedPercent(variable));
                foundVariables.put(variable, variableAdapter);
            } catch (final QuestException exception) {
                throw new QuestException("Could not create variable '" + variable + "': "
                        + exception.getMessage(), exception);
            }
        }
        return foundVariables;
    }

    private Set<String> resolveVariables(final String input) {
        return VARIABLE_PATTERN.matcher(input).results()
                .map(MatchResult::group)
                .collect(Collectors.toSet());
    }

    private String getString(final String input, final Map<String, VariableAdapter> variables,
                             @Nullable final Profile profile) throws QuestException {
        final Matcher matcher = VARIABLE_PATTERN.matcher(input);
        final StringBuilder resolvedString = new StringBuilder();
        while (matcher.find()) {
            final String variable = matcher.group();
            final VariableAdapter resolvedVariable = variables.get(variable);
            if (resolvedVariable == null) {
                throw new QuestException("Could not resolve variable '" + variable + "'");
            }
            matcher.appendReplacement(resolvedString, Matcher.quoteReplacement(resolvedVariable.getValue(profile)));
        }
        matcher.appendTail(resolvedString);
        return resolvedString.toString();
    }

    private String replaceEscapedPercent(final String input) {
        return input.replaceAll("(?<!\\\\)\\\\%", "%");
    }

    /**
     * Gets the value of the variable.
     *
     * @param profile the profile of the player to resolve the variables for
     * @return the value of the variable
     * @throws QuestException if the variable could not be resolved
     */
    public T getValue(@Nullable final Profile profile) throws QuestException {
        return value.apply(profile);
    }

    /**
     * Resolves the value of the variable with a Nullable Profile.
     *
     * @param <T> the type of the variable
     */
    @FunctionalInterface
    private interface ValueResolver<T> extends QuestFunction<Profile, T> {

        @Override
        T apply(@Nullable Profile arg) throws QuestException;
    }
}
