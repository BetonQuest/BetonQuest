package org.betonquest.betonquest.compatibility.magic;

import com.elmakers.mine.bukkit.api.magic.MagicAPI;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.api.quest.condition.online.OnlineConditionAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.argument.Argument;
import org.betonquest.betonquest.instruction.variable.Variable;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.condition.PrimaryServerThreadPlayerCondition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory to create {@link WandCondition}s from {@link Instruction}s.
 */
public class WandConditionFactory implements PlayerConditionFactory {
    /**
     * Expected length of formatted spells.
     */
    private static final int SPELL_FORMAT_LENGTH = 2;

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
        final Variable<CheckType> type = instruction.getVariable(Argument.ENUM(CheckType.class));
        final Map<String, Variable<Number>> spells = parseSpells(instruction.getList(instruction.getOptional("spells")), instruction.getPackage());
        final Variable<String> name = instruction.getVariable(instruction.getOptional("name"), Argument.STRING);
        final Variable<Number> amount = instruction.getVariable(instruction.getOptional("amount"), Argument.NUMBER);

        final BetonQuestLogger log = loggerFactory.create(WandCondition.class);
        return new PrimaryServerThreadPlayerCondition(new OnlineConditionAdapter(
                new WandCondition(api, type, name, spells, amount),
                log, instruction.getPackage()), data);
    }

    private Map<String, Variable<Number>> parseSpells(final List<String> spells, final QuestPackage questPackage) throws QuestException {
        final Map<String, Variable<Number>> parsed = new HashMap<>();
        for (final String spell : spells) {
            final String[] spellParts = spell.split(":");
            if (spellParts.length != SPELL_FORMAT_LENGTH) {
                throw new QuestException("Incorrect spell format");
            }
            final Variable<Number> level;
            try {
                level = new Variable<>(BetonQuest.getInstance().getVariableProcessor(), questPackage, spellParts[1], Argument.NUMBER);
            } catch (final QuestException e) {
                throw new QuestException("Could not parse spell level", e);
            }
            parsed.put(spellParts[0], level);
        }
        return parsed;
    }
}
