package org.betonquest.betonquest.instruction.variable;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
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
     * @param questPackage      the package in which the variable is used in
     * @param input             the string that may contain variables
     * @param resolver          the resolver to convert the resolved variable to the given type
     * @throws InstructionParseException if the variables could not be created or resolved to the given type
     */
    public Variable(final VariableProcessor variableProcessor, final QuestPackage questPackage, final String input,
                    final TypeResolver<T> resolver) throws InstructionParseException {
        final Map<String, org.betonquest.betonquest.api.Variable> variables = getVariables(variableProcessor, questPackage, input);
        if (variables.isEmpty()) {
            try {
                final T resolved = resolver.resolve(input);
                value = profile -> resolved;
            } catch (final QuestRuntimeException e) {
                throw new InstructionParseException(e.getMessage(), e);
            }
        } else {
            value = profile -> resolver.resolve(getString(input, variables, profile));
        }
    }

    private Map<String, org.betonquest.betonquest.api.Variable> getVariables(final VariableProcessor variableProcessor,
                                                                             final QuestPackage questPackage,
                                                                             final String input)
            throws InstructionParseException {
        final Map<String, org.betonquest.betonquest.api.Variable> variables = new HashMap<>();
        for (final String variable : resolveVariables(input)) {
            try {
                final org.betonquest.betonquest.api.Variable variable1 = variableProcessor.create(questPackage, replaceEscapedPercent(variable));
                variables.put(variable, variable1);
            } catch (final InstructionParseException exception) {
                throw new InstructionParseException("Could not create variable '" + variable + "': "
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
                             @Nullable final Profile profile) throws QuestRuntimeException {
        final Matcher matcher = VARIABLE_PATTERN.matcher(input);
        final StringBuilder resolvedString = new StringBuilder();
        while (matcher.find()) {
            final String variable = matcher.group();
            final String resolvedVariable = variables.get(variable).getValue(profile);
            matcher.appendReplacement(resolvedString, Matcher.quoteReplacement(resolvedVariable));
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
     * @throws QuestRuntimeException if the variable could not be resolved
     */
    public T getValue(@Nullable final Profile profile) throws QuestRuntimeException {
        return value.resolve(profile);
    }

    /**
     * Resolves the value of the variable to the given type.
     *
     * @param <T> the type of the variable
     */
    @FunctionalInterface
    public interface TypeResolver<T> {
        /**
         * Converts the resolved variable to the given type.
         *
         * @param variable the variable to resolve
         * @return the resolved variable
         * @throws QuestRuntimeException if the variable could not be resolved
         */
        T resolve(String variable) throws QuestRuntimeException;
    }

    /**
     * Resolves the value of the variable.
     *
     * @param <T> the type of the variable
     */
    @FunctionalInterface
    private interface ValueResolver<T> {
        /**
         * Gets the value of the variable.
         *
         * @param profile the profile of the player to resolve the variables for
         * @return the value of the variable
         * @throws QuestRuntimeException when the variable could not be resolved
         */
        T resolve(@Nullable Profile profile) throws QuestRuntimeException;
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
         * @throws QuestRuntimeException if the value is invalid
         */
        void check(T value) throws QuestRuntimeException;
    }
}
