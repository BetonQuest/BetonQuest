package org.betonquest.betonquest.compatibility.luckperms.permission;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.data.NodeMap;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.PermissionNode;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.bukkit.OfflinePlayer;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Event to add permissions to a player using LuckPerms.
 */
public class LuckPermsPermissionEvent implements Event {

    /**
     * The {@link LuckPerms} API.
     */
    private final LuckPerms luckPermsAPI;

    /**
     * The {@link NodeApply} to apply the permissions.
     */
    private final NodeApply nodeApply;

    /**
     * The list of {@link PermissionNode}s to add.
     */
    private final LuckPermsNodeBuilder nodeBuilder;

    /**
     * The Constructor for the {@link LuckPermsPermissionEvent}.
     *
     * @param nodeBuilder  The list of {@link PermissionNode}s to add.
     * @param luckPermsAPI The {@link LuckPerms} API.
     * @param nodeApply    The {@link NodeApply} to apply the permissions.
     */
    public LuckPermsPermissionEvent(final LuckPermsNodeBuilder nodeBuilder, final LuckPerms luckPermsAPI, final NodeApply nodeApply) {
        this.nodeBuilder = nodeBuilder;
        this.luckPermsAPI = luckPermsAPI;
        this.nodeApply = nodeApply;
    }

    @Override
    public void execute(final Profile profile) throws QuestException {
        final OfflinePlayer offlinePlayer = profile.getPlayer();
        final UserManager userManager = luckPermsAPI.getUserManager();
        final CompletableFuture<User> userFuture = userManager.loadUser(offlinePlayer.getUniqueId());
        final User user = getUser(userFuture);

        final NodeMap data = user.data();
        final List<Node> buildNodes = nodeBuilder.getNodes(profile);
        for (final Node node : buildNodes) {
            nodeApply.apply(data, node);
        }
        userManager.saveUser(user).thenAcceptAsync(result -> luckPermsAPI.getMessagingService().ifPresent(service
                -> service.pushUserUpdate(user)));
    }

    private User getUser(final CompletableFuture<User> userFuture) throws QuestException {
        try {
            return userFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new QuestException("Failed to load user from LuckPerms", e);
        }
    }

    /**
     * Functional interface to apply a {@link PermissionNode} to a {@link NodeMap}.
     */
    @FunctionalInterface
    public interface NodeApply {
        /**
         * Applies the {@link PermissionNode} to the {@link NodeMap}.
         *
         * @param nodeMap The {@link NodeMap} to apply the {@link PermissionNode} to.
         * @param node    The {@link PermissionNode} to apply.
         */
        void apply(NodeMap nodeMap, Node node);
    }
}
