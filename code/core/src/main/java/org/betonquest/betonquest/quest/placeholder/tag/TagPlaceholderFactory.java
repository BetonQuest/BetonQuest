package org.betonquest.betonquest.quest.placeholder.tag;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholderFactory;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;

/**
 * A factory for creating Tag placeholders.
 */
public class TagPlaceholderFactory extends AbstractTagPlaceholderFactory<PlayerDataStorage> implements PlayerPlaceholderFactory {

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * Creates a new TagPlaceholderFactory.
     *
     * @param dataHolder    the data holder
     * @param pluginMessage the {@link PluginMessage} instance
     */
    public TagPlaceholderFactory(final PlayerDataStorage dataHolder, final PluginMessage pluginMessage) {
        super(dataHolder);
        this.pluginMessage = pluginMessage;
    }

    @Override
    public PlayerPlaceholder parsePlayer(final Instruction instruction) throws QuestException {
        return new TagPlaceholder(pluginMessage, dataHolder, instruction.nextElement(), instruction.getPackage(),
                instruction.bool().getFlag("papiMode", true));
    }
}
