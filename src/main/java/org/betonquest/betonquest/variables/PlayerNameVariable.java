package org.betonquest.betonquest.variables;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Variable;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;

import java.util.function.Function;

/**
 * This variable resolves into the player's name. It can has optional "display"
 * argument, which will resolve it to the display name.
 */
public class PlayerNameVariable extends Variable {

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log = BetonQuest.getInstance().getLoggerFactory().create(getClass());

    /**
     * The type of the variable.
     */
    private final Type type;

    /**
     * Creates a new PlayerNameVariable from the given instruction.
     *
     * @param instruction the instruction
     */
    public PlayerNameVariable(final Instruction instruction) {
        super(instruction);
        this.type = getType(instruction);
    }

    private Type getType(final Instruction instruction) {
        if (instruction.hasNext()) {
            try {
                final String next = instruction.next();
                if ("name".equalsIgnoreCase(next)) {
                    return Type.NAME;
                } else if ("display".equalsIgnoreCase(next)) {
                    return Type.DISPLAY;
                } else if ("uuid".equalsIgnoreCase(next)) {
                    return Type.UUID;
                }
                log.warn(instruction.getPackage(), "Unknown type specified: " + next + ", defaulting to NAME.");
            } catch (final InstructionParseException e) {
                log.debug(instruction.getPackage(), "No type specified, defaulting to NAME.", e);
            }
        }
        return Type.NAME;
    }

    @Override
    public String getValue(final Profile profile) {
        try {
            return type.extractValue(profile);
        } catch (final IllegalStateException e) {
            log.warn(instruction.getPackage(), e.getMessage(), e);
            return "";
        }
    }

    /**
     * The type of the variable.
     */
    private enum Type {
        /**
         * The player's name.
         */
        NAME(profile -> profile.getPlayer().getName()),
        /**
         * The player's display name.
         */
        DISPLAY(profile -> profile.getOnlineProfile()
                .map(online -> online.getPlayer().getDisplayName())
                .orElseThrow(() -> new IllegalStateException(profile.getPlayer().getName() + " is offline, cannot get display name."))),
        /**
         * The player's UUID.
         */
        UUID(profile -> profile.getPlayer().getUniqueId().toString());

        private final Function<Profile, String> valueExtractor;

        Type(final Function<Profile, String> valueExtractor) {
            this.valueExtractor = valueExtractor;
        }

        String extractValue(final Profile profile) {
            return valueExtractor.apply(profile);
        }
    }
}
