package pl.betoncraft.betonquest.notify;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.utils.LocationData;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.betonquest.utils.Utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public abstract class NotifyIO {
    protected final static String CATCH_MESSAGE_FLOAT = "%s '%s' couldn't be parsed, it is not a valid floating point number!";
    protected final static String CATCH_MESSAGE_INTEGER = "%s '%s' couldn't be parsed, it is not a valid number!";
    protected final static String CATCH_MESSAGE_TYPE = "%s with the name '%s' does not exists!";

    protected final Map<String, String> data;
    private final IOSound sound;

    protected NotifyIO() throws InstructionParseException {
        this(new HashMap<>());
    }

    protected NotifyIO(final Map<String, String> data) throws InstructionParseException {
        this.data = data;
        sound = new IOSound();
    }

    public void sendNotify(final String packName, final String message) {
        for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
            sendNotify(packName, message, player);
        }
    }

    public void sendNotify(final String packName, @NotNull final String message, @NotNull final Player player) {
        sound.sendSound(player);
        if (packName == null) {
            sendNotify(Utils.format(message), player);
        } else {
            String resolvedMessage = message;
            for (final String variable : BetonQuest.resolveVariables(message)) {
                final String replacement = BetonQuest.getInstance().getVariableValue(packName, variable, PlayerConverter.getID(player));
                resolvedMessage = resolvedMessage.replace(variable, replacement);
            }
            sendNotify(Utils.format(resolvedMessage), player);
        }
    }

    protected abstract void sendNotify(final String message, final Player player);

    protected float getFloatData(final String dataKey, final float defaultData) throws InstructionParseException {
        final String dataString = data.get(dataKey);
        try {
            return dataString == null ? defaultData : Float.parseFloat(dataString);
        } catch (final NumberFormatException exception) {
            throw new InstructionParseException(String.format(CATCH_MESSAGE_FLOAT, dataKey, dataString), exception);
        }
    }

    protected int getIntegerData(final String dataKey, final int defaultData) throws InstructionParseException {
        final String dataString = data.get(dataKey);
        try {
            return dataString == null ? defaultData : Integer.parseInt(dataString);
        } catch (final NumberFormatException exception) {
            throw new InstructionParseException(String.format(CATCH_MESSAGE_INTEGER, dataKey, dataString), exception);
        }
    }

    private class IOSound {
        private final String sound;
        private final Location location;
        private final Vector locationOffset;
        private final SoundCategory soundCategory;
        private final float volume;
        private final float pitch;

        public IOSound() throws InstructionParseException {
            sound = data.get("sound");

            final String locationString = data.get("soundlocation");
            location = locationString == null ? null : LocationData.parseLocation(locationString);

            final String locationOffsetString = data.get("soundlocationoffset");
            locationOffset = locationOffsetString == null ? new Vector() : LocationData.parseVector(locationOffsetString);

            final String soundCategoryString = data.get("soundcategory");
            try {
                soundCategory = soundCategoryString == null ? SoundCategory.MASTER : SoundCategory.valueOf(soundCategoryString.toUpperCase(Locale.ROOT));
            } catch (final IllegalArgumentException exception) {
                throw new InstructionParseException(String.format(CATCH_MESSAGE_TYPE, "soundcategory", soundCategoryString.toUpperCase(Locale.ROOT)), exception);
            }

            final String volumeString = data.get("soundvolume");
            volume = getFloatData("soundvolume", 1);
            final String pitchString = data.get("soundpitch");
            pitch = getFloatData("soundpitch", 1);

            if (sound == null && (locationString != null || locationOffsetString != null || soundCategoryString != null || volumeString != null || pitchString != null)) {
                throw new InstructionParseException("You must specify a 'sound' if you want to use sound options!");
            }
        }

        protected void sendSound(final Player player) {
            if (sound != null) {
                final Location loc = location == null ? player.getLocation() : location.clone();
                loc.add(locationOffset);
                player.playSound(loc, sound, soundCategory, volume, pitch);
            }
        }
    }
}
