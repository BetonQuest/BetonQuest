package org.betonquest.betonquest.item.typehandler;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.betonquest.betonquest.api.common.component.BookPageWrapper;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.text.TextParser;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Handles de-/serialization of Books.
 */
public class BookHandler implements ItemMetaHandler<BookMeta> {
    /**
     * The text parser used to parse text.
     */
    private final TextParser textParser;

    /**
     * The book wrapper used to split pages.
     */
    private final BookPageWrapper bookPageWrapper;

    /**
     * The title.
     */
    @Nullable
    private Component title;

    /**
     * The required title existence.
     */
    private Existence titleE = Existence.WHATEVER;

    /**
     * The author.
     */
    @Nullable
    private Component author;

    /**
     * The required author existence.
     */
    private Existence authorE = Existence.WHATEVER;

    /**
     * The text pages.
     */
    private List<Component> text = new ArrayList<>();

    /**
     * The required text existence.
     */
    private Existence textE = Existence.WHATEVER;

    /**
     * The empty default Constructor.
     *
     * @param textParser      the text parser used to parse text
     * @param bookPageWrapper the book wrapper used to split pages
     */
    public BookHandler(final TextParser textParser, final BookPageWrapper bookPageWrapper) {
        this.textParser = textParser;
        this.bookPageWrapper = bookPageWrapper;
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
            author = " \"author:@[minimessage]" + MiniMessage.miniMessage().serialize(bookMeta.author()) + "\"";
        } else {
            author = "";
        }
        if (bookMeta.hasTitle()) {
            title = " \"title:@[minimessage]" + MiniMessage.miniMessage().serialize(bookMeta.title()) + "\"";
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
            final StringBuilder builder = new StringBuilder();
            for (final Component page : bookMeta.pages()) {
                builder.append(MiniMessage.miniMessage().serialize(page).replace("|", "\\|")).append('|');
            }
            return " \"text:@[minimessage]" + builder.substring(0, builder.length() - 1) + "\"";
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
        bookMeta.title(title).author(author).pages(text);
    }

    @Override
    public boolean check(final BookMeta bookMeta) {
        return checkExistence(titleE, title, bookMeta.title())
                && checkExistence(authorE, author, bookMeta.title())
                && checkText(bookMeta.pages());
    }

    private void setTitle(final String string) throws QuestException {
        if (Existence.NONE_KEY.equalsIgnoreCase(string)) {
            titleE = Existence.FORBIDDEN;
        } else {
            title = textParser.parse(string);
            titleE = Existence.REQUIRED;
        }
    }

    private void setAuthor(final String string) throws QuestException {
        if (Existence.NONE_KEY.equalsIgnoreCase(string)) {
            authorE = Existence.FORBIDDEN;
        } else {
            author = textParser.parse(string);
            authorE = Existence.REQUIRED;
        }
    }

    private void setText(final String string) throws QuestException {
        if (Existence.NONE_KEY.equalsIgnoreCase(string)) {
            text.add(Component.empty());
            textE = Existence.FORBIDDEN;
        } else {
            text = bookPageWrapper.splitPages(textParser.parse(string));
            textE = Existence.REQUIRED;
        }
    }

    private boolean checkExistence(final Existence existence, @Nullable final Component present, @Nullable final Component string) {
        return switch (existence) {
            case WHATEVER -> true;
            case REQUIRED -> string != null && string.equals(present);
            case FORBIDDEN -> string == null || string.equals(Component.empty());
        };
    }

    private boolean checkText(@Nullable final List<Component> list) {
        return switch (textE) {
            case WHATEVER -> true;
            case REQUIRED -> text.equals(list);
            case FORBIDDEN ->
                    list == null || list.isEmpty() || list.size() == 1 && list.get(0).equals(Component.empty());
        };
    }
}
