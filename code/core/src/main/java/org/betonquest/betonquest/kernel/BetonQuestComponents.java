package org.betonquest.betonquest.kernel;

import org.betonquest.betonquest.api.dependency.CoreComponent;
import org.betonquest.betonquest.config.migrator.Migrator;
import org.betonquest.betonquest.kernel.component.ActionsComponent;
import org.betonquest.betonquest.kernel.component.ArgumentParsersComponent;
import org.betonquest.betonquest.kernel.component.AsyncSaverComponent;
import org.betonquest.betonquest.kernel.component.BStatsMetricsComponent;
import org.betonquest.betonquest.kernel.component.BetonQuestApiComponent;
import org.betonquest.betonquest.kernel.component.CancelersComponent;
import org.betonquest.betonquest.kernel.component.CommandsComponent;
import org.betonquest.betonquest.kernel.component.CompassComponent;
import org.betonquest.betonquest.kernel.component.CompatibilityComponent;
import org.betonquest.betonquest.kernel.component.ConditionsComponent;
import org.betonquest.betonquest.kernel.component.ConfigAccessorFactoryComponent;
import org.betonquest.betonquest.kernel.component.ConfigComponent;
import org.betonquest.betonquest.kernel.component.ConversationColorsComponent;
import org.betonquest.betonquest.kernel.component.ConversationsComponent;
import org.betonquest.betonquest.kernel.component.DataLoaderComponent;
import org.betonquest.betonquest.kernel.component.DatabaseComponent;
import org.betonquest.betonquest.kernel.component.ExecutionCacheComponent;
import org.betonquest.betonquest.kernel.component.FontRegistryComponent;
import org.betonquest.betonquest.kernel.component.GlobalDataComponent;
import org.betonquest.betonquest.kernel.component.IdentifiersComponent;
import org.betonquest.betonquest.kernel.component.InstructionsComponent;
import org.betonquest.betonquest.kernel.component.IntegrationComponent;
import org.betonquest.betonquest.kernel.component.ItemsComponent;
import org.betonquest.betonquest.kernel.component.JournalsComponent;
import org.betonquest.betonquest.kernel.component.LanguageProviderComponent;
import org.betonquest.betonquest.kernel.component.ListenersComponent;
import org.betonquest.betonquest.kernel.component.LogHandlerComponent;
import org.betonquest.betonquest.kernel.component.MigratorComponent;
import org.betonquest.betonquest.kernel.component.NotificationCategoriesComponent;
import org.betonquest.betonquest.kernel.component.NotificationsComponent;
import org.betonquest.betonquest.kernel.component.NpcsComponent;
import org.betonquest.betonquest.kernel.component.ObjectivesComponent;
import org.betonquest.betonquest.kernel.component.PersistenceComponent;
import org.betonquest.betonquest.kernel.component.PlaceholdersComponent;
import org.betonquest.betonquest.kernel.component.PlayerDataStorageComponent;
import org.betonquest.betonquest.kernel.component.PlayerHiderComponent;
import org.betonquest.betonquest.kernel.component.PluginMessageComponent;
import org.betonquest.betonquest.kernel.component.PostEnableComponent;
import org.betonquest.betonquest.kernel.component.ProfileProviderComponent;
import org.betonquest.betonquest.kernel.component.QuestPackageManagerComponent;
import org.betonquest.betonquest.kernel.component.RPGMenuComponent;
import org.betonquest.betonquest.kernel.component.ReloaderComponent;
import org.betonquest.betonquest.kernel.component.SchedulesComponent;
import org.betonquest.betonquest.kernel.component.TextParserComponent;
import org.betonquest.betonquest.kernel.component.TextSectionParserComponent;
import org.betonquest.betonquest.kernel.component.UpdaterComponent;
import org.betonquest.betonquest.kernel.component.VersionInfoComponent;
import org.betonquest.betonquest.kernel.component.types.ActionTypesComponent;
import org.betonquest.betonquest.kernel.component.types.ConditionTypesComponent;
import org.betonquest.betonquest.kernel.component.types.ConversationIOTypesComponent;
import org.betonquest.betonquest.kernel.component.types.InterceptorTypesComponent;
import org.betonquest.betonquest.kernel.component.types.ItemTypesComponent;
import org.betonquest.betonquest.kernel.component.types.NotifyIOTypesComponent;
import org.betonquest.betonquest.kernel.component.types.ObjectiveTypeComponent;
import org.betonquest.betonquest.kernel.component.types.PlaceholderTypeComponent;
import org.betonquest.betonquest.kernel.component.types.ScheduleTypesComponent;
import org.betonquest.betonquest.kernel.component.types.TextParserTypesComponent;
import org.betonquest.betonquest.lib.dependency.component.RequirementComponentWrapper;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Creates all components that are used by BetonQuest.
 */
public final class BetonQuestComponents {

    private BetonQuestComponents() {

    }

    /**
     * Create a set of all default components.
     *
     * @param betonQuestPluginFile the plugin file of BetonQuest obtained through {@link JavaPlugin#getFile()}.
     * @return a set of all default components
     */
    public static Set<CoreComponent> createDefaults(final File betonQuestPluginFile) {
        return Set.of(createEssentials(), createDefaultFeatures(), createDefaultTypes(),
                        createAdditionalFeatures(betonQuestPluginFile), createIntegrationsAndAPI())
                .stream().flatMap(Set::stream).collect(Collectors.toSet());
    }

    private static Set<CoreComponent> createEssentials() {
        return Set.of(
                new ConfigAccessorFactoryComponent(),
                new RequirementComponentWrapper(new ConfigComponent(), Migrator.class),
                new QuestPackageManagerComponent(),
                new ArgumentParsersComponent(),
                new InstructionsComponent(),
                new TextParserComponent(),
                new TextSectionParserComponent(),
                new PlayerDataStorageComponent(),
                new PluginMessageComponent(),
                new ProfileProviderComponent(),
                new LanguageProviderComponent(),
                new CommandsComponent(),
                new DatabaseComponent(),
                new AsyncSaverComponent(),
                new ListenersComponent(),
                new GlobalDataComponent()
        );
    }

    private static Set<CoreComponent> createDefaultFeatures() {
        return Set.of(
                new IdentifiersComponent(),
                new ConditionsComponent(),
                new ActionsComponent(),
                new ObjectivesComponent(),
                new PlaceholdersComponent(),
                new ItemsComponent(),
                new CompassComponent(),
                new ConversationsComponent(),
                new NpcsComponent(),
                new CancelersComponent(),
                new JournalsComponent(),
                new SchedulesComponent(),
                new NotificationsComponent(),
                new NotificationCategoriesComponent(),
                new RPGMenuComponent()
        );
    }

    private static Set<CoreComponent> createIntegrationsAndAPI() {
        return Set.of(
                new IntegrationComponent(),
                new CompatibilityComponent(),
                new PersistenceComponent(),
                new BetonQuestApiComponent()
        );
    }

    private static Set<CoreComponent> createAdditionalFeatures(final File betonQuestPluginFile) {
        return Set.of(
                new BStatsMetricsComponent(),
                new VersionInfoComponent(),
                new MigratorComponent(),
                new ReloaderComponent(),
                new PostEnableComponent(),
                new LogHandlerComponent(),
                new FontRegistryComponent(),
                new UpdaterComponent(betonQuestPluginFile),
                new PlayerHiderComponent(),
                new ConversationColorsComponent(),
                new ExecutionCacheComponent(),
                new DataLoaderComponent()
        );
    }

    private static Set<CoreComponent> createDefaultTypes() {
        return Set.of(
                new ActionTypesComponent(),
                new ConditionTypesComponent(),
                new ObjectiveTypeComponent(),
                new PlaceholderTypeComponent(),
                new ConversationIOTypesComponent(),
                new InterceptorTypesComponent(),
                new ItemTypesComponent(),
                new NotifyIOTypesComponent(),
                new ScheduleTypesComponent(),
                new TextParserTypesComponent()
        );
    }
}
