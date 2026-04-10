package org.betonquest.betonquest.quest.placeholder.condition;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholderFactory;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.config.Translations;

/**
 * Factory to create {@link ConditionPlaceholder}s from {@link Instruction}s.
 */
public class ConditionPlaceholderFactory implements PlayerPlaceholderFactory {

    /**
     * The {@link PluginMessage} instance.
     */
    private final Translations translations;

    /**
     * The condition manager.
     */
    private final ConditionManager conditionManager;

    /**
     * Create the Condition Placeholder Factory.
     *
     * @param conditionManager the condition manager
     * @param translations     the {@link PluginMessage} instance
     */
    public ConditionPlaceholderFactory(final ConditionManager conditionManager, final Translations translations) {
        this.conditionManager = conditionManager;
        this.translations = translations;
    }

    @Override
    public PlayerPlaceholder parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<ConditionIdentifier> conditionId = instruction.identifier(ConditionIdentifier.class).get();
        final FlagArgument<Boolean> papiMode = instruction.bool().getFlag("papiMode", true);
        return new ConditionPlaceholder(translations, conditionId, conditionManager, papiMode);
    }
}
