package org.betonquest.betonquest.compatibility.mmogroup.mmocore;

import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.attribute.PlayerAttribute;
import net.Indyuce.mmocore.manager.AttributeManager;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.argument.Argument;

/**
 * Parses a string to an attribute.
 */
public class MMOAttributeParser implements Argument<PlayerAttribute> {
    /**
     * The default instance of {@link MMOAttributeParser}.
     */
    public static final MMOAttributeParser ATTRIBUTE = new MMOAttributeParser(MMOCore.plugin.attributeManager);

    /**
     * Manager to get attributes.
     */
    private final AttributeManager attributeManager;

    /**
     * Create a new Attribute parser.
     *
     * @param attributeManager the manager to get attributes
     */
    public MMOAttributeParser(final AttributeManager attributeManager) {
        this.attributeManager = attributeManager;
    }

    @Override
    public PlayerAttribute apply(final String attributeName) throws QuestException {
        final PlayerAttribute attribute = attributeManager.get(attributeName);
        if (attribute == null) {
            throw new QuestException("Attribute does not exist: " + attributeName);
        }
        return attribute;
    }
}
