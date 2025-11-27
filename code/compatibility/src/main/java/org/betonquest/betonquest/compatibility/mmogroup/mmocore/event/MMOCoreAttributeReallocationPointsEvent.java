package org.betonquest.betonquest.compatibility.mmogroup.mmocore.event;

import net.Indyuce.mmocore.api.player.PlayerData;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;

/**
 * Event to add attribute reallocation points to a player.
 */
public class MMOCoreAttributeReallocationPointsEvent implements PlayerEvent {

    /**
     * Amount to grant.
     */
    private final Variable<Number> amountVar;

    /**
     * Create a new attribute reallocation point event.
     *
     * @param amount the amount to grant
     */
    public MMOCoreAttributeReallocationPointsEvent(final Variable<Number> amount) {
        this.amountVar = amount;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final PlayerData data = PlayerData.get(profile.getPlayerUUID());
        final int amount = amountVar.getValue(profile).intValue();
        data.giveAttributeReallocationPoints(amount);
    }
}
