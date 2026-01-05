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
     * Parses the instruction into a {@link NexoItemWrapper}.
     *
     * @param instruction the instruction to parse
     * @return the wrapped Nexo item
     * @throws QuestException if parsing fails
     */
    @Override
    public QuestItemWrapper parseInstruction(final Instruction instruction) throws QuestException {
        final NexoItemWrapper nexoItemWrapper = new NexoItemWrapper(instruction.parse(NexoParser.NEXO_PARSER).get());
        final boolean questItem = instruction.bool().getFlag("quest-item", true)
                .getValue(null).orElse(false);
        if (questItem) {
            return new QuestItemTagAdapterWrapper(nexoItemWrapper);
        }
        return nexoItemWrapper;
    }
}
