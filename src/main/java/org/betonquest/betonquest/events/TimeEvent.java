package org.betonquest.betonquest.events;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.bukkit.World;

/**
 * Changes time on the server
 */
@SuppressWarnings("PMD.CommentRequired")
public class TimeEvent extends QuestEvent {

    private final float amount;
    private final boolean add;

    public TimeEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        final String time = instruction.next();
        add = !time.isEmpty() && time.charAt(0) == '+';
        try {
            if (add) {
                amount = Float.parseFloat(time.substring(1)) * 1000;
            } else {
                amount = Float.parseFloat(time) * 1000 + 18_000;
            }
        } catch (final NumberFormatException e) {
            throw new InstructionParseException("Could not parse time amount", e);
        }
    }

    @Override
    protected Void execute(final Profile profile) {
        final World world = profile.getOnlineProfile().getOnlinePlayer().getWorld();
        long time = (long) amount;
        if (add) {
            time += world.getTime();
        }
        world.setTime(time % 24_000);
        return null;
    }

}
