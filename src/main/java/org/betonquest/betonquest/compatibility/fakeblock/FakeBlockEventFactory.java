package org.betonquest.betonquest.compatibility.fakeblock;

import com.briarcraft.fakeblock.api.service.GroupService;
import com.briarcraft.fakeblock.api.service.PlayerGroupService;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Factory to create FakeBlock events from {@link Instruction}s.
 */
public class FakeBlockEventFactory implements EventFactory {
    /**
     * Server to use for syncing to the primary server thread.
     */
    private final Server server;
    /**
     * Scheduler to use for syncing to the primary server thread.
     */
    private final BukkitScheduler scheduler;
    /**
     * Plugin to use for syncing to the primary server thread.
     */
    private final Plugin plugin;
    /**
     * GroupService to search for existing Groups from FakeBlock.
     */
    private final RegisteredServiceProvider<GroupService> groupService;
    /**
     * PlayerGroupService to change group states for the player
     */
    private final RegisteredServiceProvider<PlayerGroupService> playerGroupService;

    /**
     * Creates the FakeBlock event factory.
     *
     * @param server    server to use
     * @param scheduler scheduler to use
     * @param plugin    plugin to use
     */
    public FakeBlockEventFactory(final Server server, final BukkitScheduler scheduler, final BetonQuest plugin) {
        this.server = server;
        this.scheduler = scheduler;
        this.plugin = plugin;
        this.groupService = server.getServicesManager().getRegistration(GroupService.class);
        this.playerGroupService = server.getServicesManager().getRegistration(PlayerGroupService.class);
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws InstructionParseException {
        return new PrimaryServerThreadEvent(getFakeBlockEvent(instruction), server, scheduler, plugin);
    }

    private Event getFakeBlockEvent(final Instruction instruction) throws InstructionParseException {
        final String action = instruction.next();
        final List<String> groupNames = new ArrayList<>();
        Collections.addAll(groupNames, instruction.getArray());
        checkForNotExistingGroups(groupNames);
        return switch (action.toLowerCase(Locale.ROOT)) {
            case "hidegroup" -> new HideGroupEvent(groupNames, playerGroupService);
            case "showgroup" -> new ShowGroupEvent(groupNames, playerGroupService);
            default ->
                    throw new InstructionParseException("Unknown action (valid options are: showgroup, hidegroup): " + action);
        };
    }

    private void checkForNotExistingGroups(final List<String> groupNames) throws InstructionParseException {
        final List<String> notExistingGroups = new ArrayList<>();
        for (final String groupName : groupNames) {
            if (!groupService.getProvider().hasGroup(groupName)) {
                notExistingGroups.add(groupName);
            }
        }
        if (notExistingGroups.isEmpty()) {
            return;
        }
        throw new InstructionParseException("The following groups do not exist: " + notExistingGroups);
    }
}
