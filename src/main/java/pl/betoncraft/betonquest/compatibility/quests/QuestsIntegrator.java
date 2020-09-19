package pl.betoncraft.betonquest.compatibility.quests;

import me.blackvein.quests.Quests;
import org.bukkit.Bukkit;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.compatibility.Integrator;


public class QuestsIntegrator implements Integrator {

    private static Quests questsInstance;
    private final BetonQuest plugin;

    public QuestsIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    public static Quests getQuestsInstance() {
        return questsInstance;
    }

    @Override
    public void hook() {
        questsInstance = (Quests) Bukkit.getPluginManager().getPlugin("Quests");
        plugin.registerConditions("quest", QuestCondition.class);
        plugin.registerEvents("quest", QuestEvent.class);
        questsInstance.getCustomRewards().add(new EventReward());
        questsInstance.getCustomRequirements().add(new ConditionRequirement());
    }

    @Override
    public void reload() {

    }

    @Override
    public void close() {

    }

}
