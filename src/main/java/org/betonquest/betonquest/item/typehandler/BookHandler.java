package org.betonquest.betonquest.item.typehandler;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    @Override
    public Class<BookMeta> metaClass() {
        return BookMeta.class;
    }

    @Override
    public Set<String> keys() {
        return Set.of("title", "author", "text");
    }

    @Override
    @Nullable
    public String serializeToString(final BookMeta bookMeta) {
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
        text = buildPages(bookMeta);
        if (author.isEmpty() && title.isEmpty() && text.isEmpty()) {
            return null;
        }
        return (author + title + text).substring(1);
    }

    private String buildPages(final BookMeta bookMeta) {
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
            return " text:" + strBldr.substring(0, strBldr.length() - 1);
        }
        return "";
    }

    @Override
    public void set(final String key, final String data) throws QuestException {
        switch (key) {
            case "title" -> setTitle(data);
            case "author" -> setAuthor(data);
            case "text" -> setText(data);
            default -> throw new QuestException("Unknown book key: " + key);
        }
    }

    @Override
    public void populate(final BookMeta bookMeta) {
        bookMeta.setTitle(title);
        bookMeta.setAuthor(author);
        bookMeta.setPages(text);
    }

    @Override
    public boolean check(final BookMeta bookMeta) {
        return checkExistence(titleE, title, bookMeta.getTitle())
                && checkExistence(authorE, author, bookMeta.getAuthor())
                && checkText(bookMeta.getPages());
    }

    public void setTitle(final String string) {
        if (Existence.NONE_KEY.equalsIgnoreCase(string)) {
            titleE = Existence.FORBIDDEN;
        } else {
            title = string.replace('_', ' ');
            title = ChatColor.translateAlternateColorCodes('&', title);
            titleE = Existence.REQUIRED;
        }
    }

    public void setAuthor(final String string) {
        if (Existence.NONE_KEY.equalsIgnoreCase(string)) {
            authorE = Existence.FORBIDDEN;
        } else {
            author = string.replace("_", " ");
            authorE = Existence.REQUIRED;
        }
    }

    public void setText(final String string) {
        if (Existence.NONE_KEY.equalsIgnoreCase(string)) {
            text.add(""); // this will prevent "Invalid book tag" message in the empty book
            textE = Existence.FORBIDDEN;
        } else {
            text = Utils.pagesFromString(string.replace("_", " "));
            text.replaceAll(textToTranslate -> ChatColor.translateAlternateColorCodes('&', textToTranslate));
            textE = Existence.REQUIRED;
        }
    }

    private boolean checkExistence(final Existence existence, @Nullable final String present, @Nullable final String string) {
        return switch (existence) {
            case WHATEVER -> true;
            case REQUIRED -> string != null && string.equals(present);
            case FORBIDDEN -> string == null || string.isBlank();
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
