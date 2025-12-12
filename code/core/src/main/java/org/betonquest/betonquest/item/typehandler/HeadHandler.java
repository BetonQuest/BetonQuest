package org.betonquest.betonquest.item.typehandler;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Handles metadata about player Skulls.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class HeadHandler implements ItemMetaHandler<SkullMeta> {
    /**
     * Owner metadata about the Skull.
     */
    public static final String META_OWNER = "owner";

    /**
     * PlayerId metadata about the Skull.
     */
    public static final String META_PLAYER_ID = "player-id";

    /**
     * Encoded texture metadata about the Skull.
     */
    public static final String META_TEXTURE = "texture";

    /**
     * Existence of the player UUID.
     */
    private Existence playerIdE = Existence.WHATEVER;

    /**
     * Existence of the encoded texture.
     */
    private Existence textureE = Existence.WHATEVER;

    /**
     * An optional player name owner of the skull.
     */
    @Nullable
    private String owner;

    /**
     * An optional player ID owner of the skull, used in conjunction with the encoded texture.
     */
    @Nullable
    private UUID playerId;

    /**
     * An optional encoded texture URL of the skull, used in conjunction with the player UUID.
     */
    @Nullable
    private String texture;

    /**
     * Existence of the owner.
     */
    private Existence ownerE = Existence.WHATEVER;

    /**
     * Construct a new HeadHandler.
     */
    public HeadHandler() {
    }

    private static Map<String, String> parseSkullMeta(final SkullMeta skullMeta) {
        final Map<String, String> parsedValues = new HashMap<>();
        if (skullMeta.hasOwner()) {
            final OfflinePlayer owningPlayer = skullMeta.getOwningPlayer();
            if (owningPlayer != null) {
                parsedValues.put(META_OWNER, owningPlayer.getName());
            }
        }
        final PlayerProfile playerProfile = skullMeta.getPlayerProfile();
        if (playerProfile != null) {
            final UUID playerId = playerProfile.getId();
            if (playerId != null) {
                parsedValues.put(META_PLAYER_ID, playerId.toString());
            }
            final String texture = encodeSkin(playerProfile);
            if (texture != null) {
                parsedValues.put(META_TEXTURE, texture);
            }
        }
        return parsedValues;
    }

    @Nullable
    private static String encodeSkin(final PlayerProfile playerProfile) {
        return playerProfile.getProperties().stream()
                .filter(it -> "textures".equals(it.getName()))
                .map(ProfileProperty::getValue)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void populate(final SkullMeta skullMeta, @Nullable final Profile profile) {
        final Profile owner = getOwner(profile);
        final UUID playerId = getPlayerId();
        final String texture = getTexture();

        if (owner != null) {
            skullMeta.setOwningPlayer(owner.getPlayer());
        }
        if (playerId != null && texture != null) {
            final PlayerProfile playerProfile = Bukkit.getServer().createProfile(playerId);
            playerProfile.getProperties().add(new ProfileProperty("textures", texture));
            skullMeta.setPlayerProfile(playerProfile);
        }
    }

    @Override
    public boolean check(final SkullMeta skullMeta) {
        final OfflinePlayer owner = skullMeta.getOwningPlayer();
        final String ownerName = owner == null ? null : owner.getName();
        final PlayerProfile playerProfile = skullMeta.getPlayerProfile();
        if (playerProfile != null) {
            final UUID playerUniqueId = playerProfile.getId();
            final String texture = encodeSkin(playerProfile);
            return checkOwner(ownerName) && checkPlayerId(playerUniqueId) && checkTexture(texture);
        } else {
            return checkOwner(ownerName);
        }
    }

    @Override
    public Class<SkullMeta> metaClass() {
        return SkullMeta.class;
    }

    @Override
    public Set<String> keys() {
        return Set.of(META_OWNER, META_PLAYER_ID, META_TEXTURE);
    }

    @Override
    @Nullable
    public String serializeToString(final SkullMeta meta) {
        final String serialized = parseSkullMeta(meta).entrySet().stream()
                .map(it -> it.getKey() + ":" + it.getValue())
                .collect(Collectors.joining(" ", " ", ""));
        if (serialized.isBlank()) {
            return null;
        }
        return serialized.substring(1);
    }

    @Override
    public void set(final String key, final String data) throws QuestException {
        switch (key) {
            case META_OWNER -> {
                if (Existence.NONE_KEY.equalsIgnoreCase(data)) {
                    ownerE = Existence.FORBIDDEN;
                } else {
                    owner = data;
                    ownerE = Existence.REQUIRED;
                }
            }
            case META_PLAYER_ID -> {
                this.playerId = UUID.fromString(data);
                this.playerIdE = Existence.REQUIRED;
            }
            case META_TEXTURE -> {
                this.texture = data;
                this.textureE = Existence.REQUIRED;
            }
            default -> throw new QuestException("Unknown head key: " + key);
        }
    }

    @Contract("_ -> fail")
    @Override
    public void populate(final SkullMeta meta) {
        throw new UnsupportedOperationException("Use #populate(SkullMeta, Profile) instead");
    }

    /**
     * Get the profile of the skull's owner.
     * Also resolves the owner name to a player if it is a variable.
     *
     * @param profile The Profile that the item is made for
     * @return The profile of the skull's owner.
     */
    @Nullable
    public Profile getOwner(@Nullable final Profile profile) {
        if (profile != null && owner != null && owner.isEmpty()) {
            return profile;
        }
        if (owner != null) {
            final OfflinePlayer player = Bukkit.getOfflinePlayer(owner);
            return BetonQuest.getInstance().getProfileProvider().getProfile(player);
        }
        return null;
    }

    /**
     * Get the player UUID.
     *
     * @return The player ID.
     */
    @Nullable
    public UUID getPlayerId() {
        return playerId;
    }

    /**
     * Get the encoded texture.
     *
     * @return The encoded texture.
     */
    @Nullable
    public String getTexture() {
        return texture;
    }

    /**
     * Check to see if the specified owner name matches this HeadHandler metadata.
     *
     * @param string The owner to check.
     * @return True if this metadata is required and matches, false otherwise.
     */
    public boolean checkOwner(@Nullable final String string) {
        return switch (ownerE) {
            case WHATEVER -> true;
            case REQUIRED -> string != null && string.equals(owner);
            case FORBIDDEN -> string == null;
        };
    }

    /**
     * Check to see if the specified player UUID matches this HeadHandler metadata.
     *
     * @param playerId The player UUID to check.
     * @return True if this metadata is required and matches, false otherwise.
     */
    public boolean checkPlayerId(@Nullable final UUID playerId) {
        return switch (playerIdE) {
            case WHATEVER -> true;
            case REQUIRED -> playerId != null && playerId.equals(this.playerId);
            case FORBIDDEN -> playerId == null;
        };
    }

    /**
     * Check to see if the specified encoded texture matches this HeadHandler metadata.
     *
     * @param string The encoded texture to check.
     * @return True if this metadata is required and matches, false otherwise.
     */
    public boolean checkTexture(@Nullable final String string) {
        return switch (textureE) {
            case WHATEVER -> true;
            case REQUIRED -> string != null && string.equals(texture);
            case FORBIDDEN -> string == null;
        };
    }
}
