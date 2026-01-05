package org.betonquest.betonquest.compatibility.craftengine.item;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.kernel.TypeFactory;
import org.betonquest.betonquest.compatibility.craftengine.CraftEngineParser;
import org.betonquest.betonquest.item.QuestItemTagAdapterWrapper;
import org.betonquest.betonquest.item.QuestItemWrapper;

public class CraftEngineItemFactory implements TypeFactory<QuestItemWrapper> {

    @Override
    public QuestItemWrapper parseInstruction(final Instruction instruction) throws QuestException {
        final CraftEngineItemWapper craftEngineItemWapper = new CraftEngineItemWapper(instruction.parse(CraftEngineParser.CRAFT_ENGINE_PARSER).get());
        final boolean questItem = instruction.bool().getFlag("quest-item", true)
                .getValue(null).orElse(false);
        if (questItem) {
            return new QuestItemTagAdapterWrapper(craftEngineItemWapper);
        }
        return craftEngineItemWapper;
    }
}
