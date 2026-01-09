package org.betonquest.betonquest.compatibility.nexo.item;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.kernel.TypeFactory;
import org.betonquest.betonquest.compatibility.nexo.NexoParser;
import org.betonquest.betonquest.item.QuestItemTagAdapterWrapper;
import org.betonquest.betonquest.item.QuestItemWrapper;

/**
 * Factory for creating {@link QuestItemWrapper} from Nexo items.
 */
public class NexoItemFactory implements TypeFactory<QuestItemWrapper> {

    /**
     * The empty default constructor.
     */
    public NexoItemFactory() {
        // Empty
    }

    @Override
    public QuestItemWrapper parseInstruction(final Instruction instruction) throws QuestException {
        final NexoItemWrapper wrapper = new NexoItemWrapper(instruction.parse(NexoParser.NEXO_PARSER).get());
        final boolean isQuestItem = instruction.bool().getFlag("quest-item", true)
                .getValue(null).orElse(false);
        return isQuestItem ? new QuestItemTagAdapterWrapper(wrapper) : wrapper;
    }
}
