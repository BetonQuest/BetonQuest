package org.betonquest.betonquest.item.handler;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.profile.Profile;
import org.jetbrains.annotations.Nullable;

/**
 * Specific Handler for the {@link QuestItem#getName()} method.
 */
public interface NameMetaHandler extends ItemMetaHandler.Standard {

    @Override
    NameAttribute parse(Instruction instruction) throws QuestException;

    /**
     * Specific attribute that resolves into {@link ResolvedNameAttribute} for the {@link QuestItem#getName()} method.
     */
    interface NameAttribute extends Attribute.Standard {

        @Override
        ResolvedNameAttribute resolve(@Nullable Profile profile) throws QuestException;
    }

    /**
     * Specific resolved attribute for the {@link QuestItem#getName()} method.
     */
    interface ResolvedNameAttribute extends ResolvedAttribute.Standard {

        /**
         * Get the name.
         *
         * @return the name
         */
        @Nullable
        Component get();
    }
}
