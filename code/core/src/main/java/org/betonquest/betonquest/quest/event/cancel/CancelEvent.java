package org.betonquest.betonquest.quest.event.cancel;

import org.betonquest.betonquest.api.feature.FeatureAPI;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.id.QuestCancelerID;
import org.betonquest.betonquest.instruction.variable.Variable;

/**
 * The cancel event.
 */
public class CancelEvent implements OnlineEvent {

    /**
     * Feature API.
     */
    private final FeatureAPI featureAPI;

    /**
     * The canceler to use.
     */
    private final Variable<QuestCancelerID> cancelerID;

    /**
     * Whether the canceler conditions should be ignored for canceling.
     */
    private final boolean bypass;

    /**
     * Creates a new cancel event.
     *
     * @param featureAPI the feature API
     * @param cancelerID the canceler to use
     * @param bypass     whether the canceler conditions should be ignored for canceling
     */
    public CancelEvent(final FeatureAPI featureAPI, final Variable<QuestCancelerID> cancelerID, final boolean bypass) {
        this.featureAPI = featureAPI;
        this.cancelerID = cancelerID;
        this.bypass = bypass;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        featureAPI.getCanceler(cancelerID.getValue(profile)).cancel(profile, bypass);
    }
}
