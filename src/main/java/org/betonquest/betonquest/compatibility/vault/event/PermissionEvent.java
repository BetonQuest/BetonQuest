package org.betonquest.betonquest.compatibility.vault.event;

import net.milkbowl.vault.permission.Permission;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestException;
import org.jetbrains.annotations.Nullable;

/**
 * Manages player's permissions.
 */
public class PermissionEvent implements Event {
    /**
     * Service where the permission will be modified.
     */
    private final Permission vault;

    /**
     * World to restrict permission to it.
     */
    @Nullable
    private final String world;

    /**
     * Permission to modify.
     */
    private final String permission;

    /**
     * If the permission should be added. When not it will be removed.
     */
    private final boolean add;

    /**
     * If the permission should be interpreted as permission. When not it is interpreted as group.
     */
    private final boolean perm;

    /**
     * Create a new Vault Permission event.
     *
     * @param permissionService the service where the permission will be modified
     * @param permission        the permission to add or remove
     * @param world             the world if the permission should be restricted to it
     * @param add               if the permission should be added or removed
     * @param perm              if the permission should be interpreted as permission or group
     */
    public PermissionEvent(final Permission permissionService, final String permission, @Nullable final String world, final boolean add, final boolean perm) {
        this.vault = permissionService;
        this.permission = permission;
        this.world = world;
        this.add = add;
        this.perm = perm;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        if (add) {
            if (perm) {
                vault.playerAdd(world, profile.getPlayer(), permission);
            } else {
                vault.playerAddGroup(world, profile.getPlayer(), permission);
            }
        } else {
            if (perm) {
                vault.playerRemove(world, profile.getPlayer(), permission);
            } else {
                vault.playerRemoveGroup(world, profile.getPlayer(), permission);
            }
        }
    }
}
