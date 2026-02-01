package org.betonquest.betonquest.compatibility.brewery.condition;

import com.dre.brewery.BPlayer;
import com.dre.brewery.api.BreweryApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.condition.OnlineCondition;

/**
 * If a player has reached a certain level of drunkenness.
 */
public class DrunkCondition implements OnlineCondition {

    /**
     * The drunkenness level.
     */
    private final Argument<Number> drunk;

    /**
     * Create a new Drunk Condition.
     *
     * @param drunk the drunkenness level.
     */
    public DrunkCondition(final Argument<Number> drunk) {
        this.drunk = drunk;
    }

    @Override
    public boolean check(final OnlineProfile profile) throws QuestException {
        final int drunkenness = drunk.getValue(profile).intValue();
        if (drunkenness < 0 || drunkenness > 100) {
            throw new QuestException("Drunkenness can only be between 0 and 100!");
        }

        final BPlayer bPlayer = BreweryApi.getBPlayer(profile.getPlayer());
        return bPlayer != null && bPlayer.getDrunkeness() >= drunkenness;
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
