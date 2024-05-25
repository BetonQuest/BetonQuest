package org.betonquest.betonquest.quest.registry;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.conditions.AdvancementCondition;
import org.betonquest.betonquest.conditions.AlternativeCondition;
import org.betonquest.betonquest.conditions.ArmorCondition;
import org.betonquest.betonquest.conditions.BiomeCondition;
import org.betonquest.betonquest.conditions.BurningCondition;
import org.betonquest.betonquest.conditions.CheckCondition;
import org.betonquest.betonquest.conditions.ChestItemCondition;
import org.betonquest.betonquest.conditions.ConjunctionCondition;
import org.betonquest.betonquest.conditions.ConversationCondition;
import org.betonquest.betonquest.conditions.DayOfWeekCondition;
import org.betonquest.betonquest.conditions.EffectCondition;
import org.betonquest.betonquest.conditions.EmptySlotsCondition;
import org.betonquest.betonquest.conditions.EntityCondition;
import org.betonquest.betonquest.conditions.ExperienceCondition;
import org.betonquest.betonquest.conditions.FacingCondition;
import org.betonquest.betonquest.conditions.FlyingCondition;
import org.betonquest.betonquest.conditions.GameModeCondition;
import org.betonquest.betonquest.conditions.GlobalPointCondition;
import org.betonquest.betonquest.conditions.GlobalTagCondition;
import org.betonquest.betonquest.conditions.HandCondition;
import org.betonquest.betonquest.conditions.HealthCondition;
import org.betonquest.betonquest.conditions.HeightCondition;
import org.betonquest.betonquest.conditions.HungerCondition;
import org.betonquest.betonquest.conditions.InConversationCondition;
import org.betonquest.betonquest.conditions.ItemCondition;
import org.betonquest.betonquest.conditions.ItemDurabilityCondition;
import org.betonquest.betonquest.conditions.JournalCondition;
import org.betonquest.betonquest.conditions.LanguageCondition;
import org.betonquest.betonquest.conditions.LocationCondition;
import org.betonquest.betonquest.conditions.LookingAtCondition;
import org.betonquest.betonquest.conditions.MooncycleCondition;
import org.betonquest.betonquest.conditions.NumberCompareCondition;
import org.betonquest.betonquest.conditions.ObjectiveCondition;
import org.betonquest.betonquest.conditions.PartialDateCondition;
import org.betonquest.betonquest.conditions.PartyCondition;
import org.betonquest.betonquest.conditions.PermissionCondition;
import org.betonquest.betonquest.conditions.PointCondition;
import org.bukkit.Server;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Registers the Conditions, Events, Objectives and Variables that come with BetonQuest.
 */
public class CoreQuestTypes {
    /**
     * Logger Factory to create new custom Logger from.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Server used for primary server thread access.
     */
    private final Server server;

    /**
     * Scheduler used for primary server thread access.
     */
    private final BukkitScheduler scheduler;

    /**
     * Plugin used for primary server thread access, type registration and general usage.
     */
    private final BetonQuest plugin;

    /**
     * Create a new Core Quest Types class for registering.
     *
     * @param loggerFactory used in event factories
     * @param server        the server used for primary server thread access.
     * @param scheduler     used in event factories
     * @param plugin        used in event factories and for objective type registration
     */
    public CoreQuestTypes(final BetonQuestLoggerFactory loggerFactory,
                          final Server server, final BukkitScheduler scheduler, final BetonQuest plugin) {
        this.loggerFactory = loggerFactory;
        this.server = server;
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    /**
     * Registers the Quest Types.
     */
    public void register() {
        // When adding new types they need to be ordered by name in the corresponding method!
        registerConditions();
        registerEvents();
        registerObjectives();
        registerVariables();
    }

    private void registerConditions() {
        plugin.registerConditions("advancement", AdvancementCondition.class);
        plugin.registerConditions("and", ConjunctionCondition.class);
        plugin.registerConditions("armor", ArmorCondition.class);
        plugin.registerConditions("biome", BiomeCondition.class);
        plugin.registerConditions("burning", BurningCondition.class);
        plugin.registerConditions("check", CheckCondition.class);
        plugin.registerConditions("chestitem", ChestItemCondition.class);
        plugin.registerConditions("conversation", ConversationCondition.class);
        plugin.registerConditions("dayofweek", DayOfWeekCondition.class);
        plugin.registerConditions("effect", EffectCondition.class);
        plugin.registerConditions("empty", EmptySlotsCondition.class);
        plugin.registerConditions("entities", EntityCondition.class);
        plugin.registerConditions("experience", ExperienceCondition.class);
        plugin.registerConditions("facing", FacingCondition.class);
        plugin.registerConditions("fly", FlyingCondition.class);
        plugin.registerConditions("gamemode", GameModeCondition.class);
        plugin.registerConditions("globalpoint", GlobalPointCondition.class);
        plugin.registerConditions("globaltag", GlobalTagCondition.class);
        plugin.registerConditions("hand", HandCondition.class);
        plugin.registerConditions("health", HealthCondition.class);
        plugin.registerConditions("height", HeightCondition.class);
        plugin.registerConditions("hunger", HungerCondition.class);
        plugin.registerConditions("inconversation", InConversationCondition.class);
        plugin.registerConditions("item", ItemCondition.class);
        plugin.registerConditions("itemdurability", ItemDurabilityCondition.class);
        plugin.registerConditions("journal", JournalCondition.class);
        plugin.registerConditions("language", LanguageCondition.class);
        plugin.registerConditions("location", LocationCondition.class);
        plugin.registerConditions("looking", LookingAtCondition.class);
        plugin.registerConditions("mooncycle", MooncycleCondition.class);
        plugin.registerConditions("numbercompare", NumberCompareCondition.class);
        plugin.registerConditions("objective", ObjectiveCondition.class);
        plugin.registerConditions("or", AlternativeCondition.class);
        plugin.registerConditions("partialdate", PartialDateCondition.class);
        plugin.registerConditions("party", PartyCondition.class);
        plugin.registerConditions("permission", PermissionCondition.class);
        plugin.registerConditions("point", PointCondition.class);
    }

    private void registerEvents() {
    }

    private void registerObjectives() {
    }

    private void registerVariables() {
    }
}
