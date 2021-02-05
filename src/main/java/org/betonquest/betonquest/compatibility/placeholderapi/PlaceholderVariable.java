package org.betonquest.betonquest.compatibility.placeholderapi;

import me.clip.placeholderapi.PlaceholderAPI;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;

@SuppressWarnings("PMD.CommentRequired")
public class PlaceholderVariable extends Variable {

    private final String placeholder;

    public PlaceholderVariable(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        placeholder = instruction.getInstruction().substring(3);
    }

    @Override
    public String getValue(final String playerID) {
        return PlaceholderAPI.setPlaceholders(PlayerConverter.getPlayer(playerID), '%' + placeholder + '%');
    }

}
