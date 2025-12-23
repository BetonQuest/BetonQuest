package org.betonquest.betonquest.id;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.SectionIdentifier;
import org.betonquest.betonquest.api.instruction.argument.parser.IdentifierParser;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a quest compass ID.
 */
public class CompassID extends SectionIdentifier {

    /**
     * Creates new QuestCompassID instance.
     *
     * @param packManager the quest package manager to get quest packages from
     * @param pack        the package where the identifier was used in
     * @param identifier  the identifier of the quest compass
     * @throws QuestException if the instruction could not be created or
     *                        when the quest compass could not be resolved with the given identifier
     */
    public CompassID(final QuestPackageManager packManager, @Nullable final QuestPackage pack, final String identifier) throws QuestException {
        super(packManager, pack, identifier, "compass", "Compass");
    }

    /**
     * Get the full path of the tag to indicate a quest compass should be shown.
     *
     * @return the compass tag
     */
    public String getTag() {
        return IdentifierParser.INSTANCE.apply(getPackage(), "compass-" + get());
    }
}
