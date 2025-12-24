package org.betonquest.betonquest.compatibility.magic;

import com.elmakers.mine.bukkit.api.magic.MagicAPI;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.SimpleArgumentParser;
import org.betonquest.betonquest.api.instruction.argument.parser.NumberParser;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;

import java.util.Collections;
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
     * Magic API to use.
     */
    private final MagicAPI api;

    /**
     * Create a new factory for Magic Wand Conditions.
     *
     * @param loggerFactory the logger factory to create class specific logger
     * @param api           the magic api
     */
    public WandConditionFactory(final BetonQuestLoggerFactory loggerFactory, final MagicAPI api) {
        this.loggerFactory = loggerFactory;
        this.api = api;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<CheckType> type = instruction.enumeration(CheckType.class).get();
        final Variable<List<Map.Entry<String, Integer>>> spells =
                instruction.parse(SpellParser.SPELL).getList("spells", Collections.emptyList());
        final Variable<String> name = instruction.string().get("name").orElse(null);
        final Variable<Number> amount = instruction.number().get("amount").orElse(null);

        final BetonQuestLogger log = loggerFactory.create(WandCondition.class);
        return new OnlineConditionAdapter(new WandCondition(api, type, name, spells, amount), log, instruction.getPackage());
    }

    /**
     * Parses a string to a Spell with level.
     */
    private static final class SpellParser implements SimpleArgumentParser<Map.Entry<String, Integer>> {

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
            return Map.entry(parts[0], NumberParser.DEFAULT.apply(parts[1]).intValue());
        }
    }
}
