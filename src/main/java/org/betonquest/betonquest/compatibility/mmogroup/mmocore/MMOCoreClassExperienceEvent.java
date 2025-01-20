package org.betonquest.betonquest.compatibility.mmogroup.mmocore;

import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.experience.EXPSource;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

@SuppressWarnings("PMD.CommentRequired")
public class MMOCoreClassExperienceEvent extends QuestEvent {
    private final VariableNumber amountVar;

    private final boolean isLevel;

    public MMOCoreClassExperienceEvent(final Instruction instruction) throws QuestException {
        super(instruction, true);

        amountVar = instruction.get(VariableNumber::new);
        isLevel = instruction.hasArgument("level");
    }

    @Override
    protected Void execute(final Profile profile) throws QuestException {
        final int amount = amountVar.getInt(profile);
        final PlayerData mmoData = PlayerData.get(profile.getPlayerUUID());

        if (isLevel) {
            mmoData.giveLevels(amount, EXPSource.QUEST);
        } else {
            mmoData.giveExperience(amount, EXPSource.QUEST);
        }
        return null;
    }
}
