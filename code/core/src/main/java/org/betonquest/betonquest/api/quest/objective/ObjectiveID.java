package org.betonquest.betonquest.api.quest.objective;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.InstructionIdentifier;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.jetbrains.annotations.Nullable;

/**
 * ID of an Objective.
 */
public class ObjectiveID extends InstructionIdentifier {

    /**
     * Create a new Objective ID.
     *
     * @param placeholders the {@link Placeholders} to create and resolve placeholders
     * @param packManager  the quest package manager to get quest packages from
     * @param pack         the package of the objective
     * @param identifier   the complete identifier of the objective
     * @throws QuestException if there is no such objective
     */
    public ObjectiveID(final Placeholders placeholders, final QuestPackageManager packManager, @Nullable final QuestPackage pack, final String identifier) throws QuestException {
        super(placeholders, packManager, pack, identifier, "objectives", "Objective");
    }
}
