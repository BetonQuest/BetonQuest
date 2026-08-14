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
import net.kyori.adventure.text.format.NamedTextColor;
import org.betonquest.betonquest.api.common.component.ComponentLineWrapper;
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
@SuppressWarnings({"PMD.TooManyMethods", "UnstableApiUsage"})
public class DialogConvIO implements ConversationIO {

    /**
     * Button for closing the conversation.
     */
    public static final Component CLOSE_TEXT = Component.translatable("mco.selectServer.close", NamedTextColor.RED);

    /**
     * An empty component constant.
     */
    private static final Component EMPTY = Component.empty();

    /**
     * The conversation instance.
     */
    private final Conversation conv;

    /**
     * The online profile of the player.
     */
    private final OnlineProfile onlineProfile;

    /**
     * The colors used in the conversation.
     */
    private final ConversationColors colors;

    /**
     * The line wrapper for calculating text widths.
     */
    private final ComponentLineWrapper componentLineWrapper;

    /**
     * The settings for the dialog.
     */
    private final DialogSettings settings;

    /**
     * The list of player options.
     */
    private final List<Component> options = new ArrayList<>();

    /**
     * The text spoken by the NPC.
     */
    private Component npcText = EMPTY;

    /**
     * The name of the NPC.
     */
    private Component npcName = EMPTY;

    /**
     * Creates a new DialogConvIO instance.
     *
     * @param conv                 the conversation this IO is part of
     * @param onlineProfile        the online profile of the player participating in the conversation
     * @param settings             the dialog settings to use
     * @param colors               the colors used in the conversation
     * @param componentLineWrapper the component line wrapper used to calculate text widths
     */
    public DialogConvIO(
            final Conversation conv,
            final OnlineProfile onlineProfile,
            final DialogSettings settings,
            final ConversationColors colors,
            final ComponentLineWrapper componentLineWrapper
    ) {
        this.conv = conv;
        this.onlineProfile = onlineProfile;
        this.colors = colors;
        this.componentLineWrapper = componentLineWrapper;
        this.settings = settings;
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
    public void addPlayerOption(final Component option, final ConfigurationSection properties) {
        this.options.add(option);
    }

    @Override
    public void display() {
        if (Component.empty().equals(npcText) && options.isEmpty()) {
            end(() -> {
            });
            return;
        }

        onlineProfile.getPlayer().showDialog(
                Dialog.create(builder -> {
                            if (settings.onlyButtons()) {
                                builder.empty()
                                        .base(buildDialogBase(new ArrayList<>(1)))
                                        .type(buildDialogType());
                            } else {
                                builder.empty()
                                        .base(buildTextDialogBase())
                                        .type(buildTextDialogType());
                            }
                        }
                )
        );
    }

    /**
     * Builds the base dialog settings including the title and body.
     *
     * @return the built DialogBase object
     */
    private DialogBase buildDialogBase(final List<DialogBody> bodies) {
        final Component name = npcName;
        final Component text = npcText;

        final DialogBody body = DialogBody.plainMessage(
                settings.questerInTitle() ? colors.getText().append(text)
                        : colors.getText().append(colors.getNpc().append(name)).append(Component.text(": ")).append(text)
        );
        bodies.add(0, body);

        final Component title = settings.questerInTitle() ? colors.getNpc().append(name) : EMPTY;

        return DialogBase.builder(title)
                .canCloseWithEscape(settings.closeButtonEnabled() && settings.closeWithEscape() && !conv.isMovementBlock())
                .body(bodies)
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
        if (settings.closeButtonEnabled() && !conv.isMovementBlock()) {
            typeBuilder.exitAction(buildExitButton());
        }
        return typeBuilder.build();
    }

    private DialogBase buildTextDialogBase() {
        final int size = options.size();
        final List<DialogBody> bodies = new ArrayList<>(size * 2 + 1);
        for (int i = 0; i < size; i++) {
            bodies.add(DialogBody.plainMessage(colorfulPlayerText(options.get(i), i)));
        }

        return buildDialogBase(bodies);
    }

    private DialogType buildTextDialogType() {
        if (options.isEmpty()) {
            return DialogType.notice();
        }

        final int size = options.size();
        final List<ActionButton> buttons = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            final Component option = options.get(i);
            final List<Component> components = componentLineWrapper.splitWidth(option, settings.buttonWidth());
            final Component text = components.size() > 1 ? components.get(0).append(Component.text("…")) : components.get(0);
            buttons.add(buildPlayerOptionButton(text, i, settings.buttonWidth() + settings.buttonRenderPadding()));
        }

        final MultiActionType.Builder typeBuilder = DialogType.multiAction(buttons).columns(1);
        if (settings.closeButtonEnabled() && !conv.isMovementBlock()) {
            typeBuilder.exitAction(buildExitButton());
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
        return ActionButton.builder(colorfulPlayerText(option, index))
                .width(width)
                .action(DialogAction.customClick((response, audience) -> conv.passPlayerAnswer(index + 1), clickOptions()))
                .build();
    }

    private Component colorfulPlayerText(final Component text, final int index) {
        return colors.getOption().append(colors.getNumber().append(Component.text(index + 1))
                .append(Component.text(". "))).append(text);
    }

    /**
     * Builds the exit button for the dialog.
     *
     * @return the built ActionButton object
     */
    private ActionButton buildExitButton() {
        final int finalWidth = settings.buttonWidth() + settings.buttonRenderPadding();

        return ActionButton.builder(CLOSE_TEXT)
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
        final int padding = settings.buttonRenderPadding();

        for (final Component option : options) {
            maxOptionWidth = Math.max(maxOptionWidth, componentLineWrapper.width(option) + padding);
        }
        return Math.min(1024, Math.max(Math.max(maxOptionWidth, settings.buttonWidth()), 100));
    }

    /**
     * Creates default click callback options.
     *
     * @return the built ClickCallback.Options object
     */
    private ClickCallback.Options clickOptions() {
        return ClickCallback.Options.builder().uses(1).build();
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
