package org.betonquest.betonquest.id.placeholder;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.identifier.DefaultIdentifier;
import org.betonquest.betonquest.api.identifier.PlaceholderIdentifier;

/**
 * The default implementation for {@link PlaceholderIdentifier}s.
 */
public class DefaultPlaceholderIdentifier extends DefaultIdentifier implements PlaceholderIdentifier {

    /**
     * The instruction value of this identifier.
     */
    private final String instruction;

    /**
     * Creates a new placeholder identifier.
     *
     * @param pack        the package the identifier is related to
     * @param identifier  the identifier itself
     * @param instruction the instruction value of this identifier
     */
    protected DefaultPlaceholderIdentifier(final QuestPackage pack, final String identifier, final String instruction) {
        super(pack, identifier);
        this.instruction = instruction;
    }

    @Override
    public String getSection() {
        throw new UnsupportedOperationException("Placeholders do not have sections.");
    }

    @Override
    public String readRawInstruction() {
        return instruction;
    }
}
