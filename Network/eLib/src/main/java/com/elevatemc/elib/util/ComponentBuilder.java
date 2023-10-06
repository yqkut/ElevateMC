package com.elevatemc.elib.util;


import com.elevatemc.elib.util.message.MessageTranslator;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.List;

public class ComponentBuilder extends net.md_5.bungee.api.chat.ComponentBuilder {
    private static Field partsField;
    private static Field currField;

    static {
        try {
            currField = net.md_5.bungee.api.chat.ComponentBuilder.class.getDeclaredField("current");
            partsField = net.md_5.bungee.api.chat.ComponentBuilder.class.getDeclaredField("parts");
            currField.setAccessible(true);
            partsField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public ComponentBuilder(String text) {
        super(text);
    }

    public TextComponent getCurrent() {
        try {
            return (TextComponent) currField.get(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setCurrent(TextComponent tc) {
        try {
            currField.set(this, tc);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public List getParts() {
        try {
            return (List) partsField.get(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ComponentBuilder color(org.bukkit.ChatColor chatColor) {
        super.color(ChatColor.getByChar(chatColor.getChar()));
        return this;
    }

    public ComponentBuilder tooltip(String text) {
        this.hover(text);
        return this;
    }

    public ComponentBuilder hover(String text) {
        this.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageTranslator.translate(text)).create()));
        return this;
    }

    public ComponentBuilder command(String command) {
        this.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        return this;
    }

    public ComponentBuilder then(ComponentBuilder builder) {
        return this.then(builder.getCurrent());
    }

    public ComponentBuilder then(String text) {
        return this.then(new ComponentBuilder(text).getCurrent());
    }

    public ComponentBuilder then(TextComponent textComponent) {
        return this.append(textComponent);
    }

    public ComponentBuilder append(TextComponent textComponent) {
        String text = textComponent.getText();
        ChatColor color = textComponent.getColor();
        boolean bold = textComponent.isBold();
        boolean underline = textComponent.isUnderlined();
        boolean italic = textComponent.isUnderlined();
        boolean strike = textComponent.isStrikethrough();
        HoverEvent he = textComponent.getHoverEvent();
        ClickEvent ce = textComponent.getClickEvent();

        append(text);
        color(color);
        underlined(underline);
        italic(italic);
        strikethrough(strike);
        event(he);
        event(ce);

        if (textComponent.getExtra() != null) {
            for (BaseComponent bc : textComponent.getExtra()) {
                if (bc instanceof TextComponent) {
                    append((TextComponent) bc);
                }
            }
        }

        return this;
    }

    public void send(Player player) {
        player.spigot().sendMessage(this.create());
    }
}
