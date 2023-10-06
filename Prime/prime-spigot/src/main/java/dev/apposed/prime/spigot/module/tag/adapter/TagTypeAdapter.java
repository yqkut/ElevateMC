package dev.apposed.prime.spigot.module.tag.adapter;

import com.elevatemc.elib.command.param.ParameterType;
import dev.apposed.prime.spigot.module.tag.Tag;
import dev.apposed.prime.spigot.module.tag.TagHandler;
import dev.apposed.prime.spigot.util.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TagTypeAdapter implements ParameterType<Tag> {

    private TagHandler tagHandler;

    public TagTypeAdapter(TagHandler tagHandler) {
        this.tagHandler = tagHandler;
    }

    @Override
    public Tag transform(CommandSender commandSender, String s) {
        final Tag tag = tagHandler.getTag(s.toUpperCase());
        if(tag == null) {
            commandSender.sendMessage(Color.translate("&cCould not find a tag by that name."));
            return null;
        }

        return tag;
    }

    @Override
    public List<String> tabComplete(Player player, Set<String> set, String s) {
        return new ArrayList<>(tagHandler
                .getTags()
                .keySet());
    }
}
