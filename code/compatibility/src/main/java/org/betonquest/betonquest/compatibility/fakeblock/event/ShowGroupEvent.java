package org.betonquest.betonquest.compatibility.fakeblock.event;

import com.briarcraft.fakeblock.api.service.PlayerGroupService;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.List;

/**
 * Shows FakeBlock groups to the player.
 */
public class ShowGroupEvent implements PlayerEvent {

    /**
     * The groupNames that should be shown to the player.
     */
    private final Argument<List<String>> groupNames;

    /**
     * PlayerGroupService to change group states for the player.
     */
    private final RegisteredServiceProvider<PlayerGroupService> playerGroupService;

    /**
     * Creates the showgroup event.
     *
     * @param groupNames         is a list that contains Strings of group names that should be shown to the player
     * @param playerGroupService the FakeBlock PlayerGroupService
     */
    public ShowGroupEvent(final Argument<List<String>> groupNames, final RegisteredServiceProvider<PlayerGroupService> playerGroupService) {
        this.groupNames = groupNames;
        this.playerGroupService = playerGroupService;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        for (final String groupName : groupNames.getValue(profile)) {
            playerGroupService.getProvider().showGroup(groupName, profile.getPlayer());
        }
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
