package org.betonquest.betonquest.objectives;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.MobKillNotifier.MobKilledEvent;
import org.betonquest.betonquest.api.Objective;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

/**
 * Player has to kill specified amount of specified mobs. It can also require
 * the player to kill specifically named mobs and notify them about the required
 * amount.
 */
@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class MobKillObjective extends Objective implements Listener {

    private final int notifyInterval;
    protected EntityType mobType;
    protected int amount;
    protected String name;
    protected String marked;
    protected boolean notify;

    public MobKillObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = MobData.class;
        mobType = instruction.getEnum(EntityType.class);
        amount = instruction.getPositive();
        name = instruction.getOptional("name");
        if (name != null) {
            name = Utils.format(name, true, false).replace('_', ' ');
        }
        marked = instruction.getOptional("marked");
        if (marked != null) {
            marked = Utils.addPackage(instruction.getPackage(), marked);
        }
        notifyInterval = instruction.getInt(instruction.getOptional("notify"), 1);
        notify = instruction.hasArgument("notify") || notifyInterval > 1;
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    @EventHandler(ignoreCancelled = true)
    public void onMobKill(final MobKilledEvent event) {
        // check if it's the right entity type
        if (!event.getEntity().getType().equals(mobType)) {
            return;
        }
        // if the entity should have a name and it does not match, return
        if (name != null && (event.getEntity().getCustomName() == null ||
                !event.getEntity().getCustomName().equals(name))) {
            return;
        }
        // check if the entity is correctly marked
        if (marked != null) {
            if (!event.getEntity().hasMetadata("betonquest-marked")) {
                return;
            }
            final List<MetadataValue> meta = event.getEntity().getMetadata("betonquest-marked");
            for (final MetadataValue m : meta) {
                if (!m.asString().equals(marked)) {
                    return;
                }
            }
        }
        // check if the player has this objective
        final String playerID = PlayerConverter.getID(event.getPlayer());
        if (containsPlayer(playerID) && checkConditions(playerID)) {
            // the right mob was killed, handle data update
            final MobData playerData = (MobData) dataMap.get(playerID);
            playerData.subtract();
            if (playerData.isZero()) {
                completeObjective(playerID);
            } else if (notify && playerData.getAmount() % notifyInterval == 0) {
                // send a notification
                try {
                    Config.sendNotify(instruction.getPackage().getName(), playerID, "mobs_to_kill", new String[]{String.valueOf(playerData.getAmount())},
                            "mobs_to_kill,info");
                } catch (final QuestRuntimeException exception) {
                    try {
                        LOG.warning(instruction.getPackage(), "The notify system was unable to play a sound for the 'mobs_to_kill' category in '" + instruction.getObjective().getFullID() + "'. Error was: '" + exception.getMessage() + "'");
                    } catch (final InstructionParseException e) {
                        LOG.reportException(instruction.getPackage(), e);
                    }
                }
            }
        }
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
        return Integer.toString(amount);
    }

    @Override
    public String getProperty(final String name, final String playerID) {
        if ("left".equalsIgnoreCase(name)) {
            return Integer.toString(((MobData) dataMap.get(playerID)).getAmount());
        } else if ("amount".equalsIgnoreCase(name)) {
            return Integer.toString(amount - ((MobData) dataMap.get(playerID)).getAmount());
        }
        return "";
    }

    public static class MobData extends ObjectiveData {

        private int amount;

        public MobData(final String instruction, final String playerID, final String objID) {
            super(instruction, playerID, objID);
            amount = Integer.parseInt(instruction);
        }

        public int getAmount() {
            return amount;
        }

        public void subtract() {
            amount--;
            update();
        }

        public boolean isZero() {
            return amount <= 0;
        }

        @Override
        public String toString() {
            return Integer.toString(amount);
        }

    }
}
