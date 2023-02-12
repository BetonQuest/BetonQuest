package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.item.QuestItem.Existence;

import java.util.UUID;

@SuppressWarnings("PMD.CommentRequired")
public class HeadHandler {

    private String owner;
    private UUID playerId;
    private String texture;
    private Existence ownerE = Existence.WHATEVER;
    private Existence playerIdE = Existence.WHATEVER;
    private Existence textureE = Existence.WHATEVER;

    public HeadHandler() {
    }

    public void setOwner(final String string) {
        if ("none".equalsIgnoreCase(string)) {
            ownerE = Existence.FORBIDDEN;
        } else {
            owner = string;
            ownerE = Existence.REQUIRED;
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
}
