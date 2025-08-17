package org.betonquest.betonquest.api.quest.objective;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.InstructionIdentifier;
import org.betonquest.betonquest.api.quest.QuestException;
import org.jetbrains.annotations.Nullable;

/**
 * ID of an Objective.
 */
public class ObjectiveID extends InstructionIdentifier {

    /**
     * Create a new Objective ID.
     *
     * @param questPackageManager the quest package manager to use for the instruction
     * @param pack                the package of the objective
     * @param identifier          the complete identifier of the objective
     * @throws QuestException if there is no such objective
     */
    public ObjectiveID(final QuestPackageManager questPackageManager, @Nullable final QuestPackage pack, final String identifier) throws QuestException {
        super(questPackageManager, pack, identifier, "objectives", "Objective");
    }
}
