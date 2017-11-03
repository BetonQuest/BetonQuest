package pl.betoncraft.betonquest.compatibility.brewery;

import com.dre.brewery.BPlayer;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class DrunkCondition extends Condition{

    private Integer drunkness;

    public DrunkCondition(Instruction instruction) throws InstructionParseException {
        super(instruction);

        drunkness = instruction.getInt();

        if(drunkness < 0 || drunkness > 100){
            throw new InstructionParseException("Drunkness can only be between 0 and 100!");
        }
    }

    @Override
    public boolean check(String playerID) throws QuestRuntimeException {
        BPlayer bPlayer = BPlayer.get(PlayerConverter.getPlayer(playerID));
        return bPlayer != null && bPlayer.getDrunkeness() >= drunkness;
    }
}
