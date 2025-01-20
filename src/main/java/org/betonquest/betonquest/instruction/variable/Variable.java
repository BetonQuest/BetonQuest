package org.betonquest.betonquest.instruction.variable;

import org.betonquest.betonquest.api.common.function.QuestFunction;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
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
     * Resolves a string that may contain variables to a variable of the given type.
     *
     * @param variableProcessor the processor to create the variables
     * @param pack              the package in which the variable is used in
     * @param input             the string that may contain variables
     * @param resolver          the resolver to convert the resolved variable to the given type
     * @throws QuestException if the variables could not be created or resolved to the given type
     */
    public Variable(final VariableProcessor variableProcessor, final QuestPackage pack, final String input,
                    final QuestFunction<String, T> resolver) throws QuestException {
        final Map<String, org.betonquest.betonquest.api.Variable> variables = getVariables(variableProcessor, pack, input);
        if (variables.isEmpty()) {
            final T resolved = resolver.apply(input);
            value = profile -> resolved;
        } else {
            value = profile -> resolver.apply(getString(input, variables, profile));
        }
    }

    private Map<String, org.betonquest.betonquest.api.Variable> getVariables(final VariableProcessor variableProcessor,
                                                                             final QuestPackage pack,
                                                                             final String input)
            throws QuestException {
        final Map<String, org.betonquest.betonquest.api.Variable> variables = new HashMap<>();
        for (final String variable : resolveVariables(input)) {
            try {
                final org.betonquest.betonquest.api.Variable variable1 = variableProcessor.create(pack, replaceEscapedPercent(variable));
                variables.put(variable, variable1);
            } catch (final QuestException exception) {
                throw new QuestException("Could not create variable '" + variable + "': "
                        + exception.getMessage(), exception);
            }
        }
        return variables;
    }

    private Set<String> resolveVariables(final String input) {
        return VARIABLE_PATTERN.matcher(input).results()
                .map(MatchResult::group)
                .collect(Collectors.toSet());
    }

    private String getString(final String input, final Map<String, org.betonquest.betonquest.api.Variable> variables,
                             @Nullable final Profile profile) throws QuestException {
        final Matcher matcher = VARIABLE_PATTERN.matcher(input);
        final StringBuilder resolvedString = new StringBuilder();
        while (matcher.find()) {
            final String variable = matcher.group();
            final org.betonquest.betonquest.api.Variable resolvedVariable = variables.get(variable);
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

    /**
     * Checks if the value of the variable is valid.
     * <p>
     * This is a pre-made implementation
     * that can be used by any implementation of {@link Variable} to avoid code duplication.
     */
    @FunctionalInterface
    public interface ValueChecker<T> {
        /**
         * Checks if the value of the variable is valid.
         *
         * @param value the value to check
         * @throws QuestException if the value is invalid
         */
        void check(T value) throws QuestException;
    }
}
