package org.betonquest.betonquest.quest.variable.name;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;
import org.betonquest.betonquest.exceptions.QuestException;

import java.util.Locale;

/**
 * Factory to create {@link PlayerNameVariable}s from {@link Instruction}s.
 */
public class PlayerNameVariableFactory implements PlayerVariableFactory {
    /**
     * Logger Factory to create new logger for the created
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Create a PlayerName variable factory.
     *
     * @param loggerFactory the logger factory to create a new custom logger for the constructed variable
     */
    public PlayerNameVariableFactory(final BetonQuestLoggerFactory loggerFactory) {
        this.loggerFactory = loggerFactory;
    }

    @Override
    public PlayerVariable parsePlayer(final Instruction instruction) throws QuestException {
        final PlayerNameType type = getType(instruction);
        return new PlayerNameVariable(type, loggerFactory.create(PlayerNameVariable.class), instruction.getPackage());
    }

    private PlayerNameType getType(final Instruction instruction) throws QuestException {
        if (!instruction.hasNext()) {
            return PlayerNameType.NAME;
        }
        final String type = instruction.next().toLowerCase(Locale.ROOT);
        return switch (type) {
            case "name" -> PlayerNameType.NAME;
            case "display" -> PlayerNameType.DISPLAY;
            case "uuid" -> PlayerNameType.UUID;
            default -> throw new QuestException("Unknown type specified: " + type);
        };
    }
}
