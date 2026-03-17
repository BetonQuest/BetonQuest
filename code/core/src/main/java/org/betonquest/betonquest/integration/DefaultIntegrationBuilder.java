package org.betonquest.betonquest.integration;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestConsumer;
import org.betonquest.betonquest.api.common.function.QuestRunnable;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.api.integration.IntegrationBuilder;
import org.betonquest.betonquest.api.integration.IntegrationPolicy;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

/**
 * Default implementation of {@link IntegrationBuilder}.
 */
public class DefaultIntegrationBuilder implements IntegrationBuilder {

    /**
     * No-operation runnable.
     */
    private static final QuestRunnable NOOP_RUNNABLE = () -> {
    };

    /**
     * No-operation consumer.
     */
    private static final QuestConsumer<BetonQuestApi> NOOP_CONSUMER = api -> {
    };

    /**
     * The {@link IntegrationPolicy} instance to register the integration with.
     */
    private final IntegrationPolicy integrations;

    /**
     * The method to call when enabling the integration.
     */
    @Nullable
    private QuestConsumer<BetonQuestApi> enableMethod;

    /**
     * The method to call after enabling all plugins.
     */
    @Nullable
    private QuestConsumer<BetonQuestApi> postEnableMethod;

    /**
     * The method to call when disabling the integration.
     */
    @Nullable
    private QuestRunnable disableMethod;

    /**
     * Creates a new instance of the {@link DefaultIntegrationBuilder}.
     *
     * @param integrations the {@link IntegrationPolicy} instance to register the integration with.
     */
    public DefaultIntegrationBuilder(final IntegrationPolicy integrations) {
        this.integrations = integrations;
    }

    @Override
    public IntegrationBuilder enable(final QuestConsumer<BetonQuestApi> enable) {
        this.enableMethod = enable;
        return this;
    }

    @Override
    public IntegrationBuilder postEnable(final QuestConsumer<BetonQuestApi> postEnable) {
        this.postEnableMethod = postEnable;
        return this;
    }

    @Override
    public IntegrationBuilder disable(final QuestRunnable disable) {
        this.disableMethod = disable;
        return this;
    }

    @Override
    public void integrate(final Plugin integratingPlugin) {
        if (enableMethod == null && postEnableMethod == null) {
            throw new IllegalStateException("No enable method specified.");
        }
        register(integratingPlugin, enableMethod == null ? NOOP_CONSUMER : enableMethod,
                postEnableMethod == null ? NOOP_CONSUMER : postEnableMethod,
                disableMethod == null ? NOOP_RUNNABLE : disableMethod);
    }

    private void register(final Plugin integratingPlugin, final QuestConsumer<BetonQuestApi> enableMethod, final QuestConsumer<BetonQuestApi> postEnableMethod,
                          final QuestRunnable disableMethod) {
        integrations.register(integratingPlugin, () -> new Integration() {
            @Override
            public void enable(final BetonQuestApi betonQuestApi) throws QuestException {
                enableMethod.accept(betonQuestApi);
            }

            @Override
            public void postEnable(final BetonQuestApi betonQuestApi) throws QuestException {
                postEnableMethod.accept(betonQuestApi);
            }

            @Override
            public void disable() throws QuestException {
                disableMethod.run();
            }
        });
    }
}
