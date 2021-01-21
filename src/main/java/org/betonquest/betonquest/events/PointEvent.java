package org.betonquest.betonquest.events;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.Point;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.LogUtils;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Level;

/**
 * Modifies player's points
 */
@SuppressWarnings("PMD.CommentRequired")
public class PointEvent extends QuestEvent {
    protected final VariableNumber count;
    protected final boolean multi;
    private final boolean notify;
    protected final String categoryName;
    protected final String category;

    public PointEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, false);
        persistent = true;
        categoryName = instruction.next();
        category = Utils.addPackage(instruction.getPackage(), categoryName);
        String number = instruction.next();
        if (!number.isEmpty() && number.charAt(0) == '*') {
            multi = true;
            number = number.replace("*", "");
        } else {
            multi = false;
        }
        try {
            count = new VariableNumber(instruction.getPackage().getName(), number);
        } catch (InstructionParseException e) {
            throw new InstructionParseException("Could not parse point count", e);
        }
        notify = instruction.hasArgument("notify");
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        if (PlayerConverter.getPlayer(playerID) == null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    final PlayerData playerData = new PlayerData(playerID);
                    try {
                        addPoints(playerID, playerData);
                    } catch (QuestRuntimeException e) {
                        LogUtils.getLogger().log(Level.WARNING, "Error while asynchronously adding " + count + " points of '" + category
                                + "' category to player " + PlayerConverter.getName(playerID) + ": " + e.getMessage());
                        LogUtils.logThrowable(e);
                    }
                }
            }.runTaskAsynchronously(BetonQuest.getInstance());
        } else {
            final PlayerData playerData = BetonQuest.getInstance().getPlayerData(playerID);
            addPoints(playerID, playerData);
        }
        return null;
    }

    @SuppressWarnings({"PMD.PreserveStackTrace", "PMD.CyclomaticComplexity"})
    private void addPoints(final String playerID, final PlayerData playerData) throws QuestRuntimeException {
        final int intCount = count.getInt(playerID);
        if (multi) {
            for (final Point p : playerData.getPoints()) {
                if (p.getCategory().equalsIgnoreCase(category)) {
                    playerData.modifyPoints(category, (int) Math.floor(p.getCount() * count.getDouble(playerID) - p.getCount()));
                    if (notify) {
                        try {
                            Config.sendNotify(instruction.getPackage().getName(), playerID, "point_multiplied", new String[]{String.valueOf(intCount), categoryName}, "point_multiplied,info");
                        } catch (final QuestRuntimeException exception) {
                            LogUtils.getLogger().log(Level.WARNING, "The notify system was unable to play a sound for the 'point_multiplied' category in '" + getFullId() + "'. Error was: '" + exception.getMessage() + "'");
                            LogUtils.logThrowable(exception);
                        }
                    }
                }
            }
        } else {
            playerData.modifyPoints(category, (int) Math.floor(count.getDouble(playerID)));
            if (notify && intCount > 0) {
                try {
                    Config.sendNotify(instruction.getPackage().getName(), playerID, "point_given", new String[]{String.valueOf(intCount), categoryName}, "point_given,info");
                } catch (final QuestRuntimeException exception) {
                    LogUtils.getLogger().log(Level.WARNING, "The notify system was unable to play a sound for the 'point_given' category in '" + getFullId() + "'. Error was: '" + exception.getMessage() + "'");
                    LogUtils.logThrowable(exception);
                }

            } else if (notify) {
                try {
                    Config.sendNotify(instruction.getPackage().getName(), playerID, "point_taken", new String[]{String.valueOf(Math.abs(intCount)), categoryName}, "point_taken,info");
                } catch (final QuestRuntimeException exception) {
                    LogUtils.getLogger().log(Level.WARNING, "The notify system was unable to play a sound for the 'point_taken' category in '" + getFullId() + "'. Error was: '" + exception.getMessage() + "'");
                    LogUtils.logThrowable(exception);
                }
            }
        }
    }
}
