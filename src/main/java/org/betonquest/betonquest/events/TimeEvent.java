package org.betonquest.betonquest.events;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.bukkit.World;

/**
 * Changes time on the server
 */
@SuppressWarnings("PMD.CommentRequired")
public class TimeEvent extends QuestEvent {

    private final long amount;
    private final boolean add;

    public TimeEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        final String time = instruction.next();
        add = !time.isEmpty() && time.charAt(0) == '+';
        try {
            if (add) {
                amount = Long.parseLong(time.substring(1)) * 1000;
            } else {
                amount = Long.parseLong(time) * 1000 + 18_000;
            }
        } catch (NumberFormatException e) {
            throw new InstructionParseException("Could not parse time amount", e);
        }
    }

    @Override
    protected Void execute(final String playerID) {
        final World world = PlayerConverter.getPlayer(playerID).getWorld();
        long time = amount;
        if (add) {
            time += world.getTime();
        }
        world.setTime(time % 24_000);
        return null;
    }

}
