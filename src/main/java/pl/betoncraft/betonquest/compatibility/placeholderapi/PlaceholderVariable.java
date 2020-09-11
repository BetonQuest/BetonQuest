package pl.betoncraft.betonquest.compatibility.placeholderapi;

import me.clip.placeholderapi.PlaceholderAPI;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Variable;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

public class PlaceholderVariable extends Variable {

    private String placeholder;

    public PlaceholderVariable(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        placeholder = instruction.next();
    }

    @Override
    public String getValue(final String playerID) {
        return PlaceholderAPI.setPlaceholders(PlayerConverter.getPlayer(playerID), '%' + placeholder + '%');
    }

}
