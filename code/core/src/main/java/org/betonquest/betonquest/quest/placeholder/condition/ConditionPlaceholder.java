package org.betonquest.betonquest.quest.placeholder.condition;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.Localizations;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.api.service.condition.ConditionManager;

/**
 * Get the "fulfillment" status of a quest condition.
 */
public class ConditionPlaceholder implements PlayerPlaceholder {

    /**
     * The {@link Localizations} instance.
     */
    private final Localizations localizations;

    /**
     * Condition to check.
     */
    private final Argument<ConditionIdentifier> conditionId;

    /**
     * If the placeholder should be in PAPI style.
     */
    private final FlagArgument<Boolean> papiMode;

    /**
     * The condition manager.
     */
    private final ConditionManager conditionManager;

    /**
     * Create a new Condition placeholder.
     *
     * @param localizations    the {@link Localizations} instance
     * @param conditionId      the condition to get the "fulfillment" status
     * @param conditionManager the condition manager
     * @param papiMode         if the return value should be in PAPI mode as defined in the documentation
     */
    public ConditionPlaceholder(final Localizations localizations, final Argument<ConditionIdentifier> conditionId,
                                final ConditionManager conditionManager, final FlagArgument<Boolean> papiMode) {
        this.localizations = localizations;
        this.conditionId = conditionId;
        this.conditionManager = conditionManager;
        this.papiMode = papiMode;
    }

    @Override
    public String getValue(final Profile profile) throws QuestException {
        final boolean papiMode = this.papiMode.getValue(profile).orElse(false);
        if (conditionManager.test(profile, conditionId.getValue(profile))) {
            return papiMode ? LegacyComponentSerializer.legacySection().serialize(localizations.getMessage(profile, "condition_placeholder_met")) : "true";
        }
        return papiMode ? LegacyComponentSerializer.legacySection().serialize(localizations.getMessage(profile, "condition_placeholder_not_met")) : "false";
    }
}
