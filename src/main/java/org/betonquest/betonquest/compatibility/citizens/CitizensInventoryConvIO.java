package org.betonquest.betonquest.compatibility.citizens;

import net.citizensnpcs.trait.SkinTrait;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.InventoryConvIO;
import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A chest conversationIO that replaces the NPCs skull with the skin of the Citizen NPC.
 */
public class CitizensInventoryConvIO extends InventoryConvIO {

    /**
     * A regex pattern to match the skin URL in the base64 encoded skin texture.
     * See <a href="https://wiki.vg/Mojang_API#UUID_to_Profile_and_Skin.2FCape">wiki.vg</a> for unofficial documentation.
     */
    private static final Pattern SKIN_JSON_URL_PATTERN = Pattern.compile("\"SKIN\" ?: ?\\{\\n *\"url\" ?: ?\"(?<url>.*)\"");

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Creates a new {@link CitizensInventoryConvIO} instance.
     *
     * @param conv          the conversation
     * @param onlineProfile the online profile
     */
    public CitizensInventoryConvIO(final Conversation conv, final OnlineProfile onlineProfile) {
        super(conv, onlineProfile);
        this.log = BetonQuest.getInstance().getLoggerFactory().create(getClass());
    }

    @Override
    protected SkullMeta updateSkullMeta(final SkullMeta meta) {
        // this only applied to Citizens NPC conversations
        if (conv instanceof final CitizensConversation citizensConv) {
            if (Bukkit.isPrimaryThread()) {
                throw new IllegalStateException("Must be called async!");
            }

            try {
                final SkinTrait skinTrait = Bukkit.getScheduler().callSyncMethod(BetonQuest.getInstance(), () -> citizensConv.getNPC().getOrAddTrait(SkinTrait.class)).get();
                final String texture = skinTrait.getTexture();
                if (texture != null) {

                    final PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), "");
                    final PlayerTextures skullTexture = profile.getTextures();
                    skullTexture.setSkin(resolveSkinURL(texture), PlayerTextures.SkinModel.CLASSIC);
                    profile.setTextures(skullTexture);
                    meta.setOwnerProfile(profile);
                    return meta;
                }
            } catch (InterruptedException | ExecutionException e) {
                log.debug(citizensConv.getPackage(), "Could not resolve a skin Texture!", e);
            } catch (final SkinFormatParseException e) {
                log.reportException(conv.getPackage(), new IllegalStateException("Could not parse the skin metadata provided by the NPC plugin. The format may have changed."));
            }
        }
        return super.updateSkullMeta(meta);
    }

    /**
     * Resolves a base64 encoded skin JSON to a skin URL.
     * See <a href="https://wiki.vg/Mojang_API#UUID_to_Profile_and_Skin.2FCape">wiki.vg</a> for unofficial documentation
     * on the skin JSON format.
     *
     * @param base64Texture base64 encoded skin JSON metadata
     * @return the skin URL
     */
    private URL resolveSkinURL(final String base64Texture) throws SkinFormatParseException {
        final String decoded = new String(Base64.getDecoder().decode(base64Texture), StandardCharsets.UTF_8);
        final Matcher matcher = SKIN_JSON_URL_PATTERN.matcher(decoded);
        if (!matcher.find()) {
            throw new SkinFormatParseException("Could not find the skin URL in the skin JSON!");
        }
        final String variable = matcher.group("url");
        try {
            return new URL(variable);
        } catch (final MalformedURLException e) {
            throw new SkinFormatParseException("Could not parse the skin URL!", e);
        }
    }

    /**
     * A CitizensInventoryConvIO that also prints the messages in the chat.
     */
    public static class CitizensCombined extends CitizensInventoryConvIO {

        /**
         * Creates a new {@link CitizensCombined} conversationIO instance.
         *
         * @param conv          the conversation
         * @param onlineProfile the online profile
         */
        public CitizensCombined(final Conversation conv, final OnlineProfile onlineProfile) {
            super(conv, onlineProfile);
            super.printMessages = true;
        }
    }

}
