package org.betonquest.betonquest.notify;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.title.Title;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("PMD.CommentRequired")
public class TitleNotifyIO extends NotifyIO {
    /**
     * The newline string to separate title and subtitle.
     */
    private static final String NEW_LINE = "\n";

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
        Component last = lines.remove(lines.size() - 1);
        for (final Component child : self.children()) {
            final List<Component> childSegments = splitComponent(child);
            last = last.append(childSegments.get(0));
            for (int i = 1; i < childSegments.size(); i++) {
                lines.add(last);
                last = Component.empty().style(last.style());
                last = last.append(childSegments.get(i));
            }
        }
        lines.add(last);
        return lines;
    }

    private List<Component> splitComponentContent(final Component component) {
        if (!(component instanceof final TextComponent text)) {
            return new ArrayList<>(List.of(component));
        }
        final String content = text.content();
        final List<String> segments = new ArrayList<>(List.of(content.split(NEW_LINE)));
        if (segments.isEmpty()) {
            segments.add("");
            segments.add("");
        } else {
            if (content.startsWith(NEW_LINE)) {
                segments.add(0, "");
            }
            if (content.endsWith(NEW_LINE)) {
                segments.add("");
            }
        }
        return segments.stream()
                .map(segment -> Component.text(segment).style(text.style()))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
