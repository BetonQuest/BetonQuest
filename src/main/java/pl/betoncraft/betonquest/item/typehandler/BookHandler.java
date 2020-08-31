/*
 * BetonQuest - advanced quests for Bukkit
 * Copyright (C) 2016  Jakub "Co0sh" Sapalski
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.betoncraft.betonquest.item.typehandler;

import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.item.QuestItem.Existence;
import pl.betoncraft.betonquest.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class BookHandler {

    private String title = Config.getMessage(Config.getLanguage(), "unknown_title");
    private Existence titleE = Existence.WHATEVER;
    private String author = Config.getMessage(Config.getLanguage(), "unknown_author");
    private Existence authorE = Existence.WHATEVER;
    private List<String> text = new ArrayList<>();
    private Existence textE = Existence.WHATEVER;

    public BookHandler() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(final String string) {
        if (string.equalsIgnoreCase("none")) {
            titleE = Existence.FORBIDDEN;
        } else {
            title = string.replace('_', ' ').replace('&', '§');
            titleE = Existence.REQUIRED;
        }
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(final String string) {
        if (string.equalsIgnoreCase("none")) {
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
        if (string.equalsIgnoreCase("none")) {
            text.add(""); // this will prevent "Invalid book tag" message in the empty book
            textE = Existence.FORBIDDEN;
        } else {
            text = Utils.pagesFromString(string.replace("_", " "));
            textE = Existence.REQUIRED;
        }
    }

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
