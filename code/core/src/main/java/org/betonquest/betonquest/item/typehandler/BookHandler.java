package org.betonquest.betonquest.item.typehandler;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.common.component.BookPageWrapper;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.item.handler.Existence;
import org.betonquest.betonquest.item.handler.ExistenceArgument;
import org.betonquest.betonquest.item.handler.ItemMetaHandler;
import org.betonquest.betonquest.item.handler.ResolvedAttribute;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

/**
 * Handles de-/serialization of Books.
 */
public class BookHandler implements ItemMetaHandler<BookMeta> {

    /**
     * The book wrapper used to split pages.
     */
    private final BookPageWrapper bookPageWrapper;

    /**
     * The title.
     */
    private ExistenceArgument<Component> title = ExistenceArgument.whateverNullValue();

    /**
     * The author.
     */
    private ExistenceArgument<Component> author = ExistenceArgument.whateverNullValue();

    /**
     * The text pages.
     */
    private ExistenceArgument<List<Component>> text = ExistenceArgument.whateverValue(List.of(Component.empty()));

    /**
     * Creates an empty BookHandler.
     *
     * @param bookPageWrapper the book wrapper used to split pages
     */
    public BookHandler(final BookPageWrapper bookPageWrapper) {
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
            author = " " + HandlerUtil.toKeyValue("author", bookMeta.author());
        } else {
            author = "";
        }
        if (bookMeta.hasTitle()) {
            title = " " + HandlerUtil.toKeyValue("title", bookMeta.title());
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
                builder.append('|').append(MiniMessage.miniMessage().serialize(page)
                        .replace("|", "\\|")
                        .replace("\"", "\\\"")
                );
            }
            return " \"text:@[minimessage]" + builder.substring(1) + "\"";
        }
        return "";
    }

    @Override
    public void parse(final Instruction instruction) throws QuestException {
        this.title = ExistenceArgument.apply("title", instruction.component().map(Component::compact));
        this.author = ExistenceArgument.apply("author", instruction.component().map(Component::compact));
        this.text = (ExistenceArgument<List<Component>>) ExistenceArgument.apply(instruction.component().map(bookPageWrapper::splitPages))
                .get("text", Pair.of(Existence.WHATEVER, List.of(Component.empty())));
    }

    @Override
    public ResolvedAttribute<BookMeta> resolve(@Nullable final Profile profile) throws QuestException {
        final Pair<Existence, Component> title = this.title.getValue(profile);
        final Pair<Existence, Component> author = this.author.getValue(profile);
        final Pair<Existence, List<Component>> text = this.text.getValue(profile);
        return new ResolvedAttribute<>() {

            @Override
            public Class<BookMeta> metaClass() {
                return BookMeta.class;
            }

            @Override
            public void populate(final BookMeta bookMeta) {
                bookMeta.title(title.getRight())
                        .author(author.getRight())
                        .addPages(text.getRight().toArray(new Component[0]));
            }

            @Override
            public boolean check(final BookMeta bookMeta) {
                return checkExistence(title, bookMeta.title())
                        && checkExistence(author, bookMeta.author())
                        && checkText(text, bookMeta.pages());
            }

            private boolean checkExistence(final Pair<Existence, @Nullable Component> stored, @Nullable final Component onItem) {
                return switch (stored.getLeft()) {
                    case WHATEVER -> true;
                    case REQUIRED -> onItem != null && onItem.compact().equals(stored.getRight());
                    case FORBIDDEN -> onItem == null || onItem.equals(Component.empty());
                };
            }

            private boolean checkText(final Pair<Existence, List<Component>> stored, @Nullable final List<Component> list) {
                return switch (stored.getLeft()) {
                    case WHATEVER -> true;
                    case REQUIRED -> stored.getRight().equals(list);
                    case FORBIDDEN ->
                            list == null || list.isEmpty() || list.size() == 1 && list.get(0).equals(Component.empty());
                };
            }
        };
    }
}
