package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.item.QuestItem.Existence;
import org.betonquest.betonquest.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("PMD.CommentRequired")
public class BookHandler implements ItemMetaHandler<BookMeta> {
    private String title = Config.getMessage(Config.getLanguage(), "unknown_title");

    private Existence titleE = Existence.WHATEVER;

    private String author = Config.getMessage(Config.getLanguage(), "unknown_author");

    private Existence authorE = Existence.WHATEVER;

    private List<String> text = new ArrayList<>();

    private Existence textE = Existence.WHATEVER;

    public BookHandler() {
    }

    /**
     * Converts the item meta into QuestItem format.
     *
     * @param bookMeta the meta to serialize
     * @return parsed values with leading space or empty string
     */
    public static String serializeToString(final BookMeta bookMeta) {
        final String author;
        final String title;
        final String text;
        if (bookMeta.hasAuthor()) {
            author = " author:" + bookMeta.getAuthor().replace(" ", "_");
        } else {
            author = "";
        }
        if (bookMeta.hasTitle()) {
            title = " title:" + bookMeta.getTitle().replace(" ", "_");
        } else {
            title = "";
        }
        if (bookMeta.hasPages()) {
            final StringBuilder strBldr = new StringBuilder();
            for (final String page : bookMeta.getPages()) {
                String processedPage = page;
                if (processedPage.startsWith("\"") && processedPage.endsWith("\"")) {
                    processedPage = processedPage.substring(1, processedPage.length() - 1);
                }
                // this will remove black color code between lines
                // Bukkit is adding it for some reason (probably to mess people's code)
                strBldr.append(processedPage.replace(" ", "_").replaceAll("(§0)?\\n(§0)?", "\\\\n")).append('|');
            }
            text = " text:" + strBldr.substring(0, strBldr.length() - 1);
        } else {
            text = "";
        }
        return author + title + text;
    }

    @Override
    public void set(final String key, final String data) {
        switch (key) {
            case "title" -> setTitle(data);
            case "author" -> setAuthor(data);
            case "text" -> setText(data);
            default -> {
            }
        }
    }

    @Override
    public void populate(final BookMeta bookMeta) {
        bookMeta.setTitle(getTitle());
        bookMeta.setAuthor(getAuthor());
        bookMeta.setPages(getText());
    }

    @Override
    public boolean check(final BookMeta bookMeta) {
        return checkTitle(bookMeta.getTitle()) && checkAuthor(bookMeta.getAuthor()) && checkText(bookMeta.getPages());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String string) {
        if (QuestItem.NONE_KEY.equalsIgnoreCase(string)) {
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
        if (QuestItem.NONE_KEY.equalsIgnoreCase(string)) {
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
        if (QuestItem.NONE_KEY.equalsIgnoreCase(string)) {
            text.add(""); // this will prevent "Invalid book tag" message in the empty book
            textE = Existence.FORBIDDEN;
        } else {
            text = Utils.pagesFromString(string.replace("_", " "));
            text.replaceAll(textToTranslate -> ChatColor.translateAlternateColorCodes('&', textToTranslate));
            textE = Existence.REQUIRED;
        }
    }

    public boolean checkTitle(@Nullable final String string) {
        return checkExistence(titleE, title, string);
    }

    public boolean checkAuthor(@Nullable final String string) {
        return checkExistence(authorE, author, string);
    }

    @SuppressWarnings("PMD.InefficientEmptyStringCheck")
    private boolean checkExistence(final Existence existence, @Nullable final String present, @Nullable final String string) {
        return switch (existence) {
            case WHATEVER -> true;
            case REQUIRED -> string != null && string.equals(present);
            case FORBIDDEN -> string == null || string.trim().isEmpty();
        };
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.CognitiveComplexity"})
    public boolean checkText(@Nullable final List<String> list) {
        switch (textE) {
            case WHATEVER:
                return true;
            case REQUIRED:
                if (list == null || list.size() != text.size()) {
                    return false;
                }
                for (int i = 0; i < text.size(); i++) {
                    // this removes black color codes, bukkit adds them for some reason
                    String line = list.get(i).replaceAll("(§0)?\\n(§0)?", "\n");
                    while (line.startsWith("\"")) {
                        line = line.substring(1);
                    }
                    while (line.endsWith("\"")) {
                        line = line.substring(0, line.length() - 1);
                    }
                    String pattern = text.get(i).replaceAll("(§0)?\\n(§0)?", "\n");
                    while (pattern.startsWith("\"")) {
                        pattern = pattern.substring(1);
                    }
                    while (pattern.endsWith("\"")) {
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
