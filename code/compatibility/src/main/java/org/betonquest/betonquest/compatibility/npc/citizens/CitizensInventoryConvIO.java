package org.betonquest.betonquest.compatibility.npc.citizens;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinTrait;
import org.betonquest.betonquest.api.common.component.FixedComponentLineWrapper;
import org.betonquest.betonquest.api.config.Translations;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.npc.NpcConversation;
import org.betonquest.betonquest.api.service.conversation.Conversations;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.api.service.item.ItemManager;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.conversation.InventoryConvIO;
import org.bukkit.Bukkit;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
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
     * Plugin instance used for scheduling.
     */
    private final Plugin plugin;

    /**
     * Creates a new CitizensInventoryConvIO instance.
     *
     * @param conv                 the conversation this IO is part of
     * @param onlineProfile        the online profile of the player participating in the conversation
     * @param log                  the custom logger for the conversation
     * @param colors               the colors used in the conversation
     * @param instructions         the instruction api instance
     * @param itemManager          the item manager instance
     * @param profileProvider      the profile provider instance
     * @param pluginManager        the plugin manager instance
     * @param plugin               the plugin instance
     * @param translations         the {@link Translations} instance
     * @param conversations        the conversations instance
     * @param showNumber           whether to show the number of the conversation
     * @param showNPCText          whether to show the NPC text
     * @param printMessages        whether to print messages
     * @param componentLineWrapper the component line wrapper
     */
    @SuppressWarnings("PMD.ExcessiveParameterList")
    public CitizensInventoryConvIO(final Conversation conv, final OnlineProfile onlineProfile, final BetonQuestLogger log,
                                   final ConversationColors colors, final Plugin plugin, final PluginManager pluginManager,
                                   final Instructions instructions, final Translations translations, final ItemManager itemManager,
                                   final ProfileProvider profileProvider, final Conversations conversations,
                                   final boolean showNumber, final boolean showNPCText, final boolean printMessages,
                                   final FixedComponentLineWrapper componentLineWrapper) {
        super(conv, onlineProfile, log, plugin, pluginManager, instructions, translations, itemManager, profileProvider,
                conversations, colors, showNumber, showNPCText, printMessages, componentLineWrapper);
        this.plugin = plugin;
    }

    @Override
    protected SkullMeta updateSkullMeta(final SkullMeta meta, final String plainTextNpcName) {
        // this only applied to Citizens NPC conversations
        if (conv instanceof final NpcConversation<?> npcConv && npcConv.getNPC().getOriginal() instanceof final NPC npc) {
            if (Bukkit.isPrimaryThread()) {
                throw new IllegalStateException("Must be called async!");
            }

            try {
                final SkinTrait skinTrait = Bukkit.getScheduler().callSyncMethod(plugin, () -> npc.getOrAddTrait(SkinTrait.class)).get();
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
                log.debug(conv.getPackage(), "Could not resolve a skin Texture!", e);
            } catch (final SkinFormatParseException e) {
                log.reportException(conv.getPackage(), new IllegalStateException("Could not parse the skin metadata provided by the NPC plugin. The format may have changed."));
            }
        }
        return super.updateSkullMeta(meta, plainTextNpcName);
    }

    /**
     * Resolves a base64 encoded skin JSON to a skin URL.
     * See <a href="https://wiki.vg/Mojang_API#UUID_to_Profile_and_Skin.2FCape">wiki.vg</a> for unofficial documentation
     * on the skin JSON format.
     *
     * @param encodedTexture base64 encoded skin JSON metadata
     * @return the skin URL
     */
    private URL resolveSkinURL(final String encodedTexture) throws SkinFormatParseException {
        final String decoded = new String(Base64.getDecoder().decode(encodedTexture), StandardCharsets.UTF_8);
        final Matcher matcher = SKIN_JSON_URL_PATTERN.matcher(decoded);
        if (!matcher.find()) {
            throw new SkinFormatParseException("Could not find the skin URL in the skin JSON!");
        }
        final String skinUrl = matcher.group("url");
        try {
            return new URL(skinUrl);
        } catch (final MalformedURLException e) {
            throw new SkinFormatParseException("Could not parse the skin URL!", e);
        }
    }
}
