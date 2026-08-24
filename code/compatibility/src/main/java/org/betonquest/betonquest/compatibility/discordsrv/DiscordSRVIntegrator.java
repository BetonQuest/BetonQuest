package org.betonquest.betonquest.compatibility.discordsrv;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.api.reload.ReloadPhase;
import org.betonquest.betonquest.compatibility.discordsrv.condition.DiscordLinkedConditionFactory;
import org.betonquest.betonquest.compatibility.discordsrv.objective.DiscordLinkObjectiveFactory;

/**
 * Integrator for DiscordSRV.
 */
public class DiscordSRVIntegrator implements Integration {

    /**
     * The minimum required version of DiscordSRV.
     */
    public static final String REQUIRED_VERSION = "1.30.0";

    /**
     * Creates the DiscordSRV integrator.
     */
    public DiscordSRVIntegrator() {
    }

    @Override
    public void enable(final BetonQuestApi api) throws QuestException {
        final AccountLinkManager linkManager = DiscordSRV.getPlugin().getAccountLinkManager();
        api.conditions().registry().register("discordlinked", new DiscordLinkedConditionFactory(linkManager));
        final DiscordLinkObjectiveFactory linkObjectiveFactory = new DiscordLinkObjectiveFactory(api.profiles());
        api.objectives().registry().register("discordlink", linkObjectiveFactory);
        api.reloader().register(ReloadPhase.INTEGRATION, linkObjectiveFactory::clear);
        DiscordSRV.api.subscribe(linkObjectiveFactory);
    }

    @Override
    public void postEnable(final BetonQuestApi api) throws QuestException {
        // Empty
    }

    @Override
    public void disable() throws QuestException {
        // Empty
    }
}
