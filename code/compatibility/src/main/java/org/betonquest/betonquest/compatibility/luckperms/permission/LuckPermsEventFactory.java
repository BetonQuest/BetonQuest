package org.betonquest.betonquest.compatibility.luckperms.permission;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.data.NodeMap;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Factory class for creating LuckPerms events.
 */
public class LuckPermsEventFactory implements PlayerActionFactory {

    /**
     * The {@link LuckPerms} API.
     */
    private final LuckPerms luckPermsAPI;

    /**
     * Creates the LuckPerms event factory.
     *
     * @param luckPermsAPI The {@link LuckPerms} API.
     */
    public LuckPermsEventFactory(final LuckPerms luckPermsAPI) {
        this.luckPermsAPI = luckPermsAPI;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final String action = instruction.string().get().getValue(null);

        return switch (action.toLowerCase(Locale.ROOT)) {
            case "addpermission" ->
                    new LuckPermsPermissionEvent(getNodeBuilder(instruction), luckPermsAPI, NodeMap::add);
            case "removepermission" ->
                    new LuckPermsPermissionEvent(getNodeBuilder(instruction), luckPermsAPI, NodeMap::remove);
            default ->
                    throw new QuestException("Unknown action: " + action + ". Expected addPermission or removePermission.");
        };
    }

    private LuckPermsNodeBuilder getNodeBuilder(final Instruction instruction) throws QuestException {
        final Argument<List<String>> permissions = instruction.string().list()
                .invalidate(List::isEmpty).get("permission", Collections.emptyList());
        final Argument<List<String>> contexts = instruction.string().list().get("context", Collections.emptyList());
        final Argument<String> value = instruction.string().get("value", "");
        final Argument<Number> expiry = instruction.number().atLeast(0).get("expiry", 0);
        final Argument<TimeUnit> timeUnit = instruction.enumeration(TimeUnit.class).get("unit", TimeUnit.DAYS);
        return new LuckPermsNodeBuilder(permissions, value, contexts, expiry, timeUnit);
    }
}
