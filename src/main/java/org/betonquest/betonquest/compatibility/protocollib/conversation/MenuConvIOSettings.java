package org.betonquest.betonquest.compatibility.protocollib.conversation;

/**
 * Menu conversation settings.
 */
public record MenuConvIOSettings(int configSelectionCooldown, int configRefreshDelay, int configLineLength,
                                 int configStartNewLines, boolean configNpcNameNewlineSeparator,
                                 boolean configNpcTextFillNewLines, String configControlSelect,
                                 String configControlCancel, String configControlMove, String configNpcNameAlign,
                                 String configNpcNameType, String configNpcWrap, String configNpcText,
                                 String configNpcTextReset, String configOptionWrap, String configOptionText,
                                 String configOptionTextReset, String configOptionSelected,
                                 String configOptionSelectedReset, String configOptionSelectedWrap,
                                 String configNpcNameFormat) {
}
