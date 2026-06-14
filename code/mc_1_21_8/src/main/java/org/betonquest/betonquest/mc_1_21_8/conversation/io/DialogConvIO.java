package org.betonquest.betonquest.mc_1_21_8.conversation.io;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import io.papermc.paper.registry.data.dialog.type.MultiActionType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.ComponentLineWrapper;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.conversation.ConversationIO;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

/**
 * ConversationIO implementation using Paper's Dialog API for Minecraft 1.21.8.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class DialogConvIO implements ConversationIO {

    /** The conversation instance. */
    private final Conversation conv;

    /** The online profile of the player. */
    private final OnlineProfile onlineProfile;

    /** The colors used in the conversation. */
    private final ConversationColors colors;

    /** The line wrapper for calculating text widths. */
    private final ComponentLineWrapper componentLineWrapper;

    /** The settings for the dialog. */
    private final DialogSettings settings;

    /** Cached component for the close button text. */
    private final Component cachedCloseText;

    /** Whether the layout is NPC title. */
    private final boolean isNpcTitleLayout;

    /** An empty component constant. */
    private static final Component EMPTY = Component.empty();

    /** The list of player options. */
    private final List<Component> options = new ArrayList<>();

    /** The text spoken by the NPC. */
    private Component npcText;

    /** The name of the NPC. */
    private Component npcName;

    /**
     * Creates a new DialogConvIO instance.
     *
     * @param conv                 the conversation this IO is part of
     * @param onlineProfile        the online profile of the player participating in the conversation
     * @param config               the plugin configuration accessor
     * @param colors               the colors used in the conversation
     * @param componentLineWrapper the component line wrapper used to calculate text widths
     */
    public DialogConvIO(
            final Conversation conv,
            final OnlineProfile onlineProfile,
            final ConfigAccessor config,
            final ConversationColors colors,
            final ComponentLineWrapper componentLineWrapper
    ) {
        this.conv = conv;
        this.onlineProfile = onlineProfile;
        this.colors = colors;
        this.componentLineWrapper = componentLineWrapper;

        final ConfigurationSection section = config.getConfigurationSection("conversation.io.dialog");
        this.settings = new DialogSettings(section);

        this.cachedCloseText = MiniMessage.miniMessage().deserialize(settings.closeButtonText);
        this.isNpcTitleLayout = settings.layout == DialogLayout.NPC_TITLE;
    }

    @Override
    public void begin() {
        // Empty
    }

    @Override
    public void setNpcResponse(final Component npcName, final Component response) {
        this.npcName = npcName;
        this.npcText = response;
    }

    @Override
    public void addPlayerOption(final Component option, final ConfigurationSection properties) throws QuestException {
        this.options.add(option);
    }

    @Override
    public void display() {
        if (Component.empty().equals(npcText) && options.isEmpty()) {
            end(() -> { });
            return;
        }

        onlineProfile.getPlayer().showDialog(
                Dialog.create(builder -> builder.empty()
                        .base(buildDialogBase())
                        .type(buildDialogType())
                )
        );
    }

    /**
     * Builds the base dialog settings including the title and body.
     *
     * @return the built DialogBase object
     */
    private DialogBase buildDialogBase() {
        final Component name = npcName;
        final Component text = npcText;

        final DialogBody body = DialogBody.plainMessage(
                isNpcTitleLayout ? colors.getText().append(text)
                        : colors.getText().append(colors.getNpc().append(name)).append(Component.text(": ")).append(text)
        );

        final Component title = isNpcTitleLayout ? colors.getNpc().append(name) : EMPTY;

        return DialogBase.builder(title)
                .canCloseWithEscape(settings.closeButtonEnabled && settings.closeWithEscape)
                .body(List.of(body))
                .build();
    }

    /**
     * Builds the action type for the dialog based on available options.
     *
     * @return the built DialogType object
     */
    private DialogType buildDialogType() {
        if (options.isEmpty()) {
            return DialogType.notice();
        }

        final int dialogWidth = computeDialogWidth();

        final int size = options.size();
        final List<ActionButton> buttons = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            buttons.add(buildPlayerOptionButton(options.get(i), i, dialogWidth));
        }

        final MultiActionType.Builder typeBuilder = DialogType.multiAction(buttons).columns(1);
        if (settings.closeButtonEnabled) {
            typeBuilder.exitAction(buildExitButton(dialogWidth));
        }

        return typeBuilder.build();
    }

    /**
     * Builds an individual action button for a player option.
     *
     * @param option the option component text
     * @param index  the index of the option
     * @param width  the calculated button width
     * @return the built ActionButton object
     */
    private ActionButton buildPlayerOptionButton(final Component option, final int index, final int width) {
        return ActionButton.builder(option)
                .width(width)
                .action(DialogAction.customClick((aud, ctx) -> conv.passPlayerAnswer(index + 1), clickOptions()))
                .build();
    }

    /**
     * Builds the exit button for the dialog.
     *
     * @param totalWidth the total width available
     * @return the built ActionButton object
     */
    private ActionButton buildExitButton(final int totalWidth) {
        final int buttonWidth = settings.closeButtonWidth;

        final int finalWidth = (buttonWidth > 0) ? buttonWidth
                : (buttonWidth == -1) ? totalWidth
                  : componentLineWrapper.width(cachedCloseText) + settings.buttonRenderPadding;

        return ActionButton.builder(cachedCloseText)
                .width(finalWidth)
                .action(DialogAction.customClick((aud, ctx) -> conv.endConversation(), clickOptions()))
                .build();
    }

    /**
     * Computes the maximum width required for the dialog buttons.
     *
     * @return the calculated maximum dialog width
     */
    private int computeDialogWidth() {
        int maxOptionWidth = 0;
        final int padding = settings.buttonRenderPadding;

        for (final Component option : options) {
            maxOptionWidth = Math.max(maxOptionWidth, componentLineWrapper.width(option) + padding);
        }
        return Math.max(Math.max(maxOptionWidth, settings.defaultButtonWidth), 100);
    }

    /**
     * Creates default click callback options.
     *
     * @return the built ClickCallback.Options object
     */
    private ClickCallback.Options clickOptions() {
        return ClickCallback.Options.builder().uses(1).lifetime(ClickCallback.DEFAULT_LIFETIME).build();
    }

    @Override
    public void clear() {
        options.clear();
        npcName = EMPTY;
        npcText = EMPTY;
    }

    @Override
    public void end(final Runnable callback) {
        callback.run();
    }
}
