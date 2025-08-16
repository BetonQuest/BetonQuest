package org.betonquest.betonquest.quest.variable.tag;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariableFactory;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.database.GlobalData;

/**
 * A factory for creating GlobalTag variables.
 */
public class GlobalTagVariableFactory extends AbstractTagVariableFactory<GlobalData> implements PlayerlessVariableFactory {

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * Create a new GlobalTagVariableFactory.
     *
     * @param dataHolder    the data holder
     * @param pluginMessage the {@link PluginMessage} instance
     */
    public GlobalTagVariableFactory(final GlobalData dataHolder, final PluginMessage pluginMessage) {
        super(dataHolder);
        this.pluginMessage = pluginMessage;
    }

    @Override
    public PlayerlessVariable parsePlayerless(final Instruction instruction) throws QuestException {
        return new GlobalTagVariable(pluginMessage, dataHolder, instruction.next(), instruction.getPackage(), instruction.hasArgument("papiMode"));
    }
}
