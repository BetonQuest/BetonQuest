package org.betonquest.betonquest.compatibility.mmogroup.mmocore;

import net.Indyuce.mmocore.api.player.PlayerData;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

@SuppressWarnings("PMD.CommentRequired")
public class MMOCoreAttributeReallocationPointsEvent extends QuestEvent {

    private final VariableNumber amountVar;

    public MMOCoreAttributeReallocationPointsEvent(final Instruction instruction) throws QuestException {
        super(instruction, true);
        amountVar = instruction.getVarNum();
    }

    @Override
    protected Void execute(final Profile profile) throws QuestException {
        final PlayerData data = PlayerData.get(profile.getPlayerUUID());
        final int amount = amountVar.getInt(profile);
        data.giveAttributeReallocationPoints(amount);
        return null;
    }
}
