package org.betonquest.betonquest.lib.instruction.argument;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.ValueParser;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Represent an argument that can be resolved to the given type.
 *
 * @param <T> the type of the argument
 */
public class DefaultArgument<T> implements Argument<T> {

    /**
     * The pattern to match placeholders in a string marked with percent signs.<br>
     * The percentage can be escaped with a backslash, and the backslash can be escaped with another backslash.
     */
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("(?<!\\\\)(?:\\\\\\\\)*(%((?:[^%\\\\]|\\\\.)*?)%)(?<!\\\\)(?:\\\\\\\\)*");

    /**
     * Supplier of the argument value.
     */
    private final Argument<T> value;

    /**
     * Creates a constant argument.
     *
     * @param value the value of the argument
     */
    public DefaultArgument(final T value) {
        this.value = profile -> value;
    }

    /**
     * Resolves a string that may contain placeholders to an {@link Argument} of the given type.
     *
     * @param placeholders    the {@link Placeholders} to create and resolve placeholders
     * @param pack            the package of the instruction in which the argument is used
     * @param input           the string that may contain placeholders
     * @param valueParser     the valueParser to convert the resolved argument to the given type
     * @param earlyValidation whether to validate the input early to discover bugs and mistakes on creation
     * @throws QuestException if the placeholders could not be created or resolved to the given type
     */
    public DefaultArgument(final Placeholders placeholders, @Nullable final QuestPackage pack, final String input,
                           final ValueParser<T> valueParser, final boolean earlyValidation) throws QuestException {
        final Map<String, Argument<String>> foundPlaceholders = getPlaceholders(placeholders, pack, input);
        if (foundPlaceholders.isEmpty()) {
            final String escapedInput = replaceEscapedPercent(input);
            if (earlyValidation) {
                valueParser.apply(escapedInput);
            }
            value = profile -> valueParser.apply(escapedInput);
        } else {
            value = profile -> valueParser.apply(replaceEscapedPercent(getString(input, foundPlaceholders, profile)));
        }
    }

    /**
     * Resolves a string that may contain placeholders to an {@link Argument} of the given type.
     * Forwards to {@link #DefaultArgument(Placeholders, QuestPackage, String, ValueParser, boolean)}
     * with earlyValidation set to true by default.
     *
     * @param placeholders the {@link Placeholders} to create and resolve placeholders
     * @param pack         the package of the instruction in which the argument is used
     * @param input        the string that may contain placeholders
     * @param valueParser  the valueParser to convert the resolved argument to the given type
     * @throws QuestException if the placeholders could not be created or resolved to the given type
     */
    public DefaultArgument(final Placeholders placeholders, @Nullable final QuestPackage pack, final String input,
                           final ValueParser<T> valueParser) throws QuestException {
        this(placeholders, pack, input, valueParser, true);
    }

    private Map<String, Argument<String>> getPlaceholders(final Placeholders placeholders, @Nullable final QuestPackage pack,
                                                          final String input)
            throws QuestException {
        final Map<String, Argument<String>> foundPlaceholders = new HashMap<>();
        for (final String placeholder : resolvePlaceholders(input)) {
            try {
                final Argument<String> placeholderArgument = placeholders.create(pack, replaceEscapedPercent(placeholder));
                foundPlaceholders.put(placeholder, placeholderArgument);
            } catch (final QuestException exception) {
                throw new QuestException("Could not create placeholder '" + placeholder + "': "
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
                throw new QuestException("Could not resolve placeholder '" + placeholder + "'");
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
