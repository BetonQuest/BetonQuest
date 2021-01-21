package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.item.QuestItem.Existence;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"PMD.DataClass", "PMD.CommentRequired"})
public class BookHandler {

    private String title = Config.getMessage(Config.getLanguage(), "unknown_title");
    private Existence titleE = Existence.WHATEVER;
    private String author = Config.getMessage(Config.getLanguage(), "unknown_author");
    private Existence authorE = Existence.WHATEVER;
    private List<String> text = new ArrayList<>();
    private Existence textE = Existence.WHATEVER;

    public BookHandler() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String string) {
        if ("none".equalsIgnoreCase(string)) {
            titleE = Existence.FORBIDDEN;
        } else {
            title = string.replace('_', ' ');
            title = ChatColor.translateAlternateColorCodes('&', title);
            titleE = Existence.REQUIRED;
        }
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(final String string) {
        if ("none".equalsIgnoreCase(string)) {
            authorE = Existence.FORBIDDEN;
        } else {
            author = string.replace("_", " ");
            authorE = Existence.REQUIRED;
        }
    }

    public List<String> getText() {
        return text;
    }

    public void setText(final String string) {
        if ("none".equalsIgnoreCase(string)) {
            text.add(""); // this will prevent "Invalid book tag" message in the empty book
            textE = Existence.FORBIDDEN;
        } else {
            text = Utils.pagesFromString(string.replace("_", " "));
            textE = Existence.REQUIRED;
        }
    }

    @SuppressWarnings("PMD.InefficientEmptyStringCheck")
    public boolean checkTitle(final String string) {
        switch (titleE) {
            case WHATEVER:
                return true;
            case REQUIRED:
                return string != null && string.equals(title);
            case FORBIDDEN:
                return string == null || string.trim().isEmpty();
        }
        return true;
    }

    @SuppressWarnings("PMD.InefficientEmptyStringCheck")
    public boolean checkAuthor(final String string) {
        switch (authorE) {
            case WHATEVER:
                return true;
            case REQUIRED:
                return string != null && string.equals(author);
            case FORBIDDEN:
                return string == null || string.trim().isEmpty();
        }
        return true;
    }

    @SuppressWarnings("PMD.CyclomaticComplexity")
    public boolean checkText(final List<String> list) {
        switch (textE) {
            case WHATEVER:
                return true;
            case REQUIRED:
                if (list == null || list.size() != text.size()) {
                    return false;
                }
                for (int i = 0; i < text.size(); i++) {
                    // this removes black color codes, bukkit adds them for some reason
                    String line = list.get(i).replaceAll("(§0)?\\n(§0)?", "\\n");
                    while (line.startsWith("\"")) {
                        line = line.substring(1);
                    }
                    while (line.endsWith("\"") && line.length() > 0) {
                        line = line.substring(0, line.length() - 1);
                    }
                    String pattern = text.get(i).replaceAll("(§0)?\\n(§0)?", "\\n");
                    while (pattern.startsWith("\"")) {
                        pattern = pattern.substring(1);
                    }
                    while (list.get(i).endsWith("\"")) {
                        pattern = pattern.substring(0, pattern.length() - 1);
                    }
                    if (!line.equals(pattern)) {
                        return false;
                    }
                }
                return true;
            case FORBIDDEN:
                return list == null || list.isEmpty() || list.size() == 1 && list.get(0).isEmpty();
        }
        return true;
    }

}
