package pl.betoncraft.betonquest.compatibility.brewery;

import com.dre.brewery.BPlayer;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class DrunkQualityCondition extends Condition{

    private Integer quality;

    public DrunkQualityCondition(Instruction instruction) throws InstructionParseException {
        super(instruction);

        quality = instruction.getInt();

        if(quality < 1 || quality > 10){
            throw new InstructionParseException("Drunk quality can only be between 1 and 10!");
        }
    }

    @Override
    public boolean check(String playerID) throws QuestRuntimeException {
        BPlayer bPlayer = BPlayer.get(PlayerConverter.getPlayer(playerID));
        return bPlayer.getQuality() >= quality;
    }
}
