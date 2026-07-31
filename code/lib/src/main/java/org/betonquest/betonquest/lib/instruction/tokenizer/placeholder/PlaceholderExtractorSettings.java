package org.betonquest.betonquest.lib.instruction.tokenizer.placeholder;

/**
 * Settings for the placeholder extractor.
 *
 * @param placeholderBrackets      the brackets that surround the placeholder
 * @param escapeCharacter          the escape character
 * @param openQuote                the opening quote character
 * @param closeQuote               the closing quote character
 * @param parseNonPlaceholderWords if non-placeholder words should be parsed
 */
public record PlaceholderExtractorSettings(char placeholderBrackets, char escapeCharacter, char openQuote,
                                           char closeQuote, boolean parseNonPlaceholderWords) {

    /**
     * The default settings for the placeholder extractor.
     */
    public static final PlaceholderExtractorSettings DEFAULT = new PlaceholderExtractorSettings('%', '\\', '{', '}', true);

    /**
     * The settings for the placeholder extractor that only extracts placeholders.
     */
    public static final PlaceholderExtractorSettings ONLY_PLACEHOLDERS = new PlaceholderExtractorSettings('%', '\\', '{', '}', false);
}
