package com.elevatemc.potpvp.cosmetic;

import com.elevatemc.potpvp.cosmetic.impl.WoolTrailCosmetic;
import com.elevatemc.potpvp.cosmetic.type.CosmeticType;
import com.elevatemc.potpvp.util.Color;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public class CosmeticHandler {

    private final List<Cosmetic> cosmetics;
    private final Map<UUID, List<Cosmetic>> activeCosmetics;

    public CosmeticHandler() {
        this.cosmetics = new ArrayList<>();
        this.activeCosmetics = new HashMap<>();
        registerCosmetics();
    }

    private void registerCosmetics() {
        cosmetics.addAll(Arrays.asList(
            new WoolTrailCosmetic()
        ));
        cosmetics.forEach(Cosmetic::register);
    }

    public void activateCosmetic(Player player, Cosmetic cosmetic) {
        final List<Cosmetic> activeCosmetics = new ArrayList<>();
        activeCosmetics.addAll(this.activeCosmetics.getOrDefault(player.getUniqueId(), Collections.emptyList()));
        if(activeCosmetics.stream().anyMatch(curCos -> curCos.getType() == cosmetic.getType())) {
            player.sendMessage(Color.translate("&cYou already have a cosmetic with that type enabled! Disable before equipping a new cosmetic!"));
            return;
        }
        activeCosmetics.add(cosmetic);
        cosmetic.onEnable(player);
        this.activeCosmetics.put(player.getUniqueId(), activeCosmetics);
    }

    public void deactivateCosmetics(Player player) {
        this.activeCosmetics.remove(player.getUniqueId()).forEach(cosmetic -> {
            cosmetic.onDisable(player);
        });
    }

    public void deactivateCosmetic(Player player, Cosmetic cosmetic) {
        final List<Cosmetic> activeCosmetics = new ArrayList<>();
        activeCosmetics.addAll(this.activeCosmetics.getOrDefault(player.getUniqueId(), Collections.emptyList()));
        if(activeCosmetics.isEmpty()) return;
        activeCosmetics.remove(cosmetic);
        cosmetic.onDisable(player);
        this.activeCosmetics.put(player.getUniqueId(), activeCosmetics);
    }

    public boolean isEnabled(Player player, CosmeticType type) {
        final List<Cosmetic> activeCosmetics = new ArrayList<>(this.activeCosmetics.getOrDefault(player.getUniqueId(), Collections.emptyList()));
        return activeCosmetics.stream().anyMatch(cosmetic -> cosmetic.getType() == type);
    }
}
