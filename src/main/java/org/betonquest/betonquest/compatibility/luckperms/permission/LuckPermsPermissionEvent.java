package org.betonquest.betonquest.compatibility.luckperms.permission;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.data.NodeMap;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.types.PermissionNode;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Event to add permissions to a player using LuckPerms
 */
public class LuckPermsPermissionEvent implements Event {

    /**
     * The {@link LuckPerms} API.
     */
    private final LuckPerms luckPermsAPI;

    /**
     * The {@link PermissionApply} to apply the permissions.
     */
    private final PermissionApply permissionApply;

    /**
     * The list of {@link PermissionNode}s to add.
     */
    private final List<PermissionNode> permissionNodes;

    /**
     * The Constructor for the {@link LuckPermsPermissionEvent}.
     *
     * @param permissionNodes The list of {@link PermissionNode}s to add.
     * @param luckPermsAPI    The {@link LuckPerms} API.
     * @param permissionApply The {@link PermissionApply} to apply the permissions.
     */
    public LuckPermsPermissionEvent(final List<PermissionNode> permissionNodes, final LuckPerms luckPermsAPI, final PermissionApply permissionApply) {
        this.permissionNodes = permissionNodes;
        this.luckPermsAPI = luckPermsAPI;
        this.permissionApply = permissionApply;
    }

    @Override
    public void execute(final Profile profile) throws QuestRuntimeException {
        final OfflinePlayer offlinePlayer = profile.getPlayer();
        final UserManager userManager = luckPermsAPI.getUserManager();
        final CompletableFuture<User> userFuture = userManager.loadUser(offlinePlayer.getUniqueId());
        userFuture.thenAcceptAsync(user -> {
            final NodeMap data = user.data();
            for (final PermissionNode permission : permissionNodes) {
                permissionApply.apply(data, permission);
            }
            userManager.saveUser(user);
        });
    }

    /**
     * Functional interface to apply a {@link PermissionNode} to a {@link NodeMap}.
     */
    @FunctionalInterface
    public interface PermissionApply {
        /**
         * Applies the {@link PermissionNode} to the {@link NodeMap}.
         *
         * @param nodeMap    The {@link NodeMap} to apply the {@link PermissionNode} to.
         * @param permission The {@link PermissionNode} to apply.
         */
        void apply(NodeMap nodeMap, PermissionNode permission);
    }
}
