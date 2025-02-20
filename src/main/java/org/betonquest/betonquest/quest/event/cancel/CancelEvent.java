package org.betonquest.betonquest.quest.event.cancel;

import org.betonquest.betonquest.api.feature.FeatureAPI;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.feature.QuestCanceler;
import org.betonquest.betonquest.id.QuestCancelerID;

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
    private final QuestCancelerID cancelerID;

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
    public CancelEvent(final FeatureAPI featureAPI, final QuestCancelerID cancelerID, final boolean bypass) {
        this.featureAPI = featureAPI;
        this.cancelerID = cancelerID;
        this.bypass = bypass;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final QuestCanceler canceler = featureAPI.getCanceler(cancelerID);
        if (canceler == null) {
            throw new QuestException("Quest canceler '" + cancelerID.getFullID() + "' does not exist."
                    + " Ensure it was loaded without errors.");
        }
        canceler.cancel(profile, bypass);
    }
}
