package org.betonquest.betonquest.quest.variable.tag;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;

/**
 * A factory for creating Tag variables.
 */
public class TagVariableFactory extends AbstractTagVariableFactory<PlayerDataStorage> implements PlayerVariableFactory {
    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * Creates a new TagVariableFactory.
     *
     * @param dataHolder    the data holder
     * @param pluginMessage the {@link PluginMessage} instance
     */
    public TagVariableFactory(final PlayerDataStorage dataHolder, final PluginMessage pluginMessage) {
        super(dataHolder);
        this.pluginMessage = pluginMessage;
    }

    @Override
    public PlayerVariable parsePlayer(final Instruction instruction) throws QuestException {
        return new TagVariable(pluginMessage, dataHolder, instruction.next(), instruction.getPackage(), instruction.hasArgument("papiMode"));
    }
}
