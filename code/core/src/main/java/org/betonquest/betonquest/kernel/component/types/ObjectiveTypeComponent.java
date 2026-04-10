package org.betonquest.betonquest.kernel.component.types;

import org.betonquest.betonquest.api.config.Translations;
import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.service.action.ActionManager;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.kernel.registry.quest.ObjectiveTypeRegistry;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;
import org.betonquest.betonquest.quest.objective.action.ActionObjectiveFactory;
import org.betonquest.betonquest.quest.objective.arrow.ArrowShootObjectiveFactory;
import org.betonquest.betonquest.quest.objective.block.BlockObjectiveFactory;
import org.betonquest.betonquest.quest.objective.breed.BreedObjectiveFactory;
import org.betonquest.betonquest.quest.objective.brew.BrewObjectiveFactory;
import org.betonquest.betonquest.quest.objective.chestput.ChestPutObjectiveFactory;
import org.betonquest.betonquest.quest.objective.command.CommandObjectiveFactory;
import org.betonquest.betonquest.quest.objective.consume.ConsumeObjectiveFactory;
import org.betonquest.betonquest.quest.objective.crafting.CraftingObjectiveFactory;
import org.betonquest.betonquest.quest.objective.data.PointObjectiveFactory;
import org.betonquest.betonquest.quest.objective.data.TagObjectiveFactory;
import org.betonquest.betonquest.quest.objective.delay.DelayObjectiveFactory;
import org.betonquest.betonquest.quest.objective.die.DieObjectiveFactory;
import org.betonquest.betonquest.quest.objective.enchant.EnchantObjectiveFactory;
import org.betonquest.betonquest.quest.objective.equip.EquipItemObjectiveFactory;
import org.betonquest.betonquest.quest.objective.experience.ExperienceObjectiveFactory;
import org.betonquest.betonquest.quest.objective.fish.FishObjectiveFactory;
import org.betonquest.betonquest.quest.objective.interact.EntityInteractObjectiveFactory;
import org.betonquest.betonquest.quest.objective.jump.JumpObjectiveFactory;
import org.betonquest.betonquest.quest.objective.kill.KillPlayerObjectiveFactory;
import org.betonquest.betonquest.quest.objective.kill.MobKillObjectiveFactory;
import org.betonquest.betonquest.quest.objective.location.LocationObjectiveFactory;
import org.betonquest.betonquest.quest.objective.login.LoginObjectiveFactory;
import org.betonquest.betonquest.quest.objective.logout.LogoutObjectiveFactory;
import org.betonquest.betonquest.quest.objective.npc.NpcInteractObjectiveFactory;
import org.betonquest.betonquest.quest.objective.npc.NpcRangeObjectiveFactory;
import org.betonquest.betonquest.quest.objective.password.PasswordObjectiveFactory;
import org.betonquest.betonquest.quest.objective.pickup.PickupObjectiveFactory;
import org.betonquest.betonquest.quest.objective.resourcepack.ResourcepackObjectiveFactory;
import org.betonquest.betonquest.quest.objective.ride.RideObjectiveFactory;
import org.betonquest.betonquest.quest.objective.shear.ShearObjectiveFactory;
import org.betonquest.betonquest.quest.objective.smelt.SmeltingObjectiveFactory;
import org.betonquest.betonquest.quest.objective.stage.StageObjectiveFactory;
import org.betonquest.betonquest.quest.objective.step.StepObjectiveFactory;
import org.betonquest.betonquest.quest.objective.tame.TameObjectiveFactory;
import org.betonquest.betonquest.quest.objective.timer.TimerObjectiveFactory;
import org.betonquest.betonquest.quest.objective.variable.VariableObjectiveFactory;
import org.bukkit.plugin.Plugin;

import java.util.Set;

/**
 * The {@link AbstractCoreComponent} loading all objective types.
 */
public class ObjectiveTypeComponent extends AbstractCoreComponent {

    /**
     * Create a new ObjectiveTypeComponent.
     */
    public ObjectiveTypeComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(BetonQuestLoggerFactory.class, ProfileProvider.class,
                PluginMessage.class, PlayerDataStorage.class,
                ObjectiveTypeRegistry.class, ActionManager.class, ConditionManager.class, Plugin.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final ProfileProvider profileProvider = getDependency(ProfileProvider.class);
        final Translations translations = getDependency(PluginMessage.class);
        final PlayerDataStorage dataStorage = getDependency(PlayerDataStorage.class);
        final ObjectiveTypeRegistry objectiveTypes = getDependency(ObjectiveTypeRegistry.class);
        final ActionManager actionManager = getDependency(ActionManager.class);
        final ConditionManager conditionManager = getDependency(ConditionManager.class);
        final Plugin plugin = getDependency(Plugin.class);

        objectiveTypes.register("action", new ActionObjectiveFactory());
        objectiveTypes.register("arrow", new ArrowShootObjectiveFactory(plugin));
        objectiveTypes.register("block", new BlockObjectiveFactory(loggerFactory, translations));
        objectiveTypes.register("breed", new BreedObjectiveFactory());
        objectiveTypes.register("brew", new BrewObjectiveFactory(plugin, profileProvider));
        objectiveTypes.register("chestput", new ChestPutObjectiveFactory(loggerFactory, translations));
        objectiveTypes.register("command", new CommandObjectiveFactory(actionManager));
        objectiveTypes.register("consume", new ConsumeObjectiveFactory());
        objectiveTypes.register("craft", new CraftingObjectiveFactory());
        objectiveTypes.register("delay", new DelayObjectiveFactory());
        objectiveTypes.register("die", new DieObjectiveFactory());
        objectiveTypes.register("enchant", new EnchantObjectiveFactory());
        objectiveTypes.register("experience", new ExperienceObjectiveFactory(loggerFactory, translations));
        objectiveTypes.register("fish", new FishObjectiveFactory());
        objectiveTypes.register("interact", new EntityInteractObjectiveFactory());
        objectiveTypes.register("kill", new KillPlayerObjectiveFactory(conditionManager));
        objectiveTypes.register("location", new LocationObjectiveFactory());
        objectiveTypes.register("login", new LoginObjectiveFactory());
        objectiveTypes.register("logout", new LogoutObjectiveFactory());
        objectiveTypes.register("mobkill", new MobKillObjectiveFactory());
        objectiveTypes.register("npcinteract", new NpcInteractObjectiveFactory());
        objectiveTypes.register("npcrange", new NpcRangeObjectiveFactory());
        objectiveTypes.register("password", new PasswordObjectiveFactory(actionManager));
        objectiveTypes.register("pickup", new PickupObjectiveFactory());
        objectiveTypes.register("point", new PointObjectiveFactory(dataStorage));
        objectiveTypes.register("ride", new RideObjectiveFactory());
        objectiveTypes.register("shear", new ShearObjectiveFactory());
        objectiveTypes.register("smelt", new SmeltingObjectiveFactory());
        objectiveTypes.register("stage", new StageObjectiveFactory());
        objectiveTypes.register("step", new StepObjectiveFactory());
        objectiveTypes.register("tag", new TagObjectiveFactory(dataStorage));
        objectiveTypes.register("tame", new TameObjectiveFactory());
        objectiveTypes.register("timer", new TimerObjectiveFactory(actionManager));
        objectiveTypes.register("variable", new VariableObjectiveFactory());
        objectiveTypes.register("equip", new EquipItemObjectiveFactory());
        objectiveTypes.register("jump", new JumpObjectiveFactory());
        objectiveTypes.register("resourcepack", new ResourcepackObjectiveFactory());
    }
}
