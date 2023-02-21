package org.betonquest.betonquest.item.typehandler;

import io.papermc.lib.PaperLib;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.item.QuestItem;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class HeadHandler {
    public static final String META_OWNER = "owner";
    public static final String META_PLAYER_ID = "player-id";
    public static final String META_TEXTURE = "texture";

    public static HeadHandler getInstance() {
        if (PaperLib.isPaper()) {
            return new PaperHeadHandler();
        } else {
            return new SpigotHeadHandler();
        }
    }

    public static String serializeSkullMeta(final SkullMeta skullMeta) {
        final Map<String, String> props;
        if (PaperLib.isPaper()) {
            props = PaperHeadHandler.parseSkullMeta(skullMeta);
        } else {
            props = SpigotHeadHandler.parseSkullMeta(skullMeta);
        }
        return props.entrySet().stream()
                .map(it -> it.getKey() + ":" + it.getValue())
                .collect(Collectors.joining(" ", " ", ""));
    }

    private String owner;
    private UUID playerId;
    private String texture;
    private QuestItem.Existence ownerE = QuestItem.Existence.WHATEVER;
    private QuestItem.Existence playerIdE = QuestItem.Existence.WHATEVER;
    private QuestItem.Existence textureE = QuestItem.Existence.WHATEVER;

    public void setOwner(final String string) {
        if ("none".equalsIgnoreCase(string)) {
            ownerE = QuestItem.Existence.FORBIDDEN;
        } else {
            owner = string;
            ownerE = QuestItem.Existence.REQUIRED;
        }
    }

    @SuppressWarnings("PMD.AvoidLiteralsInIfCondition")
    public String getOwner(final Profile profile) {
        if (profile != null && "%player%".equals(owner)) {
            return profile.getPlayer().getName();
        }
        return owner;
    }

    public void setPlayerId(final String playerId) {
        this.playerId = UUID.fromString(playerId);
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public void setTexture(final String texture) {
        this.texture = texture;
    }

    public String getTexture() {
        return texture;
    }

    public boolean checkOwner(final String string) {
        switch (ownerE) {
            case WHATEVER:
                return true;
            case REQUIRED:
                return string != null && string.equals(owner);
            case FORBIDDEN:
                return string == null;
            default:
                return false;
        }
    }

    public boolean checkPlayerId(final UUID id) {
        switch (playerIdE) {
            case WHATEVER:
                return true;
            case REQUIRED:
                return id != null && id.equals(playerId);
            case FORBIDDEN:
                return id == null;
            default:
                return false;
        }
    }

    public boolean checkTexture(final String string) {
        switch (textureE) {
            case WHATEVER:
                return true;
            case REQUIRED:
                return string != null && string.equals(texture);
            case FORBIDDEN:
                return string == null;
            default:
                return false;
        }
    }

    /**
     * Reconstitute this head data into the specified skullMeta object.
     * @param skullMeta The SkullMeta object to populate.
     * @param profile An optional Profile.
     */
    public abstract void populate(SkullMeta skullMeta, Profile profile);
    public abstract boolean check(SkullMeta skullMeta);
}
