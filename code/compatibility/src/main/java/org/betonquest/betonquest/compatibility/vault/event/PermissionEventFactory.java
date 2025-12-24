package org.betonquest.betonquest.compatibility.vault.event;

import net.milkbowl.vault.permission.Permission;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.variable.Variable;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;

/**
 * Factory to create {@link PermissionEvent}s from {@link Instruction}s.
 */
public class PermissionEventFactory implements PlayerEventFactory {

    /**
     * Service where the permission will be modified.
     */
    private final Permission permissionService;

    /**
     * Create a new Factory to create Vault Permission Events.
     *
     * @param permissionService the service where the permission will be modified
     */
    public PermissionEventFactory(final Permission permissionService) {
        this.permissionService = permissionService;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final Variable<Boolean> add = instruction.parse("add"::equalsIgnoreCase).get();
        final Variable<Boolean> perm = instruction.parse("perm"::equalsIgnoreCase).get();
        final Variable<String> permission = instruction.string().get();
        final Variable<String> world;
        if (instruction.size() >= 5 && !instruction.nextElement().startsWith("conditions:")) {
            world = instruction.get(instruction.current(), instruction.getParsers().string());
        } else {
            world = null;
        }
        return new PermissionEvent(permissionService, permission, world, add, perm);
    }
}
