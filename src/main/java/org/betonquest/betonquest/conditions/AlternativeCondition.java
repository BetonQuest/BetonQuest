package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.utils.LogUtils;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * One of specified conditions has to be true
 */
@SuppressWarnings("PMD.CommentRequired")
public class AlternativeCondition extends Condition {

    private final List<ConditionID> conditionIDs;

    public AlternativeCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        conditionIDs = instruction.getList(instruction::getCondition);
    }

    @Override
    protected Boolean execute(final String playerID) {
        if (Bukkit.isPrimaryThread()) {
            for (final ConditionID id : conditionIDs) {
                if (BetonQuest.condition(playerID, id)) {
                    return true;
                }
            }
        } else {
            final List<CompletableFuture<Boolean>> conditions = new ArrayList<>();
            for (final ConditionID id : conditionIDs) {
                final CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(
                        () -> BetonQuest.condition(playerID, id));
                conditions.add(future);
            }
            for (final CompletableFuture<Boolean> condition : conditions) {
                try {
                    if (condition.get()) {
                        return true;
                    }
                } catch (final InterruptedException | ExecutionException e) {
                    LogUtils.logThrowableReport(e);
                    return false;
                }
            }
        }
        return false;
    }
}
