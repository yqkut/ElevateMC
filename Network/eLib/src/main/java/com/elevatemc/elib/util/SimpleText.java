package com.elevatemc.elib.util;


import com.elevatemc.elib.util.message.MessageTranslator;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class SimpleText {

    private final ComponentBuilder builder;

    public SimpleText(String string) {
        builder = new ComponentBuilder(MessageTranslator.translate(string));
    }

    public SimpleText add(String string) {
        builder.append(MessageTranslator.translate(string));
        return this;
    }

    public SimpleText add(SimpleText simpleText) {
        builder.append(simpleText.builder.getCurrent());
        return this;
    }

    public SimpleText click(String command) {
        builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        return this;
    }

    public SimpleText hover(String string) {
        HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(MessageTranslator.translate(string)).create());
        builder.event(event);
        return this;
    }

    public BaseComponent[] build() {
        return builder.create();
    }

    public void send(CommandSender sender) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            player.spigot().sendMessage(this.build());
        } else {
            StringBuilder message = new StringBuilder();
            for (BaseComponent component : this.build()) {
                message.append(component.toPlainText()).append(" ");
            }
            sender.sendMessage(message.toString());
        }
    }
}

