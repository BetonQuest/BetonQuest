package org.betonquest.betonquest.compatibility.fakeblock;

import com.briarcraft.fakeblock.api.service.PlayerGroupService;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.List;

/**
 * Hides the FakeBlock groups from the player.
 */
public class HideGroupEvent implements Event {
    /**
     * The names of the groups that should be hidden for the player.
     */
    private final List<String> groupNames;

    /**
     * PlayerGroupService to change group states for the player.
     */
    private final RegisteredServiceProvider<PlayerGroupService> playerGroupService;

    /**
     * Creates the hidegroup event.
     *
     * @param groupNames         is a string list with group names that should be hidden for the player
     * @param playerGroupService the FakeBlock PlayerGroupService
     */
    public HideGroupEvent(final List<String> groupNames, final RegisteredServiceProvider<PlayerGroupService> playerGroupService) {
        this.groupNames = groupNames;
        this.playerGroupService = playerGroupService;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        for (final String groupName : groupNames) {
            playerGroupService.getProvider().hideGroup(groupName, profile.getPlayer());
        }
    }
}


