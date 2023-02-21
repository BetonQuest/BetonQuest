package org.betonquest.betonquest.item.typehandler;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.betonquest.betonquest.api.profiles.Profile;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings({"PMD.CommentRequired", "deprecation"})
public class PaperHeadHandler extends HeadHandler {
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
        final PlayerProfile playerProfile = skullMeta.getPlayerProfile();
        if (playerProfile != null) {
            final UUID playerId = playerProfile.getUniqueId();
            if (playerId != null) {
                parsedValues.put(META_PLAYER_ID, playerId.toString());
            }
            final String texture = toEncodedSkinJson(playerProfile);
            if (texture != null) {
                parsedValues.put(META_TEXTURE, texture);
            }
        }
        return parsedValues;
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
            final String texture = toEncodedSkinJson(playerProfile);
            return checkOwner(ownerName) && checkPlayerId(playerUniqueId) && checkTexture(texture);
        } else {
            return checkOwner(ownerName);
        }
    }

    private static String toEncodedSkinJson(final PlayerProfile playerProfile) {
        return playerProfile.getProperties().stream()
                .filter(it -> it.getName().equals("textures"))
                .map(ProfileProperty::getValue)
                .findFirst()
                .orElse(null);
    }
}
