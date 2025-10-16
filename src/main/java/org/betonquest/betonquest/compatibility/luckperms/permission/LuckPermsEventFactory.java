package org.betonquest.betonquest.compatibility.luckperms.permission;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.data.NodeMap;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.argument.Argument;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.instruction.variable.VariableList;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Factory class for creating LuckPerms events.
 */
public class LuckPermsEventFactory implements PlayerEventFactory {

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
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final String action = instruction.get(Argument.STRING).getValue(null);

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
        final Variable<List<String>> permissions = instruction.getValueList("permission", Argument.STRING, VariableList.notEmptyChecker());
        final Variable<List<String>> contexts = instruction.getValueList("context", Argument.STRING);
        final Variable<String> value = instruction.getValue("value", Argument.STRING, "");
        final Variable<Number> expiry = instruction.getValue("expiry", Argument.NUMBER_NOT_LESS_THAN_ONE, 0);
        final Variable<TimeUnit> timeUnit = instruction.getValue("unit", Argument.ENUM(TimeUnit.class), TimeUnit.DAYS);
        return new LuckPermsNodeBuilder(permissions, value, contexts, expiry, timeUnit);
    }
}
