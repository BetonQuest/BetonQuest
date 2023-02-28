package org.betonquest.betonquest.item.typehandler;

import lombok.CustomLog;
import org.betonquest.betonquest.api.profiles.Profile;
import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handles metadata about player Skulls for Spigot/Bukkit server.
 */
@CustomLog
public class SpigotHeadHandler extends HeadHandler {
    /**
     * Parse the metadata of a SkullMeta instance that needs to be persisted so that it can be correctly reconstituted.
     * @param skullMeta The SkullMeta to parse.
     * @return A Map of the properties parsed from the SkullMeta.
     */
    public static Map<String, String> parseSkullMeta(final SkullMeta skullMeta) {
        final Map<String, String> parsedValues = new HashMap<>();
        if (skullMeta.hasOwner()) {
            parsedValues.put(META_OWNER, skullMeta.getOwner());
        }
        return parsedValues;
    }

    /**
     * Construct a new HeadHandler.
     */
    public SpigotHeadHandler() {
        super();
    }

    @Override
    public void populate(final SkullMeta skullMeta, final Profile profile) {
        final String owner = getOwner(profile);
        final UUID playerId = getPlayerId();
        final String texture = getTexture();

        if (owner != null) {
            skullMeta.setOwner(owner);
        }
        if (playerId != null && texture != null) {
            try {
                final URL url = new URL(new String(Base64.getDecoder().decode(texture), Charset.defaultCharset()));

                final PlayerProfile playerProfile = Bukkit.getServer().createPlayerProfile(playerId);
                playerProfile.getTextures().setSkin(url);
                skullMeta.setOwnerProfile(playerProfile);
            } catch (final MalformedURLException e) {
                LOG.error("Error determining texture URL", e);
            }
        }
    }

    @Override
    public boolean check(final SkullMeta skullMeta) {
        final String ownerName = skullMeta.getOwner();
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

    private static String encodeSkin(final URL skinUrl) {
        return Base64.getEncoder()
                .encodeToString(skinUrl.toString().getBytes(Charset.defaultCharset()));
    }
}
