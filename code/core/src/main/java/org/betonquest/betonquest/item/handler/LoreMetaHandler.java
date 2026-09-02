package org.betonquest.betonquest.item.handler;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.profile.Profile;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Specific Handler for the {@link QuestItem#getLore()} method.
 */
public interface LoreMetaHandler extends ItemMetaHandler.Standard {

    @Override
    LoreAttribute parse(Instruction instruction) throws QuestException;

    /**
     * Specific attribute that resolves into {@link ResolvedLoreAttribute} for the {@link QuestItem#getLore()} method.
     */
    @FunctionalInterface
    interface LoreAttribute extends Attribute {

        @Override
        ResolvedLoreAttribute resolve(@Nullable Profile profile) throws QuestException;
    }

    /**
     * Specific resolved attribute for the {@link QuestItem#getLore()} method.
     */
    interface ResolvedLoreAttribute extends ResolvedAttribute.Standard {

        /**
         * Gets the lore.
         *
         * @return the list of lore lines, can be empty
         */
        List<Component> get();
    }
}
