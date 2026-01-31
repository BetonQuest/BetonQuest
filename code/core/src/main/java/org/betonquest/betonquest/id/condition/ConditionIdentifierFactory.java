package org.betonquest.betonquest.id.condition;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.identifier.factory.DefaultIdentifierFactory;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * A {@link DefaultIdentifierFactory} for {@link ConditionIdentifier}s.
 */
public class ConditionIdentifierFactory extends DefaultIdentifierFactory<ConditionIdentifier> {

    /**
     * Create a new identifier factory.
     *
     * @param packManager the quest package manager to resolve relative paths
     */
    public ConditionIdentifierFactory(final QuestPackageManager packManager) {
        super(packManager);
    }

    @Override
    public ConditionIdentifier parseIdentifier(@Nullable final QuestPackage source, final String input) throws QuestException {
        final boolean isInverted = !input.isEmpty() && input.charAt(0) == '!';
        final Map.Entry<QuestPackage, String> entry = parse(source, isInverted ? input.substring(1) : input);
        final DefaultConditionIdentifier identifier = new DefaultConditionIdentifier(entry.getKey(), entry.getValue(), isInverted);
        return requireInstruction(identifier, DefaultConditionIdentifier.CONDITION_SECTION);
    }
}
