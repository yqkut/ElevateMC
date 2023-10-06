package dev.apposed.prime.spigot.module.profile.adapter;

import com.elevatemc.elib.command.ParameterType;
import dev.apposed.prime.spigot.module.profile.Profile;
import dev.apposed.prime.spigot.module.profile.ProfileHandler;
import dev.apposed.prime.spigot.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ProfileTypeAdapter implements ParameterType<Profile> {

    private ProfileHandler profileHandler;

    public ProfileTypeAdapter(ProfileHandler profileHandler) {
        this.profileHandler = profileHandler;
    }

    @Override
    public Profile transform(CommandSender sender, String s) {
        Optional<Profile> profile = profileHandler.getProfile(s);
        if(!profile.isPresent()) {
            final Profile nicknameProfile = profileHandler.getProfileFromNickname(s);
            if(nicknameProfile.getPlayer() != null) {
                return nicknameProfile;
            }

            sender.sendMessage(Color.translate("&cFailed to fetch " + s + "'s profile."));
            return null;
        }

        return profile.get();
    }

    @Override
    public List<String> tabComplete(Player player, Set<String> set, String s) {
        return Bukkit.getOnlinePlayers().stream().filter(player::canSee).map(Player::getName).collect(Collectors.toList());
    }
}