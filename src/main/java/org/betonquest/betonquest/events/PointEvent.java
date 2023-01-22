package org.betonquest.betonquest.events;

import lombok.CustomLog;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.Point;
import org.betonquest.betonquest.VariableNumber;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.database.PlayerData;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Modifies profile's points
 */
@SuppressWarnings("PMD.CommentRequired")
@CustomLog
public class PointEvent extends QuestEvent {
    protected final VariableNumber count;
    protected final boolean multi;
    protected final String categoryName;
    protected final String category;
    private final boolean notify;

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
            count = new VariableNumber(instruction.getPackage(), number);
        } catch (final InstructionParseException e) {
            throw new InstructionParseException("Could not parse point count", e);
        }
        notify = instruction.hasArgument("notify");
    }

    @Override
    protected Void execute(final Profile profile) throws QuestRuntimeException {
        if (profile.getOnlineProfile().isPresent()) {
            final PlayerData playerData = BetonQuest.getInstance().getPlayerData(profile);
            addPoints(profile.getOnlineProfile().get(), playerData);
        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    final PlayerData playerData = new PlayerData(profile);
                    try {
                        addPoints(profile, playerData);
                    } catch (final QuestRuntimeException e) {
                        LOG.warn(instruction.getPackage(), "Error while asynchronously adding " + count + " points of '" + category
                                + "' category to " + profile + ": " + e.getMessage(), e);
                    }
                }
            }.runTaskAsynchronously(BetonQuest.getInstance());
        }
        return null;
    }

    @SuppressWarnings({"PMD.PreserveStackTrace", "PMD.CyclomaticComplexity", "PMD.CognitiveComplexity"})
    private void addPoints(final Profile profile, final PlayerData playerData) throws QuestRuntimeException {
        final int intCount = count.getInt(profile);
        if (multi) {
            for (final Point p : playerData.getPoints()) {
                if (p.getCategory().equalsIgnoreCase(category)) {
                    playerData.modifyPoints(category, (int) Math.floor(p.getCount() * count.getDouble(profile) - p.getCount()));
                    if (notify && profile.getOnlineProfile().isPresent()) {
                        try {
                            Config.sendNotify(instruction.getPackage().getQuestPath(), profile.getOnlineProfile().get(), "point_multiplied", new String[]{String.valueOf(intCount), categoryName}, "point_multiplied,info");
                        } catch (final QuestRuntimeException e) {
                            LOG.warn(instruction.getPackage(), "The notify system was unable to play a sound for the 'point_multiplied' category in '" + getFullId() + "'. Error was: '" + e.getMessage() + "'", e);
                        }
                    }
                }
            }
        } else {
            playerData.modifyPoints(category, (int) Math.floor(count.getDouble(profile)));
            if (notify && intCount > 0 && profile.getOnlineProfile().isPresent()) {
                try {
                    Config.sendNotify(instruction.getPackage().getQuestPath(), profile.getOnlineProfile().get(), "point_given", new String[]{String.valueOf(intCount), categoryName}, "point_given,info");
                } catch (final QuestRuntimeException e) {
                    LOG.warn(instruction.getPackage(), "The notify system was unable to play a sound for the 'point_given' category in '" + getFullId() + "'. Error was: '" + e.getMessage() + "'", e);
                }

            } else if (notify && profile.getOnlineProfile().isPresent()) {
                try {
                    Config.sendNotify(instruction.getPackage().getQuestPath(), profile.getOnlineProfile().get(), "point_taken", new String[]{String.valueOf(Math.abs(intCount)), categoryName}, "point_taken,info");
                } catch (final QuestRuntimeException e) {
                    LOG.warn(instruction.getPackage(), "The notify system was unable to play a sound for the 'point_taken' category in '" + getFullId() + "'. Error was: '" + e.getMessage() + "'", e);
                }
            }
        }
    }
}
