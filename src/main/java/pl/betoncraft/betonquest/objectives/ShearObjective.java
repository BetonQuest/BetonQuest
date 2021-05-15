package pl.betoncraft.betonquest.objectives;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerShearEntityEvent;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.utils.LogUtils;
import pl.betoncraft.betonquest.utils.PlayerConverter;

import java.util.logging.Level;
import java.util.regex.Pattern;

/**
 * Requires the player to shear a sheep.
 */
@SuppressWarnings("PMD.CommentRequired")
public class ShearObjective extends Objective implements Listener {

    private final String color;
    private final int amount;
    private final boolean notify;
    private final int notifyInterval;
    private final Pattern underscore = Pattern.compile("(?<!\\\\)_");
    private final Pattern escapedUnderscore = Pattern.compile("(\\\\)_");
    private String name;

    public ShearObjective(final Instruction instruction) throws InstructionParseException {
        super(instruction);
        template = SheepData.class;

        amount = instruction.getPositive();

        final String rawName = instruction.getOptional("name");
        if (rawName != null) {
            name = underscore.matcher(rawName).replaceAll(" ");
            name = escapedUnderscore.matcher(name).replaceAll("_");
        }

        color = instruction.getOptional("color");
        notifyInterval = instruction.getInt(instruction.getOptional("notify"), 1);
        notify = instruction.hasArgument("notify") || notifyInterval > 1;
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity"})
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    @EventHandler(ignoreCancelled = true)
    public void onShear(final PlayerShearEntityEvent event) {
        if (event.getEntity().getType() != EntityType.SHEEP) {
            return;
        }
        final String playerID = PlayerConverter.getID(event.getPlayer());
        if (!containsPlayer(playerID)) {
            return;
        }
        if (name != null && (event.getEntity().getCustomName() == null || !event.getEntity().getCustomName().equals(name))) {
            return;
        }
        if (color != null && !((Sheep) event.getEntity()).getColor().toString().equalsIgnoreCase(color)) {
            return;
        }
        final SheepData data = (SheepData) dataMap.get(playerID);

        if (checkConditions(playerID)) {
            data.shearSheep();
            // complete quest or notify
            if (data.getAmount() <= 0) {
                completeObjective(playerID);
            } else if (notify && data.getAmount() % notifyInterval == 0) {
                try {
                    Config.sendNotify(instruction.getPackage().getName(), playerID, "sheep_to_shear", new String[]{String.valueOf(data.getAmount())},
                            "sheep_to_shear,info");
                } catch (final QuestRuntimeException exception) {
                    try {
                        LogUtils.getLogger().log(Level.WARNING, "The notify system was unable to play a sound for the 'sheep_to_shear' category in '" + instruction.getObjective().getFullID() + "'. Error was: '" + exception.getMessage() + "'");
                    } catch (final InstructionParseException exep) {
                        LogUtils.logThrowableReport(exep);
                    }
                }
            }
        }
    }

    @Override
    public String getProperty(final String name, final String playerID) {
        if ("left".equalsIgnoreCase(name)) {
            return Integer.toString(((SheepData) dataMap.get(playerID)).getAmount());
        } else if ("amount".equalsIgnoreCase(name)) {
            return Integer.toString(amount - ((SheepData) dataMap.get(playerID)).getAmount());
        }
        return "";
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

    public static class SheepData extends ObjectiveData {

        private int amount;

        public SheepData(final String instruction, final String playerID, final String objID) {
            super(instruction, playerID, objID);
            amount = Integer.parseInt(instruction);
        }

        public void shearSheep() {
            amount--;
            update();
        }

        public int getAmount() {
            return amount;
        }

        @Override
        public String toString() {
            return String.valueOf(amount);
        }

    }

}
