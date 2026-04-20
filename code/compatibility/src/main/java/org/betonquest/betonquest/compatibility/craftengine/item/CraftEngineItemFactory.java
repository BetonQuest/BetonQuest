package org.betonquest.betonquest.compatibility.craftengine.item;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.item.QuestItemWrapper;
import org.betonquest.betonquest.api.quest.TypeFactory;
import org.betonquest.betonquest.compatibility.craftengine.CraftEngineParser;

/**
 * Factory for creating {@link QuestItemWrapper} from BetonQuest {@link Instruction}s
 * using the CraftEngine system.
 */
public class CraftEngineItemFactory implements TypeFactory<QuestItemWrapper> {

    /**
     * The empty default constructor.
     */
    public CraftEngineItemFactory() {
        // Empty
    }

    @Override
    public QuestItemWrapper parseInstruction(final Instruction instruction) throws QuestException {
        return new CraftEngineItemWrapper(instruction.parse(CraftEngineParser.CRAFT_ENGINE_PARSER).get());
    }
}
