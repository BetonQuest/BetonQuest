package org.betonquest.betonquest.compatibility.nexo.item;

import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.kernel.TypeFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.item.QuestItemTagAdapterWrapper;
import org.betonquest.betonquest.item.QuestItemWrapper;

public class NexoItemFactory implements TypeFactory<QuestItemWrapper> {

    @Override
    public QuestItemWrapper parseInstruction(final Instruction instruction) throws QuestException {
        final NexoItemWrapper nexoItemWrapper = new NexoItemWrapper(instruction.get(Argument.STRING));
        if (instruction.hasArgument("quest-item")) {
            return new QuestItemTagAdapterWrapper(nexoItemWrapper);
        }
        return nexoItemWrapper;
    }
}
