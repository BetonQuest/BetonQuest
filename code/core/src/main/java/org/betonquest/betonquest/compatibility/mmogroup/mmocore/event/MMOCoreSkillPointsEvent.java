package org.betonquest.betonquest.compatibility.mmogroup.mmocore.event;

import net.Indyuce.mmocore.api.player.PlayerData;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;

/**
 * Event to add skill points to a player.
 */
public class MMOCoreSkillPointsEvent implements PlayerEvent {

    /**
     * Amount to grant.
     */
    private final Variable<Number> amountVar;

    /**
     * Create a new skill point add event.
     *
     * @param amount the amount to grant
     */
    public MMOCoreSkillPointsEvent(final Variable<Number> amount) {
        this.amountVar = amount;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final PlayerData data = PlayerData.get(profile.getPlayerUUID());
        final int amount = amountVar.getValue(profile).intValue();
        data.giveSkillPoints(amount);
    }
}
