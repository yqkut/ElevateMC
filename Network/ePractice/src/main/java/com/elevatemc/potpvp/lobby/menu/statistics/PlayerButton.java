package com.elevatemc.potpvp.lobby.menu.statistics;

import com.elevatemc.potpvp.gamemode.GameMode;
import com.google.common.collect.Lists;
import dev.apposed.prime.spigot.module.profile.Profile;
import dev.apposed.prime.spigot.module.profile.ProfileHandler;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.elo.EloHandler;
import com.elevatemc.elib.menu.Button;
import com.elevatemc.elib.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PlayerButton extends Button {

    private static final EloHandler eloHandler = PotPvPSI.getInstance().getEloHandler();
    private static final ProfileHandler profileHandler = PotPvPSI.getInstance().getPrime().getModuleHandler().getModule(ProfileHandler.class);

    @Override
    public String getName(Player player) {
        return getColoredName(player.getUniqueId()) + ChatColor.WHITE + "'s Statistics";
    }

    @Override
    public List<String> getDescription(Player player) {
        List<String> description = Lists.newArrayList();

        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "");
        description.add(ChatColor.DARK_AQUA + ChatColor.BOLD.toString() + "Global" + ChatColor.GRAY + ": " + ChatColor.WHITE + eloHandler.getGlobalElo(player.getUniqueId()));
        description.add(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "");

        for (GameMode gameMode : GameMode.getAll()) {
            if (gameMode.getSupportsCompetitive()) {
                description.add(ChatColor.DARK_AQUA + gameMode.getName() + ChatColor.GRAY + " - " + ChatColor.WHITE + eloHandler.getElo(player, gameMode));
            }
        }

        return description;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.SKULL_ITEM;
    }

    @Override
    public byte getDamageValue(Player player) {
        return (byte) 3;
    }

    private String getColoredName(UUID uuid) {
        Optional<Profile> profileOptional = profileHandler.getProfile(uuid);
        if(!profileOptional.isPresent()) {
            return UUIDUtils.name(uuid);
        }

        return profileOptional.get().getColoredName();
    }
}