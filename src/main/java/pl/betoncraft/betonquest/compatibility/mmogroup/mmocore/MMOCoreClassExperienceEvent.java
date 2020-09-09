package pl.betoncraft.betonquest.compatibility.mmogroup.mmocore;

import net.Indyuce.mmocore.api.experience.EXPSource;
import net.Indyuce.mmocore.api.player.PlayerData;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;

import java.util.UUID;

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
