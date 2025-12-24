package org.betonquest.betonquest.compatibility.fakeblock.event;

import com.briarcraft.fakeblock.api.service.PlayerGroupService;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.List;

/**
 * Hides the FakeBlock groups from the player.
 */
public class HideGroupEvent implements PlayerEvent {

    /**
     * The names of the groups that should be hidden for the player.
     */
    private final Variable<List<String>> groupNames;

    /**
     * PlayerGroupService to change group states for the player.
     */
    private final RegisteredServiceProvider<PlayerGroupService> playerGroupService;

    /**
     * Creates the hide group event.
     *
     * @param groupNames         is a string list with group names that should be hidden for the player
     * @param playerGroupService the FakeBlock PlayerGroupService
     */
    public HideGroupEvent(final Variable<List<String>> groupNames, final RegisteredServiceProvider<PlayerGroupService> playerGroupService) {
        this.groupNames = groupNames;
        this.playerGroupService = playerGroupService;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        for (final String groupName : groupNames.getValue(profile)) {
            playerGroupService.getProvider().hideGroup(groupName, profile.getPlayer());
        }
    }

    @Override
    public boolean isPrimaryThreadEnforced() {
        return true;
    }
}
