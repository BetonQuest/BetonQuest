package org.betonquest.betonquest.compatibility.mmogroup.mmocore;

import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.experience.EXPSource;
import net.Indyuce.mmocore.api.experience.Profession;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

import java.util.UUID;

@SuppressWarnings("PMD.CommentRequired")
public class MMOCoreProfessionExperienceEvent extends QuestEvent {

    private final Profession profession;
    private final VariableNumber amountVar;
    private final boolean isLevel;

    public MMOCoreProfessionExperienceEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);

        final String professionName = instruction.next();
        if (MMOCore.plugin.professionManager.has(professionName)) {
            profession = MMOCore.plugin.professionManager.get(professionName);
        } else {
            throw new InstructionParseException("The profession could not be found!");
        }

        amountVar = instruction.getVarNum();
        isLevel = instruction.hasArgument("level");
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final int amount = amountVar.getInt(playerID);
        final PlayerData mmoData = PlayerData.get(UUID.fromString(playerID));

        if (isLevel) {
            mmoData.getCollectionSkills().giveLevels(profession, amount, EXPSource.QUEST);
        } else {
            mmoData.getCollectionSkills().giveExperience(profession, amount, EXPSource.QUEST);
        }
        return null;
    }
}
