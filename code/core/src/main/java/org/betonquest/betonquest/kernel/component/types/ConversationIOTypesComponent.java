package org.betonquest.betonquest.kernel.component.types;

import org.betonquest.betonquest.api.common.component.font.DefaultFontRegistry;
import org.betonquest.betonquest.api.common.component.font.FontRegistry;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.service.conversation.Conversations;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.api.service.item.ItemManager;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.conversation.io.InventoryConvIOFactory;
import org.betonquest.betonquest.conversation.io.SimpleConvIOFactory;
import org.betonquest.betonquest.conversation.io.SlowTellrawConvIOFactory;
import org.betonquest.betonquest.conversation.io.TellrawConvIOFactory;
import org.betonquest.betonquest.kernel.registry.feature.ConversationIORegistry;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.Set;

/**
 * The {@link AbstractCoreComponent} loading conversationIO types.
 */
public class ConversationIOTypesComponent extends AbstractCoreComponent {

    /**
     * Create a new ConversationIOTypesComponent.
     */
    public ConversationIOTypesComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class, PluginManager.class,
                BetonQuestLoggerFactory.class, ProfileProvider.class, ConfigAccessor.class,
                ConversationColors.class, DefaultFontRegistry.class, PluginMessage.class, ConversationIORegistry.class,
                Conversations.class, ItemManager.class, Instructions.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final Plugin plugin = getDependency(Plugin.class);
        final PluginManager pluginManager = getDependency(PluginManager.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final ProfileProvider profileProvider = getDependency(ProfileProvider.class);
        final ConversationColors colors = getDependency(ConversationColors.class);
        final ConfigAccessor config = getDependency(ConfigAccessor.class);
        final FontRegistry fontRegistry = getDependency(FontRegistry.class);
        final ConversationIORegistry conversationIORegistry = getDependency(ConversationIORegistry.class);
        final PluginMessage pluginMessage = getDependency(PluginMessage.class);
        final Conversations conversations = getDependency(Conversations.class);
        final ItemManager itemManager = getDependency(ItemManager.class);
        final Instructions instructions = getDependency(Instructions.class);

        conversationIORegistry.register("simple", new SimpleConvIOFactory(loggerFactory, config, plugin, pluginMessage, colors));
        conversationIORegistry.register("tellraw", new TellrawConvIOFactory(loggerFactory, config, plugin, pluginMessage, colors));
        final InventoryConvIOFactory.ConstructorParameters inventoryConvParams = new InventoryConvIOFactory.ConstructorParameters(
                loggerFactory, config, fontRegistry, colors, plugin, pluginManager, pluginMessage, instructions, conversations, itemManager, profileProvider);
        conversationIORegistry.register("chest", new InventoryConvIOFactory(inventoryConvParams, false));
        conversationIORegistry.register("combined", new InventoryConvIOFactory(inventoryConvParams, true));
        conversationIORegistry.register("slowtellraw", new SlowTellrawConvIOFactory(loggerFactory, config, plugin, pluginMessage, fontRegistry, colors));
    }
}
