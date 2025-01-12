package org.betonquest.betonquest.compatibility.mmogroup.mmocore;

import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.experience.EXPSource;
import net.Indyuce.mmocore.experience.Profession;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

@SuppressWarnings("PMD.CommentRequired")
public class MMOCoreProfessionExperienceEvent extends QuestEvent {
    private final Profession profession;

    private final VariableNumber amountVar;

    private final boolean isLevel;

    public MMOCoreProfessionExperienceEvent(final Instruction instruction) throws QuestException {
        super(instruction, true);

        final String professionName = instruction.next();
        if (MMOCore.plugin.professionManager.has(professionName)) {
            profession = MMOCore.plugin.professionManager.get(professionName);
        } else {
            throw new QuestException("The profession could not be found!");
        }

        amountVar = instruction.getVarNum();
        isLevel = instruction.hasArgument("level");
    }

    @Override
    protected Void execute(final Profile profile) throws QuestException {
        final int amount = amountVar.getInt(profile);
        final PlayerData mmoData = PlayerData.get(profile.getPlayerUUID());

        if (isLevel) {
            mmoData.getCollectionSkills().giveLevels(profession, amount, EXPSource.QUEST);
        } else {
            mmoData.getCollectionSkills().giveExperience(profession, amount, EXPSource.QUEST);
        }
        return null;
    }
}
