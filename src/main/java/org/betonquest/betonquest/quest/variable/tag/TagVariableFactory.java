package org.betonquest.betonquest.quest.variable.tag;

import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.modules.data.PlayerDataStorage;

/**
 * A factory for creating Tag variables.
 */
public class TagVariableFactory extends AbstractTagVariableFactory<PlayerDataStorage> implements PlayerVariableFactory {

    /**
     * Creates a new TagVariableFactory.
     *
     * @param dataHolder the data holder
     */
    public TagVariableFactory(final PlayerDataStorage dataHolder) {
        super(dataHolder);
    }

    @Override
    public PlayerVariable parsePlayer(final Instruction instruction) throws QuestException {
        return new TagVariable(dataHolder, instruction.next(), instruction.getPackage(), instruction.hasArgument("papiMode"));
    }
}
