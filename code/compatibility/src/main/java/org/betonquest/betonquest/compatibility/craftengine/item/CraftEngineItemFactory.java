package org.betonquest.betonquest.compatibility.craftengine.item;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.kernel.TypeFactory;
import org.betonquest.betonquest.compatibility.craftengine.CraftEngineParser;
import org.betonquest.betonquest.item.QuestItemTagAdapterWrapper;
import org.betonquest.betonquest.item.QuestItemWrapper;

/**
 * Factory for creating {@link QuestItemWrapper} from BetonQuest {@link Instruction}s
 * using the CraftEngine system.
 */
public class CraftEngineItemFactory implements TypeFactory<QuestItemWrapper> {

    /**
     * Default constructor for CraftEngineItemFactory.
     */
    public CraftEngineItemFactory() {
    }

    /**
     * Parses the instruction into a {@link QuestItemWrapper}.
     * @param instruction the instruction to parse
     * @return the wrapped quest item, optionally adapted for quest tags
     * @throws QuestException if parsing fails
     */
    @Override
    public QuestItemWrapper parseInstruction(final Instruction instruction) throws QuestException {
        final CraftEngineItemWrapper craftEngineItemWrapper = new CraftEngineItemWrapper(instruction.parse(CraftEngineParser.CRAFT_ENGINE_PARSER).get());
        final boolean questItem = instruction.bool().getFlag("quest-item", true)
                .getValue(null).orElse(false);
        if (questItem) {
            return new QuestItemTagAdapterWrapper(craftEngineItemWrapper);
        }
        return craftEngineItemWrapper;
    }
}
