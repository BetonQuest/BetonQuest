package org.betonquest.betonquest.quest.placeholder.translate;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.placeholder.NullablePlaceholder;
import org.betonquest.betonquest.api.text.Text;
import org.jetbrains.annotations.Nullable;

/**
 * A placeholder that evaluates to a language identified text.
 */
public class TranslatePlaceholder implements NullablePlaceholder {

    /**
     * The text value.
     */
    private final Argument<Text> text;

    /**
     * Create a placeholder that always evaluates to a language identified text.
     *
     * @param text the text value
     */
    public TranslatePlaceholder(final Argument<Text> text) {
        this.text = text;
    }

    @Override
    public String getValue(@Nullable final Profile profile) throws QuestException {
        return text.getValue(profile).asRaw(profile);
    }
}
