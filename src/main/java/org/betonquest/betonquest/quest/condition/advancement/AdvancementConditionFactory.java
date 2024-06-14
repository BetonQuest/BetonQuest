package org.betonquest.betonquest.quest.condition.advancement;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;

/**
 * Factory to create advancement conditions from {@link Instruction}s.
 */
public class AdvancementConditionFactory implements PlayerConditionFactory {
    /**
     * Amount of parts the advancement string is expected to have.
     */
    private static final int ADVANCEMENT_LENGTH = 2;

    /**
     * Data used for condition check on the primary server thread.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the Advancement Condition Factory.
     *
     * @param data the data used for checking the condition on the main thread
     */
    public AdvancementConditionFactory(final PrimaryServerThreadData data) {
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws InstructionParseException {
        final String advancementString = instruction.next();
        final String[] split = advancementString.split(":");
        if (split.length != ADVANCEMENT_LENGTH) {
            throw new InstructionParseException("The advancement '" + advancementString + "' is missing a namespace!");
        }
        final Advancement advancement = Utils.getNN(Bukkit.getServer().getAdvancement(new NamespacedKey(split[0], split[1])),
                "No such advancement: " + advancementString);
        return new PrimaryServerThreadPlayerCondition(new AdvancementCondition(advancement), data);
    }
}
