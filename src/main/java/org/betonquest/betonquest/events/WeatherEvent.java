package org.betonquest.betonquest.events;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.World;

/**
 * Changes the weather on the server
 */
@SuppressWarnings("PMD.CommentRequired")
public class WeatherEvent extends QuestEvent {

    private final boolean storm;
    private final boolean thunder;

    @SuppressWarnings("PMD.SwitchStmtsShouldHaveDefault")
    public WeatherEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        final String part = instruction.next();
        switch (part) {
            case "sun", "clear" -> {
                storm = false;
                thunder = false;
            }
            case "rain", "rainy" -> {
                storm = true;
                thunder = false;
            }
            case "storm", "thunder" -> {
                storm = true;
                thunder = true;
            }
            default -> throw new InstructionParseException("Weather type '" + part + "' does not exist");
        }
    }

    @Override
    protected Void execute(final String playerID) {
        final World world = PlayerConverter.getPlayer(playerID).getWorld();
        world.setStorm(storm);
        world.setThundering(thunder);
        return null;
    }

}
