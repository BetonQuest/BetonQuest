package org.betonquest.betonquest.quest.objective.lectern;

import io.papermc.paper.event.player.PlayerLecternPageChangeEvent;
import org.betonquest.betonquest.api.DefaultObjective;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Lectern;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Objective for interacting with lecterns.
 */
public class LecternObjective extends DefaultObjective {

    /**
     * The location of the lectern.
     */
    private final Argument<Location> location;

    /**
     * The target page to reach.
     */
    @Nullable
    private final Argument<Number> page;

    /**
     * Create a new LecternObjective.
     *
     * @param service  the objective service
     * @param location the location of the lectern
     * @param page     the target page to reach
     */
    public LecternObjective(final ObjectiveService service, final Argument<Location> location,
                            @Nullable final Argument<Number> page) {
        super(service);
        this.location = location;
        this.page = page;
    }

    /**
     * Handle the {@link PlayerInteractEvent}.
     *
     * @param event         the event
     * @param onlineProfile the profile
     * @throws QuestException if there is an error
     */
    public void onInteract(final PlayerInteractEvent event, final OnlineProfile onlineProfile) throws QuestException {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        final Block block = event.getClickedBlock();
        if (block == null || !(block.getState() instanceof final Lectern lectern)) {
            return;
        }
        if (!block.getLocation().equals(location.getValue(onlineProfile))) {
            return;
        }
        if (page != null) {
            if (lectern.getPage() + 1 == page.getValue(onlineProfile).intValue()) {
                getService().complete(onlineProfile);
            }
            return;
        }
        getService().complete(onlineProfile);
    }

    /**
     * Handle the {@link PlayerLecternPageChangeEvent}.
     *
     * @param event         the event
     * @param onlineProfile the profile
     * @throws QuestException if there is an error
     */
    public void onPageChange(final PlayerLecternPageChangeEvent event, final OnlineProfile onlineProfile) throws QuestException {
        if (page == null) {
            return;
        }
        if (!event.getLectern().getLocation().equals(location.getValue(onlineProfile))) {
            return;
        }
        if (event.getNewPage() + 1 == page.getValue(onlineProfile).intValue()) {
            getService().complete(onlineProfile);
        }
    }
}
