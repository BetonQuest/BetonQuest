package org.betonquest.betonquest.notify;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.common.function.QuestConsumer;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.QuestException;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.instruction.variable.location.VariableVector;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings("PMD.CommentRequired")
class NotifySound {
    private static final String KEY_SOUND = "sound";

    private static final String KEY_SOUND_LOCATION = "soundlocation";

    private static final String KEY_SOUND_PLAYER_OFFSET = "soundplayeroffset";

    private static final String KEY_SOUND_CATEGORY = "soundcategory";

    private static final String KEY_SOUND_VOLUME = "soundvolume";

    private static final String KEY_SOUND_PITCH = "soundpitch";

    private static final String[] SOUND_OPTIONS = {KEY_SOUND_LOCATION, KEY_SOUND_PLAYER_OFFSET, KEY_SOUND_CATEGORY, KEY_SOUND_VOLUME, KEY_SOUND_PITCH};

    private final QuestConsumer<OnlineProfile> soundPlayer;

    private final QuestPackage pack;

    protected NotifySound(final NotifyIO notify) throws QuestException {
        this.pack = notify.pack;
        final Map<String, String> data = notify.data;

        final QuestConsumer<OnlineProfile> tempSoundPlayer = checkInput(data);
        if (tempSoundPlayer != null) {
            soundPlayer = tempSoundPlayer;
            return;
        }

        final VariableLocation variableLocation = getVariableLocation(data);
        final SoundCategory soundCategory = getSoundCategory(data);
        final float volume = notify.getFloatData(KEY_SOUND_VOLUME, 1);
        final float pitch = notify.getFloatData(KEY_SOUND_PITCH, 1);

        final String playerOffsetString = data.get(KEY_SOUND_PLAYER_OFFSET);
        Float playerOffsetDistance = null;
        VariableVector playerOffset = null;
        try {
            playerOffsetDistance = getPlayerOffsetDistance(playerOffsetString);
        } catch (final QuestException e) {
            playerOffset = getPlayerOffset(playerOffsetString);
        }

        String soundString = data.get(KEY_SOUND);
        if (soundString == null) {
            throw new QuestException("Missing sound value!");
        }
        final Sound sound = getSound(soundString);
        if (sound == null) {
            soundString = soundString.toLowerCase(Locale.ROOT);
        }

        soundPlayer = getSoundPlayer(sound, soundString, variableLocation, playerOffset, playerOffsetDistance, soundCategory, volume, pitch);
    }

    private QuestConsumer<OnlineProfile> getSoundPlayer(@Nullable final Sound sound, final String soundString, @Nullable final VariableLocation variableLocation, @Nullable final VariableVector playerOffset, @Nullable final Float playerOffsetDistance, final SoundCategory soundCategory, final float volume, final float pitch) {
        return (onlineProfile) -> {
            final Location finalLocation = getLocation(onlineProfile, variableLocation, playerOffset, playerOffsetDistance);
            final Player player = onlineProfile.getPlayer();
            if (sound == null) {
                player.playSound(finalLocation, soundString, soundCategory, volume, pitch);
            } else {
                player.playSound(finalLocation, sound, soundCategory, volume, pitch);
            }
        };
    }

    private Location getLocation(final OnlineProfile onlineProfile, @Nullable final VariableLocation variableLocation, @Nullable final VariableVector playerOffset, @Nullable final Float playerOffsetDistance) throws QuestException {
        final Location location = variableLocation == null ? onlineProfile.getPlayer().getLocation() : variableLocation.getValue(onlineProfile);

        if (playerOffsetDistance != null && onlineProfile.getPlayer().getLocation().distance(location) > playerOffsetDistance) {
            return getLocationRelativeDistance(location, onlineProfile.getPlayer(), playerOffsetDistance);
        }
        if (playerOffset != null) {
            return getLocationRelativeVector(location, onlineProfile.getPlayer(), onlineProfile, playerOffset);
        }

        return location;
    }

    private Location getLocationRelativeDistance(final Location location, final Player player, final Float playerOffsetDistance) {
        final Vector directionVector = location.toVector().subtract(player.getLocation().toVector());
        directionVector.normalize().multiply(playerOffsetDistance);
        return player.getLocation().add(directionVector);
    }

    private Location getLocationRelativeVector(final Location location, final Player player, final Profile profile, final VariableVector playerOffset) throws QuestException {
        final Vector relative = playerOffset.getValue(profile);
        final Location playerLoc = player.getLocation();

        relative.rotateAroundY(-Math.toRadians(playerLoc.getYaw()));
        final Vector vec = new Vector(0, 0, 1).rotateAroundY(-Math.toRadians(playerLoc.getYaw() + 90));
        relative.rotateAroundAxis(vec, -Math.toRadians(playerLoc.getPitch()));

        return location.add(relative);
    }

    @Nullable
    private QuestConsumer<OnlineProfile> checkInput(final Map<String, String> data) throws QuestException {
        if (!data.containsKey(KEY_SOUND)) {
            if (Arrays.stream(SOUND_OPTIONS).anyMatch(data::containsKey)) {
                throw new QuestException("You must specify a 'sound' if you want to use sound options!");
            }
            return (player) -> {
            };
        }
        return null;
    }

    @Nullable
    private VariableLocation getVariableLocation(final Map<String, String> data) throws QuestException {
        final String locationString = data.get(KEY_SOUND_LOCATION);
        return locationString == null ? null : new VariableLocation(BetonQuest.getInstance().getVariableProcessor(), pack, locationString);
    }

    private SoundCategory getSoundCategory(final Map<String, String> data) throws QuestException {
        final String soundCategoryString = data.get(KEY_SOUND_CATEGORY);
        try {
            return soundCategoryString == null ? SoundCategory.MASTER : SoundCategory.valueOf(soundCategoryString.toUpperCase(Locale.ROOT));
        } catch (final IllegalArgumentException exception) {
            throw new QuestException(String.format(NotifyIO.CATCH_MESSAGE_TYPE, KEY_SOUND_CATEGORY, soundCategoryString.toUpperCase(Locale.ROOT)), exception);
        }
    }

    @Nullable
    private VariableVector getPlayerOffset(@Nullable final String playerOffsetString) throws QuestException {
        if (playerOffsetString != null) {
            try {
                return new VariableVector(BetonQuest.getInstance().getVariableProcessor(), pack, playerOffsetString);
            } catch (final QuestException e) {
                throw new QuestException(String.format("%s '%s' couldn't be parsed: " + e.getMessage(), KEY_SOUND_PLAYER_OFFSET, playerOffsetString), e);
            }
        }
        return null;
    }

    @Nullable
    private Float getPlayerOffsetDistance(@Nullable final String playerOffsetString) throws QuestException {
        if (playerOffsetString != null) {
            try {
                return Float.parseFloat(playerOffsetString);
            } catch (final NumberFormatException e) {
                throw new QuestException(e);
            }
        }
        return null;
    }

    @Nullable
    private Sound getSound(final String soundString) {
        try {
            return Sound.valueOf(soundString);
        } catch (final IllegalArgumentException exception) {
            return null;
        }
    }

    protected void sendSound(final OnlineProfile onlineProfile) throws QuestException {
        soundPlayer.accept(onlineProfile);
    }
}
