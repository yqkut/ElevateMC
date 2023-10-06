package dev.apposed.prime.spigot.module.profile.target;

import com.elevatemc.elib.command.param.ParameterType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProfileTypeAdapter implements ParameterType<ProfileTarget> {

    @Override
    public ProfileTarget transform(CommandSender sender, String s) {
        if(sender instanceof Player && s.equals("self")) {
            return new ProfileTarget(sender);
        }

        return new ProfileTarget(s);
    }

    @Override
    public List<String> tabComplete(Player player, Set<String> set, String s) {
        return Bukkit.getOnlinePlayers().stream().filter(player::canSee).map(Player::getName).collect(Collectors.toList());
    }
}