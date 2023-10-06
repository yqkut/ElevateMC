package dev.apposed.prime.spigot.module.profile.identity.command;

import com.elevatemc.elib.command.Command;
import com.elevatemc.elib.command.param.Parameter;
import dev.apposed.prime.spigot.Prime;
import dev.apposed.prime.spigot.module.ModuleHandler;
import dev.apposed.prime.spigot.module.profile.Profile;
import dev.apposed.prime.spigot.module.profile.ProfileHandler;
import dev.apposed.prime.spigot.module.profile.target.ProfileTarget;
import dev.apposed.prime.spigot.util.Color;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class IdentityCommands {

    private static final Prime plugin = Prime.getInstance();
    private static final ModuleHandler moduleHandler = plugin.getModuleHandler();

    private static final ProfileHandler profileHandler = moduleHandler.getModule(ProfileHandler.class);

    @Command(names = {"alts"}, description = "View a player's alts", async = true, permission = "prime.command.alts")
    public static void executeAlts(CommandSender sender, @Parameter(name = "player")ProfileTarget profileTarget) {
        profileTarget.resolve(profile -> {
            if (profile == null) {
                profileTarget.sendError(sender);
                return;
            }

            final List<Profile> allProfiles = new ArrayList<>();
            profile.getIdentities().forEach(identity -> allProfiles.addAll(profileHandler.fetchByIdentity(identity.getIp())));
            final List<Profile> associatedProfiles = allProfiles.stream().distinct().collect(Collectors.toList());
            sender.sendMessage(Color.translate(profile.getColoredName() + " &ahas &e" + associatedProfiles.size() + " &aaccounts on &e" + profile.getIdentities().size() + " &aidentities."));
            if(sender.hasPermission("prime.alts.detailed")) sender.sendMessage(Color.translate("&eLast joined with &7" + profile.getLastHashedIp()));
            sender.sendMessage(associatedProfiles
                    .stream()
                    .map(prof -> Color.translate(prof.getColoredName() + (sender.hasPermission("prime.alts.detailed") ? " &7❘ &c(" + prof.getLastHashedIp() + ") &f" + String.join(", ", prof.getAllHashedIps()) : "")))
                    .collect(Collectors.joining("\n")));
        });
    }
}