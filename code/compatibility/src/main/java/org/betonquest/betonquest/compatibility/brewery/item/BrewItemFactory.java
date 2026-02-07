package org.betonquest.betonquest.compatibility.brewery.item;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.item.QuestItemWrapper;
import org.betonquest.betonquest.api.quest.TypeFactory;
import org.betonquest.betonquest.compatibility.brewery.IdentifierType;
import org.betonquest.betonquest.item.QuestItemTagAdapterWrapper;

/**
 * Factory to create {@link BrewWrapper}s from {@link Instruction}s.
 */
public class BrewItemFactory implements TypeFactory<QuestItemWrapper> {

    /**
     * The empty default constructor.
     */
    public BrewItemFactory() {
    }

    @Override
    public QuestItemWrapper parseInstruction(final Instruction instruction) throws QuestException {
        final Argument<String> name = instruction.string().get();
        final Argument<Number> quality = instruction.number().get();
        final Argument<IdentifierType> mode = instruction.enumeration(IdentifierType.class).get("mode", IdentifierType.NAME);
        final BrewWrapper brewWrapper = new BrewWrapper(quality, name, mode);
        final boolean questItem = instruction.bool().getFlag("quest-item", true)
                .getValue(null).orElse(false);
        if (questItem) {
            return new QuestItemTagAdapterWrapper(brewWrapper);
        }
        return brewWrapper;
    }
}
