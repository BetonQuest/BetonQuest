package org.betonquest.betonquest.integration;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestConsumer;
import org.betonquest.betonquest.api.common.function.QuestRunnable;
import org.betonquest.betonquest.api.integration.Integration;
import org.betonquest.betonquest.api.integration.IntegrationBuilder;
import org.betonquest.betonquest.api.integration.Integrations;
import org.jetbrains.annotations.Nullable;

/**
 * Default implementation of  {@link IntegrationBuilder}.
 */
public class DefaultIntegrationBuilder implements IntegrationBuilder {

    /**
     * The {@link Integrations} instance to register the integration with.
     */
    private final Integrations integrations;

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
    private QuestRunnable teardownMethod;

    /**
     * Creates a new instance of the {@link DefaultIntegrationBuilder}.
     *
     * @param integrations the {@link Integrations} instance to register the integration with.
     */
    public DefaultIntegrationBuilder(final Integrations integrations) {
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
    public IntegrationBuilder teardown(final QuestRunnable teardown) {
        this.teardownMethod = teardown;
        return this;
    }

    @Override
    public void integrate() {
        if (enableMethod == null && postEnableMethod == null) {
            throw new IllegalStateException("No enable method specified.");
        }
        register(enableMethod == null ? api -> {
                } : enableMethod,
                postEnableMethod == null ? api -> {
                } : postEnableMethod,
                teardownMethod == null ? () -> {
                } : teardownMethod);
    }

    private void register(final QuestConsumer<BetonQuestApi> enableMethod, final QuestConsumer<BetonQuestApi> postEnableMethod,
                          final QuestRunnable teardownMethod) {
        integrations.register(new Integration() {
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
                teardownMethod.run();
            }
        });
    }
}
