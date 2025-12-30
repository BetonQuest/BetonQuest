package org.betonquest.betonquest.quest.event.cancel;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.feature.FeatureApi;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.id.QuestCancelerID;

/**
 * The cancel event.
 */
public class CancelEvent implements OnlineEvent {

    /**
     * The feature API.
     */
    private final FeatureApi featureApi;

    /**
     * The canceler to use.
     */
    private final Argument<QuestCancelerID> cancelerID;

    /**
     * Whether the canceler conditions should be ignored for canceling.
     */
    private final FlagArgument<Boolean> bypass;

    /**
     * Creates a new cancel event.
     *
     * @param featureApi the feature API
     * @param cancelerID the canceler to use
     * @param bypass     whether the canceler conditions should be ignored for canceling
     */
    public CancelEvent(final FeatureApi featureApi, final Argument<QuestCancelerID> cancelerID, final FlagArgument<Boolean> bypass) {
        this.featureApi = featureApi;
        this.cancelerID = cancelerID;
        this.bypass = bypass;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        featureApi.getCanceler(cancelerID.getValue(profile)).cancel(profile, bypass.getValue(profile).orElse(false));
    }
}
