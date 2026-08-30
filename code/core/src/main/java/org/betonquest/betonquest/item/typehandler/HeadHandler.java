package org.betonquest.betonquest.item.typehandler;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.item.handler.Attribute;
import org.betonquest.betonquest.item.handler.Existence;
import org.betonquest.betonquest.item.handler.ExistenceArgument;
import org.betonquest.betonquest.item.handler.ItemMetaHandler;
import org.betonquest.betonquest.item.handler.ResolvedAttribute;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Handles metadata about player Skulls.
 */
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
     * The empty default Constructor.
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
    @Nullable
    public Attribute parse(final Instruction instruction) throws QuestException {
        final ExistenceArgument<String> owner = ExistenceArgument.applyOrNull(META_OWNER, instruction.string());
        final ExistenceArgument<UUID> playerId = ExistenceArgument.applyOrNull(META_PLAYER_ID, instruction.uuid());
        final ExistenceArgument<String> texture = ExistenceArgument.applyOrNull(META_TEXTURE, instruction.string());
        if (owner == null && playerId == null && texture == null) {
            return null;
        }
        return new NonResolved(ExistenceArgument.fallback(owner), ExistenceArgument.fallback(playerId),
                ExistenceArgument.fallback(texture));
    }

    /**
     * The attribute with placeholders.
     *
     * @param owner    An optional player name owner of the skull.
     * @param playerId An optional player ID owner of the skull, used in conjunction with the encoded texture.
     * @param texture  An optional encoded texture URL of the skull, used in conjunction with the player UUID.
     */
    private record NonResolved(ExistenceArgument<String> owner, ExistenceArgument<UUID> playerId,
                               ExistenceArgument<String> texture)
            implements Attribute {

        /**
         * Get the profile of the skull's owner.
         * Also resolves the owner name to a player if it is a placeholder.
         *
         * @param profile The Profile that the item is made for
         * @param owner   The owner string to use
         * @return The profile of the skull's owner.
         */
        @Nullable
        private Profile getOwner(@Nullable final Profile profile, @Nullable final String owner) {
            if (profile != null && owner != null && owner.isEmpty()) {
                return profile;
            }
            if (owner != null) {
                final OfflinePlayer player = Bukkit.getOfflinePlayer(owner);
                return BetonQuest.getInstance().getBetonQuestApi().profiles().getProfile(player);
            }
            return null;
        }

        @Override
        public ResolvedAttribute<SkullMeta> resolve(@Nullable final Profile profile) throws QuestException {
            final Pair<Existence, String> ownerPair = this.owner.getValue(profile);
            final Profile owner = getOwner(profile, ownerPair.getRight());
            final Pair<Existence, UUID> playerIdPair = this.playerId.getValue(profile);
            final Pair<Existence, String> texturePair = this.texture.getValue(profile);
            return new Resolved(ownerPair, owner, playerIdPair, texturePair);
        }
    }

    /**
     * The resolved attribute.
     *
     * @param ownerPair    An optional player name owner of the skull.
     * @param owner        The resolved owner of the skull.
     * @param playerIdPair An optional player ID owner of the skull, used in conjunction with the encoded texture.
     * @param texturePair  An optional encoded texture URL of the skull, used in conjunction with the player UUID.
     */
    private record Resolved(Pair<Existence, String> ownerPair, @Nullable Profile owner,
                            Pair<Existence, UUID> playerIdPair, Pair<Existence, String> texturePair)
            implements ResolvedAttribute<SkullMeta> {

        @Override
        public Class<SkullMeta> metaClass() {
            return SkullMeta.class;
        }

        @Override
        public void populate(final SkullMeta skullMeta) {
            if (owner != null) {
                skullMeta.setOwningPlayer(owner.getPlayer());
            }
            final UUID playerId = playerIdPair.getRight();
            final String texture = texturePair.getRight();
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
                return check(ownerPair, ownerName) && checkPlayerId(playerIdPair, playerUniqueId) && check(texturePair, texture);
            }
            return check(ownerPair, ownerName);
        }

        private boolean check(final Pair<Existence, String> pair, @Nullable final String string) {
            return switch (pair.getLeft()) {
                case WHATEVER -> true;
                case REQUIRED -> string != null && string.equals(pair.getRight());
                case FORBIDDEN -> string == null;
            };
        }

        private boolean checkPlayerId(final Pair<Existence, UUID> pair, @Nullable final UUID playerId) {
            return switch (pair.getLeft()) {
                case WHATEVER -> true;
                case REQUIRED -> playerId != null && playerId.equals(pair.getRight());
                case FORBIDDEN -> playerId == null;
            };
        }
    }
}
