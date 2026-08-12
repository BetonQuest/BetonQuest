package org.betonquest.betonquest.id;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.identifier.ReadableIdentifier;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Util class for common identifier methods.
 */
public final class IdentifierUtil {

    private IdentifierUtil() {
    }

    /**
     * Get from an identifier all "leaf" identifiers inside its {@link ConfigurationSection}.
     *
     * @param identifierFactory the factory to create new identifiers for the subsections
     * @param identifier        the identifier pointing to the wanted subsection
     * @param <I>               the identifier type
     * @return list of identifiers that are
     * @throws QuestException when there are no subsection for the given identifier
     */
    public static <I extends ReadableIdentifier> List<I> subsectionIdentifiers(
            final IdentifierFactory<I> identifierFactory, final I identifier) throws QuestException {
        final QuestPackage pack = identifier.getPackage();
        final String identifierSection = identifier.getSection();
        final ConfigurationSection section = pack.getConfig().getConfigurationSection(identifierSection);
        if (section == null) {
            throw new QuestException("There is no base section '%s' in pack '%s'".formatted(identifierSection, identifier.getPackage()));
        }
        final ConfigurationSection targetSection = section.getConfigurationSection(identifier.get());
        if (targetSection == null) {
            throw new QuestException("Target section '%s' does not exist (in base section '%s' in pack '%s')"
                    .formatted(identifier.get(), identifierSection, identifier.getPackage()));
        }
        final List<I> identifiers = new ArrayList<>();
        for (final Map.Entry<String, Object> entry : targetSection.getValues(true).entrySet()) {
            if (entry.getValue() instanceof ConfigurationSection) {
                continue;
            }
            identifiers.add(identifierFactory.parseIdentifier(pack, identifier.get() + "." + entry.getKey()));
        }
        return identifiers;
    }
}
