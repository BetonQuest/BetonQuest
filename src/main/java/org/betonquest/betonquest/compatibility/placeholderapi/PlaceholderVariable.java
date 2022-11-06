package org.betonquest.betonquest.compatibility.placeholderapi;

import me.clip.placeholderapi.PlaceholderAPI;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;

@SuppressWarnings("PMD.CommentRequired")
public class PlaceholderVariable extends Variable {

    private final String placeholder;

    public PlaceholderVariable(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        placeholder = instruction.getInstruction().substring(3);
    }

    @Override
    public String getValue(final Profile profile) {
        return PlaceholderAPI.setPlaceholders(profile.getOnlineProfile().get().getPlayer(), '%' + placeholder + '%');
    }

}
