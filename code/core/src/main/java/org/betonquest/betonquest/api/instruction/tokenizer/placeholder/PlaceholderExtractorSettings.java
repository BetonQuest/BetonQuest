package org.betonquest.betonquest.api.instruction.tokenizer.placeholder;

/**
 * Settings for the placeholder extractor.
 *
 * @param placeholderBrackets the brackets that surround the placeholder
 * @param escapeCharacter     the escape character
 * @param openQuote           the opening quote character
 * @param closeQuote          the closing quote character
 */
public record PlaceholderExtractorSettings(char placeholderBrackets, char escapeCharacter, char openQuote,
                                           char closeQuote) {

    /**
     * The default settings for the placeholder extractor.
     */
    public static final PlaceholderExtractorSettings DEFAULT = new PlaceholderExtractorSettings('%', '\\', '{', '}');
}
