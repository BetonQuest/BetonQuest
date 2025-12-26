package org.betonquest.betonquest.lib.instruction.argument;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.ValueParser;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.Variables;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Represent a variable that can be resolved to the given type.
 *
 * @param <T> the type of the variable
 */
public class DefaultArgument<T> implements Argument<T> {

    /**
     * The pattern to match placeholders in a string marked with percent signs.<br>
     * The percentage can be escaped with a backslash, and the backslash can be escaped with another backslash.
     */
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("(?<!\\\\)(?:\\\\\\\\)*(%((?:[^%\\\\]|\\\\.)*?)%)(?<!\\\\)(?:\\\\\\\\)*");

    /**
     * Supplier of the variable value: a variable itself. Magic.
     */
    private final Argument<T> value;

    /**
     * Creates a constant variable.
     *
     * @param value the value of the variable
     */
    public DefaultArgument(final T value) {
        this.value = profile -> value;
    }

    /**
     * Resolves a string that may contain variables to a variable of the given type.
     *
     * @param variables   the processor to create the variables
     * @param pack        the package in which the variable is used in
     * @param input       the string that may contain variables
     * @param valueParser the valueParser to convert the resolved variable to the given type
     * @throws QuestException if the variables could not be created or resolved to the given type
     */
    public DefaultArgument(final Variables variables, @Nullable final QuestPackage pack, final String input,
                           final ValueParser<T> valueParser) throws QuestException {
        final Map<String, Argument<String>> foundPlaceholders = getPlaceholders(variables, pack, input);
        if (foundPlaceholders.isEmpty()) {
            final String escapedInput = replaceEscapedPercent(input);
            valueParser.apply(escapedInput);
            value = profile -> valueParser.apply(escapedInput);
        } else {
            value = profile -> valueParser.apply(replaceEscapedPercent(getString(input, foundPlaceholders, profile)));
        }
    }

    private Map<String, Argument<String>> getPlaceholders(final Variables variables,
                                                          @Nullable final QuestPackage pack,
                                                          final String input)
            throws QuestException {
        final Map<String, Argument<String>> foundPlaceholders = new HashMap<>();
        for (final String placeholder : resolvePlaceholders(input)) {
            try {
                final Argument<String> placeholderArgument = variables.create(pack, replaceEscapedPercent(placeholder));
                foundPlaceholders.put(placeholder, placeholderArgument);
            } catch (final QuestException exception) {
                throw new QuestException("Could not create variable '" + placeholder + "': "
                        + exception.getMessage(), exception);
            }
        }
        return foundPlaceholders;
    }

    private Set<String> resolvePlaceholders(final String input) {
        return PLACEHOLDER_PATTERN.matcher(input).results()
                .map(MatchResult::group)
                .collect(Collectors.toSet());
    }

    private String getString(final String input, final Map<String, Argument<String>> foundPlaceholders,
                             @Nullable final Profile profile) throws QuestException {
        final Matcher matcher = PLACEHOLDER_PATTERN.matcher(input);
        final StringBuilder resolvedString = new StringBuilder();
        while (matcher.find()) {
            final String placeholder = matcher.group();
            final Argument<String> resolved = foundPlaceholders.get(placeholder);
            if (resolved == null) {
                throw new QuestException("Could not resolve variable '" + placeholder + "'");
            }
            matcher.appendReplacement(resolvedString, Matcher.quoteReplacement(resolved.getValue(profile)));
        }
        matcher.appendTail(resolvedString);
        return resolvedString.toString();
    }

    private String replaceEscapedPercent(final String input) {
        return input.replaceAll("(?<!\\\\)\\\\%", "%");
    }

    @Override
    public T getValue(@Nullable final Profile profile) throws QuestException {
        return value.getValue(profile);
    }
}
