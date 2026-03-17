package org.betonquest.betonquest.compatibility.fakeblock;

import com.briarcraft.fakeblock.api.service.GroupService;
import com.briarcraft.fakeblock.api.service.PlayerGroupService;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.compatibility.fakeblock.action.FakeBlockActionFactory;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Integrates with FakeBlock.
 */
public class FakeBlockIntegrator implements Integration {

    /**
     * The minimum required version of FakeBlock.
     */
    public static final String REQUIRED_VERSION = "2.0.1";

    /**
     * Create the FakeBlock integration.
     */
    public FakeBlockIntegrator() {
    }

    @Override
    public void enable(final BetonQuestApi api) throws QuestException {

        final RegisteredServiceProvider<GroupService> groupService = getServiceProvider(GroupService.class);
        final RegisteredServiceProvider<PlayerGroupService> playerGroupService = getServiceProvider(PlayerGroupService.class);

        api.actions().registry().register("fakeblock", new FakeBlockActionFactory(groupService, playerGroupService));
    }

    private <T> RegisteredServiceProvider<T> getServiceProvider(final Class<T> service) throws QuestException {
        final RegisteredServiceProvider<T> provider = Bukkit.getServer().getServicesManager().getRegistration(service);
        if (provider == null) {
            throw new QuestException("Could not find service provider for " + service.getName());
        }
        return provider;
    }

    @Override
    public void postEnable(final BetonQuestApi api) {
        // Empty
    }

    @Override
    public void disable() {
        // Empty
    }
}
