package org.betonquest.betonquest.notify;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.title.Title;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("PMD.CommentRequired")
public class TitleNotifyIO extends NotifyIO {
    private final Duration fadeIn;

    private final Duration stay;

    private final Duration fadeOut;

    public TitleNotifyIO(final QuestPackage pack, final Map<String, String> data) throws QuestException {
        super(pack, data);

        fadeIn = Duration.ofMillis(getIntegerData("fadein", 10) * 50L);
        stay = Duration.ofMillis(getIntegerData("stay", 70) * 50L);
        fadeOut = Duration.ofMillis(getIntegerData("fadeout", 20) * 50L);
    }

    @Override
    protected void notifyPlayer(final String message, final OnlineProfile onlineProfile) {
        final String[] messageParts = message.split("\n");
        final String title = messageParts[0].isEmpty() ? " " : messageParts[0];
        final String subtitle = messageParts.length > 1 ? messageParts[1] : "";
        onlineProfile.getPlayer().sendTitle(title, subtitle, (int) (fadeIn.toMillis() / 50), (int) (stay.toMillis() / 50), (int) (fadeOut.toMillis() / 50));
    }

    @Override
    protected void notifyPlayer(final Component message, final OnlineProfile onlineProfile) {
        final List<Component> messageComponents = splitComponent(message);
        final int size = messageComponents.size();
        final Component titleComponent = size >= 1 ? messageComponents.get(0) : Component.empty();
        final Component subtitleComponent = size >= 2 ? messageComponents.get(1) : Component.empty();
        final Title title = Title.title(titleComponent, subtitleComponent, Title.Times.times(fadeIn, stay, fadeOut));
        onlineProfile.getPlayer().showTitle(title);
    }

    private List<Component> splitComponent(final Component self) {
        final List<Component> lines = splitComponentContent(self);
        if (self.children().isEmpty()) {
            return lines;
        }
        Component parent = lines.remove(lines.size() - 1);
        for (final Component child : self.children()) {
            final List<Component> childSegments = splitComponent(child);
            parent = parent.append(childSegments.get(0));
            for (int i = 1; i < childSegments.size(); i++) {
                lines.add(parent);
                parent = Component.empty().style(parent.style());
                parent = parent.append(childSegments.get(i));
            }
        }
        lines.add(parent);
        return lines;
    }

    private List<Component> splitComponentContent(final Component component) {
        if (!(component instanceof final TextComponent text)) {
            return new ArrayList<>(List.of(component));
        }
        final String[] segments = text.content().split("\n");
        return Arrays.stream(segments)
                .map(segment -> Component.text(segment).style(text.style()))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
