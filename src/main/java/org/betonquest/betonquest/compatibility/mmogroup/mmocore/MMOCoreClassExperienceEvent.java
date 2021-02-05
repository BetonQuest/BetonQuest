package org.betonquest.betonquest.compatibility.mmogroup.mmocore;

import net.Indyuce.mmocore.api.experience.EXPSource;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;

import java.util.UUID;

@SuppressWarnings("PMD.CommentRequired")
public class MMOCoreClassExperienceEvent extends QuestEvent {

    private final VariableNumber amountVar;
    private final boolean isLevel;

    public MMOCoreClassExperienceEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);

        amountVar = instruction.getVarNum();
        isLevel = instruction.hasArgument("level");
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final int amount = amountVar.getInt(playerID);
        final PlayerData mmoData = PlayerData.get(UUID.fromString(playerID));

        if (isLevel) {
            mmoData.giveLevels(amount, EXPSource.QUEST);
        } else {
            mmoData.giveExperience(amount, EXPSource.QUEST);
        }
        return null;
    }
}
