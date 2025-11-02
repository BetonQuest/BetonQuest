package org.betonquest.betonquest.compatibility.mmogroup.mmocore.event;

import net.Indyuce.mmocore.api.player.PlayerData;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;

/**
 * Event to add attribute points to a player.
 */
public class MMOCoreAttributePointsEvent implements PlayerEvent {

    /**
     * Amount to grant.
     */
    private final Variable<Number> amountVar;

    /**
     * Create a new attribute point event.
     *
     * @param amount the amount to grant
     */
    public MMOCoreAttributePointsEvent(final Variable<Number> amount) {
        amountVar = amount;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final PlayerData data = PlayerData.get(profile.getPlayerUUID());
        final int amount = amountVar.getValue(profile).intValue();
        data.giveAttributePoints(amount);
    }
}
