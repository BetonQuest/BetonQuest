package org.betonquest.betonquest.compatibility.fakeblock;

import com.briarcraft.fakeblock.api.service.GroupService;
import com.briarcraft.fakeblock.api.service.PlayerGroupService;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Factory to create FakeBlock events from {@link Instruction}s.
 */
public class FakeBlockEventFactory implements EventFactory {
    /**
     * GroupService to search for existing Groups from FakeBlock.
     */
    private final RegisteredServiceProvider<GroupService> groupService;

    /**
     * PlayerGroupService to change group states for the player.
     */
    private final RegisteredServiceProvider<PlayerGroupService> playerGroupService;

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Creates the FakeBlock event factory.
     *
     * @param servicesManager servicesManager to get services from
     * @param data            the data for primary server thread access
     */
    public FakeBlockEventFactory(final ServicesManager servicesManager, final PrimaryServerThreadData data) {
        this.data = data;
        this.groupService = servicesManager.getRegistration(GroupService.class);
        this.playerGroupService = servicesManager.getRegistration(PlayerGroupService.class);
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadEvent(getFakeBlockEvent(instruction), data);
    }

    private Event getFakeBlockEvent(final Instruction instruction) throws QuestException {
        final String action = instruction.next();
        final List<String> groupNames = new ArrayList<>();
        Collections.addAll(groupNames, instruction.getArray());
        checkForNotExistingGroups(groupNames);
        return switch (action.toLowerCase(Locale.ROOT)) {
            case "hidegroup" -> new HideGroupEvent(groupNames, playerGroupService);
            case "showgroup" -> new ShowGroupEvent(groupNames, playerGroupService);
            default -> throw new QuestException("Unknown action (valid options are: showgroup, hidegroup): " + action);
        };
    }

    private void checkForNotExistingGroups(final List<String> groupNames) throws QuestException {
        final List<String> notExistingGroups = new ArrayList<>();
        for (final String groupName : groupNames) {
            if (!groupService.getProvider().hasGroup(groupName)) {
                notExistingGroups.add(groupName);
            }
        }
        if (notExistingGroups.isEmpty()) {
            return;
        }
        throw new QuestException("The following groups do not exist: " + notExistingGroups);
    }
}
