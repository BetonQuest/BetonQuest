package org.betonquest.betonquest.compatibility.mmogroup.mmocore;

import net.Indyuce.mmocore.api.player.PlayerData;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

@SuppressWarnings("PMD.CommentRequired")
public class MMOCoreSkillPointsEvent extends QuestEvent {

    private final VariableNumber amountVar;

    public MMOCoreSkillPointsEvent(final Instruction instruction) throws QuestException {
        super(instruction, true);
        amountVar = instruction.get(VariableNumber::new);
    }

    @Override
    protected Void execute(final Profile profile) throws QuestException {
        final PlayerData data = PlayerData.get(profile.getPlayerUUID());
        final int amount = amountVar.getInt(profile);
        data.giveSkillPoints(amount);
        return null;
    }
}
