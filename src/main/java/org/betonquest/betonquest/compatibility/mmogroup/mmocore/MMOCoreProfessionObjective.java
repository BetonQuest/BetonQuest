package org.betonquest.betonquest.compatibility.mmogroup.mmocore;

import net.Indyuce.mmocore.api.event.PlayerLevelUpEvent;
import net.Indyuce.mmocore.experience.Profession;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.util.PlayerConverter;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("PMD.CommentRequired")
public class MMOCoreProfessionObjective extends Objective implements Listener {
    @Nullable
    private final String professionName;

    private final VariableNumber targetLevel;

    public MMOCoreProfessionObjective(final Instruction instruction) throws QuestException {
        super(instruction);
        final String profession = instruction.next();
        professionName = "MAIN".equalsIgnoreCase(profession) ? null : profession;
        targetLevel = instruction.get(VariableNumber::new);
    }

    @EventHandler(ignoreCancelled = true)
    public void onLevelUp(final PlayerLevelUpEvent event) {
        final OnlineProfile onlineProfile = PlayerConverter.getID(event.getPlayer());
        if (!containsPlayer(onlineProfile) || !checkConditions(onlineProfile)) {
            return;
        }
        final Profession profession = event.getProfession();
        if (profession != null && !profession.getName().equalsIgnoreCase(professionName)) {
            return;
        }
        if (event.getNewLevel() < targetLevel.getInt(onlineProfile)) {
            return;
        }
        completeObjective(onlineProfile);
    }

    @Override
    public void start() {
        Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public String getDefaultDataInstruction() {
        return "";
    }

    @Override
    public String getProperty(final String name, final Profile profile) {
        return "";
    }
}
