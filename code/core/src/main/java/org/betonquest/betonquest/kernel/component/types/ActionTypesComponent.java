package org.betonquest.betonquest.kernel.component.types;

import org.betonquest.betonquest.api.LanguageProvider;
import org.betonquest.betonquest.api.data.Persistence;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.npc.DefaultNpcHider;
import org.betonquest.betonquest.api.service.action.ActionManager;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.betonquest.betonquest.api.service.conversation.Conversations;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.api.service.npc.NpcManager;
import org.betonquest.betonquest.api.service.objective.ObjectiveManager;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.GlobalData;
import org.betonquest.betonquest.database.PlayerDataFactory;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.kernel.processor.feature.CancelerProcessor;
import org.betonquest.betonquest.kernel.processor.feature.CompassProcessor;
import org.betonquest.betonquest.kernel.registry.quest.ActionTypeRegistry;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.betonquest.betonquest.quest.action.burn.BurnActionFactory;
import org.betonquest.betonquest.quest.action.cancel.CancelActionFactory;
import org.betonquest.betonquest.quest.action.chat.ChatActionFactory;
import org.betonquest.betonquest.quest.action.chest.ChestClearActionFactory;
import org.betonquest.betonquest.quest.action.chest.ChestGiveActionFactory;
import org.betonquest.betonquest.quest.action.chest.ChestTakeActionFactory;
import org.betonquest.betonquest.quest.action.command.CommandActionFactory;
import org.betonquest.betonquest.quest.action.command.OpSudoActionFactory;
import org.betonquest.betonquest.quest.action.command.SudoActionFactory;
import org.betonquest.betonquest.quest.action.compass.CompassActionFactory;
import org.betonquest.betonquest.quest.action.conversation.CancelConversationActionFactory;
import org.betonquest.betonquest.quest.action.conversation.ConversationActionFactory;
import org.betonquest.betonquest.quest.action.damage.DamageActionFactory;
import org.betonquest.betonquest.quest.action.door.DoorActionFactory;
import org.betonquest.betonquest.quest.action.drop.DropActionFactory;
import org.betonquest.betonquest.quest.action.effect.DeleteEffectActionFactory;
import org.betonquest.betonquest.quest.action.effect.EffectActionFactory;
import org.betonquest.betonquest.quest.action.entity.RemoveEntityActionFactory;
import org.betonquest.betonquest.quest.action.eval.EvalActionFactory;
import org.betonquest.betonquest.quest.action.experience.ExperienceActionFactory;
import org.betonquest.betonquest.quest.action.explosion.ExplosionActionFactory;
import org.betonquest.betonquest.quest.action.folder.FolderActionFactory;
import org.betonquest.betonquest.quest.action.give.GiveActionFactory;
import org.betonquest.betonquest.quest.action.hunger.HungerActionFactory;
import org.betonquest.betonquest.quest.action.item.ItemDurabilityActionFactory;
import org.betonquest.betonquest.quest.action.journal.GiveJournalActionFactory;
import org.betonquest.betonquest.quest.action.journal.JournalActionFactory;
import org.betonquest.betonquest.quest.action.kill.KillActionFactory;
import org.betonquest.betonquest.quest.action.language.LanguageActionFactory;
import org.betonquest.betonquest.quest.action.lever.LeverActionFactory;
import org.betonquest.betonquest.quest.action.lightning.LightningActionFactory;
import org.betonquest.betonquest.quest.action.log.LogActionFactory;
import org.betonquest.betonquest.quest.action.logic.FirstActionFactory;
import org.betonquest.betonquest.quest.action.logic.IfElseActionFactory;
import org.betonquest.betonquest.quest.action.notify.NotifyActionFactory;
import org.betonquest.betonquest.quest.action.notify.NotifyAllActionFactory;
import org.betonquest.betonquest.quest.action.npc.NpcTeleportActionFactory;
import org.betonquest.betonquest.quest.action.npc.UpdateVisibilityNowActionFactory;
import org.betonquest.betonquest.quest.action.objective.ObjectiveActionFactory;
import org.betonquest.betonquest.quest.action.party.PartyActionFactory;
import org.betonquest.betonquest.quest.action.point.DeleteGlobalPointActionFactory;
import org.betonquest.betonquest.quest.action.point.DeletePointActionFactory;
import org.betonquest.betonquest.quest.action.point.GlobalPointActionFactory;
import org.betonquest.betonquest.quest.action.point.PointActionFactory;
import org.betonquest.betonquest.quest.action.random.PickRandomActionFactory;
import org.betonquest.betonquest.quest.action.run.RunActionFactory;
import org.betonquest.betonquest.quest.action.run.RunForAllActionFactory;
import org.betonquest.betonquest.quest.action.run.RunIndependentActionFactory;
import org.betonquest.betonquest.quest.action.scoreboard.ScoreboardObjectiveActionFactory;
import org.betonquest.betonquest.quest.action.scoreboard.ScoreboardTagActionFactory;
import org.betonquest.betonquest.quest.action.setblock.SetBlockActionFactory;
import org.betonquest.betonquest.quest.action.spawn.SpawnMobActionFactory;
import org.betonquest.betonquest.quest.action.stage.StageActionFactory;
import org.betonquest.betonquest.quest.action.tag.TagGlobalActionFactory;
import org.betonquest.betonquest.quest.action.tag.TagPlayerActionFactory;
import org.betonquest.betonquest.quest.action.take.TakeActionFactory;
import org.betonquest.betonquest.quest.action.teleport.TeleportActionFactory;
import org.betonquest.betonquest.quest.action.time.TimeActionFactory;
import org.betonquest.betonquest.quest.action.variable.VariableActionFactory;
import org.betonquest.betonquest.quest.action.velocity.VelocityActionFactory;
import org.betonquest.betonquest.quest.action.weather.WeatherActionFactory;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

import java.time.InstantSource;
import java.util.Set;

/**
 * The {@link AbstractCoreComponent} loading all action types.
 */
@SuppressWarnings({"PMD.CouplingBetweenObjects", "PMD.NcssCount"})
public class ActionTypesComponent extends AbstractCoreComponent {

    /**
     * Create a new ActionTypesComponent.
     */
    public ActionTypesComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class, PluginManager.class, Server.class, BukkitScheduler.class,
                BetonQuestLoggerFactory.class, ProfileProvider.class, PlayerDataFactory.class,
                PlayerDataStorage.class, GlobalData.class, PluginMessage.class, LanguageProvider.class,
                Saver.class, TextParser.class, Instructions.class, Persistence.class,
                ActionTypeRegistry.class, Conversations.class, ActionManager.class, ConditionManager.class,
                ObjectiveManager.class, NpcManager.class, CompassProcessor.class, CancelerProcessor.class,
                DefaultNpcHider.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final ProfileProvider profileProvider = getDependency(ProfileProvider.class);
        final PlayerDataFactory playerDataFactory = getDependency(PlayerDataFactory.class);
        final GlobalData globalData = getDependency(GlobalData.class);
        final PlayerDataStorage playerDataStorage = getDependency(PlayerDataStorage.class);
        final Persistence persistence = getDependency(Persistence.class);
        final PluginMessage pluginMessage = getDependency(PluginMessage.class);
        final LanguageProvider languageProvider = getDependency(LanguageProvider.class);
        final TextParser textParser = getDependency(TextParser.class);
        final Saver saver = getDependency(Saver.class);
        final Plugin plugin = getDependency(Plugin.class);
        final Server server = getDependency(Server.class);
        final PluginManager pluginManager = getDependency(PluginManager.class);
        final BukkitScheduler scheduler = getDependency(BukkitScheduler.class);
        final Conversations conversations = getDependency(Conversations.class);
        final Instructions instructions = getDependency(Instructions.class);
        final ActionTypeRegistry actionTypes = getDependency(ActionTypeRegistry.class);
        final ActionManager actionManager = getDependency(ActionManager.class);
        final ConditionManager conditionManager = getDependency(ConditionManager.class);
        final ObjectiveManager objectiveManager = getDependency(ObjectiveManager.class);
        final NpcManager npcManager = getDependency(NpcManager.class);
        final CompassProcessor compassProcessor = getDependency(CompassProcessor.class);
        final CancelerProcessor cancelerProcessor = getDependency(CancelerProcessor.class);
        final DefaultNpcHider npcHider = getDependency(DefaultNpcHider.class);

        actionTypes.register("burn", new BurnActionFactory());
        actionTypes.register("cancel", new CancelActionFactory(cancelerProcessor));
        actionTypes.register("cancelconversation", new CancelConversationActionFactory(conversations));
        actionTypes.register("chat", new ChatActionFactory());
        actionTypes.registerCombined("chestclear", new ChestClearActionFactory());
        actionTypes.registerCombined("chestgive", new ChestGiveActionFactory());
        actionTypes.registerCombined("chesttake", new ChestTakeActionFactory());
        actionTypes.register("compass", new CompassActionFactory(compassProcessor, persistence));
        actionTypes.registerCombined("command", new CommandActionFactory(loggerFactory, server));
        actionTypes.register("conversation", new ConversationActionFactory(conversations));
        actionTypes.register("damage", new DamageActionFactory());
        actionTypes.register("deleffect", new DeleteEffectActionFactory());
        actionTypes.registerCombined("deleteglobalpoint", new DeleteGlobalPointActionFactory(globalData));
        actionTypes.registerCombined("deletepoint", new DeletePointActionFactory(playerDataStorage, saver, profileProvider));
        actionTypes.registerCombined("door", new DoorActionFactory());
        actionTypes.registerCombined("drop", new DropActionFactory(profileProvider));
        actionTypes.register("effect", new EffectActionFactory());
        actionTypes.registerCombined("eval", new EvalActionFactory(instructions, actionTypes, scheduler, plugin));
        actionTypes.register("experience", new ExperienceActionFactory());
        actionTypes.registerCombined("explosion", new ExplosionActionFactory());
        actionTypes.registerCombined("folder", new FolderActionFactory(plugin, loggerFactory, pluginManager,
                actionManager, conditionManager));
        actionTypes.registerCombined("first", new FirstActionFactory(actionManager));
        actionTypes.register("give", new GiveActionFactory(loggerFactory, playerDataStorage, pluginMessage));
        actionTypes.register("givejournal", new GiveJournalActionFactory(playerDataStorage));
        actionTypes.registerCombined("globaltag", new TagGlobalActionFactory(globalData));
        actionTypes.registerCombined("globalpoint", new GlobalPointActionFactory(globalData));
        actionTypes.register("hunger", new HungerActionFactory());
        actionTypes.registerCombined("if", new IfElseActionFactory(actionManager, conditionManager));
        actionTypes.register("itemdurability", new ItemDurabilityActionFactory());
        actionTypes.registerCombined("journal", new JournalActionFactory(loggerFactory, pluginMessage, playerDataStorage,
                InstantSource.system(), saver, profileProvider));
        actionTypes.register("kill", new KillActionFactory());
        actionTypes.register("language", new LanguageActionFactory(playerDataStorage));
        actionTypes.registerCombined("lever", new LeverActionFactory());
        actionTypes.registerCombined("lightning", new LightningActionFactory());
        actionTypes.registerCombined("log", new LogActionFactory(loggerFactory));
        actionTypes.register("notify", new NotifyActionFactory(textParser, playerDataStorage, languageProvider));
        actionTypes.registerCombined("notifyall", new NotifyAllActionFactory(textParser, playerDataStorage, profileProvider, languageProvider));
        actionTypes.registerCombined("npcteleport", new NpcTeleportActionFactory(npcManager));
        actionTypes.registerCombined("objective", new ObjectiveActionFactory(plugin, loggerFactory, profileProvider, saver,
                objectiveManager, playerDataStorage, playerDataFactory));
        actionTypes.register("opsudo", new OpSudoActionFactory(server));
        actionTypes.register("party", new PartyActionFactory(profileProvider, actionManager, conditionManager));
        actionTypes.registerCombined("pickrandom", new PickRandomActionFactory(actionManager));
        actionTypes.register("point", new PointActionFactory(loggerFactory, playerDataStorage,
                pluginMessage));
        actionTypes.registerCombined("removeentity", new RemoveEntityActionFactory());
        actionTypes.registerCombined("run", new RunActionFactory(instructions, actionTypes));
        actionTypes.register("runForAll", new RunForAllActionFactory(profileProvider, actionManager, conditionManager));
        actionTypes.register("runIndependent", new RunIndependentActionFactory(actionManager));
        actionTypes.registerCombined("setblock", new SetBlockActionFactory());
        actionTypes.register("score", new ScoreboardObjectiveActionFactory());
        actionTypes.register("scoretag", new ScoreboardTagActionFactory());
        actionTypes.registerCombined("spawn", new SpawnMobActionFactory());
        actionTypes.register("stage", new StageActionFactory(objectiveManager));
        actionTypes.register("sudo", new SudoActionFactory(server));
        actionTypes.registerCombined("tag", new TagPlayerActionFactory(persistence, saver, profileProvider));
        actionTypes.register("take", new TakeActionFactory(loggerFactory, pluginMessage));
        actionTypes.register("teleport", new TeleportActionFactory(conversations));
        actionTypes.registerCombined("time", new TimeActionFactory());
        actionTypes.register("updatevisibility", new UpdateVisibilityNowActionFactory(npcHider));
        actionTypes.register("variable", new VariableActionFactory(objectiveManager));
        actionTypes.register("velocity", new VelocityActionFactory());
        actionTypes.registerCombined("weather", new WeatherActionFactory());
    }
}
