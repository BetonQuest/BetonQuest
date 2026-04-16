package org.betonquest.betonquest.quest.placeholder.tag;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.Localizations;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerlessPlaceholderFactory;
import org.betonquest.betonquest.database.GlobalData;

/**
 * A factory for creating GlobalTag placeholders.
 */
public class GlobalTagPlaceholderFactory extends AbstractTagPlaceholderFactory<GlobalData> implements PlayerlessPlaceholderFactory {

    /**
     * The {@link Localizations} instance.
     */
    private final Localizations localizations;

    /**
     * Create a new GlobalTagPlaceholderFactory.
     *
     * @param dataHolder    the data holder
     * @param localizations the {@link Localizations} instance
     */
    public GlobalTagPlaceholderFactory(final GlobalData dataHolder, final Localizations localizations) {
        super(dataHolder);
        this.localizations = localizations;
    }

    @Override
    public PlayerlessPlaceholder parsePlayerless(final Instruction instruction) throws QuestException {
        return new GlobalTagPlaceholder(localizations, dataHolder, instruction.nextElement(), instruction.getPackage(),
                instruction.bool().getFlag("papiMode", true));
    }
}
