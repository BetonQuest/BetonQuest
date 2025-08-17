package org.betonquest.betonquest.api.identifier;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.quest.QuestException;
import org.jetbrains.annotations.Nullable;

/**
 * Identifier for a section in a {@link QuestPackage}.
 * This class is used to ensure that the identifier corresponds to a valid section in the {@link QuestPackage}.
 */
public class SectionIdentifier extends Identifier {
    /**
     * Creates a new section identifier,
     * ensuring that the identifier corresponds to a valid section in the package's configuration.
     *
     * @param questPackageManager the quest package manager to use for the instruction
     * @param pack                the package the ID is in
     * @param identifier          the identifier string leading to the object
     * @param section             the section of the config file
     * @param readable            the readable name of the object type
     * @throws QuestException if the identifier could not be parsed
     */
    protected SectionIdentifier(final QuestPackageManager questPackageManager, @Nullable final QuestPackage pack, final String identifier, final String section, final String readable) throws QuestException {
        super(questPackageManager, pack, identifier);
        if (!super.getPackage().getConfig().isConfigurationSection(section + SEPERATOR + super.get())) {
            throw new QuestException(readable + " '" + super.getFull() + "' is not defined");
        }
    }
}
