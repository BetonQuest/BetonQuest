package org.betonquest.betonquest.quest.variable.tag;

import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariableFactory;
import org.betonquest.betonquest.database.GlobalData;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.Instruction;

/**
 * A factory for creating GlobalTag variables.
 */
public class GlobalTagVariableFactory extends AbstractTagVariableFactory<GlobalData> implements PlayerlessVariableFactory {

    /**
     * Create a new GlobalTagVariableFactory.
     *
     * @param dataHolder the data holder
     */
    public GlobalTagVariableFactory(final GlobalData dataHolder) {
        super(dataHolder);
    }

    @Override
    public PlayerlessVariable parsePlayerless(final Instruction instruction) throws QuestException {
        return new GlobalTagVariable(dataHolder, instruction.next(), instruction.getPackage(), instruction.hasArgument("papiMode"));
    }
}
