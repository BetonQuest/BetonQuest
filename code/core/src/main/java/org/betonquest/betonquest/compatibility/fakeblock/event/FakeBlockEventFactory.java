package org.betonquest.betonquest.compatibility.fakeblock.event;

import com.briarcraft.fakeblock.api.service.GroupService;
import com.briarcraft.fakeblock.api.service.PlayerGroupService;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.ValueChecker;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.thread.PrimaryServerThreadEvent;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Factory to create FakeBlock events from {@link Instruction}s.
 */
public class FakeBlockEventFactory implements PlayerEventFactory {
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
     * @param groupService       the {@link GroupService} service.
     * @param playerGroupService the {@link PlayerGroupService}.
     * @param data               the data for primary server thread access
     */
    public FakeBlockEventFactory(final RegisteredServiceProvider<GroupService> groupService,
                                 final RegisteredServiceProvider<PlayerGroupService> playerGroupService,
                                 final PrimaryServerThreadData data) {
        this.data = data;
        this.groupService = groupService;
        this.playerGroupService = playerGroupService;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return new PrimaryServerThreadEvent(getFakeBlockEvent(instruction), data);
    }

    private PlayerEvent getFakeBlockEvent(final Instruction instruction) throws QuestException {
        final String action = instruction.get(Argument.STRING).getValue(null);
        final Variable<List<String>> groupNames = instruction.getList(Argument.STRING, checkForNotExistingGroups());
        return switch (action.toLowerCase(Locale.ROOT)) {
            case "hidegroup" -> new HideGroupEvent(groupNames, playerGroupService);
            case "showgroup" -> new ShowGroupEvent(groupNames, playerGroupService);
            default -> throw new QuestException("Unknown action (valid options are: showgroup, hidegroup): " + action);
        };
    }

    private ValueChecker<List<String>> checkForNotExistingGroups() {
        return value -> {
            final List<String> notExistingGroups = new ArrayList<>();
            for (final String groupName : value) {
                if (!groupService.getProvider().hasGroup(groupName)) {
                    notExistingGroups.add(groupName);
                }
            }
            if (notExistingGroups.isEmpty()) {
                return;
            }
            throw new QuestException("The following groups do not exist: " + notExistingGroups);
        };
    }
}
