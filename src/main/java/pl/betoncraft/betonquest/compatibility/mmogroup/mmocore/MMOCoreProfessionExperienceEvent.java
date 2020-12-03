package pl.betoncraft.betonquest.compatibility.mmogroup.mmocore;

import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.experience.EXPSource;
import net.Indyuce.mmocore.api.experience.Profession;
import net.Indyuce.mmocore.api.player.PlayerData;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;

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
