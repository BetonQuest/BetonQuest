package org.betonquest.betonquest.notify;

import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.betonquest.betonquest.utils.location.VectorData;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings("PMD.CommentRequired")
class NotifySound {
    private final static String KEY_SOUND = "sound";
    private final static String KEY_SOUND_LOCATION = "soundlocation";
    private final static String KEY_SOUND_PLAYER_OFFSET = "soundplayeroffset";
    private final static String KEY_SOUND_CATEGORY = "soundcategory";
    private final static String KEY_SOUND_VOLUME = "soundvolume";
    private final static String KEY_SOUND_PITCH = "soundpitch";
    private final static String[] SOUND_OPTIONS = {KEY_SOUND_LOCATION, KEY_SOUND_PLAYER_OFFSET, KEY_SOUND_CATEGORY, KEY_SOUND_VOLUME, KEY_SOUND_PITCH};

    private final SoundPlayer soundPlayer;

    protected NotifySound(final NotifyIO notify) throws InstructionParseException {
        final Map<String, String> data = notify.data;

        final SoundPlayer tempSoundPlayer = checkInput(data);
        if (tempSoundPlayer != null) {
            soundPlayer = tempSoundPlayer;
            return;
        }

        final CompoundLocation compoundLocation = getCompoundLocation(data);
        final SoundCategory soundCategory = getSoundCategory(data);
        final float volume = notify.getFloatData(KEY_SOUND_VOLUME, 1);
        final float pitch = notify.getFloatData(KEY_SOUND_PITCH, 1);

        final String playerOffsetString = data.get(KEY_SOUND_PLAYER_OFFSET);
        Float playerOffsetDistance = null;
        VectorData playerOffset = null;
        try {
            playerOffsetDistance = getPlayerOffsetDistance(playerOffsetString);
        } catch (final InstructionParseException e) {
            playerOffset = getPlayerOffset(playerOffsetString);
        }

        String soundString = data.get(KEY_SOUND);
        final Sound sound = getSound(soundString);
        if (sound == null) {
            soundString = soundString.toLowerCase(Locale.ROOT);
        }

        soundPlayer = getSoundPlayer(sound, soundString, compoundLocation, playerOffset, playerOffsetDistance, soundCategory, volume, pitch);
    }

    private SoundPlayer getSoundPlayer(final Sound sound, final String soundString, final CompoundLocation compoundLocation, final VectorData playerOffset, final Float playerOffsetDistance, final SoundCategory soundCategory, final float volume, final float pitch) {
        return (player) -> {
            final Location finalLocation = getLocation(player, compoundLocation, playerOffset, playerOffsetDistance);

            if (sound == null) {
                player.playSound(finalLocation, soundString, soundCategory, volume, pitch);
            } else {
                player.playSound(finalLocation, sound, soundCategory, volume, pitch);
            }
        };
    }

    private Location getLocation(final Player player, final CompoundLocation compoundLocation, final VectorData playerOffset, final Float playerOffsetDistance) throws QuestRuntimeException {
        final String playerID = PlayerConverter.getID(player);
        final Location location = compoundLocation == null ? player.getLocation() : compoundLocation.getLocation(playerID);

        if (playerOffsetDistance != null && player.getLocation().distance(location) > playerOffsetDistance) {
            return getLocationRelativeDistance(location, player, playerOffsetDistance);
        }
        if (playerOffset != null) {
            return getLocationRelativeVector(location, player, playerID, playerOffset);
        }

        return location;
    }

    private Location getLocationRelativeDistance(final Location location, final Player player, final Float playerOffsetDistance) {
        final Vector directionVector = location.toVector().subtract(player.getLocation().toVector());
        directionVector.normalize().multiply(playerOffsetDistance);
        return player.getLocation().add(directionVector);
    }

    private Location getLocationRelativeVector(final Location location, final Player player, final String playerID, final VectorData playerOffset) throws QuestRuntimeException {
        final Vector relative = playerOffset.get(playerID);
        final Location playerLoc = player.getLocation();

        relative.rotateAroundY(-Math.toRadians(playerLoc.getYaw()));
        final Vector vec = new Vector(0, 0, 1).rotateAroundY(-Math.toRadians(playerLoc.getYaw() + 90));
        relative.rotateAroundAxis(vec, -Math.toRadians(playerLoc.getPitch()));

        return location.add(relative);
    }

    private SoundPlayer checkInput(final Map<String, String> data) throws InstructionParseException {
        if (!data.containsKey(KEY_SOUND)) {
            if (Arrays.stream(SOUND_OPTIONS).anyMatch(data::containsKey)) {
                throw new InstructionParseException("You must specify a 'sound' if you want to use sound options!");
            }
            return (player) -> {
            };
        }
        return null;
    }

    private CompoundLocation getCompoundLocation(final Map<String, String> data) throws InstructionParseException {
        final String locationString = data.get(KEY_SOUND_LOCATION);
        return locationString == null ? null : new CompoundLocation(null, locationString);
    }

    private SoundCategory getSoundCategory(final Map<String, String> data) throws InstructionParseException {
        final String soundCategoryString = data.get(KEY_SOUND_CATEGORY);
        try {
            return soundCategoryString == null ? SoundCategory.MASTER : SoundCategory.valueOf(soundCategoryString.toUpperCase(Locale.ROOT));
        } catch (final IllegalArgumentException exception) {
            throw new InstructionParseException(String.format(NotifyIO.CATCH_MESSAGE_TYPE, KEY_SOUND_CATEGORY, soundCategoryString.toUpperCase(Locale.ROOT)), exception);
        }
    }

    private VectorData getPlayerOffset(final String playerOffsetString) throws InstructionParseException {
        if (playerOffsetString != null) {
            try {
                return new VectorData(null, playerOffsetString);
            } catch (final InstructionParseException exception) {
                throw new InstructionParseException(String.format("%s '%s' couldn't be parsed, it is not a valid vector or a floating point number!", KEY_SOUND_PLAYER_OFFSET, playerOffsetString), exception);
            }
        }
        return null;
    }

    private Float getPlayerOffsetDistance(final String playerOffsetString) throws InstructionParseException {
        if (playerOffsetString != null) {
            try {
                return Float.parseFloat(playerOffsetString);
            } catch (final NumberFormatException e) {
                throw new InstructionParseException(e);
            }
        }
        return null;
    }

    private Sound getSound(final String soundString) {
        try {
            return Sound.valueOf(soundString);
        } catch (final IllegalArgumentException exception) {
            return null;
        }
    }

    protected void sendSound(final Player player) throws QuestRuntimeException {
        soundPlayer.play(player);
    }

    @FunctionalInterface
    private interface SoundPlayer {
        void play(final Player player) throws QuestRuntimeException;
    }
}
