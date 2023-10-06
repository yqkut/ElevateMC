package com.elevatemc.potpvp.cosmetic.command.type;

import com.elevatemc.elib.command.param.ParameterType;
import com.elevatemc.potpvp.PotPvPSI;
import com.elevatemc.potpvp.cosmetic.Cosmetic;
import com.elevatemc.potpvp.cosmetic.CosmeticHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CosmeticParameterType implements ParameterType<Cosmetic> {

    private final CosmeticHandler cosmeticHandler = PotPvPSI.getInstance().getCosmeticHandler();

    @Override
    public Cosmetic transform(CommandSender sender, String s) {
        if(s.equalsIgnoreCase("all")) return null;
        return cosmeticHandler.getCosmetics()
                .stream()
                .filter(cosmetic -> cosmetic.getName().equalsIgnoreCase(s))
                .findAny()
                .orElse(null);
    }

    @Override
    public List<String> tabComplete(Player player, Set<String> set, String s) {
        return cosmeticHandler.getCosmetics()
                .stream()
                .map(Cosmetic::getName)
                .collect(Collectors.toList());
    }
}
