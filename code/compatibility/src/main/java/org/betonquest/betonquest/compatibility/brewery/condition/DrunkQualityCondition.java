package org.betonquest.betonquest.compatibility.brewery.condition;

import com.dre.brewery.BPlayer;
import com.dre.brewery.api.BreweryApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.online.OnlineCondition;
import org.betonquest.betonquest.compatibility.brewery.BreweryUtils;

/**
 * If a player has reached a certain level of drunkenness.
 */
public class DrunkQualityCondition implements OnlineCondition {

    /**
     * The drunkenness level.
     */
    private final Argument<Number> qualityVar;

    /**
     * Create a new Drunk Condition.
     *
     * @param qualityVar the drunkenness level.
     */
    public DrunkQualityCondition(final Argument<Number> qualityVar) {
        this.qualityVar = qualityVar;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        final int quality = qualityVar.getValue(profile).intValue();
        BreweryUtils.validateQualityOrThrow(quality);

        final BPlayer bPlayer = BreweryApi.getBPlayer(profile.getPlayer());
        return bPlayer != null && bPlayer.getQuality() >= quality;
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
