package org.betonquest.betonquest.compatibility.magic;

import com.elmakers.mine.bukkit.api.magic.MagicAPI;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.instruction.variable.VariableList;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;

import java.util.List;
import java.util.Map;

/**
 * Factory to create {@link WandCondition}s from {@link Instruction}s.
 */
public class WandConditionFactory implements PlayerConditionFactory {

    /**
     * Logger Factory to create new class specific logger.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Magic API to use.
     */
    private final MagicAPI api;

    /**
     * Create a new factory for Magic Wand Conditions.
     *
     * @param loggerFactory the logger factory to create class specific logger
     * @param api           the magic api
     * @param data          the data for primary server thread access
     */
    public WandConditionFactory(final BetonQuestLoggerFactory loggerFactory, final MagicAPI api, final PrimaryServerThreadData data) {
        this.loggerFactory = loggerFactory;
        this.api = api;
        this.data = data;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<CheckType> type = instruction.get(Argument.ENUM(CheckType.class));
        final Variable<List<Map.Entry<String, Integer>>> spells =
                instruction.getValueList("spells", SpellParser.SPELL, VariableList.notDuplicateKeyChecker());
        final Variable<String> name = instruction.getValue("name", Argument.STRING);
        final Variable<Number> amount = instruction.getValue("amount", Argument.NUMBER);

        final BetonQuestLogger log = loggerFactory.create(WandCondition.class);
        return new PrimaryServerThreadPlayerCondition(new OnlineConditionAdapter(
                new WandCondition(api, type, name, spells, amount),
                log, instruction.getPackage()), data);
    }

    /**
     * Parses a string to a Spell with level.
     */
    private static final class SpellParser implements Argument<Map.Entry<String, Integer>> {
        /**
         * The default instance of {@link SpellParser}.
         */
        public static final SpellParser SPELL = new SpellParser();

        /**
         * Expected length of formatted spells.
         */
        private static final int SPELL_FORMAT_LENGTH = 2;

        @Override
        public Map.Entry<String, Integer> apply(final String value) throws QuestException {
            final String[] parts = value.split(":");
            if (parts.length != SPELL_FORMAT_LENGTH) {
                throw new IllegalArgumentException("Invalid spell format: " + value);
            }
            return Map.entry(parts[0], NUMBER.apply(parts[1]).intValue());
        }
    }
}
