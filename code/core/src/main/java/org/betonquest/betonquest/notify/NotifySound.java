package org.betonquest.betonquest.notify;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.function.QuestConsumer;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.ValueParser;
import org.betonquest.betonquest.api.instruction.argument.parser.LocationParser;
import org.betonquest.betonquest.api.instruction.argument.parser.VectorParser;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.Placeholders;
import org.betonquest.betonquest.lib.instruction.argument.DefaultArgument;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Plays a sound to a player.
 */
@SuppressWarnings("PMD.CouplingBetweenObjects")
class NotifySound {

    /**
     * Key of sound to play.
     */
    private static final String KEY_SOUND = "sound";

    /**
     * Location to play the sound at.
     */
    private static final String KEY_SOUND_LOCATION = "soundlocation";

    /**
     * Offset vector to apply to the source location.
     */
    private static final String KEY_SOUND_PLAYER_OFFSET = "soundplayeroffset";

    /**
     * Category to play at.
     */
    private static final String KEY_SOUND_CATEGORY = "soundcategory";

    /**
     * Volume.
     */
    private static final String KEY_SOUND_VOLUME = "soundvolume";

    /**
     * Pitch.
     */
    private static final String KEY_SOUND_PITCH = "soundpitch";

    /**
     * All sound options keys.
     */
    private static final String[] SOUND_OPTIONS = {KEY_SOUND_LOCATION, KEY_SOUND_PLAYER_OFFSET, KEY_SOUND_CATEGORY, KEY_SOUND_VOLUME, KEY_SOUND_PITCH};

    /**
     * The {@link Placeholders} to create and resolve placeholders.
     */
    private final Placeholders placeholders;

    /**
     * Source of player to play the sound for.
     */
    private final QuestConsumer<OnlineProfile> soundPlayer;

    /**
     * Source pack to use for argument creation.
     */
    @Nullable
    private final QuestPackage pack;

    /**
     * Creates a new Notify sound from existent NotifyIO.
     *
     * @param notify the notify IO supplying the data
     * @throws QuestException when the data could not be parsed
     */
    protected NotifySound(final NotifyIO notify) throws QuestException {
        this.placeholders = notify.placeholders;
        this.pack = notify.pack;
        final Map<String, String> data = notify.data;

        final QuestConsumer<OnlineProfile> tempSoundPlayer = checkInput(data);
        if (tempSoundPlayer != null) {
            soundPlayer = tempSoundPlayer;
            return;
        }

        final Argument<Location> location = getLocationArgument(data);
        final Argument<SoundCategory> soundCategory = getSoundCategory(data);
        final Argument<Number> volume = notify.getNumberData(KEY_SOUND_VOLUME, 1);
        final Argument<Number> pitch = notify.getNumberData(KEY_SOUND_PITCH, 1);

        final String playerOffsetString = data.get(KEY_SOUND_PLAYER_OFFSET);
        final Argument<Optional<Float>> playerOffsetDistance;
        final Argument<Optional<Vector>> playerOffset;
        if (playerOffsetString == null) {
            playerOffsetDistance = p -> Optional.empty();
            playerOffset = p -> Optional.empty();
        } else {
            playerOffsetDistance = getArgument(playerOffsetString, this::getPlayerOffsetDistance);
            playerOffset = getArgument(playerOffsetString, this::getPlayerOffset);
        }

        final String rawSoundString = data.get(KEY_SOUND);
        if (rawSoundString == null) {
            throw new QuestException("Missing sound value!");
        }
        final Argument<Optional<Sound>> sound = getArgument(rawSoundString, this::getSound);
        final Argument<String> soundString = getArgument(rawSoundString, resolved -> resolved.toLowerCase(Locale.ROOT));

        soundPlayer = getSoundPlayer(sound, soundString, location, playerOffset, playerOffsetDistance, soundCategory, volume, pitch);
    }

    private <T> Argument<T> getArgument(final String raw, final ValueParser<T> parser) throws QuestException {
        return new DefaultArgument<>(placeholders, pack, raw, parser);
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private QuestConsumer<OnlineProfile> getSoundPlayer(
            final Argument<Optional<Sound>> sound, final Argument<String> soundString, @Nullable final Argument<Location> location,
            final Argument<Optional<Vector>> playerOffset, final Argument<Optional<Float>> playerOffsetDistance,
            final Argument<SoundCategory> soundCategory, final Argument<Number> volume, final Argument<Number> pitch) {
        return (onlineProfile) -> {
            final Location finalLocation = getLocation(onlineProfile, location, playerOffset, playerOffsetDistance);
            final float resolvedVolume = volume.getValue(onlineProfile).floatValue();
            final float resolvedPitch = pitch.getValue(onlineProfile).floatValue();
            final Player player = onlineProfile.getPlayer();
            final Optional<Sound> resolvedSound = sound.getValue(onlineProfile);
            final SoundCategory resolvedCategory = soundCategory.getValue(onlineProfile);
            if (resolvedSound.isEmpty()) {
                try {
                    player.playSound(finalLocation, soundString.getValue(onlineProfile), resolvedCategory, resolvedVolume, resolvedPitch);
                } catch (final RuntimeException exception) {
                    throw new QuestException(exception.getMessage(), exception);
                }
            } else {
                player.playSound(finalLocation, resolvedSound.get(), resolvedCategory, resolvedVolume, resolvedPitch);
            }
        };
    }

    private Location getLocation(final OnlineProfile onlineProfile, @Nullable final Argument<Location> location,
                                 final Argument<Optional<Vector>> playerOffset,
                                 final Argument<Optional<Float>> playerOffsetDistance) throws QuestException {
        final Location resolvedLocation = location == null ? onlineProfile.getPlayer().getLocation() : location.getValue(onlineProfile);

        final Optional<Float> offsetDistance = playerOffsetDistance.getValue(onlineProfile);
        if (offsetDistance.isPresent() && onlineProfile.getPlayer().getLocation().distance(resolvedLocation) > offsetDistance.get()) {
            return getLocationRelativeDistance(resolvedLocation, onlineProfile.getPlayer(), offsetDistance.get());
        }
        final Optional<Vector> offset = playerOffset.getValue(onlineProfile);
        if (offset.isPresent()) {
            return getLocationRelativeVector(resolvedLocation, onlineProfile.getPlayer(), offset.get());
        }

        return resolvedLocation;
    }

    private Location getLocationRelativeDistance(final Location location, final Player player, final Float playerOffsetDistance) {
        final Vector directionVector = location.toVector().subtract(player.getLocation().toVector());
        directionVector.normalize().multiply(playerOffsetDistance);
        return player.getLocation().add(directionVector);
    }

    private Location getLocationRelativeVector(final Location location, final Player player, final Vector relative) {
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
    private Argument<Location> getLocationArgument(final Map<String, String> data) throws QuestException {
        final String locationString = data.get(KEY_SOUND_LOCATION);
        return locationString == null ? null : getArgument(locationString, new LocationParser(Bukkit.getServer()));
    }

    private Argument<SoundCategory> getSoundCategory(final Map<String, String> data) throws QuestException {
        final String soundCategoryString = data.get(KEY_SOUND_CATEGORY);
        if (soundCategoryString == null) {
            return p -> SoundCategory.MASTER;
        }
        return getArgument(soundCategoryString, resolved -> {
            try {
                return SoundCategory.valueOf(soundCategoryString.toUpperCase(Locale.ROOT));
            } catch (final IllegalArgumentException exception) {
                throw new QuestException(String.format(NotifyIO.CATCH_MESSAGE_TYPE, KEY_SOUND_CATEGORY, soundCategoryString.toUpperCase(Locale.ROOT)), exception);
            }
        });
    }

    private Optional<Vector> getPlayerOffset(final String playerOffsetString) throws QuestException {
        try {
            return Optional.of(new VectorParser().apply(playerOffsetString));
        } catch (final QuestException e) {
            throw new QuestException(String.format("%s '%s' couldn't be parsed: " + e.getMessage(), KEY_SOUND_PLAYER_OFFSET, playerOffsetString), e);
        }
    }

    private Optional<Float> getPlayerOffsetDistance(final String playerOffsetString) {
        try {
            return Optional.of(Float.parseFloat(playerOffsetString));
        } catch (final NumberFormatException e) {
            return Optional.empty();
        }
    }

    @SuppressFBWarnings("DCN_NULLPOINTER_EXCEPTION")
    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    private Optional<Sound> getSound(final String soundString) {
        try {
            return Optional.of(Sound.valueOf(soundString));
        } catch (final IllegalArgumentException | NullPointerException exception) {
            return Optional.empty();
        }
    }

    /**
     * Sends the sound to the profile.
     *
     * @param onlineProfile the profile to send the sound to
     * @throws QuestException when placeholders could not be resolved
     */
    protected void sendSound(final OnlineProfile onlineProfile) throws QuestException {
        soundPlayer.accept(onlineProfile);
    }
}
