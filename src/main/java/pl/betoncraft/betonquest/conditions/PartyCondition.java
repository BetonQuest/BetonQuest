package pl.betoncraft.betonquest.conditions;

import org.bukkit.Bukkit;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.VariableNumber;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.id.ConditionID;
import pl.betoncraft.betonquest.utils.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Checks the conditions for the whole party (including the player that started
 * the checking)
 */
@SuppressWarnings("PMD.CommentRequired")
public class PartyCondition extends Condition {

    private final VariableNumber range;
    private final ConditionID[] conditions;
    private final ConditionID[] everyone;
    private final ConditionID[] anyone;
    private final VariableNumber count;

    public PartyCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        range = instruction.getVarNum();
        conditions = instruction.getList(instruction::getCondition).toArray(new ConditionID[0]);
        everyone = instruction.getList(instruction.getOptional("every"), instruction::getCondition).toArray(new ConditionID[0]);
        anyone = instruction.getList(instruction.getOptional("any"), instruction::getCondition).toArray(new ConditionID[0]);
        count = instruction.getVarNum(instruction.getOptional("count"));
    }

    @Override
    protected Boolean execute(final String playerID) throws QuestRuntimeException {
        // get the party
        final List<String> members = Utils.getParty(playerID, range.getDouble(playerID), instruction.getPackage().getName(), conditions);
        // check every condition against every player - all of them must meet those conditions
        final Stream<String> partyStream = Bukkit.isPrimaryThread() ? members.stream() : members.parallelStream();
        if (!partyStream.allMatch(member -> BetonQuest.conditions(member, everyone))) {
            return false;
        }

        // check every condition against every player - every condition must be met by at least one player
        final Stream<ConditionID> anyoneStream = Bukkit.isPrimaryThread() ? Arrays.stream(anyone) : Arrays.stream(anyone).parallel();
        if (!anyoneStream.allMatch(condition -> {
            final Stream<String> memberStream = Bukkit.isPrimaryThread() ? members.stream() : members.parallelStream();
            return memberStream.anyMatch(member -> BetonQuest.condition(member, condition));
        })) {
            return false;
        }

        // if the count is more than 0, we need to check if there are more
        // players in the party than required minimum
        final int pCount = count == null ? 0 : count.getInt(playerID);
        return pCount <= 0 || members.size() >= pCount;
    }

}
