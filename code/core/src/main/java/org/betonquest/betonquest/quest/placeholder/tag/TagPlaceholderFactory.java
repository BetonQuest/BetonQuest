package org.betonquest.betonquest.quest.placeholder.tag;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.Localizations;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholderFactory;
import org.betonquest.betonquest.data.PlayerDataStorage;

/**
 * A factory for creating Tag placeholders.
 */
public class TagPlaceholderFactory extends AbstractTagPlaceholderFactory<PlayerDataStorage> implements PlayerPlaceholderFactory {

    /**
     * The {@link Localizations} instance.
     */
    private final Localizations localizations;

    /**
     * Creates a new TagPlaceholderFactory.
     *
     * @param dataHolder    the data holder
     * @param localizations the {@link Localizations} instance
     */
    public TagPlaceholderFactory(final PlayerDataStorage dataHolder, final Localizations localizations) {
        super(dataHolder);
        this.localizations = localizations;
    }

    @Override
    public PlayerPlaceholder parsePlayer(final Instruction instruction) throws QuestException {
        return new TagPlaceholder(localizations, dataHolder, instruction.nextElement(), instruction.getPackage(),
                instruction.bool().getFlag("papiMode", true));
    }
}
