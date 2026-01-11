package org.betonquest.betonquest.api.identifier;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.bukkit.config.custom.multi.MultiConfiguration;
import org.betonquest.betonquest.api.config.quest.QuestPackage;

/**
 * A default implementation of the {@link ReadableIdentifier}.
 */
public abstract class DefaultReadableIdentifier extends DefaultIdentifier implements ReadableIdentifier {

    /**
     * The section in the configuration where the identifier is defined.
     */
    private final String section;

    /**
     * Creates a new identifier.
     *
     * @param pack       the package this identifier belongs to
     * @param identifier the identifier without the package name
     * @param section    the section in the configuration where the identifier is defined
     * @throws QuestException if the identifier is not defined in the configuration
     */
    protected DefaultReadableIdentifier(final QuestPackage pack, final String identifier, final String section) throws QuestException {
        super(pack, identifier);
        this.section = section;
        final MultiConfiguration config = pack.getConfig();
        if (!config.isString(section + config.options().pathSeparator() + super.get())) {
            throw new QuestException("'%s' is not defined as string in section '%s'".formatted(super.getFull(), section));
        }
    }

    @Override
    public String getSection() {
        return section;
    }

    @Override
    public String readRawInstruction() throws QuestException {
        final MultiConfiguration config = getPackage().getConfig();
        final String rawInstruction = config.getString(section + config.options().pathSeparator() + get());
        if (rawInstruction == null) {
            throw new QuestException("'%s' is not defined in section '%s'".formatted(getFull(), section));
        }
        return rawInstruction;
    }
}
