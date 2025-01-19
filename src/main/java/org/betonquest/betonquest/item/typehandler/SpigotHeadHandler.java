package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handles metadata about player Skulls for Spigot/Bukkit server.
 * This class relies on classes and methods that Paper has defined as deprecated, even though they are not deprecated
 * in Bukkit / Spigot. Thus, deprecation warnings are suppressed for this class.
 */
@SuppressWarnings("deprecation")
public class SpigotHeadHandler extends HeadHandler {
    /**
     * Prefix for JSON structure minecraft uses for defining skin texture URL.
     */
    private static final String TEXTURE_PREFIX = "{\"textures\":{\"SKIN\":{\"url\":\"";

    /**
     * Suffix for JSON structure minecraft uses for defining skin texture URL.
     */
    private static final String TEXTURE_SUFFIX = "\"}}}";

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Construct a new HeadHandler.
     *
     * @param log the logger that will be used for logging
     */
    public SpigotHeadHandler(final BetonQuestLogger log) {
        super();
        this.log = log;
    }

    /**
     * Parse the metadata of a SkullMeta instance that needs to be persisted so that it can be correctly reconstituted.
     *
     * @param skullMeta The SkullMeta to parse.
     * @return A Map of the properties parsed from the SkullMeta.
     */
    public static Map<String, String> parseSkullMeta(final SkullMeta skullMeta) {
        final Map<String, String> parsedValues = new HashMap<>();
        if (skullMeta.hasOwner()) {
            final OfflinePlayer owningPlayer = skullMeta.getOwningPlayer();
            if (owningPlayer != null) {
                parsedValues.put(META_OWNER, owningPlayer.getName());
            }
        }
        return parsedValues;
    }

    private String encodeSkin(final URL skinUrl) {
        return Base64.getEncoder()
                .encodeToString((TEXTURE_PREFIX + skinUrl + TEXTURE_SUFFIX)
                        .getBytes(Charset.defaultCharset()));
    }

    /**
     * Decode the skin texture URL from the JSON structure minecraft uses for defining skin texture URL.
     *
     * @param texture The JSON structure minecraft uses for defining skin texture URL.
     * @return The skin texture URL.
     * @throws MalformedURLException    If the texture URL is malformed.
     * @throws IllegalArgumentException If src is not in valid Base64 scheme
     */
    private URL decodeSkin(final String texture) throws MalformedURLException {
        final String json = new String(Base64.getDecoder().decode(texture), Charset.defaultCharset());
        return new URL(json.substring(TEXTURE_PREFIX.length(), json.length() - TEXTURE_SUFFIX.length()));
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
            try {
                final URL url = decodeSkin(texture);

                final PlayerProfile playerProfile = Bukkit.getServer().createPlayerProfile(playerId);
                playerProfile.getTextures().setSkin(url);
                skullMeta.setOwnerProfile(playerProfile);
            } catch (final MalformedURLException | IllegalArgumentException e) {
                log.warn("The quest item that was just given to '" + profile + "' has an invalid head texture.", e);
            }
        }
    }

    @Override
    public boolean check(final SkullMeta skullMeta) {
        final OfflinePlayer owningPlayer = skullMeta.getOwningPlayer();
        final String ownerName;
        if (owningPlayer != null) {
            ownerName = owningPlayer.getName();
        } else {
            ownerName = null;
        }
        final PlayerProfile playerProfile = skullMeta.getOwnerProfile();
        if (playerProfile != null) {
            final UUID playerUniqueId = playerProfile.getUniqueId();
            final URL skin = playerProfile.getTextures().getSkin();
            if (skin != null) {
                final String texture = encodeSkin(skin);
                return checkOwner(ownerName) && checkPlayerId(playerUniqueId) && checkTexture(texture);
            } else {
                return checkOwner(ownerName);
            }
        } else {
            return checkOwner(ownerName);
        }
    }
}
