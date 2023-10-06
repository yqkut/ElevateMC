package com.elevatemc.elib.util;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;

import java.lang.reflect.Field;
import java.util.List;

public class eLibComponentBuilder extends com.elevatemc.elib.util.ComponentBuilder {
    private static Field partsField;
    private static Field currField;

    static {
        try {
            currField = com.elevatemc.elib.util.ComponentBuilder.class.getDeclaredField("current");
            partsField = com.elevatemc.elib.util.ComponentBuilder.class.getDeclaredField("parts");

            currField.setAccessible(true);
            partsField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public eLibComponentBuilder(String text) {
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

}
