package org.betonquest.betonquest.compatibility.mmogroup.mmocore;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.condition.MMOCoreAttributeConditionFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.condition.MMOCoreClassConditionFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmocore.condition.MMOCoreProfessionLevelConditionFactory;
import org.betonquest.betonquest.kernel.registry.quest.ConditionTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.EventTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.ObjectiveTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.QuestTypeRegistries;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.jetbrains.annotations.Nullable;

/**
 * Integrator for MMO CORE.
 */
public class MMOCoreIntegrator implements Integrator {
    /**
     * Util class to get and validate attributes.
     */
    @Nullable
    private MMOCoreUtils mmoCoreUtils;

    /**
     * The default constructor.
     */
    public MMOCoreIntegrator() {

    }

    @Override
    public void hook() {
        mmoCoreUtils = new MMOCoreUtils(Bukkit.getPluginManager().getPlugin("MMOCore").getDataFolder());

        final BetonQuest plugin = BetonQuest.getInstance();
        final Server server = plugin.getServer();
        final PrimaryServerThreadData data = new PrimaryServerThreadData(server, server.getScheduler(), plugin);
        final QuestTypeRegistries questRegistries = plugin.getQuestRegistries();
        final ConditionTypeRegistry conditionTypes = questRegistries.condition();
        conditionTypes.register("mmoclass", new MMOCoreClassConditionFactory(data));
        conditionTypes.register("mmoattribute", new MMOCoreAttributeConditionFactory(data, mmoCoreUtils));
        conditionTypes.register("mmoprofession", new MMOCoreProfessionLevelConditionFactory(data));

        final ObjectiveTypeRegistry objectiveTypes = questRegistries.objective();
        objectiveTypes.register("mmoprofessionlevelup", MMOCoreProfessionObjective.class);
        objectiveTypes.register("mmochangeclass", MMOCoreChangeClassObjective.class);
        objectiveTypes.register("mmocorebreakblock", MMOCoreBreakCustomBlockObjective.class);

        final EventTypeRegistry eventTypes = questRegistries.event();
        eventTypes.register("mmoclassexperience", MMOCoreClassExperienceEvent.class);
        eventTypes.register("mmoprofessionexperience", MMOCoreProfessionExperienceEvent.class);
        eventTypes.register("mmocoreclasspoints", MMOCoreClassPointsEvent.class);
        eventTypes.register("mmocoreattributepoints", MMOCoreAttributePointsEvent.class);
        eventTypes.register("mmocoreattributereallocationpoints", MMOCoreAttributeReallocationPointsEvent.class);
        eventTypes.register("mmocoreskillpoints", MMOCoreSkillPointsEvent.class);
    }

    @Override
    public void reload() {
        if (mmoCoreUtils != null) {
            mmoCoreUtils.reload(Bukkit.getPluginManager().getPlugin("MMOCore").getDataFolder());
        }
    }

    @Override
    public void close() {
        // Empty
    }
}
