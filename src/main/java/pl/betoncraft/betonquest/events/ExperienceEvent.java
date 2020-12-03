package pl.betoncraft.betonquest.events;

import org.bukkit.entity.Player;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.QuestEvent;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.PlayerConverter;

/**
 * Gives the player specified amount of experience
 */
@SuppressWarnings("PMD.CommentRequired")
public class ExperienceEvent extends QuestEvent {

    private final VariableNumber amount;
    private final boolean checkForLevel;

    public ExperienceEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        this.amount = instruction.getVarNum();
        this.checkForLevel = instruction.hasArgument("level");
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final Player player = PlayerConverter.getPlayer(playerID);
        final int amount = this.amount.getInt(playerID);
        if (checkForLevel) {
            player.giveExpLevels(amount);
        } else {
            player.giveExp(amount);
        }
        return null;
    }
}
