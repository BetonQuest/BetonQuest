package org.betonquest.betonquest.compatibility.fakeblock.event;

import com.briarcraft.fakeblock.api.service.GroupService;
import com.briarcraft.fakeblock.api.service.PlayerGroupService;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.ValueValidator;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.List;
import java.util.Locale;

/**
 * Factory to create FakeBlock events from {@link Instruction}s.
 */
public class FakeBlockActionFactory implements PlayerActionFactory {

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
    public FakeBlockActionFactory(final RegisteredServiceProvider<GroupService> groupService,
                                  final RegisteredServiceProvider<PlayerGroupService> playerGroupService) {
        this.groupService = groupService;
        this.playerGroupService = playerGroupService;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return getFakeBlockAction(instruction);
    }

    private PlayerAction getFakeBlockAction(final Instruction instruction) throws QuestException {
        final String action = instruction.string().get().getValue(null);
        final Argument<List<String>> groupNames = instruction.string()
                .validate(checkForNotExistingGroups())
                .list().get();
        return switch (action.toLowerCase(Locale.ROOT)) {
            case "hidegroup" -> new HideGroupAction(groupNames, playerGroupService);
            case "showgroup" -> new ShowGroupAction(groupNames, playerGroupService);
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
