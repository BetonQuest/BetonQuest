package pl.betoncraft.betonquest.events;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;

/**
 * Changes player's language.
 */
public class LanguageEvent extends QuestEvent {

    private final String lang;

    public LanguageEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        lang = instruction.next();
        if (!Config.getLanguages().contains(lang)) {
            throw new InstructionParseException("Language " + lang + " does not exists");
        }
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        BetonQuest.getInstance().getPlayerData(playerID).setLanguage(lang);
        return null;
    }

}
