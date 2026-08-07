package org.betonquest.betonquest.lib.instruction.argument;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.ValueParser;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.service.placeholder.PlaceholderManager;
import org.betonquest.betonquest.lib.instruction.tokenizer.Token;
import org.betonquest.betonquest.lib.instruction.tokenizer.TokenizerException;
import org.betonquest.betonquest.lib.instruction.tokenizer.placeholder.PlaceholderExtractor;
import org.betonquest.betonquest.lib.instruction.tokenizer.placeholder.PlaceholderExtractorSettings;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represent an argument that can be resolved to the given type.
 *
 * @param <T> the type of the argument
 */
public class DefaultArgument<T> implements Argument<T> {

    /**
     * The extractor of placeholders from a string.
     * Any syntactically relevant character can be escaped with a backslash.
     * Extracts placeholders and everything else.
     */
    private static final PlaceholderExtractor DEFAULT_PH_EXTRACTOR = new PlaceholderExtractor(PlaceholderExtractorSettings.DEFAULT);

    /**
     * The extractor of placeholders from a string.
     * Any syntactically relevant character can be escaped with a backslash.
     * Only extracts placeholders, ignores everything else.
     */
    private static final PlaceholderExtractor ONLY_PH_EXTRACTOR = new PlaceholderExtractor(PlaceholderExtractorSettings.ONLY_PLACEHOLDERS);

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
     * @param placeholders    the {@link PlaceholderManager} to create and resolve placeholders
     * @param pack            the package of the instruction in which the argument is used
     * @param input           the string that may contain placeholders
     * @param valueParser     the valueParser to convert the resolved argument to the given type
     * @param earlyValidation whether to validate the input early to discover bugs and mistakes on creation
     * @throws QuestException if the placeholders could not be created or resolved to the given type
     */
    public DefaultArgument(final PlaceholderManager placeholders, @Nullable final QuestPackage pack, final String input,
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
     * Forwards to {@link #DefaultArgument(PlaceholderManager, QuestPackage, String, ValueParser, boolean)}
     * with earlyValidation set to true by default.
     *
     * @param placeholders the {@link PlaceholderManager} to create and resolve placeholders
     * @param pack         the package of the instruction in which the argument is used
     * @param input        the string that may contain placeholders
     * @param valueParser  the valueParser to convert the resolved argument to the given type
     * @throws QuestException if the placeholders could not be created or resolved to the given type
     */
    public DefaultArgument(final PlaceholderManager placeholders, @Nullable final QuestPackage pack, final String input,
                           final ValueParser<T> valueParser) throws QuestException {
        this(placeholders, pack, input, valueParser, true);
    }

    private Map<String, Argument<String>> getPlaceholders(final PlaceholderManager placeholders, @Nullable final QuestPackage pack,
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

    private Set<String> resolvePlaceholders(final String input) throws QuestException {
        try {
            final Token[] tokens = ONLY_PH_EXTRACTOR.tokens(input);
            return Arrays.stream(tokens)
                    .map(Token::resolveValue)
                    .collect(Collectors.toSet());
        } catch (final TokenizerException e) {
            throw new QuestException("Failed to parse placeholders in '%s'".formatted(input), e);
        }
    }

    private String getString(final String input, final Map<String, Argument<String>> foundPlaceholders,
                             @Nullable final Profile profile) throws QuestException {
        try {
            final Token[] tokens = DEFAULT_PH_EXTRACTOR.tokens(input);
            final List<Argument<String>> list = Stream.of(tokens)
                    .map(Token::resolveValue)
                    .map(token -> foundPlaceholders.getOrDefault(token, new DefaultArgument<>(token)))
                    .toList();
            final StringBuilder builder = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                final Argument<String> argument = list.get(i);
                final String resolved = argument.getValue(profile);
                if (resolved == null) {
                    throw new QuestException("Could not resolve placeholder '%s'".formatted(tokens[i].resolveValue()));
                }
                builder.append(resolved);
            }
            return builder.toString();
        } catch (final TokenizerException e) {
            throw new QuestException("Failed to parse placeholders in '%s'".formatted(input), e);
        }
    }

    private String replaceEscapedPercent(final String input) {
        return input.replaceAll("(?<!\\\\)\\\\%", "%");
    }

    @Override
    public T getValue(@Nullable final Profile profile) throws QuestException {
        return value.getValue(profile);
    }
}
