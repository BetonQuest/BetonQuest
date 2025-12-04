package org.betonquest.betonquest.id;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.InstructionIdentifier;
import org.betonquest.betonquest.api.quest.Variables;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a journal entry ID.
 */
public class JournalEntryID extends InstructionIdentifier {

    /**
     * Creates new JournalEntryID instance.
     *
     * @param variables   the variable processor to create and resolve variables
     * @param packManager the quest package manager to get quest packages from
     * @param pack        the package where the identifier was used in
     * @param identifier  the identifier of the quest compass
     * @throws QuestException if the instruction could not be created or
     *                        when the journal entry could not be resolved with the given identifier
     */
    public JournalEntryID(final Variables variables, final QuestPackageManager packManager, @Nullable final QuestPackage pack, final String identifier) throws QuestException {
        super(variables, packManager, pack, identifier, "journal", "Journal Entry");
    }
}
