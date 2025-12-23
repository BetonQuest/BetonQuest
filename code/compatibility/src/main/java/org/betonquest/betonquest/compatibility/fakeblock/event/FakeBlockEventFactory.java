package org.betonquest.betonquest.compatibility.fakeblock.event;

import com.briarcraft.fakeblock.api.service.GroupService;
import com.briarcraft.fakeblock.api.service.PlayerGroupService;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.ValueValidator;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.bukkit.plugin.RegisteredServiceProvider;

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
     * Creates the FakeBlock event factory.
     *
     * @param groupService       the {@link GroupService} service.
     * @param playerGroupService the {@link PlayerGroupService}.
     */
    public FakeBlockEventFactory(final RegisteredServiceProvider<GroupService> groupService,
                                 final RegisteredServiceProvider<PlayerGroupService> playerGroupService) {
        this.groupService = groupService;
        this.playerGroupService = playerGroupService;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        return getFakeBlockEvent(instruction);
    }

    private PlayerEvent getFakeBlockEvent(final Instruction instruction) throws QuestException {
        final String action = instruction.string().get().getValue(null);
        final Variable<List<String>> groupNames = instruction.string()
                .validate(checkForNotExistingGroups())
                .getList();
        return switch (action.toLowerCase(Locale.ROOT)) {
            case "hidegroup" -> new HideGroupEvent(groupNames, playerGroupService);
            case "showgroup" -> new ShowGroupEvent(groupNames, playerGroupService);
            default -> throw new QuestException("Unknown action (valid options are: showgroup, hidegroup): " + action);
        };
    }

    private ValueValidator<String> checkForNotExistingGroups() {
        return value -> {
            if (!groupService.getProvider().hasGroup(value)) {
                throw new QuestException("This group do not exist: " + value);
            }
            return true;
        };
    }
}
