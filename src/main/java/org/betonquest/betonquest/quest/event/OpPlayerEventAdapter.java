package org.betonquest.betonquest.quest.event;

import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.quest.event.online.OnlineEvent;
import org.betonquest.betonquest.exceptions.QuestException;
import org.bukkit.entity.Player;

/**
 * Adapt an event to be run as Op.
 * <p>
 * Gives the player op, executes the nested event and then reverts the operation if necessary.
 */
public class OpPlayerEventAdapter implements OnlineEvent {

    /**
     * The event to execute as Op.
     */
    private final OnlineEvent event;

    /**
     * Creates a new OpPlayerEventAdapter.
     *
     * @param event the event to execute as op.
     */
    public OpPlayerEventAdapter(final OnlineEvent event) {
        this.event = event;
    }

    @Override
    public void execute(final OnlineProfile profile) throws QuestException {
        final Player player = profile.getPlayer();
        final boolean previousOp = player.isOp();
        try {
            player.setOp(true);
            event.execute(profile);
        } finally {
            player.setOp(previousOp);
        }
    }
}
