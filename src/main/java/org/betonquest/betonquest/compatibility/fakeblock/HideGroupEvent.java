package org.betonquest.betonquest.compatibility.fakeblock;

import com.briarcraft.fakeblock.api.service.PlayerGroupService;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.List;

/**
 * Hides the FakeBlock groups to the player.
 */
public class HideGroupEvent implements Event {
    /**
     * The groupNames that should be hidden for the player.
     */
    private final List<String> groupNames;
    /**
     * PlayerGroupService to change group states for the player
     */
    private final RegisteredServiceProvider<PlayerGroupService> playerGroupService;

    /**
     * Create the hidegroup event.
     *
     * @param groupNames         is a list that contains Strings of group names that should be hidden for the player.
     * @param playerGroupService the FakeBlock PlayerGroupService.
     */
    public HideGroupEvent(final List<String> groupNames, final RegisteredServiceProvider<PlayerGroupService> playerGroupService) {
        this.groupNames = groupNames;
        this.playerGroupService = playerGroupService;
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        if (groupNames.isEmpty()) {
            throw new QuestRuntimeException("FakeBlock no Groups were specified.");
        }
        for (final String groupName : groupNames) {
            playerGroupService.getProvider().hideGroup(groupName, profile.getPlayer());
        }
    }
}


