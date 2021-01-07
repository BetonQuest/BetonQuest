package pl.betoncraft.betonquest.conditions;

import org.bukkit.Bukkit;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.id.ConditionID;

import java.util.List;
import java.util.stream.Stream;

/**
 * One of specified conditions has to be true
 */
@SuppressWarnings("PMD.CommentRequired")
public class AlternativeCondition extends Condition {

    private final List<ConditionID> conditions;

    public AlternativeCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        conditions = instruction.getList(instruction::getCondition);
    }

    @Override
    protected Boolean execute(final String playerID) {
        return BetonQuest.conditions(playerID, conditions);
    }
}
