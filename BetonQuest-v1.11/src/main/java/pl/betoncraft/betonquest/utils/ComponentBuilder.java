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

/*
 * Created on 23.06.2018.
 *
 * @author Jonas Blocher
 */
package pl.betoncraft.betonquest.utils;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.logging.Level;


/**
 * Helper class which uses the bungee API to send tellraw like messages to players and allows falling back on legacy messages if API
 * isn't available.
 * <p>
 * The bungee api is included in spigot since version 1.9
 * <p>
 * Created on 23.06.2018.
 *
 * @author Jonas Blocher
 */
public interface ComponentBuilder {

    ComponentBuilder append(String text);

    ComponentBuilder append(String text, ChatColor color);

    default ComponentBuilder hover(String hoverText) {
        return this;
    }

    default ComponentBuilder click(ClickEvent event, String text) {
        return this;
    }

    void send(CommandSender sender);

    enum ClickEvent {
        RUN_COMMAND,
        SUGGEST_COMMAND;

        public net.md_5.bungee.api.chat.ClickEvent.Action asBungee() {
            switch (this) {
                case RUN_COMMAND:
                    return net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND;
                case SUGGEST_COMMAND:
                default:
                    return net.md_5.bungee.api.chat.ClickEvent.Action.SUGGEST_COMMAND;
            }
        }
    }

    class LegacyBuilder implements ComponentBuilder {

        private StringBuilder stringBuilder;

        public LegacyBuilder() {
            this.stringBuilder = new StringBuilder();
        }

        @Override
        public ComponentBuilder append(String text) {
            stringBuilder.append(text);
            return this;
        }

        @Override
        public ComponentBuilder append(String text, ChatColor color) {
            stringBuilder.append(color).append(text);
            return this;
        }

        @Override
        public void send(CommandSender sender) {
            sender.sendMessage(toString());
        }

        @Override
        public String toString() {
            return stringBuilder.toString();
        }

        public StringBuilder getStringBuilder() {
            return stringBuilder;
        }
    }

    class BugeeCordAPIBuilder implements ComponentBuilder {

        protected net.md_5.bungee.api.chat.ComponentBuilder builder;

        public BugeeCordAPIBuilder() throws LinkageError {
            builder = new net.md_5.bungee.api.chat.ComponentBuilder("");
        }

        public static ComponentBuilder create() {
            try {
                return new BugeeCordAPIBuilder();
            } catch (LinkageError e) {
                LogUtils.getLogger().log(Level.WARNING, "Could not link BungeeCordAPIBuilder: " + e.getMessage());
                LogUtils.logThrowable(e);
                return new LegacyBuilder();
            }
        }

        @Override
        public ComponentBuilder append(String text) {
            builder.append(text);
            return this;
        }

        @Override
        public ComponentBuilder append(String text, ChatColor color) {
            builder.append(text, net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention.FORMATTING).color(color.asBungee());
            return this;
        }

        @Override
        public ComponentBuilder hover(String hoverText) {
            builder.event(new net.md_5.bungee.api.chat.HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT,
                    TextComponent.fromLegacyText(hoverText)));
            return this;
        }

        @Override
        public ComponentBuilder click(ClickEvent event, String text) {
            builder.event(new net.md_5.bungee.api.chat.ClickEvent(event.asBungee(), text));
            return this;
        }

        @Override
        public void send(CommandSender sender) {
            sender.sendMessage(new TextComponent(builder.create()).toLegacyText());
        }

        @Override
        public String toString() {
            return builder.toString();
        }
    }
}
