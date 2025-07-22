package org.betonquest.betonquest.compatibility.vault.event;

import net.milkbowl.vault.permission.Permission;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;

/**
 * Factory to create {@link PermissionEvent}s from {@link Instruction}s.
 */
public class PermissionEventFactory implements PlayerEventFactory {
    /**
     * Service where the permission will be modified.
     */
    private final Permission permissionService;

    /**
     * Data used for primary server access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create a new Factory to create Vault Permission Events.
     *
     * @param permissionService the service where the permission will be modified
     * @param data              the data used for primary server access
     */
    public PermissionEventFactory(final Permission permissionService, final PrimaryServerThreadData data) {
        this.permissionService = permissionService;
        this.data = data;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final boolean add = "add".equalsIgnoreCase(instruction.next());
        final boolean perm = "perm".equalsIgnoreCase(instruction.next());
        final String permission = instruction.next();
        final String world;
        if (instruction.size() >= 5 && !instruction.next().matches("^conditions?:")) {
            world = instruction.current();
        } else {
            world = null;
        }
        return new PrimaryServerThreadEvent(new PermissionEvent(permissionService, permission, world, add, perm), data);
    }
}
